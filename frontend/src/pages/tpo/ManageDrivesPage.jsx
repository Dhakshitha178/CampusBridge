import React, { useState, useEffect } from "react";
import {
  getPendingDrives,
  approveDrive,
  rejectDrive,
} from "../../services/tpoService";
import { getActiveDrives } from "../../services/driveService";
import Loader from "../../components/common/Loader";

const ManageDrivesPage = () => {
  const [pendingDrives, setPendingDrives] = useState([]);
  const [allDrives, setAllDrives] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState("pending");
  const [processingId, setProcessingId] = useState(null);
  const [msg, setMsg] = useState({ text: "", type: "" });

  const loadData = async () => {
    try {
      setLoading(true);
      const [pendingRes, allRes] = await Promise.all([
        getPendingDrives(),
        getActiveDrives(),
      ]);
      if (pendingRes.success && pendingRes.data) {
        setPendingDrives(pendingRes.data);
      }
      if (allRes.success && allRes.data) {
        setAllDrives(allRes.data);
      }
    } catch (err) {
      console.error("Error loading drives:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleApprove = async (driveId) => {
    const notes = window.prompt("Enter approval remarks / notes (optional):", "Approved for campus recruitment drive");
    if (notes === null) return;

    setProcessingId(driveId);
    setMsg({ text: "", type: "" });
    try {
      const res = await approveDrive(driveId, notes);
      if (res.success) {
        setMsg({ text: "Drive approved successfully! It is now active for eligible students.", type: "success" });
        loadData();
      } else {
        setMsg({ text: res.message || "Approval failed.", type: "danger" });
      }
    } catch (err) {
      setMsg({ text: err.response?.data?.message || "Failed to approve drive.", type: "danger" });
    } finally {
      setProcessingId(null);
    }
  };

  const handleReject = async (driveId) => {
    const reason = window.prompt("Please state the reason for rejecting this drive:", "Criteria not conforming to college guidelines");
    if (!reason) return;

    setProcessingId(driveId);
    setMsg({ text: "", type: "" });
    try {
      const res = await rejectDrive(driveId, reason);
      if (res.success) {
        setMsg({ text: "Drive rejected and returned to recruiter with feedback.", type: "info" });
        loadData();
      } else {
        setMsg({ text: res.message || "Rejection failed.", type: "danger" });
      }
    } catch (err) {
      setMsg({ text: err.response?.data?.message || "Failed to reject drive.", type: "danger" });
    } finally {
      setProcessingId(null);
    }
  };

  if (loading) return <Loader message="Loading placement drives for coordinator review..." />;

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Manage Placement Drives</h3>
          <p className="text-muted small mb-0">
            Review, verify, and approve incoming recruitment drives submitted by corporate partners.
          </p>
        </div>
        <div className="btn-group btn-group-sm">
          <button
            className={`btn ${activeTab === "pending" ? "btn-primary" : "btn-outline-primary"}`}
            onClick={() => setActiveTab("pending")}
          >
            Pending Approval ({pendingDrives.length})
          </button>
          <button
            className={`btn ${activeTab === "active" ? "btn-primary" : "btn-outline-primary"}`}
            onClick={() => setActiveTab("active")}
          >
            Active &amp; Approved ({allDrives.length})
          </button>
        </div>
      </div>

      {msg.text && (
        <div className={`alert alert-${msg.type} py-2 small d-flex align-items-center gap-2 mb-3`}>
          <i className="bi bi-info-circle-fill"></i>
          <div>{msg.text}</div>
        </div>
      )}

      {activeTab === "pending" ? (
        pendingDrives.length === 0 ? (
          <div className="text-center p-5 bg-white rounded border text-muted">
            <i className="bi bi-check2-all fs-1 text-success mb-2 d-block"></i>
            All incoming placement drives have been reviewed. No pending requests.
          </div>
        ) : (
          <div className="row g-4">
            {pendingDrives.map((drive) => (
              <div key={drive.id} className="col-12">
                <div className="card shadow-sm border-0" style={{ borderRadius: "10px" }}>
                  <div className="card-body p-4">
                    <div className="d-flex justify-content-between align-items-start mb-2">
                      <div>
                        <span className="badge bg-warning-subtle text-dark border border-warning-subtle mb-1">
                          PENDING TPO APPROVAL
                        </span>
                        <h4 className="fw-bold text-dark mb-1">{drive.title}</h4>
                        <div className="text-muted small">
                          Company: <strong>{drive.companyName}</strong> &bull; Role: {drive.jobRole}
                        </div>
                      </div>
                      <div className="text-end">
                        <span className="fs-5 fw-bold text-success d-block">{drive.salaryPackage} LPA</span>
                        <span className="text-muted small">Deadline: {drive.applicationDeadline}</span>
                      </div>
                    </div>

                    <p className="text-secondary small my-3" style={{ lineHeight: "1.5" }}>
                      {drive.description}
                    </p>

                    {/* Criteria snapshot */}
                    {drive.criteria && (
                      <div className="p-3 bg-light rounded small mb-3">
                        <div className="row g-2">
                          <div className="col-md-3">
                            <span className="text-muted d-block">Min CGPA:</span>
                            <strong>{drive.criteria.minCgpa}</strong>
                          </div>
                          <div className="col-md-3">
                            <span className="text-muted d-block">Max Backlogs:</span>
                            <strong>{drive.criteria.maxBacklogs}</strong>
                          </div>
                          <div className="col-md-3">
                            <span className="text-muted d-block">Graduation Year:</span>
                            <strong>{drive.criteria.graduationYear}</strong>
                          </div>
                          <div className="col-md-3">
                            <span className="text-muted d-block">Allowed Depts:</span>
                            <strong>{drive.criteria.allowedDepartments || "All"}</strong>
                          </div>
                        </div>
                      </div>
                    )}

                    <div className="d-flex justify-content-end gap-2 pt-2 border-top">
                      <button
                        className="btn btn-outline-danger btn-sm"
                        disabled={processingId === drive.id}
                        onClick={() => handleReject(drive.id)}
                      >
                        <i className="bi bi-x-circle me-1"></i> Reject Drive
                      </button>
                      <button
                        className="btn btn-success btn-sm px-3"
                        disabled={processingId === drive.id}
                        onClick={() => handleApprove(drive.id)}
                      >
                        <i className="bi bi-check-circle me-1"></i> Approve &amp; Publish
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )
      ) : (
        <div className="content-card">
          <div className="content-card-body p-0">
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light small">
                  <tr>
                    <th>Company</th>
                    <th>Drive Title</th>
                    <th>Role</th>
                    <th>Package</th>
                    <th>Vacancies</th>
                    <th>Mode</th>
                    <th>Deadline</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody className="small">
                  {allDrives.map((d) => (
                    <tr key={d.id}>
                      <td className="fw-semibold text-dark">{d.companyName}</td>
                      <td>{d.title}</td>
                      <td>{d.jobRole}</td>
                      <td><span className="badge bg-success-subtle text-success">{d.salaryPackage} LPA</span></td>
                      <td>{d.vacancies}</td>
                      <td>{d.workMode}</td>
                      <td>{d.applicationDeadline}</td>
                      <td><span className="badge bg-success">{d.status}</span></td>
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

export default ManageDrivesPage;
