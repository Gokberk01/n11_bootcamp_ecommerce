import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8763/api',
});


export default api;
