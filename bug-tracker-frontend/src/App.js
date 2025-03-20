import React, { useState, useEffect } from 'react';
import { Routes, Route } from 'react-router-dom';
import WelcomePage from './WelcomePage';
import DashboardPage from './DashboardPage';
import AccountPage from './AccountPage';

function App() {
  const [authenticated, setAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function checkAuth() {
      try {
        const response = await fetch('/api/me', {
          credentials: 'include'
        });
        if (response.status === 200) {
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
      <Route path="/account" element={<AccountPage />} />
    </Routes>
  );
}

export default App;