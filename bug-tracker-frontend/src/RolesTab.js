import React, { useEffect, useState } from 'react';

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

  if (!roles.length) return <p>No roles found.</p>;

  return (
    <div>
      <h3>Roles</h3>
      <table className="table table-striped custom-border">
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
                <button
                  className="btn btn-sm btn-warning"
                  onClick={() => handleEditRole(role.id)}
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

export default RolesTab;