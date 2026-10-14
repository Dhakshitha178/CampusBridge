import React, { useState, useEffect } from "react";
import {
  getAnnouncements,
  createAnnouncement,
  deleteAnnouncement,
} from "../../services/tpoService";
import Loader from "../../components/common/Loader";

const AnnouncementsPage = () => {
  const [announcements, setAnnouncements] = useState([]);
  const [loading, setLoading] = useState(true);
  const [creating, setCreating] = useState(false);
  const [msg, setMsg] = useState({ text: "", type: "" });

  const [form, setForm] = useState({
    title: "",
    content: "",
    targetAudience: "ALL",
    priority: "NORMAL",
  });

  const loadAnnouncements = async () => {
    try {
      setLoading(true);
      const res = await getAnnouncements();
      if (res.success && res.data) {
        setAnnouncements(res.data);
      }
    } catch (err) {
      console.error("Error loading announcements:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAnnouncements();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setCreating(true);
    setMsg({ text: "", type: "" });
    try {
      const res = await createAnnouncement(form);
      if (res.success) {
        setMsg({ text: "Announcement broadcasted successfully!", type: "success" });
        setForm({
          title: "",
          content: "",
          targetAudience: "ALL",
          priority: "NORMAL",
        });
        loadAnnouncements();
      } else {
        setMsg({ text: res.message || "Failed to post announcement.", type: "danger" });
      }
    } catch (err) {
      setMsg({
        text: err.response?.data?.message || "Failed to post announcement.",
        type: "danger",
      });
    } finally {
      setCreating(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this announcement?")) return;
    try {
      await deleteAnnouncement(id);
      setMsg({ text: "Announcement deleted.", type: "info" });
      loadAnnouncements();
    } catch (err) {
      alert("Failed to delete announcement.");
    }
  };

  if (loading) return <Loader message="Loading placement notices and announcements..." />;

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Placement Notices &amp; Announcements 📢</h3>
          <p className="text-muted small mb-0">
            Publish campus-wide alerts, schedule changes, dress code guidelines, and drive instructions.
          </p>
        </div>
        <div>
          <span className="badge bg-primary px-3 py-2">
            {announcements.length} Active Notice(s)
          </span>
        </div>
      </div>

      {msg.text && (
        <div className={`alert alert-${msg.type} py-2 small d-flex align-items-center gap-2 mb-3`}>
          <i className="bi bi-info-circle-fill"></i>
          <div>{msg.text}</div>
        </div>
      )}

      <div className="row g-4">
        {/* CREATE ANNOUNCEMENT FORM */}
        <div className="col-md-5">
          <div className="content-card">
            <div className="content-card-header bg-light">
              <h5><i className="bi bi-megaphone me-2 text-primary"></i> Broadcast New Notice</h5>
            </div>
            <div className="content-card-body">
              <form onSubmit={handleSubmit}>
                <div className="mb-3">
                  <label className="form-label small fw-semibold">Notice Title *</label>
                  <input
                    type="text"
                    className="form-control form-control-sm"
                    placeholder="e.g. Mandatory Pre-Placement Talk for TCS"
                    value={form.title}
                    onChange={(e) => setForm({ ...form, title: e.target.value })}
                    required
                  />
                </div>

                <div className="row g-2 mb-3">
                  <div className="col-6">
                    <label className="form-label small fw-semibold">Target Audience</label>
                    <select
                      className="form-select form-select-sm"
                      value={form.targetAudience}
                      onChange={(e) => setForm({ ...form, targetAudience: e.target.value })}
                    >
                      <option value="ALL">All Campus (Everyone)</option>
                      <option value="STUDENT">All Registered Students</option>
                      <option value="RECRUITER">Corporate Recruiters</option>
                      <option value="CSE">CSE Department</option>
                      <option value="IT">IT Department</option>
                    </select>
                  </div>
                  <div className="col-6">
                    <label className="form-label small fw-semibold">Priority</label>
                    <select
                      className="form-select form-select-sm"
                      value={form.priority}
                      onChange={(e) => setForm({ ...form, priority: e.target.value })}
                    >
                      <option value="NORMAL">Normal</option>
                      <option value="HIGH">High Priority</option>
                      <option value="URGENT">Urgent / Critical</option>
                    </select>
                  </div>
                </div>

                <div className="mb-3">
                  <label className="form-label small fw-semibold">Message Content *</label>
                  <textarea
                    rows="4"
                    className="form-control form-control-sm"
                    placeholder="Detailed notice text, instructions, venue details..."
                    value={form.content}
                    onChange={(e) => setForm({ ...form, content: e.target.value })}
                    required
                  ></textarea>
                </div>

                <button
                  type="submit"
                  className="btn btn-primary btn-sm w-100"
                  disabled={creating}
                  style={{ backgroundColor: "#0f4c81" }}
                >
                  {creating ? "Broadcasting..." : "Publish Announcement"}
                </button>
              </form>
            </div>
          </div>
        </div>

        {/* ACTIVE ANNOUNCEMENTS LIST */}
        <div className="col-md-7">
          <div className="content-card">
            <div className="content-card-header">
              <h5><i className="bi bi-bell-fill me-2 text-warning"></i> Active Notice Board</h5>
            </div>
            <div className="content-card-body p-0">
              {announcements.length === 0 ? (
                <div className="p-4 text-center text-muted small">No active announcements.</div>
              ) : (
                <div className="list-group list-group-flush">
                  {announcements.map((a) => (
                    <div key={a.id} className="list-group-item p-3">
                      <div className="d-flex justify-content-between align-items-start mb-1">
                        <div>
                          <strong className="text-dark fs-6">{a.title}</strong>
                          <div className="d-flex gap-2 mt-1">
                            <span className="badge bg-light text-dark border">
                              To: {a.targetAudience}
                            </span>
                            <span
                              className={`badge ${
                                a.priority === "URGENT"
                                  ? "bg-danger"
                                  : a.priority === "HIGH"
                                  ? "bg-warning text-dark"
                                  : "bg-secondary"
                              }`}
                            >
                              {a.priority}
                            </span>
                          </div>
                        </div>
                        <button
                          className="btn btn-outline-danger btn-sm py-0"
                          title="Delete notice"
                          onClick={() => handleDelete(a.id)}
                        >
                          <i className="bi bi-trash"></i>
                        </button>
                      </div>
                      <p className="text-secondary small mt-2 mb-1" style={{ whiteSpace: "pre-line" }}>
                        {a.content}
                      </p>
                      <div className="text-muted text-end" style={{ fontSize: "0.75rem" }}>
                        Posted: {a.createdAt ? new Date(a.createdAt).toLocaleString() : "Recent"}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AnnouncementsPage;
