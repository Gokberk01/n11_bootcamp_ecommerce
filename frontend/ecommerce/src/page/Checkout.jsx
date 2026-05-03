import React, { useState } from 'react';
import api from '../service/api';
import { useNavigate } from 'react-router-dom';
import { 
    CreditCard, MapPin, User, ChevronRight, 
    CheckCircle2, ShoppingBag, ShieldCheck, ArrowLeft 
} from 'lucide-react';

const Checkout = () => {
    const navigate = useNavigate();
    const cartId = localStorage.getItem('activeCartId');
    const [step, setStep] = useState(1); 
    
    
    const cartQuantities = JSON.parse(localStorage.getItem('cartQuantities') || '{}');
    
    const [formData, setFormData] = useState({
        username: localStorage.getItem('username') || "guest",
        firstName: "", lastName: "", email: "", phone: "",
        streetAddress: "", city: "", country: "Türkiye",
        paymentMethod: "IYZICO",
        card: {
            cardHolderName: "",
            cardNumber: "",
            expireMonth: "",
            expireYear: "",
            cvc: ""
        }
    });

    const handleInputChange = (e, section = null) => {
        const { name, value } = e.target;
        if (section) {
            setFormData(prev => ({
                ...prev,
                [section]: { ...prev[section], [name]: value }
            }));
        } else {
            setFormData(prev => ({ ...prev, [name]: value }));
        }
    };

    const checkOrderStatus = async (orderId) => {
    const startTime = Date.now();
    const timeout = 60000; // 1 dakika (ms)
    const interval = 5000; // 5 saniye 

    return new Promise((resolve, reject) => {
        const timer = setInterval(async () => {
            try {
                const response = await api.get(`/orders/${orderId}`);
                const currentStatus = response.data.status.toUpperCase();

                console.log(`Sipariş Kontrol Ediliyor (${orderId}): ${currentStatus}`);


                if (currentStatus !== 'CREATED') {
                    clearInterval(timer);
                    resolve(response.data);
                }

                
                if (Date.now() - startTime > timeout) {
                    clearInterval(timer);
                    reject(new Error("Sipariş zaman aşımına uğradı (Status hala CREATED)."));
                }
            } catch (err) {
                clearInterval(timer);
                reject(err);
            }
        }, interval);
    });
};

const handleSubmitOrder = async () => {
        try {
            
            const cartRes = await api.get(`/shopping-cart/${cartId}`);
            const productsInCart = cartRes.data.products || []; 

            
            const orderItems = productsInCart.map(product => {
                
                const localQty = cartQuantities[product.id] || 0;
                
                return {
                    productId: product.id,
                    productName: product.title, 
                    price: product.price,       
                    quantity: localQty          
                };
            }).filter(item => item.quantity > 0); 

            const currentUsername = localStorage.getItem('username') || "guest";
            
            const finalOrderRequest = {
                ...formData,
                username: currentUsername,
                items: orderItems
            };

            
            const response = await api.post('/orders', finalOrderRequest);
            
            const orderData = response.data;

            const orderId = orderData.orderId; 

                    if (orderData && orderData.status === 'CREATED') {
                        
                        console.log("Sipariş oluşturuldu, asenkron süreç bekleniyor...");
                        
                        try {
                            
                            const finalOrder = await checkOrderStatus(orderId);
                            const finalStatus = finalOrder.status.toUpperCase();

                            if (['PAID', 'COMPLETED', 'STOCK_DEDUCTED'].includes(finalStatus)) {
                                
                                await api.delete(`/shopping-cart/${cartId}`);
                                localStorage.removeItem('cartQuantities');
                                localStorage.removeItem('activeCartId');
                                setStep(3); 
                            } else if (finalStatus === 'CANCELLED') {
                                alert("Ödeme veya stok hatası nedeniyle sipariş iptal edildi.");
                            }
                        } catch (pollErr) {
                            console.error("Polling hatası veya zaman aşımı:", pollErr);
                            
                            setStep(3); 
                        }
                    }
        } catch (err) {
            console.error("Sipariş oluşturma hatası:", err);
            alert("Siparişiniz işlenirken bir sorun oluştu. Lütfen bilgilerinizi ve sepetinizi kontrol edin.");
        }
    };

    return (
        <div style={pageWrapper}>
            <div style={container}>
                {/* Stepper (Adımlar) */}
                <div style={stepperContainer}>
                    <div style={step === 1 ? activeStep : completedStep}>
                        <User size={20} /> <span>Bilgiler</span>
                    </div>
                    <div style={divider}></div>
                    <div style={step === 2 ? activeStep : (step > 2 ? completedStep : inactiveStep)}>
                        <CreditCard size={20} /> <span>Ödeme</span>
                    </div>
                    <div style={divider}></div>
                    <div style={step === 3 ? activeStep : inactiveStep}>
                        <CheckCircle2 size={20} /> <span>Onay</span>
                    </div>
                </div>

                {step === 1 && (
                    <div style={card}>
                        <h2 style={sectionTitle}><MapPin size={24} /> Teslimat ve İletişim</h2>
                        <div style={grid}>
                            <input type="text" placeholder="Ad" name="firstName" value={formData.firstName} onChange={handleInputChange} style={input} />
                            <input type="text" placeholder="Soyad" name="lastName" value={formData.lastName} onChange={handleInputChange} style={input} />
                            <input type="email" placeholder="E-posta" name="email" value={formData.email} onChange={handleInputChange} style={input} />
                            <input type="text" placeholder="Telefon" name="phone" value={formData.phone} onChange={handleInputChange} style={input} />
                            <div style={{gridColumn: 'span 2'}}>
                                <input type="text" placeholder="Adres" name="streetAddress" value={formData.streetAddress} onChange={handleInputChange} style={input} />
                            </div>
                            <input type="text" placeholder="Şehir" name="city" value={formData.city} onChange={handleInputChange} style={input} />
                            <input type="text" placeholder="Ülke" name="country" value={formData.country} onChange={handleInputChange} style={input} />
                        </div>
                        <button onClick={() => setStep(2)} style={primaryBtn}>
                            Ödeme Bilgilerine Geç <ChevronRight size={20} />
                        </button>
                    </div>
                )}

                {step === 2 && (
                    <div style={card}>
                        <h2 style={sectionTitle}><CreditCard size={24} /> Kart Bilgileri</h2>
                        <div style={cardForm}>
                            <input type="text" placeholder="Kart Üzerindeki İsim" name="cardHolderName" value={formData.card.cardHolderName} onChange={(e) => handleInputChange(e, 'card')} style={input} />
                            <input type="text" placeholder="Kart Numarası" name="cardNumber" value={formData.card.cardNumber} onChange={(e) => handleInputChange(e, 'card')} style={input} />
                            <div style={grid}>
                                <input type="text" placeholder="Ay (MM)" name="expireMonth" value={formData.card.expireMonth} onChange={(e) => handleInputChange(e, 'card')} style={input} />
                                <input type="text" placeholder="Yıl (YYYY)" name="expireYear" value={formData.card.expireYear} onChange={(e) => handleInputChange(e, 'card')} style={input} />
                            </div>
                            <input type="password" placeholder="CVC" name="cvc" value={formData.card.cvc} onChange={(e) => handleInputChange(e, 'card')} style={input} />
                        </div>
                        
                        <div style={secureNote}>
                            <ShieldCheck size={18} color="#10b981" /> 
                            <span>Güvenli 256-bit SSL ödeme altyapısı kullanılmaktadır.</span>
                        </div>

                        <div style={buttonGroup}>
                            <button onClick={() => setStep(1)} style={secondaryBtn}><ArrowLeft size={18}/> Geri</button>
                            <button onClick={handleSubmitOrder} style={orderBtn}>Siparişi Tamamla</button>
                        </div>
                    </div>
                )}

                {step === 3 && (
                    <div style={successCard}>
                        <div style={successIcon}><CheckCircle2 size={80} color="#10b981" /></div>
                        <h1 style={title}>Siparişiniz Alındı!</h1>
                        <p style={subtitle}>Siparişiniz başarıyla oluşturuldu ve hazırlık sürecine başlandı.</p>
                        <button onClick={() => navigate('/')} style={primaryBtn}>
                            <ShoppingBag size={20} /> Alışverişe Devam Et
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
};


const pageWrapper = { backgroundColor: '#f8fafc', minHeight: '100vh', padding: '60px 20px' };
const container = { maxWidth: '800px', margin: '0 auto' };

const stepperContainer = { display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '20px', marginBottom: '40px' };
const baseStep = { display: 'flex', alignItems: 'center', gap: '8px', padding: '10px 20px', borderRadius: '12px', fontWeight: '700', fontSize: '0.9rem' };
const activeStep = { ...baseStep, backgroundColor: '#2563eb', color: '#fff' };
const completedStep = { ...baseStep, backgroundColor: '#dcfce7', color: '#166534' };
const inactiveStep = { ...baseStep, backgroundColor: '#fff', color: '#94a3b8', border: '1px solid #e2e8f0' };
const divider = { flex: 1, height: '2px', backgroundColor: '#e2e8f0', maxWidth: '50px' };

const card = { backgroundColor: '#fff', borderRadius: '24px', padding: '40px', boxShadow: '0 10px 25px -5px rgba(0,0,0,0.05)', border: '1px solid #f1f5f9' };
const sectionTitle = { display: 'flex', alignItems: 'center', gap: '12px', fontSize: '1.5rem', fontWeight: '800', color: '#0f172a', marginBottom: '30px' };
const grid = { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' };
const input = { width: '100%', padding: '15px', borderRadius: '12px', border: '1px solid #e2e8f0', fontSize: '1rem', outline: 'none', transition: 'border-color 0.2s', backgroundColor: '#fdfdfd', color: '#0f172a' };

const cardForm = { display: 'flex', flexDirection: 'column', gap: '20px', maxWidth: '500px', margin: '0 auto' };

const buttonGroup = { display: 'flex', gap: '15px', marginTop: '40px' };
const primaryBtn = { width: '100%', backgroundColor: '#2563eb', color: '#fff', border: 'none', padding: '18px', borderRadius: '16px', fontWeight: '700', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '10px', fontSize: '1.1rem' };
const secondaryBtn = { backgroundColor: '#f1f5f9', color: '#475569', border: 'none', padding: '18px 30px', borderRadius: '16px', fontWeight: '700', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px' };
const orderBtn = { ...primaryBtn, backgroundColor: '#0f172a' };

const secureNote = { display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px', marginTop: '25px', color: '#64748b', fontSize: '0.85rem' };

const successCard = { textAlign: 'center', backgroundColor: '#fff', padding: '60px', borderRadius: '32px', boxShadow: '0 20px 40px rgba(0,0,0,0.05)' };
const successIcon = { marginBottom: '30px' };
const title = { fontSize: '2.5rem', fontWeight: '900', color: '#0f172a', marginBottom: '10px' };
const subtitle = { color: '#64748b', fontSize: '1.1rem', marginBottom: '40px' };

export default Checkout;