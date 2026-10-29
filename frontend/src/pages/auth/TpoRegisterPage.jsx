import React, { useState, useEffect, useContext } from "react";
import { useNavigate, Link } from "react-router-dom";
import { registerTpo, getColleges } from "../../services/authService";
import { AuthContext } from "../../context/AuthContext";

const TpoRegisterPage = () => {
  const navigate = useNavigate();
  const { login } = useContext(AuthContext);

  const [colleges, setColleges] = useState([]);
  const [form, setForm] = useState({
    fullName: "",
    email: "",
    password: "",
    confirmPassword: "",
    phoneNumber: "",
    designation: "Training and Placement Officer",
    collegeId: "",
    collegeName: "",
    collegeCode: "",
  });

  const [useExistingCollege, setUseExistingCollege] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    getColleges()
      .then((res) => {
        if (res.success && res.data) setColleges(res.data);
      })
      .catch((err) => {
        console.error("Error loading colleges:", err);
      });
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const validateForm = () => {
    if (!form.fullName.trim()) return "Placement officer full name is required.";
    if (!form.email.trim() || !form.email.includes("@")) return "A valid official college email ID is required.";
    if (form.password.length < 6) return "Password must be at least 6 characters long.";
    if (form.password !== form.confirmPassword) return "Passwords do not match.";
    if (useExistingCollege && !form.collegeId) return "Please select your associated college.";
    if (!useExistingCollege && !form.collegeName.trim()) return "College name is required.";
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
        collegeId: useExistingCollege && form.collegeId ? parseInt(form.collegeId) : null,
        collegeName: !useExistingCollege ? form.collegeName.trim() : null,
        collegeCode: !useExistingCollege && form.collegeCode ? form.collegeCode.trim().toUpperCase() : null,
      };

      const res = await registerTpo(payload);
      if (res.success && res.data) {
        setSuccess(
          "TPO account registered successfully! Redirecting to TPO dashboard..."
        );
        if (login) {
          login(res.data, res.data.token);
        }
        setTimeout(() => {
          navigate("/tpo/dashboard");
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
        background: "linear-gradient(135deg, #0f2027 0%, #203a43 50%, #2c5364 100%)",
      }}
    >
      <div
        className="card shadow-lg border-0"
        style={{ maxWidth: "560px", width: "100%", borderRadius: "14px" }}
      >
        <div
          className="card-header text-white text-center py-4"
          style={{
            background: "linear-gradient(135deg, #0f2027, #203a43, #2c5364)",
            borderTopLeftRadius: "14px",
            borderTopRightRadius: "14px",
          }}
        >
          <i className="bi bi-shield-lock-fill fs-1 text-info mb-2 d-block"></i>
          <h3 className="fw-bold mb-1">TPO Registration</h3>
          <p className="small mb-0 opacity-75">
            CampusBridge — Training &amp; Placement Officer / Coordinator Portal
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
              {/* Full Name */}
              <div className="col-12">
                <label className="form-label small fw-semibold">
                  Full Name of Placement Officer / Coordinator *
                </label>
                <div className="input-group">
                  <span className="input-group-text bg-light">
                    <i className="bi bi-person"></i>
                  </span>
                  <input
                    type="text"
                    name="fullName"
                    className="form-control"
                    placeholder="e.g. Dr. Rajesh Kumar"
                    value={form.fullName}
                    onChange={handleChange}
                    required
                  />
                </div>
              </div>

              {/* Official College Email */}
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Official College Email ID *</label>
                <div className="input-group">
                  <span className="input-group-text bg-light">
                    <i className="bi bi-envelope"></i>
                  </span>
                  <input
                    type="email"
                    name="email"
                    className="form-control"
                    placeholder="tpo@college.ac.in"
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

              {/* Designation */}
              <div className="col-12">
                <label className="form-label small fw-semibold">
                  Official Designation *
                </label>
                <div className="input-group">
                  <span className="input-group-text bg-light">
                    <i className="bi bi-award"></i>
                  </span>
                  <input
                    type="text"
                    name="designation"
                    className="form-control"
                    placeholder="e.g. Training and Placement Officer, Placement Director, Placement Coordinator"
                    value={form.designation}
                    onChange={handleChange}
                    required
                  />
                </div>
              </div>

              {/* College Selection Mode Toggle */}
              <div className="col-12">
                <div className="p-2 border rounded bg-light d-flex justify-content-between align-items-center">
                  <span className="small fw-semibold text-dark">
                    <i className="bi bi-building me-1"></i> College Affiliation:
                  </span>
                  <div className="btn-group btn-group-sm" role="group">
                    <button
                      type="button"
                      className={`btn ${useExistingCollege ? "btn-primary active" : "btn-outline-secondary"}`}
                      onClick={() => setUseExistingCollege(true)}
                    >
                      Registered College
                    </button>
                    <button
                      type="button"
                      className={`btn ${!useExistingCollege ? "btn-primary active" : "btn-outline-secondary"}`}
                      onClick={() => setUseExistingCollege(false)}
                    >
                      New College
                    </button>
                  </div>
                </div>
              </div>

              {/* College Selector / Input */}
              {useExistingCollege ? (
                <div className="col-12">
                  <label className="form-label small fw-semibold">Associated Institution *</label>
                  <select
                    name="collegeId"
                    className="form-select"
                    value={form.collegeId}
                    onChange={handleChange}
                    required={useExistingCollege}
                  >
                    <option value="">-- Select your affiliated college --</option>
                    {colleges.map((c) => (
                      <option key={c.id} value={c.id}>
                        {c.name} ({c.code || "COL"}) — {c.city}, {c.state}
                      </option>
                    ))}
                  </select>
                </div>
              ) : (
                <>
                  <div className="col-md-8">
                    <label className="form-label small fw-semibold">Full College Name *</label>
                    <input
                      type="text"
                      name="collegeName"
                      className="form-control"
                      placeholder="e.g. National Institute of Technology"
                      value={form.collegeName}
                      onChange={handleChange}
                      required={!useExistingCollege}
                    />
                  </div>
                  <div className="col-md-4">
                    <label className="form-label small fw-semibold">College Code</label>
                    <input
                      type="text"
                      name="collegeCode"
                      className="form-control"
                      placeholder="e.g. NIT-01"
                      value={form.collegeCode}
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
              className="btn btn-primary w-100 py-2 fw-bold mt-4"
              type="submit"
              disabled={loading}
              style={{ backgroundColor: "#0f4c81", borderColor: "#0f4c81" }}
            >
              {loading ? (
                <span>
                  <span className="spinner-border spinner-border-sm me-2"></span>
                  Registering Officer Account...
                </span>
              ) : (
                <>
                  <i className="bi bi-shield-lock-fill me-2"></i>
                  Register as TPO Officer
                </>
              )}
            </button>
          </form>

          {/* Navigation to Sign In & Other roles */}
          <div className="text-center mt-3 pt-2 text-muted small border-top">
            Already registered?{" "}
            <Link
              to="/login?role=tpo"
              className="text-decoration-none fw-bold"
              style={{ color: "#0f4c81" }}
            >
              Sign In as TPO Officer &rarr;
            </Link>
            <div className="mt-2 text-secondary">
              <Link to="/register" className="text-decoration-none mx-1 text-muted">
                Student Registration
              </Link>
              <span>|</span>
              <Link to="/register-recruiter" className="text-decoration-none mx-1 text-muted">
                Recruiter Registration
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TpoRegisterPage;
