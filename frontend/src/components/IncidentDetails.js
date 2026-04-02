import React, { useState, useEffect } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import { getIncidentById, updateIncidentStatus } from '../services/api';
import LiveTracking from './LiveTracking';

function IncidentDetails({ user, onLogout }) {
  const { id } = useParams();
  const [incident, setIncident] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    loadIncident();
  }, [id]);

  const loadIncident = async () => {
    try {
      const response = await getIncidentById(id);
      setIncident(response.data);
    } catch (error) {
      console.error('Error loading incident:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleStatusUpdate = async (newStatus) => {
    try {
      await updateIncidentStatus(id, newStatus);
      setIncident({ ...incident, status: newStatus });
    } catch (error) {
      console.error('Error updating status:', error);
    }
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!incident) {
    return <div>Incident not found</div>;
  }

  const getRiskScoreClass = (score) => {
    if (score >= 70) return 'low';
    if (score >= 40) return 'medium';
    return 'high';
  };

  return (
    <div>
      <nav className="navbar">
        <h1>🛡️ AegisIQ - Incident Details</h1>
        <div className="navbar-right">
          <Link to="/dashboard" className="nav-link">Dashboard</Link>
          <button onClick={onLogout} className="btn-logout">Logout</button>
        </div>
      </nav>

      <div className="incident-details-container">
        <div className="incident-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
            <h2>Incident #{incident.incidentId}</h2>
            <span className={`priority-badge ${incident.priorityClassification}`}>
              {incident.priorityClassification || 'PENDING'}
            </span>
          </div>

          <div style={{ marginBottom: '2rem' }}>
            <p><strong>Status:</strong> {incident.status}</p>
            <p><strong>Location:</strong> {incident.location}</p>
            <p><strong>Reported by:</strong> {incident.userName}</p>
            <p><strong>Date:</strong> {new Date(incident.timestamp).toLocaleString()}</p>
          </div>

          <h3>Incident Description</h3>
          <p style={{ padding: '1rem', background: '#f8f9fa', borderRadius: '5px', marginBottom: '2rem' }}>
            {incident.description}
          </p>

          {incident.imageUrl && (
            <div style={{ marginBottom: '2rem' }}>
              <h3>Evidence</h3>
              <img 
                src={incident.imageUrl} 
                alt="Incident evidence" 
                style={{ maxWidth: '100%', borderRadius: '5px' }}
              />
            </div>
          )}
        </div>

        {/* Live Tracking & Video Feed - For Active Incidents */}
        {(incident.status === 'UNDER_REVIEW' || incident.status === 'PENDING') && (
          <div className="incident-card">
            <LiveTracking incidentId={incident.incidentId} responderName="Officer John" />
          </div>
        )}

        {incident.credibilityScore && (
          <div className="incident-card">
            <h2>AI Risk Analysis</h2>
            
            <div style={{ textAlign: 'center', margin: '2rem 0' }}>
              <h3>Credibility Score</h3>
              <div className={`risk-score ${getRiskScoreClass(incident.credibilityScore)}`}>
                {incident.credibilityScore.toFixed(0)}%
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1.5rem' }}>
              <div>
                <strong>Severity Level:</strong>
                <p style={{ fontSize: '1.5rem', margin: '0.5rem 0' }}>
                  {incident.severityLevel}
                </p>
              </div>
              <div>
                <strong>Risk Level:</strong>
                <p style={{ fontSize: '1.5rem', margin: '0.5rem 0' }}>
                  {incident.priorityClassification}
                </p>
              </div>
            </div>

            {incident.recommendedAction && (
              <div className="recommended-action">
                <strong>Recommended Action:</strong>
                <p style={{ marginTop: '0.5rem' }}>{incident.recommendedAction}</p>
              </div>
            )}
          </div>
        )}

        {(user.role === 'ADMIN' || user.role === 'RESPONDER') && (
          <div className="incident-card">
            <h3>Update Status</h3>
            <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
              <button 
                className="btn btn-secondary"
                onClick={() => handleStatusUpdate('PENDING')}
                disabled={incident.status === 'PENDING'}
              >
                Mark as Pending
              </button>
              <button 
                className="btn btn-secondary"
                onClick={() => handleStatusUpdate('UNDER_REVIEW')}
                disabled={incident.status === 'UNDER_REVIEW'}
              >
                Mark as Under Review
              </button>
              <button 
                className="btn btn-success"
                onClick={() => handleStatusUpdate('RESOLVED')}
                disabled={incident.status === 'RESOLVED'}
              >
                Mark as Resolved
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default IncidentDetails;
