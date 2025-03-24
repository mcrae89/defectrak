import React, { useEffect, useState } from 'react';

const PrioritiesTab = () => {
  const [priorities, setPriorities] = useState([]);

  useEffect(() => {
    const fetchPriorities = async () => {
      try {
        const response = await fetch('/api/priorities', { credentials: 'include' });
        if (response.ok) {
          const data = await response.json();
          setPriorities(data);
        } else {
          console.error('Error fetching priorities');
        }
      } catch (error) {
        console.error('Error fetching priorities:', error);
      }
    };
    fetchPriorities();
  }, []);

  const handleEditPriority = (priorityId) => {
    console.log('Edit priority with ID:', priorityId);
    // TODO: implement update logic or open an edit modal
  };

  if (!priorities.length) return <p>No priorities found.</p>;

  return (
    <div>
      <h3>Priorities</h3>
      <table className="table table-striped custom-border">
        <thead>
          <tr>
            <th>Level</th>
            <th>Status</th>
            <th>Edit</th>
          </tr>
        </thead>
        <tbody>
          {priorities.map((priority) => (
            <tr key={priority.id}>
              <td>{priority.level}</td>
              <td>{priority.status}</td>
              <td>
                <button
                  className="btn btn-sm btn-warning"
                  onClick={() => handleEditPriority(priority.id)}
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

export default PrioritiesTab;