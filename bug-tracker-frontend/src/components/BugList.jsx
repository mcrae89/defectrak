import React, { useState, useEffect } from 'react';
import { Modal, Button, Container, Row, Col } from 'react-bootstrap';
import { AutoCompleteComponent, DropDownListComponent } from '@syncfusion/ej2-react-dropdowns';
import { TextBoxComponent } from '@syncfusion/ej2-react-inputs';
import Fuse from 'fuse.js';

const BugList = ({ user }) => {
  const [bugs, setBugs] = useState([]);
  const [selectedBug, setSelectedBug] = useState(null); // null indicates create mode
  const [showModal, setShowModal] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const maxTitleLength = 255;
  const maxDescriptionLength = 4000;
  const cardHeight = '250px';
  const cardContentHeight = '126px';

  // State for form fields (used for both create and edit)
  const [editTitle, setEditTitle] = useState('');
  const [editDescription, setEditDescription] = useState('');
  const [editPriority, setEditPriority] = useState('');
  const [editStatus, setEditStatus] = useState('');
  // editAssignee is stored as an object { id, name }
  const [editAssignee, setEditAssignee] = useState({ id: null, name: '' });

  // Data for priorities, statuses, and users
  const [activePriorities, setActivePriorities] = useState([]);
  const [activeStatuses, setActiveStatuses] = useState([]);
  const [allUsers, setAllUsers] = useState([]); // all active users for assignee autocomplete

  // Fetch bugs from the API
  useEffect(() => {
    fetch('/api/bugs')
      .then(response => response.json())
      .then(data => setBugs(data))
      .catch(error => console.error('Error fetching bugs:', error));
  }, []);

  // Fetch active priorities and statuses when in editing/creating mode
  useEffect(() => {
    if (isEditing) {
      fetch('/api/priorities/active')
        .then(response => response.json())
        .then(data => setActivePriorities(data))
        .catch(error => console.error('Error fetching active priorities:', error));
      fetch('/api/statuses/active')
        .then(response => response.json())
        .then(data => setActiveStatuses(data))
        .catch(error => console.error('Error fetching active statuses:', error));
    }
  }, [isEditing]);

  // Fetch all active users once for the assignee autocomplete
  useEffect(() => {
    fetch('/api/users/active')
      .then(response => response.json())
      .then(data => {
        const mapped = data.map(u => ({
          ...u,
          fullName: `${u.firstName} ${u.lastName}`
        }));
        setAllUsers(mapped);
      })
      .catch(error => console.error('Error fetching active users:', error));
  }, []);

  // Custom filtering event using Fuse.js
  const onFiltering = (e) => {
    if (e.text.length < 3) {
      e.updateData([], null);
      return;
    }
    const options = {
      keys: ['fullName'],
      includeMatches: true,
      findAllMatches: true,
      threshold: 0.4
    };
    const fuse = new Fuse(allUsers, options);
    const result = fuse.search(e.text);
    const filteredData = result.map(item => item.item);
    e.updateData(filteredData, null);
  };

  // Helper to reset edit fields to the currently selected bug or blank for new bug
  const resetEditFields = () => {
    if (selectedBug) {
      setEditTitle(selectedBug.title);
      setEditDescription(selectedBug.description);
      setEditPriority(selectedBug.priority ? selectedBug.priority.id : '');
      setEditStatus(selectedBug.status ? selectedBug.status.id : '');
      setEditAssignee(
        selectedBug.assignee
          ? { id: selectedBug.assignee.id, name: `${selectedBug.assignee.firstName} ${selectedBug.assignee.lastName}` }
          : { id: null, name: '' }
      );
    } else {
      setEditTitle('');
      setEditDescription('');
      setEditPriority('');
      setEditStatus('');
      setEditAssignee({ id: null, name: '' });
    }
  };

  // Opens modal in view/edit mode for an existing bug
  const handleCardClick = (bug) => {
    setSelectedBug(bug);
    setShowModal(true);
    setIsEditing(false); // open in view mode initially
    // Prepopulate fields
    setEditTitle(bug.title);
    setEditDescription(bug.description);
    setEditPriority(bug.priority ? bug.priority.id : '');
    setEditStatus(bug.status ? bug.status.id : '');
    setEditAssignee(
      bug.assignee
        ? { id: bug.assignee.id, name: `${bug.assignee.firstName} ${bug.assignee.lastName}` }
        : { id: null, name: '' }
    );
  };

  const handleEditClick = () => {
    // Switch to edit mode (fields have been prepopulated)
    setIsEditing(true);
  };

  // Opens modal for creating a new bug
  const handleCreateClick = () => {
    setSelectedBug(null); // indicates creation mode
    resetEditFields();
    setIsEditing(true);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedBug(null);
    setIsEditing(false);
    resetEditFields();
  };

  // Cancel editing (reset fields to saved bug values if available)
  const handleCancelEdit = () => {
    resetEditFields();
    setIsEditing(false);
  };

  // Save updated bug (edit mode)
  const handleSaveClick = async (e) => {
    e.preventDefault();
    const updatedBug = {
      ...selectedBug,
      title: editTitle,
      description: editDescription,
      priorityId: editPriority,
      statusId: editStatus,
      assigneeId: editAssignee.id,
    };
    try {
      const response = await fetch(`/api/bugs/${updatedBug.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(updatedBug)
      });
      if (response.ok) {
        const updatedBugFromServer = await response.json();
        setBugs(bugs.map(b => b.id === updatedBugFromServer.id ? updatedBugFromServer : b));
        setSelectedBug(updatedBugFromServer);
      } else {
        const errorData = await response.json();
        console.error('Error during updating:', errorData);
      }
    } catch (error) {
      console.error('Error during updating:', error);
    }
    setIsEditing(false);
    setShowModal(false);
  };

  // Create a new bug (create mode)
  const handleCreateBug = async (e) => {
    e.preventDefault();
    const newBug = {
      title: editTitle,
      description: editDescription,
      priorityId: editPriority,
      statusId: editStatus,
      assigneeId: editAssignee.id,
      createdByUserId: user.id
    };
    try {
      const response = await fetch('/api/bugs', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(newBug)
      });
      if (response.ok) {
        const createdBug = await response.json();
        setBugs([...bugs, createdBug]);
        setShowModal(false);
      } else {
        const errorData = await response.json();
        console.error('Error during creation:', errorData);
      }
    } catch (error) {
      console.error('Error during creation:', error);
    }
  };

  return (
    <div>
      <Container>
        <Row>
          {/* Create Bug Card using Syncfusion card styles */}
          <Col sm={12} md={6} lg={4} className="mb-3">
            <div onClick={handleCreateClick} style={{ cursor: 'pointer', height: cardHeight }}>
              <div className="e-card" id="create_bug_card" style={{ borderRadius: '8px', height: '100%' }}>
                <div className="e-card-header">
                  <div className="e-card-header-caption" style={{ textAlign: 'center' }}>
                    <div className="e-card-title" style={{ fontSize: '2rem' }}>
                      <i className="bi bi-plus"></i>
                    </div>
                  </div>
                </div>
                <div className="e-card-content" style={{ textAlign: 'center', height: cardContentHeight }}>
                  Create Bug
                </div>
              </div>
            </div>
          </Col>
          {/* Existing Bug Cards using Syncfusion card styles */}
          {bugs.map((bug) => (
            <Col key={bug.id} sm={12} md={6} lg={4} className="mb-3">
              <div onClick={() => handleCardClick(bug)} style={{ cursor: 'pointer', height: cardHeight }}>
                <div className="e-card" id={`bug_card_${bug.id}`} style={{ borderRadius: '8px', height: '100%' }}>
                  <div className="e-card-header">
                    <div className="e-card-header-caption">
                      <div className="e-card-title">{bug.title}</div>
                      <div className="e-card-sub-title">
                        Priority: {bug.priority ? bug.priority.level : '-'} | Status: {bug.status ? bug.status.statusLabel : '-'} | Assignee: {bug.assignee ? bug.assignee.firstName : '-'}
                      </div>
                    </div>
                  </div>
                  <div className="e-card-content" style={{ height: cardContentHeight }}>
                    {bug.description ? (bug.description.length > 255 ? bug.description.substring(0, 255) + '...' : bug.description) : ''}
                  </div>
                  <div className="e-card-actions">
                    <button className="e-btn e-outline e-primary" style={{ transform: 'scale(0.75)', transformOrigin: 'center', borderRadius: '8px' }}>
                      View Details
                    </button>
                  </div>
                </div>
              </div>
            </Col>
          ))}
        </Row>
      </Container>

      {/* Modal for Create / View / Edit */}
      <Modal show={showModal} onHide={handleCloseModal} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>
            {isEditing ? (
              <>
                <TextBoxComponent
                  value={editTitle}
                  change={(e) => setEditTitle(e.value)}
                  input={(e) => setEditTitle(e.value)}
                  placeholder="Enter bug title"
                  maxLength={maxTitleLength}
                  floatLabelType="Auto"
                />
                <div style={{ fontSize: '0.8rem', color: '#6c757d' }}>
                  {editTitle.length}/{maxTitleLength}
                </div>
              </>
            ) : (
              selectedBug && selectedBug.title
            )}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <>
            <p><strong>Description:</strong></p>
            {isEditing ? (
              <>
                <TextBoxComponent
                  multiline={true}
                  value={editDescription}
                  change={(e) => setEditDescription(e.value)}
                  input={(e) => setEditDescription(e.value)}
                  placeholder="Enter bug description"
                  maxLength={maxDescriptionLength}
                  floatLabelType="Auto"
                  style={{ height: '200px' }}
                />
                <div style={{ fontSize: '0.8rem', color: '#6c757d' }}>
                  {editDescription.length}/{maxDescriptionLength}
                </div>
              </>
            ) : (
              <div style={{ maxHeight: '200px', overflowY: 'auto' }}>
                {selectedBug && selectedBug.description}
              </div>
            )}

            <p><strong>Priority:</strong></p>
            {isEditing ? (
              <DropDownListComponent
                id="priority-dropdown"
                dataSource={activePriorities}
                fields={{ text: 'level', value: 'id' }}
                placeholder="Select Priority"
                value={editPriority}
                change={(e) => setEditPriority(e.value)}
                popupHeight="220px"
              />
            ) : (
              <p>{selectedBug && selectedBug.priority ? selectedBug.priority.level : '-'}</p>
            )}

            <p><strong>Status:</strong></p>
            {isEditing ? (
              <DropDownListComponent
                id="status-dropdown"
                dataSource={activeStatuses}
                fields={{ text: 'statusLabel', value: 'id' }}
                placeholder="Select Status"
                value={editStatus}
                change={(e) => setEditStatus(e.value)}
                popupHeight="220px"
              />
            ) : (
              <p>{selectedBug && selectedBug.status ? selectedBug.status.statusLabel : '-'}</p>
            )}

            <p><strong>Assignee:</strong></p>
            {isEditing ? (
              <>
                <AutoCompleteComponent
                  id="assignee-autocomplete"
                  dataSource={allUsers}
                  filtering={onFiltering}
                  placeholder="Enter assignee name..."
                  filterType="Contains"
                  allowFiltering={true}
                  fields={{ value: 'fullName' }}
                  value={editAssignee.name}
                  allowCustomValue={true}
                  change={(e) => {
                    if (e.itemData) {
                      setEditAssignee({ id: e.itemData.id, name: e.itemData.fullName });
                    } else {
                      setEditAssignee({ id: null, name: e.value });
                    }
                  }}
                />
              </>
            ) : (
              <p>{selectedBug && selectedBug.assignee ? `${selectedBug.assignee.firstName} ${selectedBug.assignee.lastName}` : '-'}</p>
            )}

            <p><strong>Created By:</strong></p>
            <p>{selectedBug && selectedBug.createdByUserId ? `${selectedBug.createdByUserId.firstName} ${selectedBug.createdByUserId.lastName}` : '-'}</p>
          </>
        </Modal.Body>
        <Modal.Footer>
          {isEditing ? (
            <>
              {selectedBug ? (
                <>
                  <Button variant="primary" onClick={handleSaveClick}>
                    Save
                  </Button>
                  <Button variant="secondary" onClick={handleCancelEdit}>
                    Cancel
                  </Button>
                </>
              ) : (
                <Button variant="primary" onClick={handleCreateBug}>
                  Create
                </Button>
              )}
            </>
          ) : (
            <Button variant="warning" onClick={handleEditClick}>
              Edit
            </Button>
          )}
          <Button variant="secondary" onClick={handleCloseModal}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default BugList;
