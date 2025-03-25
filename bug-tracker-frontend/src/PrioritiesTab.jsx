import React, { useEffect, useState } from 'react';
import { Container, Table, Button } from 'react-bootstrap';

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

  return (
    <Container className="py-4">
      <h3>Priorities</h3>
      {priorities.length === 0 ? (
        <p>No priorities found.</p>
      ) : (
        <Table striped bordered hover responsive className="mt-3">
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
                  <Button variant="warning" size="sm" onClick={() => handleEditPriority(priority.id)}>
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

export default PrioritiesTab;
