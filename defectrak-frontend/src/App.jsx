import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { registerLicense } from '@syncfusion/ej2-base';

import WelcomePage from './WelcomePage';
import DashboardPage from './DashboardPage';
import AccountPage from './AccountPage';
import AdminPage from './AdminPage';
import { UserProvider, UserContext } from './userContext';
import { ToastProvider } from './components/ToastContext';

registerLicense(process.env.REACT_APP_SYNCFUSION_LICENSE);

const AppRoutes = () => {
  const { authenticated, user } = React.useContext(UserContext);

  return (
    <Routes>
      <Route path="/" element={authenticated ? <DashboardPage /> : <WelcomePage />} />
      <Route path="/account" element={authenticated ? <AccountPage /> : <Navigate to="/" />} />
      <Route
        path="/admin"
        element={authenticated && user?.role?.role === 'admin' ? <AdminPage /> : <Navigate to="/" />}
      />
    </Routes>
  );
};

function App() {
  return (
    <ToastProvider>
      <UserProvider>
        <AppRoutes />
      </UserProvider>
      </ToastProvider>
  );
}

export default App;
