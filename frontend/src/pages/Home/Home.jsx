import React, { useCallback, useEffect, useRef, useState } from 'react';
import Logo from '../../components/layout/Logo/Logo.jsx';
import Modal from '../../components/common/Modal/Modal.jsx';
import AuthModal from '../../features/auth/components/AuthModal.jsx';
import ProductImage from '../../features/products/components/ProductImage.jsx';
import { readSession, request, writeSession } from '../../services/api.js';
import './Home.scss';

function Home() {
  const [session, setSessionState] = useState(readSession);
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [cart, setCart] = useState([]);
  const [category, setCategory] = useState('');
  const [query, setQuery] = useState('');
  const [status, setStatus] = useState('');
  const [modal, setModal] = useState(null);
  const [message, setMessage] = useState('');
  const [adding, setAdding] = useState(false);
  const timer = useRef(null);
  const setSession = useCallback(value => { writeSession(value); setSessionState(value); }, []);
  const notify = useCallback(value => { setMessage(value); clearTimeout(timer.current); timer.current = setTimeout(() => setMessage(''), 3500); }, []);
  useEffect(() => () => clearTimeout(timer.current), []);
  const call = useCallback((path, options = {}) => request(path, { ...options, session, onSession: setSession }), [session, setSession]);

  useEffect(() => {
    let current = true;
    setProducts([]); setCategories([]); setCart([]); setCategory('');
    if (!session) { setStatus('Vui lòng đăng nhập để xem các sản phẩm của cửa hàng.'); return () => { current = false; }; }
    setStatus('Đang tải sản phẩm...');
    Promise.allSettled([call('/api/products'), call('/api/categories'), call(`/api/cart?userId=${encodeURIComponent(session.userId)}`)]).then(([productResult, categoryResult, cartResult]) => {
      if (!current) return;
      if (productResult.status === 'fulfilled') { setProducts(Array.isArray(productResult.value) ? productResult.value : []); setStatus(''); }
      else setStatus(productResult.reason.message);
      if (categoryResult.status === 'fulfilled' && Array.isArray(categoryResult.value)) setCategories(categoryResult.value);
      if (cartResult.status === 'fulfilled') setCart(cartResult.value?.listCartItems || []);
    });
    return () => { current = false; };
  }, [call, session]);

  async function addToCart(id) {
    if (!session) { setModal({ type: 'auth' }); return; }
    if (adding) return;
    setAdding(true);
    try { const result = await call('/api/cart/items', { method: 'POST', body: JSON.stringify({ productId: Number(id), quantity: 1 }) }); setCart(result?.listCartItems || []); notify('Đã thêm sản phẩm vào giỏ hàng.'); }
    catch (error) { notify(error.message); } finally { setAdding(false); }
  }
  async function openCart() {
    if (!session) { setModal({ type: 'auth' }); return; }
    try { const result = await call(`/api/cart?userId=${encodeURIComponent(session.userId)}`); setCart(result?.listCartItems || []); setModal({ type: 'cart' }); }
    catch (error) { notify(error.message); }
  }
  async function clearCart() {
    try { await call('/api/cart', { method: 'DELETE' }); setCart([]); notify('Đã xóa giỏ hàng.'); }
    catch (error) { notify(error.message); }
  }
  async function logout() {
    try { await call('/auth/logout', { method: 'POST', headers: { 'x-token': session.refreshToken } }); }
    catch { notify('Đã đăng xuất khỏi trình duyệt.'); }
    setSession(null); setCart([]);
  }
  const visibleProducts = products.filter(p => (!category || String(p.categoryId) === category) && (!query.trim() || `${p.name || ''} ${p.description || ''}`.toLocaleLowerCase('vi').includes(query.trim().toLocaleLowerCase('vi'))));
  const cartCount = cart.reduce((total, item) => total + (Number(item.quantity) || 0), 0);
  const selectedProduct = modal?.type === 'product' ? products.find(p => p.id === modal.id) : null;

  return <>
    <div className="announcement">MỘT CHÚT CÔNG NGHỆ, MỘT NGÀY TỐT HƠN <span>✳</span> CHÀO MỪNG ĐẾN TECH DEVICE</div>
    <header className="header wrap"><Logo /><nav aria-label="Điều hướng chính"><a href="#products">Sản phẩm</a><a href="#about">Về chúng tôi</a></nav><div className="header-actions"><button className="text-button" onClick={() => session ? logout() : setModal({ type: 'auth' })}>{session ? 'Đăng xuất' : 'Đăng nhập'}</button><button className="cart-button" onClick={openCart}>Giỏ hàng <span>{cartCount}</span></button></div></header>
    <main id="home">
      <section className="hero wrap"><div className="hero-copy"><div className="eyebrow"><span className="dot" /> THIẾT BỊ TỐT. CUỘC SỐNG TỐT.</div><h1>Công nghệ<br />cho <em>mỗi ngày.</em></h1><p>Những thiết bị bạn cần, cho công việc, cảm hứng và những điều bạn yêu thích.</p><a className="primary-button" href="#products">Khám phá sản phẩm <span>↗</span></a><div className="hero-note"><span className="stars">★★★★★</span><span>Chọn điều phù hợp với bạn</span></div></div><div className="hero-art" aria-hidden="true"><div className="art-orbit orbit-one" /><div className="art-orbit orbit-two" /><div className="art-spark">✳</div><div className="art-label">A LITTLE<br />MORE<br />EVERYDAY.</div><div className="device-shadow" /><div className="headphones"><div className="band" /><div className="ear ear-left" /><div className="ear ear-right" /></div><div className="art-caption"><span>01 / 03</span><span>MADE FOR EVERY DAY ↗</span></div></div></section>
      <section className="benefits wrap" aria-label="Điểm nổi bật"><div><span>✳</span><p><strong>Chọn lựa dễ dàng</strong><small>Tìm thiết bị phù hợp với bạn</small></p></div><div><span>◈</span><p><strong>Công nghệ mỗi ngày</strong><small>Cho công việc và cuộc sống</small></p></div><div><span>↗</span><p><strong>Mua sắm thuận tiện</strong><small>Quản lý giỏ hàng của bạn</small></p></div></section>
      <section id="products" className="products wrap"><div className="section-heading"><div><div className="eyebrow"><span className="dot" /> TÌM MÓN ĐỒ YÊU THÍCH</div><h2>Khám phá sản phẩm<span>.</span></h2><p>Chọn một chút cảm hứng cho góc công nghệ của bạn.</p></div><span className="section-index">THE COLLECTION / 01</span></div><div className="toolbar"><div className="filters"><button className={`chip ${category === '' ? 'active' : ''}`} onClick={() => setCategory('')}>Tất cả</button>{categories.map(c => <button key={c.id} className={`chip ${category === String(c.id) ? 'active' : ''}`} onClick={() => setCategory(String(c.id))}>{c.name}</button>)}</div><label className="search"><span aria-hidden="true">⌕</span><input type="search" value={query} onChange={e => setQuery(e.target.value)} placeholder="Tìm kiếm sản phẩm..." aria-label="Tìm kiếm sản phẩm" /></label></div>
        <div className="status" role="status">{status || (session && !visibleProducts.length ? (products.length ? 'Không tìm thấy sản phẩm phù hợp.' : 'Hiện chưa có sản phẩm để hiển thị.') : '')}</div>
        <div className="product-grid">{visibleProducts.map(p => <article className="product-card" key={p.id}><button className="product-image" onClick={() => setModal({ type: 'product', id: p.id })} aria-label={`Xem ${p.name}`}><span className="product-number">TECH DEVICE / {p.id}</span><ProductImage product={p} /></button><div className="product-info"><div className="product-meta">{categories.find(c => c.id === p.categoryId)?.name || 'TECH DEVICE'}</div><h3>{p.name || 'Sản phẩm'}</h3><p>{p.status || 'Thiết bị công nghệ'}</p><div className="product-bottom"><span>{p.ratingAvg != null ? `★ ${p.ratingAvg} · ${p.reviewCount || 0} đánh giá` : 'Khám phá chi tiết'}</span><button className="add-button" disabled={adding} onClick={() => addToCart(p.id)} aria-label={`Thêm ${p.name} vào giỏ hàng`}>+</button></div></div></article>)}</div>
      </section>
      <section className="about wrap" id="about"><div><div className="eyebrow"><span className="dot" /> ĐƠN GIẢN LÀ TỐT HƠN</div><h2>Công nghệ vừa đủ.<br /><em>Cho những điều thật sự quan trọng.</em></h2></div><p>Tech Device mang đến một không gian mua sắm gọn gàng, nơi bạn có thể tìm kiếm và lựa chọn thiết bị cho nhịp sống của mình.</p><span className="about-icon">✳</span></section>
    </main>
    <footer className="footer wrap"><Logo /><p>Chút công nghệ. Thêm nhiều cảm hứng.</p><span>© TECH DEVICE</span></footer>
    {modal?.type === 'auth' && <AuthModal onClose={() => setModal(null)} onLogin={setSession} notify={notify} />}
    {modal?.type === 'cart' && <Modal onClose={() => setModal(null)} className="cart-dialog"><div className="dialog-head"><div><div className="eyebrow">CỦA BẠN</div><h2>Giỏ hàng<span>.</span></h2></div><button className="icon-button" onClick={() => setModal(null)} aria-label="Đóng">×</button></div><p className="dialog-intro">Những lựa chọn đã được lưu trong giỏ hàng.</p><div className="cart-content">{cart.length ? cart.map(item => { const product = products.find(p => String(p.id) === String(item.product_id)); return <div className="cart-item" key={item.product_id}><div className="cart-thumb"><ProductImage product={product} /></div><div><strong>{product?.name || `Sản phẩm #${item.product_id}`}</strong><small>Số lượng: {item.quantity}</small></div></div>; }) : <div className="status">Giỏ hàng của bạn đang trống.</div>}</div>{cart.length > 0 && <div className="cart-actions"><button className="outline-button" onClick={clearCart}>Xóa toàn bộ giỏ hàng</button></div>}</Modal>}
    {selectedProduct && <Modal onClose={() => setModal(null)} className="product-dialog"><div className="dialog-head"><div className="eyebrow">CHI TIẾT SẢN PHẨM</div><button className="icon-button" onClick={() => setModal(null)} aria-label="Đóng">×</button></div><div className="detail-image"><ProductImage product={selectedProduct} /></div><h2>{selectedProduct.name}</h2><p className="detail-description">{selectedProduct.description || 'Chưa có mô tả cho sản phẩm này.'}</p>{selectedProduct.specifications && <p className="detail-description"><strong>Thông số:</strong> {selectedProduct.specifications}</p>}<button className="primary-button full" disabled={adding} onClick={() => addToCart(selectedProduct.id)}>Thêm vào giỏ hàng <span>↗</span></button></Modal>}
    <div className={`toast ${message ? 'show' : ''}`} role="status" aria-live="polite">{message}</div>
  </>;
}

export default Home;
