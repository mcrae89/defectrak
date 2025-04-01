import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Container, Navbar, Nav, Dropdown } from 'react-bootstrap';
import HoverDropdown from './components/HoverDropdown';
import { TabComponent } from '@syncfusion/ej2-react-navigations';

// Synchronously import all tab components
import UsersTab from './UsersTab';
import RolesTab from './RolesTab';
import PrioritiesTab from './PrioritiesTab';
import StatusesTab from './StatusesTab';

const AdminPage = () => {
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

  // Define tab items for the Syncfusion Tab component.
  const tabItems = [
    { header: { text: 'Users' }, content: () => <UsersTab /> },
    { header: { text: 'Roles' }, content: () => <RolesTab /> },
    { header: { text: 'Priorities' }, content: () => <PrioritiesTab /> },
    { header: { text: 'Statuses' }, content: () => <StatusesTab /> }
  ];

  return (
    <>
      {/* Header */}
      <Navbar bg="dark" variant="dark" expand="lg">
        <Container>
          <Navbar.Brand>Admin</Navbar.Brand>
          <Nav className="ms-auto">
            <HoverDropdown
              toggleContent={<i className="bi bi-person-circle" style={{ fontSize: '1.5rem' }}></i>}
            >
              <Dropdown.Item onClick={handleAccount}>Account</Dropdown.Item>
              <Dropdown.Item onClick={handleLogout}>Logout</Dropdown.Item>
            </HoverDropdown>
            <HoverDropdown
              toggleContent={<i className="bi bi-list" style={{ fontSize: '1.5rem' }}></i>}
            >
              <Dropdown.Item onClick={() => navigate('/')}>Dashboard</Dropdown.Item>
            </HoverDropdown>
          </Nav>
        </Container>
      </Navbar>

      {/* Main Content using Syncfusion Tabs */}
      <Container className="mt-4">
        <TabComponent items={tabItems} />
      </Container>
    </>
  );
};

export default AdminPage;
