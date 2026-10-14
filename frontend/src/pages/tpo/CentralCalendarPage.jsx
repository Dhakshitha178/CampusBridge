import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { getCentralCalendar } from "../../services/tpoService";
import Loader from "../../components/common/Loader";

const CentralCalendarPage = () => {
  const [calendarItems, setCalendarItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterConflictOnly, setFilterConflictOnly] = useState(false);
  const [selectedCompany, setSelectedCompany] = useState("ALL");

  const loadData = async () => {
    try {
      setLoading(true);
      const res = await getCentralCalendar();
      if (res.success && res.data) {
        setCalendarItems(res.data);
      }
    } catch (err) {
      console.error("Error loading central calendar:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  if (loading) return <Loader message="Compiling central placement interview calendar..." />;

  const companiesList = Array.from(new Set(calendarItems.map((c) => c.companyName)));
  const totalConflicts = calendarItems.filter((i) => i.hasConflict).length;

  const filteredItems = calendarItems.filter((item) => {
    const matchesConflict = !filterConflictOnly || item.hasConflict;
    const matchesCompany = selectedCompany === "ALL" || item.companyName === selectedCompany;
    return matchesConflict && matchesCompany;
  });

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Central Cross-Company Interview Calendar 🗓️</h3>
          <p className="text-muted small mb-0">
            Master campus scheduling view with automatic half-open interval conflict detection.
          </p>
        </div>
        <div className="d-flex gap-2">
          {totalConflicts > 0 && (
            <Link to="/tpo/reschedules" className="btn btn-danger btn-sm">
              <i className="bi bi-exclamation-octagon-fill me-1"></i> Resolve {totalConflicts} Conflict(s)
            </Link>
          )}
          <button className="btn btn-outline-secondary btn-sm" onClick={loadData}>
            <i className="bi bi-arrow-clockwise"></i> Refresh
          </button>
        </div>
      </div>

      {/* FILTER BAR */}
      <div className="card shadow-sm border-0 mb-4 p-3 bg-white">
        <div className="row g-2 align-items-center">
          <div className="col-md-4">
            <select
              className="form-select form-select-sm"
              value={selectedCompany}
              onChange={(e) => setSelectedCompany(e.target.value)}
            >
              <option value="ALL">All Recruiting Companies</option>
              {companiesList.map((comp, idx) => (
                <option key={idx} value={comp}>{comp}</option>
              ))}
            </select>
          </div>
          <div className="col-md-5">
            <div className="form-check form-switch pt-1">
              <input
                className="form-check-input"
                type="checkbox"
                id="conflictSwitch"
                checked={filterConflictOnly}
                onChange={(e) => setFilterConflictOnly(e.target.checked)}
              />
              <label className="form-check-label small fw-semibold" htmlFor="conflictSwitch">
                Highlight Overlapping Conflicts Only ({totalConflicts} identified)
              </label>
            </div>
          </div>
          <div className="col-md-3 text-end small text-muted">
            Showing <strong>{filteredItems.length}</strong> scheduled sessions
          </div>
        </div>
      </div>

      {filteredItems.length === 0 ? (
        <div className="text-center p-5 bg-white rounded border text-muted">
          No scheduled interviews matching current filters.
        </div>
      ) : (
        <div className="content-card">
          <div className="content-card-body p-0">
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light small">
                  <tr>
                    <th>Candidate</th>
                    <th>Roll No</th>
                    <th>Company</th>
                    <th>Round Type</th>
                    <th>Date</th>
                    <th>Slot Window</th>
                    <th>Venue / Link</th>
                    <th>Conflict Status</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody className="small">
                  {filteredItems.map((item) => (
                    <tr
                      key={item.id}
                      className={item.hasConflict ? "table-danger bg-opacity-25" : ""}
                    >
                      <td className="fw-semibold text-dark">{item.studentName}</td>
                      <td><span className="badge bg-light text-dark border">{item.studentRollNumber}</span></td>
                      <td className="fw-bold">{item.companyName}</td>
                      <td>{item.roundName || item.roundType}</td>
                      <td>{item.interviewDate}</td>
                      <td>
                        <strong className={item.hasConflict ? "text-danger" : "text-dark"}>
                          {item.startTime} - {item.endTime}
                        </strong>
                      </td>
                      <td>
                        {item.mode === "ONLINE" ? (
                          <span className="badge bg-info text-dark">ONLINE MEET</span>
                        ) : (
                          <span>{item.venue || "Lab 2"}</span>
                        )}
                      </td>
                      <td>
                        {item.hasConflict ? (
                          <span className="badge bg-danger">
                            <i className="bi bi-exclamation-triangle-fill me-1"></i> OVERLAP CONFLICT
                          </span>
                        ) : (
                          <span className="badge bg-success-subtle text-success border">NO CONFLICT</span>
                        )}
                      </td>
                      <td>
                        {item.hasConflict ? (
                          <Link
                            to="/tpo/reschedules"
                            className="btn btn-danger btn-sm py-0"
                            title="Reschedule this conflict"
                          >
                            Reschedule
                          </Link>
                        ) : (
                          <span className="text-muted">&mdash;</span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CentralCalendarPage;
