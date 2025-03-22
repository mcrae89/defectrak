import React, { useState, useEffect } from 'react';
import { Routes, Route } from 'react-router-dom';
import { Navigate } from 'react-router-dom';
import WelcomePage from './WelcomePage';
import DashboardPage from './DashboardPage';
import AccountPage from './AccountPage';

function App() {
  const [authenticated, setAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);
  const [user, setUser] = useState(null);

  useEffect(() => {
    async function checkAuth() {
      try {
        const response = await fetch('/api/me', {
          credentials: 'include'
        });
        if (response.status === 200) {
          const userData = await response.json();
          setUser(userData);
          setAuthenticated(true);
        } else {
          setAuthenticated(false);
        }
      } catch (error) {
        setAuthenticated(false);
      } finally {
        setLoading(false);
      }
    }
    checkAuth();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <Routes>
      <Route path="/" element={authenticated ? <DashboardPage /> : <WelcomePage />} />
      <Route path="/account" element={authenticated ? (<AccountPage user={user} setUser={setUser} />) : (<Navigate to='/' />)} />
    </Routes>
  );
}

export default App;