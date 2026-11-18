import React, { useState } from 'react';
import Modal from '../../../components/common/Modal/Modal.jsx';
import { request } from '../../../services/api.js';
import './AuthModal.scss';

export default function AuthModal({ onClose, onLogin, notify }) {
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState({ email: '', password: '', fullName: '', phone: '', confirmPassword: '' });
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  const register = mode === 'register';
  const change = e => setForm({ ...form, [e.target.name]: e.target.value });
  const selectMode = next => { setMode(next); setError(''); };
  async function submit(e) {
    e.preventDefault(); setError('');
    if (register && form.password !== form.confirmPassword) { setError('Mật khẩu xác nhận chưa khớp.'); return; }
    setBusy(true);
    try {
      if (register) {
        await request('/auth/register', { method: 'POST', body: JSON.stringify({ email: form.email.trim(), password: form.password, fullName: form.fullName.trim(), phone: form.phone.trim() }) });
        setForm({ ...form, password: '', confirmPassword: '' }); selectMode('login'); notify('Đăng ký thành công. Vui lòng đăng nhập.');
      } else {
        const session = await request('/auth/access', { method: 'POST', body: JSON.stringify({ email: form.email.trim(), password: form.password }) });
        if (!session?.accessToken || !session?.userId) throw new Error('Phản hồi đăng nhập không hợp lệ.');
        onLogin(session); onClose(); notify('Đăng nhập thành công.');
      }
    } catch (err) { setError(err.message); } finally { setBusy(false); }
  }
  return <Modal onClose={onClose}>
    <div className="dialog-head"><div><div className="eyebrow">CHÀO MỪNG BẠN</div><h2>{register ? 'Đăng ký' : 'Đăng nhập'}<span>.</span></h2></div><button className="icon-button" onClick={onClose} aria-label="Đóng">×</button></div>
    <p className="dialog-intro">{register ? 'Tạo tài khoản để bắt đầu mua sắm cùng Tech Device.' : 'Đăng nhập để khám phá và lưu sản phẩm yêu thích.'}</p>
    <div className="auth-tabs"><button type="button" className={!register ? 'active' : ''} onClick={() => selectMode('login')}>Đăng nhập</button><button type="button" className={register ? 'active' : ''} onClick={() => selectMode('register')}>Đăng ký</button></div>
    <form onSubmit={submit}>
      {register && <><label>Họ và tên <span>không bắt buộc</span><input name="fullName" value={form.fullName} onChange={change} autoComplete="name" placeholder="Tên của bạn" /></label><label>Số điện thoại <span>không bắt buộc</span><input name="phone" value={form.phone} onChange={change} autoComplete="tel" placeholder="Số điện thoại" /></label></>}
      <label>Email<input type="email" name="email" value={form.email} onChange={change} autoComplete="email" placeholder="ban@example.com" required /></label>
      <label>Mật khẩu<input type="password" name="password" value={form.password} onChange={change} autoComplete={register ? 'new-password' : 'current-password'} placeholder="Nhập mật khẩu" required /></label>
      {register && <label>Xác nhận mật khẩu<input type="password" name="confirmPassword" value={form.confirmPassword} onChange={change} autoComplete="new-password" placeholder="Nhập lại mật khẩu" required /></label>}
      <div className="form-error" role="alert">{error}</div><button className="primary-button full" disabled={busy}>{busy ? 'Đang xử lý...' : register ? 'Tạo tài khoản' : 'Đăng nhập'} <span>↗</span></button>
    </form>
  </Modal>;
}

