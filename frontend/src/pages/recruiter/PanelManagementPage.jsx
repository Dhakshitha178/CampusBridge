import React, { useState, useEffect } from "react";
import {
  getInterviewers,
  createInterviewer,
  getPanels,
  createPanel,
} from "../../services/recruiterService";
import Loader from "../../components/common/Loader";

const PanelManagementPage = () => {
  const [interviewers, setInterviewers] = useState([]);
  const [panels, setPanels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [savingInterviewer, setSavingInterviewer] = useState(false);
  const [savingPanel, setSavingPanel] = useState(false);
  const [msg, setMsg] = useState({ text: "", type: "" });

  const [interviewerForm, setInterviewerForm] = useState({
    name: "",
    email: "",
    designation: "Senior Software Engineer",
    domainExpertise: "Java, Spring Boot, Microservices",
  });

  const [panelForm, setPanelForm] = useState({
    name: "Core Technical Panel 1",
    panelName: "Core Technical Panel 1",
    description: "Evaluates algorithms, data structures, and back-end design",
  });

  const loadData = async () => {
    try {
      setLoading(true);
      const [intRes, panRes] = await Promise.all([
        getInterviewers(),
        getPanels(),
      ]);
      if (intRes.success && intRes.data) {
        setInterviewers(intRes.data);
      }
      if (panRes.success && panRes.data) {
        setPanels(panRes.data);
      }
    } catch (err) {
      console.error("Error loading panel data:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleCreateInterviewer = async (e) => {
    e.preventDefault();
    setSavingInterviewer(true);
    setMsg({ text: "", type: "" });
    try {
      const res = await createInterviewer(interviewerForm);
      if (res.success) {
        setMsg({ text: "Interviewer added to roster successfully!", type: "success" });
        setInterviewerForm({
          name: "",
          email: "",
          designation: "Senior Software Engineer",
          domainExpertise: "Java, Spring Boot, Microservices",
        });
        loadData();
      } else {
        setMsg({ text: res.message || "Failed to add interviewer.", type: "danger" });
      }
    } catch (err) {
      setMsg({
        text: err.response?.data?.message || "Failed to add interviewer.",
        type: "danger",
      });
    } finally {
      setSavingInterviewer(false);
    }
  };

  const handleCreatePanel = async (e) => {
    e.preventDefault();
    setSavingPanel(true);
    setMsg({ text: "", type: "" });
    try {
      const res = await createPanel(panelForm);
      if (res.success) {
        setMsg({ text: "Evaluation panel created successfully!", type: "success" });
        setPanelForm({ name: "", panelName: "", description: "" });
        loadData();
      } else {
        setMsg({ text: res.message || "Failed to create panel.", type: "danger" });
      }
    } catch (err) {
      setMsg({
        text: err.response?.data?.message || "Failed to create panel.",
        type: "danger",
      });
    } finally {
      setSavingPanel(false);
    }
  };

  if (loading) return <Loader message="Loading interviewers and evaluation panels..." />;

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Interview Panels &amp; Evaluator Roster</h3>
          <p className="text-muted small mb-0">
            Organize engineering panels, designate technical leads, and assign interviewers.
          </p>
        </div>
        <div className="d-flex gap-2">
          <span className="badge bg-primary px-3 py-2">
            {interviewers.length} Evaluators Active
          </span>
        </div>
      </div>

      {msg.text && (
        <div className={`alert alert-${msg.type} py-2 small d-flex align-items-center gap-2 mb-3`}>
          <i className="bi bi-info-circle-fill"></i>
          <div>{msg.text}</div>
        </div>
      )}

      <div className="row g-4">
        {/* INTERVIEWERS ROSTER */}
        <div className="col-md-6">
          <div className="content-card mb-4">
            <div className="content-card-header bg-light">
              <h5><i className="bi bi-person-plus me-2 text-primary"></i> Add Technical Evaluator</h5>
            </div>
            <div className="content-card-body">
              <form onSubmit={handleCreateInterviewer}>
                <div className="mb-2">
                  <label className="form-label small fw-semibold">Evaluator Name *</label>
                  <input
                    type="text"
                    className="form-control form-control-sm"
                    placeholder="e.g. Rajesh Kumar"
                    value={interviewerForm.name}
                    onChange={(e) =>
                      setInterviewerForm({ ...interviewerForm, name: e.target.value })
                    }
                    required
                  />
                </div>
                <div className="mb-2">
                  <label className="form-label small fw-semibold">Corporate Email *</label>
                  <input
                    type="email"
                    className="form-control form-control-sm"
                    placeholder="rajesh@zoho.com"
                    value={interviewerForm.email}
                    onChange={(e) =>
                      setInterviewerForm({ ...interviewerForm, email: e.target.value })
                    }
                    required
                  />
                </div>
                <div className="mb-2">
                  <label className="form-label small fw-semibold">Designation</label>
                  <input
                    type="text"
                    className="form-control form-control-sm"
                    value={interviewerForm.designation}
                    onChange={(e) =>
                      setInterviewerForm({ ...interviewerForm, designation: e.target.value })
                    }
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label small fw-semibold">Domain Expertise</label>
                  <input
                    type="text"
                    className="form-control form-control-sm"
                    value={interviewerForm.domainExpertise}
                    onChange={(e) =>
                      setInterviewerForm({ ...interviewerForm, domainExpertise: e.target.value })
                    }
                  />
                </div>
                <button
                  type="submit"
                  className="btn btn-primary btn-sm w-100"
                  disabled={savingInterviewer}
                >
                  {savingInterviewer ? "Adding..." : "Add Evaluator to Roster"}
                </button>
              </form>
            </div>
          </div>

          <div className="content-card">
            <div className="content-card-header">
              <h5>Evaluators List ({interviewers.length})</h5>
            </div>
            <div className="content-card-body p-0">
              {interviewers.length === 0 ? (
                <div className="p-3 text-center text-muted small">No interviewers added yet.</div>
              ) : (
                <div className="list-group list-group-flush">
                  {interviewers.map((inv) => (
                    <div key={inv.id} className="list-group-item p-3">
                      <div className="fw-bold text-dark">{inv.name}</div>
                      <div className="text-muted small">{inv.designation} &bull; {inv.email}</div>
                      <div className="badge bg-light text-dark border mt-1">
                        {inv.domainExpertise || "General Technical"}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>

        {/* INTERVIEW PANELS */}
        <div className="col-md-6">
          <div className="content-card mb-4">
            <div className="content-card-header bg-light">
              <h5><i className="bi bi-people me-2 text-primary"></i> Create Interview Panel</h5>
            </div>
            <div className="content-card-body">
              <form onSubmit={handleCreatePanel}>
                <div className="mb-2">
                  <label className="form-label small fw-semibold">Panel Title *</label>
                  <input
                    type="text"
                    className="form-control form-control-sm"
                    placeholder="e.g. Backend Engineering Panel A"
                    value={panelForm.name}
                    onChange={(e) => setPanelForm({ ...panelForm, name: e.target.value, panelName: e.target.value })}
                    required
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label small fw-semibold">Panel Purpose</label>
                  <textarea
                    rows="3"
                    className="form-control form-control-sm"
                    value={panelForm.description}
                    onChange={(e) => setPanelForm({ ...panelForm, description: e.target.value })}
                  ></textarea>
                </div>
                <button
                  type="submit"
                  className="btn btn-success btn-sm w-100"
                  disabled={savingPanel}
                >
                  {savingPanel ? "Creating..." : "Create Panel"}
                </button>
              </form>
            </div>
          </div>

          <div className="content-card">
            <div className="content-card-header">
              <h5>Active Panels ({panels.length})</h5>
            </div>
            <div className="content-card-body p-0">
              {panels.length === 0 ? (
                <div className="p-3 text-center text-muted small">No panels configured.</div>
              ) : (
                <div className="list-group list-group-flush">
                  {panels.map((p) => (
                    <div key={p.id} className="list-group-item p-3">
                      <div className="fw-bold text-dark">{p.name}</div>
                      <p className="text-secondary small mb-1">{p.description || "General evaluation panel."}</p>
                      <span className="badge bg-success-subtle text-success">
                        {p.active ? "Ready for Rounds" : "Inactive"}
                      </span>
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

export default PanelManagementPage;
