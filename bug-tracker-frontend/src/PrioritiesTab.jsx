import React, { useEffect, useState } from 'react';
import { Container, Table, Button, Form } from 'react-bootstrap';

const PrioritiesTab = () => {
  const [priorities, setPriorities] = useState([]);
  const [editingPriorityId, setEditingPriorityId] = useState(null);
  const [editingLevel, setEditingLevel] = useState('');
  const [editingStatus, setEditingStatus] = useState('');

  useEffect(() => {
    fetchPriorities();
  }, []);

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

  const handleStartEditing = (priority) => {
    setEditingPriorityId(priority.id);
    setEditingLevel(priority.level);
    setEditingStatus(priority.status);
  };

  const handleCancelEdit = () => {
    setEditingPriorityId(null);
    setEditingLevel('');
    setEditingStatus('');
  };

  // Save the updated priority level
  const handleSaveEdit = async (priorityId) => {
    try {
      const response = await fetch(`/api/priorities/${priorityId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
          level: editingLevel,
          status: editingStatus
        })
      });

      if (response.ok) {
        console.log('Priority updated successfully');
        await fetchPriorities();
        handleCancelEdit();
      } else {
        console.error('Error updating priority');
      }
    } catch (error) {
      console.error('Error updating priority:', error);
    }
  };

  // Dummy implementations for disable/enable
  const handleDisablePriority = async (priority) => {
    console.log('Disable priority with ID:', priority.id);
    try {
      const response = await fetch(`/api/priorities/${priority.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
          level: priority.level,
          status: 'disabled'
        })
      });

      if (response.ok) {
        console.log('Priority updated successfully');
        await fetchPriorities();
        handleCancelEdit();
      } else {
        console.error('Error updating priority');
      }
    } catch (error) {
      console.error('Error updating priority:', error);
    }
  };

  const handleEnablePriority = async (priority) => {
    console.log('Enable priority with ID:', priority.id);
    try {
      const response = await fetch(`/api/priorities/${priority.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
          level: priority.level,
          status: 'active'
        })
      });

      if (response.ok) {
        console.log('Priority updated successfully');
        await fetchPriorities();
        handleCancelEdit();
      } else {
        console.error('Error updating priority');
      }
    } catch (error) {
      console.error('Error updating priority:', error);
    }
  };

  return (
    <Container className="py-4">
      <h3>Priorities</h3>
      {priorities.length === 0 ? (
        <p>No priorities found.</p>
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
              <col style={{ width: '40%' }} />   {/* Level */}
              <col style={{ width: '40%' }} />   {/* Status */}
              <col style={{ width: '20%' }} />   {/* Edit */}
            </colgroup>

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
                  <td>
                    {editingPriorityId === priority.id ? (
                      <Form.Control
                        type="text"
                        value={editingLevel}
                        onChange={(e) => setEditingLevel(e.target.value)}
                        size="sm"
                        style={{
                          width: '200px',
                          overflow: 'hidden',
                          textOverflow: 'ellipsis'
                        }}
                      />
                    ) : (
                      priority.level
                    )}
                  </td>
                  <td>{priority.status}</td>
                  <td>
                    {editingPriorityId === priority.id ? (
                      // Editing mode: check (save) and X (cancel)
                      <span style={{ display: 'inline-flex', gap: '1em' }}>
                        <Button
                          variant="link"
                          size="sm"
                          onClick={() => handleSaveEdit(priority.id)}
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
                          onClick={(e) => handleStartEditing(priority)}
                          style={{ padding: 0, border: 'none', background: 'none' }}
                        >
                          <i className="bi bi-pencil-square" style={{ fontSize: '1rem' }}></i>
                        </Button>
                        {priority.status === 'active' ? (
                          <Button
                            variant="link"
                            size="sm"
                            onClick={() => handleDisablePriority(priority)}
                            style={{ padding: 0, border: 'none', background: 'none' }}
                          >
                            <i className="bi bi-trash" style={{ fontSize: '1rem' }}></i>
                          </Button>
                        ) : (
                          <Button
                            variant="link"
                            size="sm"
                            onClick={() => handleEnablePriority(priority)}
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

export default PrioritiesTab;