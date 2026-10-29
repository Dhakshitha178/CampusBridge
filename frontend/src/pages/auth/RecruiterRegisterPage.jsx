import React, { useState, useEffect, useContext } from "react";
import { useNavigate, Link } from "react-router-dom";
import { registerRecruiter, getCompanies } from "../../services/authService";
import { AuthContext } from "../../context/AuthContext";

const RecruiterRegisterPage = () => {
  const navigate = useNavigate();
  const { login } = useContext(AuthContext);

  const [companies, setCompanies] = useState([]);
  const [form, setForm] = useState({
    fullName: "",
    email: "",
    password: "",
    confirmPassword: "",
    phoneNumber: "",
    designation: "HR Talent Acquisition Manager",
    companyId: "",
    companyName: "",
    companyIndustry: "Technology & Software",
    companyWebsite: "",
  });

  const [useExistingCompany, setUseExistingCompany] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    getCompanies()
      .then((res) => {
        if (res.success && res.data) setCompanies(res.data);
      })
      .catch((err) => {
        console.error("Error loading companies:", err);
      });
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const validateForm = () => {
    if (!form.fullName.trim()) return "Representative full name is required.";
    if (!form.email.trim() || !form.email.includes("@")) return "A valid official company email ID is required.";
    if (form.password.length < 6) return "Password must be at least 6 characters long.";
    if (form.password !== form.confirmPassword) return "Passwords do not match.";
    if (useExistingCompany && !form.companyId) return "Please select your associated company.";
    if (!useExistingCompany && !form.companyName.trim()) return "Company name is required.";
    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");

    const validationError = validateForm();
    if (validationError) {
      setError(validationError);
      return;
    }

    setLoading(true);
    try {
      const payload = {
        fullName: form.fullName.trim(),
        email: form.email.trim().toLowerCase(),
        password: form.password,
        phoneNumber: form.phoneNumber.trim(),
        designation: form.designation.trim(),
        companyId: useExistingCompany && form.companyId ? parseInt(form.companyId) : null,
        companyName: !useExistingCompany ? form.companyName.trim() : null,
        companyIndustry: !useExistingCompany ? form.companyIndustry.trim() : null,
        companyWebsite: !useExistingCompany ? form.companyWebsite.trim() : null,
      };

      const res = await registerRecruiter(payload);
      if (res.success && res.data) {
        setSuccess(
          "Recruiter registration successful! Redirecting to recruiter dashboard..."
        );
        if (login) {
          login(res.data, res.data.token);
        }
        setTimeout(() => {
          navigate("/recruiter/dashboard");
        }, 1500);
      } else {
        setError(res.message || "Registration failed.");
      }
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Registration failed. Please check if this email is already registered."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      className="min-vh-100 d-flex align-items-center justify-content-center p-3"
      style={{
        background: "linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%)",
      }}
    >
      <div
        className="card shadow-lg border-0"
        style={{ maxWidth: "580px", width: "100%", borderRadius: "14px" }}
      >
        <div
          className="card-header text-white text-center py-4"
          style={{
            background: "linear-gradient(135deg, #1a1a2e, #16213e, #0f3460)",
            borderTopLeftRadius: "14px",
            borderTopRightRadius: "14px",
          }}
        >
          <i className="bi bi-building-fill fs-1 text-warning mb-2 d-block"></i>
          <h3 className="fw-bold mb-1">Company Recruiter Registration</h3>
          <p className="small mb-0 opacity-75">
            CampusBridge — Corporate Talent Acquisition &amp; Drive Management
          </p>
        </div>

        <div className="card-body p-4">
          {error && (
            <div className="alert alert-danger py-2 small d-flex align-items-center gap-2 mb-3" role="alert">
              <i className="bi bi-exclamation-triangle-fill flex-shrink-0"></i>
              <div>{error}</div>
            </div>
          )}

          {success && (
            <div className="alert alert-success py-2 small d-flex align-items-center gap-2 mb-3" role="alert">
              <i className="bi bi-check-circle-fill flex-shrink-0"></i>
              <div>{success}</div>
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="row g-3">
              {/* Representative Full Name */}
              <div className="col-12">
                <label className="form-label small fw-semibold">
                  Full Name of Authorized Representative *
                </label>
                <div className="input-group">
                  <span className="input-group-text bg-light">
                    <i className="bi bi-person"></i>
                  </span>
                  <input
                    type="text"
                    name="fullName"
                    className="form-control"
                    placeholder="e.g. Priya Sharma"
                    value={form.fullName}
                    onChange={handleChange}
                    required
                  />
                </div>
              </div>

              {/* Official Company Email */}
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Official Company Email ID *</label>
                <div className="input-group">
                  <span className="input-group-text bg-light">
                    <i className="bi bi-envelope"></i>
                  </span>
                  <input
                    type="email"
                    name="email"
                    className="form-control"
                    placeholder="recruiter@company.com"
                    value={form.email}
                    onChange={handleChange}
                    required
                  />
                </div>
              </div>

              {/* Contact Number */}
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Contact Number *</label>
                <div className="input-group">
                  <span className="input-group-text bg-light">
                    <i className="bi bi-telephone"></i>
                  </span>
                  <input
                    type="tel"
                    name="phoneNumber"
                    className="form-control"
                    placeholder="+91 9876543210"
                    value={form.phoneNumber}
                    onChange={handleChange}
                    required
                  />
                </div>
              </div>

              {/* Designation / Authority */}
              <div className="col-12">
                <label className="form-label small fw-semibold">
                  Designation / Role in Organization *
                </label>
                <div className="input-group">
                  <span className="input-group-text bg-light">
                    <i className="bi bi-briefcase"></i>
                  </span>
                  <input
                    type="text"
                    name="designation"
                    className="form-control"
                    placeholder="e.g. HR Manager, Talent Acquisition Lead, Recruitment Officer"
                    value={form.designation}
                    onChange={handleChange}
                    required
                  />
                </div>
              </div>

              {/* Company Selection Mode Toggle */}
              <div className="col-12">
                <div className="p-2 border rounded bg-light d-flex justify-content-between align-items-center">
                  <span className="small fw-semibold text-dark">
                    <i className="bi bi-building me-1"></i> Company Association:
                  </span>
                  <div className="btn-group btn-group-sm" role="group">
                    <button
                      type="button"
                      className={`btn ${useExistingCompany ? "btn-dark active" : "btn-outline-secondary"}`}
                      onClick={() => setUseExistingCompany(true)}
                    >
                      Existing Corporate Partner
                    </button>
                    <button
                      type="button"
                      className={`btn ${!useExistingCompany ? "btn-dark active" : "btn-outline-secondary"}`}
                      onClick={() => setUseExistingCompany(false)}
                    >
                      New Company
                    </button>
                  </div>
                </div>
              </div>

              {/* Company Input */}
              {useExistingCompany ? (
                <div className="col-12">
                  <label className="form-label small fw-semibold">Select Company Partner *</label>
                  <select
                    name="companyId"
                    className="form-select"
                    value={form.companyId}
                    onChange={handleChange}
                    required={useExistingCompany}
                  >
                    <option value="">-- Choose verified corporate partner --</option>
                    {companies.map((c) => (
                      <option key={c.id} value={c.id}>
                        {c.name} ({c.industry || "Technology"}) {c.verified ? "✓ Verified" : "⏳ Pending"}
                      </option>
                    ))}
                  </select>
                </div>
              ) : (
                <>
                  <div className="col-12">
                    <label className="form-label small fw-semibold">Official Company Name *</label>
                    <input
                      type="text"
                      name="companyName"
                      className="form-control"
                      placeholder="e.g. Acme Innovations Technologies"
                      value={form.companyName}
                      onChange={handleChange}
                      required={!useExistingCompany}
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small fw-semibold">Industry Sector</label>
                    <input
                      type="text"
                      name="companyIndustry"
                      className="form-control"
                      placeholder="e.g. Cloud Infrastructure / SaaS"
                      value={form.companyIndustry}
                      onChange={handleChange}
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small fw-semibold">Corporate Website</label>
                    <input
                      type="url"
                      name="companyWebsite"
                      className="form-control"
                      placeholder="https://acme.com"
                      value={form.companyWebsite}
                      onChange={handleChange}
                    />
                  </div>
                </>
              )}

              {/* Password */}
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Password *</label>
                <div className="input-group">
                  <span className="input-group-text bg-light">
                    <i className="bi bi-lock"></i>
                  </span>
                  <input
                    type={showPassword ? "text" : "password"}
                    name="password"
                    className="form-control"
                    placeholder="Min. 6 chars"
                    value={form.password}
                    onChange={handleChange}
                    required
                    minLength={6}
                  />
                  <button
                    type="button"
                    className="btn btn-outline-secondary"
                    onClick={() => setShowPassword(!showPassword)}
                    tabIndex="-1"
                  >
                    <i className={`bi ${showPassword ? "bi-eye-slash-fill" : "bi-eye-fill"}`}></i>
                  </button>
                </div>
              </div>

              {/* Confirm Password */}
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Confirm Password *</label>
                <div className="input-group">
                  <span className="input-group-text bg-light">
                    <i className="bi bi-lock-fill"></i>
                  </span>
                  <input
                    type={showConfirmPassword ? "text" : "password"}
                    name="confirmPassword"
                    className="form-control"
                    placeholder="Repeat password"
                    value={form.confirmPassword}
                    onChange={handleChange}
                    required
                  />
                  <button
                    type="button"
                    className="btn btn-outline-secondary"
                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                    tabIndex="-1"
                  >
                    <i className={`bi ${showConfirmPassword ? "bi-eye-slash-fill" : "bi-eye-fill"}`}></i>
                  </button>
                </div>
              </div>
            </div>

            <button
              className="btn btn-warning w-100 py-2 fw-bold mt-4"
              type="submit"
              disabled={loading}
              style={{ backgroundColor: "#f59e0b", borderColor: "#d97706", color: "#111827" }}
            >
              {loading ? (
                <span>
                  <span className="spinner-border spinner-border-sm me-2"></span>
                  Registering Account...
                </span>
              ) : (
                <>
                  <i className="bi bi-building-fill-add me-2"></i>
                  Register Recruiter Account
                </>
              )}
            </button>
          </form>

          {/* Navigation to Sign In & Other roles */}
          <div className="text-center mt-3 pt-2 text-muted small border-top">
            Already registered?{" "}
            <Link
              to="/login?role=recruiter"
              className="text-decoration-none fw-bold"
              style={{ color: "#d97706" }}
            >
              Sign In as Recruiter &rarr;
            </Link>
            <div className="mt-2 text-secondary">
              <Link to="/register" className="text-decoration-none mx-1 text-muted">
                Student Registration
              </Link>
              <span>|</span>
              <Link to="/register-tpo" className="text-decoration-none mx-1 text-muted">
                TPO Registration
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RecruiterRegisterPage;
