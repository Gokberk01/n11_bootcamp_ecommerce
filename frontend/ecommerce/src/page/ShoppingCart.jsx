import React, { useEffect, useState } from 'react';
import api from '../service/api';
import { Trash2, Plus, Minus, ArrowLeft, ShoppingBag, CreditCard, Palette } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const ShoppingCart = () => {
    const navigate = useNavigate();
    const [cartItems, setCartItems] = useState([]);
    const [loading, setLoading] = useState(true);
    
    
    const [cartQuantities, setCartQuantities] = useState(() => {
        const saved = localStorage.getItem('cartQuantities');
        return saved ? JSON.parse(saved) : {};
    });

    const cartId = localStorage.getItem('activeCartId');

    useEffect(() => {
        const fetchCart = async () => {
            if (!cartId) {
                setLoading(false);
                return;
            }
            try {
                const response = await api.get(`/shopping-cart/${cartId}`);
                
                const items = response.data.products || [];
                setCartItems(items);
            } catch (err) {
                console.error("Sepet getirme hatası:", err);
            } finally {
                setLoading(false);
            }
        };
        fetchCart();
    }, [cartId]);

    
    const updateLocalQuantity = (productId, delta) => {
        const currentQty = cartQuantities[productId] || 0;
        const newQty = Math.max(0, currentQty + delta);
        
        const newQuantities = { ...cartQuantities, [productId]: newQty };
        if (newQty === 0) delete newQuantities[productId];
        
        setCartQuantities(newQuantities);
        localStorage.setItem('cartQuantities', JSON.stringify(newQuantities));
        
        
        if (newQty === 0) {
            setCartItems(prev => prev.filter(item => item.id !== productId));
        }
    };

    
    const handleAdd = async (product) => {
        try {
            await api.post(`/shopping-cart/${cartId}`, [product], {
                headers: { 'Content-Type': 'application/json' }
            });
            updateLocalQuantity(product.id, 1);
        } catch (err) {
            console.error("Ekleme hatası:", err);
        }
    };

    
    const handleRemove = async (productId) => {
        const currentQty = cartQuantities[productId] || 0;
        try {
            if (currentQty === 1) {
                await api.delete(`/shopping-cart/${cartId}/products/${productId}`);
            }
            updateLocalQuantity(productId, -1);
        } catch (err) {
            console.error("Silme hatası:", err);
        }
    };

    const calculateTotal = () => {
        return cartItems.reduce((acc, item) => {
            const qty = cartQuantities[item.id] || 0;
            return acc + (item.price * qty);
        }, 0);
    };

    
    const handleProceedToCheckout = () => {
        if (cartItems.length === 0) {
            alert("Sepetiniz boş, lütfen ürün ekleyin.");
            return;
        }
        
        navigate('/checkout');
    };

    if (loading) return <div style={loader}>Sepetiniz yükleniyor...</div>;

    return (
        <div style={pageWrapper}>
            <style>
                {`
                .cart-card { transition: all 0.3s ease; }
                .qty-btn { transition: all 0.2s; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; }
                .qty-btn:hover { filter: brightness(0.9); transform: scale(1.1); }
                .checkout-btn { transition: all 0.3s; background: #2563eb; border: none; cursor: pointer; }
                .checkout-btn:hover { background: #1d4ed8; transform: translateY(-2px); box-shadow: 0 10px 20px -5px rgba(37, 99, 235, 0.4); }
                `}
            </style>

            <div style={container}>
                <header style={cartHeader}>
                    <button onClick={() => navigate('/')} style={backButton}>
                        <ArrowLeft size={18} /> Market'e Dön
                    </button>
                    <h1 style={title}>Alışveriş Sepetim</h1>
                </header>

                {cartItems.length === 0 ? (
                    <div style={emptyState}>
                        <ShoppingBag size={80} color="#e2e8f0" />
                        <h2 style={{color: '#475569', marginTop: '20px'}}>Sepetiniz Boş</h2>
                        <p style={{color: '#94a3b8'}}>Görünüşe göre henüz bir ürün eklememişsiniz.</p>
                    </div>
                ) : (
                    <div style={mainLayout}>
                        {/* Ürün Listesi */}
                        <div style={itemsList}>
                            {cartItems.map((item) => {
                                const quantity = cartQuantities[item.id] || 0;
                                if (quantity === 0) return null;

                                return (
                                    <div key={item.id} style={itemRow} className="cart-card">
                                        <div style={imgContainer}>
                                            <img src={item.img || 'https://via.placeholder.com/100'} alt={item.title} style={productImg} />
                                        </div>

                                        <div style={infoContainer}>
                                            <h3 style={itemName}>{item.title}</h3>
                                            <div style={itemMeta}>
                                                <span style={brandTag}>{item.brand}</span>
                                                {item.color && (
                                                    <span style={colorTag}>
                                                        <Palette size={12} style={{marginRight: 4}}/> {item.color}
                                                    </span>
                                                )}
                                            </div>
                                        </div>

                                        {/* Sayaç Yapısı */}
                                        <div style={counterWrapper}>
                                            <button onClick={() => handleRemove(item.id)} className="qty-btn" style={minusBtn}>
                                                <Minus size={16} />
                                            </button>
                                            <div style={qtyDisplay}>
                                                <span style={qtyText}>{quantity}</span>
                                                <span style={qtyLabel}>Adet</span>
                                            </div>
                                            <button onClick={() => handleAdd(item)} className="qty-btn" style={plusBtn}>
                                                <Plus size={16} />
                                            </button>
                                        </div>

                                        <div style={priceWrapper}>
                                            <div style={totalItemPrice}>{(item.price * quantity).toLocaleString()} TL</div>
                                            <div style={unitPrice}>{item.price.toLocaleString()} TL / adet</div>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>

                        {/* Sipariş Özeti */}
                        <div style={summaryPanel}>
                            <h3 style={summaryTitle}>Sipariş Özeti</h3>
                            <div style={summaryDetail}>
                                <div style={summaryRow}>
                                    <span>Ürün Toplamı</span>
                                    <span>{calculateTotal().toLocaleString()} TL</span>
                                </div>
                                <div style={summaryRow}>
                                    <span>Kargo</span>
                                    <span style={{color: '#10b981'}}>Ücretsiz</span>
                                </div>
                                <div style={totalDivider}></div>
                                <div style={finalTotalRow}>
                                    <span>Genel Toplam</span>
                                    <span style={finalPrice}>{calculateTotal().toLocaleString()} TL</span>
                                </div>
                            </div>
                            <button className="checkout-btn" style={checkoutBtn} onClick={handleProceedToCheckout}>
                                <CreditCard size={20} /> Satın Almayı Tamamla
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};


const pageWrapper = { backgroundColor: '#f8fafc', minHeight: '100vh', padding: '50px 0' };
const container = { maxWidth: '1200px', margin: '0 auto', padding: '0 20px' };
const cartHeader = { marginBottom: '40px' };
const backButton = { display: 'flex', alignItems: 'center', gap: '8px', background: 'none', border: 'none', color: '#64748b', cursor: 'pointer', fontWeight: '600', marginBottom: '10px' };
const title = { fontSize: '2.2rem', fontWeight: '900', color: '#0f172a' };

const mainLayout = { display: 'grid', gridTemplateColumns: '1fr 380px', gap: '40px', alignItems: 'start' };
const itemsList = { display: 'flex', flexDirection: 'column', gap: '20px' };

const itemRow = { 
    backgroundColor: '#fff', borderRadius: '24px', padding: '20px', 
    display: 'flex', alignItems: 'center', gap: '25px', 
    boxShadow: '0 4px 6px -1px rgba(0,0,0,0.02)', border: '1px solid #f1f5f9' 
};

const imgContainer = { width: '100px', height: '100px', backgroundColor: '#f8fafc', borderRadius: '16px', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '10px' };
const productImg = { maxWidth: '100%', maxHeight: '100%', objectFit: 'contain' };

const infoContainer = { flex: 1 };
const itemName = { fontSize: '1.1rem', fontWeight: '700', color: '#1e293b', marginBottom: '8px' };
const itemMeta = { display: 'flex', gap: '10px' };
const brandTag = { backgroundColor: '#0f172a', color: '#fff', padding: '4px 10px', borderRadius: '6px', fontSize: '0.65rem', fontWeight: '700', textTransform: 'uppercase' };
const colorTag = { display: 'flex', alignItems: 'center', backgroundColor: '#f1f5f9', color: '#64748b', padding: '4px 10px', borderRadius: '6px', fontSize: '0.65rem', fontWeight: '700' };


const counterWrapper = { 
    display: 'flex', alignItems: 'center', backgroundColor: '#f8fafc', 
    padding: '6px', borderRadius: '18px', border: '1px solid #e2e8f0', minWidth: '150px' 
};
const minusBtn = { width: '36px', height: '36px', borderRadius: '12px', backgroundColor: '#fff', color: '#64748b', boxShadow: '0 2px 4px rgba(0,0,0,0.05)' };
const plusBtn = { width: '36px', height: '36px', borderRadius: '12px', backgroundColor: '#2563eb', color: '#fff', boxShadow: '0 4px 10px rgba(37, 99, 235, 0.2)' };
const qtyDisplay = { display: 'flex', flexDirection: 'column', alignItems: 'center', flex: 1 };
const qtyText = { fontSize: '1rem', fontWeight: '800', color: '#0f172a' };
const qtyLabel = { fontSize: '0.55rem', color: '#94a3b8', fontWeight: '700', textTransform: 'uppercase' };

const priceWrapper = { textAlign: 'right', minWidth: '130px' };
const totalItemPrice = { fontSize: '1.25rem', fontWeight: '800', color: '#2563eb' };
const unitPrice = { fontSize: '0.75rem', color: '#94a3b8', marginTop: '4px' };

const summaryPanel = { backgroundColor: '#fff', borderRadius: '28px', padding: '30px', boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.05)', position: 'sticky', top: '20px' };
const summaryTitle = { fontSize: '1.4rem', fontWeight: '800', color: '#0f172a', marginBottom: '25px' };
const summaryDetail = { display: 'flex', flexDirection: 'column', gap: '15px' };
const summaryRow = { display: 'flex', justifyContent: 'space-between', color: '#64748b', fontWeight: '600' };
const totalDivider = { height: '1px', backgroundColor: '#f1f5f9', margin: '10px 0' };
const finalTotalRow = { display: 'flex', justifyContent: 'space-between', alignItems: 'center' };
const finalPrice = { fontSize: '1.8rem', fontWeight: '900', color: '#0f172a' };
const checkoutBtn = { width: '100%', marginTop: '30px', padding: '18px', borderRadius: '20px', color: '#fff', fontWeight: '700', fontSize: '1.1rem', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '12px' };

const emptyState = { textAlign: 'center', padding: '100px 0', display: 'flex', flexDirection: 'column', alignItems: 'center' };
const loader = { textAlign: 'center', padding: '100px', color: '#64748b', fontSize: '1.2rem' };

export default ShoppingCart;