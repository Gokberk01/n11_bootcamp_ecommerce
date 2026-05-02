import { useEffect, useState } from 'react';
import api from '../services/api';
import { useCart } from '../context/CartContext';

const Home = () => {
  const [products, setProducts] = useState([]);
  const [page, setPage] = useState(0);
  const { addToCart } = useCart();

  useEffect(() => {
    api.get(`/product/paged?page=${page}&size=4`)
      .then(res => setProducts(res.data.items))
      .catch(err => console.error("Hata:", err));
  }, [page]);

  return (
    <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
      <h1 style={{ textAlign: 'center', marginBottom: '30px' }}>Ürün Kataloğu</h1>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))', gap: '20px' }}>
        {products.map(p => (
          <div key={p.id} style={{ border: '1px solid #ddd', padding: '15px', borderRadius: '8px', textAlign: 'center', background: '#fff', color: '#000' }}>
            <h3>{p.name}</h3>
            <p style={{ fontWeight: 'bold', color: '#2563eb' }}>{p.price} TL</p>
            <button 
              onClick={() => addToCart(p)}
              style={{ background: '#059669', color: 'white', border: 'none', padding: '10px 20px', borderRadius: '5px', cursor: 'pointer' }}
            >
              Sepete Ekle
            </button>
          </div>
        ))}
      </div>
      <div style={{ marginTop: '30px', textAlign: 'center' }}>
        <button onClick={() => setPage(p => Math.max(0, p - 1))} style={{ marginRight: '10px' }}>Önceki</button>
        <span>Sayfa {page + 1}</span>
        <button onClick={() => setPage(p => p + 1)} style={{ marginLeft: '10px' }}>Sonraki</button>
      </div>
    </div>
  );
};

export default Home;