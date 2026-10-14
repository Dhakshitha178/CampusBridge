import React, { useState, useEffect } from "react";
import {
  getStudentCalendar,
  getStudentConflicts,
  submitRescheduleRequest,
} from "../../services/studentService";
import Loader from "../../components/common/Loader";

const InterviewCalendarPage = () => {
  const [interviews, setInterviews] = useState([]);
  const [conflicts, setConflicts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedInterview, setSelectedInterview] = useState(null);
  const [rescheduleModalOpen, setRescheduleModalOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [msg, setMsg] = useState({ text: "", type: "" });

  const [rescheduleForm, setRescheduleForm] = useState({
    reason: "Overlapping interview conflict with another company",
    preferredDate: "",
    preferredTimeSlot: "14:00 - 15:00",
  });

  const loadCalendarData = async () => {
    try {
      setLoading(true);
      const [calRes, confRes] = await Promise.all([
        getStudentCalendar(),
        getStudentConflicts(),
      ]);

      if (calRes.success && calRes.data) {
        setInterviews(calRes.data);
      }
      if (confRes.success && confRes.data) {
        setConflicts(confRes.data);
      }
    } catch (err) {
      console.error("Error loading interview calendar:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCalendarData();
  }, []);

  const openRescheduleModal = (interview) => {
    setSelectedInterview(interview);
    setRescheduleForm({
      reason: "Overlapping interview conflict with another company",
      preferredDate: interview.interviewDate || "",
      preferredTimeSlot: "14:00 - 15:00",
    });
    setRescheduleModalOpen(true);
  };

  const handleRescheduleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedInterview) return;
    setSubmitting(true);
    setMsg({ text: "", type: "" });
    try {
      const payload = {
        interviewId: selectedInterview.id,
        reason: rescheduleForm.reason,
        preferredDate: rescheduleForm.preferredDate,
        preferredTimeSlot: rescheduleForm.preferredTimeSlot,
      };
      const res = await submitRescheduleRequest(payload);
      if (res.success) {
        setMsg({
          text: "Reschedule request submitted to TPO Placement Cell successfully!",
          type: "success",
        });
        setRescheduleModalOpen(false);
        loadCalendarData();
      } else {
        setMsg({ text: res.message || "Failed to submit reschedule request.", type: "danger" });
      }
    } catch (err) {
      setMsg({
        text: err.response?.data?.message || "Failed to submit reschedule request.",
        type: "danger",
      });
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <Loader message="Loading unified multi-company calendar..." />;

  const conflictInterviewIds = new Set();
  conflicts.forEach((c) => {
    if (c.interview1Id) conflictInterviewIds.add(c.interview1Id);
    if (c.interview2Id) conflictInterviewIds.add(c.interview2Id);
  });

  return (
    <div>
      {/* Header */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Unified Multi-Company Interview Calendar</h3>
          <p className="text-muted small mb-0">
            Coordinated cross-company schedules with real-time conflict detection and TPO rescheduling.
          </p>
        </div>
        <div>
          <span className="badge bg-primary px-3 py-2">
            <i className="bi bi-calendar3 me-1"></i> {interviews.length} Scheduled Rounds
          </span>
        </div>
      </div>

      {msg.text && (
        <div className={`alert alert-${msg.type} py-2 small d-flex align-items-center gap-2 mb-3`}>
          <i className="bi bi-info-circle-fill"></i>
          <div>{msg.text}</div>
        </div>
      )}

      {/* PROMINENT CONFLICT BANNER */}
      {conflicts.length > 0 && (
        <div className="conflict-alert-box mb-4">
          <h5>
            <i className="bi bi-exclamation-octagon-fill"></i> Schedule Overlap Detected!
          </h5>
          <p className="small mb-2 text-dark">
            Our scheduling engine has identified <strong>{conflicts.length} overlapping interview slot(s)</strong>.
            Please submit a rescheduling request immediately to enable the Placement Cell to negotiate alternative slots with the recruiter.
          </p>
          <div className="list-group list-group-flush bg-transparent">
            {conflicts.map((conf, idx) => (
              <div
                key={idx}
                className="list-group-item bg-white bg-opacity-75 rounded p-2 mb-2 d-flex justify-content-between align-items-center"
              >
                <div>
                  <strong className="text-danger">
                    <i className="bi bi-arrow-left-right me-1"></i> Conflict #{idx + 1}:
                  </strong>{" "}
                  <span className="fw-semibold">{conf.company1Name} ({conf.round1Name})</span>{" "}
                  overlaps with{" "}
                  <span className="fw-semibold">{conf.company2Name} ({conf.round2Name})</span>{" "}
                  on <strong>{conf.date}</strong> ({conf.timeSlot1} vs {conf.timeSlot2})
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* INTERVIEWS LIST */}
      {interviews.length === 0 ? (
        <div className="text-center p-5 bg-white rounded border text-muted">
          <i className="bi bi-calendar-x fs-1 text-secondary mb-2 d-block"></i>
          No upcoming interviews scheduled on your calendar.
        </div>
      ) : (
        <div className="row g-3">
          {interviews.map((item) => {
            const hasConflict = conflictInterviewIds.has(item.id);

            return (
              <div key={item.id} className="col-md-6">
                <div
                  className={`card h-100 shadow-sm border-0 ${
                    hasConflict ? "border border-danger border-2" : ""
                  }`}
                  style={{ borderRadius: "10px" }}
                >
                  <div className="card-body p-4 d-flex flex-column justify-content-between">
                    <div>
                      {/* Top Badges */}
                      <div className="d-flex justify-content-between align-items-center mb-2">
                        <span className="badge bg-light text-dark border">
                          <i className="bi bi-building me-1"></i> {item.companyName}
                        </span>
                        {hasConflict ? (
                          <span className="badge bg-danger animate__animated animate__pulse">
                            <i className="bi bi-exclamation-triangle-fill me-1"></i> TIME CONFLICT
                          </span>
                        ) : (
                          <span className="badge bg-success-subtle text-success border">
                            {item.status}
                          </span>
                        )}
                      </div>

                      <h5 className="fw-bold text-dark mb-1">{item.roundName || item.roundType}</h5>
                      <div className="text-muted small mb-3">{item.driveTitle}</div>

                      {/* Time & Venue */}
                      <div className="p-3 bg-light rounded mb-3 small">
                        <div className="d-flex justify-content-between py-1">
                          <span className="text-muted"><i className="bi bi-calendar-date me-1"></i> Date:</span>
                          <strong>{item.interviewDate}</strong>
                        </div>
                        <div className="d-flex justify-content-between py-1">
                          <span className="text-muted"><i className="bi bi-clock me-1"></i> Time Window:</span>
                          <strong className={hasConflict ? "text-danger" : "text-dark"}>
                            {item.startTime} &mdash; {item.endTime}
                          </strong>
                        </div>
                        <div className="d-flex justify-content-between py-1">
                          <span className="text-muted"><i className="bi bi-laptop me-1"></i> Mode:</span>
                          <span className={`badge ${item.mode === "ONLINE" ? "bg-info text-dark" : "bg-dark"}`}>
                            {item.mode}
                          </span>
                        </div>
                        <div className="d-flex justify-content-between py-1">
                          <span className="text-muted">
                            <i className="bi bi-geo-alt me-1"></i> Venue / Meeting:
                          </span>
                          <span>
                            {item.mode === "ONLINE" && item.meetingLink ? (
                              <a
                                href={item.meetingLink}
                                target="_blank"
                                rel="noreferrer"
                                className="text-primary fw-semibold"
                              >
                                Join Meeting &rarr;
                              </a>
                            ) : (
                              item.venue || "Placement Lab / Room"
                            )}
                          </span>
                        </div>
                      </div>
                    </div>

                    {/* Reschedule Button */}
                    <div className="d-flex justify-content-between align-items-center pt-2 border-top">
                      <span className="small text-muted">
                        Duration: ~{item.durationMinutes || 60} mins
                      </span>
                      {item.status === "SCHEDULED" && (
                        <button
                          className={`btn btn-sm ${hasConflict ? "btn-danger fw-semibold" : "btn-outline-secondary"}`}
                          onClick={() => openRescheduleModal(item)}
                        >
                          <i className="bi bi-arrow-repeat me-1"></i> Request Reschedule
                        </button>
                      )}
                      {item.status === "RESCHEDULE_REQUESTED" && (
                        <span className="badge bg-warning text-dark">
                          Reschedule Under Review
                        </span>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* RESCHEDULE MODAL */}
      {rescheduleModalOpen && selectedInterview && (
        <div
          className="modal show d-block"
          tabIndex="-1"
          style={{ backgroundColor: "rgba(0,0,0,0.5)" }}
        >
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content shadow-lg border-0" style={{ borderRadius: "10px" }}>
              <div className="modal-header bg-primary text-white">
                <h5 className="modal-title fs-6">
                  <i className="bi bi-arrow-repeat me-2"></i> Request Interview Reschedule
                </h5>
                <button
                  type="button"
                  className="btn-close btn-close-white"
                  onClick={() => setRescheduleModalOpen(false)}
                ></button>
              </div>
              <form onSubmit={handleRescheduleSubmit}>
                <div className="modal-body p-4">
                  <div className="p-3 bg-light rounded mb-3 small">
                    <div><strong>Company:</strong> {selectedInterview.companyName}</div>
                    <div><strong>Round:</strong> {selectedInterview.roundName}</div>
                    <div>
                      <strong>Current Time:</strong> {selectedInterview.interviewDate} at{" "}
                      {selectedInterview.startTime} - {selectedInterview.endTime}
                    </div>
                  </div>

                  <div className="mb-3">
                    <label className="form-label small fw-semibold">Reason for Rescheduling *</label>
                    <select
                      className="form-select form-select-sm mb-2"
                      value={rescheduleForm.reason}
                      onChange={(e) =>
                        setRescheduleForm({ ...rescheduleForm, reason: e.target.value })
                      }
                      required
                    >
                      <option value="Overlapping interview conflict with another company">
                        Overlapping interview conflict with another company
                      </option>
                      <option value="Academic university examination / lab schedule">
                        Academic university examination / lab schedule
                      </option>
                      <option value="Medical emergency">Medical emergency</option>
                      <option value="Severe internet connectivity / hardware outage">
                        Severe internet connectivity / hardware outage
                      </option>
                      <option value="Other unavoidable reason">Other unavoidable reason</option>
                    </select>
                  </div>

                  <div className="mb-3">
                    <label className="form-label small fw-semibold">Preferred Alternative Date *</label>
                    <input
                      type="date"
                      className="form-control form-control-sm"
                      value={rescheduleForm.preferredDate}
                      onChange={(e) =>
                        setRescheduleForm({ ...rescheduleForm, preferredDate: e.target.value })
                      }
                      required
                    />
                  </div>

                  <div className="mb-3">
                    <label className="form-label small fw-semibold">Preferred Alternative Slot *</label>
                    <input
                      type="text"
                      className="form-control form-control-sm"
                      placeholder="e.g. 14:00 - 15:00 or Afternoon slot"
                      value={rescheduleForm.preferredTimeSlot}
                      onChange={(e) =>
                        setRescheduleForm({ ...rescheduleForm, preferredTimeSlot: e.target.value })
                      }
                      required
                    />
                  </div>
                </div>
                <div className="modal-footer bg-light">
                  <button
                    type="button"
                    className="btn btn-secondary btn-sm"
                    onClick={() => setRescheduleModalOpen(false)}
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="btn btn-primary btn-sm"
                    disabled={submitting}
                  >
                    {submitting ? "Submitting..." : "Submit to TPO"}
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

export default InterviewCalendarPage;
