import React, { useState, useEffect, useRef } from 'react';
import { Container } from 'react-bootstrap';
import {
  GridComponent,
  ColumnsDirective,
  ColumnDirective,
  Edit,
  Inject,
  Toolbar,
  Page,
  Sort,
  Filter
} from '@syncfusion/ej2-react-grids';

const PrioritiesTab = () => {
  const [priorities, setPriorities] = useState([]);
  const [isEditing, setIsEditing] = useState(false);
  const gridRef = useRef(null);

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

  // Inline editing & adding settings.
  const editingSettings = {
    allowEditing: true,
    allowAdding: true,
    allowDeleting: false, // Deletion is handled in the Actions column
    mode: 'Normal',
    newRowPosition: 'Top'
  };

  // When not editing, show Add and Edit. When editing/adding, show Update and Cancel.
  const toolbarOptions = isEditing ? ['Update', 'Cancel'] : ['Add', 'Edit'];
  const pageSettings = { pageSize: 10, pageSizes: ['10', '25', '50', 'All'] };
  const sortSettings = { columns: [] };
  const filterSettings = { type: 'Menu' };

  // Change toolbar state when entering edit/add mode.
  const actionBegin = (args) => {
    if (args.requestType === 'beginEdit' || args.requestType === 'add') {
      setIsEditing(true);
    }
    if ((args.requestType === 'beginEdit' || args.requestType === 'add') && priorities.length === 0) {
      args.cancel = true;
    }
  };

  // Process save, cancel, and delete actions.
  const actionComplete = async (args) => {
    if (args.requestType === 'save' && args.action === 'add') {
      try {
        const response = await fetch('/api/priorities', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify({
            level: args.data.level,
            status: 'active'
          })
        });
        if (response.ok) {
          await fetchPriorities();
        }
      } catch (error) {
        console.error('Error adding priority:', error);
      }
      setIsEditing(false);
    }

    if (args.requestType === 'save' && args.action === 'edit') {
      try {
        const response = await fetch(`/api/priorities/${args.data.id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify({
            level: args.data.level,
            status: args.data.status
          })
        });
        if (response.ok) {
          await fetchPriorities();
        }
      } catch (error) {
        console.error('Error updating priority:', error);
      }
      setIsEditing(false);
    }

    if (args.requestType === 'cancel') {
      setIsEditing(false);
    }
  };

  // Soft-delete (disable) the priority.
  const handleDelete = async (data) => {
    try {
      const response = await fetch(`/api/priorities/${data.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
          level: data.level,
          status: 'disabled'
        })
      });
      if (response.ok) {
        await fetchPriorities();
      } else {
        console.error('Error disabling priority');
      }
    } catch (error) {
      console.error('Error disabling priority:', error);
    }
  };

  // Enable the priority (set status to active).
  const handleEnable = async (data) => {
    try {
      const response = await fetch(`/api/priorities/${data.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
          level: data.level,
          status: 'active'
        })
      });
      if (response.ok) {
        await fetchPriorities();
      } else {
        console.error('Error enabling priority');
      }
    } catch (error) {
      console.error('Error enabling priority:', error);
    }
  };

  // Template for the Actions column that displays a trashcan icon for active priorities
  // and a plus icon for disabled priorities.
  const commandTemplate = (props) => (
    <>
      {props.status === 'active' ? (
        <button
          className="btn btn-link p-0"
          onClick={() => handleDelete(props)}
          title="Disable"
        >
          <i className="bi bi-trash" style={{ fontSize: '1.25rem'}}></i>
        </button>
      ) : (
        <button
          className="btn btn-link p-0"
          onClick={() => handleEnable(props)}
          title="Enable"
        >
          <i className="bi bi-plus" style={{ fontSize: '1.4rem'}}></i>
        </button>
      )}
    </>
  );

  return (
    <Container className="py-4">
      <h3>Priorities</h3>
      {priorities.length === 0 ? (
        <p>No priorities found.</p>
      ) : (
        <div className="col-md-6" style={{ visibility: priorities.length > 0 ? 'visible' : 'hidden' }}>
          <GridComponent
            key={priorities.length}
            ref={gridRef}
            dataSource={priorities}
            editSettings={editingSettings}
            toolbar={toolbarOptions}
            actionComplete={actionComplete}
            actionBegin={actionBegin}
            allowPaging={true}
            pageSettings={pageSettings}
            allowSorting={true}
            sortSettings={sortSettings}
            allowFiltering={true}
            filterSettings={filterSettings}
            allowSelection={true}
          >
            <ColumnsDirective>
              <ColumnDirective field="id" isPrimaryKey={true} visible={false} />
              <ColumnDirective
                field="level"
                headerText="Level"
                width="150"
                textAlign="Left"
                validationRules={{ required: true }}
              />
              <ColumnDirective
                field="status"
                headerText="Status"
                width="150"
                textAlign="Left"
                allowEditing={false}
              />
              {/* New Actions column for delete/enable icons */}
              <ColumnDirective
                headerText="Actions"
                width="120"
                textAlign="Center"
                template={commandTemplate}
                allowEditing={false}
              />
            </ColumnsDirective>
            <Inject services={[Page, Edit, Toolbar, Sort, Filter]} />
          </GridComponent>
        </div>
      )}
    </Container>
  );
};

export default PrioritiesTab;