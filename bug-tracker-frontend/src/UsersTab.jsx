import React, { useEffect, useState } from 'react';
import { Container, Table, Button } from 'react-bootstrap';

const UsersTab = () => {
  const [users, setUsers] = useState([]);

  useEffect(() => {
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

    fetchUsers();
  }, []);

  const handleEditUser = (userId) => {
    console.log('Edit user with ID:', userId);
    // TODO: Implement update logic or open an edit modal for the user
  };

  return (
    <Container className="py-4">
      <h3>Users</h3>
      {users.length === 0 ? (
        <p>No users found.</p>
      ) : (
        <Table striped bordered hover responsive className="mt-3">
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
                <td>{user.role?.role}</td>
                <td>{user.status}</td>
                <td>
                  <Button
                    variant="warning"
                    size="sm"
                    onClick={() => handleEditUser(user.id)}
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

export default UsersTab;