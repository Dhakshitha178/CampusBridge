import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { registerTpo, getColleges } from "../../services/authService";

const TpoRegisterPage = () => {
  const navigate = useNavigate();
  const [colleges, setColleges] = useState([]);
  const [form, setForm] = useState({
    fullName: "",
    email: "",
    password: "",
    confirmPassword: "",
    phoneNumber: "",
    collegeId: "",
  });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    getColleges()
      .then((res) => { if (res.success && res.data) setColleges(res.data); })
      .catch(() => {});
  }, []);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(""); setSuccess("");
    if (form.password !== form.confirmPassword) { setError("Passwords do not match."); return; }
    setLoading(true);
    try {
      const payload = {
        fullName: form.fullName, email: form.email, password: form.password,
        phoneNumber: form.phoneNumber,
        collegeId: form.collegeId ? parseInt(form.collegeId) : null,
      };
      const res = await registerTpo(payload);
      if (res.success) {
        setSuccess("TPO account created successfully! Redirecting to login...");
        setTimeout(() => navigate("/login"), 2500);
      } else { setError(res.message || "Registration failed."); }
    } catch (err) {
      setError(err.response?.data?.message || "Registration failed. Please try again.");
    } finally { setLoading(false); }
  };

  return (
    <div className="min-vh-100 d-flex align-items-center justify-content-center"
      style={{ background: "linear-gradient(135deg, #0f2027 0%, #203a43 50%, #2c5364 100%)" }}>
      <div className="card shadow-lg border-0" style={{ maxWidth: "500px", width: "100%", borderRadius: "12px", margin: "1rem" }}>
        <div className="card-header text-white text-center py-4"
          style={{ background: "linear-gradient(135deg, #0f2027, #203a43, #2c5364)", borderTopLeftRadius: "12px", borderTopRightRadius: "12px" }}>
          <i className="bi bi-shield-lock-fill fs-1 text-info mb-2 d-block"></i>
          <h3 className="fw-bold mb-1">TPO Registration</h3>
          <p className="small mb-0 opacity-75">CampusBridge — Training &amp; Placement Officer Portal</p>
        </div>

        <div className="card-body p-4">
          {error && (<div className="alert alert-danger py-2 small" role="alert"><i className="bi bi-exclamation-triangle-fill me-2"></i>{error}</div>)}
          {success && (<div className="alert alert-success py-2 small" role="alert"><i className="bi bi-check-circle-fill me-2"></i>{success}</div>)}

          <form onSubmit={handleSubmit}>
            <div className="row g-3">
              <div className="col-12">
                <label className="form-label small fw-semibold">Full Name *</label>
                <input type="text" name="fullName" className="form-control" placeholder="Dr. Your Name"
                  value={form.fullName} onChange={handleChange} required />
              </div>
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Email Address *</label>
                <input type="email" name="email" className="form-control" placeholder="tpo@college.edu"
                  value={form.email} onChange={handleChange} required />
              </div>
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Phone Number</label>
                <input type="tel" name="phoneNumber" className="form-control" placeholder="+91 XXXXXXXXXX"
                  value={form.phoneNumber} onChange={handleChange} />
              </div>
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Password *</label>
                <input type="password" name="password" className="form-control" placeholder="Min. 6 characters"
                  value={form.password} onChange={handleChange} required minLength={6} />
              </div>
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Confirm Password *</label>
                <input type="password" name="confirmPassword" className="form-control" placeholder="Repeat password"
                  value={form.confirmPassword} onChange={handleChange} required />
              </div>
              <div className="col-12">
                <label className="form-label small fw-semibold">Associated College *</label>
                <select name="collegeId" className="form-select" value={form.collegeId} onChange={handleChange} required>
                  <option value="">-- Select your institution --</option>
                  {colleges.map((c) => (<option key={c.id} value={c.id}>{c.name} — {c.city}, {c.state}</option>))}
                </select>
              </div>
            </div>

            <button className="btn btn-primary w-100 py-2 fw-semibold mt-4" type="submit" disabled={loading}
              style={{ backgroundColor: "#0f4c81", borderColor: "#0f4c81" }}>
              {loading ? (<span><span className="spinner-border spinner-border-sm me-2"></span>Registering...</span>)
                : (<><i className="bi bi-shield-lock-fill me-2"></i>Register as TPO Officer</>)}
            </button>
          </form>

          <div className="text-center mt-3 pt-2 text-muted small">
            Already have an account?{" "}<Link to="/login" className="text-decoration-none fw-semibold">Sign In</Link>
            <span className="mx-2">|</span>
            <Link to="/register" className="text-decoration-none">Student Registration</Link>
            <span className="mx-2">|</span>
            <Link to="/register-recruiter" className="text-decoration-none">Recruiter Registration</Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TpoRegisterPage;
