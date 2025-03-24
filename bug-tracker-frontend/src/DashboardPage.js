import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const DashboardPage = () => {
  const [showOptions, setShowOptions] = useState(false);
  const navigate = useNavigate();

  const handleMouseEnter = () => {
    setShowOptions(true);
  };

  const handleMouseLeave = () => {
    setShowOptions(false);
  };

  const handleAccount = () => {
    navigate('/account');
  };

  const handleLogout = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch('/api/logout', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        credentials: 'include'
      });

      if (response.ok) {
        console.log('Logout successful');
        window.location.reload();
      } else {
        const errorData = await response.json();
        console.log(errorData);
      }
    } catch (error) {
      console.error('Error during logout:', error);
    }
  };

  return (
    <div className="dashboard">
      <header className="header bg-dark text-white p-3 d-flex justify-content-between align-items-center">
        <h1>Dashboard</h1>
        <div
          style={{ position: 'relative', display: 'inline-block' }}
          onMouseEnter={handleMouseEnter}
          onMouseLeave={handleMouseLeave}
        >
          <i
            className="bi bi-person-circle"
            style={{ fontSize: '2rem', cursor: 'pointer' }}
          ></i>
          {showOptions && (
            <div
              style={{
                position: 'absolute',
                top: '100%',
                right: 0,
                backgroundColor: '#fff',
                color: '#000',
                border: '1px solid #ccc',
                borderRadius: '4px',
                boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
                zIndex: 1000,
                padding: '0.5rem',
                minWidth: '120px'
              }}
            >
              <div
                style={{ padding: '0.5rem', cursor: 'pointer' }}
                onClick={handleAccount}
              >
                Account
              </div>
              <div
                style={{ padding: '0.5rem', cursor: 'pointer' }}
                onClick={handleLogout}
              >
                Logout
              </div>
            </div>
          )}
        </div>
      </header>

      <main className="container mt-4">
        <p>Welcome to your dashboard!</p>
      </main>
    </div>
  );
};

export default DashboardPage;
