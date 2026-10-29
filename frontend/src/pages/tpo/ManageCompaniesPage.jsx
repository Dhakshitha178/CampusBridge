import React, { useState, useEffect } from "react";
import { getCompaniesList, verifyCompany, getPendingUsers, approveUser } from "../../services/tpoService";
import Loader from "../../components/common/Loader";

const ManageCompaniesPage = () => {
  const [activeTab, setActiveTab] = useState("COMPANIES"); // "COMPANIES" or "PENDING_ACCREDITATIONS"
  const [companies, setCompanies] = useState([]);
  const [pendingUsers, setPendingUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [verifyingId, setVerifyingId] = useState(null);
  const [approvingUserId, setApprovingUserId] = useState(null);
  const [msg, setMsg] = useState({ text: "", type: "" });

  const loadData = async () => {
    try {
      setLoading(true);
      const [compRes, usersRes] = await Promise.all([
        getCompaniesList(),
        getPendingUsers().catch(() => ({ success: true, data: [] })),
      ]);
      if (compRes.success && compRes.data) {
        setCompanies(compRes.data);
      }
      if (usersRes.success && usersRes.data) {
        setPendingUsers(usersRes.data);
      }
    } catch (err) {
      console.error("Error loading data:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleApproveUser = async (userId, userName) => {
    setApprovingUserId(userId);
    setMsg({ text: "", type: "" });
    try {
      const res = await approveUser(userId);
      if (res.success) {
        setMsg({
          text: `Account for ${userName} has been successfully approved and accredited!`,
          type: "success",
        });
        loadData();
      } else {
        setMsg({ text: res.message || "Failed to approve user.", type: "danger" });
      }
    } catch (err) {
      setMsg({
        text: err.response?.data?.message || "Error approving account.",
        type: "danger",
      });
    } finally {
      setApprovingUserId(null);
    }
  };

  const handleToggleVerify = async (companyId, currentVerified) => {
    setVerifyingId(companyId);
    setMsg({ text: "", type: "" });
    try {
      const res = await verifyCompany(companyId, !currentVerified);
      if (res.success) {
        setMsg({
          text: `Company verification status updated successfully.`,
          type: "success",
        });
        loadData();
      } else {
        setMsg({ text: res.message || "Failed to update verification.", type: "danger" });
      }
    } catch (err) {
      setMsg({
        text: err.response?.data?.message || "Error updating company status.",
        type: "danger",
      });
    } finally {
      setVerifyingId(null);
    }
  };

  if (loading) return <Loader message="Loading corporate recruitment partner registry..." />;

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Company Registry &amp; Identity Accreditation</h3>
          <p className="text-muted small mb-0">
            Manage recruiting corporate partners, verification accreditations, and placement affiliations.
          </p>
        </div>
        <div>
          <div className="btn-group" role="group">
            <button
              type="button"
              className={`btn ${activeTab === "COMPANIES" ? "btn-primary" : "btn-outline-primary"} btn-sm px-3`}
              onClick={() => setActiveTab("COMPANIES")}
            >
              <i className="bi bi-building me-1"></i> Corporate Partners ({companies.length})
            </button>
            <button
              type="button"
              className={`btn ${activeTab === "PENDING_ACCREDITATIONS" ? "btn-warning text-dark fw-bold" : "btn-outline-warning text-dark"} btn-sm px-3`}
              onClick={() => setActiveTab("PENDING_ACCREDITATIONS")}
            >
              <i className="bi bi-person-check-fill me-1"></i> Pending Registrations
              {pendingUsers.length > 0 && (
                <span className="badge bg-danger ms-2">{pendingUsers.length}</span>
              )}
            </button>
          </div>
        </div>
      </div>

      {msg.text && (
        <div className={`alert alert-${msg.type} py-2 small d-flex align-items-center gap-2 mb-3`}>
          <i className="bi bi-info-circle-fill"></i>
          <div>{msg.text}</div>
        </div>
      )}

      {activeTab === "PENDING_ACCREDITATIONS" ? (
        <div className="card shadow-sm border-0" style={{ borderRadius: "10px" }}>
          <div className="card-header bg-white py-3 d-flex justify-content-between align-items-center">
            <h5 className="fw-bold mb-0">
              <i className="bi bi-shield-exclamation text-warning me-2"></i>
              Registrations Awaiting Placement Accreditation
            </h5>
            <span className="badge bg-warning text-dark">{pendingUsers.length} Pending Approval</span>
          </div>
          <div className="card-body p-0">
            {pendingUsers.length === 0 ? (
              <div className="text-center p-5 text-muted">
                <i className="bi bi-check-circle-fill text-success fs-1 d-block mb-2"></i>
                No accounts currently pending accreditation. All users are verified.
              </div>
            ) : (
              <div className="table-responsive">
                <table className="table table-hover align-middle mb-0">
                  <thead className="table-light small text-uppercase">
                    <tr>
                      <th className="ps-4">Representative</th>
                      <th>Email ID</th>
                      <th>Role</th>
                      <th>Designation</th>
                      <th>Contact</th>
                      <th>Status</th>
                      <th className="text-end pe-4">Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {pendingUsers.map((u) => (
                      <tr key={u.id}>
                        <td className="ps-4">
                          <div className="fw-bold text-dark">{u.fullName}</div>
                          <small className="text-muted">User ID: #{u.id}</small>
                        </td>
                        <td>
                          <span className="text-monospace small">{u.email}</span>
                        </td>
                        <td>
                          <span
                            className={`badge ${
                              u.role === "RECRUITER"
                                ? "bg-warning text-dark"
                                : u.role === "TPO"
                                ? "bg-info text-white"
                                : "bg-secondary"
                            }`}
                          >
                            {u.role}
                          </span>
                        </td>
                        <td>{u.designation || "Authorized Representative"}</td>
                        <td>{u.phoneNumber || "N/A"}</td>
                        <td>
                          <span className="badge bg-warning text-dark">
                            {u.approvalStatus || "PENDING"}
                          </span>
                        </td>
                        <td className="text-end pe-4">
                          <button
                            className="btn btn-sm btn-success fw-semibold"
                            disabled={approvingUserId === u.id}
                            onClick={() => handleApproveUser(u.id, u.fullName)}
                          >
                            {approvingUserId === u.id ? (
                              <span>
                                <span className="spinner-border spinner-border-sm me-1"></span>
                                Approving...
                              </span>
                            ) : (
                              <>
                                <i className="bi bi-check-lg me-1"></i>
                                Approve &amp; Accredit
                              </>
                            )}
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      ) : companies.length === 0 ? (
        <div className="text-center p-5 bg-white rounded border text-muted">
          No corporate partners registered yet.
        </div>
      ) : (
        <div className="row g-4">
          {companies.map((comp) => (
            <div key={comp.id} className="col-md-6">
              <div className="card h-100 shadow-sm border-0" style={{ borderRadius: "10px" }}>
                <div className="card-body p-4 d-flex flex-column justify-content-between">
                  <div>
                    <div className="d-flex justify-content-between align-items-start mb-2">
                      <h4 className="fw-bold text-dark mb-0">{comp.name}</h4>
                      <span
                        className={`badge ${
                          comp.verified ? "bg-success" : "bg-warning text-dark"
                        }`}
                      >
                        {comp.verified ? "VERIFIED PARTNER" : "UNVERIFIED"}
                      </span>
                    </div>

                    <div className="badge bg-light text-dark border mb-3">
                      {comp.industry || "Software & Technology"}
                    </div>

                    <p className="text-secondary small mb-3" style={{ lineHeight: "1.4" }}>
                      {comp.description || "Leading global IT services and consulting enterprise."}
                    </p>

                    <div className="p-3 bg-light rounded small mb-3">
                      <div className="d-flex justify-content-between py-1 border-bottom">
                        <span className="text-muted">Contact Person:</span>
                        <strong>{comp.contactPerson || "HR Campus Lead"}</strong>
                      </div>
                      <div className="d-flex justify-content-between py-1 border-bottom">
                        <span className="text-muted">Corporate Email:</span>
                        <span>{comp.contactEmail}</span>
                      </div>
                      <div className="d-flex justify-content-between py-1">
                        <span className="text-muted">Official Portal:</span>
                        <span>
                          {comp.website ? (
                            <a href={comp.website} target="_blank" rel="noreferrer" className="text-primary">
                              {comp.website} &rarr;
                            </a>
                          ) : (
                            "N/A"
                          )}
                        </span>
                      </div>
                    </div>
                  </div>

                  <div className="d-flex justify-content-between align-items-center pt-3 border-top">
                    <span className="small text-muted">
                      Status: <strong>{comp.active ? "Active" : "Inactive"}</strong>
                    </span>
                    <button
                      className={`btn btn-sm ${comp.verified ? "btn-outline-danger" : "btn-success"}`}
                      disabled={verifyingId === comp.id}
                      onClick={() => handleToggleVerify(comp.id, comp.verified)}
                    >
                      {verifyingId === comp.id
                        ? "Updating..."
                        : comp.verified
                        ? "Revoke Verification"
                        : "Verify Partner"}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ManageCompaniesPage;
