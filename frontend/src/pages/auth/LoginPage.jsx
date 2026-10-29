import React, { useState, useContext, useEffect } from "react";
import { useNavigate, Link, useSearchParams } from "react-router-dom";
import { loginUser } from "../../services/authService";
import { AuthContext } from "../../context/AuthContext";

const LoginPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { login } = useContext(AuthContext);

  // Default active role: check URL param or default to STUDENT
  const roleParam = searchParams.get("role")?.toUpperCase();
  const initialRole = ["STUDENT", "TPO", "RECRUITER"].includes(roleParam) ? roleParam : "STUDENT";

  const [activeRole, setActiveRole] = useState(initialRole);
  const [form, setForm] = useState({ email: "", password: "" });
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (roleParam && ["STUDENT", "TPO", "RECRUITER"].includes(roleParam)) {
      setActiveRole(roleParam);
    }
  }, [roleParam]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleRoleChange = (role) => {
    setActiveRole(role);
    setError("");
  };

  const handleAuthSuccess = (authData) => {
    login(authData, authData.token);

    if (authData.role === "STUDENT") {
      navigate("/student/dashboard");
    } else if (authData.role === "TPO") {
      navigate("/tpo/dashboard");
    } else if (authData.role === "RECRUITER") {
      navigate("/recruiter/dashboard");
    } else {
      navigate("/");
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const res = await loginUser(form);
      if (res.success && res.data) {
        if (res.data.role !== activeRole) {
          console.warn(`User authenticated as ${res.data.role} while tab was ${activeRole}`);
        }
        handleAuthSuccess(res.data);
      } else {
        setError(res.message || "Login failed");
      }
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Invalid credentials. Please verify your email and password."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleQuickLogin = async (email, password, role) => {
    setError("");
    setLoading(true);
    if (role) setActiveRole(role);
    setForm({ email, password });
    try {
      const res = await loginUser({ email, password });
      if (res.success && res.data) {
        handleAuthSuccess(res.data);
      } else {
        setError(res.message || "Quick demo login failed.");
      }
    } catch (err) {
      setError(err.response?.data?.message || "Quick demo login failed.");
    } finally {
      setLoading(false);
    }
  };

  // Role metadata configurations
  const roleConfig = {
    STUDENT: {
      title: "Student Portal",
      subtitle: "Unified Virtual Placement Drive Ecosystem",
      emailLabel: "Student Email Address",
      placeholder: "student@cb.com",
      icon: "bi-mortarboard-fill",
      btnClass: "btn-primary",
      btnStyle: { backgroundColor: "#0f4c81", borderColor: "#0f4c81" },
      submitText: "Sign In as Student",
      registerText: "New Student?",
      registerAction: "Register here",
      registerLink: "/register",
    },
    TPO: {
      title: "TPO & Placement Cell",
      subtitle: "Institutional Placement & Accreditation Portal",
      emailLabel: "Official College Email ID",
      placeholder: "tpo@college.edu",
      icon: "bi-shield-lock-fill",
      btnClass: "btn-info text-white",
      btnStyle: { backgroundColor: "#0288d1", borderColor: "#0288d1" },
      submitText: "Sign In as Placement Officer",
      registerText: "Placement Coordinator?",
      registerAction: "Register as TPO",
      registerLink: "/register-tpo",
    },
    RECRUITER: {
      title: "Company Recruiter Portal",
      subtitle: "Corporate Talent Acquisition & Drive Management",
      emailLabel: "Official Company Email ID",
      placeholder: "recruiter@company.com",
      icon: "bi-building-fill",
      btnClass: "btn-warning text-dark",
      btnStyle: { backgroundColor: "#f59e0b", borderColor: "#d97706" },
      submitText: "Sign In as Recruiter",
      registerText: "Corporate Partner?",
      registerAction: "Register as Recruiter",
      registerLink: "/register-recruiter",
    },
  };

  const current = roleConfig[activeRole];

  return (
    <div className="w-100 my-auto py-2 d-flex justify-content-center">
      <div
        className="card shadow-lg border-0"
        style={{ maxWidth: "490px", width: "100%", borderRadius: "14px" }}
      >
        {/* Header */}
        <div
          className="card-header text-white text-center py-4 position-relative"
          style={{
            background: "linear-gradient(135deg, #0f2027, #203a43, #2c5364)",
            borderTopLeftRadius: "14px",
            borderTopRightRadius: "14px",
          }}
        >
          <i className={`bi ${current.icon} fs-1 text-info mb-2 d-block`}></i>
          <h3 className="fw-bold mb-1">CampusBridge</h3>
          <p className="small mb-0 opacity-75">{current.subtitle}</p>
        </div>

        {/* 3 Role Selection Tabs */}
        <div className="bg-light border-bottom px-3 pt-3">
          <ul className="nav nav-pills nav-fill gap-1" role="tablist">
            <li className="nav-item">
              <button
                type="button"
                className={`nav-link py-2 px-2 small fw-semibold d-flex flex-column align-items-center ${
                  activeRole === "STUDENT" ? "active bg-primary text-white" : "text-muted"
                }`}
                onClick={() => handleRoleChange("STUDENT")}
              >
                <i className="bi bi-mortarboard-fill mb-1"></i>
                <span>Student</span>
              </button>
            </li>
            <li className="nav-item">
              <button
                type="button"
                className={`nav-link py-2 px-2 small fw-semibold d-flex flex-column align-items-center ${
                  activeRole === "TPO" ? "active text-white" : "text-muted"
                }`}
                style={activeRole === "TPO" ? { backgroundColor: "#0288d1" } : {}}
                onClick={() => handleRoleChange("TPO")}
              >
                <i className="bi bi-shield-lock-fill mb-1"></i>
                <span>TPO / Cell</span>
              </button>
            </li>
            <li className="nav-item">
              <button
                type="button"
                className={`nav-link py-2 px-2 small fw-semibold d-flex flex-column align-items-center ${
                  activeRole === "RECRUITER" ? "active bg-warning text-dark fw-bold" : "text-muted"
                }`}
                onClick={() => handleRoleChange("RECRUITER")}
              >
                <i className="bi bi-building-fill mb-1"></i>
                <span>Recruiter</span>
              </button>
            </li>
          </ul>
        </div>

        {/* Card Body */}
        <div className="card-body p-4">
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h5 className="fw-bold mb-0 text-dark">{current.title}</h5>
            <Link
              to={current.registerLink}
              className="badge text-decoration-none px-2 py-1 fw-semibold"
              style={{
                backgroundColor:
                  activeRole === "RECRUITER"
                    ? "#fffbeb"
                    : activeRole === "TPO"
                    ? "#e0f2fe"
                    : "#e0e7ff",
                color:
                  activeRole === "RECRUITER"
                    ? "#b45309"
                    : activeRole === "TPO"
                    ? "#0369a1"
                    : "#3730a3",
                border: "1px solid currentColor",
              }}
            >
              <i className="bi bi-person-plus-fill me-1"></i>
              Register
            </Link>
          </div>

          {error && (
            <div
              className="alert alert-danger py-2 small d-flex align-items-center gap-2"
              role="alert"
            >
              <i className="bi bi-exclamation-triangle-fill flex-shrink-0"></i>
              <div>{error}</div>
            </div>
          )}

          <form onSubmit={handleSubmit}>
            {/* Email Field */}
            <div className="mb-3">
              <label className="form-label small fw-semibold">{current.emailLabel} *</label>
              <div className="input-group">
                <span className="input-group-text bg-light">
                  <i className="bi bi-envelope"></i>
                </span>
                <input
                  type="email"
                  name="email"
                  className="form-control"
                  placeholder={current.placeholder}
                  value={form.email}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            {/* Password Field with Show/Hide Toggle */}
            <div className="mb-3">
              <label className="form-label small fw-semibold">Password *</label>
              <div className="input-group">
                <span className="input-group-text bg-light">
                  <i className="bi bi-lock"></i>
                </span>
                <input
                  type={showPassword ? "text" : "password"}
                  name="password"
                  className="form-control"
                  placeholder="Enter your account password"
                  value={form.password}
                  onChange={handleChange}
                  required
                />
                <button
                  type="button"
                  className="btn btn-outline-secondary"
                  onClick={() => setShowPassword(!showPassword)}
                  title={showPassword ? "Hide password" : "Show password"}
                  tabIndex="-1"
                >
                  <i className={`bi ${showPassword ? "bi-eye-slash-fill" : "bi-eye-fill"}`}></i>
                </button>
              </div>
            </div>

            {/* Forgot Password */}
            <div className="text-end mb-3">
              <Link
                to="/forgot-password"
                className="text-decoration-none small fw-semibold"
                style={{ color: "#0f4c81" }}
              >
                Forgot Password?
              </Link>
            </div>

            {/* Sign In Button */}
            <button
              className={`btn ${current.btnClass} w-100 py-2 fw-semibold mb-3`}
              type="submit"
              disabled={loading}
              style={current.btnStyle}
            >
              {loading ? (
                <span>
                  <span className="spinner-border spinner-border-sm me-2"></span>
                  Authenticating...
                </span>
              ) : (
                <>
                  <i className="bi bi-box-arrow-in-right me-2"></i>
                  {current.submitText}
                </>
              )}
            </button>
          </form>

          {/* Registration Link Box */}
          <div className="p-3 rounded bg-light border text-center mb-3">
            <span className="text-muted small me-2">{current.registerText}</span>
            <Link
              to={current.registerLink}
              className="text-decoration-none fw-bold small text-primary"
            >
              {current.registerAction} &rarr;
            </Link>
          </div>

          {/* Evaluator Demo Shortcuts */}
          <div className="pt-2 border-top">
            <div
              className="small text-muted fw-bold text-uppercase mb-2 text-center"
              style={{ fontSize: "0.72rem", letterSpacing: "0.5px" }}
            >
              Demo Quick Logins (Evaluator Shortcuts)
            </div>
            <div className="d-grid gap-2">
              <button
                type="button"
                className={`btn btn-sm text-start d-flex justify-content-between align-items-center ${
                  activeRole === "STUDENT"
                    ? "btn-success"
                    : "btn-outline-success"
                }`}
                onClick={() => handleQuickLogin("student@cb.com", "student28", "STUDENT")}
                disabled={loading}
              >
                <span>
                  <i className="bi bi-mortarboard-fill me-1"></i> Demo Student
                </span>
                <span className={`badge ${activeRole === "STUDENT" ? "bg-light text-success" : "bg-success"}`}>
                  student@cb.com
                </span>
              </button>
              <button
                type="button"
                className={`btn btn-sm text-start d-flex justify-content-between align-items-center ${
                  activeRole === "TPO"
                    ? "btn-primary"
                    : "btn-outline-primary"
                }`}
                onClick={() => handleQuickLogin("tpo@cb.com", "tpo28", "TPO")}
                disabled={loading}
              >
                <span>
                  <i className="bi bi-shield-lock-fill me-1"></i> Demo TPO Officer
                </span>
                <span className={`badge ${activeRole === "TPO" ? "bg-light text-primary" : "bg-primary"}`}>
                  tpo@cb.com
                </span>
              </button>
              <button
                type="button"
                className={`btn btn-sm text-start d-flex justify-content-between align-items-center ${
                  activeRole === "RECRUITER"
                    ? "btn-warning text-dark fw-semibold"
                    : "btn-outline-warning text-dark"
                }`}
                onClick={() => handleQuickLogin("recruiter@cb.com", "rec28", "RECRUITER")}
                disabled={loading}
              >
                <span>
                  <i className="bi bi-building me-1"></i> Demo Recruiter
                </span>
                <span className={`badge ${activeRole === "RECRUITER" ? "bg-dark text-warning" : "bg-warning text-dark"}`}>
                  recruiter@cb.com
                </span>
              </button>
            </div>

            {/* Additional test accounts */}
            <div className="text-center text-muted my-2" style={{ fontSize: "0.68rem" }}>
              ── legacy test accounts ──
            </div>
            <div className="d-grid gap-1">
              <button
                type="button"
                className="btn btn-outline-secondary btn-sm py-1 text-start d-flex justify-content-between align-items-center"
                style={{ fontSize: "0.78rem" }}
                onClick={() => handleQuickLogin("arun.prakash@college.ac.in", "student123", "STUDENT")}
                disabled={loading}
              >
                <span><i className="bi bi-person me-1"></i> Student: Arun Prakash</span>
                <span className="badge bg-secondary">Login as Student</span>
              </button>
              <button
                type="button"
                className="btn btn-outline-secondary btn-sm py-1 text-start d-flex justify-content-between align-items-center"
                style={{ fontSize: "0.78rem" }}
                onClick={() => handleQuickLogin("rangarajan@college.ac.in", "tpo123", "TPO")}
                disabled={loading}
              >
                <span><i className="bi bi-shield me-1"></i> TPO: Dr. K. Rangarajan</span>
                <span className="badge bg-secondary">Login as TPO</span>
              </button>
              <button
                type="button"
                className="btn btn-outline-secondary btn-sm py-1 text-start d-flex justify-content-between align-items-center"
                style={{ fontSize: "0.78rem" }}
                onClick={() => handleQuickLogin("recruiter@zoho.com", "recruiter123", "RECRUITER")}
                disabled={loading}
              >
                <span><i className="bi bi-briefcase me-1"></i> Recruiter: Zoho Corp</span>
                <span className="badge bg-secondary">Login as Recruiter</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
