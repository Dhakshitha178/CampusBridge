import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { getStudentApplications, withdrawApplication } from "../../services/studentService";
import Loader from "../../components/common/Loader";

const MyApplicationsPage = () => {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [withdrawingId, setWithdrawingId] = useState(null);
  const [msg, setMsg] = useState({ text: "", type: "" });
  const [viewMode, setViewMode] = useState("tracker"); // 'tracker' or 'list'

  const loadApplications = async () => {
    try {
      setLoading(true);
      const res = await getStudentApplications();
      if (res.success && res.data) {
        setApplications(res.data);
      }
    } catch (err) {
      console.error("Error loading applications:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadApplications();
  }, []);

  const handleWithdraw = async (appId) => {
    if (!window.confirm("Are you sure you want to withdraw this application?")) return;
    setWithdrawingId(appId);
    setMsg({ text: "", type: "" });
    try {
      const res = await withdrawApplication(appId);
      if (res.success) {
        setMsg({ text: "Application withdrawn successfully.", type: "info" });
        loadApplications();
      } else {
        setMsg({ text: res.message || "Failed to withdraw application.", type: "danger" });
      }
    } catch (err) {
      setMsg({
        text: err.response?.data?.message || "Failed to withdraw application.",
        type: "danger",
      });
    } finally {
      setWithdrawingId(null);
    }
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case "SELECTED":
        return <span className="badge bg-success">SELECTED / OFFERED</span>;
      case "SHORTLISTED":
        return <span className="badge bg-info text-dark">SHORTLISTED</span>;
      case "ASSESSMENT":
        return <span className="badge bg-primary">ASSESSMENT ROUND</span>;
      case "INTERVIEW":
        return <span className="badge bg-warning text-dark">INTERVIEW STAGE</span>;
      case "UNDER_REVIEW":
        return <span className="badge bg-secondary">UNDER REVIEW</span>;
      case "REJECTED":
      case "NOT_SELECTED":
        return <span className="badge bg-danger">NOT SELECTED</span>;
      case "WITHDRAWN":
        return <span className="badge bg-dark">WITHDRAWN</span>;
      case "APPLIED":
      default:
        return <span className="badge bg-light text-dark border">APPLIED</span>;
    }
  };

  if (loading) return <Loader message="Loading multi-company application tracker..." />;

  return (
    <div>
      {/* Header */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Multi-Company Placement Tracker</h3>
          <p className="text-muted small mb-0">
            Consolidated lifecycle tracking across all participating recruitment drives.
          </p>
        </div>
        <div className="d-flex gap-2">
          <div className="btn-group btn-group-sm" role="group">
            <button
              type="button"
              className={`btn ${viewMode === "tracker" ? "btn-primary" : "btn-outline-primary"}`}
              onClick={() => setViewMode("tracker")}
            >
              <i className="bi bi-grid-fill me-1"></i> Unified Tracker
            </button>
            <button
              type="button"
              className={`btn ${viewMode === "list" ? "btn-primary" : "btn-outline-primary"}`}
              onClick={() => setViewMode("list")}
            >
              <i className="bi bi-list-ul me-1"></i> Detailed List
            </button>
          </div>
          <Link to="/student/drives" className="btn btn-outline-secondary btn-sm">
            <i className="bi bi-plus-circle me-1"></i> Apply More
          </Link>
        </div>
      </div>

      {msg.text && (
        <div className={`alert alert-${msg.type} py-2 small d-flex align-items-center gap-2 mb-3`}>
          <i className="bi bi-info-circle-fill"></i>
          <div>{msg.text}</div>
        </div>
      )}

      {applications.length === 0 ? (
        <div className="text-center p-5 bg-white rounded border text-muted">
          <i className="bi bi-inbox fs-1 text-secondary mb-2 d-block"></i>
          You have not applied to any placement drives yet.
          <div className="mt-3">
            <Link to="/student/drives" className="btn btn-primary btn-sm">
              Discover Placement Drives
            </Link>
          </div>
        </div>
      ) : viewMode === "tracker" ? (
        /* MULTI-COMPANY CONSOLIDATED TRACKER MATRIX */
        <div className="row g-4">
          {applications.map((app) => (
            <div key={app.id} className="col-md-6 col-lg-4">
              <div className="card h-100 border-0 shadow-sm" style={{ borderRadius: "10px" }}>
                <div className="card-body p-4 d-flex flex-column justify-content-between">
                  <div>
                    {/* Company & Role */}
                    <div className="d-flex justify-content-between align-items-start mb-2">
                      <span className="badge bg-light text-dark border">
                        <i className="bi bi-building me-1"></i> {app.companyName}
                      </span>
                      {getStatusBadge(app.status)}
                    </div>
                    <h5 className="fw-bold text-dark mb-1">{app.driveTitle}</h5>
                    <div className="text-muted small mb-3">{app.jobRole || "Engineering Trainee"}</div>

                    {/* Progress Pipeline Summary */}
                    <div className="p-3 bg-light rounded mb-3">
                      <div className="small text-muted mb-1">Current Active Stage:</div>
                      <div className="fw-bold text-primary fs-6">
                        <i className="bi bi-arrow-right-circle-fill me-1"></i>
                        {app.currentStageName || app.status}
                      </div>

                      <div className="small text-muted mt-2">Applied Date:</div>
                      <div className="small text-dark">
                        <i className="bi bi-clock me-1"></i>
                        {app.appliedAt ? new Date(app.appliedAt).toLocaleDateString() : "Recent"}
                      </div>
                    </div>
                  </div>

                  {/* Actions */}
                  <div className="d-flex justify-content-between align-items-center pt-2 border-top">
                    <Link to="/student/calendar" className="btn btn-link text-decoration-none btn-sm p-0">
                      View Schedule &rarr;
                    </Link>
                    {(app.status === "APPLIED" || app.status === "UNDER_REVIEW") && (
                      <button
                        className="btn btn-outline-danger btn-sm py-0"
                        disabled={withdrawingId === app.id}
                        onClick={() => handleWithdraw(app.id)}
                        title="Withdraw this application"
                      >
                        {withdrawingId === app.id ? "Withdrawing..." : "Withdraw"}
                      </button>
                    )}
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        /* DETAILED TABLE VIEW WITH STAGE HISTORY */
        <div className="content-card">
          <div className="content-card-body p-0">
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light small">
                  <tr>
                    <th>Company</th>
                    <th>Drive Title</th>
                    <th>Current Stage</th>
                    <th>Status</th>
                    <th>Applied On</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody className="small">
                  {applications.map((app) => (
                    <tr key={app.id}>
                      <td className="fw-bold text-dark">{app.companyName}</td>
                      <td>{app.driveTitle}</td>
                      <td>
                        <span className="badge bg-secondary-subtle text-secondary border">
                          {app.currentStageName || app.status}
                        </span>
                      </td>
                      <td>{getStatusBadge(app.status)}</td>
                      <td className="text-muted">
                        {app.appliedAt ? new Date(app.appliedAt).toLocaleDateString() : "N/A"}
                      </td>
                      <td>
                        <div className="d-flex gap-2">
                          <Link to="/student/calendar" className="btn btn-outline-primary btn-sm py-0">
                            Calendar
                          </Link>
                          {(app.status === "APPLIED" || app.status === "UNDER_REVIEW") && (
                            <button
                              className="btn btn-outline-danger btn-sm py-0"
                              disabled={withdrawingId === app.id}
                              onClick={() => handleWithdraw(app.id)}
                            >
                              Withdraw
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default MyApplicationsPage;
