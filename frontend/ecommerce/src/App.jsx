import { useState } from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './page/Home';
import ShoppingCart from './page/ShoppingCart';
import Signin from './page/Signin';
import Signup from './page/Signup';
import Checkout from './page/Checkout';
import Navbar from './component/Navbar';
import './App.css'

function App() {
return (<Router>
        {/* Beyaz arkaplan */}
        <div style={{ minHeight: '100vh', backgroundColor: '#ffffff' }}>
          <Navbar />
          
          <div>
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/signin" element={<Signin />} />
              <Route path="/signup" element={<Signup />} />
              <Route path="/shoppingcart" element={<ShoppingCart />} />
              <Route path="/checkout" element={<Checkout />} />
            </Routes>
          </div>
        </div>
      </Router>)
}

export default App
