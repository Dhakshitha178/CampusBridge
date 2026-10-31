import React, { useState, useEffect } from "react";
import {
  getScheduledInterviews,
  submitEvaluation,
  getInterviewEvaluation,
} from "../../services/recruiterService";
import Loader from "../../components/common/Loader";

const EvaluationPage = () => {
  const [interviews, setInterviews] = useState([]);
  const [selectedInterviewId, setSelectedInterviewId] = useState("");
  const [existingEval, setExistingEval] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [msg, setMsg] = useState({ text: "", type: "" });

  const [form, setForm] = useState({
    technicalScore: 8.5,
    communicationScore: 8.0,
    problemSolvingScore: 9.0,
    overallRating: 4.5,
    decision: "PASS",
    feedback: "Strong grasp of algorithmic complexities, clean data structure decomposition, and articulate explanation.",
  });

  const loadInterviews = async () => {
    try {
      setLoading(true);
      const res = await getScheduledInterviews();
      if (res.success && res.data && res.data.length > 0) {
        setInterviews(res.data);
        setSelectedInterviewId(res.data[0].id);
        fetchEval(res.data[0].id);
      }
    } catch (err) {
      console.error("Error loading interviews for evaluation:", err);
    } finally {
      setLoading(false);
    }
  };

  const fetchEval = async (intId) => {
    setExistingEval(null);
    try {
      const res = await getInterviewEvaluation(intId);
      if (res.success && res.data) {
        setExistingEval(res.data);
      }
    } catch (e) {
      // No evaluation yet
    }
  };

  useEffect(() => {
    loadInterviews();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleInterviewChange = (intId) => {
    setSelectedInterviewId(intId);
    fetchEval(intId);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({
      ...form,
      [name]:
        name.includes("Score") || name === "overallRating"
          ? parseFloat(value) || 0
          : value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedInterviewId) return;
    setSubmitting(true);
    setMsg({ text: "", type: "" });
    try {
      const payload = {
        interviewId: Number(selectedInterviewId),
        technicalScore: form.technicalScore,
        communicationScore: form.communicationScore,
        problemSolvingScore: form.problemSolvingScore,
        overallRating: form.overallRating,
        decision: form.decision,
        feedback: form.feedback,
      };
      const res = await submitEvaluation(payload);
      if (res.success) {
        setMsg({
          text: `Evaluation recorded with decision: ${form.decision}. Application stage updated accordingly!`,
          type: "success",
        });
        fetchEval(selectedInterviewId);
      } else {
        setMsg({ text: res.message || "Failed to record evaluation.", type: "danger" });
      }
    } catch (err) {
      setMsg({
        text: err.response?.data?.message || "Error submitting evaluation.",
        type: "danger",
      });
    } finally {
      setSubmitting(false);
    }
  };

  if (loading && interviews.length === 0) {
    return <Loader message="Loading interview evaluation rubric..." />;
  }

  const selectedInt = interviews.find((i) => i.id === Number(selectedInterviewId));

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Candidate Scoring &amp; Evaluation Rubric 📝</h3>
          <p className="text-muted small mb-0">
            Submit quantitative scores (1-10) and qualitative feedback. Automatically advances applicant status.
          </p>
        </div>
      </div>

      {msg.text && (
        <div className={`alert alert-${msg.type} py-2 small d-flex align-items-center gap-2 mb-3`}>
          <i className="bi bi-info-circle-fill"></i>
          <div>{msg.text}</div>
        </div>
      )}

      {/* SELECT INTERVIEW */}
      <div className="card shadow-sm border-0 mb-4 p-3 bg-white">
        <label className="form-label small fw-semibold">Select Candidate &amp; Scheduled Round:</label>
        <select
          className="form-select form-select-sm"
          value={selectedInterviewId}
          onChange={(e) => handleInterviewChange(e.target.value)}
        >
          {interviews.map((item) => (
            <option key={item.id} value={item.id}>
              {item.studentName || (item.student && item.student.fullName) || `Candidate #${item.id}`} ({item.studentRollNumber || (item.student && item.student.rollNumber) || "N/A"}) &bull; {item.roundName || item.stageName || item.roundType} on {item.interviewDate} ({item.startTime})
            </option>
          ))}
        </select>
      </div>

      <div className="row g-4">
        {/* EVALUATION FORM */}
        <div className="col-md-7">
          <div className="content-card">
            <div className="content-card-header bg-light">
              <h5><i className="bi bi-star-fill me-2 text-warning"></i> Structured Evaluation Rubric</h5>
            </div>
            <div className="content-card-body">
              {selectedInt && (
                <div className="p-3 bg-light rounded mb-3 small">
                  <div><strong>Candidate:</strong> {selectedInt.studentName || (selectedInt.student && selectedInt.student.fullName) || `Candidate #${selectedInt.id}`} ({selectedInt.studentRollNumber || (selectedInt.student && selectedInt.student.rollNumber) || "N/A"})</div>
                  <div><strong>Round:</strong> {selectedInt.roundName || selectedInt.stageName || selectedInt.roundType}</div>
                  <div><strong>Drive:</strong> {selectedInt.driveTitle || selectedInt.jobTitle}</div>
                </div>
              )}

              <form onSubmit={handleSubmit}>
                <div className="row g-3 mb-3">
                  <div className="col-md-6">
                    <label className="form-label small fw-semibold">Technical Score (1 to 10)</label>
                    <input
                      type="number"
                      step="0.5"
                      min="1"
                      max="10"
                      name="technicalScore"
                      className="form-control form-control-sm"
                      value={form.technicalScore}
                      onChange={handleChange}
                      required
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small fw-semibold">Communication Score (1 to 10)</label>
                    <input
                      type="number"
                      step="0.5"
                      min="1"
                      max="10"
                      name="communicationScore"
                      className="form-control form-control-sm"
                      value={form.communicationScore}
                      onChange={handleChange}
                      required
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small fw-semibold">Problem Solving (1 to 10)</label>
                    <input
                      type="number"
                      step="0.5"
                      min="1"
                      max="10"
                      name="problemSolvingScore"
                      className="form-control form-control-sm"
                      value={form.problemSolvingScore}
                      onChange={handleChange}
                      required
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label small fw-semibold">Overall Rating (1 to 5 Stars)</label>
                    <input
                      type="number"
                      step="0.1"
                      min="1"
                      max="5"
                      name="overallRating"
                      className="form-control form-control-sm"
                      value={form.overallRating}
                      onChange={handleChange}
                      required
                    />
                  </div>
                </div>

                <div className="mb-3">
                  <label className="form-label small fw-semibold">Evaluation Decision *</label>
                  <select
                    name="decision"
                    className="form-select form-select-sm"
                    value={form.decision}
                    onChange={handleChange}
                    required
                  >
                    <option value="PASS">PASS (Advance Candidate / Selected)</option>
                    <option value="ON_HOLD">ON_HOLD (Comparative Pool Review)</option>
                    <option value="FAIL">FAIL (Do Not Progress)</option>
                  </select>
                </div>

                <div className="mb-3">
                  <label className="form-label small fw-semibold">Evaluator Comments &amp; Feedback *</label>
                  <textarea
                    rows="3"
                    name="feedback"
                    className="form-control form-control-sm"
                    value={form.feedback}
                    onChange={handleChange}
                    required
                  ></textarea>
                </div>

                <button
                  type="submit"
                  className="btn btn-primary btn-sm w-100 py-2 fw-semibold"
                  disabled={submitting}
                  style={{ backgroundColor: "#0f4c81" }}
                >
                  {submitting ? "Submitting Evaluation..." : "Submit Evaluation & Update Result"}
                </button>
              </form>
            </div>
          </div>
        </div>

        {/* EXISTING EVALUATION RECORD */}
        <div className="col-md-5">
          <div className="content-card">
            <div className="content-card-header">
              <h5><i className="bi bi-clock-history me-2 text-primary"></i> Current Evaluation on Record</h5>
            </div>
            <div className="content-card-body">
              {!existingEval ? (
                <div className="text-center p-4 text-muted small">
                  No evaluation has been finalized for this interview session yet.
                </div>
              ) : (
                <div className="small">
                  <div className="d-flex justify-content-between align-items-center mb-3">
                    <span className="text-muted">Decision:</span>
                    <span
                      className={`badge fs-6 ${
                        existingEval.decision === "PASS"
                          ? "bg-success"
                          : existingEval.decision === "FAIL"
                          ? "bg-danger"
                          : "bg-warning text-dark"
                      }`}
                    >
                      {existingEval.decision}
                    </span>
                  </div>

                  <div className="d-flex justify-content-between py-1 border-bottom">
                    <span className="text-muted">Technical:</span>
                    <strong>{existingEval.technicalScore} / 10</strong>
                  </div>
                  <div className="d-flex justify-content-between py-1 border-bottom">
                    <span className="text-muted">Communication:</span>
                    <strong>{existingEval.communicationScore} / 10</strong>
                  </div>
                  <div className="d-flex justify-content-between py-1 border-bottom">
                    <span className="text-muted">Problem Solving:</span>
                    <strong>{existingEval.problemSolvingScore} / 10</strong>
                  </div>
                  <div className="d-flex justify-content-between py-1 border-bottom">
                    <span className="text-muted">Overall Rating:</span>
                    <strong>{existingEval.overallRating} ★</strong>
                  </div>

                  <div className="mt-3">
                    <span className="text-muted d-block mb-1">Evaluator Notes:</span>
                    <div className="p-2 bg-light rounded border text-secondary">
                      {existingEval.feedback}
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EvaluationPage;
