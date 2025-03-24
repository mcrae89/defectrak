import React, { useState, lazy, Suspense } from 'react';
import { useNavigate } from 'react-router-dom';

// Lazy-loaded tab components
const UsersTab = lazy(() => import('./UsersTab'));
const RolesTab = lazy(() => import('./RolesTab'));
const PrioritiesTab = lazy(() => import('./PrioritiesTab'));
const StatusesTab = lazy(() => import('./StatusesTab'));

const AdminPage = () => {
  // Header options state
  const [showAccountOptions, setShowAccountOptions] = useState(false);
  const [showMenuOptions, setShowMenuOptions] = useState(false);
  const navigate = useNavigate();

  const handleMouseAccountEnter = () => setShowAccountOptions(true);
  const handleMouseAccountLeave = () => setShowAccountOptions(false);
  const handleMouseMenuEnter = () => setShowMenuOptions(true);
  const handleMouseMenuLeave = () => setShowMenuOptions(false);
  const handleAccount = () => navigate('/account');
  const handleLogout = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch('/api/logout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
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

  // Tabs state for main content
  const [activeTab, setActiveTab] = useState('users');

  return (
    <div className="admin">
      <header className="header bg-dark text-white">
        <div className="logo">Admin</div>
        <div
          className="header-icons"
          style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}
        >
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
                <div
                  onClick={handleAccount}
                  style={{ padding: '0.5rem', cursor: 'pointer' }}
                >
                  Account
                </div>
                <div
                  onClick={handleLogout}
                  style={{ padding: '0.5rem', cursor: 'pointer' }}
                >
                  Logout
                </div>
              </div>
            )}
          </div>
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
                <div onClick={() => navigate('/')} style={{ padding: '0.5rem', cursor: 'pointer' }}>
                  Dashboard
                </div>
              </div>
            )}
          </div>
        </div>
      </header>

      <main className="container mt-4">
        {/* Tab Navigation */}
        <div className="d-flex mb-3" style={{ gap: '1rem' }}>
          <button
            className={`btn ${activeTab === 'users' ? 'btn-primary' : 'btn-light'}`}
            onClick={() => setActiveTab('users')}
          >
            Users
          </button>
          <button
            className={`btn ${activeTab === 'roles' ? 'btn-primary' : 'btn-light'}`}
            onClick={() => setActiveTab('roles')}
          >
            Roles
          </button>
          <button
            className={`btn ${activeTab === 'priorities' ? 'btn-primary' : 'btn-light'}`}
            onClick={() => setActiveTab('priorities')}
          >
            Priorities
          </button>
          <button
            className={`btn ${activeTab === 'statuses' ? 'btn-primary' : 'btn-light'}`}
            onClick={() => setActiveTab('statuses')}
          >
            Statuses
          </button>
        </div>

        {/* Lazy-loaded content */}
        <Suspense fallback={<div>Loading tab...</div>}>
          {activeTab === 'users' && <UsersTab />}
          {activeTab === 'roles' && <RolesTab />}
          {activeTab === 'priorities' && <PrioritiesTab />}
          {activeTab === 'statuses' && <StatusesTab />}
        </Suspense>
      </main>
    </div>
  );
};

export default AdminPage;