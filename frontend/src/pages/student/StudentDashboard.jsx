import React, { useState, useEffect, useContext } from "react";
import { Link } from "react-router-dom";
import { AuthContext } from "../../context/AuthContext";
import { getStudentDashboard } from "../../services/studentService";
import Loader from "../../components/common/Loader";

const StudentDashboard = () => {
  const { user } = useContext(AuthContext);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadData = async () => {
    try {
      setLoading(true);
      const res = await getStudentDashboard();
      if (res.success && res.data) {
        setStats(res.data);
      }
    } catch (err) {
      console.error("Error loading student dashboard:", err);
      setError("Failed to load dashboard metrics.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  if (loading) return <Loader message="Loading student placement dashboard..." />;

  const hasConflicts = stats && stats.conflictsCount > 0;

  return (
    <div>
      {/* Welcome Header */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">
            Welcome back, {user?.fullName || "Student"}! 🎓
          </h3>
          <p className="text-muted small mb-0">
            {user?.collegeName} &bull; Register No: {stats?.rollNumber || "N/A"} &bull; CGPA: {stats?.cgpa || "N/A"}
          </p>
        </div>
        <div className="d-flex gap-2">
          <Link to="/student/drives" className="btn btn-primary btn-sm">
            <i className="bi bi-briefcase me-1"></i> Browse Drives
          </Link>
          <Link to="/student/calendar" className="btn btn-outline-secondary btn-sm">
            <i className="bi bi-calendar-event me-1"></i> My Calendar
          </Link>
        </div>
      </div>

      {/* ERROR ALERT */}
      {error && <div className="alert alert-danger py-2 small">{error}</div>}

      {/* CRITICAL CONFLICT ALERT BANNER */}
      {hasConflicts && (
        <div className="conflict-alert-box">
          <div className="d-flex justify-content-between align-items-center">
            <div>
              <h5>
                <i className="bi bi-exclamation-octagon-fill"></i> Interview Scheduling Conflict Detected!
              </h5>
              <div className="small text-danger">
                You have <strong>{stats.conflictsCount} interview conflict(s)</strong> scheduled at overlapping times across different companies.
                Prompt action is required to avoid missing rounds.
              </div>
            </div>
            <Link to="/student/calendar" className="btn btn-danger btn-sm fw-semibold">
              <i className="bi bi-arrow-repeat me-1"></i> Resolve &amp; Reschedule
            </Link>
          </div>
        </div>
      )}

      {/* STATS METRIC CARDS */}
      <div className="row g-3 mb-4">
        <div className="col-md-3 col-sm-6">
          <div className="stat-card">
            <div className="stat-label">Total Applications</div>
            <div className="stat-number">{stats?.totalApplications || 0}</div>
            <div className="small text-muted mt-1">Drives applied</div>
          </div>
        </div>
        <div className="col-md-3 col-sm-6">
          <div className="stat-card card-accent-teal">
            <div className="stat-label">Shortlisted</div>
            <div className="stat-number">{stats?.shortlistedApplications || 0}</div>
            <div className="small text-success mt-1">Advanced to stages</div>
          </div>
        </div>
        <div className="col-md-3 col-sm-6">
          <div className="stat-card card-accent-purple">
            <div className="stat-label">Upcoming Interviews</div>
            <div className="stat-number">{stats?.upcomingInterviews || 0}</div>
            <div className="small text-primary mt-1">Rounds scheduled</div>
          </div>
        </div>
        <div className="col-md-3 col-sm-6">
          <div className={`stat-card ${stats?.offersReceived > 0 ? "card-accent-teal" : "card-accent-warning"}`}>
            <div className="stat-label">Job Offers</div>
            <div className="stat-number text-success">{stats?.offersReceived || 0}</div>
            <div className="small text-muted mt-1">Received offers</div>
          </div>
        </div>
      </div>

      {/* SECOND ROW OF METRICS */}
      <div className="row g-3 mb-4">
        <div className="col-md-4">
          <div className="stat-card card-accent-danger">
            <div className="stat-label">Interview Conflicts</div>
            <div className="stat-number text-danger">{stats?.conflictsCount || 0}</div>
            <div className="small text-muted mt-1">Overlapping slots</div>
          </div>
        </div>
        <div className="col-md-4">
          <div className="stat-card card-accent-warning">
            <div className="stat-label">Pending Reschedules</div>
            <div className="stat-number">{stats?.pendingReschedules || 0}</div>
            <div className="small text-muted mt-1">Awaiting TPO decision</div>
          </div>
        </div>
        <div className="col-md-4">
          <div className="stat-card">
            <div className="stat-label">Unread Notifications</div>
            <div className="stat-number text-info">{stats?.unreadNotifications || 0}</div>
            <div className="small text-muted mt-1">Drive &amp; interview alerts</div>
          </div>
        </div>
      </div>

      {/* RECENT UPCOMING INTERVIEWS TABLE */}
      <div className="content-card">
        <div className="content-card-header">
          <h5><i className="bi bi-clock-history me-2 text-primary"></i> Upcoming Placement Rounds</h5>
          <Link to="/student/calendar" className="btn btn-sm btn-link text-decoration-none">
            View Unified Calendar &rarr;
          </Link>
        </div>
        <div className="content-card-body p-0">
          {!stats?.upcomingInterviewsList || stats.upcomingInterviewsList.length === 0 ? (
            <div className="p-4 text-center text-muted">
              No interviews currently scheduled. Browse active drives to apply!
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light small">
                  <tr>
                    <th>Company</th>
                    <th>Round</th>
                    <th>Date &amp; Time</th>
                    <th>Mode</th>
                    <th>Venue / Link</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody className="small">
                  {stats.upcomingInterviewsList.map((item) => (
                    <tr key={item.interviewId}>
                      <td className="fw-semibold text-dark">{item.companyName}</td>
                      <td>
                        <span className="badge bg-secondary">{item.roundName || item.roundType}</span>
                      </td>
                      <td>
                        <i className="bi bi-calendar3 me-1 text-muted"></i> {item.date}
                        <br />
                        <span className="text-muted">{item.startTime} - {item.endTime}</span>
                      </td>
                      <td>
                        <span className={`badge ${item.mode === "ONLINE" ? "bg-info text-dark" : "bg-dark"}`}>
                          {item.mode}
                        </span>
                      </td>
                      <td>
                        {item.mode === "ONLINE" && item.meetingLink ? (
                          <a
                            href={item.meetingLink}
                            target="_blank"
                            rel="noreferrer"
                            className="btn btn-outline-primary btn-sm py-0"
                          >
                            <i className="bi bi-camera-video me-1"></i> Join Call
                          </a>
                        ) : (
                          <span>{item.venue || "Campus Lab"}</span>
                        )}
                      </td>
                      <td>
                        <span className="badge bg-primary">{item.status}</span>
                      </td>
                      <td>
                        <Link
                          to="/student/calendar"
                          className="btn btn-outline-secondary btn-sm py-0"
                          title="View in Calendar or Request Reschedule"
                        >
                          Details
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

export default StudentDashboard;