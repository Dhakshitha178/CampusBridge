import React, { useState, useEffect } from "react";
import { getStudentsList } from "../../services/tpoService";
import Loader from "../../components/common/Loader";

const ManageStudentsPage = () => {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedStudent, setSelectedStudent] = useState(null);

  const [filters, setFilters] = useState({
    search: "",
    department: "",
    placementStatus: "",
    minCgpa: "",
    maxBacklogs: "",
  });

  const loadStudents = async () => {
    try {
      setLoading(true);
      const params = {};
      if (filters.search) params.search = filters.search;
      if (filters.department) params.department = filters.department;
      if (filters.placementStatus) params.placementStatus = filters.placementStatus;
      if (filters.minCgpa) params.minCgpa = filters.minCgpa;
      if (filters.maxBacklogs) params.maxBacklogs = filters.maxBacklogs;

      const res = await getStudentsList(params);
      if (res.success && res.data) {
        setStudents(res.data);
      }
    } catch (err) {
      console.error("Error loading students:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadStudents();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filters.department, filters.placementStatus, filters.minCgpa, filters.maxBacklogs]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    loadStudents();
  };

  const handleFilterChange = (e) => {
    setFilters({ ...filters, [e.target.name]: e.target.value });
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case "PLACED":
        return <span className="badge bg-success">PLACED</span>;
      case "OPTED_OUT":
        return <span className="badge bg-secondary">OPTED OUT</span>;
      case "UNPLACED":
      default:
        return <span className="badge bg-warning text-dark">UNPLACED</span>;
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Student Directory &amp; Talent Pool</h3>
          <p className="text-muted small mb-0">
            Search, filter, and track campus candidates across departments, academic criteria, and placement statuses.
          </p>
        </div>
        <div>
          <span className="badge bg-primary px-3 py-2">
            {students.length} Candidates Enrolled
          </span>
        </div>
      </div>

      {/* FILTER BAR */}
      <div className="card shadow-sm border-0 mb-4 p-3 bg-white">
        <form onSubmit={handleSearchSubmit} className="row g-2 align-items-center">
          <div className="col-md-3">
            <input
              type="text"
              name="search"
              className="form-control form-control-sm"
              placeholder="Search by name or roll number..."
              value={filters.search}
              onChange={handleFilterChange}
            />
          </div>
          <div className="col-md-3">
            <select
              name="department"
              className="form-select form-select-sm"
              value={filters.department}
              onChange={handleFilterChange}
            >
              <option value="">All Departments</option>
              <option value="Computer Science & Engineering">Computer Science & Engineering</option>
              <option value="Information Technology">Information Technology</option>
              <option value="Electronics & Communication">Electronics & Communication</option>
              <option value="Electrical & Electronics">Electrical & Electronics</option>
              <option value="Mechanical Engineering">Mechanical Engineering</option>
            </select>
          </div>
          <div className="col-md-2">
            <select
              name="placementStatus"
              className="form-select form-select-sm"
              value={filters.placementStatus}
              onChange={handleFilterChange}
            >
              <option value="">All Placement Statuses</option>
              <option value="UNPLACED">Unplaced</option>
              <option value="PLACED">Placed</option>
              <option value="OPTED_OUT">Opted Out</option>
            </select>
          </div>
          <div className="col-md-2">
            <input
              type="number"
              step="0.1"
              name="minCgpa"
              className="form-control form-control-sm"
              placeholder="Min CGPA (e.g. 7.5)"
              value={filters.minCgpa}
              onChange={handleFilterChange}
            />
          </div>
          <div className="col-md-2">
            <button type="submit" className="btn btn-primary btn-sm w-100">
              <i className="bi bi-search me-1"></i> Apply Filter
            </button>
          </div>
        </form>
      </div>

      {loading ? (
        <Loader message="Loading student records..." />
      ) : students.length === 0 ? (
        <div className="text-center p-5 bg-white rounded border text-muted">
          No students match the specified search filters.
        </div>
      ) : (
        <div className="content-card">
          <div className="content-card-body p-0">
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light small">
                  <tr>
                    <th>Roll Number</th>
                    <th>Candidate Name</th>
                    <th>Department</th>
                    <th>CGPA</th>
                    <th>Backlogs</th>
                    <th>Batch</th>
                    <th>Status</th>
                    <th>Skills Snapshot</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody className="small">
                  {students.map((std) => (
                    <tr key={std.id}>
                      <td><span className="badge bg-light text-dark border">{std.rollNumber}</span></td>
                      <td className="fw-semibold text-dark">{std.fullName}</td>
                      <td>{std.department}</td>
                      <td><strong>{std.cgpa?.toFixed(2)}</strong></td>
                      <td>
                        <span className={std.backlogs > 0 ? "badge bg-danger" : "badge bg-success"}>
                          {std.backlogs}
                        </span>
                      </td>
                      <td>{std.graduationYear}</td>
                      <td>{getStatusBadge(std.placementStatus)}</td>
                      <td className="text-truncate" style={{ maxWidth: "160px" }}>
                        {std.skills || "N/A"}
                      </td>
                      <td>
                        <button
                          className="btn btn-outline-primary btn-sm py-0"
                          onClick={() => setSelectedStudent(std)}
                        >
                          View Profile
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* STUDENT PROFILE POPUP MODAL */}
      {selectedStudent && (
        <div
          className="modal show d-block"
          tabIndex="-1"
          style={{ backgroundColor: "rgba(0,0,0,0.5)" }}
        >
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content shadow-lg border-0" style={{ borderRadius: "10px" }}>
              <div className="modal-header bg-primary text-white">
                <h5 className="modal-title fs-6">
                  <i className="bi bi-person-circle me-2"></i> {selectedStudent.fullName} ({selectedStudent.rollNumber})
                </h5>
                <button
                  type="button"
                  className="btn-close btn-close-white"
                  onClick={() => setSelectedStudent(null)}
                ></button>
              </div>
              <div className="modal-body p-4 small">
                <div className="row g-2 mb-3">
                  <div className="col-6">
                    <span className="text-muted d-block">Department</span>
                    <strong>{selectedStudent.department}</strong>
                  </div>
                  <div className="col-6">
                    <span className="text-muted d-block">Placement Status</span>
                    {getStatusBadge(selectedStudent.placementStatus)}
                  </div>
                  <div className="col-6">
                    <span className="text-muted d-block">CGPA</span>
                    <strong>{selectedStudent.cgpa}</strong>
                  </div>
                  <div className="col-6">
                    <span className="text-muted d-block">Standing Backlogs</span>
                    <strong>{selectedStudent.backlogs}</strong>
                  </div>
                  <div className="col-6">
                    <span className="text-muted d-block">Graduation Year</span>
                    <strong>{selectedStudent.graduationYear}</strong>
                  </div>
                  <div className="col-6">
                    <span className="text-muted d-block">Contact Phone</span>
                    <strong>{selectedStudent.phoneNumber || "Not listed"}</strong>
                  </div>
                </div>

                <div className="mb-3">
                  <span className="text-muted d-block mb-1">Technical Skills</span>
                  <div className="p-2 bg-light rounded border">
                    {selectedStudent.skills || "None listed"}
                  </div>
                </div>

                {selectedStudent.resumeUrl && (
                  <div>
                    <a
                      href={selectedStudent.resumeUrl}
                      target="_blank"
                      rel="noreferrer"
                      className="btn btn-outline-primary btn-sm w-100"
                    >
                      <i className="bi bi-file-earmark-pdf me-1"></i> Open Resume Document
                    </a>
                  </div>
                )}
              </div>
              <div className="modal-footer bg-light">
                <button
                  type="button"
                  className="btn btn-secondary btn-sm"
                  onClick={() => setSelectedStudent(null)}
                >
                  Close
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ManageStudentsPage;
