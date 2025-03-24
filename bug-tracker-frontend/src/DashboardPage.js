import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const DashboardPage = ({ user }) => {
  const [showAccountOptions, setShowAccountOptions] = useState(false);
  const [showMenuOptions, setShowMenuOptions] = useState(false);
  const navigate = useNavigate();

  const handleMouseAccountEnter = () => {
    setShowAccountOptions(true);
  };

  const handleMouseAccountLeave = () => {
    setShowAccountOptions(false);
  };

  const handleMouseMenuEnter = () => {
    setShowMenuOptions(true);
  };

  const handleMouseMenuLeave = () => {
    setShowMenuOptions(false);
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
      <header className="header bg-dark text-white">
        <div className="logo">Dashboard</div>
        <div className="header-icons" style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <div
            style={{ position: 'relative', display: 'inline-block' }}
            onMouseEnter={handleMouseAccountEnter}
            onMouseLeave={handleMouseAccountLeave}
          >
            <i
              className="bi bi-person-circle"
              style={{ fontSize: '2rem', cursor: 'pointer' }}
            ></i>
            {showAccountOptions && (
              <div className="options">
                <div onClick={handleAccount} style={{ padding: '0.5rem', cursor: 'pointer' }}>
                  Account
                </div>
                <div onClick={handleLogout} style={{ padding: '0.5rem', cursor: 'pointer' }}>
                  Logout
                </div>
              </div>
            )}
          </div>
          {user.role.role === 'admin' && (
            <div
              style={{ position: 'relative', display: 'inline-block' }}
              onMouseEnter={handleMouseMenuEnter}
              onMouseLeave={handleMouseMenuLeave}
            >
              <i
                className="bi bi-list"
                style={{ fontSize: '2rem', cursor: 'pointer' }}
              ></i>
              {showMenuOptions && (
                <div className="options">
                  <div
                    onClick={() => navigate('/admin')}
                  >
                    Admin
                  </div>
                </div>
              )}
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