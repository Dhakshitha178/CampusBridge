import React, { useState, useContext, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../../context/AuthContext";
import { verifyEmail, checkUserStatus } from "../../services/authService";

const PendingApprovalPage = () => {
  const navigate = useNavigate();
  const { user, login, logout } = useContext(AuthContext);

  const [verificationCode, setVerificationCode] = useState("");
  const [email, setEmail] = useState(user?.email || "");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [checkingStatus, setCheckingStatus] = useState(false);

  useEffect(() => {
    if (user?.email) {
      setEmail(user.email);
    }
  }, [user]);

  const handleVerifyCode = async (e) => {
    e.preventDefault();
    if (!verificationCode || verificationCode.trim().length !== 6) {
      setError("Please enter a valid 6-digit verification code.");
      return;
    }
    setError("");
    setSuccess("");
    setLoading(true);

    try {
      const res = await verifyEmail(email, verificationCode.trim());
      if (res.success && res.data) {
        setSuccess("Email verified and account accredited successfully! Redirecting...");
        login(res.data, res.data.token);
        setTimeout(() => {
          if (res.data.role === "RECRUITER") {
            navigate("/recruiter/dashboard");
          } else if (res.data.role === "TPO") {
            navigate("/tpo/dashboard");
          } else {
            navigate("/student/dashboard");
          }
        }, 1500);
      } else {
        setError(res.message || "Verification failed. Please check the code.");
      }
    } catch (err) {
      setError(err.response?.data?.message || "Invalid or expired verification code.");
    } finally {
      setLoading(false);
    }
  };

  const handleCheckStatus = async () => {
    if (!email) {
      setError("Please specify the account email address.");
      return;
    }
    setError("");
    setSuccess("");
    setCheckingStatus(true);

    try {
      const res = await checkUserStatus(email);
      if (res.success && res.data) {
        if (res.data.approvalStatus === "APPROVED") {
          setSuccess("Account has been approved! Redirecting to dashboard...");
          login(res.data, res.data.token);
          setTimeout(() => {
            if (res.data.role === "RECRUITER") {
              navigate("/recruiter/dashboard");
            } else if (res.data.role === "TPO") {
              navigate("/tpo/dashboard");
            } else {
              navigate("/student/dashboard");
            }
          }, 1500);
        } else {
          setError(`Current account status: ${res.data.approvalStatus}. Your account is awaiting institutional accreditation.`);
        }
      } else {
        setError(res.message || "Failed to retrieve account status.");
      }
    } catch (err) {
      setError(err.response?.data?.message || "Could not check account status.");
    } finally {
      setCheckingStatus(false);
    }
  };

  const handleSignOut = () => {
    logout();
    navigate("/login");
  };

  const roleName = user?.role === "RECRUITER" ? "Company Recruiter" : user?.role === "TPO" ? "Training & Placement Officer" : "User";

  return (
    <div
      className="min-vh-100 d-flex align-items-center justify-content-center p-3"
      style={{
        background: "linear-gradient(135deg, #0f2027 0%, #203a43 50%, #2c5364 100%)",
      }}
    >
      <div
        className="card shadow-lg border-0"
        style={{ maxWidth: "540px", width: "100%", borderRadius: "14px" }}
      >
        <div
          className="card-header text-white text-center py-4"
          style={{
            background: "linear-gradient(135deg, #1e3c72, #2a5298)",
            borderTopLeftRadius: "14px",
            borderTopRightRadius: "14px",
          }}
        >
          <div className="mb-2">
            <span
              className="badge bg-warning text-dark px-3 py-2 fw-bold text-uppercase"
              style={{ letterSpacing: "1px" }}
            >
              <i className="bi bi-shield-exclamation me-1"></i> Account Pending Accreditation
            </span>
          </div>
          <h3 className="fw-bold mb-1">Verification Required</h3>
          <p className="small mb-0 opacity-75">
            CampusBridge Institutional Identity &amp; Authorization Guard
          </p>
        </div>

        <div className="card-body p-4">
          {error && (
            <div className="alert alert-danger py-2 small d-flex align-items-center gap-2 mb-3">
              <i className="bi bi-exclamation-triangle-fill"></i>
              <div>{error}</div>
            </div>
          )}

          {success && (
            <div className="alert alert-success py-2 small d-flex align-items-center gap-2 mb-3">
              <i className="bi bi-check-circle-fill"></i>
              <div>{success}</div>
            </div>
          )}

          {/* User Summary Info */}
          <div className="bg-light p-3 rounded mb-3 border">
            <div className="d-flex justify-content-between align-items-center mb-1">
              <span className="text-muted small">Registered Role:</span>
              <span className="badge bg-secondary">{roleName}</span>
            </div>
            {user?.fullName && (
              <div className="d-flex justify-content-between align-items-center mb-1">
                <span className="text-muted small">Representative:</span>
                <span className="fw-semibold small">{user.fullName}</span>
              </div>
            )}
            <div className="d-flex justify-content-between align-items-center mb-1">
              <span className="text-muted small">Official Email:</span>
              <span className="fw-semibold small">{email || "Not specified"}</span>
            </div>
            {user?.companyName && (
              <div className="d-flex justify-content-between align-items-center mb-1">
                <span className="text-muted small">Associated Company:</span>
                <span className="fw-semibold small text-primary">{user.companyName}</span>
              </div>
            )}
            <div className="d-flex justify-content-between align-items-center pt-2 mt-2 border-top">
              <span className="text-muted small">Status:</span>
              <span className="badge bg-warning text-dark">
                {user?.approvalStatus || "PENDING_APPROVAL"}
              </span>
            </div>
          </div>

          <div className="alert alert-info py-2 small mb-4">
            <i className="bi bi-info-circle-fill me-2"></i>
            <strong>Why is my account pending?</strong> To prevent unauthorized corporate impersonation
            and protect institutional student data, official accounts require email verification
            or administrative placement accreditation before granting access to live placement drives.
          </div>

          {/* Verification Code Form */}
          <form onSubmit={handleVerifyCode} className="mb-4">
            <label className="form-label small fw-semibold">
              Enter 6-Digit Email Verification Code
            </label>
            <div className="input-group mb-3">
              <span className="input-group-text bg-light">
                <i className="bi bi-key-fill text-muted"></i>
              </span>
              <input
                type="text"
                className="form-control text-center fw-bold"
                placeholder="XXXXXX"
                maxLength={6}
                value={verificationCode}
                onChange={(e) => setVerificationCode(e.target.value.replace(/\D/g, ""))}
                style={{ letterSpacing: "0.4em", fontSize: "1.2rem" }}
              />
              <button
                className="btn btn-primary px-4 fw-semibold"
                type="submit"
                disabled={loading || verificationCode.length !== 6}
                style={{ backgroundColor: "#0f4c81", borderColor: "#0f4c81" }}
              >
                {loading ? (
                  <span className="spinner-border spinner-border-sm"></span>
                ) : (
                  "Verify & Activate"
                )}
              </button>
            </div>
            <div className="form-text small text-muted">
              Check your inbox for the 6-digit code sent during registration. (If email server is in mock/log mode, check your server console log).
            </div>
          </form>

          {/* Action Buttons */}
          <div className="d-grid gap-2">
            <button
              className="btn btn-outline-primary py-2 fw-semibold"
              onClick={handleCheckStatus}
              disabled={checkingStatus}
            >
              {checkingStatus ? (
                <span>
                  <span className="spinner-border spinner-border-sm me-2"></span>
                  Checking Institutional Status...
                </span>
              ) : (
                <>
                  <i className="bi bi-arrow-clockwise me-2"></i>
                  Check Approval Status
                </>
              )}
            </button>

            <button
              className="btn btn-outline-secondary py-2 small mt-1"
              onClick={handleSignOut}
            >
              <i className="bi bi-box-arrow-right me-2"></i>
              Sign Out / Switch Account
            </button>
          </div>

          <div className="text-center mt-3 pt-2 text-muted small">
            Need urgent assistance? Contact the Central Placement Cell at{" "}
            <span className="text-dark fw-semibold">placement@campusbridge.edu</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PendingApprovalPage;
