import React, { useEffect, useState } from 'react';
import api from '../service/api';
import { Plus, Minus, ChevronLeft, ChevronRight, Tag, Palette } from 'lucide-react';

const Home = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const pageSize = 6;

    const [cartId, setCartId] = useState(localStorage.getItem('activeCartId'));
    
    const [cartQuantities, setCartQuantities] = useState(() => {
        const saved = localStorage.getItem('cartQuantities');
        return saved ? JSON.parse(saved) : {};
    });

    useEffect(() => {
        const fetchProducts = async () => {
            setLoading(true);
            try {
                const response = await api.get(`/product/paged?page=${page}&size=${pageSize}`);
                setProducts(response.data.items || response.data.content || []);
                setTotalPages(response.data.totalPages || 0);
            } catch (err) {
                setError("Ürünler yüklenirken bir hata oluştu.");
            } finally {
                setLoading(false);
            }
        };
        fetchProducts();
    }, [page]);

    const updateLocalQuantity = (productId, delta) => {
        const currentQty = cartQuantities[productId] || 0;
        const newQty = Math.max(0, currentQty + delta);
        const newQuantities = { ...cartQuantities, [productId]: newQty };
        if (newQty === 0) delete newQuantities[productId];
        setCartQuantities(newQuantities);
        localStorage.setItem('cartQuantities', JSON.stringify(newQuantities));
    };

    const handleAddToCart = async (product) => {
        try {
            let currentCartId = cartId;

            
            const username = localStorage.getItem('username');
            const cartOwner = username ? username : "guest";

            if (!currentCartId) {
                const cartRes = await api.post('/shopping-cart', cartOwner, {
                    headers: { 'Content-Type': 'text/plain' }
                });
                currentCartId = cartRes.data.id;
                setCartId(currentCartId);
                localStorage.setItem('activeCartId', currentCartId);
            }
            await api.post(`/shopping-cart/${currentCartId}`, [product], {
                headers: { 'Content-Type': 'application/json' }
            });
            updateLocalQuantity(product.id, 1);
        } catch (err) {
            console.error("Ekleme hatası:", err);
        }
    };

    const handleRemoveFromCart = async (productId) => {
        const currentQty = cartQuantities[productId] || 0;
        try {
            if (currentQty === 1) {
                await api.delete(`/shopping-cart/${cartId}/products/${productId}`);
                console.log("Ürün DB'den tamamen silindi.");
            }
            updateLocalQuantity(productId, -1);
        } catch (err) {
            console.error("Silme hatası:", err);
            alert("Ürün sepetten çıkarılırken bir hata oluştu.");
        }
    };

    if (loading && page === 0) return <div style={loader}>Yükleniyor...</div>;

    return (
        <div style={pageWrapper}>
            <style>
                {`
                .product-card { transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); border: 1px solid #f1f5f9; }
                .product-card:hover { transform: translateY(-8px); box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.05); border-color: #2563eb; }
                .qty-btn { transition: all 0.2s; display: flex; align-items: center; justify-content: center; border: none; cursor: pointer; }
                .qty-btn:hover { filter: brightness(0.9); transform: scale(1.05); }
                
                /* Modern Custom Scrollbar for Description */
                .desc-scroll::-webkit-scrollbar { width: 4px; }
                .desc-scroll::-webkit-scrollbar-track { background: #f1f5f9; border-radius: 10px; }
                .desc-scroll::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
                .desc-scroll::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
                `}
            </style>

            <div style={container}>
                <header style={headerSection}>
                    <h1 style={title}>N11 Bootcamp Market</h1>
                    <p style={subtitle}>Kaliteli ürünler, akıllı miktar yönetimi.</p>
                </header>

                <div style={productGrid}>
                    {products.map((product) => {
                        const quantity = cartQuantities[product.id] || 0;
                        
                        return (
                            <div key={product.id} className="product-card" style={productCard}>
                                {/* Sol Üst Badge Alanı */}
                                <div style={badgeContainer}>
                                    <span style={brandBadge}>{product.brand}</span>
                                    <span style={categoryBadge}>
                                        <Tag size={10} style={{marginRight: 4}}/>
                                        {product.category || 'Genel'}
                                    </span>
                                </div>

                                <div style={imageWrapper}>
                                    <img src={product.img || 'https://via.placeholder.com/150'} alt={product.title} style={productImage} />
                                </div>

                                <div style={cardBody}>
                                    <div style={nameColorRow}>
                                        <h3 style={productName}>{product.title}</h3>
                                        {product.color && (
                                            <span style={colorBadge}>
                                                <Palette size={10} style={{marginRight: 4}}/>
                                                {product.color}
                                            </span>
                                        )}
                                    </div>

                                    {/* Scrollable Description */}
                                    <div className="desc-scroll" style={descriptionWrapper}>
                                        {product.description || "Bu ürün için bir açıklama bulunmamaktadır."}
                                    </div>

                                    <div style={priceTag}>{product.price?.toLocaleString()} TL</div>

                                    <div style={actionArea}>
                                        {quantity === 0 ? (
                                            <button onClick={() => handleAddToCart(product)} style={addButton}>
                                                <Plus size={18} /> Sepete Ekle
                                            </button>
                                        ) : (
                                            <div style={counterContainer}>
                                                <button onClick={() => handleRemoveFromCart(product.id)} className="qty-btn" style={minusBtn}>
                                                    <Minus size={18} />
                                                </button>
                                                <div style={quantityDisplay}>
                                                    <span style={qtyNumber}>{quantity}</span>
                                                    <span style={qtyLabel}>Adet</span>
                                                </div>
                                                <button onClick={() => handleAddToCart(product)} className="qty-btn" style={plusBtn}>
                                                    <Plus size={18} />
                                                </button>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>
                        );
                    })}
                </div>

                <div style={paginationArea}>
                    <button disabled={page === 0} onClick={() => setPage(p => p - 1)} style={page === 0 ? disabledBtn : activeBtn}>
                        <ChevronLeft size={20} />
                    </button>
                    <div style={pageIndicator}>
                        <span style={{color: '#2563eb', fontWeight: 'bold'}}>{page + 1}</span> / {totalPages}
                    </div>
                    <button disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)} style={page >= totalPages - 1 ? disabledBtn : activeBtn}>
                        <ChevronRight size={20} />
                    </button>
                </div>
            </div>
        </div>
    );
};


