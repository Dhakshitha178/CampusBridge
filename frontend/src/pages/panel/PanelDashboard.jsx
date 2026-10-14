import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import Loader from "../../components/common/Loader";
import { getCentralCalendar } from "../../services/interviewService";

const PanelDashboard = () => {
  const [interviews, setInterviews] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchInterviews = async () => {
      try {
        setLoading(true);
        const res = await getCentralCalendar();
        if (res.success && res.data) {
          setInterviews(res.data);
        }
      } catch (err) {
        console.error("Error loading panel calendar:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchInterviews();
  }, []);

  if (loading) return <Loader text="Loading Panel Dashboard..." />;

  return (
    <div className="container-fluid p-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Interview Panel Workspace 🎙️</h3>
          <p className="text-muted small mb-0">
            View assigned candidate interviews, access profiles, and record real-time evaluations.
          </p>
        </div>
      </div>

      <div className="card shadow-sm border-0 rounded-3">
        <div className="card-header bg-white py-3 border-0">
          <h5 className="fw-bold mb-0">Today's Scheduled Interviews</h5>
        </div>
        <div className="card-body p-0">
          {interviews.length === 0 ? (
            <div className="text-center py-5 text-muted">
              <i className="bi bi-calendar-check fs-1 d-block mb-2"></i>
              No upcoming candidate interviews assigned.
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                  <tr>
                    <th>Candidate</th>
                    <th>Roll Number</th>
                    <th>Drive / Role</th>
                    <th>Stage</th>
                    <th>Time Slot</th>
                    <th>Mode</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {interviews.map((item) => (
                    <tr key={item.interviewId}>
                      <td className="fw-semibold text-dark">{item.studentName}</td>
                      <td>{item.studentRollNumber}</td>
                      <td>
                        <span className="badge bg-light text-dark border">
                          {item.companyName}
                        </span>
                      </td>
                      <td>{item.stageName}</td>
                      <td>
                        {item.interviewDate} ({item.startTime} - {item.endTime})
                      </td>
                      <td>
                        <span className="badge bg-secondary-subtle text-secondary">
                          {item.mode}
                        </span>
                      </td>
                      <td>
                        <Link
                          to={`/recruiter/evaluations?interviewId=${item.interviewId}`}
                          className="btn btn-primary btn-sm"
                        >
                          Evaluate
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

export default PanelDashboard;
