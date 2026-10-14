import React, { useState, useEffect } from "react";
import { getCompanyProfile, updateCompanyProfile } from "../../services/recruiterService";
import Loader from "../../components/common/Loader";

const CompanyProfilePage = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState({});
  const [editing, setEditing] = useState(false);
  const [saving, setSaving] = useState(false);
  const [msg, setMsg] = useState({ text: "", type: "" });

  const loadProfile = async () => {
    try {
      setLoading(true);
      const res = await getCompanyProfile();
      if (res.success && res.data) {
        setProfile(res.data);
        setForm(res.data);
      }
    } catch (err) {
      console.error("Error loading company profile:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProfile();
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setMsg({ text: "", type: "" });
    try {
      const res = await updateCompanyProfile(form);
      if (res.success && res.data) {
        setProfile(res.data);
        setEditing(false);
        setMsg({ text: "Company profile updated successfully!", type: "success" });
      } else {
        setMsg({ text: res.message || "Failed to update profile.", type: "danger" });
      }
    } catch (err) {
      setMsg({
        text: err.response?.data?.message || "Failed to update profile.",
        type: "danger",
      });
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <Loader message="Loading corporate organization profile..." />;

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Company Corporate Profile</h3>
          <p className="text-muted small mb-0">
            Enterprise branding, recruiter primary points of contact, and industry affiliations.
          </p>
        </div>
        <div>
          {!editing ? (
            <button className="btn btn-primary btn-sm" onClick={() => setEditing(true)}>
              <i className="bi bi-pencil-square me-1"></i> Edit Organization Details
            </button>
          ) : (
            <button
              className="btn btn-outline-secondary btn-sm"
              onClick={() => {
                setForm(profile);
                setEditing(false);
              }}
            >
              Cancel
            </button>
          )}
        </div>
      </div>

      {msg.text && (
        <div className={`alert alert-${msg.type} py-2 small d-flex align-items-center gap-2 mb-3`}>
          <i className="bi bi-info-circle-fill"></i>
          <div>{msg.text}</div>
        </div>
      )}

      <div className="row g-4">
        <div className="col-md-4">
          <div className="content-card text-center">
            <div className="content-card-body p-4">
              <div
                className="rounded-circle bg-dark text-white d-inline-flex align-items-center justify-content-center mb-3 shadow-sm"
                style={{ width: "80px", height: "80px", fontSize: "2rem" }}
              >
                <i className="bi bi-building"></i>
              </div>
              <h4 className="fw-bold mb-1">{profile?.name}</h4>
              <p className="text-muted small mb-2">{profile?.industry || "Software & Technology"}</p>
              <span
                className={`badge ${
                  profile?.verified ? "bg-success" : "bg-warning text-dark"
                }`}
              >
                {profile?.verified ? "VERIFIED RECRUITMENT PARTNER" : "UNVERIFIED"}
              </span>

              <hr />

              <div className="text-start small">
                <div className="py-1">
                  <span className="text-muted d-block">Official Website:</span>
                  <a href={profile?.website} target="_blank" rel="noreferrer" className="text-primary text-truncate d-block">
                    {profile?.website || "https://company.com"}
                  </a>
                </div>
                <div className="py-1">
                  <span className="text-muted d-block">Headquarters / Location:</span>
                  <strong>{profile?.location || "Chennai, Tamil Nadu"}</strong>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="col-md-8">
          <div className="content-card">
            <div className="content-card-header">
              <h5>{editing ? "Edit Corporate Profile" : "Corporate Overview & Contact Leads"}</h5>
            </div>
            <div className="content-card-body">
              {!editing ? (
                <div className="row g-3">
                  <div className="col-md-6">
                    <label className="text-muted small">Organization Name</label>
                    <div className="fw-bold">{profile?.name}</div>
                  </div>
                  <div className="col-md-6">
                    <label className="text-muted small">Industry Sector</label>
                    <div className="fw-semibold">{profile?.industry}</div>
                  </div>
                  <div className="col-12">
                    <label className="text-muted small">About Company</label>
                    <p className="text-secondary small mt-1">{profile?.description || "No description."}</p>
                  </div>
                  <div className="col-md-6">
                    <label className="text-muted small">HR Campus Lead</label>
                    <div className="fw-semibold">{profile?.contactPerson || "Lead Recruiter"}</div>
                  </div>
                  <div className="col-md-6">
                    <label className="text-muted small">Contact Email</label>
                    <div className="fw-semibold">{profile?.contactEmail}</div>
                  </div>
                  <div className="col-md-6">
                    <label className="text-muted small">Contact Phone</label>
                    <div className="fw-semibold">{profile?.contactPhone || "+91 9444001122"}</div>
                  </div>
                  <div className="col-md-6">
                    <label className="text-muted small">Website URL</label>
                    <div>{profile?.website}</div>
                  </div>
                </div>
              ) : (
                <form onSubmit={handleSave}>
                  <div className="row g-3">
                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Company Name *</label>
                      <input
                        type="text"
                        name="name"
                        className="form-control form-control-sm"
                        value={form.name || ""}
                        onChange={handleChange}
                        required
                      />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Industry Sector</label>
                      <input
                        type="text"
                        name="industry"
                        className="form-control form-control-sm"
                        value={form.industry || ""}
                        onChange={handleChange}
                      />
                    </div>
                    <div className="col-12">
                      <label className="form-label small fw-semibold">About Company</label>
                      <textarea
                        rows="3"
                        name="description"
                        className="form-control form-control-sm"
                        value={form.description || ""}
                        onChange={handleChange}
                      ></textarea>
                    </div>
                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">HR Campus Lead Name</label>
                      <input
                        type="text"
                        name="contactPerson"
                        className="form-control form-control-sm"
                        value={form.contactPerson || ""}
                        onChange={handleChange}
                      />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Contact Email</label>
                      <input
                        type="email"
                        name="contactEmail"
                        className="form-control form-control-sm"
                        value={form.contactEmail || ""}
                        onChange={handleChange}
                      />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Contact Phone</label>
                      <input
                        type="text"
                        name="contactPhone"
                        className="form-control form-control-sm"
                        value={form.contactPhone || ""}
                        onChange={handleChange}
                      />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Website</label>
                      <input
                        type="url"
                        name="website"
                        className="form-control form-control-sm"
                        value={form.website || ""}
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
                      {saving ? "Saving..." : "Save Changes"}
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

export default CompanyProfilePage;
