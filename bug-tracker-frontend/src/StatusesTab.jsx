import React, { useState, useEffect, useRef, useContext } from 'react';
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
import { ToastContext } from './components/ToastContext';

const StatusesTab = () => {
  const [statuses, setStatuses] = useState([]);
  const [isEditing, setIsEditing] = useState(false);
  const { showToast } = useContext(ToastContext);
  const gridRef = useRef(null);

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
        showToast("Error fetching statuses!", 'error');
      }
    } catch (error) {
      console.error('Error fetching statuses:', error);
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
	if (args.requestType === 'save') {
      const newStatusLabel = args.data.statusLabel;
      if (args.action === 'add') {
        // For new rows, check if any priority already has the same level.
        if (statuses.some(item => item.statusLabel === newStatusLabel)) {
          showToast("Status already exists");
          args.cancel = true;
          return;
        }
      } else if (args.action === 'edit') {
        // For edits, ignore the current row being edited.
        if (statuses.some(item => item.statusLabel === newStatusLabel && item.id !== args.data.id)) {
          showToast("Status already exists");
          args.cancel = true;
          return;
        }
      }
    }
    if (args.requestType === 'beginEdit' || args.requestType === 'add') {
      setIsEditing(true);
    }
    if ((args.requestType === 'beginEdit' || args.requestType === 'add') && statuses.length === 0) {
      args.cancel = true;
    }
  };

  // Process save, cancel, and delete actions.
  const actionComplete = async (args) => {
    if (args.requestType === 'save' && args.action === 'add') {
      try {
        const response = await fetch('/api/statuses', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify({
            statusLabel: args.data.statusLabel,
            status: 'active'
          })
        });
        if (response.ok) {
          await fetchStatuses();
          showToast("Status successfully added!", 'success');
        }
      } catch (error) {
        console.error('Error adding status:', error);
        showToast("Error adding status!", 'error');
      }
      setIsEditing(false);
    }

    if (args.requestType === 'save' && args.action === 'edit') {
      try {
        const response = await fetch(`/api/statuses/${args.data.id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify({
            statusLabel: args.data.statusLabel,
            status: args.data.status
          })
        });
        if (response.ok) {
          await fetchStatuses();
          showToast("Status successfully updated!", 'success');
        }
      } catch (error) {
        console.error('Error updating status:', error);
        showToast("Error updating status!", 'error');
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
      const response = await fetch(`/api/statuses/${data.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
          statusLabel: data.statusLabel,
          status: 'disabled'
        })
      });
      if (response.ok) {
        await fetchStatuses();
        showToast("Status successfully disabled!", 'success');
      } else {
        showToast("Error disabling status!", 'error');
      }
    } catch (error) {
      console.error('Error disabling priority:', error);
    }
  };

  // Enable the priority (set status to active).
  const handleEnable = async (data) => {
    try {
      const response = await fetch(`/api/statuses/${data.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
          statusLabel: data.statusLabel,
          status: 'active'
        })
      });
      if (response.ok) {
        await fetchStatuses();
        showToast("Status successfully enabled!", 'success');
      } else {
        showToast("Error enabling status!", 'error');
      }
    } catch (error) {
      console.error('Error enabling priority:', error);
    }
  };

  // Template for the Actions column that displays a trashcan icon for active statuses
  // and a plus icon for disabled statuses.
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
      <h3>Statuses</h3>
      {statuses.length === 0 ? (
        <p>No statuses found.</p>
      ) : (
        <div className="col-md-6" style={{ visibility: statuses.length > 0 ? 'visible' : 'hidden' }}>
          <GridComponent
            key={statuses.length}
            ref={gridRef}
            dataSource={statuses}
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
                field="statusLabel"
                headerText="Label"
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

export default StatusesTab;