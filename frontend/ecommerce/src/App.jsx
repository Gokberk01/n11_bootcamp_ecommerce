import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Checkout from './pages/Checkout';
import Signup from './pages/Signup';
import { CartProvider } from './context/CartContext';
import { ShoppingCart, User, Store, LogOut } from 'lucide-react';

function App() {
  const username = localStorage.getItem('username');

  const handleLogout = () => {
    localStorage.clear();
    window.location.href = '/';
  };

  return (
    <CartProvider>
      <Router>
        <div style={{ minHeight: '100vh', backgroundColor: '#00050a' }}>
          {/* Modern Navbar Yapısı */}
          <nav style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            padding: '1rem 2rem',
            backgroundColor: '#eef5f4',
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
            position: 'sticky',
            top: 0,
            zIndex: 1000
          }}>
            {/* Logo Kısmı */}
            <Link to="/" style={{ 
              display: 'flex', 
              alignItems: 'center', 
              gap: '10px', 
              fontSize: '1.5rem', 
              fontWeight: 'bold', 
              color: '#2563eb', 
              textDecoration: 'none' 
            }}>
              <Store size={28} />
              <span>N11 Market</span>
            </Link>

            {/* Linkler ve Butonlar */}
            <div style={{ display: 'flex', alignItems: 'center', gap: '30px' }}>
              <Link to="/" style={navLinkStyle}>Ürünler</Link>
              
              <Link to="/checkout" style={{ ...navLinkStyle, display: 'flex', alignItems: 'center', gap: '5px' }}>
                <ShoppingCart size={20} />
                Sepetim
              </Link>

              {username ? (
                <div style={{ display: 'flex', alignItems: 'center', gap: '15px', borderLeft: '1px solid #ddd', paddingLeft: '15px' }}>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '5px', color: '#4b5563', fontSize: '0.9rem' }}>
                    <User size={18} />
                    {username}
                  </span>
                  <button onClick={handleLogout} style={logoutButtonStyle}>
                    <LogOut size={16} />
                    Çıkış
                  </button>
                </div>
              ) : (
                <Link to="/login" style={loginButtonStyle}>Giriş Yap</Link>
              )}
            </div>
          </nav>

          {/* Sayfa İçeriği */}
          <div style={{ padding: '2rem' }}>
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/login" element={<Login />} />
              <Route path="/signup" element={<Signup />} />
              <Route path="/checkout" element={<Checkout />} />
            </Routes>
          </div>
        </div>
      </Router>
    </CartProvider>
  );
}

// Yardımcı Stil Objeleri (CSS dosyası ile uğraşmamak için)
const navLinkStyle = {
  textDecoration: 'none',
  color: '#374151',
  fontWeight: '500',
  fontSize: '1rem',
  transition: 'color 0.2s'
};

const loginButtonStyle = {
  textDecoration: 'none',
  backgroundColor: '#2563eb',
  color: 'white',
  padding: '8px 18px',
  borderRadius: '6px',
  fontWeight: '500',
  transition: 'background-color 0.2s'
};

const logoutButtonStyle = {
  display: 'flex',
  alignItems: 'center',
  gap: '5px',
  backgroundColor: 'transparent',
  color: '#ef4444',
  border: '1px solid #ef4444',
  padding: '5px 12px',
  borderRadius: '6px',
  cursor: 'pointer',
  fontSize: '0.85rem'
};

export default App;