import React, { useEffect, useState } from 'react';
import { Container, Table, Button, Form } from 'react-bootstrap';

const RolesTab = () => {
  const [roles, setRoles] = useState([]);
  const [editingRoleId, setEditingRoleId] = useState(null);
  const [editingRole, setEditingRole] = useState('');
  const [editingStatus, setEditingStatus] = useState('');

  useEffect(() => {
    fetchRoles();
  }, []);

  const fetchRoles = async () => {
    try {
      const response = await fetch('/api/user-roles', { credentials: 'include' });
      if (response.ok) {
        const data = await response.json();
        setRoles(data);
      } else {
        console.error('Error fetching roles');
      }
    } catch (error) {
      console.error('Error fetching roles:', error);
    }
  };

  const handleStartEditing = (role) => {
    setEditingRoleId(role.id);
    setEditingRole(role.role);
    setEditingStatus(role.status);
  };

  const handleCancelEdit = () => {
    setEditingRoleId(null);
    setEditingRole('');
    setEditingStatus('');
  };

  // Save the updated role role
  const handleSaveEdit = async (roleId) => {
    try {
      const response = await fetch(`/api/user-roles/${roleId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
          role: editingRole,
          status: editingStatus
        })
      });

      if (response.ok) {
        console.log('Priority updated successfully');
        await fetchRoles();
        handleCancelEdit();
      } else {
        console.error('Error updating role');
      }
    } catch (error) {
      console.error('Error updating role:', error);
    }
  };

  // Dummy implementations for disable/enable
  const handleDisableRole = async (role) => {
    console.log('Disable role with ID:', role.id);
    try {
      const response = await fetch(`/api/user-roles/${role.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
          role: role.role,
          status: 'disabled'
        })
      });

      if (response.ok) {
        console.log('Priority updated successfully');
        await fetchRoles();
        handleCancelEdit();
      } else {
        console.error('Error updating role');
      }
    } catch (error) {
      console.error('Error updating role:', error);
    }
  };

  const handleEnableRole = async (role) => {
    console.log('Enable role with ID:', role.id);
    try {
      const response = await fetch(`/api/user-roles/${role.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
          role: role.role,
          status: 'active'
        })
      });

      if (response.ok) {
        console.log('Priority updated successfully');
        await fetchRoles();
        handleCancelEdit();
      } else {
        console.error('Error updating role');
      }
    } catch (error) {
      console.error('Error updating role:', error);
    }
  };

  return (
    <Container className="py-4">
      <h3>Roles</h3>
      {roles.length === 0 ? (
        <p>No roles found.</p>
      ) : (
        <div className='col-md-6'>
          <Table
            striped
            bordered
            hover
            className="mt-3"
            style={{ tableLayout: 'fixed', width: '100%' }}
          >

            <colgroup>
              <col style={{ width: '40%' }} />   {/* Role */}
              <col style={{ width: '40%' }} />   {/* Status */}
              <col style={{ width: '20%' }} />   {/* Edit */}
            </colgroup>

            <thead>
              <tr>
                <th>Role</th>
                <th>Status</th>
                <th>Edit</th>
              </tr>
            </thead>
            <tbody>
              {roles.map((role) => (
                <tr key={role.id}>
                  <td>
                    {editingRoleId === role.id ? (
                      <Form.Control
                        type="text"
                        value={editingRole}
                        onChange={(e) => setEditingRole(e.target.value)}
                        size="sm"
                        style={{
                          width: '200px',
                          overflow: 'hidden',
                          textOverflow: 'ellipsis'
                        }}
                      />
                    ) : (
                      role.role
                    )}
                  </td>
                  <td>{role.status}</td>
                  <td>
                    {editingRoleId === role.id ? (
                      // Editing mode: check (save) and X (cancel)
                      <span style={{ display: 'inline-flex', gap: '1em' }}>
                        <Button
                          variant="link"
                          size="sm"
                          onClick={() => handleSaveEdit(role.id)}
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
                      // Viewing mode: pencil icon + trash/plus icon
                      <span style={{ display: 'inline-flex', gap: '1em' }}>
                        <Button
                          variant="link"
                          size="sm"
                          onClick={(e) => handleStartEditing(role)}
                          style={{ padding: 0, border: 'none', background: 'none' }}
                        >
                          <i className="bi bi-pencil-square" style={{ fontSize: '1rem' }}></i>
                        </Button>
                        {role.status === 'active' ? (
                          <Button
                            variant="link"
                            size="sm"
                            onClick={() => handleDisableRole(role)}
                            style={{ padding: 0, border: 'none', background: 'none' }}
                          >
                            <i className="bi bi-trash" style={{ fontSize: '1rem' }}></i>
                          </Button>
                        ) : (
                          <Button
                            variant="link"
                            size="sm"
                            onClick={() => handleEnableRole(role)}
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

export default RolesTab;