const pageWrapper = { backgroundColor: '#fdfdfd', minHeight: '100vh', padding: '40px 0' };
const container = { maxWidth: '1200px', margin: '0 auto', padding: '0 20px' };
const headerSection = { textAlign: 'center', marginBottom: '50px' };
const title = { fontSize: '2.5rem', color: '#0f172a', fontWeight: '900' };
const subtitle = { color: '#64748b', fontSize: '1.1rem' };
const productGrid = { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '30px' };
const productCard = { backgroundColor: '#fff', borderRadius: '24px', overflow: 'hidden', display: 'flex', flexDirection: 'column', position: 'relative' };


const badgeContainer = { position: 'absolute', top: '15px', left: '15px', zIndex: 5, display: 'flex', flexDirection: 'column', gap: '6px' };
const brandBadge = { backgroundColor: '#0f172a', color: '#fff', padding: '5px 12px', borderRadius: '8px', fontSize: '0.65rem', fontWeight: '800', textTransform: 'uppercase', width: 'fit-content' };
const categoryBadge = { backgroundColor: '#f1f5f9', color: '#64748b', padding: '4px 10px', borderRadius: '8px', fontSize: '0.6rem', fontWeight: '700', display: 'flex', alignItems: 'center', width: 'fit-content', border: '1px solid #e2e8f0' };

const imageWrapper = { height: '220px', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '20px', background: '#fff' };
const productImage = { maxWidth: '100%', maxHeight: '100%', objectFit: 'contain' };

const cardBody = { padding: '20px', flexGrow: 1, display: 'flex', flexDirection: 'column' };
const nameColorRow = { display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '10px', gap: '10px' };
const productName = { fontSize: '1.05rem', fontWeight: '700', color: '#1e293b', flex: 1 };
const colorBadge = { display: 'inline-flex', alignItems: 'center', padding: '2px 8px', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '0.65rem', color: '#475569', fontWeight: '600', backgroundColor: '#f8fafc', whiteSpace: 'nowrap' };


const descriptionWrapper = { fontSize: '0.85rem', color: '#64748b', height: '60px', overflowY: 'auto', marginBottom: '15px', lineHeight: '1.4', paddingRight: '5px' };

const priceTag = { fontSize: '1.5rem', fontWeight: '900', color: '#2563eb', marginBottom: '15px' };
const actionArea = { marginTop: 'auto' };
const addButton = { width: '100%', backgroundColor: '#2563eb', color: '#fff', border: 'none', padding: '12px', borderRadius: '14px', fontWeight: '700', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '10px' };

const counterContainer = { display: 'flex', alignItems: 'center', justifyContent: 'space-between', backgroundColor: '#f8fafc', borderRadius: '18px', padding: '6px', border: '1px solid #e2e8f0' };
const minusBtn = { width: '40px', height: '40px', borderRadius: '14px', backgroundColor: '#fff', color: '#64748b', boxShadow: '0 2px 4px rgba(0,0,0,0.05)' };
const plusBtn = { width: '40px', height: '40px', borderRadius: '14px', backgroundColor: '#2563eb', color: '#fff', boxShadow: '0 4px 10px rgba(37, 99, 235, 0.2)' };
const quantityDisplay = { display: 'flex', flexDirection: 'column', alignItems: 'center', minWidth: '60px' };
const qtyNumber = { fontSize: '1.1rem', fontWeight: '800', color: '#0f172a' };
const qtyLabel = { fontSize: '0.6rem', color: '#94a3b8', fontWeight: '700' };

const paginationArea = { display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '20px', marginTop: '60px' };
const pageIndicator = { padding: '10px 20px', backgroundColor: '#fff', borderRadius: '12px', border: '1px solid #e2e8f0' };
const activeBtn = { width: '45px', height: '45px', borderRadius: '12px', backgroundColor: '#0f172a', color: '#fff', border: 'none', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' };
const disabledBtn = { ...activeBtn, backgroundColor: '#f1f5f9', color: '#cbd5e1', cursor: 'not-allowed' };
const loader = { textAlign: 'center', padding: '100px', color: '#64748b' };

export default Home;