import React, { useState, useEffect } from 'react';
import { Card, Modal, Button, Container, Row, Col } from 'react-bootstrap';

const BugList = () => {
  const [bugs, setBugs] = useState([]);
  const [selectedBug, setSelectedBug] = useState(null);
  const [showModal, setShowModal] = useState(false);

  // Fetch all bugs from the API
  useEffect(() => {
    fetch('/api/bugs')
      .then(response => response.json())
      .then(data => setBugs(data))
      .catch(error => console.error('Error fetching bugs:', error));
  }, []);

  const handleCardClick = (bug) => {
    setSelectedBug(bug);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedBug(null);
  };

  return (
    <div>
      <Container>
        <Row>
          {bugs.map(bug => (
            console.log(bug),
            <Col key={bug.id} sm={12} md={6} lg={4} className="mb-3">
              <Card onClick={() => handleCardClick(bug)} style={{ cursor: 'pointer' }}>
                <Card.Body>
                  <Card.Title>{bug.title}</Card.Title>
                  <Card.Text>
                    {bug.description ? bug.description.substring(0, 255) : ''}
                  </Card.Text>
                  <Card.Text>
                    <strong>Priority:</strong> {bug.priority ? bug.priority.level : '-'}<br />
                    <strong>Status:</strong> {bug.status ? bug.status.statusLabel : '-'}<br />
                    <strong>Assignee:</strong> {bug.assignee ? bug.assignee.firstName : '-'}
                  </Card.Text>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      </Container>

      {/* Modal for displaying full bug details */}
      <Modal show={showModal} onHide={handleCloseModal} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>{selectedBug && selectedBug.title}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {selectedBug && (
            <div>
              <p><strong>Description:</strong></p>
              <div
                style={{
                  maxHeight: '200px',
                  overflowY: 'auto',
                  border: '1px solid #ddd',
                  padding: '10px'
                }}
              >
                {selectedBug.description}
              </div>
              <p><strong>Priority:</strong> {selectedBug.priority ? selectedBug.priority.level : '-'}</p>
              <p><strong>Status:</strong> {selectedBug.status ? selectedBug.status.statusLabel : '-'}</p>
              <p><strong>Assignee:</strong> {selectedBug.assignee ? `${selectedBug.assignee.firstName} ${selectedBug.assignee.lastName}` : '-'}</p>
              {selectedBug.createdBy && (
                <p><strong>Created By:</strong> {`${selectedBug.createdBy.firstName} ${selectedBug.createdBy.lastName}`}</p>
              )}
              {selectedBug.createdAt && (
                <p><strong>Created At:</strong> {new Date(selectedBug.createdAt).toLocaleString()}</p>
              )}
            </div>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCloseModal}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default BugList;