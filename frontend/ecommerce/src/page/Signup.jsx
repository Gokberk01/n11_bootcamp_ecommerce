import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../service/api';

const SignUp = () => {
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: ''
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            
            await api.post('/user/signup', formData);
            
            navigate('/signin', { 
                state: { message: 'Kayıt başarılı! Şimdi giriş yapabilirsiniz.' } 
            });
        } catch (err) {
            setError(err.response?.data?.message || 'Kayıt sırasında bir hata oluştu.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={styles.wrapper}>
            <div style={styles.container}>
                <h2 style={styles.title}>Yeni Hesap Oluştur</h2>
                <p style={styles.subtitle}>Bootcamp E-Ticaret dünyasına katılın</p>
                
                {error && <div style={styles.errorBanner}>{error}</div>}
                
                <form onSubmit={handleSubmit} style={styles.form}>
                    <div style={styles.inputGroup}>
                        <label>Kullanıcı Adı</label>
                        <input
                            type="text"
                            name="username"
                            value={formData.username}
                            onChange={handleChange}
                            required
                            style={styles.input}
                        />
                    </div>
                    <div style={styles.inputGroup}>
                        <label>E-posta</label>
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            required
                            style={styles.input}
                        />
                    </div>
                    <div style={styles.inputGroup}>
                        <label>Şifre</label>
                        <input
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            required
                            style={styles.input}
                        />
                    </div>
                    <button type="submit" disabled={loading} style={styles.button}>
                        {loading ? 'Kaydediliyor...' : 'Kayıt Ol'}
                    </button>
                </form>
                <p style={styles.footerText}>
                    Zaten üye misiniz? <Link to="/signin" style={styles.link}>Giriş Yap</Link>
                </p>
            </div>
        </div>
    );
};

export default SignUp;

const styles = {
    wrapper: { display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', backgroundColor: '#f0f2f5', padding: '20px' },
    container: { backgroundColor: '#fff', padding: '40px', borderRadius: '12px', boxShadow: '0 8px 24px rgba(0,0,0,0.1)', width: '100%', maxWidth: '400px', textAlign: 'center' },
    title: { margin: '0 0 10px 0', fontSize: '24px', color: '#1a1a1a' },
    subtitle: { color: '#666', marginBottom: '25px', fontSize: '14px' },
    form: { textAlign: 'left' },
    inputGroup: { marginBottom: '15px' },
    input: { width: '100%', padding: '12px', marginTop: '5px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '16px', boxSizing: 'border-box' },
    button: { width: '100%', padding: '14px', backgroundColor: '#2563eb', color: '#fff', border: 'none', borderRadius: '8px', fontSize: '16px', fontWeight: '600', cursor: 'pointer', marginTop: '10px' },
    errorBanner: { backgroundColor: '#fee2e2', color: '#dc2626', padding: '10px', borderRadius: '6px', marginBottom: '15px', fontSize: '14px' },
    successBanner: { backgroundColor: '#dcfce7', color: '#16a34a', padding: '10px', borderRadius: '6px', marginBottom: '15px', fontSize: '14px' },
    footerText: { marginTop: '20px', fontSize: '14px', color: '#666' },
    link: { color: '#2563eb', textDecoration: 'none', fontWeight: '500' }
};