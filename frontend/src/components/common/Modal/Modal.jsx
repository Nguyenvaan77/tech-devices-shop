import React, { useEffect, useRef } from 'react';
import './Modal.scss';

export default function Modal({ children, onClose, className = '' }) {
  const ref = useRef(null);
  useEffect(() => { ref.current?.showModal(); }, []);
  return <dialog ref={ref} className={`dialog ${className}`} onClose={onClose}>{children}</dialog>;
}

