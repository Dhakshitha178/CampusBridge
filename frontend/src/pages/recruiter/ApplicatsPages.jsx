import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import {
  getRecruiterDrives,
  getDriveApplicants,
  updateApplicationStage,
} from "../../services/recruiterService";
import Loader from "../../components/common/Loader";

const ApplicatsPages = () => {
  const [drives, setDrives] = useState([]);
  const [selectedDriveId, setSelectedDriveId] = useState("");
  const [applicants, setApplicants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [updatingId, setUpdatingId] = useState(null);
  const [filterEligibleOnly, setFilterEligibleOnly] = useState(false);
  const [msg, setMsg] = useState({ text: "", type: "" });

  useEffect(() => {
    const loadDrives = async () => {
      try {
        setLoading(true);
        const res = await getRecruiterDrives();
        if (res.success && res.data && res.data.length > 0) {
          setDrives(res.data);
          setSelectedDriveId(res.data[0].id);
        }
      } catch (err) {
        console.error("Error loading recruiter drives:", err);
      } finally {
        setLoading(false);
      }
    };
    loadDrives();
  }, []);

  const loadApplicants = async (driveId) => {
    if (!driveId) return;
    try {
      setLoading(true);
      const res = await getDriveApplicants(driveId);
      if (res.success && res.data) {
        setApplicants(res.data);
      }
    } catch (err) {
      console.error("Error loading applicants:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (selectedDriveId) {
      loadApplicants(selectedDriveId);
    }
  }, [selectedDriveId]);

  const handleStageUpdate = async (appId, newStatus, remarks) => {
    setUpdatingId(appId);
    setMsg({ text: "", type: "" });
    try {
      const payload = {
        status: newStatus,
        remarks: remarks || `Status transitioned to ${newStatus}`,
      };
      const res = await updateApplicationStage(appId, payload);
      if (res.success) {
        setMsg({ text: `Candidate status updated to ${newStatus}.`, type: "success" });
        loadApplicants(selectedDriveId);
      } else {
        setMsg({ text: res.message || "Failed to update candidate.", type: "danger" });
      }
    } catch (err) {
      setMsg({
        text: err.response?.data?.message || "Error updating candidate stage.",
        type: "danger",
      });
    } finally {
      setUpdatingId(null);
    }
  };

  if (loading && drives.length === 0) {
    return <Loader message="Loading recruiter candidate applicant portal..." />;
  }

  const filteredApplicants = applicants.filter((app) => {
    if (filterEligibleOnly) {
      return app.eligible !== false;
    }
    return true;
  });

  const getStatusBadge = (status) => {
    switch (status) {
      case "SELECTED":
        return <span className="badge bg-success">SELECTED</span>;
      case "SHORTLISTED":
        return <span className="badge bg-info text-dark">SHORTLISTED</span>;
      case "INTERVIEW":
        return <span className="badge bg-warning text-dark">INTERVIEW</span>;
      case "REJECTED":
      case "NOT_SELECTED":
        return <span className="badge bg-danger">NOT SELECTED</span>;
      default:
        return <span className="badge bg-secondary">{status}</span>;
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Candidate Applications &amp; Funnel</h3>
          <p className="text-muted small mb-0">
            Review applicant profiles, check explainable eligibility compliance, and advance candidates by round.
          </p>
        </div>
        <div className="d-flex gap-2">
          <Link to="/recruiter/interviews" className="btn btn-outline-primary btn-sm">
            <i className="bi bi-camera-video me-1"></i> Schedule Interviews
          </Link>
        </div>
      </div>

      {msg.text && (
        <div className={`alert alert-${msg.type} py-2 small d-flex align-items-center gap-2 mb-3`}>
          <i className="bi bi-info-circle-fill"></i>
          <div>{msg.text}</div>
        </div>
      )}

      {/* DRIVE SELECTION AND FILTER */}
      <div className="card shadow-sm border-0 mb-4 p-3 bg-white">
        <div className="row g-2 align-items-center">
          <div className="col-md-6">
            <label className="form-label small fw-semibold">Select Recruitment Drive:</label>
            <select
              className="form-select form-select-sm"
              value={selectedDriveId}
              onChange={(e) => setSelectedDriveId(e.target.value)}
            >
              {drives.map((d) => (
                <option key={d.id} value={d.id}>
                  {d.title} ({d.jobRole}) &bull; {d.salaryPackage} LPA
                </option>
              ))}
            </select>
          </div>
          <div className="col-md-6 d-flex align-items-end justify-content-between">
            <div className="form-check form-switch pt-4">
              <input
                className="form-check-input"
                type="checkbox"
                id="eligSwitch"
                checked={filterEligibleOnly}
                onChange={(e) => setFilterEligibleOnly(e.target.checked)}
              />
              <label className="form-check-label small fw-semibold" htmlFor="eligSwitch">
                Show Eligible Candidates Only
              </label>
            </div>
            <div className="small text-muted pt-4">
              Total Applicants: <strong>{applicants.length}</strong>
            </div>
          </div>
        </div>
      </div>

      {filteredApplicants.length === 0 ? (
        <div className="text-center p-5 bg-white rounded border text-muted">
          No applicants match the current criteria for this drive.
        </div>
      ) : (
        <div className="content-card">
          <div className="content-card-body p-0">
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light small">
                  <tr>
                    <th>Candidate</th>
                    <th>Roll Number</th>
                    <th>CGPA</th>
                    <th>Backlogs</th>
                    <th>Department</th>
                    <th>Eligibility Check</th>
                    <th>Current Stage</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody className="small">
                  {filteredApplicants.map((app) => (
                    <tr key={app.id}>
                      <td className="fw-semibold text-dark">
                        {app.studentName}
                        {app.resumeUrl && (
                          <a
                            href={app.resumeUrl}
                            target="_blank"
                            rel="noreferrer"
                            className="ms-2 small text-primary"
                            title="View Resume"
                          >
                            <i className="bi bi-file-earmark-pdf"></i>
                          </a>
                        )}
                      </td>
                      <td><span className="badge bg-light text-dark border">{app.studentRollNumber}</span></td>
                      <td><strong>{app.cgpa}</strong></td>
                      <td>
                        <span className={app.backlogs > 0 ? "badge bg-danger" : "badge bg-success"}>
                          {app.backlogs}
                        </span>
                      </td>
                      <td>{app.department}</td>
                      <td>
                        {app.eligible !== false ? (
                          <span className="badge bg-success-subtle text-success border">
                            <i className="bi bi-check-circle me-1"></i> ELIGIBLE
                          </span>
                        ) : (
                          <span className="badge bg-danger-subtle text-danger border" title={app.ineligibilityReason}>
                            <i className="bi bi-x-circle me-1"></i> INELIGIBLE
                          </span>
                        )}
                      </td>
                      <td>
                        <span className="badge bg-primary-subtle text-primary">
                          {app.currentStageName || app.status}
                        </span>
                      </td>
                      <td>{getStatusBadge(app.status)}</td>
                      <td>
                        <div className="btn-group btn-group-sm">
                          {app.status === "APPLIED" && (
                            <button
                              className="btn btn-primary py-0"
                              disabled={updatingId === app.id}
                              onClick={() => handleStageUpdate(app.id, "SHORTLISTED", "Shortlisted for Round 1")}
                            >
                              Shortlist
                            </button>
                          )}
                          {app.status === "SHORTLISTED" && (
                            <button
                              className="btn btn-warning text-dark py-0 fw-semibold"
                              disabled={updatingId === app.id}
                              onClick={() => handleStageUpdate(app.id, "INTERVIEW", "Advanced to Technical Interview")}
                            >
                              To Interview
                            </button>
                          )}
                          {app.status === "INTERVIEW" && (
                            <button
                              className="btn btn-success py-0"
                              disabled={updatingId === app.id}
                              onClick={() => handleStageUpdate(app.id, "SELECTED", "Cleared all interview rounds")}
                            >
                              Select Candidate
                            </button>
                          )}
                          {app.status !== "REJECTED" && app.status !== "SELECTED" && (
                            <button
                              className="btn btn-outline-danger py-0"
                              disabled={updatingId === app.id}
                              onClick={() => handleStageUpdate(app.id, "REJECTED", "Candidate disqualified")}
                            >
                              Reject
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

export default ApplicatsPages;
