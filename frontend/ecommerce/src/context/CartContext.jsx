import React, { createContext, useState, useContext, useEffect } from 'react';

const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [cartItems, setCartItems] = useState([]);
  // Kullanıcı giriş yapmadıysa rastgele bir id atıyoruz
  const [guestId] = useState(() => localStorage.getItem('guestId') || `guest_${Math.random().toString(36).substr(2, 9)}`);

  useEffect(() => {
    localStorage.setItem('guestId', guestId);
    const savedCart = localStorage.getItem('cart');
    if (savedCart) setCartItems(JSON.parse(savedCart));
  }, [guestId]);

  const addToCart = (product) => {
    setCartItems((prev) => {
      const updated = [...prev, product];
      localStorage.setItem('cart', JSON.stringify(updated));
      return updated;
    });
  };

  const getActiveIdentifier = () => {
    return localStorage.getItem('username') || guestId;
  };

  return (
    <CartContext.Provider value={{ cartItems, addToCart, getActiveIdentifier, setCartItems }}>
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => useContext(CartContext);