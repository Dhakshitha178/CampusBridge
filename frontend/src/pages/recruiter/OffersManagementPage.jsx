import React, { useState, useEffect } from "react";
import {
  getRecruiterOffers,
  createOffer,
  getRecruiterDrives,
  getDriveApplicants,
} from "../../services/recruiterService";
import Loader from "../../components/common/Loader";

const OffersManagementPage = () => {
  const [offers, setOffers] = useState([]);
  const [drives, setDrives] = useState([]);
  const [applicants, setApplicants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [msg, setMsg] = useState({ text: "", type: "" });

  const [form, setForm] = useState({
    driveId: "",
    applicationId: "",
    studentId: "",
    designation: "Associate Software Engineer",
    ctcPackage: 8.5,
    location: "Chennai, Tamil Nadu",
    joiningDate: "2026-07-01",
    offerLetterUrl: "https://campusbridge.ac.in/letters/offer-sample.pdf",
  });

  const loadData = async () => {
    try {
      setLoading(true);
      const [offRes, drivesRes] = await Promise.all([
        getRecruiterOffers(),
        getRecruiterDrives(),
      ]);
      if (offRes.success && offRes.data) {
        setOffers(offRes.data);
      }
      if (drivesRes.success && drivesRes.data && drivesRes.data.length > 0) {
        setDrives(drivesRes.data);
        setForm((prev) => ({ ...prev, driveId: drivesRes.data[0].id }));
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
      console.error("Error loading offers data:", err);
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
    } catch (e) {
      console.warn("Could not load applicants for drive");
    }
  };

  const handleApplicantChange = (appId) => {
    const app = applicants.find((a) => a.id === Number(appId));
    if (app) {
      setForm({ ...form, applicationId: app.id, studentId: app.studentId });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setMsg({ text: "", type: "" });
    try {
      const res = await createOffer(form);
      if (res.success) {
        setMsg({ text: "Employment offer extended successfully to the candidate!", type: "success" });
        setModalOpen(false);
        loadData();
      } else {
        setMsg({ text: res.message || "Failed to issue offer.", type: "danger" });
      }
    } catch (err) {
      setMsg({
        text: err.response?.data?.message || "Failed to extend job offer.",
        type: "danger",
      });
    } finally {
      setSaving(false);
    }
  };

  if (loading && offers.length === 0) {
    return <Loader message="Loading recruiter employment offers..." />;
  }

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Offer Management &amp; Issuance 📜</h3>
          <p className="text-muted small mb-0">
            Generate formal employment contracts, CTC compensation breakdowns, and track student acceptances.
          </p>
        </div>
        <div>
          <button className="btn btn-success btn-sm" onClick={() => setModalOpen(true)}>
            <i className="bi bi-send-plus me-1"></i> Issue New Job Offer
          </button>
        </div>
      </div>

      {msg.text && (
        <div className={`alert alert-${msg.type} py-2 small d-flex align-items-center gap-2 mb-3`}>
          <i className="bi bi-info-circle-fill"></i>
          <div>{msg.text}</div>
        </div>
      )}

      {offers.length === 0 ? (
        <div className="text-center p-5 bg-white rounded border text-muted">
          No job offers issued yet. Click "Issue New Job Offer" above.
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
                    <th>Role Designation</th>
                    <th>Package (CTC)</th>
                    <th>Work Location</th>
                    <th>Joining Date</th>
                    <th>Offer Status</th>
                  </tr>
                </thead>
                <tbody className="small">
                  {offers.map((off) => (
                    <tr key={off.id}>
                      <td className="fw-bold text-dark">{off.studentName}</td>
                      <td><span className="badge bg-light text-dark border">{off.studentRollNumber}</span></td>
                      <td>{off.designation || off.role}</td>
                      <td><strong className="text-success">{off.ctcPackage} LPA</strong></td>
                      <td>{off.location}</td>
                      <td>{off.joiningDate}</td>
                      <td>
                        <span
                          className={`badge ${
                            off.status === "ACCEPTED"
                              ? "bg-success"
                              : off.status === "REJECTED"
                              ? "bg-danger"
                              : "bg-warning text-dark"
                          }`}
                        >
                          {off.status}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* ISSUE OFFER MODAL */}
      {modalOpen && (
        <div
          className="modal show d-block"
          tabIndex="-1"
          style={{ backgroundColor: "rgba(0,0,0,0.5)" }}
        >
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content shadow-lg border-0" style={{ borderRadius: "10px" }}>
              <div className="modal-header bg-success text-white">
                <h5 className="modal-title fs-6">
                  <i className="bi bi-award-fill me-2"></i> Issue Formal Placement Offer
                </h5>
                <button
                  type="button"
                  className="btn-close btn-close-white"
                  onClick={() => setModalOpen(false)}
                ></button>
              </div>
              <form onSubmit={handleSubmit}>
                <div className="modal-body p-4">
                  <div className="mb-2">
                    <label className="form-label small fw-semibold">Recruitment Drive *</label>
                    <select
                      className="form-select form-select-sm"
                      value={form.driveId}
                      onChange={(e) => handleDriveChange(e.target.value)}
                      required
                    >
                      {drives.map((d) => (
                        <option key={d.id} value={d.id}>{d.title}</option>
                      ))}
                    </select>
                  </div>

                  <div className="mb-2">
                    <label className="form-label small fw-semibold">Select Candidate *</label>
                    <select
                      className="form-select form-select-sm"
                      value={form.applicationId}
                      onChange={(e) => handleApplicantChange(e.target.value)}
                      required
                    >
                      {applicants.map((a) => (
                        <option key={a.id} value={a.id}>
                          {a.studentName} ({a.studentRollNumber})
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="mb-2">
                    <label className="form-label small fw-semibold">Job Designation *</label>
                    <input
                      type="text"
                      className="form-control form-control-sm"
                      value={form.designation}
                      onChange={(e) => setForm({ ...form, designation: e.target.value })}
                      required
                    />
                  </div>

                  <div className="row g-2 mb-2">
                    <div className="col-6">
                      <label className="form-label small fw-semibold">CTC Package (LPA) *</label>
                      <input
                        type="number"
                        step="0.1"
                        className="form-control form-control-sm"
                        value={form.ctcPackage}
                        onChange={(e) =>
                          setForm({ ...form, ctcPackage: parseFloat(e.target.value) || 0 })
                        }
                        required
                      />
                    </div>
                    <div className="col-6">
                      <label className="form-label small fw-semibold">Joining Date *</label>
                      <input
                        type="date"
                        className="form-control form-control-sm"
                        value={form.joiningDate}
                        onChange={(e) => setForm({ ...form, joiningDate: e.target.value })}
                        required
                      />
                    </div>
                  </div>

                  <div className="mb-2">
                    <label className="form-label small fw-semibold">Work Location</label>
                    <input
                      type="text"
                      className="form-control form-control-sm"
                      value={form.location}
                      onChange={(e) => setForm({ ...form, location: e.target.value })}
                    />
                  </div>

                  <div className="mb-2">
                    <label className="form-label small fw-semibold">Offer Document URL</label>
                    <input
                      type="url"
                      className="form-control form-control-sm"
                      value={form.offerLetterUrl}
                      onChange={(e) => setForm({ ...form, offerLetterUrl: e.target.value })}
                    />
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
                    disabled={saving}
                  >
                    {saving ? "Extending Offer..." : "Roll Out Offer"}
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

export default OffersManagementPage;
