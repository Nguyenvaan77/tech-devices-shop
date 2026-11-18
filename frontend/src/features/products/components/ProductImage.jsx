import React, { useState } from 'react';
import './ProductImage.scss';

export default function ProductImage({ product }) {
  const [broken, setBroken] = useState(false);
  const url = product?.images?.[0]?.publicUrl;
  let validUrl = false;
  try { validUrl = !!url && ['http:', 'https:'].includes(new URL(url, window.location.origin).protocol); } catch { /* Hiển thị biểu tượng thay thế. */ }
  return validUrl && !broken ? <img src={url} alt="" loading="lazy" onError={() => setBroken(true)} /> : <span className="placeholder" aria-hidden="true">✳</span>;
}

