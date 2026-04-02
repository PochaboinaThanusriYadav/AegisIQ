import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Auth APIs
export const login = (email, password) => {
  return api.post('/auth/login', { email, password });
};

export const register = (userData) => {
  return api.post('/auth/register', userData);
};

// Incident APIs
export const reportIncident = (incidentData, userId) => {
  return api.post(`/incidents/report?userId=${userId}`, incidentData);
};

export const getAllIncidents = () => {
  return api.get('/incidents');
};

export const getIncidentById = (id) => {
  return api.get(`/incidents/${id}`);
};

export const getIncidentsByUser = (userId) => {
  return api.get(`/incidents/user/${userId}`);
};

export const updateIncidentStatus = (id, status) => {
  return api.put(`/incidents/${id}/status?status=${status}`);
};

// Dashboard APIs
export const getDashboardStats = () => {
  return api.get('/dashboard/stats');
};

export const getRecentIncidents = (limit = 10) => {
  return api.get(`/dashboard/recent-incidents?limit=${limit}`);
};

// Emergency APIs
export const getEmergencyContacts = () => {
  return api.get('/emergency/contacts');
};

export const logEmergencyCall = (callData) => {
  return api.post('/emergency/call', callData);
};

// User APIs
export const getAllUsers = () => {
  return api.get('/users');
};

export const getUserById = (id) => {
  return api.get(`/users/${id}`);
};

export default api;
