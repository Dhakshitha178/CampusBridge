import React from "react";
import { Link } from "react-router-dom";

const DriveCard = ({ drive, onApply, applying = false, hasApplied = false }) => {
  const isExpired = drive.applicationDeadline && new Date(drive.applicationDeadline) < new Date();

  return (
    <div className="card shadow-sm border-0 h-100 rounded-3 overflow-hidden drive-card">
      <div className="card-body p-4 d-flex flex-column">
        <div className="d-flex align-items-start justify-content-between mb-3">
          <div className="d-flex align-items-center">
            {drive.companyLogo ? (
              <img
                src={drive.companyLogo}
                alt={drive.companyName}
                className="rounded-3 me-3"
                style={{ width: "48px", height: "48px", objectFit: "contain" }}
              />
            ) : (
              <div
                className="rounded-3 me-3 d-flex align-items-center justify-content-center bg-primary-subtle text-primary fw-bold fs-5"
                style={{ width: "48px", height: "48px" }}
              >
                {drive.companyName ? drive.companyName.charAt(0) : "C"}
              </div>
            )}
            <div>
              <h5 className="card-title fw-bold mb-0 text-dark">{drive.jobTitle}</h5>
              <span className="text-muted small">{drive.companyName}</span>
            </div>
          </div>
          <span className="badge bg-success-subtle text-success border border-success-subtle px-3 py-2 rounded-pill fw-semibold">
            ₹{drive.packageLpa} LPA
          </span>
        </div>

        <p className="text-secondary small mb-3 flex-grow-1 text-truncate-2">
          {drive.jobDescription}
        </p>

        <div className="d-flex flex-wrap gap-2 mb-3">
          <span className="badge bg-light text-dark border">
            <i className="bi bi-geo-alt me-1 text-primary"></i>
            {drive.location} ({drive.workMode})
          </span>
          <span className="badge bg-light text-dark border">
            <i className="bi bi-briefcase me-1 text-primary"></i>
            {drive.employmentType}
          </span>
          <span className="badge bg-light text-dark border">
            <i className="bi bi-people me-1 text-primary"></i>
            {drive.vacancies} Openings
          </span>
        </div>

        {drive.eligibility && (
          <div className="p-2 mb-3 rounded-2 bg-light small">
            <div className="d-flex justify-content-between text-muted">
              <span>Min CGPA: <strong>{drive.eligibility.minCgpa}</strong></span>
              <span>Max Backlogs: <strong>{drive.eligibility.maxBacklogs}</strong></span>
              <span>Passout: <strong>{drive.eligibility.graduationYear}</strong></span>
            </div>
          </div>
        )}

        <div className="mt-auto d-flex gap-2">
          <Link to={`/student/drives/${drive.id}`} className="btn btn-outline-primary btn-sm flex-grow-1">
            View Details
          </Link>
          {hasApplied ? (
            <button className="btn btn-success btn-sm flex-grow-1" disabled>
              <i className="bi bi-check2-circle me-1"></i> Applied
            </button>
          ) : isExpired ? (
            <button className="btn btn-secondary btn-sm flex-grow-1" disabled>
              Expired
            </button>
          ) : (
            <button
              onClick={() => onApply && onApply(drive.id)}
              disabled={applying}
              className="btn btn-primary btn-sm flex-grow-1"
            >
              {applying ? "Applying..." : "Apply Now"}
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default DriveCard;
