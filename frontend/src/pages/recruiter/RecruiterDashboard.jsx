import React, { useState, useEffect, useContext } from "react";
import { Link } from "react-router-dom";
import { AuthContext } from "../../context/AuthContext";
import { getRecruiterDashboard, getRecruiterDrives } from "../../services/recruiterService";
import Loader from "../../components/common/Loader";

const RecruiterDashboard = () => {
  const { user } = useContext(AuthContext);
  const [stats, setStats] = useState(null);
  const [drives, setDrives] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadData = async () => {
    try {
      setLoading(true);
      const [statsRes, drivesRes] = await Promise.all([
        getRecruiterDashboard(),
        getRecruiterDrives(),
      ]);
      if (statsRes.success && statsRes.data) {
        setStats(statsRes.data);
      }
      if (drivesRes.success && drivesRes.data) {
        setDrives(drivesRes.data);
      }
    } catch (err) {
      console.error("Error loading recruiter dashboard:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  if (loading) return <Loader message="Loading recruiter hiring operations portal..." />;

  return (
    <div>
      {/* Header */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">
            Recruitment Command Center 🏢
          </h3>
          <p className="text-muted small mb-0">
            {user?.companyName || "Corporate Partner"} &bull; Recruiter Lead: <strong>{user?.fullName}</strong>
          </p>
        </div>
        <div className="d-flex gap-2">
          <Link to="/recruiter/create-drive" className="btn btn-primary btn-sm">
            <i className="bi bi-plus-circle me-1"></i> Launch New Drive
          </Link>
          <Link to="/recruiter/recommendations" className="btn btn-outline-info text-dark btn-sm">
            <i className="bi bi-compass me-1"></i> College Insights
          </Link>
        </div>
      </div>

      {/* METRICS ROW */}
      <div className="row g-3 mb-4">
        <div className="col-md-3 col-sm-6">
          <div className="stat-card">
            <div className="stat-label">Active Drives</div>
            <div className="stat-number">{stats?.totalDrives || drives.length}</div>
            <div className="small text-muted mt-1">Campus initiatives</div>
          </div>
        </div>
        <div className="col-md-3 col-sm-6">
          <div className="stat-card card-accent-teal">
            <div className="stat-label">Total Applicants</div>
            <div className="stat-number">{stats?.totalApplicants || 0}</div>
            <div className="small text-muted mt-1">Student submissions</div>
          </div>
        </div>
        <div className="col-md-3 col-sm-6">
          <div className="stat-card card-accent-purple">
            <div className="stat-label">Interviews Scheduled</div>
            <div className="stat-number">{stats?.interviewsScheduled || 0}</div>
            <div className="small text-muted mt-1">Assessment &amp; rounds</div>
          </div>
        </div>
        <div className="col-md-3 col-sm-6">
          <div className="stat-card card-accent-warning">
            <div className="stat-label">Pending Evaluations</div>
            <div className="stat-number text-warning">{stats?.pendingEvaluations || 0}</div>
            <div className="small text-muted mt-1">Awaiting score &amp; decision</div>
          </div>
        </div>
      </div>

      {/* QUICK WORKFLOW CARDS */}
      <div className="row g-3 mb-4">
        <div className="col-md-4">
          <div className="card shadow-sm border-0 h-100 p-3 bg-white" style={{ borderRadius: "10px" }}>
            <div className="d-flex align-items-center gap-3">
              <div className="p-3 bg-primary bg-opacity-10 rounded text-primary fs-3">
                <i className="bi bi-person-lines-fill"></i>
              </div>
              <div>
                <h6 className="fw-bold mb-1">Candidate Pipeline</h6>
                <div className="small text-muted mb-2">Shortlist and advance students by round</div>
                <Link to="/recruiter/applicants" className="btn btn-sm btn-outline-primary py-0">
                  Review Applicants &rarr;
                </Link>
              </div>
            </div>
          </div>
        </div>
        <div className="col-md-4">
          <div className="card shadow-sm border-0 h-100 p-3 bg-white" style={{ borderRadius: "10px" }}>
            <div className="d-flex align-items-center gap-3">
              <div className="p-3 bg-info bg-opacity-10 rounded text-info fs-3">
                <i className="bi bi-camera-video"></i>
              </div>
              <div>
                <h6 className="fw-bold mb-1">Interview Scheduling</h6>
                <div className="small text-muted mb-2">Schedule with instant conflict prevention</div>
                <Link to="/recruiter/interviews" className="btn btn-sm btn-outline-info text-dark py-0">
                  Schedule Rounds &rarr;
                </Link>
              </div>
            </div>
          </div>
        </div>
        <div className="col-md-4">
          <div className="card shadow-sm border-0 h-100 p-3 bg-white" style={{ borderRadius: "10px" }}>
            <div className="d-flex align-items-center gap-3">
              <div className="p-3 bg-success bg-opacity-10 rounded text-success fs-3">
                <i className="bi bi-send-check"></i>
              </div>
              <div>
                <h6 className="fw-bold mb-1">Job Offers</h6>
                <div className="small text-muted mb-2">Issue formal employment letters</div>
                <Link to="/recruiter/offers" className="btn btn-sm btn-outline-success py-0">
                  Issue Offers &rarr;
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* RECRUITER DRIVES TABLE */}
      <div className="content-card">
        <div className="content-card-header">
          <h5><i className="bi bi-briefcase me-2 text-primary"></i> Company Placement Drives</h5>
          <Link to="/recruiter/create-drive" className="btn btn-sm btn-primary">
            + New Drive
          </Link>
        </div>
        <div className="content-card-body p-0">
          {drives.length === 0 ? (
            <div className="p-4 text-center text-muted small">
              No placement drives registered yet. Create your first hiring drive!
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light small">
                  <tr>
                    <th>Drive Title</th>
                    <th>Job Role</th>
                    <th>Package</th>
                    <th>Vacancies</th>
                    <th>Deadline</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody className="small">
                  {drives.map((d) => (
                    <tr key={d.id}>
                      <td className="fw-bold text-dark">{d.title}</td>
                      <td>{d.jobRole}</td>
                      <td><span className="badge bg-success-subtle text-success">{d.salaryPackage} LPA</span></td>
                      <td>{d.vacancies}</td>
                      <td>{d.applicationDeadline}</td>
                      <td>
                        <span
                          className={`badge ${
                            d.status === "ACTIVE"
                              ? "bg-success"
                              : d.status === "PENDING_APPROVAL"
                              ? "bg-warning text-dark"
                              : "bg-secondary"
                          }`}
                        >
                          {d.status}
                        </span>
                      </td>
                      <td>
                        <Link to="/recruiter/applicants" className="btn btn-outline-primary btn-sm py-0">
                          View Applicants
                        </Link>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default RecruiterDashboard;
