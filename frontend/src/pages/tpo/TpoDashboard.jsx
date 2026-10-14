import React, { useState, useEffect, useContext } from "react";
import { Link } from "react-router-dom";
import { AuthContext } from "../../context/AuthContext";
import {
  getTpoDashboard,
  getRescheduleRequests,
  getPendingDrives,
  exportPlacementExcel,
} from "../../services/tpoService";
import Loader from "../../components/common/Loader";

const TpoDashboard = () => {
  const { user } = useContext(AuthContext);
  const [stats, setStats] = useState(null);
  const [pendingDrives, setPendingDrives] = useState([]);
  const [reschedules, setReschedules] = useState([]);
  const [loading, setLoading] = useState(true);
  const [exporting, setExporting] = useState(false);

  const loadData = async () => {
    try {
      setLoading(true);
      const [dashRes, drivesRes, reschedRes] = await Promise.all([
        getTpoDashboard(),
        getPendingDrives(),
        getRescheduleRequests(),
      ]);

      if (dashRes.success && dashRes.data) {
        setStats(dashRes.data);
      }
      if (drivesRes.success && drivesRes.data) {
        setPendingDrives(drivesRes.data);
      }
      if (reschedRes.success && reschedRes.data) {
        setReschedules(reschedRes.data.filter((r) => r.status === "PENDING"));
      }
    } catch (err) {
      console.error("Error loading TPO dashboard data:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleExportExcel = async () => {
    try {
      setExporting(true);
      await exportPlacementExcel();
    } catch (err) {
      alert("Failed to export Excel report.");
    } finally {
      setExporting(false);
    }
  };

  if (loading) return <Loader message="Loading TPO placement cell dashboard..." />;

  const hasConflicts = stats && stats.interviewConflicts > 0;
  const hasReschedules = reschedules.length > 0;

  return (
    <div>
      {/* Top Banner */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">
            TPO Placement Operations Center 🏛️
          </h3>
          <p className="text-muted small mb-0">
            {user?.collegeName || "Government College of Technology"} &bull; Placement Coordinator: <strong>{user?.fullName}</strong>
          </p>
        </div>
        <div className="d-flex gap-2">
          <Link to="/tpo/live-monitor" className="btn btn-primary btn-sm">
            <i className="bi bi-activity me-1"></i> Live Drive Monitor
          </Link>
          <button
            className="btn btn-outline-success btn-sm"
            onClick={handleExportExcel}
            disabled={exporting}
          >
            <i className="bi bi-file-earmark-excel me-1"></i> {exporting ? "Exporting..." : "Export Excel"}
          </button>
        </div>
      </div>

      {/* CONFLICT & RESCHEDULE ALERT NOTIFICATION */}
      {(hasConflicts || hasReschedules) && (
        <div className="row g-3 mb-4">
          {hasConflicts && (
            <div className="col-md-6">
              <div className="p-3 bg-danger-subtle border border-danger-subtle rounded d-flex justify-content-between align-items-center">
                <div>
                  <h6 className="text-danger fw-bold mb-1">
                    <i className="bi bi-exclamation-octagon-fill me-1"></i> {stats.interviewConflicts} Scheduling Conflict(s)
                  </h6>
                  <span className="small text-danger">Overlapping candidate interviews require coordinator resolution.</span>
                </div>
                <Link to="/tpo/calendar" className="btn btn-danger btn-sm">
                  Review &rarr;
                </Link>
              </div>
            </div>
          )}
          {hasReschedules && (
            <div className="col-md-6">
              <div className="p-3 bg-warning-subtle border border-warning-subtle rounded d-flex justify-content-between align-items-center">
                <div>
                  <h6 className="text-dark fw-bold mb-1">
                    <i className="bi bi-arrow-repeat me-1"></i> {reschedules.length} Reschedule Request(s)
                  </h6>
                  <span className="small text-secondary">Students waiting for alternative interview slot assignment.</span>
                </div>
                <Link to="/tpo/reschedules" className="btn btn-warning btn-sm text-dark fw-semibold">
                  Queue &rarr;
                </Link>
              </div>
            </div>
          )}
        </div>
      )}

      {/* METRICS ROW 1 */}
      <div className="row g-3 mb-4">
        <div className="col-md-3 col-sm-6">
          <div className="stat-card">
            <div className="stat-label">Registered Students</div>
            <div className="stat-number">{stats?.registeredStudents || 0}</div>
            <div className="small text-muted mt-1">Campus talent pool</div>
          </div>
        </div>
        <div className="col-md-3 col-sm-6">
          <div className="stat-card card-accent-teal">
            <div className="stat-label">Active Companies</div>
            <div className="stat-number">{stats?.activeCompanies || 0}</div>
            <div className="small text-muted mt-1">Recruiting partners</div>
          </div>
        </div>
        <div className="col-md-3 col-sm-6">
          <div className="stat-card card-accent-purple">
            <div className="stat-label">Active Drives</div>
            <div className="stat-number">{stats?.activeDrives || 0}</div>
            <div className="small text-muted mt-1">Ongoing hiring drives</div>
          </div>
        </div>
        <div className="col-md-3 col-sm-6">
          <div className="stat-card card-accent-teal">
            <div className="stat-label">Total Selections</div>
            <div className="stat-number text-success">{stats?.totalSelections || 0}</div>
            <div className="small text-success mt-1">Placed candidates</div>
          </div>
        </div>
      </div>

      {/* METRICS ROW 2 */}
      <div className="row g-3 mb-4">
        <div className="col-md-3 col-sm-6">
          <div className="stat-card">
            <div className="stat-label">Total Applications</div>
            <div className="stat-number">{stats?.totalApplications || 0}</div>
            <div className="small text-muted mt-1">Across all drives</div>
          </div>
        </div>
        <div className="col-md-3 col-sm-6">
          <div className="stat-card">
            <div className="stat-label">Interviews Scheduled</div>
            <div className="stat-number">{stats?.interviewsScheduled || 0}</div>
            <div className="small text-muted mt-1">Rounds lined up</div>
          </div>
        </div>
        <div className="col-md-3 col-sm-6">
          <div className="stat-card card-accent-warning">
            <div className="stat-label">Pending Drive Approvals</div>
            <div className="stat-number">{pendingDrives.length}</div>
            <div className="small text-muted mt-1">Awaiting TPO verify</div>
          </div>
        </div>
        <div className="col-md-3 col-sm-6">
          <div className="stat-card card-accent-danger">
            <div className="stat-label">Interview Conflicts</div>
            <div className="stat-number text-danger">{stats?.interviewConflicts || 0}</div>
            <div className="small text-muted mt-1">Schedule overlaps</div>
          </div>
        </div>
      </div>

      {/* TWO COLUMN WIDGETS: PENDING DRIVES & PENDING RESCHEDULES */}
      <div className="row g-4">
        {/* Pending Drive Approvals */}
        <div className="col-md-6">
          <div className="content-card h-100">
            <div className="content-card-header">
              <h5><i className="bi bi-briefcase me-2 text-primary"></i> Pending Drive Approvals</h5>
              <Link to="/tpo/drives" className="btn btn-sm btn-link text-decoration-none p-0">
                Manage All &rarr;
              </Link>
            </div>
            <div className="content-card-body p-0">
              {pendingDrives.length === 0 ? (
                <div className="p-4 text-center text-muted small">No drives currently awaiting approval.</div>
              ) : (
                <div className="list-group list-group-flush">
                  {pendingDrives.slice(0, 5).map((drive) => (
                    <div key={drive.id} className="list-group-item p-3 d-flex justify-content-between align-items-center">
                      <div>
                        <strong className="text-dark">{drive.title}</strong>
                        <div className="small text-muted">
                          {drive.companyName} &bull; {drive.salaryPackage} LPA &bull; Deadline: {drive.applicationDeadline}
                        </div>
                      </div>
                      <Link to="/tpo/drives" className="btn btn-sm btn-outline-primary py-0">
                        Review
                      </Link>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Pending Reschedule Queue */}
        <div className="col-md-6">
          <div className="content-card h-100">
            <div className="content-card-header">
              <h5><i className="bi bi-arrow-repeat me-2 text-warning"></i> Reschedule Requests Queue</h5>
              <Link to="/tpo/reschedules" className="btn btn-sm btn-link text-decoration-none p-0">
                View Queue &rarr;
              </Link>
            </div>
            <div className="content-card-body p-0">
              {reschedules.length === 0 ? (
                <div className="p-4 text-center text-muted small">No student reschedule requests pending.</div>
              ) : (
                <div className="list-group list-group-flush">
                  {reschedules.slice(0, 5).map((req) => (
                    <div key={req.id} className="list-group-item p-3 d-flex justify-content-between align-items-center">
                      <div>
                        <strong className="text-dark">{req.studentName}</strong>
                        <span className="badge bg-danger-subtle text-danger ms-2">{req.companyName}</span>
                        <div className="small text-muted mt-1 text-truncate" style={{ maxWidth: "260px" }}>
                          {req.reason}
                        </div>
                      </div>
                      <Link to="/tpo/reschedules" className="btn btn-sm btn-warning text-dark py-0 fw-semibold">
                        Resolve
                      </Link>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TpoDashboard;
