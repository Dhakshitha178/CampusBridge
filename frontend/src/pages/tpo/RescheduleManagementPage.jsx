import React, { useState, useEffect } from "react";
import {
  getRescheduleRequests,
  approveRescheduleRequest,
  rejectRescheduleRequest,
} from "../../services/tpoService";
import Loader from "../../components/common/Loader";

const RescheduleManagementPage = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedReq, setSelectedReq] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [msg, setMsg] = useState({ text: "", type: "" });

  const [approveForm, setApproveForm] = useState({
    newInterviewDate: "",
    newStartTime: "14:00",
    newEndTime: "15:00",
    coordinatorNotes: "Resolved overlapping interview conflict.",
  });

  const loadRequests = async () => {
    try {
      setLoading(true);
      const res = await getRescheduleRequests();
      if (res.success && res.data) {
        setRequests(res.data);
      }
    } catch (err) {
      console.error("Error loading reschedule requests:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRequests();
  }, []);

  const openApproveModal = (req) => {
    setSelectedReq(req);
    setApproveForm({
      newInterviewDate: req.preferredDate || new Date().toISOString().slice(0, 10),
      newStartTime: "14:00",
      newEndTime: "15:00",
      coordinatorNotes: "Approved reschedule: Shifted slot to avoid conflict.",
    });
    setModalOpen(true);
  };

  const handleApproveSubmit = async (e) => {
    e.preventDefault();
    if (!selectedReq) return;
    setSubmitting(true);
    setMsg({ text: "", type: "" });
    try {
      const res = await approveRescheduleRequest(selectedReq.id, approveForm);
      if (res.success) {
        setMsg({
          text: `Reschedule request #${selectedReq.id} approved! The student's interview has been rescheduled to ${approveForm.newInterviewDate} (${approveForm.newStartTime} - ${approveForm.newEndTime}).`,
          type: "success",
        });
        setModalOpen(false);
        loadRequests();
      } else {
        setMsg({ text: res.message || "Failed to approve request.", type: "danger" });
      }
    } catch (err) {
      setMsg({
        text: err.response?.data?.message || "Error approving reschedule.",
        type: "danger",
      });
    } finally {
      setSubmitting(false);
    }
  };

  const handleReject = async (reqId) => {
    const reason = window.prompt("Enter rejection reason for this request:", "Interview slot cannot be shifted due to panel availability.");
    if (!reason) return;

    setMsg({ text: "", type: "" });
    try {
      const res = await rejectRescheduleRequest(reqId, reason);
      if (res.success) {
        setMsg({ text: "Reschedule request rejected.", type: "info" });
        loadRequests();
      } else {
        setMsg({ text: res.message || "Failed to reject.", type: "danger" });
      }
    } catch (err) {
      setMsg({
        text: err.response?.data?.message || "Failed to reject request.",
        type: "danger",
      });
    }
  };

  if (loading) return <Loader message="Loading student reschedule requests queue..." />;

  const pendingRequests = requests.filter((r) => r.status === "PENDING");
  const historyRequests = requests.filter((r) => r.status !== "PENDING");

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Interview Reschedule Request Queue 🔄</h3>
          <p className="text-muted small mb-0">
            TPO dispute and conflict arbitration workflow: negotiate new slots and reconcile cross-company calendars.
          </p>
        </div>
        <div>
          <span className="badge bg-warning text-dark px-3 py-2 fw-semibold">
            {pendingRequests.length} Pending Resolution
          </span>
        </div>
      </div>

      {msg.text && (
        <div className={`alert alert-${msg.type} py-2 small d-flex align-items-center gap-2 mb-3`}>
          <i className="bi bi-info-circle-fill"></i>
          <div>{msg.text}</div>
        </div>
      )}

      {/* PENDING RESOLUTIONS SECTION */}
      <div className="content-card mb-4">
        <div className="content-card-header bg-light">
          <h5><i className="bi bi-hourglass-split me-2 text-warning"></i> Pending Student Requests</h5>
        </div>
        <div className="content-card-body p-0">
          {pendingRequests.length === 0 ? (
            <div className="p-4 text-center text-muted small">
              <i className="bi bi-check-circle fs-3 text-success d-block mb-1"></i>
              No pending rescheduling requests. All student interview conflicts resolved!
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light small">
                  <tr>
                    <th>Student</th>
                    <th>Roll Number</th>
                    <th>Company</th>
                    <th>Interview Details</th>
                    <th>Stated Reason</th>
                    <th>Preferred Slot</th>
                    <th>Submitted On</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody className="small">
                  {pendingRequests.map((req) => (
                    <tr key={req.id}>
                      <td className="fw-bold text-dark">{req.studentName}</td>
                      <td><span className="badge bg-light text-dark border">{req.studentRollNumber}</span></td>
                      <td className="fw-semibold">{req.companyName}</td>
                      <td>
                        <span className="badge bg-secondary-subtle text-secondary">{req.roundName}</span>
                        <div className="text-muted small mt-1">
                          {req.currentDate} ({req.currentTimeSlot})
                        </div>
                      </td>
                      <td>
                        <span className="badge bg-danger-subtle text-danger border border-danger-subtle">
                          {req.reason}
                        </span>
                      </td>
                      <td>
                        <strong>{req.preferredDate}</strong>
                        <div className="text-muted small">{req.preferredTimeSlot}</div>
                      </td>
                      <td className="text-muted">
                        {req.createdAt ? new Date(req.createdAt).toLocaleDateString() : "Recent"}
                      </td>
                      <td>
                        <div className="d-flex gap-2">
                          <button
                            className="btn btn-success btn-sm py-0"
                            onClick={() => openApproveModal(req)}
                          >
                            Assign New Slot
                          </button>
                          <button
                            className="btn btn-outline-danger btn-sm py-0"
                            onClick={() => handleReject(req.id)}
                          >
                            Decline
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      {/* RESOLVED HISTORY SECTION */}
      {historyRequests.length > 0 && (
        <div className="content-card">
          <div className="content-card-header">
            <h5><i className="bi bi-clock-history me-2 text-primary"></i> Resolved Arbitration History</h5>
          </div>
          <div className="content-card-body p-0">
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light small">
                  <tr>
                    <th>Student</th>
                    <th>Company</th>
                    <th>Status</th>
                    <th>Assigned New Slot</th>
                    <th>Coordinator Remarks</th>
                  </tr>
                </thead>
                <tbody className="small">
                  {historyRequests.map((req) => (
                    <tr key={req.id}>
                      <td className="fw-semibold">{req.studentName}</td>
                      <td>{req.companyName}</td>
                      <td>
                        <span
                          className={`badge ${
                            req.status === "APPROVED" ? "bg-success" : "bg-danger"
                          }`}
                        >
                          {req.status}
                        </span>
                      </td>
                      <td>
                        {req.status === "APPROVED" ? (
                          <strong>{req.allocatedDate} ({req.allocatedTimeSlot})</strong>
                        ) : (
                          <span className="text-muted">&mdash;</span>
                        )}
                      </td>
                      <td className="text-muted">{req.decisionNotes || "None"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* APPROVE & RE-SLOT MODAL */}
      {modalOpen && selectedReq && (
        <div
          className="modal show d-block"
          tabIndex="-1"
          style={{ backgroundColor: "rgba(0,0,0,0.5)" }}
        >
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content shadow-lg border-0" style={{ borderRadius: "10px" }}>
              <div className="modal-header bg-success text-white">
                <h5 className="modal-title fs-6">
                  <i className="bi bi-check-circle me-2"></i> Approve &amp; Re-Slot Interview
                </h5>
                <button
                  type="button"
                  className="btn-close btn-close-white"
                  onClick={() => setModalOpen(false)}
                ></button>
              </div>
              <form onSubmit={handleApproveSubmit}>
                <div className="modal-body p-4">
                  <div className="p-3 bg-light rounded mb-3 small">
                    <div><strong>Candidate:</strong> {selectedReq.studentName} ({selectedReq.studentRollNumber})</div>
                    <div><strong>Company:</strong> {selectedReq.companyName} &bull; {selectedReq.roundName}</div>
                    <div className="text-danger">
                      <strong>Stated Conflict:</strong> {selectedReq.reason}
                    </div>
                    <div>
                      <strong>Requested Preference:</strong> {selectedReq.preferredDate} ({selectedReq.preferredTimeSlot})
                    </div>
                  </div>

                  <div className="mb-3">
                    <label className="form-label small fw-semibold">New Interview Date *</label>
                    <input
                      type="date"
                      className="form-control form-control-sm"
                      value={approveForm.newInterviewDate}
                      onChange={(e) =>
                        setApproveForm({ ...approveForm, newInterviewDate: e.target.value })
                      }
                      required
                    />
                  </div>

                  <div className="row g-2 mb-3">
                    <div className="col-6">
                      <label className="form-label small fw-semibold">New Start Time *</label>
                      <input
                        type="time"
                        className="form-control form-control-sm"
                        value={approveForm.newStartTime}
                        onChange={(e) =>
                          setApproveForm({ ...approveForm, newStartTime: e.target.value })
                        }
                        required
                      />
                    </div>
                    <div className="col-6">
                      <label className="form-label small fw-semibold">New End Time *</label>
                      <input
                        type="time"
                        className="form-control form-control-sm"
                        value={approveForm.newEndTime}
                        onChange={(e) =>
                          setApproveForm({ ...approveForm, newEndTime: e.target.value })
                        }
                        required
                      />
                    </div>
                  </div>

                  <div className="mb-3">
                    <label className="form-label small fw-semibold">Coordinator Notes for Recruiter &amp; Student</label>
                    <textarea
                      rows="2"
                      className="form-control form-control-sm"
                      value={approveForm.coordinatorNotes}
                      onChange={(e) =>
                        setApproveForm({ ...approveForm, coordinatorNotes: e.target.value })
                      }
                    ></textarea>
                  </div>
                </div>
                <div className="modal-footer bg-light">
                  <button
                    type="button"
                    className="btn btn-secondary btn-sm"
                    onClick={() => setModalOpen(false)}
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="btn btn-success btn-sm"
                    disabled={submitting}
                  >
                    {submitting ? "Confirming..." : "Confirm Reschedule"}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default RescheduleManagementPage;
