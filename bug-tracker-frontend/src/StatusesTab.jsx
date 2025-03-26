import React, { useEffect, useState } from 'react';
import { Container, Table, Button, Form } from 'react-bootstrap';

const StatusesTab = () => {
  const [statuses, setStatuses] = useState([]);
  const [editingStatusId, setEditingStatusId] = useState(null);
  const [editingStatusLabel, setEditingStatusLabel] = useState('');
  const [editingStatus, setEditingStatus] = useState('');

  useEffect(() => {
    fetchStatuses();
  }, []);

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

  const handleStartEditing = (status) => {
    setEditingStatusId(status.id);
    setEditingStatusLabel(status.statusLabel);
    setEditingStatus(status.status);
  };

  const handleCancelEdit = () => {
    setEditingStatusId(null);
    setEditingStatusLabel('');
    setEditingStatus('');
  };

  // Save the updated status label
  const handleSaveEdit = async (statusId) => {
    try {
      const response = await fetch(`/api/statuses/${statusId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
          statusLabel: editingStatusLabel,
          status: editingStatus
        })
      });

      if (response.ok) {
        console.log('Status updated successfully');
        await fetchStatuses();
        handleCancelEdit();
      } else {
        console.error('Error updating status');
      }
    } catch (error) {
      console.error('Error updating status:', error);
    }
  };

  // Dummy implementations for disable/enable
  const handleDisableStatus = async (status) => {
    console.log('Disable status with ID:', status.id);
    try {
      const response = await fetch(`/api/statuses/${status.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
          statusLabel: status.statusLabel,
          status: 'disabled'
        })
      });

      if (response.ok) {
        console.log('Status updated successfully');
        await fetchStatuses();
        handleCancelEdit();
      } else {
        console.error('Error updating status');
      }
    } catch (error) {
      console.error('Error updating status:', error);
    }
  };

  const handleEnableStatus = async (status) => {
    console.log('Enable status with ID:', status.id);
    try {
      const response = await fetch(`/api/statuses/${status.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
          statusLabel: status.statusLabel,
          status: 'active'
        })
      });

      if (response.ok) {
        console.log('Status updated successfully');
        await fetchStatuses();
        handleCancelEdit();
      } else {
        console.error('Error updating status');
      }
    } catch (error) {
      console.error('Error updating status:', error);
    }
  };

  return (
    <Container className="py-4">
      <h3>Statuses</h3>
      {statuses.length === 0 ? (
        <p>No statuses found.</p>
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
              <col style={{ width: '40%' }} />   {/* Label */}
              <col style={{ width: '40%' }} />   {/* Status */}
              <col style={{ width: '20%' }} />   {/* Edit */}
            </colgroup>

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
                  <td>
                    {editingStatusId === stat.id ? (
                      <Form.Control
                        type="text"
                        value={editingStatusLabel}
                        onChange={(e) => setEditingStatusLabel(e.target.value)}
                        size="sm"
                        style={{
                          width: '200px',
                          overflow: 'hidden',
                          textOverflow: 'ellipsis'
                        }}
                      />
                    ) : (
                      stat.statusLabel
                    )}
                  </td>
                  <td>{stat.status}</td>
                  <td>
                    {editingStatusId === stat.id ? (
                      // Editing mode: check (save) and X (cancel)
                      <span style={{ display: 'inline-flex', gap: '1em' }}>
                        <Button
                          variant="link"
                          size="sm"
                          onClick={() => handleSaveEdit(stat.id)}
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
                          onClick={(e) => handleStartEditing(stat, e)}
                          style={{ padding: 0, border: 'none', background: 'none' }}
                        >
                          <i className="bi bi-pencil-square" style={{ fontSize: '1rem' }}></i>
                        </Button>
                        {stat.status === 'active' ? (
                          <Button
                            variant="link"
                            size="sm"
                            onClick={() => handleDisableStatus(stat)}
                            style={{ padding: 0, border: 'none', background: 'none' }}
                          >
                            <i className="bi bi-trash" style={{ fontSize: '1rem' }}></i>
                          </Button>
                        ) : (
                          <Button
                            variant="link"
                            size="sm"
                            onClick={() => handleEnableStatus(stat)}
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

export default StatusesTab;