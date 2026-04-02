import React, { useState, useEffect } from 'react';

function LiveTracking({ incidentId, responderName }) {
  const [isStreaming, setIsStreaming] = useState(true);
  const [videoQuality, setVideoQuality] = useState('high');
  const [trackingData, setTrackingData] = useState({
    latitude: 0,
    longitude: 0,
    speed: 0,
    altitude: 0,
    accuracy: 0
  });
  const [recordingTime, setRecordingTime] = useState(0);
  const [gpsStatus, setGpsStatus] = useState('Initializing GPS...');

  // Initialize real GPS tracking
  useEffect(() => {
    if (!isStreaming) return;

    if (!navigator.geolocation) {
      setGpsStatus('❌ Geolocation not supported');
      return;
    }

    setGpsStatus('🔍 Acquiring GPS signal...');

    // Request real-time location updates
    const id = navigator.geolocation.watchPosition(
      (position) => {
        const { latitude, longitude, accuracy, altitude } = position.coords;
        const speed = position.coords.speed || 0;

        setTrackingData({
          latitude,
          longitude,
          speed: speed * 3.6, // Convert m/s to km/h
          altitude: altitude || 0,
          accuracy: accuracy || 0
        });

        setGpsStatus('🟢 GPS Signal Strong');
      },
      (error) => {
        console.error('Geolocation error:', error);
        switch (error.code) {
          case error.PERMISSION_DENIED:
            setGpsStatus('❌ Location permission denied');
            break;
          case error.POSITION_UNAVAILABLE:
            setGpsStatus('❌ Location unavailable');
            break;
          case error.TIMEOUT:
            setGpsStatus('⏱️ GPS signal timeout');
            break;
          default:
            setGpsStatus('❌ GPS Error');
        }
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 3000
      }
    );

    return () => {
      navigator.geolocation.clearWatch(id);
    };
  }, [isStreaming]);

  // Update recording time
  useEffect(() => {
    if (!isStreaming) return;

    const timer = setInterval(() => {
      setRecordingTime(prev => prev + 1);
    }, 1000);

    return () => clearInterval(timer);
  }, [isStreaming]);

  const formatTime = (seconds) => {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;
    return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
  };

  const hasCoordinates = trackingData.latitude !== 0 || trackingData.longitude !== 0;
  const satelliteMapUrl = hasCoordinates
    ? `https://www.google.com/maps?q=${trackingData.latitude},${trackingData.longitude}&z=18&t=k&output=embed`
    : null;

  return (
    <div className="live-tracking-container">
      <div className="live-header">
        <div className="live-badge">🔴 LIVE</div>
        <h3>Live Incident Tracking & Video Feed</h3>
        <div className="recording-time">{formatTime(recordingTime)}</div>
      </div>

      <div className="live-content">
        {/* Video Stream Section */}
        <div className="video-stream-section">
          <div className="video-player">
            <div className="video-mockup">
              <div className="video-overlay">
                <div className="responder-info">
                  📹 {responderName || 'Responder'} • Camera Feed
                </div>
                <div className="video-quality">
                  Quality: {videoQuality.toUpperCase()}
                  <select value={videoQuality} onChange={(e) => setVideoQuality(e.target.value)}>
                    <option value="low">Low (360p)</option>
                    <option value="medium">Medium (720p)</option>
                    <option value="high">High (1080p)</option>
                  </select>
                </div>
                <div className="incident-view-indicator">
                  🎬 Incident Scene View
                </div>
              </div>
            </div>
          </div>

          <div className="stream-controls">
            <button 
              className={`control-btn ${isStreaming ? 'recording' : 'stopped'}`}
              onClick={() => setIsStreaming(!isStreaming)}
            >
              {isStreaming ? '⏹ Stop Stream' : '▶ Resume Stream'}
            </button>
            <button className="control-btn snapshot">📸 Take Snapshot</button>
            <button className="control-btn fullscreen">⛶ Fullscreen</button>
          </div>

          {!navigator.geolocation && (
            <div style={{
              background: 'rgba(231, 76, 60, 0.2)',
              border: '1px solid #e74c3c',
              padding: '1rem',
              borderRadius: '6px',
              color: '#e74c3c',
              marginTop: '1rem',
              fontSize: '0.9rem'
            }}>
              ⚠️ GPS/Geolocation not available on this browser or device.
            </div>
          )}
        </div>

        {/* Live Tracking Map Section */}
        <div className="tracking-section">
          <h4>🛰️ Live Satellite View</h4>
          <div className="map-mockup">
            <div className="map-container">
              {satelliteMapUrl ? (
                <>
                  <iframe
                    className="satellite-frame"
                    title="Live satellite view"
                    src={satelliteMapUrl}
                    allowFullScreen
                    loading="lazy"
                    referrerPolicy="no-referrer-when-downgrade"
                  />
                  <div className="satellite-overlay-label">
                    📍 Live GPS Position
                  </div>
                  <div className="distance-indicator">
                    {gpsStatus === '🟢 GPS Signal Strong' ? 'Real coordinates active' : 'Awaiting GPS lock'}
                  </div>
                </>
              ) : (
                <div className="satellite-placeholder">
                  <div className="satellite-placeholder-title">Waiting for real GPS signal</div>
                  <div className="satellite-placeholder-text">Allow location access to show the live satellite view.</div>
                </div>
              )}
            </div>
          </div>

          <div className="tracking-stats">
            <div className="stat">
              <label>GPS Status:</label>
              <span>{gpsStatus}</span>
            </div>
            <div className="stat">
              <label>Latitude:</label>
              <span>{trackingData.latitude.toFixed(6)}°</span>
            </div>
            <div className="stat">
              <label>Longitude:</label>
              <span>{trackingData.longitude.toFixed(6)}°</span>
            </div>
            <div className="stat">
              <label>Speed:</label>
              <span>{trackingData.speed.toFixed(1)} km/h</span>
            </div>
            <div className="stat">
              <label>Accuracy:</label>
              <span>±{trackingData.accuracy.toFixed(0)}m</span>
            </div>
            <div className="stat">
              <label>Altitude:</label>
              <span>{trackingData.altitude.toFixed(1)}m</span>
            </div>
          </div>
        </div>
      </div>

      {/* Connection Status */}
      <div className={`connection-status ${isStreaming ? 'connected' : 'disconnected'}`}>
        <div className="status-dot"></div>
        {isStreaming ? (
          <>
            <span>🟢 Live Stream Connected</span>
            <span className="signal-strength">Signal: ████████░░ Excellent</span>
          </>
        ) : (
          <span>⚫ Stream Paused</span>
        )}
      </div>
    </div>
  );
}

export default LiveTracking;
