import React from 'react';
import { Container } from 'react-bootstrap';
import BugList from './components/BugList';
import CustomNavbar from './components/CustomNavbar';

const DashboardPage = () => {
  
  return (
    <>
      {/* Header */}
      <CustomNavbar 
        brand="Dashboard" 
      />

      {/* Main Content */}
      <Container className="mt-4">
        <BugList />
      </Container>
    </>
  );
};

export default DashboardPage;
