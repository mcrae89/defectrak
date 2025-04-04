import React, { createContext, useRef, useEffect } from 'react';
import ReactDOM from 'react-dom';
import { ToastComponent } from '@syncfusion/ej2-react-notifications';

export const ToastContext = createContext();

export const ToastProvider = ({ children }) => {
  const toastRef = useRef(null);

  useEffect(() => {
    return () => {
      if (toastRef.current) {
        toastRef.current.hide();
      }
    };
  }, []);

  const showToast = (message, type = 'warning') => {
    if (toastRef.current) {
      toastRef.current.show({
        content: message,
        icon: `e-${type}`,         // e.g., "e-warning", "e-success", etc.
        cssClass: `e-toast-${type}`
      });
    }
  };

  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}
      {ReactDOM.createPortal(
        <ToastComponent ref={toastRef} position={{ X: 'Right', Y: 'Top' }} />,
        document.body
      )}
    </ToastContext.Provider>
  );
};
