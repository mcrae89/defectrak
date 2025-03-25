import React, { useEffect, useState } from 'react';
import { Container, Table, Button } from 'react-bootstrap';

const RolesTab = () => {
  const [roles, setRoles] = useState([]);

  useEffect(() => {
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
    fetchRoles();
  }, []);

  const handleEditRole = (roleId) => {
    console.log('Edit role with ID:', roleId);
    // TODO: Implement update logic or open an edit modal for the role
  };

  return (
    <Container className="py-4">
      <h3>Roles</h3>
      {roles.length === 0 ? (
        <p>No roles found.</p>
      ) : (
        <Table striped bordered hover responsive className="mt-3">
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
                <td>{role.role}</td>
                <td>{role.status}</td>
                <td>
                  <Button
                    variant="warning"
                    size="sm"
                    onClick={() => handleEditRole(role.id)}
                  >
                    Edit
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}
    </Container>
  );
};

export default RolesTab;
