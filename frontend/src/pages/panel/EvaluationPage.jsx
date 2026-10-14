import React, { useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import EvaluationForm from "../../components/panel/EvaluationForm";
import { recordEvaluation } from "../../services/interviewService";

const EvaluationPage = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const interviewId = searchParams.get("interviewId") || "1";
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (formData) => {
    try {
      setSubmitting(true);
      setError("");
      await recordEvaluation(interviewId, formData);
      setMessage("Evaluation saved successfully!");
      setTimeout(() => {
        navigate("/recruiter/evaluations");
      }, 1500);
    } catch (err) {
      setError(err?.response?.data?.message || "Failed to submit evaluation.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="container-fluid p-4">
      <div className="mb-4">
        <h3 className="fw-bold text-dark mb-1">Interview Assessment & Scoring</h3>
        <p className="text-muted small mb-0">Interview ID #{interviewId}</p>
      </div>

      {message && <div className="alert alert-success">{message}</div>}
      {error && <div className="alert alert-danger">{error}</div>}

      <div className="row justify-content-center">
        <div className="col-lg-8">
          <EvaluationForm onSubmit={handleSubmit} submitting={submitting} />
        </div>
      </div>
    </div>
  );
};

export default EvaluationPage;
