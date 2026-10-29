import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { getActiveDrives, checkDriveEligibility } from "../../services/driveService";
import { applyToDrive, getStudentApplications } from "../../services/studentService";
import Loader from "../../components/common/Loader";

const BrowsedrivesPage = () => {
  const [drives, setDrives] = useState([]);
  const [eligibilityMap, setEligibilityMap] = useState({});
  const [appliedDriveIds, setAppliedDriveIds] = useState(new Set());
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const [modeFilter, setModeFilter] = useState("ALL");
  const [minSalaryFilter, setMinSalaryFilter] = useState(0);
  const [applyingId, setApplyingId] = useState(null);
  const [actionMessage, setActionMessage] = useState({ text: "", type: "" });

  const loadData = async () => {
    try {
      setLoading(true);
      const [drivesRes, appsRes] = await Promise.all([
        getActiveDrives(),
        getStudentApplications(),
      ]);

      if (drivesRes.success && drivesRes.data) {
        setDrives(drivesRes.data);

        // Pre-fetch eligibility check for each drive if not already on drive object
        const eligMap = {};
        await Promise.all(
          drivesRes.data.map(async (drive) => {
            if (drive.studentEligibility) {
              eligMap[drive.id] = drive.studentEligibility;
            } else {
              try {
                const res = await checkDriveEligibility(drive.id);
                if (res.success && res.data) {
                  eligMap[drive.id] = res.data;
                }
              } catch (e) {
                console.warn(`Eligibility check failed for drive ${drive.id}`);
              }
            }
          })
        );
        setEligibilityMap(eligMap);
      }

      if (appsRes.success && appsRes.data) {
        const appliedSet = new Set(appsRes.data.map((app) => app.driveId));
        setAppliedDriveIds(appliedSet);
      }
    } catch (err) {
      console.error("Error loading active drives:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleApply = async (driveId) => {
    setApplyingId(driveId);
    setActionMessage({ text: "", type: "" });
    try {
      const res = await applyToDrive(driveId);
      if (res.success) {
        setActionMessage({
          text: "Application submitted successfully! Track your progress in My Applications.",
          type: "success",
        });
        setAppliedDriveIds((prev) => new Set([...prev, driveId]));
      } else {
        setActionMessage({ text: res.message || "Failed to apply.", type: "danger" });
      }
    } catch (err) {
      setActionMessage({
        text: err.response?.data?.message || "Failed to submit application.",
        type: "danger",
      });
    } finally {
      setApplyingId(null);
    }
  };

  const filteredDrives = drives.filter((drive) => {
    const title = drive.jobTitle || drive.title || "";
    const company = drive.companyName || "";
    const role = drive.jobRole || drive.jobTitle || "";
    const matchesSearch =
      title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      company.toLowerCase().includes(searchTerm.toLowerCase()) ||
      role.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesMode = modeFilter === "ALL" || drive.workMode === modeFilter;
    const salary = drive.packageLpa || drive.salaryPackage || 0;
    const matchesSalary = !minSalaryFilter || salary >= minSalaryFilter;
    return matchesSearch && matchesMode && matchesSalary;
  });

  if (loading) return <Loader message="Discovering active placement drives..." />;

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Browse Placement Drives</h3>
          <p className="text-muted small mb-0">
            Automated, explainable eligibility checking tailored to your academic profile.
          </p>
        </div>
        <Link to="/student/applications" className="btn btn-outline-primary btn-sm">
          <i className="bi bi-layers me-1"></i> My Applications
        </Link>
      </div>

      {actionMessage.text && (
        <div className={`alert alert-${actionMessage.type} py-2 small d-flex align-items-center gap-2 mb-3`}>
          <i className="bi bi-info-circle-fill"></i>
          <div>{actionMessage.text}</div>
        </div>
      )}

      {/* FILTER CONTROLS */}
      <div className="card shadow-sm border-0 mb-4 p-3 bg-white">
        <div className="row g-2 align-items-center">
          <div className="col-md-5">
            <div className="input-group input-group-sm">
              <span className="input-group-text bg-light"><i className="bi bi-search"></i></span>
              <input
                type="text"
                className="form-control"
                placeholder="Search by company or job role..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
          </div>
          <div className="col-md-3">
            <select
              className="form-select form-select-sm"
              value={modeFilter}
              onChange={(e) => setModeFilter(e.target.value)}
            >
              <option value="ALL">All Work Modes</option>
              <option value="ONSITE">Onsite</option>
              <option value="HYBRID">Hybrid</option>
              <option value="REMOTE">Remote</option>
            </select>
          </div>
          <div className="col-md-4">
            <div className="input-group input-group-sm">
              <span className="input-group-text bg-light">Min CTC:</span>
              <select
                className="form-select"
                value={minSalaryFilter}
                onChange={(e) => setMinSalaryFilter(Number(e.target.value))}
              >
                <option value={0}>Any Package</option>
                <option value={5}>5.0+ LPA</option>
                <option value={8}>8.0+ LPA</option>
                <option value={12}>12.0+ LPA</option>
              </select>
            </div>
          </div>
        </div>
      </div>

      {/* DRIVES LIST */}
      {filteredDrives.length === 0 ? (
        <div className="text-center p-5 bg-white rounded border text-muted">
          <i className="bi bi-briefcase fs-1 text-secondary mb-2 d-block"></i>
          No placement drives matched your search criteria.
        </div>
      ) : (
        <div className="row g-4">
          {filteredDrives.map((drive) => {
            const elig = drive.studentEligibility || eligibilityMap[drive.id];
            const isApplied = appliedDriveIds.has(drive.id);
            const isEligible = elig
              ? elig.isEligible !== undefined
                ? elig.isEligible
                : elig.eligible !== undefined
                ? elig.eligible
                : true
              : true;

            const deadlineText = drive.applicationDeadline
              ? new Date(drive.applicationDeadline).toLocaleDateString("en-US", {
                  month: "short",
                  day: "numeric",
                  year: "numeric",
                })
              : "Open";

            return (
              <div key={drive.id} className="col-md-6">
                <div className="card h-100 border-0 shadow-sm" style={{ borderRadius: "10px" }}>
                  <div className="card-body p-4 d-flex flex-column justify-content-between">
                    <div>
                      {/* Top Header */}
                      <div className="d-flex justify-content-between align-items-start mb-2">
                        <div>
                          <span className="badge bg-light text-dark border mb-1">
                            <i className="bi bi-building me-1"></i> {drive.companyName}
                          </span>
                          <h5 className="fw-bold text-dark mb-0">{drive.jobTitle || drive.title}</h5>
                          <div className="text-muted small">{drive.jobRole || drive.jobTitle}</div>
                        </div>
                        {elig && (
                          <span
                            className={`eligibility-pill ${
                              isEligible ? "eligibility-eligible" : "eligibility-ineligible"
                            }`}
                          >
                            <i className={`bi ${isEligible ? "bi-check-circle-fill" : "bi-x-circle-fill"}`}></i>
                            {isEligible ? "ELIGIBLE" : "INELIGIBLE"}
                          </span>
                        )}
                      </div>

                      {/* Package & Details Pills */}
                      <div className="d-flex flex-wrap gap-2 my-3">
                        <span className="badge bg-success-subtle text-success border border-success-subtle px-2 py-1">
                          <i className="bi bi-cash-stack me-1"></i> {drive.packageLpa || drive.salaryPackage} LPA
                        </span>
                        <span className="badge bg-secondary-subtle text-secondary px-2 py-1">
                          <i className="bi bi-geo-alt me-1"></i> {drive.location}
                        </span>
                        <span className="badge bg-info-subtle text-info-emphasis px-2 py-1">
                          <i className="bi bi-laptop me-1"></i> {drive.workMode}
                        </span>
                        <span className="badge bg-warning-subtle text-dark px-2 py-1">
                          <i className="bi bi-people me-1"></i> {drive.vacancies} Vacancies
                        </span>
                      </div>

                      <p className="text-secondary small mb-3" style={{ lineHeight: "1.4" }}>
                        {drive.jobDescription || drive.description
                          ? (drive.jobDescription || drive.description).length > 140
                            ? (drive.jobDescription || drive.description).substring(0, 140) + "..."
                            : (drive.jobDescription || drive.description)
                          : "Exciting campus hiring opportunity."}
                      </p>

                      {/* EXPLAINABLE ELIGIBILITY ACCORDION / BOX */}
                      {elig && (
                        <div
                          className={`p-2 rounded mb-3 small ${
                            isEligible ? "bg-light border" : "bg-danger-subtle border border-danger-subtle"
                          }`}
                        >
                          <div className="d-flex justify-content-between align-items-center mb-1">
                            <strong className="text-dark" style={{ fontSize: "0.8rem" }}>
                              <i className="bi bi-cpu me-1"></i> Eligibility Rule Breakdown:
                            </strong>
                            <span className="fw-bold text-primary" style={{ fontSize: "0.75rem" }}>
                              Skill Match: {elig.skillMatchPercentage || 0}%
                            </span>
                          </div>

                          {/* Skill match progress bar */}
                          <div className="progress mb-2" style={{ height: "5px" }}>
                            <div
                              className={`progress-bar ${isEligible ? "bg-success" : "bg-danger"}`}
                              role="progressbar"
                              style={{ width: `${elig.skillMatchPercentage || 0}%` }}
                            ></div>
                          </div>

                          {!isEligible && elig.reasons && elig.reasons.length > 0 && (
                            <ul className="mb-1 ps-3 text-danger small" style={{ fontSize: "0.78rem" }}>
                              {elig.reasons.map((r, i) => (
                                <li key={i}>{r}</li>
                              ))}
                            </ul>
                          )}

                          {elig.missingSkills && elig.missingSkills.length > 0 && (
                            <div className="text-secondary small" style={{ fontSize: "0.75rem" }}>
                              <span className="text-danger fw-semibold">Missing Skills: </span>
                              {elig.missingSkills.join(", ")}
                            </div>
                          )}
                        </div>
                      )}
                    </div>

                    {/* Bottom Action Footer */}
                    <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 pt-3 border-top mt-2">
                      <div className="small text-muted text-nowrap">
                        <i className="bi bi-calendar3 me-1"></i> Deadline:{" "}
                        <strong className="text-dark">{deadlineText}</strong>
                      </div>
                      <div className="d-flex gap-2">
                        <Link to={`/student/drives/${drive.id}`} className="btn btn-outline-secondary btn-sm">
                          Stages &amp; Details
                        </Link>
                        {isApplied ? (
                          <button className="btn btn-secondary btn-sm" disabled>
                            <i className="bi bi-check2 me-1"></i> Applied
                          </button>
                        ) : (
                          <button
                            className="btn btn-primary btn-sm"
                            disabled={!isEligible || applyingId === drive.id}
                            onClick={() => handleApply(drive.id)}
                            style={{ backgroundColor: "#0f4c81" }}
                          >
                            {applyingId === drive.id ? "Applying..." : "Apply Now"}
                          </button>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default BrowsedrivesPage;
