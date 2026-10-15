import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { registerRecruiter, getCompanies } from "../../services/authService";

const RecruiterRegisterPage = () => {
  const navigate = useNavigate();
  const [companies, setCompanies] = useState([]);
  const [form, setForm] = useState({
    fullName: "",
    email: "",
    password: "",
    confirmPassword: "",
    phoneNumber: "",
    designation: "",
    companyId: "",
    companyName: "",
    companyIndustry: "",
    companyWebsite: "",
  });
  const [useExistingCompany, setUseExistingCompany] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    getCompanies()
      .then((res) => { if (res.success && res.data) setCompanies(res.data); })
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
        phoneNumber: form.phoneNumber, designation: form.designation,
        companyId: useExistingCompany && form.companyId ? parseInt(form.companyId) : null,
        companyName: !useExistingCompany ? form.companyName : null,
        companyIndustry: !useExistingCompany ? form.companyIndustry : null,
        companyWebsite: !useExistingCompany ? form.companyWebsite : null,
      };
      const res = await registerRecruiter(payload);
      if (res.success) {
        setSuccess("Recruiter account created successfully! Redirecting to login...");
        setTimeout(() => navigate("/login"), 2500);
      } else { setError(res.message || "Registration failed."); }
    } catch (err) {
      setError(err.response?.data?.message || "Registration failed. Please try again.");
    } finally { setLoading(false); }
  };

  return (
    <div className="min-vh-100 d-flex align-items-center justify-content-center"
      style={{ background: "linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%)" }}>
      <div className="card shadow-lg border-0" style={{ maxWidth: "560px", width: "100%", borderRadius: "12px", margin: "1rem" }}>
        <div className="card-header text-white text-center py-4"
          style={{ background: "linear-gradient(135deg, #1a1a2e, #16213e, #0f3460)", borderTopLeftRadius: "12px", borderTopRightRadius: "12px" }}>
          <i className="bi bi-building-fill fs-1 text-warning mb-2 d-block"></i>
          <h3 className="fw-bold mb-1">Recruiter Registration</h3>
          <p className="small mb-0 opacity-75">CampusBridge — Campus Talent Acquisition Portal</p>
        </div>

        <div className="card-body p-4">
          {error && (<div className="alert alert-danger py-2 small" role="alert"><i className="bi bi-exclamation-triangle-fill me-2"></i>{error}</div>)}
          {success && (<div className="alert alert-success py-2 small" role="alert"><i className="bi bi-check-circle-fill me-2"></i>{success}</div>)}

          <form onSubmit={handleSubmit}>
            <div className="row g-3">
              <div className="col-12">
                <label className="form-label small fw-semibold">Full Name *</label>
                <input type="text" name="fullName" className="form-control" placeholder="Your full name"
                  value={form.fullName} onChange={handleChange} required />
              </div>
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Email Address *</label>
                <input type="email" name="email" className="form-control" placeholder="you@company.com"
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
                <label className="form-label small fw-semibold">Designation</label>
                <input type="text" name="designation" className="form-control" placeholder="e.g. Campus Recruiter, HR Lead"
                  value={form.designation} onChange={handleChange} />
              </div>
              <div className="col-12">
                <div className="form-check form-switch mb-2">
                  <input className="form-check-input" type="checkbox" id="companySwitch"
                    checked={useExistingCompany} onChange={(e) => setUseExistingCompany(e.target.checked)} />
                  <label className="form-check-label small fw-semibold" htmlFor="companySwitch">
                    {useExistingCompany ? "Select Existing Company" : "Register New Company"}
                  </label>
                </div>
              </div>
              {useExistingCompany ? (
                <div className="col-12">
                  <label className="form-label small fw-semibold">Select Company *</label>
                  <select name="companyId" className="form-select" value={form.companyId} onChange={handleChange} required={useExistingCompany}>
                    <option value="">-- Choose a company --</option>
                    {companies.map((c) => (<option key={c.id} value={c.id}>{c.name} — {c.industry}</option>))}
                  </select>
                </div>
              ) : (
                <>
                  <div className="col-12">
                    <label className="form-label small fw-semibold">Company Name *</label>
                    <input type="text" name="companyName" className="form-control" placeholder="Your company name"
                      value={form.companyName} onChange={handleChange} required={!useExistingCompany} />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small fw-semibold">Industry</label>
                    <input type="text" name="companyIndustry" className="form-control" placeholder="e.g. IT Services"
                      value={form.companyIndustry} onChange={handleChange} />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small fw-semibold">Company Website</label>
                    <input type="url" name="companyWebsite" className="form-control" placeholder="https://company.com"
                      value={form.companyWebsite} onChange={handleChange} />
                  </div>
                </>
              )}
            </div>
            <button className="btn btn-warning w-100 py-2 fw-semibold mt-4" type="submit" disabled={loading}>
              {loading ? (<span><span className="spinner-border spinner-border-sm me-2"></span>Registering...</span>)
                : (<><i className="bi bi-building-fill me-2"></i>Register as Recruiter</>)}
            </button>
          </form>

          <div className="text-center mt-3 pt-2 text-muted small">
            Already have an account?{" "}<Link to="/login" className="text-decoration-none fw-semibold">Sign In</Link>
            <span className="mx-2">|</span>
            <Link to="/register" className="text-decoration-none">Student Registration</Link>
            <span className="mx-2">|</span>
            <Link to="/register-tpo" className="text-decoration-none">TPO Registration</Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RecruiterRegisterPage;
