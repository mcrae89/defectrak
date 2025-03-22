import React, { useState } from 'react';
import './css/AccountPage.css';

const AccountPage = ({ user: initialUser, setUser }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    email: initialUser?.email || '',
    firstName: initialUser?.firstName || '',
    lastName: initialUser?.lastName || ''
  });

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
        //window.location.reload();
      } else {
        const errorData = await response.json();
        console.error('Error during updating:', errorData);
      }
    } catch (error) {
      console.error('Error during updating:', error);
    }
    setIsEditing(false);
  };

  return (
    <div className="account-page">
      <header className="header bg-dark text-white p-3 d-flex justify-content-between align-items-center">
        <h1>Account</h1>
        <i className="bi bi-person-circle" style={{ fontSize: '2rem' }}></i>
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
                </div>
              ) : (
                <p>No user data available.</p>
              )}
            </>
          )}
        </div>
      </main>
    </div>
  );
};

export default AccountPage;