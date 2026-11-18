const SESSION_KEY = 'techdevice-session';

export function readSession() {
  try { return JSON.parse(sessionStorage.getItem(SESSION_KEY)) || null; }
  catch { return null; }
}

export function writeSession(session) {
  if (session) sessionStorage.setItem(SESSION_KEY, JSON.stringify(session));
  else sessionStorage.removeItem(SESSION_KEY);
}

export async function request(path, { session, onSession, retry = true, ...options } = {}) {
  const headers = { ...(options.body ? { 'Content-Type': 'application/json' } : {}), ...options.headers };
  if (session?.accessToken) headers.Authorization = `Bearer ${session.accessToken}`;
  let response;
  try { response = await fetch(path, { ...options, headers }); }
  catch { throw new Error('Không thể kết nối máy chủ. Vui lòng kiểm tra backend.'); }

  if (response.status === 401 && retry && session?.refreshToken && !path.startsWith('/auth/')) {
    let tokens;
    try {
      const refreshed = await fetch('/auth/refresh', { method: 'POST', headers: { 'x-token': session.refreshToken } });
      if (!refreshed.ok) throw new Error();
      tokens = await refreshed.json();
      if (!tokens.accessToken) throw new Error();
    } catch {
      onSession?.(null);
      throw new Error('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
    }
    const updated = { ...session, ...tokens };
    onSession?.(updated);
    return request(path, { ...options, session: updated, onSession, retry: false });
  }

  if (response.status === 204) return null;
  let body = null;
  try { body = await response.json(); } catch { /* Một số API trả về text. */ }
  if (!response.ok) throw new Error(body?.message || (response.status === 403 ? 'Tài khoản của bạn không có quyền thực hiện thao tác này.' : `Yêu cầu không thành công (${response.status}).`));
  return body && Object.hasOwn(body, 'data') ? body.data : body;
}
