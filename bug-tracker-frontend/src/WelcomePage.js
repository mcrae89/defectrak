import React, { useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import ReactDOM from 'react-dom';
import './css/WelcomePage.css'

const Modal = ({ children, onClose }) => {
  return ReactDOM.createPortal(
    <div className="modal-overlay">
      <div className="modal-content card p-4" style={{ width: '350px' }}>
        {children}
        <button onClick={onClose} className="btn btn-secondary w-100 mt-2">
          Close
        </button>
      </div>
    </div>,
    document.body
  );
};

const WelcomePage = () => {
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [showRegisterModal, setShowRegisterModal] = useState(false);
  const[loginEmail, setLoginEmail] = useState('');
  const[loginPassword, setLoginPassword] = useState('');
  const[loginError, setLoginError] = useState(null);

  const handleLoginClick = () => {
    setShowLoginModal(true);
    if(!document.getElementById('page-content').classList.contains('faded')) {
      document.getElementById('page-content').classList.add('faded');
    }
  };

  const handleRegisterClick = () => {
    setShowRegisterModal(true);
    if(!document.getElementById('page-content').classList.contains('faded')) {
      document.getElementById('page-content').classList.add('faded');
    }
  };

  const handleClose = () => {
    setShowLoginModal(false);
    setShowRegisterModal(false);
    if(document.getElementById('page-content').classList.contains('faded')) {
      document.getElementById('page-content').classList.remove('faded');
    }
    setLoginError(null);
  };


  const handleLoginSubmit = async (e) => {
    e.preventDefault();
    setLoginError(null);

    try {
      const response = await fetch(`${process.env.REACT_APP_API_URL}/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          email: loginEmail,
          password: loginPassword
        })
      });
  
      if (response.ok) {
        console.log('Login successful');
        handleClose();
      } else {
        const errorData = await response.json();
        setLoginError(errorData.message || 'Invalid credentials');
      }
    } catch (error) {
      console.error('Error during login:', error);
      setLoginError('An error occurred. Please try again.');
    }
  };


  return (
    <div className="welcome-page" style={{ position: 'relative' }}>
      {/* Main Page Content */}
      <div id="page-content">
        <header className="header d-flex justify-content-between align-items-center p-3 bg-dark text-white">
          <div className="logo">Bug Tracker</div>
          <button onClick={handleLoginClick} className="btn btn-light">
            Login
          </button>
        </header>
        <main className="main-content text-center py-5">
          <h1>Welcome to Bug Tracker</h1>
          <p>Your one-stop solution to track and manage bugs efficiently.</p>
          <button onClick={handleRegisterClick} className="btn btn-primary mt-3">Get Started</button>
        </main>
      </div>

      {/* Login Modal Overlay */}
      {showLoginModal && (
        <Modal onClose={handleClose}>
          <h2 className="text-center mb-4">Login</h2>
          <form onSubmit={handleLoginSubmit}>
            <div className="mb-3">
              <label htmlFor="login-email" className="form-label">
                Email address
              </label>
              <input
                type="email"
                className="form-control"
                id="login-email"
                placeholder="Enter email"
                value={loginEmail}
                onChange={(e) => setLoginEmail(e.target.value)}
                required
              />
            </div>
            <div className="mb-3">
              <label htmlFor="login-password" className="form-label">
                Password
              </label>
              <input
                type="password"
                className="form-control"
                id="login-password"
                placeholder="Password"
                value={loginPassword}
                onChange={(e) => setLoginPassword(e.target.value)}
                required
              />
            </div>
            {loginError && <div className="text-danger mb-3">{loginError}</div>}
            <button type="submit" className="btn btn-primary w-100">
              Login
            </button>
          </form>
        </Modal>
      )}

      {showRegisterModal && (
        <Modal onClose={handleClose}>
          <h2 className="text-center mb-4">Register</h2>
          <form>
            <div className="mb-3">
              <label htmlFor="register-email" className="form-label">
                Email address
              </label>
              <input
                type="email"
                className="form-control"
                id="register-email"
                placeholder="Enter email"
              />
            </div>
            <div className="mb-3">
              <label htmlFor="register-password" className="form-label">
                Password
              </label>
              <input
                type="password"
                className="form-control"
                id="register-password"
                placeholder="Password"
              />
            </div>
            <button type="submit" className="btn btn-primary w-100">
              Register
            </button>
          </form>
        </Modal>
      )}

    </div>
  );
};

export default WelcomePage;
