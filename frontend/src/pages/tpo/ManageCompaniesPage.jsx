import React, { useState, useEffect } from "react";
import { getCompaniesList, verifyCompany } from "../../services/tpoService";
import Loader from "../../components/common/Loader";

const ManageCompaniesPage = () => {
  const [companies, setCompanies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [verifyingId, setVerifyingId] = useState(null);
  const [msg, setMsg] = useState({ text: "", type: "" });

  const loadCompanies = async () => {
    try {
      setLoading(true);
      const res = await getCompaniesList();
      if (res.success && res.data) {
        setCompanies(res.data);
      }
    } catch (err) {
      console.error("Error loading companies:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCompanies();
  }, []);

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
        loadCompanies();
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
          <h3 className="fw-bold text-dark mb-1">Company Registry &amp; Industry Connect</h3>
          <p className="text-muted small mb-0">
            Manage recruiting corporate partners, verification accreditations, and placement affiliations.
          </p>
        </div>
        <div>
          <span className="badge bg-primary px-3 py-2">
            {companies.length} Corporate Partners
          </span>
        </div>
      </div>

      {msg.text && (
        <div className={`alert alert-${msg.type} py-2 small d-flex align-items-center gap-2 mb-3`}>
          <i className="bi bi-info-circle-fill"></i>
          <div>{msg.text}</div>
        </div>
      )}

      {companies.length === 0 ? (
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
