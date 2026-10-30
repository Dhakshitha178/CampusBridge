import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { createDrive } from "../../services/recruiterService";
import { getColleges } from "../../services/authService";
import { mergeColleges } from "../../constants/colleges";

const CreateDrivePage = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [colleges, setColleges] = useState(mergeColleges());

  const [form, setForm] = useState({
    title: "",
    jobRole: "Software Development Engineer",
    description: "",
    salaryPackage: 8.5,
    vacancies: 15,
    location: "Chennai / Bangalore",
    workMode: "HYBRID",
    collegeId: "",
    applicationDeadline: new Date(Date.now() + 14 * 24 * 60 * 60 * 1000).toISOString().slice(0, 10),
    driveDate: new Date(Date.now() + 21 * 24 * 60 * 60 * 1000).toISOString().slice(0, 10),
    criteria: {
      minCgpa: 7.5,
      maxBacklogs: 0,
      graduationYear: 2026,
      allowedDepartments: "Computer Science & Engineering,Information Technology",
      requiredSkills: "Java, Spring Boot, SQL, Data Structures",
    },
    stages: [
      {
        roundOrder: 1,
        stageName: "Online Coding & Aptitude Assessment",
        stageType: "ASSESSMENT",
        description: "90-minute online coding test on algorithms and core CS",
      },
      {
        roundOrder: 2,
        stageName: "Technical Interview - Systems & Problem Solving",
        stageType: "TECHNICAL_INTERVIEW",
        description: "1-on-1 technical interview focusing on Java/OOP and DB design",
      },
      {
        roundOrder: 3,
        stageName: "HR & Cultural Alignment Round",
        stageType: "HR_INTERVIEW",
        description: "Discussion on behavioural competencies, joining timeline and terms",
      },
    ],
  });

  useEffect(() => {
    getColleges()
      .then((res) => {
        if (res.success && res.data && res.data.length > 0) {
          setColleges(mergeColleges(res.data));
        }
      })
      .catch((err) => {
        console.error("Error loading colleges for drive targeting:", err);
      });
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({
      ...form,
      [name]:
        name === "salaryPackage"
          ? parseFloat(value) || 0
          : name === "vacancies"
          ? parseInt(value) || 0
          : name === "collegeId"
          ? (value ? parseInt(value) : "")
          : value,
    });
  };

  const handleCriteriaChange = (e) => {
    const { name, value } = e.target;
    setForm({
      ...form,
      criteria: {
        ...form.criteria,
        [name]:
          name === "minCgpa"
            ? parseFloat(value) || 0
            : name === "maxBacklogs" || name === "graduationYear"
            ? parseInt(value) || 0
            : value,
      },
    });
  };

  // Stage manipulation
  const handleStageChange = (idx, field, value) => {
    const updatedStages = [...form.stages];
    updatedStages[idx][field] = value;
    setForm({ ...form, stages: updatedStages });
  };

  const addStage = () => {
    const nextOrder = form.stages.length + 1;
    const newStage = {
      roundOrder: nextOrder,
      stageName: `Recruitment Round ${nextOrder}`,
      stageType: "TECHNICAL_INTERVIEW",
      description: "Candidate technical assessment",
    };
    setForm({ ...form, stages: [...form.stages, newStage] });
  };

  const removeStage = (idx) => {
    if (form.stages.length <= 1) {
      alert("At least one recruitment stage is required.");
      return;
    }
    const updated = form.stages
      .filter((_, i) => i !== idx)
      .map((stg, i) => ({ ...stg, roundOrder: i + 1 }));
    setForm({ ...form, stages: updated });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    setLoading(true);
    try {
      const res = await createDrive(form);
      if (res.success) {
        setSuccess("Placement drive published successfully! It has been submitted for TPO validation.");
        setTimeout(() => navigate("/recruiter/dashboard"), 1500);
      } else {
        setError(res.message || "Failed to create placement drive.");
      }
    } catch (err) {
      setError(err.response?.data?.message || "Failed to create drive. Check all fields.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Configure Placement Drive</h3>
          <p className="text-muted small mb-0">
            Define corporate job profile, transparent eligibility criteria rules, and custom recruitment stages.
          </p>
        </div>
        <Link to="/recruiter/dashboard" className="btn btn-outline-secondary btn-sm">
          Cancel &amp; Return
        </Link>
      </div>

      {error && <div className="alert alert-danger py-2 small">{error}</div>}
      {success && <div className="alert alert-success py-2 small">{success}</div>}

      <form onSubmit={handleSubmit}>
        {/* SECTION 1: ROLE OVERVIEW */}
        <div className="content-card mb-4">
          <div className="content-card-header bg-light">
            <h5><i className="bi bi-briefcase me-2 text-primary"></i> 1. Job Role &amp; Commercial Terms</h5>
          </div>
          <div className="content-card-body">
            <div className="row g-3">
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Drive Campaign Title *</label>
                <input
                  type="text"
                  name="title"
                  className="form-control form-control-sm"
                  placeholder="e.g. Zoho Campus Hiring 2026 - Product Development"
                  value={form.title}
                  onChange={handleChange}
                  required
                />
              </div>
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Designation / Role *</label>
                <input
                  type="text"
                  name="jobRole"
                  className="form-control form-control-sm"
                  placeholder="e.g. Member Technical Staff (MTS)"
                  value={form.jobRole}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="col-md-3">
                <label className="form-label small fw-semibold">Salary Package (LPA CTC) *</label>
                <input
                  type="number"
                  step="0.1"
                  min="1"
                  name="salaryPackage"
                  className="form-control form-control-sm"
                  value={form.salaryPackage}
                  onChange={handleChange}
                  required
                />
              </div>
              <div className="col-md-3">
                <label className="form-label small fw-semibold">Estimated Vacancies *</label>
                <input
                  type="number"
                  min="1"
                  name="vacancies"
                  className="form-control form-control-sm"
                  value={form.vacancies}
                  onChange={handleChange}
                  required
                />
              </div>
              <div className="col-md-3">
                <label className="form-label small fw-semibold">Work Mode</label>
                <select
                  name="workMode"
                  className="form-select form-select-sm"
                  value={form.workMode}
                  onChange={handleChange}
                >
                  <option value="ON_SITE">Onsite</option>
                  <option value="HYBRID">Hybrid</option>
                  <option value="REMOTE">Remote</option>
                </select>
              </div>
              <div className="col-md-3">
                <label className="form-label small fw-semibold">Application Deadline *</label>
                <input
                  type="date"
                  name="applicationDeadline"
                  className="form-control form-control-sm"
                  value={form.applicationDeadline}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="col-md-3">
                <label className="form-label small fw-semibold">Drive Date *</label>
                <input
                  type="date"
                  name="driveDate"
                  className="form-control form-control-sm"
                  value={form.driveDate}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="col-md-6">
                <label className="form-label small fw-semibold">Work Location</label>
                <input
                  type="text"
                  name="location"
                  className="form-control form-control-sm"
                  value={form.location}
                  onChange={handleChange}
                />
              </div>

              <div className="col-md-6">
                <label className="form-label small fw-semibold">Target Institution / College</label>
                <select
                  name="collegeId"
                  className="form-select form-select-sm"
                  value={form.collegeId || ""}
                  onChange={handleChange}
                >
                  <option value="">All Affiliated Institutions (Central Pool)</option>
                  {colleges.map((col) => (
                    <option key={col.id} value={col.id}>
                      {col.name} ({col.code || "COL"}){col.city ? ` — ${col.city}` : ""}
                    </option>
                  ))}
                </select>
                <div className="form-text small">
                  Target a specific institution or leave as Central Pool for cross-campus hiring.
                </div>
              </div>

              <div className="col-12">
                <label className="form-label small fw-semibold">Detailed Job Description *</label>
                <textarea
                  rows="3"
                  name="description"
                  className="form-control form-control-sm"
                  placeholder="Responsibilities, technical challenges, working environment..."
                  value={form.description}
                  onChange={handleChange}
                  required
                ></textarea>
              </div>
            </div>
          </div>
        </div>

        {/* SECTION 2: CONFIGURABLE ELIGIBILITY CRITERIA */}
        <div className="content-card mb-4">
          <div className="content-card-header bg-light">
            <h5><i className="bi bi-shield-check me-2 text-primary"></i> 2. Transparent Eligibility Criteria Engine</h5>
          </div>
          <div className="content-card-body">
            <div className="row g-3">
              <div className="col-md-3">
                <label className="form-label small fw-semibold">Minimum CGPA *</label>
                <input
                  type="number"
                  step="0.1"
                  min="0"
                  max="10"
                  name="minCgpa"
                  className="form-control form-control-sm"
                  value={form.criteria.minCgpa}
                  onChange={handleCriteriaChange}
                  required
                />
              </div>
              <div className="col-md-3">
                <label className="form-label small fw-semibold">Max Standing Backlogs *</label>
                <input
                  type="number"
                  min="0"
                  name="maxBacklogs"
                  className="form-control form-control-sm"
                  value={form.criteria.maxBacklogs}
                  onChange={handleCriteriaChange}
                  required
                />
              </div>
              <div className="col-md-3">
                <label className="form-label small fw-semibold">Graduation Batch Year *</label>
                <input
                  type="number"
                  name="graduationYear"
                  className="form-control form-control-sm"
                  value={form.criteria.graduationYear}
                  onChange={handleCriteriaChange}
                  required
                />
              </div>

              <div className="col-md-6">
                <label className="form-label small fw-semibold">Permitted Departments (Comma-separated)</label>
                <input
                  type="text"
                  name="allowedDepartments"
                  className="form-control form-control-sm"
                  value={form.criteria.allowedDepartments}
                  onChange={handleCriteriaChange}
                />
              </div>
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Required Technical Skills (Comma-separated)</label>
                <input
                  type="text"
                  name="requiredSkills"
                  className="form-control form-control-sm"
                  value={form.criteria.requiredSkills}
                  onChange={handleCriteriaChange}
                />
              </div>
            </div>
          </div>
        </div>

        {/* SECTION 3: DYNAMIC RECRUITMENT STAGES */}
        <div className="content-card mb-4">
          <div className="content-card-header bg-light d-flex justify-content-between align-items-center">
            <h5><i className="bi bi-bezier2 me-2 text-primary"></i> 3. Dynamic Multi-Round Recruitment Pipeline</h5>
            <button type="button" className="btn btn-outline-primary btn-sm" onClick={addStage}>
              + Add Custom Stage
            </button>
          </div>
          <div className="content-card-body">
            <p className="text-muted small">
              Define each evaluation round. Candidates will progress sequentially through these rounds based on panel scores.
            </p>
            {form.stages.map((stage, idx) => (
              <div key={idx} className="p-3 bg-light rounded border mb-3">
                <div className="d-flex justify-content-between align-items-center mb-2">
                  <span className="badge bg-primary">Round {idx + 1}</span>
                  {form.stages.length > 1 && (
                    <button
                      type="button"
                      className="btn btn-link text-danger p-0 small text-decoration-none"
                      onClick={() => removeStage(idx)}
                    >
                      <i className="bi bi-trash"></i> Remove Round
                    </button>
                  )}
                </div>

                <div className="row g-2">
                  <div className="col-md-5">
                    <label className="form-label small fw-semibold">Stage Title</label>
                    <input
                      type="text"
                      className="form-control form-control-sm"
                      value={stage.stageName}
                      onChange={(e) => handleStageChange(idx, "stageName", e.target.value)}
                      required
                    />
                  </div>
                  <div className="col-md-3">
                    <label className="form-label small fw-semibold">Stage Type</label>
                    <select
                      className="form-select form-select-sm"
                      value={stage.stageType}
                      onChange={(e) => handleStageChange(idx, "stageType", e.target.value)}
                    >
                      <option value="ASSESSMENT">Online Assessment</option>
                      <option value="APTITUDE">Aptitude Test</option>
                      <option value="CODING">Coding Challenge</option>
                      <option value="TECHNICAL_INTERVIEW">Technical Interview</option>
                      <option value="MANAGERIAL_INTERVIEW">Managerial Interview</option>
                      <option value="HR_INTERVIEW">HR Interview</option>
                      <option value="GROUP_DISCUSSION">Group Discussion</option>
                    </select>
                  </div>
                  <div className="col-md-4">
                    <label className="form-label small fw-semibold">Stage Purpose / Instructions</label>
                    <input
                      type="text"
                      className="form-control form-control-sm"
                      value={stage.description}
                      onChange={(e) => handleStageChange(idx, "description", e.target.value)}
                    />
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        <button
          type="submit"
          className="btn btn-primary btn-lg w-100 py-3 fw-bold mb-4 shadow-sm"
          disabled={loading}
          style={{ backgroundColor: "#0f4c81" }}
        >
          {loading ? "Publishing Drive..." : "Publish Placement Drive & Submit for TPO Approval"}
        </button>
      </form>
    </div>
  );
};

export default CreateDrivePage;
