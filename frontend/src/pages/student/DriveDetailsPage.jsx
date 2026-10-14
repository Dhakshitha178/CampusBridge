import React, { useState, useEffect } from "react";
import { useParams, Link } from "react-router-dom";
import { getDriveDetails, checkDriveEligibility } from "../../services/driveService";
import { applyToDrive, getStudentApplications } from "../../services/studentService";
import Loader from "../../components/common/Loader";

const DriveDetailsPage = () => {
  const { driveId } = useParams();
  const [drive, setDrive] = useState(null);
  const [eligibility, setEligibility] = useState(null);
  const [isApplied, setIsApplied] = useState(false);
  const [loading, setLoading] = useState(true);
  const [applying, setApplying] = useState(false);
  const [msg, setMsg] = useState({ text: "", type: "" });

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [driveRes, eligRes, appsRes] = await Promise.all([
          getDriveDetails(driveId),
          checkDriveEligibility(driveId),
          getStudentApplications(),
        ]);

        if (driveRes.success && driveRes.data) {
          setDrive(driveRes.data);
        }
        if (eligRes.success && eligRes.data) {
          setEligibility(eligRes.data);
        }
        if (appsRes.success && appsRes.data) {
          const applied = appsRes.data.some((a) => a.driveId === Number(driveId));
          setIsApplied(applied);
        }
      } catch (err) {
        console.error("Error loading drive details:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [driveId]);

  const handleApply = async () => {
    setApplying(true);
    setMsg({ text: "", type: "" });
    try {
      const res = await applyToDrive(driveId);
      if (res.success) {
        setIsApplied(true);
        setMsg({ text: "Application submitted successfully!", type: "success" });
      } else {
        setMsg({ text: res.message || "Failed to apply.", type: "danger" });
      }
    } catch (err) {
      setMsg({
        text: err.response?.data?.message || "Failed to submit application.",
        type: "danger",
      });
    } finally {
      setApplying(false);
    }
  };

  if (loading) return <Loader message="Loading placement drive details..." />;
  if (!drive) return <div className="alert alert-warning">Drive not found.</div>;

  const isEligible = eligibility ? eligibility.isEligible : true;

  return (
    <div>
      {/* Back button */}
      <div className="mb-3">
        <Link to="/student/drives" className="btn btn-outline-secondary btn-sm">
          &larr; Back to Active Drives
        </Link>
      </div>

      {msg.text && (
        <div className={`alert alert-${msg.type} py-2 small d-flex align-items-center gap-2 mb-3`}>
          <i className="bi bi-info-circle-fill"></i>
          <div>{msg.text}</div>
        </div>
      )}

      {/* Hero Header */}
      <div className="card shadow-sm border-0 mb-4 bg-white" style={{ borderRadius: "10px" }}>
        <div className="card-body p-4">
          <div className="d-flex justify-content-between align-items-start flex-wrap gap-3">
            <div>
              <span className="badge bg-primary-subtle text-primary border mb-2">
                <i className="bi bi-building me-1"></i> {drive.companyName}
              </span>
              <h3 className="fw-bold text-dark mb-1">{drive.title}</h3>
              <p className="text-secondary small mb-2">{drive.jobRole} &bull; Job ID: #{drive.id}</p>
            </div>

            <div className="text-end">
              {eligibility && (
                <div className="mb-2">
                  <span
                    className={`eligibility-pill ${
                      isEligible ? "eligibility-eligible" : "eligibility-ineligible"
                    }`}
                  >
                    <i className={`bi ${isEligible ? "bi-check-circle-fill" : "bi-x-circle-fill"}`}></i>
                    {isEligible ? "ELIGIBLE FOR THIS DRIVE" : "NOT ELIGIBLE"}
                  </span>
                </div>
              )}
              {isApplied ? (
                <button className="btn btn-secondary btn-sm" disabled>
                  <i className="bi bi-check2-circle me-1"></i> Application Submitted
                </button>
              ) : (
                <button
                  className="btn btn-primary btn-sm px-4"
                  disabled={!isEligible || applying}
                  onClick={handleApply}
                  style={{ backgroundColor: "#0f4c81" }}
                >
                  {applying ? "Submitting..." : "Apply For Drive"}
                </button>
              )}
            </div>
          </div>

          <div className="d-flex flex-wrap gap-2 mt-3 pt-3 border-top">
            <div className="px-3 py-2 bg-light rounded text-center">
              <span className="small text-muted d-block">Package (CTC)</span>
              <strong className="text-success">{drive.salaryPackage} LPA</strong>
            </div>
            <div className="px-3 py-2 bg-light rounded text-center">
              <span className="small text-muted d-block">Work Mode</span>
              <strong>{drive.workMode}</strong>
            </div>
            <div className="px-3 py-2 bg-light rounded text-center">
              <span className="small text-muted d-block">Location</span>
              <strong>{drive.location}</strong>
            </div>
            <div className="px-3 py-2 bg-light rounded text-center">
              <span className="small text-muted d-block">Vacancies</span>
              <strong>{drive.vacancies} Positions</strong>
            </div>
            <div className="px-3 py-2 bg-light rounded text-center">
              <span className="small text-muted d-block">Deadline</span>
              <strong className="text-danger">{drive.applicationDeadline}</strong>
            </div>
          </div>
        </div>
      </div>

      <div className="row g-4">
        {/* Left Column: Job Description & Pipeline */}
        <div className="col-md-7">
          <div className="content-card">
            <div className="content-card-header">
              <h5><i className="bi bi-file-text me-2 text-primary"></i> Job Description &amp; Role Overview</h5>
            </div>
            <div className="content-card-body">
              <p style={{ whiteSpace: "pre-line", lineHeight: "1.6" }}>{drive.description}</p>
            </div>
          </div>

          {/* Configurable Recruitment Stages Pipeline */}
          <div className="content-card">
            <div className="content-card-header">
              <h5><i className="bi bi-bezier2 me-2 text-primary"></i> Multi-Stage Recruitment Journey</h5>
            </div>
            <div className="content-card-body">
              <p className="text-muted small">
                The hiring team has defined the following interview rounds and stages for this drive:
              </p>
              {!drive.stages || drive.stages.length === 0 ? (
                <div className="text-muted small">Standard selection process applies.</div>
              ) : (
                <div className="list-group list-group-flush">
                  {drive.stages.map((stage, idx) => (
                    <div key={stage.id} className="list-group-item d-flex align-items-start gap-3 py-3 px-2">
                      <div
                        className="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center fw-bold"
                        style={{ width: "32px", height: "32px", minWidth: "32px" }}
                      >
                        {idx + 1}
                      </div>
                      <div className="flex-grow-1">
                        <div className="d-flex justify-content-between">
                          <strong className="text-dark">{stage.stageName}</strong>
                          <span className="badge bg-light text-dark border">{stage.stageType}</span>
                        </div>
                        <div className="text-muted small mt-1">{stage.description || "Assessment & evaluation round"}</div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Right Column: Eligibility & Criteria */}
        <div className="col-md-5">
          <div className="content-card">
            <div className="content-card-header">
              <h5><i className="bi bi-shield-check me-2 text-primary"></i> Configured Eligibility Criteria</h5>
            </div>
            <div className="content-card-body">
              {drive.criteria ? (
                <div className="small">
                  <div className="d-flex justify-content-between py-2 border-bottom">
                    <span className="text-muted">Minimum CGPA</span>
                    <strong>{drive.criteria.minCgpa}</strong>
                  </div>
                  <div className="d-flex justify-content-between py-2 border-bottom">
                    <span className="text-muted">Maximum Standing Backlogs</span>
                    <strong>{drive.criteria.maxBacklogs}</strong>
                  </div>
                  <div className="d-flex justify-content-between py-2 border-bottom">
                    <span className="text-muted">Eligible Graduation Batch</span>
                    <strong>{drive.criteria.graduationYear}</strong>
                  </div>
                  <div className="py-2 border-bottom">
                    <span className="text-muted d-block mb-1">Eligible Departments</span>
                    <strong>{drive.criteria.allowedDepartments || "All Engineering Disciplines"}</strong>
                  </div>
                  <div className="py-2">
                    <span className="text-muted d-block mb-1">Required Skills</span>
                    <strong>{drive.criteria.requiredSkills || "General Aptitude"}</strong>
                  </div>
                </div>
              ) : (
                <span className="text-muted small">No special criteria set. Open to all students.</span>
              )}

              {/* Explainable Verification Output */}
              {eligibility && (
                <div className={`mt-3 p-3 rounded ${isEligible ? "bg-success-subtle" : "bg-danger-subtle"}`}>
                  <strong className={isEligible ? "text-success" : "text-danger"}>
                    {isEligible ? "✓ Profile Match Confirmed" : "✗ Ineligibility Reasons:"}
                  </strong>
                  {!isEligible && eligibility.reasons && (
                    <ul className="mb-0 mt-2 ps-3 small text-danger">
                      {eligibility.reasons.map((r, i) => (
                        <li key={i}>{r}</li>
                      ))}
                    </ul>
                  )}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DriveDetailsPage;
