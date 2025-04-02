import React, { useEffect, useState, useRef } from 'react';
import { Container } from 'react-bootstrap';
import { DataManager, Query } from '@syncfusion/ej2-data';
import {
  GridComponent,
  ColumnsDirective,
  ColumnDirective,
  Edit,
  Inject,
  Toolbar,
  Page,
  Sort,
  Filter
} from '@syncfusion/ej2-react-grids';

const UsersTab = () => {
  const [users, setUsers] = useState([]);
  const [activeRoles, setActiveRoles] = useState([]);
  const [isEditing, setIsEditing] = useState(false);
  const gridRef = useRef(null);

  useEffect(() => {
    fetchActiveRoles();
    fetchUsers();
  }, []);

  // Fetch users and map each user to include both roleId and roleName
  const fetchUsers = async () => {
    try {
      const response = await fetch('/api/users', { credentials: 'include' });
      if (response.ok) {
        const data = await response.json();
        setUsers(data);
      } else {
        console.error('Error fetching users');
      }
    } catch (error) {
      console.error('Error fetching users:', error);
    }
  };

  // Fetch roles and filter to active roles only
  const fetchActiveRoles = async () => {
    try {
      const response = await fetch('/api/user-roles', { credentials: 'include' });
      if (response.ok) {
        const data = await response.json();
        const active = data.filter(role => role.status === 'active');
        setActiveRoles(active);
      } else {
        console.error('Error fetching roles');
      }
    } catch (error) {
      console.error('Error fetching roles:', error);
    }
  };

  // Disable user (set status to "disabled")
  const handleDisableUser = async (user) => {
    try {
      const response = await fetch(`/api/users/${user.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'text/plain' },
        credentials: 'include',
        body: 'disabled'
      });
      if (response.ok) {
        await fetchUsers();
      }
    } catch (error) {
      console.error('Error disabling user:', error);
    }
  };

  // Enable user (set status to "active")
  const handleEnableUser = async (user) => {
    try {
      const response = await fetch(`/api/users/${user.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'text/plain' },
        credentials: 'include',
        body: 'active'
      });
      if (response.ok) {
        await fetchUsers();
      }
    } catch (error) {
      console.error('Error enabling user:', error);
    }
  };

  // Allow only editing (no adding) and use inline editing mode
  const editingSettings = {
    allowEditing: true,
    allowAdding: false,
    allowDeleting: false,
    mode: 'Normal'
  };

  const toolbarOptions = isEditing ? ['Update', 'Cancel'] : ['Edit'];
  const pageSettings = { pageSize: 10, pageSizes: ['10', '25', '50', 'All'] };
  const sortSettings = { columns: [] };
  const filterSettings = { type: 'Menu' };

  const actionBegin = (args) => {
    if (args.requestType === 'beginEdit') {
      setIsEditing(true);
    }
    if (args.requestType === 'add') {
      args.cancel = true;
    }
  };

  // On save, update the user's role via the API.
  const actionComplete = async (args) => {
    if (args.requestType === 'save' && args.action === 'edit') {
      try {
        const updatedUser = args.data;
        const selectedRoleId = updatedUser.role.id; // Handle both object and ID cases
        console.log('Updated data:', args.data);
        const response = await fetch(`/api/users/${updatedUser.id}/role`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify({
            userRoleId: Number(selectedRoleId)
          })
        });
        if (response.ok) await fetchUsers();
      } catch (error) {
        console.error('Error updating user role:', error);
      }
      setIsEditing(false);
    }
    if (args.requestType === 'cancel') setIsEditing(false);
  };

  // Actions column template: display a trash icon to disable active users
  // or a plus icon to enable disabled users.
  const commandTemplate = (props) => (
    <>
      {props.status === 'active' ? (
        <button
          className="btn btn-link p-0"
          onClick={() => handleDisableUser(props)}
          title="Disable"
        >
          <i className="bi bi-trash" style={{ fontSize: '1.25rem' }}></i>
        </button>
      ) : (
        <button
          className="btn btn-link p-0"
          onClick={() => handleEnableUser(props)}
          title="Enable"
        >
          <i className="bi bi-plus" style={{ fontSize: '1.4rem' }}></i>
        </button>
      )}
    </>
  );

  // Template for displaying the role name when not editing
  const roleTemplate = (props) => {
    return props.role?.role || '';
  };

  // Custom dropdown editor configuration using your example approach
  const dropdownParams = {
    params: {
      actionComplete: () => false,
      dataSource: new DataManager(activeRoles),
      fields: { text: "role", value: "id" },
      query: new Query()
    }
  };

  return (
    <Container className="py-4">
      <h3>Users</h3>
      {users.length === 0 ? (
        <p>No users found.</p>
      ) : (
        <div className="col-md-12">
          <GridComponent
            ref={gridRef}
            dataSource={users}
            editSettings={editingSettings}
            toolbar={toolbarOptions}
            actionBegin={actionBegin}
            actionComplete={actionComplete}
            allowPaging={true}
            pageSettings={pageSettings}
            allowSorting={true}
            sortSettings={sortSettings}
            allowFiltering={true}
            filterSettings={filterSettings}
          >
            <ColumnsDirective>
              <ColumnDirective field="id" isPrimaryKey={true} visible={false} />
              <ColumnDirective field="email" headerText="Email" width="200" textAlign="Left" allowEditing={false} />
              <ColumnDirective field="firstName" headerText="First Name" width="150" textAlign="Left" allowEditing={false} />
              <ColumnDirective field="lastName" headerText="Last Name" width="150" textAlign="Left" allowEditing={false} />
              <ColumnDirective
                field="role.id"  // Access the nested property
                headerText="Role"
                width="150"
                textAlign="Left"
                editType="dropdownedit"
                edit={dropdownParams}
                template={roleTemplate}
                validationRules={{ required: true }}
              />
              <ColumnDirective field="status" headerText="Status" width="100" textAlign="Left" allowEditing={false} />
              <ColumnDirective
                headerText="Actions"
                width="120"
                textAlign="Center"
                template={commandTemplate}
                allowEditing={false}
              />
            </ColumnsDirective>
            <Inject services={[Page, Edit, Toolbar, Sort, Filter]} />
          </GridComponent>
        </div>
      )}
    </Container>
  );
};

export default UsersTab;