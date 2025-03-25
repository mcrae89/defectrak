import React, { useState, useRef } from 'react';
import { Dropdown } from 'react-bootstrap';

const HoverDropdown = ({ children, toggleContent }) => {
  const [show, setShow] = useState(false);
  const containerRef = useRef(null);

  const handleMouseEnter = () => setShow(true);

  const handleMouseLeave = (e) => {
    if (containerRef.current && !containerRef.current.contains(e.relatedTarget)) {
      setShow(false);
    }
  };

  return (
    <div
      ref={containerRef}
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
      style={{ position: 'relative', display: 'inline-block' }}
    >
      <Dropdown show={show}>
        <Dropdown.Toggle variant="secondary">
          {toggleContent}
        </Dropdown.Toggle>
        <Dropdown.Menu
          renderMenuOnMount
          popperConfig={{ strategy: 'fixed' }}
          style={{ margin: 0, position: 'absolute', top: '100%', left: 0 }}
        >
          {children}
        </Dropdown.Menu>
      </Dropdown>
    </div>
  );
};

export default HoverDropdown;