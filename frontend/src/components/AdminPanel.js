import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getAllUsers, getAllIncidents } from '../services/api';

function AdminPanel({ user, onLogout }) {
  const [users, setUsers] = useState([]);
  const [incidents, setIncidents] = useState([]);
  const [activeTab, setActiveTab] = useState('users');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [usersRes, incidentsRes] = await Promise.all([
        getAllUsers(),
        getAllIncidents()
      ]);
      setUsers(usersRes.data);
      setIncidents(incidentsRes.data);
    } catch (error) {
      console.error('Error loading admin data:', error);
    }
  };

  return (
    <div>
      <nav className="navbar">
        <h1>🛡️ AegisIQ - Admin Control Panel</h1>
        <div className="navbar-right">
          <Link to="/dashboard" className="nav-link">Dashboard</Link>
          <Link to="/report" className="nav-link">Report Incident</Link>
          <button onClick={onLogout} className="btn-logout">Logout</button>
        </div>
      </nav>

      <div className="dashboard-container">
        <div style={{ marginBottom: '2rem' }}>
          <button 
            className={`btn ${activeTab === 'users' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setActiveTab('users')}
            style={{ marginRight: '1rem' }}
          >
            Users ({users.length})
          </button>
          <button 
            className={`btn ${activeTab === 'incidents' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setActiveTab('incidents')}
          >
            All Incidents ({incidents.length})
          </button>
        </div>

        {activeTab === 'users' && (
          <div className="incident-card">
            <h2>System Users</h2>
            <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '1rem' }}>
              <thead>
                <tr style={{ borderBottom: '2px solid #ddd' }}>
                  <th style={{ padding: '0.75rem', textAlign: 'left' }}>ID</th>
                  <th style={{ padding: '0.75rem', textAlign: 'left' }}>Name</th>
                  <th style={{ padding: '0.75rem', textAlign: 'left' }}>Email</th>
                  <th style={{ padding: '0.75rem', textAlign: 'left' }}>Phone</th>
                  <th style={{ padding: '0.75rem', textAlign: 'left' }}>Role</th>
                </tr>
              </thead>
              <tbody>
                {users.map((u) => (
                  <tr key={u.userId} style={{ borderBottom: '1px solid #eee' }}>
                    <td style={{ padding: '0.75rem' }}>{u.userId}</td>
                    <td style={{ padding: '0.75rem' }}>{u.name}</td>
                    <td style={{ padding: '0.75rem' }}>{u.email}</td>
                    <td style={{ padding: '0.75rem' }}>{u.phone || 'N/A'}</td>
                    <td style={{ padding: '0.75rem' }}>
                      <span className={`priority-badge ${u.role === 'ADMIN' ? 'HIGH' : u.role === 'RESPONDER' ? 'MEDIUM' : 'LOW'}`}>
                        {u.role}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {activeTab === 'incidents' && (
          <div className="incident-card">
            <h2>All Incidents</h2>
            <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '1rem' }}>
              <thead>
                <tr style={{ borderBottom: '2px solid #ddd' }}>
                  <th style={{ padding: '0.75rem', textAlign: 'left' }}>ID</th>
                  <th style={{ padding: '0.75rem', textAlign: 'left' }}>Location</th>
                  <th style={{ padding: '0.75rem', textAlign: 'left' }}>Status</th>
                  <th style={{ padding: '0.75rem', textAlign: 'left' }}>Priority</th>
                  <th style={{ padding: '0.75rem', textAlign: 'left' }}>Credibility</th>
                  <th style={{ padding: '0.75rem', textAlign: 'left' }}>Date</th>
                </tr>
              </thead>
              <tbody>
                {incidents.map((inc) => (
                  <tr key={inc.incidentId} style={{ borderBottom: '1px solid #eee' }}>
                    <td style={{ padding: '0.75rem' }}>
                      <Link to={`/incident/${inc.incidentId}`} style={{ color: '#3498db' }}>
                        {inc.incidentId}
                      </Link>
                    </td>
                    <td style={{ padding: '0.75rem' }}>{inc.location}</td>
                    <td style={{ padding: '0.75rem' }}>{inc.status}</td>
                    <td style={{ padding: '0.75rem' }}>
                      <span className={`priority-badge ${inc.priorityClassification || 'LOW'}`}>
                        {inc.priorityClassification || 'PENDING'}
                      </span>
                    </td>
                    <td style={{ padding: '0.75rem' }}>
                      {inc.credibilityScore ? `${inc.credibilityScore.toFixed(0)}%` : 'N/A'}
                    </td>
                    <td style={{ padding: '0.75rem' }}>
                      {new Date(inc.timestamp).toLocaleDateString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

export default AdminPanel;
