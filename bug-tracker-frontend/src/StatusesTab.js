import React, { useEffect, useState } from 'react';

const StatusesTab = () => {
  const [statuses, setStatuses] = useState([]);

  useEffect(() => {
    const fetchStatuses = async () => {
      try {
        const response = await fetch('/api/statuses', { credentials: 'include' });
        if (response.ok) {
          const data = await response.json();
          setStatuses(data);
        } else {
          console.error('Error fetching statuses');
        }
      } catch (error) {
        console.error('Error fetching statuses:', error);
      }
    };
    fetchStatuses();
  }, []);

  const handleEditStatus = (statusId) => {
    console.log('Edit status with ID:', statusId);
    // TODO: implement update logic or open an edit modal
  };

  if (!statuses.length) return <p>No statuses found.</p>;

  return (
    <div>
      <h3>Statuses</h3>
      <table className="table table-striped custom-border">
        <thead>
          <tr>
            <th>Label</th>
            <th>Status</th>
            <th>Edit</th>
          </tr>
        </thead>
        <tbody>
          {statuses.map((stat) => (
            <tr key={stat.id}>
              <td>{stat.statusLabel}</td>
              <td>{stat.status}</td>
              <td>
                <button
                  className="btn btn-sm btn-warning"
                  onClick={() => handleEditStatus(stat.id)}
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

export default StatusesTab;