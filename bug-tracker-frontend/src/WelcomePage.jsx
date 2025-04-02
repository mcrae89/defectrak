import React, { useState } from 'react';
import { Container, Navbar, Button, Form, Modal } from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import './css/WelcomePage.css';
import { TextBoxComponent } from '@syncfusion/ej2-react-inputs';

const WelcomePage = () => {
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [showRegisterModal, setShowRegisterModal] = useState(false);
  const [loginEmail, setLoginEmail] = useState('');
  const [loginPassword, setLoginPassword] = useState('');
  const [loginError, setLoginError] = useState(null);
  const [registerEmail, setRegisterEmail] = useState('');
  const [registerPassword, setRegisterPassword] = useState('');
  const [confirmRegisterPassword, setConfirmRegisterPassword] = useState('');
  const [registerFirstName, setRegisterFirstName] = useState('');
  const [registerLastName, setRegisterLastName] = useState('');
  const [registerError, setRegisterError] = useState(null);

  const handleLoginClick = () => {
    setShowLoginModal(true);
  };

  const handleRegisterClick = () => {
    setShowRegisterModal(true);
  };

  const handleClose = () => {
    setShowLoginModal(false);
    setShowRegisterModal(false);
    setLoginError(null);
    setRegisterError(null);
    setRegisterEmail('');
    setRegisterPassword('');
    setConfirmRegisterPassword('');
    setRegisterFirstName('');
    setRegisterLastName('');
  };

  const handleLoginSubmit = async (e) => {
    e.preventDefault();
    setLoginError(null);

    try {
      const response = await fetch('/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ email: loginEmail, password: loginPassword })
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
        headers: { 'Content-Type': 'application/json' },
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
        setShowLoginModal(true);
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

  // Calculate whether all register fields are filled and valid
  const isRegisterFormValid =
    registerEmail &&
    registerFirstName &&
    registerLastName &&
    registerPassword &&
    confirmRegisterPassword &&
    registerPassword === confirmRegisterPassword;

  return (
    <div className="welcome-page">
      {/* Header */}
      <Navbar bg="dark" variant="dark">
        <Container>
          <Navbar.Brand>Bug Tracker</Navbar.Brand>
          <Button variant="light" onClick={handleLoginClick}>
            Login
          </Button>
        </Container>
      </Navbar>

      {/* Main Content */}
      <Container className="main-content text-center py-5">
        <h1>Welcome to Bug Tracker</h1>
        <p>Your one-stop solution to track and manage bugs efficiently.</p>
        <Button variant="primary" className="mt-3" onClick={handleRegisterClick}>
          Get Started
        </Button>
      </Container>

      {/* Login Modal */}
      <Modal show={showLoginModal} onHide={handleClose} centered>
        <Modal.Header closeButton>
          <Modal.Title>Login</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleLoginSubmit}>
            <Form.Group className="mb-3" controlId="login-email">
              <Form.Label>Email address</Form.Label>
              <TextBoxComponent
                type="email"
                placeholder="Enter email"
                value={loginEmail}
                change={(e) => setLoginEmail(e.value)}
                required={true}
              />
            </Form.Group>
            <Form.Group className="mb-3" controlId="login-password">
              <Form.Label>Password</Form.Label>
              <TextBoxComponent
                type="password"
                placeholder="Password"
                value={loginPassword}
                change={(e) => setLoginPassword(e.value)}
                required={true}
              />
            </Form.Group>
            {loginError && <div className="text-danger mb-3">{loginError}</div>}
            <Button variant="primary" type="submit" className="w-100">
              Login
            </Button>
          </Form>
        </Modal.Body>
      </Modal>

      {/* Register Modal */}
      <Modal show={showRegisterModal} onHide={handleClose} centered>
        <Modal.Header closeButton>
          <Modal.Title>Register</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleRegisterSubmit}>
            <Form.Group className="mb-3" controlId="register-email">
              <Form.Label>Email address</Form.Label>
              <TextBoxComponent
                type="email"
                placeholder="Enter email"
                value={registerEmail}
                change={(e) => setRegisterEmail(e.value)}
                required={true}
              />
            </Form.Group>
            <Form.Group className="mb-3" controlId="register-first-name">
              <Form.Label>First Name</Form.Label>
              <TextBoxComponent
                type="text"
                placeholder="First Name"
                value={registerFirstName}
                change={(e) => setRegisterFirstName(e.value)}
                required={true}
              />
            </Form.Group>
            <Form.Group className="mb-3" controlId="register-last-name">
              <Form.Label>Last Name</Form.Label>
              <TextBoxComponent
                type="text"
                placeholder="Last Name"
                value={registerLastName}
                change={(e) => setRegisterLastName(e.value)}
                required={true}
              />
            </Form.Group>
            <Form.Group className="mb-3" controlId="register-password">
              <Form.Label>Password</Form.Label>
              <TextBoxComponent
                type="password"
                placeholder="Password"
                value={registerPassword}
                change={(e) => setRegisterPassword(e.value)}
                required={true}
              />
            </Form.Group>
            <Form.Group className="mb-3" controlId="confirm-register-password">
              <Form.Label>Confirm Password</Form.Label>
              <TextBoxComponent
                type="password"
                placeholder="Confirm Password"
                value={confirmRegisterPassword}
                change={(e) => setConfirmRegisterPassword(e.value)}
                required={true}
              />
            </Form.Group>
            {registerError && <div className="text-danger mb-3">{registerError}</div>}
            <Button
              variant="primary"
              type="submit"
              className="w-100"
              disabled={!isRegisterFormValid}
            >
              Register
            </Button>
          </Form>
        </Modal.Body>
      </Modal>
    </div>
  );
};

export default WelcomePage;
