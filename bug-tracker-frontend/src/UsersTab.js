import React, { useEffect, useState } from 'react';

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

  if (!users.length) return <p>No users found.</p>;

  return (
    <div>
      <h3>Users</h3>
      <table className="table table-striped custom-border">
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
              <td>{user.firstName} {user.lastName}</td>
              <td>{user.role?.role}</td>
              <td>{user.status}</td>
              <td>
                <button
                  className="btn btn-sm btn-warning"
                  onClick={() => handleEditUser(user.id)}
                >
                  Edit
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default UsersTab;