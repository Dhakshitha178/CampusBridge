import React, { useEffect, useState } from "react";
import { getStudentProfile, updateStudentProfile } from "../../services/studentService";
import Loader from "../../components/common/Loader";

const StudentProfilePage = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [form, setForm] = useState({});
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [saving, setSaving] = useState(false);

  const fetchProfile = async () => {
    try {
      setLoading(true);
      const res = await getStudentProfile();
      if (res.success && res.data) {
        setProfile(res.data);
        setForm(res.data);
      }
    } catch (err) {
      setError("Could not load student profile.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({
      ...form,
      [name]:
        name === "cgpa"
          ? parseFloat(value) || 0
          : name === "backlogs" || name === "graduationYear"
          ? parseInt(value) || 0
          : value,
    });
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    setSaving(true);
    try {
      const res = await updateStudentProfile(form);
      if (res.success && res.data) {
        setProfile(res.data);
        setEditing(false);
        setSuccess("Academic profile updated successfully!");
      } else {
        setError(res.message || "Failed to update profile.");
      }
    } catch (err) {
      setError(err.response?.data?.message || "Failed to update profile.");
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <Loader message="Loading academic profile..." />;

  const skillsList = profile?.skills
    ? profile.skills.split(",").map((s) => s.trim()).filter(Boolean)
    : [];

  return (
    <div className="container-fluid px-0">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Student Academic Profile</h3>
          <p className="text-muted small mb-0">
            {profile?.collegeName} &bull; Roll No: <strong>{profile?.rollNumber}</strong>
          </p>
        </div>
        <div>
          {!editing ? (
            <button
              className="btn btn-primary btn-sm d-flex align-items-center gap-1"
              onClick={() => setEditing(true)}
            >
              <i className="bi bi-pencil-square"></i> Edit Profile
            </button>
          ) : (
            <button
              className="btn btn-outline-secondary btn-sm"
              onClick={() => {
                setForm(profile);
                setEditing(false);
                setError("");
              }}
            >
              Cancel
            </button>
          )}
        </div>
      </div>

      {error && <div className="alert alert-danger py-2 small">{error}</div>}
      {success && <div className="alert alert-success py-2 small">{success}</div>}

      <div className="row g-4">
        {/* LEFT COLUMN: Overview Card */}
        <div className="col-md-4">
          <div className="content-card text-center">
            <div className="content-card-body p-4">
              <div
                className="rounded-circle bg-primary text-white d-inline-flex align-items-center justify-content-center mb-3 shadow-sm"
                style={{ width: "80px", height: "80px", fontSize: "2rem" }}
              >
                {profile?.fullName ? profile.fullName.charAt(0) : "S"}
              </div>
              <h5 className="fw-bold mb-1">{profile?.fullName}</h5>
              <p className="text-muted small mb-2">{profile?.department}</p>
              <span className="badge bg-success mb-3">{profile?.placementStatus || "UNPLACED"}</span>

              <hr />

              <div className="text-start small">
                <div className="d-flex justify-content-between py-1 border-bottom">
                  <span className="text-muted">CGPA</span>
                  <strong>{profile?.cgpa?.toFixed(2) || "N/A"}</strong>
                </div>
                <div className="d-flex justify-content-between py-1 border-bottom">
                  <span className="text-muted">Standing Backlogs</span>
                  <span className={profile?.backlogs > 0 ? "badge bg-danger" : "badge bg-success"}>
                    {profile?.backlogs || 0}
                  </span>
                </div>
                <div className="d-flex justify-content-between py-1 border-bottom">
                  <span className="text-muted">Graduation Year</span>
                  <strong>{profile?.graduationYear || "N/A"}</strong>
                </div>
                <div className="d-flex justify-content-between py-1 border-bottom">
                  <span className="text-muted">Batch</span>
                  <span>{profile?.academicYear || "N/A"}</span>
                </div>
                <div className="d-flex justify-content-between py-1">
                  <span className="text-muted">College</span>
                  <span className="text-end text-truncate" style={{ maxWidth: "160px" }}>
                    {profile?.collegeName}
                  </span>
                </div>
              </div>
            </div>
          </div>

          {/* Technical Skills Box */}
          <div className="content-card">
            <div className="content-card-header">
              <h5><i className="bi bi-tools me-1 text-primary"></i> Skills &amp; Stack</h5>
            </div>
            <div className="content-card-body">
              {skillsList.length === 0 ? (
                <span className="text-muted small">No skills specified yet.</span>
              ) : (
                <div className="d-flex flex-wrap gap-1">
                  {skillsList.map((skill, idx) => (
                    <span key={idx} className="badge bg-light text-dark border">
                      {skill}
                    </span>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>

        {/* RIGHT COLUMN: Details & Edit Form */}
        <div className="col-md-8">
          <div className="content-card">
            <div className="content-card-header">
              <h5>{editing ? "Edit Profile Details" : "Academic & Contact Details"}</h5>
            </div>
            <div className="content-card-body">
              {!editing ? (
                <div className="row g-3">
                  <div className="col-md-6">
                    <label className="text-muted small">Full Name</label>
                    <div className="fw-semibold">{profile?.fullName}</div>
                  </div>
                  <div className="col-md-6">
                    <label className="text-muted small">Roll / Register Number</label>
                    <div className="fw-semibold">{profile?.rollNumber}</div>
                  </div>
                  <div className="col-md-6">
                    <label className="text-muted small">Email Address</label>
                    <div className="fw-semibold">{profile?.email}</div>
                  </div>
                  <div className="col-md-6">
                    <label className="text-muted small">Phone Number</label>
                    <div className="fw-semibold">{profile?.phoneNumber || "Not provided"}</div>
                  </div>
                  <div className="col-md-6">
                    <label className="text-muted small">Department</label>
                    <div className="fw-semibold">{profile?.department}</div>
                  </div>
                  <div className="col-md-6">
                    <label className="text-muted small">Academic Year</label>
                    <div className="fw-semibold">{profile?.academicYear || "Final Year"}</div>
                  </div>
                  <div className="col-md-4">
                    <label className="text-muted small">CGPA</label>
                    <div className="fw-semibold">{profile?.cgpa}</div>
                  </div>
                  <div className="col-md-4">
                    <label className="text-muted small">Backlogs</label>
                    <div className="fw-semibold">{profile?.backlogs}</div>
                  </div>
                  <div className="col-md-4">
                    <label className="text-muted small">Graduation Year</label>
                    <div className="fw-semibold">{profile?.graduationYear}</div>
                  </div>
                  <div className="col-12">
                    <label className="text-muted small">Skills</label>
                    <div className="fw-semibold">{profile?.skills || "None"}</div>
                  </div>
                  <div className="col-12">
                    <label className="text-muted small">Certifications</label>
                    <div className="fw-semibold">{profile?.certifications || "None listed"}</div>
                  </div>
                  <div className="col-12">
                    <label className="text-muted small">Resume URL</label>
                    <div>
                      {profile?.resumeUrl ? (
                        <a
                          href={profile.resumeUrl}
                          target="_blank"
                          rel="noreferrer"
                          className="btn btn-sm btn-outline-primary"
                        >
                          <i className="bi bi-file-earmark-pdf me-1"></i> View Resume
                        </a>
                      ) : (
                        <span className="text-muted small">No resume link provided.</span>
                      )}
                    </div>
                  </div>
                </div>
              ) : (
                <form onSubmit={handleSave}>
                  <div className="row g-3">
                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Full Name</label>
                      <input
                        type="text"
                        name="fullName"
                        className="form-control form-control-sm"
                        value={form.fullName || ""}
                        onChange={handleChange}
                        required
                      />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Phone Number</label>
                      <input
                        type="text"
                        name="phoneNumber"
                        className="form-control form-control-sm"
                        value={form.phoneNumber || ""}
                        onChange={handleChange}
                      />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Department</label>
                      <select
                        name="department"
                        className="form-select form-select-sm"
                        value={form.department || ""}
                        onChange={handleChange}
                      >
                        <option value="Computer Science & Engineering">Computer Science & Engineering</option>
                        <option value="Information Technology">Information Technology</option>
                        <option value="Electronics & Communication">Electronics & Communication</option>
                        <option value="Electrical & Electronics">Electrical & Electronics</option>
                        <option value="Mechanical Engineering">Mechanical Engineering</option>
                      </select>
                    </div>
                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Academic Year</label>
                      <input
                        type="text"
                        name="academicYear"
                        className="form-control form-control-sm"
                        value={form.academicYear || ""}
                        onChange={handleChange}
                      />
                    </div>
                    <div className="col-md-4">
                      <label className="form-label small fw-semibold">CGPA</label>
                      <input
                        type="number"
                        step="0.01"
                        min="0"
                        max="10"
                        name="cgpa"
                        className="form-control form-control-sm"
                        value={form.cgpa || 0}
                        onChange={handleChange}
                        required
                      />
                    </div>
                    <div className="col-md-4">
                      <label className="form-label small fw-semibold">Backlogs</label>
                      <input
                        type="number"
                        min="0"
                        name="backlogs"
                        className="form-control form-control-sm"
                        value={form.backlogs || 0}
                        onChange={handleChange}
                        required
                      />
                    </div>
                    <div className="col-md-4">
                      <label className="form-label small fw-semibold">Graduation Year</label>
                      <input
                        type="number"
                        name="graduationYear"
                        className="form-control form-control-sm"
                        value={form.graduationYear || 2026}
                        onChange={handleChange}
                        required
                      />
                    </div>
                    <div className="col-12">
                      <label className="form-label small fw-semibold">Skills (Comma-separated)</label>
                      <input
                        type="text"
                        name="skills"
                        className="form-control form-control-sm"
                        value={form.skills || ""}
                        onChange={handleChange}
                      />
                    </div>
                    <div className="col-12">
                      <label className="form-label small fw-semibold">Certifications</label>
                      <input
                        type="text"
                        name="certifications"
                        className="form-control form-control-sm"
                        value={form.certifications || ""}
                        onChange={handleChange}
                      />
                    </div>
                    <div className="col-12">
                      <label className="form-label small fw-semibold">Resume Link / URL</label>
                      <input
                        type="url"
                        name="resumeUrl"
                        className="form-control form-control-sm"
                        placeholder="https://drive.google.com/..."
                        value={form.resumeUrl || ""}
                        onChange={handleChange}
                      />
                    </div>
                  </div>

                  <div className="mt-4 d-flex justify-content-end gap-2">
                    <button
                      type="button"
                      className="btn btn-secondary btn-sm"
                      onClick={() => setEditing(false)}
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      className="btn btn-primary btn-sm"
                      disabled={saving}
                    >
                      {saving ? "Saving Changes..." : "Save Profile"}
                    </button>
                  </div>
                </form>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StudentProfilePage;