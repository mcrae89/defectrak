import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Modal, Button, Navbar, Container, Row, Col, Dropdown, Nav } from 'react-bootstrap';
import HoverDropdown from './components/HoverDropdown';
import './css/AccountPage.css';

import { TextBoxComponent } from '@syncfusion/ej2-react-inputs';

const AccountPage = ({ user: initialUser, setUser, setAuthenticated }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    email: initialUser?.email || '',
    firstName: initialUser?.firstName || '',
    lastName: initialUser?.lastName || ''
  });
  const [showModal, setShowModal] = useState(false);
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordError, setPasswordError] = useState('');

  const navigate = useNavigate();

  const handleEditClick = () => {
    setIsEditing(true);
  };

  const handleCancel = () => {
    setFormData({
      email: initialUser?.email || '',
      firstName: initialUser?.firstName || '',
      lastName: initialUser?.lastName || ''
    });
    setIsEditing(false);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log("Updated data:", formData);

    try {
      const response = await fetch('/api/me', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(formData)
      });

      if (response.ok) {
        const updatedUser = await response.json();
        setUser(updatedUser);
      } else {
        const errorData = await response.json();
        console.error('Error during updating:', errorData);
      }
    } catch (error) {
      console.error('Error during updating:', error);
    }
    setIsEditing(false);
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
        setUser(null);
        setAuthenticated(false);
        navigate('/');
      } else {
        const errorData = await response.json();
        console.log(errorData);
      }
    } catch (error) {
      console.error('Error during logout:', error);
    }
  };

  const openPasswordModal = () => {
    setNewPassword('');
    setConfirmPassword('');
    setPasswordError('');
    setShowModal(true);
  };

  const closePasswordModal = () => {
    setShowModal(false);
    setPasswordError('');
  };

  const handlePasswordUpdateSubmit = async (e) => {
    e.preventDefault();
    if (newPassword !== confirmPassword || !newPassword) {
      setPasswordError("Passwords must match and cannot be empty.");
      return;
    }
    try {
      const response = await fetch('/api/me/password', {
        method: 'PUT',
        headers: { 'Content-Type': 'text/plain' },
        credentials: 'include',
        body: newPassword
      });
      if (response.ok) {
        console.log('Password update successful');
        closePasswordModal();
      } else {
        const errorData = await response.json();
        console.error('Error updating password:', errorData);
        setPasswordError(errorData.message || "Error updating password");
      }
    } catch (error) {
      console.error('Error updating password:', error);
      setPasswordError("Error updating password");
    }
  };

  return (
    <>
      <Navbar bg="dark" variant="dark" expand="lg">
        <Container>
          <Navbar.Brand>Account</Navbar.Brand>
          <Nav className="ms-auto">
            <HoverDropdown 
              toggleContent={<i className="bi bi-person-circle" style={{ fontSize: '1.5rem' }}></i>}
            >
              <Dropdown.Item onClick={handleLogout}>Logout</Dropdown.Item>
            </HoverDropdown>
            <HoverDropdown 
              toggleContent={<i className="bi bi-list" style={{ fontSize: '1.5rem' }}></i>}
            >
              <Dropdown.Item onClick={() => navigate('/')}>Dashboard</Dropdown.Item>
              {initialUser?.role?.role === 'admin' && (
                <Dropdown.Item onClick={() => navigate('/admin')}>Admin</Dropdown.Item>
              )}
            </HoverDropdown>
          </Nav>
        </Container>
      </Navbar>

      {/* Main Content */}
      <Container className="my-4">
        <Row className="justify-content-center">
          <Col md={5}>
            <div className="d-flex justify-content-between align-items-center mb-3">
              <h2>Account Information</h2>
              <Button variant="outline-secondary" onClick={handleEditClick}>
                <i className="bi bi-pencil-square"></i> Edit
              </Button>
            </div>
            {isEditing ? (
              <form onSubmit={handleSubmit}>
                <div className="mb-3">
                  <label htmlFor="formEmail">Email:</label>
                  <TextBoxComponent 
                    id="formEmail"
                    name="email"
                    value={formData.email}
                    change={(e) => handleChange({ target: { name: 'email', value: e.value } })}
                  />
                </div>
                <div className="mb-3">
                  <label htmlFor="formFirstName">First Name:</label>
                  <TextBoxComponent 
                    id="formFirstName"
                    name="firstName"
                    value={formData.firstName}
                    change={(e) => handleChange({ target: { name: 'firstName', value: e.value } })}
                  />
                </div>
                <div className="mb-3">
                  <label htmlFor="formLastName">Last Name:</label>
                  <TextBoxComponent 
                    id="formLastName"
                    name="lastName"
                    value={formData.lastName}
                    change={(e) => handleChange({ target: { name: 'lastName', value: e.value } })}
                  />
                </div>
                <div className="d-flex gap-2">
                  <Button type="submit" variant="primary">Update</Button>
                  <Button type="button" variant="secondary" onClick={handleCancel}>Cancel</Button>
                </div>
              </form>
            ) : (
              <div>
                {initialUser ? (
                  <>
                    <p>Email: {initialUser.email}</p>
                    <p>
                      Name: {initialUser.firstName} {initialUser.lastName}
                    </p>
                    <Button type="button" variant="link" onClick={openPasswordModal} className="p-0">
                      Update Password
                    </Button>
                  </>
                ) : (
                  <p>No user data available.</p>
                )}
              </div>
            )}
          </Col>
        </Row>
      </Container>

      {/* Password Update Modal */}
      <Modal show={showModal} onHide={closePasswordModal}>
        <Modal.Header closeButton>
          <Modal.Title>Update Password</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <form onSubmit={handlePasswordUpdateSubmit}>
            <div className="mb-3">
              <label htmlFor="newPassword">New Password</label>
              <TextBoxComponent 
                id="newPassword"
                type="password"
                placeholder="Password"
                value={newPassword}
                change={(e) => setNewPassword(e.value)}
              />
            </div>
            <div className="mb-3">
              <label htmlFor="confirmPassword">Confirm Password</label>
              <TextBoxComponent 
                id="confirmPassword"
                type="password"
                placeholder="Password"
                value={confirmPassword}
                change={(e) => setConfirmPassword(e.value)}
              />
            </div>
            {passwordError && <div className="text-danger mb-3">{passwordError}</div>}
            <div className="d-flex gap-2">
              <Button type="submit" variant="primary" disabled={!newPassword || newPassword !== confirmPassword}>
                Update Password
              </Button>
              <Button type="button" variant="secondary" onClick={closePasswordModal}>
                Cancel
              </Button>
            </div>
          </form>
        </Modal.Body>
      </Modal>
    </>
  );
};

export default AccountPage;