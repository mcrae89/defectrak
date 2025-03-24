import React, { useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import ReactDOM from 'react-dom';
import './css/WelcomePage.css';

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
  const [loginEmail, setLoginEmail] = useState('');
  const [loginPassword, setLoginPassword] = useState('');
  const [loginError, setLoginError] = useState(null);
  const [registerEmail, setRegisterEmail] = useState('');
  const [registerPassword, setRegisterPassword] = useState('');
  const [registerFirstName, setRegisterFirstName] = useState('');
  const [registerLastName, setRegisterLastName] = useState('');
  const [registerError, setRegisterError] = useState(null);

  const handleLoginClick = () => {
    setShowLoginModal(true);
    if (!document.getElementById('page-content').classList.contains('faded')) {
      document.getElementById('page-content').classList.add('faded');
    }
  };

  const handleRegisterClick = () => {
    setShowRegisterModal(true);
    if (!document.getElementById('page-content').classList.contains('faded')) {
      document.getElementById('page-content').classList.add('faded');
    }
  };

  const handleClose = () => {
    setShowLoginModal(false);
    setShowRegisterModal(false);
    if (document.getElementById('page-content').classList.contains('faded')) {
      document.getElementById('page-content').classList.remove('faded');
    }
    setLoginError(null);
    setRegisterError(null);
  };

  const handleLoginSubmit = async (e) => {
    e.preventDefault();
    setLoginError(null);

    try {
      const response = await fetch('/api/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({
          email: loginEmail,
          password: loginPassword
        })
      });

      if (response.ok) {
        console.log('Login successful');
        handleClose();
        window.location.reload();
      } else {
        const errorData = await response.json();
        setLoginError(errorData.message || 'Invalid credentials');
      }
    } catch (error) {
      console.error('Error during login:', error);
      setLoginError('An error occurred. Please try again.');
    }
  };

  const handleRegisterSubmit = async (e) => {
    e.preventDefault();
    setRegisterError(null);
  
    try {
      const response = await fetch('/api/users/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({
          email: registerEmail,
          firstName: registerFirstName,
          lastName: registerLastName,
          password: registerPassword,
          userRoleId: 2,
          status: 'active'
        })
      });

      if (response.ok) {
        console.log('Registration successful');
        handleClose();
        handleLoginClick();
      } else if (response.status === 409) {
        const errorText = await response.text();
        setRegisterError(errorText || 'A user with this email already exists.');
      } else {
        const errorData = await response.json();
        setRegisterError(errorData.message || 'Invalid credentials');
      }
    } catch (error) {
      console.error('Error during registration:', error);
      setRegisterError('An error occurred. Please try again.');
    }
  };
  

  return (
    <div className="welcome-page" style={{ position: 'relative' }}>
      {/* Main Page Content */}
      <div id="page-content">
        <header className="header bg-dark text-white">
          <div className="logo">Bug Tracker</div>
          <button onClick={handleLoginClick} className="btn btn-light">
            Login
          </button>
        </header>
        <main className="main-content text-center py-5">
          <h1>Welcome to Bug Tracker</h1>
          <p>Your one-stop solution to track and manage bugs efficiently.</p>
          <button onClick={handleRegisterClick} className="btn btn-primary mt-3">
            Get Started
          </button>
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

      {/* Register Modal Overlay */}
      {showRegisterModal && (
        <Modal onClose={handleClose}>
          <h2 className="text-center mb-4">Register</h2>
          <form onSubmit={handleRegisterSubmit}>
            <div className="mb-3">
              <label htmlFor="register-email" className="form-label">
                Email address
              </label>
              <input
                type="email"
                className="form-control"
                id="register-email"
                placeholder="Enter email"
                value={registerEmail}
                onChange={(e) => setRegisterEmail(e.target.value)}
                required
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
                value={registerPassword}
                onChange={(e) => setRegisterPassword(e.target.value)}
                required
              />
            </div>
            <div className="mb-3">
              <label htmlFor="register-first-name" className="form-label">
                First Name
              </label>
              <input
                type="text"
                className="form-control"
                id="register-first-name"
                placeholder="First Name"
                value={registerFirstName}
                onChange={(e) => setRegisterFirstName(e.target.value)}
                required
              />
            </div>
            <div className="mb-3">
              <label htmlFor="register-last-name" className="form-label">
                Last Name
              </label>
              <input
                type="text"
                className="form-control"
                id="register-last-name"
                placeholder="Last Name"
                value={registerLastName}
                onChange={(e) => setRegisterLastName(e.target.value)}
                required
              />
            </div>
            {registerError && <div className="text-danger mb-3">{registerError}</div>}
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
