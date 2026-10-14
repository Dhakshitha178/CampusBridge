import React from "react";

const Loader = ({ message = "Loading data..." }) => {
  return (
    <div className="d-flex flex-column align-items-center justify-content-center p-5 text-muted">
      <div className="spinner-border text-primary mb-3" role="status">
        <span className="visually-hidden">Loading...</span>
      </div>
      <span className="small">{message}</span>
    </div>
  );
};

export default Loader;
