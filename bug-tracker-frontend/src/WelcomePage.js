import React from 'react';
import './css/WelcomePage.css';

const WelcomePage = () => {
  return (
    <div className="welcome-page">
      <header className="header">
        <div className="logo">Bug Tracker</div>
        <div className="auth-buttons">
          <button className="login-button">Login/Register</button>
        </div>
      </header>
      <main className="main-content">
        <h1>Welcome to Bug Tracker</h1>
        <p>Your one-stop solution to track and manage bugs efficiently.</p>
        <button className="get-started">Get Started</button>
      </main>
    </div>
  );
};

export default WelcomePage;
