import React, { useState } from 'react';
import ReactDOM from 'react-dom';
import './css/AccountPage.css';
import { useNavigate } from 'react-router-dom';

const Modal = ({ children, onClose }) => {
  return ReactDOM.createPortal(
    <div className="modal-overlay">
      <div className="modal-content card p-4" style={{ width: '350px' }}>
        {children}
      </div>
    </div>,
    document.body
  );
};

const AccountPage = ({ user: initialUser, setUser, setAuthenticated }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    email: initialUser?.email || '',
    firstName: initialUser?.firstName || '',
    lastName: initialUser?.lastName || ''
  });
  const [showAccountOptions, setShowAccountOptions] = useState(false);
  const [showMenuOptions, setShowMenuOptions] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordError, setPasswordError] = useState('');
  
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
        headers: {
          'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify(formData)
      });

      if (response.ok) {
        console.log('Update successful');
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
        headers: {
          'Content-Type': 'application/json'
        },
        credentials: 'include'
      });
  
      if (response.ok) {
        console.log('Logout successful');
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
        headers: {
          'Content-Type': 'text/plain'
        },
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

  const handleBackClick = () => {
    navigate('/');
  };

  return (
    <div className="account-page">
      <header className="header bg-dark text-white">
        <div className="logo">Account</div>
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
                <div onClick={handleLogout} style={{ padding: '0.5rem', cursor: 'pointer' }}>
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
                <div
                    onClick={() => navigate('/')}
                  >
                    Dashboard
                  </div>
                {initialUser.role.role === 'admin' && (
                  <div
                    onClick={() => navigate('/admin')}
                  >
                    Admin
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </header>

      <main className="account-main-content">
        <div className="col-md-8 account-info">
          <div className="edit-icon" onClick={handleEditClick}>
            <i className="bi bi-pencil-square"></i>
          </div>
          {isEditing ? (
            <form onSubmit={handleSubmit} className="account-form">
              <div className="form-group">
                <label>Email:</label>
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  className="form-control"
                />
              </div>
              <div className="form-group">
                <label>First Name:</label>
                <input
                  type="text"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                  className="form-control"
                />
              </div>
              <div className="form-group">
                <label>Last Name:</label>
                <input
                  type="text"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                  className="form-control"
                />
              </div>
              <div className="button-group">
                <button type="submit" className="btn btn-primary">Update</button>
                <button type="button" onClick={handleCancel} className="btn btn-secondary">
                  Cancel
                </button>
              </div>
            </form>
          ) : (
            <>
              {initialUser ? (
                <div className="user-details">
                  <p>Email: {initialUser.email}</p>
                  <p>
                    Name: {initialUser.firstName} {initialUser.lastName}
                  </p>
                  <p>
                    <button className="btn btn-link" onClick={openPasswordModal} style={{ paddingLeft: 0 }}>
                      Update password
                    </button>
                  </p>
                </div>
              ) : (
                <p>No user data available.</p>
              )}
            </>
          )}
        </div>
      </main>

      {/* Modal Overlay */}
      {showModal && (
        <Modal onClose={closePasswordModal}>
          <h2 className="text-center mb-4">Update Password</h2>
          <form onSubmit={handlePasswordUpdateSubmit}>
            <div className="mb-3">
              <label htmlFor="new-password" className="form-label">
                New Password
              </label>
              <input
                type="password"
                className="form-control"
                id="new-password"
                placeholder="Password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                required
              />
            </div>
            <div className="mb-3">
              <label htmlFor="confirm-password" className="form-label">
                Confirm Password
              </label>
              <input
                type="password"
                className="form-control"
                id="new-confirm"
                placeholder="Password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
              />
            </div>
            {passwordError && <div className="text-danger mb-3">{passwordError}</div>}
            <div className="button-group">
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={!newPassword || newPassword !== confirmPassword}
                >
                  Update Password
                </button>
                <button type="button" onClick={closePasswordModal} className="btn btn-secondary">
                  Cancel
                </button>
              </div>
          </form>
        </Modal>
      )}

    </div>
  );
};

export default AccountPage;