import React, { useState } from "react";

const EvaluationForm = ({ onSubmit, submitting = false }) => {
  const [form, setForm] = useState({
    technicalScore: 7,
    communicationScore: 8,
    problemSolvingScore: 7,
    overallRating: 7.5,
    decision: "PASS",
    comments: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]:
        name === "decision" || name === "comments" ? value : parseFloat(value) || 0,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (onSubmit) onSubmit(form);
  };

  return (
    <form onSubmit={handleSubmit} className="card shadow-sm border-0 p-4 rounded-3">
      <h5 className="fw-bold mb-3 text-dark">Candidate Evaluation Form</h5>

      <div className="row g-3 mb-3">
        <div className="col-md-4">
          <label className="form-label small fw-semibold">Technical Score (1-10)</label>
          <input
            type="number"
            min="1"
            max="10"
            step="0.5"
            name="technicalScore"
            value={form.technicalScore}
            onChange={handleChange}
            className="form-control"
            required
          />
        </div>

        <div className="col-md-4">
          <label className="form-label small fw-semibold">Communication (1-10)</label>
          <input
            type="number"
            min="1"
            max="10"
            step="0.5"
            name="communicationScore"
            value={form.communicationScore}
            onChange={handleChange}
            className="form-control"
            required
          />
        </div>

        <div className="col-md-4">
          <label className="form-label small fw-semibold">Problem Solving (1-10)</label>
          <input
            type="number"
            min="1"
            max="10"
            step="0.5"
            name="problemSolvingScore"
            value={form.problemSolvingScore}
            onChange={handleChange}
            className="form-control"
            required
          />
        </div>
      </div>

      <div className="row g-3 mb-3">
        <div className="col-md-6">
          <label className="form-label small fw-semibold">Overall Rating (1-10)</label>
          <input
            type="number"
            min="1"
            max="10"
            step="0.1"
            name="overallRating"
            value={form.overallRating}
            onChange={handleChange}
            className="form-control"
            required
          />
        </div>

        <div className="col-md-6">
          <label className="form-label small fw-semibold">Decision</label>
          <select
            name="decision"
            value={form.decision}
            onChange={handleChange}
            className="form-select"
          >
            <option value="PASS">Pass / Recommend</option>
            <option value="HOLD">Hold</option>
            <option value="FAIL">Reject / Do Not Recommend</option>
          </select>
        </div>
      </div>

      <div className="mb-3">
        <label className="form-label small fw-semibold">Interviewer Feedback / Notes</label>
        <textarea
          name="comments"
          value={form.comments}
          onChange={handleChange}
          rows={4}
          className="form-control"
          placeholder="Strengths, weaknesses, behavioral observations..."
        ></textarea>
      </div>

      <button type="submit" disabled={submitting} className="btn btn-primary">
        {submitting ? "Submitting..." : "Submit Evaluation"}
      </button>
    </form>
  );
};

export default EvaluationForm;
