import React, { useState, lazy, Suspense } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Navbar,
  Nav,
  Dropdown
} from 'react-bootstrap';
import HoverDropdown from './components/HoverDropdown';

// Lazy-loaded tab components
const UsersTab = lazy(() => import('./UsersTab'));
const RolesTab = lazy(() => import('./RolesTab'));
const PrioritiesTab = lazy(() => import('./PrioritiesTab'));
const StatusesTab = lazy(() => import('./StatusesTab'));

const AdminPage = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('users');

  const handleAccount = () => {
    navigate('/account');
  };

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

  // Helper to get a user-friendly label for the current tab
  const getTabLabel = (tab) => {
    switch (tab) {
      case 'users':
        return 'Users';
      case 'roles':
        return 'Roles';
      case 'priorities':
        return 'Priorities';
      case 'statuses':
        return 'Statuses';
      default:
        return 'Select Tab';
    }
  };

  return (
    <>
      {/* Header */}
      <Navbar bg="dark" variant="dark" expand="lg">
        <Container>
          <Navbar.Brand>Admin</Navbar.Brand>
          <Nav className="ms-auto">
            <HoverDropdown
              toggleContent={
                <i className="bi bi-person-circle" style={{ fontSize: '1.5rem' }}></i>
              }
            >
              <Dropdown.Item onClick={handleAccount}>Account</Dropdown.Item>
              <Dropdown.Item onClick={handleLogout}>Logout</Dropdown.Item>
            </HoverDropdown>
            <HoverDropdown
              toggleContent={
                <i className="bi bi-list" style={{ fontSize: '1.5rem' }}></i>
              }
            >
              <Dropdown.Item onClick={() => navigate('/')}>
                Dashboard
              </Dropdown.Item>
            </HoverDropdown>
          </Nav>
        </Container>
      </Navbar>

      {/* Main Content */}
      <Container className="mt-4">
        {/* Single Dropdown for Tabs */}
        <div className="mb-3">
          <Dropdown>
            <Dropdown.Toggle variant="primary">
              {getTabLabel(activeTab)}
            </Dropdown.Toggle>
            <Dropdown.Menu>
              <Dropdown.Item onClick={() => setActiveTab('users')}>Users</Dropdown.Item>
              <Dropdown.Item onClick={() => setActiveTab('roles')}>Roles</Dropdown.Item>
              <Dropdown.Item onClick={() => setActiveTab('priorities')}>Priorities</Dropdown.Item>
              <Dropdown.Item onClick={() => setActiveTab('statuses')}>Statuses</Dropdown.Item>
            </Dropdown.Menu>
          </Dropdown>
        </div>

        {/* Lazy-loaded Tab Content */}
        <Suspense fallback={<div>Loading tab...</div>}>
          {activeTab === 'users' && <UsersTab />}
          {activeTab === 'roles' && <RolesTab />}
          {activeTab === 'priorities' && <PrioritiesTab />}
          {activeTab === 'statuses' && <StatusesTab />}
        </Suspense>
      </Container>
    </>
  );
};

export default AdminPage;