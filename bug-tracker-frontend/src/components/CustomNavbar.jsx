import React, { useContext } from 'react';
import { Navbar, Container, Nav, Button, Dropdown } from 'react-bootstrap';
import { useLocation, useNavigate } from 'react-router-dom';
import HoverDropdown from './HoverDropdown';
import { UserContext } from '../userContext';
import { ToastContext } from './ToastContext';

const CustomNavbar = ({ brand=null, onLoginClick = null }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, setUser, setAuthenticated } = useContext(UserContext);
  const { showToast } = useContext(ToastContext);

  // Define navigation items.
  const navItems = [
    { label: 'Dashboard', path: '/' },
    { label: 'Admin', path: '/admin' }
  ];

  // Filter out items based on current route and authorization.
  const filteredNavItems = navItems
    .filter(item => item.path !== location.pathname)
    .filter(item => !(item.path === '/admin' && user?.role?.role !== 'admin'));

  // Only show the "Account" option if not already on the account page.
  const showAccountOption = location.pathname !== '/account';

  const handleLogout = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch('/api/logout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include'
      });
      if (response.ok) {
        setUser(null);
        setAuthenticated(false);
        showToast("Logged out successfully!", 'success');
        navigate('/');
      } 
    } catch (error) {
      console.error('Error during logout:', error);
    }
  };

  const handleAccount = () => {
    navigate('/account');
  };

  return (
    <Navbar bg="dark" variant="dark" expand="lg">
      <Container>
        <Navbar.Brand>
          <img src="/defectrak-logo.svg" alt="DefecTrak Logo" style={{ height: '50px', paddingRight: '1em' }} />
          {brand}
        </Navbar.Brand>
        <Nav className="ms-auto">
          {onLoginClick ? (
            <Button variant="light" onClick={onLoginClick}>
              Login
            </Button>
          ) : (
            <>
              <HoverDropdown 
                toggleContent={
                  <i className="bi bi-person-circle" style={{ fontSize: '1.5rem' }}></i>
                }
              >
                {showAccountOption && (
                  <Dropdown.Item onClick={handleAccount}>Account</Dropdown.Item>
                )}
                <Dropdown.Item onClick={handleLogout}>Logout</Dropdown.Item>
              </HoverDropdown>
              {filteredNavItems.length > 0 && (
                <HoverDropdown 
                  toggleContent={
                    <i className="bi bi-list" style={{ fontSize: '1.5rem' }}></i>
                  }
                >
                  {filteredNavItems.map(item => (
                    <Dropdown.Item
                      key={item.path}
                      onClick={() => navigate(item.path)}
                    >
                      {item.label}
                    </Dropdown.Item>
                  ))}
                </HoverDropdown>
              )}
            </>
          )}
        </Nav>
      </Container>
    </Navbar>
  );
};

export default CustomNavbar;
