import React, { useState, useContext } from "react";
import { useNavigate, Link } from "react-router-dom";
import { loginUser } from "../../services/authService";
import { AuthContext } from "../../context/AuthContext";

const LoginPage = () => {
  const navigate = useNavigate();
  const { login } = useContext(AuthContext);
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
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
        handleAuthSuccess(res.data);
      } else {
        setError(res.message || "Login failed");
      }
    } catch (err) {
      setError(err.response?.data?.message || "Invalid credentials. Please verify your email and password.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card shadow-lg border-0" style={{ maxWidth: "460px", width: "100%", borderRadius: "12px" }}>
      <div className="card-header text-white text-center py-4" style={{ background: "linear-gradient(135deg, #0f2027, #203a43, #2c5364)", borderTopLeftRadius: "12px", borderTopRightRadius: "12px" }}>
        <i className="bi bi-mortarboard-fill fs-1 text-info mb-2 d-block"></i>
        <h3 className="fw-bold mb-1">CampusBridge</h3>
        <p className="small mb-0 opacity-75">Unified Virtual Placement Drive Ecosystem</p>
      </div>

      <div className="card-body p-4">
        {error && (
          <div className="alert alert-danger py-2 small d-flex align-items-center gap-2" role="alert">
            <i className="bi bi-exclamation-triangle-fill"></i>
            <div>{error}</div>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label small fw-semibold">Email Address</label>
            <div className="input-group">
              <span className="input-group-text bg-light"><i className="bi bi-envelope"></i></span>
              <input
                type="email"
                name="email"
                className="form-control"
                placeholder="name@college.ac.in"
                value={form.email}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <div className="mb-3">
            <label className="form-label small fw-semibold">Password</label>
            <div className="input-group">
              <span className="input-group-text bg-light"><i className="bi bi-lock"></i></span>
              <input
                type="password"
                name="password"
                className="form-control"
                placeholder="Enter password"
                value={form.password}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <div className="text-end mb-3">
            <Link to="/forgot-password" className="text-decoration-none small fw-semibold" style={{ color: "#0f4c81" }}>
              Forgot Password?
            </Link>
          </div>

          <button
            className="btn btn-primary w-100 py-2 fw-semibold mb-3"
            type="submit"
            disabled={loading}
            style={{ backgroundColor: "#0f4c81", borderColor: "#0f4c81" }}
          >
            {loading ? (
              <span><span className="spinner-border spinner-border-sm me-2"></span>Signing in...</span>
            ) : (
              "Sign In"
            )}
          </button>
        </form>

        <div className="text-center mt-3 pt-2 text-muted small">
          New Student? <Link to="/register" className="text-decoration-none fw-semibold">Register here</Link>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
