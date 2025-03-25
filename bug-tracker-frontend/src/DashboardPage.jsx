import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Navbar, Container, Nav, Dropdown } from 'react-bootstrap';
import HoverDropdown from './components/HoverDropdown';

const DashboardPage = ({ user }) => {
  const navigate = useNavigate();

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

  return (
    <>
      {/* Header */}
      <Navbar bg="dark" variant="dark" expand="lg">
        <Container>
          <Navbar.Brand>Dashboard</Navbar.Brand>
          <Nav className="ms-auto">
            <HoverDropdown 
              toggleContent={<i className="bi bi-person-circle" style={{ fontSize: '1.5rem' }}></i>}
            >
              <Dropdown.Item onClick={handleAccount}>Account</Dropdown.Item>
              <Dropdown.Item onClick={handleLogout}>Logout</Dropdown.Item>
            </HoverDropdown>
            {user.role.role === 'admin' && (
              <HoverDropdown 
                toggleContent={<i className="bi bi-list" style={{ fontSize: '1.5rem' }}></i>}
              >
                <Dropdown.Item onClick={() => navigate('/admin')}>Admin</Dropdown.Item>
              </HoverDropdown>
            )}
          </Nav>
        </Container>
      </Navbar>

      {/* Main Content */}
      <Container className="mt-4">
        <p>Welcome to your dashboard!</p>
      </Container>
    </>
  );
};

export default DashboardPage;