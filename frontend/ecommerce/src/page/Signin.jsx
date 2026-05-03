import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import api from '../service/api';

const SignIn = () => {
    const [loginData, setLoginData] = useState({ username: '', password: '' });
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        
        if (location.state?.message) {
            setSuccessMessage(location.state.message);
        }
    }, [location]);

    const handleChange = (e) => {
        setLoginData({ ...loginData, [e.target.name]: e.target.value });
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');
        setSuccessMessage('');

        try {
            
            const response = await api.post('/user/signin', loginData);

            localStorage.setItem('username', loginData.username);
            
            window.dispatchEvent(new Event("storage"));
            navigate('/'); 
        } catch (err) {
            setError('Kullanıcı adı veya şifre hatalı!');
        }
    };

    return (
        <div style={styles.wrapper}>
            <div style={styles.container}>
                <h2 style={styles.title}>Hoş Geldiniz</h2>
                <p style={styles.subtitle}>Devam etmek için giriş yapın</p>

                {successMessage && <div style={styles.successBanner}>{successMessage}</div>}
                {error && <div style={styles.errorBanner}>{error}</div>}

                <form onSubmit={handleLogin} style={styles.form}>
                    <div style={styles.inputGroup}>
                        <label>Kullanıcı Adı</label>
                        <input
                            type="text"
                            name="username"
                            value={loginData.username}
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
                            value={loginData.password}
                            onChange={handleChange}
                            required
                            style={styles.input}
                        />
                    </div>
                    <button type="submit" style={styles.button}>Giriş Yap</button>
                </form>
                <p style={styles.footerText}>
                    Henüz hesabınız yok mu? <Link to="/signup" style={styles.link}>Kayıt Ol</Link>
                </p>
            </div>
        </div>
    );
};


export default SignIn;

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