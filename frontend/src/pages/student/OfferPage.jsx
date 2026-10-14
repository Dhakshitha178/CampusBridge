import React, { useState, useEffect } from "react";
import { getStudentOffers, respondToOffer } from "../../services/studentService";
import Loader from "../../components/common/Loader";

const OfferPage = () => {
  const [offers, setOffers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actingId, setActingId] = useState(null);
  const [msg, setMsg] = useState({ text: "", type: "" });

  const loadOffers = async () => {
    try {
      setLoading(true);
      const res = await getStudentOffers();
      if (res.success && res.data) {
        setOffers(res.data);
      }
    } catch (err) {
      console.error("Error loading offers:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadOffers();
  }, []);

  const handleRespond = async (offerId, accept) => {
    const actionWord = accept ? "ACCEPT" : "DECLINE";
    if (!window.confirm(`Are you sure you want to ${actionWord} this employment offer?`)) return;

    setActingId(offerId);
    setMsg({ text: "", type: "" });
    try {
      const res = await respondToOffer(offerId, accept);
      if (res.success) {
        setMsg({
          text: `Offer successfully ${accept ? "ACCEPTED! Congratulations!" : "DECLINED."}`,
          type: accept ? "success" : "info",
        });
        loadOffers();
      } else {
        setMsg({ text: res.message || "Failed to respond to offer.", type: "danger" });
      }
    } catch (err) {
      setMsg({
        text: err.response?.data?.message || "Failed to update offer decision.",
        type: "danger",
      });
    } finally {
      setActingId(null);
    }
  };

  if (loading) return <Loader message="Loading job offers..." />;

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Placement Offers &amp; Selections</h3>
          <p className="text-muted small mb-0">
            Review employment letters, joining dates, and officially accept your placement offers.
          </p>
        </div>
        <div>
          <span className="badge bg-success px-3 py-2">
            <i className="bi bi-award-fill me-1"></i> {offers.length} Offer(s) Received
          </span>
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
          <i className="bi bi-award fs-1 text-secondary mb-2 d-block"></i>
          No job offers recorded yet. Keep preparing and participating in active drives!
        </div>
      ) : (
        <div className="row g-4">
          {offers.map((offer) => {
            const isPending = offer.status === "OFFERED" || offer.status === "PENDING";
            const isAccepted = offer.status === "ACCEPTED";
            const isRejected = offer.status === "REJECTED";

            return (
              <div key={offer.id} className="col-md-6">
                <div
                  className={`card h-100 shadow-sm border-0 ${
                    isAccepted ? "border-top border-success border-4" : ""
                  }`}
                  style={{ borderRadius: "10px" }}
                >
                  <div className="card-body p-4 d-flex flex-column justify-content-between">
                    <div>
                      <div className="d-flex justify-content-between align-items-start mb-2">
                        <span className="badge bg-light text-dark border">
                          <i className="bi bi-building me-1"></i> {offer.companyName}
                        </span>
                        <span
                          className={`badge ${
                            isAccepted
                              ? "bg-success"
                              : isRejected
                              ? "bg-danger"
                              : "bg-warning text-dark"
                          }`}
                        >
                          {offer.status}
                        </span>
                      </div>

                      <h4 className="fw-bold text-dark mb-1">{offer.designation || offer.role}</h4>
                      <div className="text-muted small mb-3">Drive: {offer.driveTitle}</div>

                      <div className="p-3 bg-light rounded mb-3 small">
                        <div className="d-flex justify-content-between py-1 border-bottom">
                          <span className="text-muted">Annual CTC Package</span>
                          <strong className="text-success fs-6">{offer.ctcPackage} LPA</strong>
                        </div>
                        <div className="d-flex justify-content-between py-1 border-bottom">
                          <span className="text-muted">Work Location</span>
                          <strong>{offer.location || "Bangalore / Chennai"}</strong>
                        </div>
                        <div className="d-flex justify-content-between py-1 border-bottom">
                          <span className="text-muted">Expected Joining Date</span>
                          <strong>{offer.joiningDate || "July 2026"}</strong>
                        </div>
                        <div className="d-flex justify-content-between py-1">
                          <span className="text-muted">Offer Issued On</span>
                          <span>{offer.createdAt ? new Date(offer.createdAt).toLocaleDateString() : "Recent"}</span>
                        </div>
                      </div>

                      {offer.offerLetterUrl && (
                        <div className="mb-3">
                          <a
                            href={offer.offerLetterUrl}
                            target="_blank"
                            rel="noreferrer"
                            className="btn btn-outline-primary btn-sm w-100"
                          >
                            <i className="bi bi-file-earmark-pdf me-1"></i> View Official Offer Letter
                          </a>
                        </div>
                      )}
                    </div>

                    <div className="pt-3 border-top">
                      {isPending ? (
                        <div className="d-flex gap-2">
                          <button
                            className="btn btn-success btn-sm flex-grow-1"
                            disabled={actingId === offer.id}
                            onClick={() => handleRespond(offer.id, true)}
                          >
                            <i className="bi bi-check-lg me-1"></i> Accept Offer
                          </button>
                          <button
                            className="btn btn-outline-danger btn-sm"
                            disabled={actingId === offer.id}
                            onClick={() => handleRespond(offer.id, false)}
                          >
                            Decline
                          </button>
                        </div>
                      ) : isAccepted ? (
                        <div className="text-center p-2 bg-success-subtle text-success rounded small fw-bold">
                          <i className="bi bi-patch-check-fill me-1"></i> You have officially ACCEPTED this offer!
                        </div>
                      ) : (
                        <div className="text-center p-2 bg-secondary-subtle text-muted rounded small">
                          This offer was declined.
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default OfferPage;
