import React, { useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';

const WelcomePage = () => {
  const [showModal, setShowModal] = useState(false);

  const handleLoginClick = () => {
    setShowModal(true);
  };

  const handleClose = () => {
    setShowModal(false);
  };

  return (
    <div className="welcome-page" style={{ position: 'relative' }}>
      {/* Main Page Content */}
      <div className={`page-content ${showModal ? 'faded' : ''}`}>
        <header className="header d-flex justify-content-between align-items-center p-3 bg-dark text-white">
          <div className="logo">Bug Tracker</div>
          <button onClick={handleLoginClick} className="btn btn-light">
            Login
          </button>
        </header>
        <main className="main-content text-center py-5">
          <h1>Welcome to Bug Tracker</h1>
          <p>Your one-stop solution to track and manage bugs efficiently.</p>
          <button className="btn btn-primary mt-3">Get Started</button>
        </main>
      </div>

      {/* Modal Overlay */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content card p-4" style={{ width: '350px' }}>
            <h2 className="text-center mb-4">Login</h2>
            <form>
              <div className="mb-3">
                <label htmlFor="email" className="form-label">
                  Email address
                </label>
                <input
                  type="email"
                  className="form-control"
                  id="email"
                  placeholder="Enter email"
                />
              </div>
              <div className="mb-3">
                <label htmlFor="password" className="form-label">
                  Password
                </label>
                <input
                  type="password"
                  className="form-control"
                  id="password"
                  placeholder="Password"
                />
              </div>
              <button type="submit" className="btn btn-primary w-100">
                Login
              </button>
            </form>
            <button onClick={handleClose} className="btn btn-secondary w-100 mt-2">
              Close
            </button>
          </div>
        </div>
      )}

      {/* Inline Styles for Fade and Modal */}
      <style jsx="true">{`
        .page-content.faded {
          opacity: 0.3;
          transition: opacity 0.3s ease;
        }
        .modal-overlay {
          position: fixed;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          display: flex;
          justify-content: center;
          align-items: center;
          background: rgba(0, 0, 0, 0.5);
          z-index: 1050;
        }
        .modal-content {
          z-index: 1100;
          animation: fadeIn 0.3s ease;
          background: white;
        }
        @keyframes fadeIn {
          from {
            opacity: 0;
            transform: translateY(-10px);
          }
          to {
            opacity: 1;
            transform: translateY(0);
          }
        }
      `}</style>
    </div>
  );
};

export default WelcomePage;
