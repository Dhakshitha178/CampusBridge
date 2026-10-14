import React, { useState, useEffect } from "react";
import {
  getWeeklyReports,
  generateWeeklyReport,
  downloadReportPdf,
  exportPlacementExcel,
} from "../../services/tpoService";
import Loader from "../../components/common/Loader";

const WeeklyReportsPage = () => {
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [downloadingId, setDownloadingId] = useState(null);
  const [exportingExcel, setExportingExcel] = useState(false);
  const [msg, setMsg] = useState({ text: "", type: "" });

  const loadReports = async () => {
    try {
      setLoading(true);
      const res = await getWeeklyReports();
      if (res.success && res.data) {
        setReports(res.data);
      }
    } catch (err) {
      console.error("Error loading reports:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadReports();
  }, []);

  const handleGenerate = async () => {
    setGenerating(true);
    setMsg({ text: "", type: "" });
    try {
      const res = await generateWeeklyReport();
      if (res.success) {
        setMsg({ text: "Weekly placement snapshot generated successfully!", type: "success" });
        loadReports();
      } else {
        setMsg({ text: res.message || "Failed to generate report.", type: "danger" });
      }
    } catch (err) {
      setMsg({
        text: err.response?.data?.message || "Failed to generate weekly report snapshot.",
        type: "danger",
      });
    } finally {
      setGenerating(false);
    }
  };

  const handleDownloadPdf = async (reportId) => {
    setDownloadingId(reportId);
    try {
      await downloadReportPdf(reportId);
    } catch (err) {
      alert("Failed to download PDF report.");
    } finally {
      setDownloadingId(null);
    }
  };

  const handleExportExcel = async () => {
    setExportingExcel(true);
    try {
      await exportPlacementExcel();
    } catch (err) {
      alert("Failed to export Excel report.");
    } finally {
      setExportingExcel(false);
    }
  };

  if (loading) return <Loader message="Loading weekly placement report archive..." />;

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Weekly Reports &amp; Data Exports 📑</h3>
          <p className="text-muted small mb-0">
            Automated PDF placement summaries (OpenPDF) and master candidate spreadsheet exports (Apache POI).
          </p>
        </div>
        <div className="d-flex gap-2">
          <button
            className="btn btn-primary btn-sm"
            onClick={handleGenerate}
            disabled={generating}
          >
            <i className="bi bi-file-earmark-plus me-1"></i>{" "}
            {generating ? "Generating..." : "Generate Weekly Snapshot"}
          </button>
          <button
            className="btn btn-success btn-sm"
            onClick={handleExportExcel}
            disabled={exportingExcel}
          >
            <i className="bi bi-file-earmark-excel me-1"></i>{" "}
            {exportingExcel ? "Exporting..." : "Export All to Excel"}
          </button>
        </div>
      </div>

      {msg.text && (
        <div className={`alert alert-${msg.type} py-2 small d-flex align-items-center gap-2 mb-3`}>
          <i className="bi bi-info-circle-fill"></i>
          <div>{msg.text}</div>
        </div>
      )}

      {reports.length === 0 ? (
        <div className="text-center p-5 bg-white rounded border text-muted">
          <i className="bi bi-file-earmark-text fs-1 text-secondary mb-2 d-block"></i>
          No weekly placement reports generated yet. Click "Generate Weekly Snapshot" above.
        </div>
      ) : (
        <div className="row g-4">
          {reports.map((rep) => (
            <div key={rep.id} className="col-md-6">
              <div className="card h-100 shadow-sm border-0" style={{ borderRadius: "10px" }}>
                <div className="card-body p-4 d-flex flex-column justify-content-between">
                  <div>
                    <div className="d-flex justify-content-between align-items-start mb-2">
                      <span className="badge bg-primary-subtle text-primary border">
                        WEEK #{rep.weekNumber || 1} REPORT
                      </span>
                      <span className="badge bg-light text-dark border">
                        {rep.reportDate || "Recent"}
                      </span>
                    </div>

                    <h4 className="fw-bold text-dark mb-1">
                      Placement Summary Snapshot #{rep.id}
                    </h4>
                    <p className="text-muted small mb-3">
                      Institutional weekly audit &amp; drive progress log.
                    </p>

                    <div className="p-3 bg-light rounded small mb-3">
                      <div className="row g-2">
                        <div className="col-6">
                          <span className="text-muted d-block">Total Drives:</span>
                          <strong>{rep.totalDrives || 0}</strong>
                        </div>
                        <div className="col-6">
                          <span className="text-muted d-block">Total Applications:</span>
                          <strong>{rep.totalApplications || 0}</strong>
                        </div>
                        <div className="col-6">
                          <span className="text-muted d-block">Interviews Scheduled:</span>
                          <strong>{rep.interviewsScheduled || 0}</strong>
                        </div>
                        <div className="col-6">
                          <span className="text-muted d-block">Conflicts Resolved:</span>
                          <strong className="text-danger">{rep.conflictsDetected || 0}</strong>
                        </div>
                        <div className="col-6">
                          <span className="text-muted d-block">Selections Made:</span>
                          <strong className="text-success">{rep.selectionsMade || 0}</strong>
                        </div>
                        <div className="col-6">
                          <span className="text-muted d-block">Offers Extended:</span>
                          <strong className="text-success">{rep.offersExtended || 0}</strong>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div className="pt-3 border-top">
                    <button
                      className="btn btn-outline-danger btn-sm w-100 d-flex align-items-center justify-content-center gap-2"
                      disabled={downloadingId === rep.id}
                      onClick={() => handleDownloadPdf(rep.id)}
                    >
                      <i className="bi bi-file-earmark-pdf-fill"></i>
                      <span>
                        {downloadingId === rep.id
                          ? "Generating PDF Download..."
                          : "Download Official PDF Report"}
                      </span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default WeeklyReportsPage;
