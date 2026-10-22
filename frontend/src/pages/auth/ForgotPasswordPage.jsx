import React, { useState } from "react";
import { Link } from "react-router-dom";
import { forgotPassword, resetPassword } from "../../services/authService";

const ForgotPasswordPage = () => {
  const [step, setStep] = useState(1); // 1=email, 2=code+newPassword, 3=done
  const [email, setEmail] = useState("");
  const [code, setCode] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleSendCode = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    setLoading(true);
    try {
      const res = await forgotPassword(email);
      if (res.success) {
        setSuccess("Verification code sent to your email. Please check your inbox.");
        setStep(2);
      } else {
        setError(res.message || "Failed to send reset code.");
      }
    } catch (err) {
      setError(err.response?.data?.message || "Failed to send reset code. Please check the email address.");
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");

    if (newPassword.length < 6) {
      setError("Password must be at least 6 characters long.");
      return;
    }
    if (newPassword !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setLoading(true);
    try {
      const res = await resetPassword(code, newPassword);
      if (res.success) {
        setSuccess("Password reset successful! You can now sign in with your new password.");
        setStep(3);
      } else {
        setError(res.message || "Failed to reset password.");
      }
    } catch (err) {
      setError(err.response?.data?.message || "Failed to reset password. The code may be invalid or expired.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card shadow-lg border-0" style={{ maxWidth: "460px", width: "100%", borderRadius: "12px" }}>
      <div className="card-header text-white text-center py-4" style={{ background: "linear-gradient(135deg, #0f2027, #203a43, #2c5364)", borderTopLeftRadius: "12px", borderTopRightRadius: "12px" }}>
        <i className="bi bi-shield-lock-fill fs-1 text-info mb-2 d-block"></i>
        <h3 className="fw-bold mb-1">Reset Password</h3>
        <p className="small mb-0 opacity-75">CampusBridge Account Recovery</p>
      </div>

      <div className="card-body p-4">
        {error && (
          <div className="alert alert-danger py-2 small d-flex align-items-center gap-2" role="alert">
            <i className="bi bi-exclamation-triangle-fill"></i>
            <div>{error}</div>
          </div>
        )}
        {success && (
          <div className="alert alert-success py-2 small d-flex align-items-center gap-2" role="alert">
            <i className="bi bi-check-circle-fill"></i>
            <div>{success}</div>
          </div>
        )}

        {/* Step 1: Enter email */}
        {step === 1 && (
          <form onSubmit={handleSendCode}>
            <p className="text-muted small mb-3">
              Enter your registered email address. We'll send you a verification code to reset your password.
            </p>
            <div className="mb-3">
              <label className="form-label small fw-semibold">Email Address</label>
              <div className="input-group">
                <span className="input-group-text bg-light"><i className="bi bi-envelope"></i></span>
                <input
                  type="email"
                  className="form-control"
                  placeholder="name@college.ac.in"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </div>
            </div>
            <button
              className="btn btn-primary w-100 py-2 fw-semibold"
              type="submit"
              disabled={loading}
              style={{ backgroundColor: "#0f4c81", borderColor: "#0f4c81" }}
            >
              {loading ? (
                <span><span className="spinner-border spinner-border-sm me-2"></span>Sending...</span>
              ) : (
                "Send Verification Code"
              )}
            </button>
          </form>
        )}

        {/* Step 2: Enter code + new password */}
        {step === 2 && (
          <form onSubmit={handleResetPassword}>
            <p className="text-muted small mb-3">
              Enter the 6-digit verification code sent to <strong>{email}</strong> and choose your new password.
            </p>
            <div className="mb-3">
              <label className="form-label small fw-semibold">Verification Code</label>
              <div className="input-group">
                <span className="input-group-text bg-light"><i className="bi bi-key"></i></span>
                <input
                  type="text"
                  className="form-control text-center fw-bold"
                  placeholder="Enter 6-digit code"
                  value={code}
                  onChange={(e) => setCode(e.target.value)}
                  maxLength={6}
                  required
                  style={{ letterSpacing: "0.3em", fontSize: "1.1rem" }}
                />
              </div>
            </div>
            <div className="mb-3">
              <label className="form-label small fw-semibold">New Password</label>
              <div className="input-group">
                <span className="input-group-text bg-light"><i className="bi bi-lock"></i></span>
                <input
                  type="password"
                  className="form-control"
                  placeholder="At least 6 characters"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  minLength={6}
                  required
                />
              </div>
            </div>
            <div className="mb-3">
              <label className="form-label small fw-semibold">Confirm New Password</label>
              <div className="input-group">
                <span className="input-group-text bg-light"><i className="bi bi-lock-fill"></i></span>
                <input
                  type="password"
                  className="form-control"
                  placeholder="Re-enter new password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  minLength={6}
                  required
                />
              </div>
            </div>
            <button
              className="btn btn-primary w-100 py-2 fw-semibold mb-2"
              type="submit"
              disabled={loading}
              style={{ backgroundColor: "#0f4c81", borderColor: "#0f4c81" }}
            >
              {loading ? (
                <span><span className="spinner-border spinner-border-sm me-2"></span>Resetting...</span>
              ) : (
                "Reset Password"
              )}
            </button>
            <button
              type="button"
              className="btn btn-outline-secondary w-100 btn-sm"
              onClick={() => { setStep(1); setError(""); setSuccess(""); }}
            >
              Resend Code
            </button>
          </form>
        )}

        {/* Step 3: Success */}
        {step === 3 && (
          <div className="text-center py-3">
            <i className="bi bi-check-circle-fill text-success fs-1 d-block mb-3"></i>
            <p className="fw-semibold">Your password has been reset successfully!</p>
            <Link to="/login" className="btn btn-primary w-100 py-2 fw-semibold" style={{ backgroundColor: "#0f4c81", borderColor: "#0f4c81" }}>
              Back to Sign In
            </Link>
          </div>
        )}

        <div className="text-center mt-3 pt-2 text-muted small">
          <Link to="/login" className="text-decoration-none fw-semibold">
            <i className="bi bi-arrow-left me-1"></i>Back to Login
          </Link>
        </div>
      </div>
    </div>
  );
};

export default ForgotPasswordPage;
