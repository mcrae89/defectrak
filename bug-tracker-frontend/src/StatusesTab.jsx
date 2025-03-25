import React, { useEffect, useState } from 'react';
import { Container, Table, Button } from 'react-bootstrap';

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

  return (
    <Container className="py-4">
      <h3>Statuses</h3>
      {statuses.length === 0 ? (
        <p>No statuses found.</p>
      ) : (
        <Table striped bordered hover responsive className="mt-3">
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
                  <Button variant="warning" size="sm" onClick={() => handleEditStatus(stat.id)}>
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

export default StatusesTab;
