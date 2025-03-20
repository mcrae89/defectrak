import React from 'react';

const AccountPage = () => {
  
  return (
    <div className="account">
      <header className="header bg-dark text-white p-3 d-flex justify-content-between align-items-center">
        <h1>Account</h1>
        <i className="bi bi-person-circle" style={{ fontSize: '2rem' }}></i>
      </header>

      <main className="container mt-4">
        <p>Welcome to your Account!</p>
      </main>
    </div>
  );
};

export default AccountPage;
