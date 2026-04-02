import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { reportIncident } from '../services/api';

function IncidentReport({ user, onLogout }) {
  const [formData, setFormData] = useState({
    description: '',
    location: '',
    imageUrl: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    try {
      const response = await reportIncident(formData, user.userId);
      setSuccess('Incident reported successfully! AI analysis completed.');
      setTimeout(() => {
        navigate(`/incident/${response.data.incidentId}`);
      }, 2000);
    } catch (err) {
      setError('Failed to report incident. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <nav className="navbar">
        <h1>🛡️ AegisIQ - Report Incident</h1>
        <div className="navbar-right">
          <Link to="/dashboard" className="nav-link">Dashboard</Link>
          <button onClick={onLogout} className="btn-logout">Logout</button>
        </div>
      </nav>

      <div className="dashboard-container">
        <div style={{ maxWidth: '600px', margin: '2rem auto' }}>
          <div className="incident-card">
            <h2>Report New Incident</h2>
            <p style={{ color: '#777', marginBottom: '1.5rem' }}>
              Describe the incident in detail. Our AI system will analyze the credibility 
              and risk level automatically.
            </p>

            {error && <div className="error-message">{error}</div>}
            {success && <div className="success-message">{success}</div>}

            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Incident Description *</label>
                <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  required
                  placeholder="Describe the incident in detail. Include what happened, when, and any other relevant information..."
                  style={{ minHeight: '150px' }}
                />
              </div>

              <div className="form-group">
                <label>Location *</label>
                <input
                  type="text"
                  name="location"
                  value={formData.location}
                  onChange={handleChange}
                  required
                  placeholder="Enter the location of the incident (address, landmark, etc.)"
                />
              </div>

              <div className="form-group">
                <label>Image URL (Optional)</label>
                <input
                  type="url"
                  name="imageUrl"
                  value={formData.imageUrl}
                  onChange={handleChange}
                  placeholder="Paste URL of an image if available"
                />
              </div>

              <button 
                type="submit" 
                className="btn-primary"
                disabled={loading}
              >
                {loading ? 'Submitting & Analyzing...' : 'Report Incident'}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}

export default IncidentReport;
