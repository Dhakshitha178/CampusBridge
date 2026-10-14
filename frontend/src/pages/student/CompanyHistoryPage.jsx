import React, { useState, useEffect } from "react";
import {
  getCompanyPlacementHistory,
  getInterCollegePlacementHistory,
} from "../../services/studentService";
import Loader from "../../components/common/Loader";

const CompanyHistoryPage = () => {
  const [history, setHistory] = useState([]);
  const [interCollegeHistory, setInterCollegeHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState("college"); // 'college' or 'intercollege'

  useEffect(() => {
    const loadHistory = async () => {
      try {
        setLoading(true);
        const [histRes, interRes] = await Promise.all([
          getCompanyPlacementHistory(),
          getInterCollegePlacementHistory(),
        ]);
        if (histRes.success && histRes.data) {
          setHistory(histRes.data);
        }
        if (interRes.success && interRes.data) {
          setInterCollegeHistory(interRes.data);
        }
      } catch (err) {
        console.error("Error loading history:", err);
      } finally {
        setLoading(false);
      }
    };
    loadHistory();
  }, []);

  if (loading) return <Loader message="Loading historical placement analytics..." />;

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Company Placement History</h3>
          <p className="text-muted small mb-0">
            Historical campus hiring trends, package statistics, and cross-institutional benchmarks.
          </p>
        </div>
        <div className="btn-group btn-group-sm">
          <button
            className={`btn ${activeTab === "college" ? "btn-primary" : "btn-outline-primary"}`}
            onClick={() => setActiveTab("college")}
          >
            <i className="bi bi-building me-1"></i> Our College Records
          </button>
          <button
            className={`btn ${activeTab === "intercollege" ? "btn-primary" : "btn-outline-primary"}`}
            onClick={() => setActiveTab("intercollege")}
          >
            <i className="bi bi-diagram-3 me-1"></i> Inter-College Benchmarks
          </button>
        </div>
      </div>

      {activeTab === "college" ? (
        <div className="content-card">
          <div className="content-card-header">
            <h5><i className="bi bi-clock-history me-2 text-primary"></i> Historical Hiring Statistics at Your Campus</h5>
          </div>
          <div className="content-card-body p-0">
            {history.length === 0 ? (
              <div className="p-4 text-center text-muted">No historical data recorded for this campus.</div>
            ) : (
              <div className="table-responsive">
                <table className="table table-hover align-middle mb-0">
                  <thead className="table-light small">
                    <tr>
                      <th>Academic Year</th>
                      <th>Company</th>
                      <th>Roles Offered</th>
                      <th>Applicants</th>
                      <th>Selected</th>
                      <th>Selection Rate</th>
                      <th>Average CTC</th>
                      <th>Highest CTC</th>
                    </tr>
                  </thead>
                  <tbody className="small">
                    {history.map((item, idx) => {
                      const rate =
                        item.totalApplicants > 0
                          ? ((item.selectedCandidates / item.totalApplicants) * 100).toFixed(1)
                          : 0;
                      return (
                        <tr key={idx}>
                          <td><span className="badge bg-light text-dark border">{item.academicYear}</span></td>
                          <td className="fw-bold text-dark">{item.companyName}</td>
                          <td>{item.rolesOffered || "Software Engineer"}</td>
                          <td>{item.totalApplicants}</td>
                          <td><strong className="text-success">{item.selectedCandidates}</strong></td>
                          <td>
                            <div className="d-flex align-items-center gap-2">
                              <span className="fw-semibold">{rate}%</span>
                              <div className="progress flex-grow-1" style={{ height: "4px", minWidth: "50px" }}>
                                <div className="progress-bar bg-success" style={{ width: `${rate}%` }}></div>
                              </div>
                            </div>
                          </td>
                          <td>{item.averagePackage ? `${item.averagePackage} LPA` : "N/A"}</td>
                          <td><span className="badge bg-success-subtle text-success">{item.highestPackage} LPA</span></td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      ) : (
        <div className="content-card">
          <div className="content-card-header">
            <h5><i className="bi bi-shield-lock me-2 text-primary"></i> Aggregated Inter-College Placement Statistics</h5>
          </div>
          <div className="content-card-body p-0">
            <div className="p-3 bg-light border-bottom small text-muted">
              <i className="bi bi-info-circle me-1"></i> Aggregated statistics across participating Tamil Nadu institutions under the Naan Mudhalvan ecosystem. Individual student private details are strictly redacted.
            </div>
            {interCollegeHistory.length === 0 ? (
              <div className="p-4 text-center text-muted">No cross-college records available.</div>
            ) : (
              <div className="table-responsive">
                <table className="table table-hover align-middle mb-0">
                  <thead className="table-light small">
                    <tr>
                      <th>Participating Institution</th>
                      <th>Company</th>
                      <th>Hiring Year</th>
                      <th>Total Selections</th>
                      <th>Avg CTC (LPA)</th>
                      <th>Top Role</th>
                    </tr>
                  </thead>
                  <tbody className="small">
                    {interCollegeHistory.map((item, idx) => (
                      <tr key={idx}>
                        <td className="fw-semibold text-dark">{item.collegeName}</td>
                        <td>{item.companyName}</td>
                        <td><span className="badge bg-light text-dark border">{item.academicYear}</span></td>
                        <td><strong className="text-primary">{item.selectedCandidates}</strong></td>
                        <td>{item.averagePackage ? `${item.averagePackage} LPA` : "N/A"}</td>
                        <td>{item.rolesOffered || "Graduate Engineer"}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default CompanyHistoryPage;
