import React, { useEffect, useState, useRef } from 'react';
import { Container } from 'react-bootstrap';
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
import { DropDownListComponent } from '@syncfusion/ej2-react-dropdowns';

const UsersTab = () => {
  const [users, setUsers] = useState([]);
  const [activeRoles, setActiveRoles] = useState([]);
  const [isEditing, setIsEditing] = useState(false);
  const gridRef = useRef(null);

  useEffect(() => {
    fetchUsers();
    fetchActiveRoles();
  }, []);

  // Fetch users and map to include roleId for grid binding
  const fetchUsers = async () => {
    try {
      const response = await fetch('/api/users', { credentials: 'include' });
      if (response.ok) {
        const data = await response.json();
        const mappedData = data.map(user => ({
          ...user,
          roleId: user.role ? user.role.id : ''
        }));
        setUsers(mappedData);
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
    console.log('Disable user with ID:', user.id);
    try {
      const response = await fetch(`/api/users/${user.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'text/plain' },
        credentials: 'include',
        body: 'disabled'
      });
      if (response.ok) {
        console.log('User disabled successfully');
        await fetchUsers();
      } else {
        console.error('Error disabling user');
      }
    } catch (error) {
      console.error('Error disabling user:', error);
    }
  };

  // Enable user (set status to "active")
  const handleEnableUser = async (user) => {
    console.log('Enable user with ID:', user.id);
    try {
      const response = await fetch(`/api/users/${user.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'text/plain' },
        credentials: 'include',
        body: 'active'
      });
      if (response.ok) {
        console.log('User enabled successfully');
        await fetchUsers();
      } else {
        console.error('Error enabling user');
      }
    } catch (error) {
      console.error('Error enabling user:', error);
    }
  };

  // Only allow editing of the Role column
  const editingSettings = {
    allowEditing: true,
    allowAdding: false,  // no adding
    allowDeleting: false,
    mode: 'Normal'
  };

  // Toolbar shows "Edit" when not editing, and "Update" / "Cancel" when editing.
  const toolbarOptions = isEditing ? ['Update', 'Cancel'] : ['Edit'];

  // Cancel any unexpected "add" request so no empty row appears
  const actionBegin = (args) => {
    if (args.requestType === 'beginEdit') {
      setIsEditing(true);
    }
    if (args.requestType === 'add') {
      // We do not allow adding users on this tab
      args.cancel = true;
    }
  };

  // On save, update the user's role via the API.
  const actionComplete = async (args) => {
    if (args.requestType === 'save' && args.action === 'edit') {
      try {
        const updatedUser = args.data;
        const response = await fetch(`/api/users/${updatedUser.id}/role`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify({
            userRoleId: Number(updatedUser.roleId)
          })
        });
        if (response.ok) {
          await fetchUsers();
        } else {
          console.error('Error updating user role');
        }
      } catch (error) {
        console.error('Error updating user role:', error);
      }
      setIsEditing(false);
    }
    if (args.requestType === 'cancel') {
      setIsEditing(false);
    }
  };

  // Template for displaying the Role text (instead of roleId) in non-edit mode.
  const roleTemplate = (rowData) => {
    return rowData.role ? rowData.role.role : '';
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
            pageSettings={{ pageSize: 10, pageSizes: ['10', '25', '50', 'All'] }}
            allowSorting={true}
            allowFiltering={true}
          >
            <ColumnsDirective>
              <ColumnDirective field="id" isPrimaryKey={true} visible={false} />
              <ColumnDirective
                field="email"
                headerText="Email"
                width="200"
                textAlign="Left"
                allowEditing={false}
              />
              <ColumnDirective
                field="firstName"
                headerText="First Name"
                width="150"
                textAlign="Left"
                allowEditing={false}
              />
              <ColumnDirective
                field="lastName"
                headerText="Last Name"
                width="150"
                textAlign="Left"
                allowEditing={false}
              />
              <ColumnDirective
                field="roleId"
                headerText="Role"
                width="150"
                textAlign="Left"
                editType="dropdownedit"
                edit={{ params: { value: 'id', text: 'role', dataSource: activeRoles } }}
                template={roleTemplate}
                validationRules={{ required: true }}
              />
              <ColumnDirective
                field="status"
                headerText="Status"
                width="100"
                textAlign="Left"
                allowEditing={false}
              />
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
