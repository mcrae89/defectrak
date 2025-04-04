import React, { useState, useContext } from 'react';
import { Modal, Button, Container, Row, Col } from 'react-bootstrap';
import { TextBoxComponent } from '@syncfusion/ej2-react-inputs';
import { UserContext } from './userContext';
import CustomNavbar from './components/CustomNavbar';
import { ToastContext } from './components/ToastContext';
import './css/AccountPage.css';

const AccountPage = () => {
  const { user, setUser } = useContext(UserContext);
  const { showToast } = useContext(ToastContext);

  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    email: user?.email || '',
    firstName: user?.firstName || '',
    lastName: user?.lastName || ''
  });
  const [showModal, setShowModal] = useState(false);
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordError, setPasswordError] = useState('');

  const handleEditClick = () => {
    setIsEditing(true);
  };

  const handleCancel = () => {
    setFormData({
      email: user?.email || '',
      firstName: user?.firstName || '',
      lastName: user?.lastName || ''
    });
    setIsEditing(false);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

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
        showToast("Account information updated successfully!", 'success');
      } else {
        showToast("Error updating account information", 'error');
      }
    } catch (error) {
      console.error('Error during updating:', error);
    }
    setIsEditing(false);
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
        showToast("Password updated successfully!", 'success');
        closePasswordModal();
      } else {
        const errorData = await response.json();
        showToast("Error updating password", 'error');
        setPasswordError(errorData.message || "Error updating password");
      }
    } catch (error) {
      console.error('Error updating password:', error);
      setPasswordError("Error updating password");
    }
  };

  return (
    <>
      <CustomNavbar brand="Account" />
      <Container className="my-4">
        <Row className="justify-content-center">
          <Col md={5}  className='account-form'>
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
                {user ? (
                  <>
                    <p>Email: {user.email}</p>
                    <p>
                      Name: {user.firstName} {user.lastName}
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
