import React from "react";
import ApplicationStatusBadge from "../student/ApplicationStatusBadge";

const ApplicationTable = ({
  applications,
  stages = [],
  onUpdateStatus,
  updatingId = null,
}) => {
  if (!applications || applications.length === 0) {
    return (
      <div className="text-center py-5 text-muted">
        <i className="bi bi-inbox fs-1 d-block mb-2"></i>
        No applicants found.
      </div>
    );
  }

  return (
    <div className="table-responsive">
      <table className="table table-hover align-middle mb-0">
        <thead className="table-light">
          <tr>
            <th>Student</th>
            <th>Department</th>
            <th>CGPA</th>
            <th>Backlogs</th>
            <th>Current Stage</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {applications.map((app) => (
            <tr key={app.id}>
              <td>
                <div className="fw-semibold text-dark">{app.studentName}</div>
                <div className="text-muted small">{app.studentRollNumber}</div>
              </td>
              <td>{app.department}</td>
              <td>
                <span className="badge bg-light text-dark border">{app.cgpa}</span>
              </td>
              <td>{app.backlogs ?? 0}</td>
              <td>
                <span className="badge bg-primary-subtle text-primary border border-primary-subtle">
                  {app.currentStageName || "Stage 1"}
                </span>
              </td>
              <td>
                <ApplicationStatusBadge status={app.status} />
              </td>
              <td>
                <div className="d-flex gap-1">
                  <select
                    className="form-select form-select-sm"
                    style={{ width: "130px" }}
                    value={app.status}
                    disabled={updatingId === app.id}
                    onChange={(e) =>
                      onUpdateStatus && onUpdateStatus(app.id, e.target.value)
                    }
                  >
                    <option value="APPLIED">Applied</option>
                    <option value="SHORTLISTED">Shortlisted</option>
                    <option value="INTERVIEW">Interview</option>
                    <option value="SELECTED">Selected</option>
                    <option value="REJECTED">Rejected</option>
                  </select>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ApplicationTable;
