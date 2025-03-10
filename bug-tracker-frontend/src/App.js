import React from 'react';
import { Routes, Route } from 'react-router-dom';
import WelcomePage from './WelcomePage';
import LoginPage from './LoginPage';

function App() {
  return (
    <Routes>
      <Route path="/" element={<WelcomePage />} />
      <Route path="/login" element={<LoginPage />} />
    </Routes>
  );
}

export default App;
