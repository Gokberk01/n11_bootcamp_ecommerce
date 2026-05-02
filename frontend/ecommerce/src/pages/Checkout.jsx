import { useCart } from '../context/CartContext';
import api from '../services/api';

const Checkout = () => {
  const { cartItems, getActiveIdentifier, setCartItems } = useCart();

  const handlePay = async () => {
    const identifier = getActiveIdentifier();
    
    const orderRequest = {
      username: identifier, // Backend ziyaretçiyi de bu alanla kabul etmeli
      totalAmount: cartItems.reduce((acc, item) => acc + item.price, 0),
      items: cartItems.map(i => i.id)
    };

    try {
      const res = await api.post('/orders', orderRequest);
      alert("Sipariş başarıyla oluşturuldu! ID: " + res.data.id);
      setCartItems([]);
      localStorage.removeItem('cart');
    } catch (err) {
      alert("Ödeme sırasında bir hata oluştu.");
    }
  };

  return (
    <div style={{ maxWidth: '600px', margin: '40px auto', padding: '20px', background: '#fff', color: '#000', borderRadius: '10px' }}>
      <h2>Sipariş Özeti</h2>
      {cartItems.length === 0 ? <p>Sepetiniz boş.</p> : (
        <>
          {cartItems.map((item, idx) => (
            <div key={idx} style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid #eee', padding: '10px 0' }}>
              <span>{item.name}</span>
              <span>{item.price} TL</span>
            </div>
          ))}
          <div style={{ marginTop: '20px', fontWeight: 'bold', fontSize: '1.2rem' }}>
            Toplam: {cartItems.reduce((acc, item) => acc + item.price, 0)} TL
          </div>
          <button 
            onClick={handlePay}
            style={{ width: '100%', marginTop: '20px', padding: '15px', background: '#ea580c', color: 'white', border: 'none', borderRadius: '5px', fontWeight: 'bold', cursor: 'pointer' }}
          >
            Ödemeyi Tamamla
          </button>
        </>
      )}
    </div>
  );
};

export default Checkout;