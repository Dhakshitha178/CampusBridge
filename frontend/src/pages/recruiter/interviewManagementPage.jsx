import React, { useState, useEffect } from "react";
import {
  getScheduledInterviews,
  scheduleInterview,
  checkInterviewConflicts,
  getRecruiterDrives,
  getDriveApplicants,
} from "../../services/recruiterService";
import Loader from "../../components/common/Loader";

const InterviewManagementPage = () => {
  const [interviews, setInterviews] = useState([]);
  const [drives, setDrives] = useState([]);
  const [applicants, setApplicants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [conflictReport, setConflictReport] = useState(null);
  const [checkingConflict, setCheckingConflict] = useState(false);
  const [saving, setSaving] = useState(false);
  const [msg, setMsg] = useState({ text: "", type: "" });

  const [form, setForm] = useState({
    driveId: "",
    applicationId: "",
    studentId: "",
    roundName: "Technical Interview Round 1",
    roundType: "TECHNICAL_INTERVIEW",
    interviewDate: new Date(Date.now() + 2 * 24 * 60 * 60 * 1000).toISOString().slice(0, 10),
    startTime: "11:30",
    endTime: "12:30",
    mode: "ONLINE",
    meetingLink: "https://meet.google.com/campus-interview-demo",
    venue: "Main Placement Lab",
  });

  const loadData = async () => {
    try {
      setLoading(true);
      const [intRes, drivesRes] = await Promise.all([
        getScheduledInterviews(),
        getRecruiterDrives(),
      ]);
      if (intRes.success && intRes.data) {
        setInterviews(intRes.data);
      }
      if (drivesRes.success && drivesRes.data && drivesRes.data.length > 0) {
        setDrives(drivesRes.data);
        setForm((prev) => ({ ...prev, driveId: drivesRes.data[0].id }));
        // load applicants for drive
        const appsRes = await getDriveApplicants(drivesRes.data[0].id);
        if (appsRes.success && appsRes.data) {
          setApplicants(appsRes.data);
          if (appsRes.data.length > 0) {
            setForm((prev) => ({
              ...prev,
              applicationId: appsRes.data[0].id,
              studentId: appsRes.data[0].studentId,
            }));
          }
        }
      }
    } catch (err) {
      console.error("Error loading interview management data:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleDriveChange = async (driveId) => {
    setForm({ ...form, driveId });
    try {
      const appsRes = await getDriveApplicants(driveId);
      if (appsRes.success && appsRes.data) {
        setApplicants(appsRes.data);
        if (appsRes.data.length > 0) {
          setForm((prev) => ({
            ...prev,
            driveId,
            applicationId: appsRes.data[0].id,
            studentId: appsRes.data[0].studentId,
          }));
        }
      }
    } catch (err) {
      console.warn("Could not load applicants for drive");
    }
  };

  const handleApplicantChange = (appId) => {
    const app = applicants.find((a) => a.id === Number(appId));
    if (app) {
      setForm({ ...form, applicationId: app.id, studentId: app.studentId });
    }
  };

  // Conflict Pre-Check
  const handleCheckConflict = async () => {
    if (!form.studentId) return;
    setCheckingConflict(true);
    setConflictReport(null);
    try {
      const payload = {
        studentId: form.studentId,
        interviewDate: form.interviewDate,
        startTime: form.startTime,
        endTime: form.endTime,
        mode: form.mode,
        venue: form.venue,
      };
      const res = await checkInterviewConflicts(payload);
      if (res.success && res.data) {
        setConflictReport(res.data);
      }
    } catch (err) {
      console.error("Conflict check error:", err);
    } finally {
      setCheckingConflict(false);
    }
  };

  const handleScheduleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setMsg({ text: "", type: "" });
    try {
      const res = await scheduleInterview(form);
      if (res.success) {
        setMsg({ text: "Interview scheduled successfully and notified to student!", type: "success" });
        setModalOpen(false);
        setConflictReport(null);
        loadData();
      } else {
        setMsg({ text: res.message || "Failed to schedule interview.", type: "danger" });
      }
    } catch (err) {
      setMsg({
        text: err.response?.data?.message || "Failed to schedule interview.",
        type: "danger",
      });
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <Loader message="Loading interview coordinator workspace..." />;

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Interview Scheduling &amp; Coordination 🎥</h3>
          <p className="text-muted small mb-0">
            Schedule candidate rounds with real-time pre-flight conflict checking across campus calendars.
          </p>
        </div>
        <div>
          <button
            className="btn btn-primary btn-sm"
            onClick={() => {
              setConflictReport(null);
              setModalOpen(true);
            }}
          >
            <i className="bi bi-plus-circle me-1"></i> + Schedule Interview
          </button>
        </div>
      </div>

      {msg.text && (
        <div className={`alert alert-${msg.type} py-2 small d-flex align-items-center gap-2 mb-3`}>
          <i className="bi bi-info-circle-fill"></i>
          <div>{msg.text}</div>
        </div>
      )}

      {interviews.length === 0 ? (
        <div className="text-center p-5 bg-white rounded border text-muted">
          No interviews scheduled yet. Click "+ Schedule Interview" above.
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
                    <th>Round Title</th>
                    <th>Date</th>
                    <th>Window</th>
                    <th>Mode</th>
                    <th>Status</th>
                    <th>Meeting / Venue</th>
                  </tr>
                </thead>
                <tbody className="small">
                  {interviews.map((item) => (
                    <tr key={item.id}>
                      <td className="fw-bold text-dark">{item.studentName}</td>
                      <td><span className="badge bg-light text-dark border">{item.studentRollNumber}</span></td>
                      <td><span className="badge bg-primary-subtle text-primary">{item.roundName || item.roundType}</span></td>
                      <td>{item.interviewDate}</td>
                      <td><strong>{item.startTime} - {item.endTime}</strong></td>
                      <td>
                        <span className={`badge ${item.mode === "ONLINE" ? "bg-info text-dark" : "bg-dark"}`}>
                          {item.mode}
                        </span>
                      </td>
                      <td><span className="badge bg-success">{item.status}</span></td>
                      <td>
                        {item.mode === "ONLINE" && item.meetingLink ? (
                          <a href={item.meetingLink} target="_blank" rel="noreferrer" className="text-primary small">
                            Join Link &rarr;
                          </a>
                        ) : (
                          item.venue || "Lab 1"
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* SCHEDULE INTERVIEW MODAL WITH CONFLICT CHECK */}
      {modalOpen && (
        <div
          className="modal show d-block"
          tabIndex="-1"
          style={{ backgroundColor: "rgba(0,0,0,0.5)" }}
        >
          <div className="modal-dialog modal-dialog-centered modal-lg">
            <div className="modal-content shadow-lg border-0" style={{ borderRadius: "10px" }}>
              <div className="modal-header bg-primary text-white">
                <h5 className="modal-title fs-6">
                  <i className="bi bi-camera-video me-2"></i> Schedule Candidate Interview
                </h5>
                <button
                  type="button"
                  className="btn-close btn-close-white"
                  onClick={() => setModalOpen(false)}
                ></button>
              </div>
              <form onSubmit={handleScheduleSubmit}>
                <div className="modal-body p-4">
                  <div className="row g-3 mb-3">
                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Drive *</label>
                      <select
                        className="form-select form-select-sm"
                        value={form.driveId}
                        onChange={(e) => handleDriveChange(e.target.value)}
                        required
                      >
                        {drives.map((d) => (
                          <option key={d.id} value={d.id}>
                            {d.companyName ? `${d.companyName} — ` : ""}{d.title}
                          </option>
                        ))}
                      </select>
                    </div>

                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Candidate *</label>
                      <select
                        className="form-select form-select-sm"
                        value={form.applicationId}
                        onChange={(e) => handleApplicantChange(e.target.value)}
                        required
                      >
                        {applicants.map((a) => (
                          <option key={a.id} value={a.id}>
                            {a.studentName || a.name || (a.student && a.student.fullName) || `Candidate #${a.id}`} ({a.studentRollNumber || a.rollNumber || (a.student && a.student.rollNumber) || "N/A"})
                          </option>
                        ))}
                      </select>
                    </div>

                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Round Title *</label>
                      <input
                        type="text"
                        className="form-control form-control-sm"
                        value={form.roundName}
                        onChange={(e) => setForm({ ...form, roundName: e.target.value })}
                        required
                      />
                    </div>

                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Round Type</label>
                      <select
                        className="form-select form-select-sm"
                        value={form.roundType}
                        onChange={(e) => setForm({ ...form, roundType: e.target.value })}
                      >
                        <option value="TECHNICAL_INTERVIEW">Technical Interview</option>
                        <option value="MANAGERIAL_INTERVIEW">Managerial Interview</option>
                        <option value="HR_INTERVIEW">HR Interview</option>
                        <option value="ASSESSMENT">Online Assessment</option>
                      </select>
                    </div>

                    <div className="col-md-4">
                      <label className="form-label small fw-semibold">Interview Date *</label>
                      <input
                        type="date"
                        className="form-control form-control-sm"
                        value={form.interviewDate}
                        onChange={(e) => setForm({ ...form, interviewDate: e.target.value })}
                        required
                      />
                    </div>

                    <div className="col-md-4">
                      <label className="form-label small fw-semibold">Start Time *</label>
                      <input
                        type="time"
                        className="form-control form-control-sm"
                        value={form.startTime}
                        onChange={(e) => setForm({ ...form, startTime: e.target.value })}
                        required
                      />
                    </div>

                    <div className="col-md-4">
                      <label className="form-label small fw-semibold">End Time *</label>
                      <input
                        type="time"
                        className="form-control form-control-sm"
                        value={form.endTime}
                        onChange={(e) => setForm({ ...form, endTime: e.target.value })}
                        required
                      />
                    </div>

                    <div className="col-md-4">
                      <label className="form-label small fw-semibold">Mode</label>
                      <select
                        className="form-select form-select-sm"
                        value={form.mode}
                        onChange={(e) => setForm({ ...form, mode: e.target.value })}
                      >
                        <option value="ONLINE">Online Video Call</option>
                        <option value="OFFLINE">Offline In-Person</option>
                      </select>
                    </div>

                    <div className="col-md-8">
                      <label className="form-label small fw-semibold">
                        {form.mode === "ONLINE" ? "Meeting URL" : "Campus Venue Room"}
                      </label>
                      <input
                        type="text"
                        className="form-control form-control-sm"
                        value={form.mode === "ONLINE" ? form.meetingLink : form.venue}
                        onChange={(e) =>
                          setForm({
                            ...form,
                            [form.mode === "ONLINE" ? "meetingLink" : "venue"]: e.target.value,
                          })
                        }
                      />
                    </div>
                  </div>

                  {/* REAL-TIME PRE-FLIGHT CONFLICT CHECK BUTTON */}
                  <div className="d-flex justify-content-between align-items-center p-3 bg-light rounded border mb-3">
                    <span className="small text-muted">
                      Verify candidate has no overlapping interviews with other companies:
                    </span>
                    <button
                      type="button"
                      className="btn btn-outline-primary btn-sm"
                      onClick={handleCheckConflict}
                      disabled={checkingConflict}
                    >
                      <i className="bi bi-shield-check me-1"></i>
                      {checkingConflict ? "Checking..." : "Pre-Check Conflict"}
                    </button>
                  </div>

                  {/* CONFLICT REPORT DISPLAY */}
                  {conflictReport && (
                    <div
                      className={`p-3 rounded small mb-3 ${
                        conflictReport.hasConflict
                          ? "bg-danger-subtle border border-danger-subtle text-danger"
                          : "bg-success-subtle border border-success-subtle text-success"
                      }`}
                    >
                      <strong>
                        {conflictReport.hasConflict
                          ? "⚠️ Conflict Detected!"
                          : "✓ Slot is Clean & Conflict-Free!"}
                      </strong>
                      <div className="mt-1">
                        {conflictReport.hasConflict ? (
                          <span>
                            This student is already scheduled for another interview during this time window.
                            Scheduling now will trigger a conflict warning on the student's calendar.
                          </span>
                        ) : (
                          <span>No overlapping interview commitments exist for this candidate.</span>
                        )}
                      </div>
                    </div>
                  )}
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
                    className="btn btn-primary btn-sm"
                    disabled={saving}
                  >
                    {saving ? "Scheduling..." : "Confirm & Notify Candidate"}
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

export default InterviewManagementPage;
