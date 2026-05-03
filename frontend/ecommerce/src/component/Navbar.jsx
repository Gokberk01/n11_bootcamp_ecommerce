import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { ShoppingCart, User, Store, LogOut } from 'lucide-react';

const Navbar = () => {

  const [username, setUsername] = useState(localStorage.getItem('username'));

  useEffect(() => {
    
    const handleStorageChange = () => {
      setUsername(localStorage.getItem('username'));
    };

    
    window.addEventListener('storage', handleStorageChange);
    
    return () => {
      window.removeEventListener('storage', handleStorageChange);
    };
  }, []);

  const handleLogout = () => {
    localStorage.clear();
    
    setUsername(null); 
    window.location.href = '/';
  };

  return (
    <nav style={navContainer}>
      {/* Logo Kısmı */}
      <Link to="/" style={logoStyle}>
        <Store size={28} />
        <span>N11 Market</span>
      </Link>

      {/* Linkler ve Butonlar */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '30px' }}>
        <Link to="/" style={navLinkStyle}>Ürünler</Link>
        
        <Link to="/shoppingcart" style={{ ...navLinkStyle, display: 'flex', alignItems: 'center', gap: '5px' }}>
          <ShoppingCart size={20} />
          Sepetim
        </Link>

        {username ? (
          <div style={userSection}>
            <span style={userInfo}>
              <User size={18} />
              {username}
            </span>
            <button onClick={handleLogout} style={logoutButtonStyle}>
              <LogOut size={16} />
              Çıkış
            </button>
          </div>
        ) : (
          <Link to="/signin" style={loginButtonStyle}>Giriş Yap</Link>
        )}
      </div>
    </nav>
  );
};

const navContainer = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  padding: '1rem 5%',
  backgroundColor: '#ffffff',
  boxShadow: '0 2px 10px rgba(0,0,0,0.05)',
  position: 'sticky',
  top: 0,
  zIndex: 1000
};

const logoStyle = { 
  display: 'flex', 
  alignItems: 'center', 
  gap: '10px', 
  fontSize: '1.5rem', 
  fontWeight: '800', 
  color: '#2563eb', 
  textDecoration: 'none' 
};

const navLinkStyle = {
  textDecoration: 'none',
  color: '#334155',
  fontWeight: '600',
  fontSize: '1rem',
  transition: 'color 0.2s'
};

const userSection = { 
  display: 'flex', 
  alignItems: 'center', 
  gap: '15px', 
  borderLeft: '1px solid #e2e8f0', 
  paddingLeft: '15px' 
};

const userInfo = { 
  display: 'flex', 
  alignItems: 'center', 
  gap: '5px', 
  color: '#64748b', 
  fontSize: '0.9rem',
  fontWeight: '500'
};

const loginButtonStyle = {
  textDecoration: 'none',
  backgroundColor: '#2563eb',
  color: 'white',
  padding: '10px 22px',
  borderRadius: '12px',
  fontWeight: '700',
  transition: 'all 0.2s'
};

const logoutButtonStyle = {
  display: 'flex',
  alignItems: 'center',
  gap: '5px',
  backgroundColor: 'transparent',
  color: '#ef4444',
  border: '1px solid #ef4444',
  padding: '6px 14px',
  borderRadius: '10px',
  cursor: 'pointer',
  fontSize: '0.85rem',
  fontWeight: '600'
};

export default Navbar;