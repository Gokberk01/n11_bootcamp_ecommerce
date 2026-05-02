import React, { useState } from 'react';
import api from '../services/api';
import { useNavigate } from 'react-router-dom';
import { Lock, User, Mail, UserPlus, AlertCircle, CheckCircle2 } from 'lucide-react';

const Signup = () => {
  const [formData, setFormData] = useState({ username: '', email: '', password: '' });
  const [status, setStatus] = useState({ type: '', message: '' });
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setStatus({ type: '', message: '' });
    setLoading(true);

    try {
      // User Service: /api/user/signup endpoint'ine istek atar
      await api.post('/user/signup', formData);
      setStatus({ type: 'success', message: 'Hesabınız başarıyla oluşturuldu! Yönlendiriliyorsunuz...' });
      
      // 2 saniye sonra login sayfasına yönlendir
      setTimeout(() => navigate('/login'), 2000);
    } catch (err) {
      setStatus({ 
        type: 'error', 
        message: err.response?.data?.message || 'Kayıt sırasında bir hata oluştu.' 
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={containerStyle}>
      <div style={cardStyle}>
        <div style={headerStyle}>
          <div style={iconCircleStyle}>
            <UserPlus size={32} color="#2563eb" />
          </div>
          <h2 style={titleStyle}>Yeni Hesap Oluştur</h2>
          <p style={subtitleStyle}>Aramıza katılmak için formu doldurun</p>
        </div>

        {status.message && (
          <div style={status.type === 'error' ? errorBoxStyle : successBoxStyle}>
            {status.type === 'error' ? <AlertCircle size={18} /> : <CheckCircle2 size={18} />}
            <span>{status.message}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} style={formStyle}>
          <div style={inputGroupStyle}>
            <label style={labelStyle}>Kullanıcı Adı</label>
            <div style={inputWrapperStyle}>
              <User size={20} style={inputIconStyle} />
              <input
                type="text"
                placeholder="Kullanıcı adınızı seçin"
                style={inputStyle}
                required
                onChange={(e) => setFormData({ ...formData, username: e.target.value })}
              />
            </div>
          </div>

          <div style={inputGroupStyle}>
            <label style={labelStyle}>E-posta</label>
            <div style={inputWrapperStyle}>
              <Mail size={20} style={inputIconStyle} />
              <input
                type="email"
                placeholder="ornek@mail.com"
                style={inputStyle}
                required
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              />
            </div>
          </div>

          <div style={inputGroupStyle}>
            <label style={labelStyle}>Şifre</label>
            <div style={inputWrapperStyle}>
              <Lock size={20} style={inputIconStyle} />
              <input
                type="password"
                placeholder="••••••••"
                style={inputStyle}
                required
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              />
            </div>
          </div>

          <button 
            type="submit" 
            style={loading ? { ...buttonStyle, opacity: 0.7 } : buttonStyle}
            disabled={loading}
          >
            {loading ? 'Kaydediliyor...' : 'Kayıt Ol'}
          </button>
        </form>

        <div style={footerStyle}>
          <span>Zaten hesabınız var mı?</span>
          <button style={loginLinkStyle} onClick={() => navigate('/login')}>Giriş Yap</button>
        </div>
      </div>
    </div>
  );
};

// --- Stiller (Login ile uyumlu) ---
const containerStyle = { display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '80vh' };
const cardStyle = { backgroundColor: '#ffffff', padding: '40px', borderRadius: '16px', boxShadow: '0 10px 25px rgba(0,0,0,0.05)', width: '100%', maxWidth: '400px' };
const headerStyle = { textAlign: 'center', marginBottom: '30px' };
const iconCircleStyle = { width: '64px', height: '64px', backgroundColor: '#eff6ff', borderRadius: '50%', display: 'flex', justifyContent: 'center', alignItems: 'center', margin: '0 auto 16px' };
const titleStyle = { fontSize: '1.5rem', fontWeight: '700', color: '#1f2937', margin: '0 0 8px 0' };
const subtitleStyle = { color: '#6b7280', fontSize: '0.95rem', margin: 0 };
const formStyle = { display: 'flex', flexDirection: 'column', gap: '20px' };
const inputGroupStyle = { display: 'flex', flexDirection: 'column', gap: '8px' };
const labelStyle = { fontSize: '0.9rem', fontWeight: '600', color: '#374151' };
const inputWrapperStyle = { position: 'relative', display: 'flex', alignItems: 'center' };
const inputIconStyle = { position: 'absolute', left: '12px', color: '#9ca3af' };
const inputStyle = { width: '100%', padding: '12px 12px 12px 40px', borderRadius: '8px', border: '1px solid #d1d5db', fontSize: '1rem', outline: 'none', boxSizing: 'border-box' };
const buttonStyle = { backgroundColor: '#2563eb', color: 'white', padding: '12px', borderRadius: '8px', border: 'none', fontSize: '1rem', fontWeight: '600', cursor: 'pointer', marginTop: '10px' };
const errorBoxStyle = { backgroundColor: '#fef2f2', color: '#dc2626', padding: '12px', borderRadius: '8px', marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '10px', fontSize: '0.85rem' };
const successBoxStyle = { backgroundColor: '#f0fdf4', color: '#16a34a', padding: '12px', borderRadius: '8px', marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '10px', fontSize: '0.85rem' };
const footerStyle = { marginTop: '25px', textAlign: 'center', fontSize: '0.9rem', color: '#6b7280' };
const loginLinkStyle = { background: 'none', border: 'none', color: '#2563eb', fontWeight: '600', marginLeft: '5px', cursor: 'pointer' };

export default Signup;