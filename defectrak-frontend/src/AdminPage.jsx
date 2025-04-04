import React from 'react';
import { Container } from 'react-bootstrap';
import { TabComponent } from '@syncfusion/ej2-react-navigations';
import CustomNavbar from './components/CustomNavbar';

// Synchronously import all tab components
import UsersTab from './UsersTab';
import RolesTab from './RolesTab';
import PrioritiesTab from './PrioritiesTab';
import StatusesTab from './StatusesTab';

const AdminPage = () => {

  // Define tab items for the Syncfusion Tab component.
  const tabItems = [
    { header: { text: 'Users' }, content: () => <UsersTab /> },
    { header: { text: 'Roles' }, content: () => <RolesTab /> },
    { header: { text: 'Priorities' }, content: () => <PrioritiesTab /> },
    { header: { text: 'Statuses' }, content: () => <StatusesTab /> }
  ];

  return (
    <>
      {/* Header */}
      <CustomNavbar 
        brand="Admin" 
      />

      {/* Main Content using Syncfusion Tabs */}
      <Container className="mt-4">
        <TabComponent items={tabItems} />
      </Container>
    </>
  );
};

export default AdminPage;
