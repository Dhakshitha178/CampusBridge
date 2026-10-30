import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { registerStudent, getColleges } from "../../services/authService";
import { mergeColleges } from "../../constants/colleges";

const INITIAL_COLLEGES = mergeColleges();

const RegisterPage = () => {
  const navigate = useNavigate();
  const [colleges, setColleges] = useState(INITIAL_COLLEGES);
  const [form, setForm] = useState({
    fullName: "",
    rollNumber: "",
    email: "",
    password: "",
    phoneNumber: "",
    department: "Computer Science & Engineering",
    academicYear: "Final Year (4th Year)",
    graduationYear: 2026,
    cgpa: 8.0,
    backlogs: 0,
    skills: "Java, Spring Boot, React, SQL",
    collegeId: INITIAL_COLLEGES[0]?.id || 1,
  });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchColleges = async () => {
      try {
        const res = await getColleges();
        if (res.success && res.data && res.data.length > 0) {
          const list = mergeColleges(res.data);
          setColleges(list);
          setForm((prev) => ({ ...prev, collegeId: prev.collegeId || list[0].id }));
        }
      } catch (err) {
        console.warn("Could not fetch colleges dynamically, using regional defaults.");
      }
    };
    fetchColleges();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({
      ...form,
      [name]:
        name === "cgpa"
          ? parseFloat(value) || 0
          : name === "backlogs" || name === "graduationYear" || name === "collegeId"
          ? parseInt(value) || 0
          : value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    setLoading(true);
    try {
      const res = await registerStudent(form);
      if (res.success) {
        setSuccess("Registration successful! Redirecting to login...");
        setTimeout(() => navigate("/login"), 1500);
      } else {
        setError(res.message || "Registration failed");
      }
    } catch (err) {
      setError(err.response?.data?.message || "Registration failed. Please check your details.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card shadow-lg border-0 my-4" style={{ maxWidth: "680px", width: "100%", borderRadius: "12px" }}>
      <div
        className="card-header text-white text-center py-3"
        style={{ background: "linear-gradient(135deg, #0f2027, #203a43, #2c5364)", borderTopLeftRadius: "12px", borderTopRightRadius: "12px" }}
      >
        <h4 className="fw-bold mb-1">Student Portal Registration</h4>
        <p className="small mb-0 opacity-75">Join the CampusBridge centralized placement pool</p>
      </div>

      <div className="card-body p-4">
        {error && (
          <div className="alert alert-danger py-2 small d-flex align-items-center gap-2">
            <i className="bi bi-exclamation-triangle-fill"></i>
            <div>{error}</div>
          </div>
        )}
        {success && (
          <div className="alert alert-success py-2 small d-flex align-items-center gap-2">
            <i className="bi bi-check-circle-fill"></i>
            <div>{success}</div>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="row g-3">
            <div className="col-md-6">
              <label className="form-label small fw-semibold">Full Name *</label>
              <input
                type="text"
                name="fullName"
                className="form-control form-control-sm"
                placeholder="e.g. Arun Prakash"
                value={form.fullName}
                onChange={handleChange}
                required
              />
            </div>
            <div className="col-md-6">
              <label className="form-label small fw-semibold">Register / Roll Number *</label>
              <input
                type="text"
                name="rollNumber"
                className="form-control form-control-sm"
                placeholder="e.g. 717822P101"
                value={form.rollNumber}
                onChange={handleChange}
                required
              />
            </div>

            <div className="col-md-6">
              <label className="form-label small fw-semibold">Email Address *</label>
              <input
                type="email"
                name="email"
                className="form-control form-control-sm"
                placeholder="student@college.ac.in"
                value={form.email}
                onChange={handleChange}
                required
              />
            </div>
            <div className="col-md-6">
              <label className="form-label small fw-semibold">Password *</label>
              <input
                type="password"
                name="password"
                className="form-control form-control-sm"
                placeholder="At least 6 characters"
                value={form.password}
                onChange={handleChange}
                required
              />
            </div>

            <div className="col-md-6">
              <label className="form-label small fw-semibold">College / Institution *</label>
              <select
                name="collegeId"
                className="form-select form-select-sm"
                value={form.collegeId}
                onChange={handleChange}
                required
              >
                {colleges.map((col) => (
                  <option key={col.id} value={col.id}>
                    {col.name} ({col.code || "COL"}){col.city ? ` — ${col.city}` : ""}
                  </option>
                ))}
              </select>
            </div>
            <div className="col-md-6">
              <label className="form-label small fw-semibold">Department *</label>
              <select
                name="department"
                className="form-select form-select-sm"
                value={form.department}
                onChange={handleChange}
                required
              >
                <option value="Computer Science & Engineering">Computer Science & Engineering</option>
                <option value="Information Technology">Information Technology</option>
                <option value="Electronics & Communication">Electronics & Communication</option>
                <option value="Electrical & Electronics">Electrical & Electronics</option>
                <option value="Mechanical Engineering">Mechanical Engineering</option>
              </select>
            </div>

            <div className="col-md-4">
              <label className="form-label small fw-semibold">CGPA *</label>
              <input
                type="number"
                step="0.01"
                min="0"
                max="10"
                name="cgpa"
                className="form-control form-control-sm"
                value={form.cgpa}
                onChange={handleChange}
                required
              />
            </div>
            <div className="col-md-4">
              <label className="form-label small fw-semibold">Standing Backlogs *</label>
              <input
                type="number"
                min="0"
                name="backlogs"
                className="form-control form-control-sm"
                value={form.backlogs}
                onChange={handleChange}
                required
              />
            </div>
            <div className="col-md-4">
              <label className="form-label small fw-semibold">Graduation Year *</label>
              <input
                type="number"
                name="graduationYear"
                className="form-control form-control-sm"
                value={form.graduationYear}
                onChange={handleChange}
                required
              />
            </div>

            <div className="col-md-6">
              <label className="form-label small fw-semibold">Phone Number</label>
              <input
                type="text"
                name="phoneNumber"
                className="form-control form-control-sm"
                placeholder="+91 9876543210"
                value={form.phoneNumber}
                onChange={handleChange}
              />
            </div>
            <div className="col-md-6">
              <label className="form-label small fw-semibold">Academic Year</label>
              <input
                type="text"
                name="academicYear"
                className="form-control form-control-sm"
                value={form.academicYear}
                onChange={handleChange}
              />
            </div>

            <div className="col-12">
              <label className="form-label small fw-semibold">Technical Skills (Comma separated)</label>
              <input
                type="text"
                name="skills"
                className="form-control form-control-sm"
                placeholder="e.g. Java, Python, React, Spring Boot, MySQL"
                value={form.skills}
                onChange={handleChange}
              />
            </div>
          </div>

          <button
            className="btn btn-primary w-100 py-2 fw-semibold mt-4"
            type="submit"
            disabled={loading}
            style={{ backgroundColor: "#0f4c81", borderColor: "#0f4c81" }}
          >
            {loading ? (
              <span><span className="spinner-border spinner-border-sm me-2"></span>Creating Profile...</span>
            ) : (
              "Complete Registration"
            )}
          </button>
        </form>

        <div className="text-center mt-3 pt-2 text-muted small border-top">
          Already registered? <Link to="/login" className="text-decoration-none fw-semibold">Sign in here</Link>
          <div className="mt-2 text-secondary">
            <Link to="/register-recruiter" className="text-decoration-none mx-1 text-muted">
              Recruiter Registration
            </Link>
            <span>|</span>
            <Link to="/register-tpo" className="text-decoration-none mx-1 text-muted">
              TPO Registration
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;