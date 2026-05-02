import React, { useState } from 'react';
import api from '../services/api';
import { useNavigate } from 'react-router-dom';
import { Lock, User, LogIn, AlertCircle } from 'lucide-react';

const Login = () => {
  const [formData, setFormData] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // User Service: /api/user/signin endpoint'ine istek atar
      const res = await api.post('/user/signin', formData);
      
      // Token ve kullanıcı bilgisini sakla
      localStorage.setItem('token', res.data.token);
      localStorage.setItem('username', formData.username);
      
      // Başarılı giriş sonrası ana sayfaya yönlendir
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Giriş yapılamadı. Bilgilerinizi kontrol edin.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={containerStyle}>
      <div style={cardStyle}>
        <div style={headerStyle}>
          <div style={iconCircleStyle}>
            <LogIn size={32} color="#2563eb" />
          </div>
          <h2 style={titleStyle}>Hoş Geldiniz</h2>
          <p style={subtitleStyle}>Devam etmek için lütfen giriş yapın</p>
        </div>

        {error && (
          <div style={errorBoxStyle}>
            <AlertCircle size={18} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} style={formStyle}>
          <div style={inputGroupStyle}>
            <label style={labelStyle}>Kullanıcı Adı</label>
            <div style={inputWrapperStyle}>
              <User size={20} style={inputIconStyle} />
              <input
                type="text"
                placeholder="Kullanıcı adınızı girin"
                style={inputStyle}
                required
                onChange={(e) => setFormData({ ...formData, username: e.target.value })}
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
            {loading ? 'Giriş Yapılıyor...' : 'Giriş Yap'}
          </button>
        </form>

        <div style={footerStyle}>
          <span>Hesabınız yok mu?</span>
          <button style={registerLinkStyle} onClick={() => navigate('/signup')}>Hemen Kaydol</button>
        </div>
      </div>
    </div>
  );
};

// --- Modern UI Stilleri ---

const containerStyle = {
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  minHeight: '80vh',
};

const cardStyle = {
  backgroundColor: '#ffffff',
  padding: '40px',
  borderRadius: '16px',
  boxShadow: '0 10px 25px rgba(0,0,0,0.05)',
  width: '100%',
  maxWidth: '400px',
};

const headerStyle = {
  textAlign: 'center',
  marginBottom: '30px',
};

const iconCircleStyle = {
  width: '64px',
  height: '64px',
  backgroundColor: '#eff6ff',
  borderRadius: '50%',
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  margin: '0 auto 16px',
};

const titleStyle = {
  fontSize: '1.5rem',
  fontWeight: '700',
  color: '#1f2937',
  margin: '0 0 8px 0',
};

const subtitleStyle = {
  color: '#6b7280',
  fontSize: '0.95rem',
  margin: 0,
};

const formStyle = {
  display: 'flex',
  flexDirection: 'column',
  gap: '20px',
};

const inputGroupStyle = {
  display: 'flex',
  flexDirection: 'column',
  gap: '8px',
};

const labelStyle = {
  fontSize: '0.9rem',
  fontWeight: '600',
  color: '#374151',
};

const inputWrapperStyle = {
  position: 'relative',
  display: 'flex',
  alignItems: 'center',
};

const inputIconStyle = {
  position: 'absolute',
  left: '12px',
  color: '#9ca3af',
};

const inputStyle = {
  width: '100%',
  padding: '12px 12px 12px 40px',
  borderRadius: '8px',
  border: '1px solid #d1d5db',
  fontSize: '1rem',
  outline: 'none',
  transition: 'border-color 0.2s',
  boxSizing: 'border-box'
};

const buttonStyle = {
  backgroundColor: '#2563eb',
  color: 'white',
  padding: '12px',
  borderRadius: '8px',
  border: 'none',
  fontSize: '1rem',
  fontWeight: '600',
  cursor: 'pointer',
  marginTop: '10px',
  transition: 'background-color 0.2s',
};

const errorBoxStyle = {
  backgroundColor: '#fef2f2',
  color: '#dc2626',
  padding: '12px',
  borderRadius: '8px',
  marginBottom: '20px',
  display: 'flex',
  alignItems: 'center',
  gap: '10px',
  fontSize: '0.85rem',
};

const footerStyle = {
  marginTop: '25px',
  textAlign: 'center',
  fontSize: '0.9rem',
  color: '#6b7280',
};

const registerLinkStyle = {
  background: 'none',
  border: 'none',
  color: '#2563eb',
  fontWeight: '600',
  marginLeft: '5px',
  cursor: 'pointer',
};

export default Login;