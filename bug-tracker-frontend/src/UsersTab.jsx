import React, { useEffect, useState } from 'react';
import { Container, Table, Button, Form } from 'react-bootstrap';

const UsersTab = () => {
  const [users, setUsers] = useState([]);
  const [activeRoles, setActiveRoles] = useState([]);
  const [editingUserId, setEditingUserId] = useState(null);
  const [editingUserRole, setEditingUserRole] = useState('');

  useEffect(() => {
    fetchUsers();
    fetchActiveRoles();
  }, []);

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

  // Fetch all roles and filter to active roles only
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

  // Begin editing the user's role
  const handleEditUser = (user) => {
    setEditingUserId(user.id);
    // Assuming user.role is an object with an id; adjust if needed
    setEditingUserRole(user.role?.id || '');
  };

  const handleCancelEdit = () => {
    setEditingUserId(null);
    setEditingUserRole('');
  };

  // Save the updated user role
  const handleSaveEditUser = async (user) => {
    try {
      // Find the full role object from activeRoles using the selected id
      const updatedRole = activeRoles.find(role => role.id === editingUserRole);
      const response = await fetch(`/api/users/${user.id}/role`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
          userRoleId: Number(editingUserRole)
        })        
      });
      if (response.ok) {
        console.log('User updated successfully');
        await fetchUsers();
        handleCancelEdit();
      } else {
        console.error('Error updating user');
      }
    } catch (error) {
      console.error('Error updating user:', error);
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

  return (
    <Container className="py-4">
      <h3>Users</h3>
      {users.length === 0 ? (
        <p>No users found.</p>
      ) : (
        <div className="col-md-8">
          <Table
            striped
            bordered
            hover
            className="mt-3"
            style={{ tableLayout: 'fixed', width: '100%' }}
          >
            <colgroup>
              <col style={{ width: '25%' }} />
              <col style={{ width: '25%' }} />
              <col style={{ width: '25%' }} />
              <col style={{ width: '15%' }} />
              <col style={{ width: '10%' }} />
            </colgroup>
            <thead>
              <tr>
                <th>Email</th>
                <th>Name</th>
                <th>Role</th>
                <th>Status</th>
                <th>Edit</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user) => (
                <tr key={user.id}>
                  <td>{user.email}</td>
                  <td>
                    {user.firstName} {user.lastName}
                  </td>
                  <td>
                    {editingUserId === user.id ? (
                      <Form.Control
                        as="select"
                        size="sm"
                        value={editingUserRole}
                        onChange={(e) => setEditingUserRole(e.target.value)}
                      >
                        <option value="">Select Role</option>
                        {activeRoles.map((role) => (
                          <option key={role.id} value={role.id}>
                            {role.role}
                          </option>
                        ))}
                      </Form.Control>
                    ) : (
                      user.role?.role
                    )}
                  </td>
                  <td>{user.status}</td>
                  <td>
                    {editingUserId === user.id ? (
                      <span style={{ display: 'inline-flex', gap: '1em' }}>
                        <Button
                          variant="link"
                          size="sm"
                          onClick={() => handleSaveEditUser(user)}
                          style={{ padding: 0, border: 'none', background: 'none' }}
                        >
                          <i className="bi bi-check-lg" style={{ fontSize: '1rem' }}></i>
                        </Button>
                        <Button
                          variant="link"
                          size="sm"
                          onClick={handleCancelEdit}
                          style={{ padding: 0, border: 'none', background: 'none' }}
                        >
                          <i className="bi bi-x-lg" style={{ fontSize: '1rem' }}></i>
                        </Button>
                      </span>
                    ) : (
                      <span style={{ display: 'inline-flex', gap: '1em' }}>
                        <Button
                          variant="link"
                          size="sm"
                          onClick={() => handleEditUser(user)}
                          style={{ padding: 0, border: 'none', background: 'none' }}
                        >
                          <i className="bi bi-pencil-square" style={{ fontSize: '1rem' }}></i>
                        </Button>
                        {user.status === 'active' ? (
                          <Button
                            variant="link"
                            size="sm"
                            onClick={() => handleDisableUser(user)}
                            style={{ padding: 0, border: 'none', background: 'none' }}
                          >
                            <i className="bi bi-trash" style={{ fontSize: '1rem' }}></i>
                          </Button>
                        ) : (
                          <Button
                            variant="link"
                            size="sm"
                            onClick={() => handleEnableUser(user)}
                            style={{ padding: 0, border: 'none', background: 'none' }}
                          >
                            <i className="bi bi-plus-lg" style={{ fontSize: '1rem' }}></i>
                          </Button>
                        )}
                      </span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        </div>
      )}
    </Container>
  );
};

export default UsersTab;
