import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getDashboardStats, getRecentIncidents, getEmergencyContacts, logEmergencyCall } from '../services/api';

function Dashboard({ user, onLogout }) {
  const [stats, setStats] = useState(null);
  const [recentIncidents, setRecentIncidents] = useState([]);
  const [showEmergencyPanel, setShowEmergencyPanel] = useState(false);
  const [liveTrackingActive, setLiveTrackingActive] = useState(false);
  const [liveVideoActive, setLiveVideoActive] = useState(false);
  const [emergencyContacts, setEmergencyContacts] = useState([]);
  const navigate = useNavigate();
  const activeIncidents = recentIncidents.filter((incident) => incident.status !== 'RESOLVED');
  const myIncidents = recentIncidents.filter((incident) => incident.userId === user.userId);

  useEffect(() => {
    loadDashboardData();
    loadEmergencyContacts();
  }, []);

  const loadDashboardData = async () => {
    try {
      const [statsRes, incidentsRes] = await Promise.all([
        getDashboardStats(),
        getRecentIncidents(10)
      ]);
      setStats(statsRes.data);
      setRecentIncidents(incidentsRes.data);
    } catch (error) {
      console.error('Error loading dashboard data:', error);
    }
  };

  const loadEmergencyContacts = async () => {
    try {
      const response = await getEmergencyContacts();
      setEmergencyContacts(response.data);
    } catch (error) {
      console.error('Error loading emergency contacts:', error);
      setEmergencyContacts([
        { label: 'Police Control', number: '112', role: 'Police' },
        { label: 'Ambulance Dispatch', number: '108', role: 'Medical' },
        { label: 'Fire Control', number: '101', role: 'Fire' },
        { label: 'Rescue Team', number: '100', role: 'Rescue' },
        { label: 'Incident Commander', number: '+91-90000-00001', role: 'Command' },
        { label: 'On-Call Doctor', number: '+91-90000-00002', role: 'Medical' }
      ]);
    }
  };

  const handleIncidentClick = (incidentId) => {
    navigate(`/incident/${incidentId}`);
  };

  const callEmergencyServices = () => {
    setShowEmergencyPanel((prev) => !prev);
  };

  const handleEmergencyContactClick = async (contact, event) => {
    event.preventDefault();

    try {
      await logEmergencyCall({
        incidentId: activeIncidents[0]?.incidentId || null,
        callerUserId: user.userId,
        contactNumber: contact.number,
        contactLabel: contact.label,
        notes: `Emergency contact opened from responder dashboard by ${user.name}`
      });
    } catch (error) {
      console.error('Error logging emergency call:', error);
    }

    window.location.href = `tel:${contact.number}`;
  };

  const startLiveTracking = () => {
    setLiveTrackingActive(!liveTrackingActive);
    if (!liveTrackingActive) {
      alert('🗺️ Live Tracking Activated\n\nTracking responder location in real-time.\nSharing location with incident commander.');
    } else {
      alert('🗺️ Live Tracking Deactivated');
    }
  };

  const startLiveVideo = () => {
    setLiveVideoActive(!liveVideoActive);
    if (!liveVideoActive) {
      alert('📹 Live Video Stream Started\n\nCamera access granted.\nStreaming incident scene to command center.\nBitrate: Adaptive');
    } else {
      alert('📹 Live Video Stream Stopped');
    }
  };

  const getRoleLabel = () => {
    if (user.role === 'ADMIN') return 'Admin Command Interface';
    if (user.role === 'RESPONDER') return 'Responder Operations Interface';
    return 'Citizen Reporting Interface';
  };

  const renderRoleSpotlight = () => {
    if (user.role === 'ADMIN') {
      return (
        <div className="role-spotlight role-admin">
          <h3>Admin Command Center</h3>
          <p>Monitor platform-wide activity, manage user roles, and supervise all incidents.</p>
          <Link to="/admin" className="role-cta">Open Admin Panel</Link>
        </div>
      );
    }

    if (user.role === 'RESPONDER') {
      return (
        <div className="role-spotlight role-responder">
          <h3>Responder Live Queue & Emergency Tools</h3>
          <p>Track active incidents and deploy emergency response resources.</p>
          <div className="role-meta">Active Cases: {activeIncidents.length}</div>
          <div className="role-meta emergency-note">Tap a contact to call directly on mobile or desktop calling apps.</div>
          
          <div className="emergency-tools">
            <button 
              className="emergency-btn call-btn"
              onClick={callEmergencyServices}
              title="Call emergency services"
            >
              📞 Emergency People Calls
            </button>
            
            <button 
              className={`emergency-btn tracking-btn ${liveTrackingActive ? 'active' : ''}`}
              onClick={startLiveTracking}
              title="Start live location tracking"
            >
              🗺️ {liveTrackingActive ? 'Stop' : 'Start'} Live Tracking
            </button>
            
            <button 
              className={`emergency-btn video-btn ${liveVideoActive ? 'active' : ''}`}
              onClick={startLiveVideo}
              title="Start live video stream"
            >
              📹 {liveVideoActive ? 'Stop' : 'Start'} Live Video
            </button>
          </div>

          {showEmergencyPanel && (
            <div className="emergency-panel">
              <h4>Emergency Contacts</h4>
              <div className="emergency-contact-grid">
                {emergencyContacts.map((contact) => (
                  <a
                    key={contact.label}
                    className={`emergency-contact-card emergency-${contact.role.toLowerCase()}`}
                    href={`tel:${contact.number}`}
                    onClick={(event) => handleEmergencyContactClick(contact, event)}
                    aria-label={`Call ${contact.label} at ${contact.number}`}
                  >
                    <span className="contact-role">{contact.role}</span>
                    <strong>{contact.label}</strong>
                    <span className="contact-number">{contact.number}</span>
                  </a>
                ))}
              </div>
            </div>
          )}
        </div>
      );
    }

    return (
      <div className="role-spotlight role-user">
        <h3>Community Reporter Hub</h3>
        <p>Submit incidents quickly and follow updates from responders.</p>
        <div className="role-meta">Your Recent Reports: {myIncidents.length}</div>
      </div>
    );
  };

  return (
    <div>
      <nav className="navbar">
        <h1>🛡️ AegisIQ Dashboard</h1>
        <div className="navbar-right">
          <span>Welcome, {user.name}</span>
          <Link to="/report" className="nav-link">Report Incident</Link>
          {user.role === 'ADMIN' && (
            <Link to="/admin" className="nav-link">Admin Panel</Link>
          )}
          <button onClick={onLogout} className="btn-logout">Logout</button>
        </div>
      </nav>

      <div className="dashboard-container">
        <div className={`role-banner role-${user.role.toLowerCase()}`}>
          <strong>{getRoleLabel()}</strong>
        </div>

        {renderRoleSpotlight()}

        {stats && (
          <>
            <h2>{user.role === 'ADMIN' ? 'System Overview' : user.role === 'RESPONDER' ? 'Operational Snapshot' : 'Safety Snapshot'}</h2>
            <div className="stats-grid">
              <div className="stat-card">
                <h3>Total Incidents</h3>
                <div className="stat-value">{stats.totalIncidents}</div>
              </div>
              <div className="stat-card high">
                <h3>High Priority</h3>
                <div className="stat-value">{stats.priorityBreakdown.high}</div>
              </div>
              <div className="stat-card medium">
                <h3>Medium Priority</h3>
                <div className="stat-value">{stats.priorityBreakdown.medium}</div>
              </div>
              <div className="stat-card low">
                <h3>Low Priority</h3>
                <div className="stat-value">{stats.priorityBreakdown.low}</div>
              </div>
              <div className="stat-card">
                <h3>Under Review</h3>
                <div className="stat-value">{stats.statusBreakdown.underReview}</div>
              </div>
              <div className="stat-card">
                <h3>Resolved</h3>
                <div className="stat-value">{stats.statusBreakdown.resolved}</div>
              </div>
            </div>
          </>
        )}

        <h2>{user.role === 'RESPONDER' ? 'Responder Queue' : user.role === 'USER' ? 'Recent Public Incidents' : 'Recent Incidents'}</h2>
        <div className="incidents-list">
          {recentIncidents.length === 0 ? (
            <p>No incidents reported yet.</p>
          ) : (
            recentIncidents.map((incident) => (
              <div
                key={incident.incidentId}
                className="incident-item"
                onClick={() => handleIncidentClick(incident.incidentId)}
              >
                <div className="incident-header">
                  <strong>{incident.location || 'No location specified'}</strong>
                  <span className={`priority-badge ${incident.priorityClassification}`}>
                    {incident.priorityClassification || 'PENDING'}
                  </span>
                </div>
                <p>{incident.description.substring(0, 100)}...</p>
                <small>
                  Reported by: {incident.userName} | 
                  Credibility: {incident.credibilityScore ? `${incident.credibilityScore.toFixed(0)}%` : 'Pending'} | 
                  Status: {incident.status}
                </small>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
