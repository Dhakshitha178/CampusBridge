import React, { useState, useEffect } from "react";
import { getLiveMonitor } from "../../services/tpoService";
import Loader from "../../components/common/Loader";

const LiveDriveMonitorPage = () => {
  const [monitorData, setMonitorData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");

  const loadData = async () => {
    try {
      setLoading(true);
      const res = await getLiveMonitor();
      if (res.success && res.data) {
        setMonitorData(res.data);
      }
    } catch (err) {
      console.error("Error loading live drive monitor:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
    const interval = setInterval(loadData, 15000); // Live poll every 15 seconds
    return () => clearInterval(interval);
  }, []);

  if (loading && monitorData.length === 0) {
    return <Loader message="Connecting to live drive recruitment telemetry..." />;
  }

  const filteredData = monitorData.filter(
    (item) =>
      (item.companyName || "").toLowerCase().includes(searchTerm.toLowerCase()) ||
      (item.driveTitle || item.jobTitle || "").toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">
            Multi-Company Live Recruitment Lifecycle Monitor 📡
          </h3>
          <p className="text-muted small mb-0">
            Real-time candidate progression and stage funnel across all active company hiring drives.
          </p>
        </div>
        <div className="d-flex align-items-center gap-2">
          <span className="badge bg-success-subtle text-success border border-success-subtle px-2 py-1">
            <span className="spinner-grow spinner-grow-sm me-1" role="status" style={{ width: "8px", height: "8px" }}></span>
            Live Telemetry (15s Auto-Sync)
          </span>
          <button className="btn btn-outline-secondary btn-sm" onClick={loadData}>
            <i className="bi bi-arrow-clockwise"></i>
          </button>
        </div>
      </div>

      {/* SEARCH / FILTER */}
      <div className="card shadow-sm border-0 mb-4 p-3 bg-white">
        <div className="row g-2 align-items-center">
          <div className="col-md-6">
            <div className="input-group input-group-sm">
              <span className="input-group-text bg-light"><i className="bi bi-search"></i></span>
              <input
                type="text"
                className="form-control"
                placeholder="Filter by company or drive name..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
          </div>
          <div className="col-md-6 text-end small text-muted">
            Tracking <strong>{monitorData.length}</strong> active recruitment pipelines
          </div>
        </div>
      </div>

      {filteredData.length === 0 ? (
        <div className="text-center p-5 bg-white rounded border text-muted">
          No live drives currently active.
        </div>
      ) : (
        <div className="row g-4">
          {filteredData.map((drive) => {
            const total = drive.totalApplicants !== undefined ? drive.totalApplicants : (drive.totalDriveApplicants || 0);
            const selected = drive.selectedCount || 0;
            const inProgress = drive.inProgressCount !== undefined ? drive.inProgressCount : Math.max(0, total - selected - (drive.rejectedCount || 0));
            const rejected = drive.rejectedCount || 0;
            const title = drive.driveTitle || drive.jobTitle || "Campus Recruitment Drive";
            const pkg = drive.salaryPackage || drive.packageLpa || "Competitive";
            const mode = drive.workMode || "ON_SITE";
            const vacancies = drive.vacancies || 0;
            const status = drive.driveStatus || drive.stageStatus || "ACTIVE";
            const activeRound = drive.currentActiveStage || drive.currentStage || "Round 1 Active";

            const selectedPct = total > 0 ? ((selected / total) * 100).toFixed(1) : 0;
            const inProgressPct = total > 0 ? ((inProgress / total) * 100).toFixed(1) : 0;
            const rejectedPct = total > 0 ? ((rejected / total) * 100).toFixed(1) : 0;

            return (
              <div key={drive.driveId} className="col-12">
                <div className="card shadow-sm border-0" style={{ borderRadius: "10px" }}>
                  <div className="card-body p-4">
                    <div className="d-flex justify-content-between align-items-start mb-3">
                      <div>
                        <div className="d-flex align-items-center gap-2 mb-1">
                          <span className="badge bg-light text-dark border">
                            <i className="bi bi-building me-1"></i> {drive.companyName}
                          </span>
                          <span className="badge bg-primary-subtle text-primary">
                            Drive ID: #{drive.driveId}
                          </span>
                          <span className="badge bg-success-subtle text-success">
                            {status}
                          </span>
                        </div>
                        <h4 className="fw-bold text-dark mb-0">{title}</h4>
                        <div className="text-muted small">
                          Package: <strong>{pkg} LPA</strong> &bull; Mode: {mode} &bull; Vacancies: {vacancies}
                        </div>
                      </div>

                      <div className="text-end">
                        <div className="small text-muted">Active Current Round:</div>
                        <span className="badge bg-primary fs-6 px-3 py-2 mt-1">
                          {activeRound}
                        </span>
                      </div>
                    </div>

                    {/* METRIC CHIPS */}
                    <div className="row g-2 mb-3 text-center small">
                      <div className="col-md-3 col-6">
                        <div className="p-2 bg-light rounded border">
                          <div className="text-muted">Total Applicants</div>
                          <div className="fs-5 fw-bold text-dark">{total}</div>
                        </div>
                      </div>
                      <div className="col-md-3 col-6">
                        <div className="p-2 bg-warning-subtle rounded border border-warning-subtle">
                          <div className="text-dark">In Pipeline / Rounds</div>
                          <div className="fs-5 fw-bold text-dark">{inProgress}</div>
                        </div>
                      </div>
                      <div className="col-md-3 col-6">
                        <div className="p-2 bg-success-subtle rounded border border-success-subtle">
                          <div className="text-success">Offers / Selected</div>
                          <div className="fs-5 fw-bold text-success">{selected}</div>
                        </div>
                      </div>
                      <div className="col-md-3 col-6">
                        <div className="p-2 bg-danger-subtle rounded border border-danger-subtle">
                          <div className="text-danger">Not Selected</div>
                          <div className="fs-5 fw-bold text-danger">{rejected}</div>
                        </div>
                      </div>
                    </div>

                    {/* PROGRESSION FUNNEL BAR */}
                    <div className="mb-2">
                      <div className="d-flex justify-content-between small text-muted mb-1">
                        <span>Recruitment Funnel Distribution:</span>
                        <span>
                          {selectedPct}% Selected &bull; {inProgressPct}% In Rounds &bull; {rejectedPct}% Disqualified
                        </span>
                      </div>
                      <div className="progress" style={{ height: "12px", borderRadius: "6px" }}>
                        <div
                          className="progress-bar bg-success"
                          role="progressbar"
                          style={{ width: `${selectedPct}%` }}
                          title={`Selected: ${selected}`}
                        ></div>
                        <div
                          className="progress-bar bg-warning progress-bar-striped progress-bar-animated"
                          role="progressbar"
                          style={{ width: `${inProgressPct}%` }}
                          title={`In Progress: ${inProgress}`}
                        ></div>
                        <div
                          className="progress-bar bg-secondary opacity-50"
                          role="progressbar"
                          style={{ width: `${rejectedPct}%` }}
                          title={`Not Selected: ${rejected}`}
                        ></div>
                      </div>
                    </div>

                    {/* STAGES BREAKDOWN CHIPS */}
                    {drive.stageBreakdown && drive.stageBreakdown.length > 0 && (
                      <div className="mt-3 pt-3 border-top">
                        <div className="small text-muted fw-semibold mb-2">Stage-by-Stage Candidate Density:</div>
                        <div className="d-flex flex-wrap gap-2">
                          {drive.stageBreakdown.map((stg, i) => (
                            <div key={i} className="stage-step-chip">
                              <span className="fw-bold">{stg.stageName}:</span>
                              <span className="badge bg-primary rounded-pill">{stg.candidateCount} candidates</span>
                            </div>
                          ))}
                        </div>
                      </div>
                    )}
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

export default LiveDriveMonitorPage;
