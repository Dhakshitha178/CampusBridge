import React, { useState } from "react";

const STAGE_TYPES = [
  "APTITUDE",
  "CODING",
  "TECHNICAL_INTERVIEW",
  "MANAGERIAL_INTERVIEW",
  "HR_INTERVIEW",
  "GROUP_DISCUSSION",
  "FINAL_RESULT",
];

const InterviewRoundManager = ({ stages = [], onChange }) => {
  const [newStage, setNewStage] = useState({
    stageName: "",
    stageType: "TECHNICAL_INTERVIEW",
    description: "",
    cutOffScore: "",
    isFinalStage: false,
  });

  const handleAdd = () => {
    if (!newStage.stageName.trim()) return;
    const updated = [
      ...stages,
      {
        ...newStage,
        stageOrder: stages.length + 1,
        cutOffScore: newStage.cutOffScore ? parseFloat(newStage.cutOffScore) : null,
      },
    ];
    onChange(updated);
    setNewStage({
      stageName: "",
      stageType: "TECHNICAL_INTERVIEW",
      description: "",
      cutOffScore: "",
      isFinalStage: false,
    });
  };

  const handleRemove = (index) => {
    const updated = stages
      .filter((_, i) => i !== index)
      .map((s, idx) => ({ ...s, stageOrder: idx + 1 }));
    onChange(updated);
  };

  return (
    <div className="card border-0 bg-light rounded-3 p-3 mb-3">
      <h6 className="fw-bold mb-3 text-dark">Recruitment Rounds & Stages</h6>

      {stages.length > 0 && (
        <ul className="list-group mb-3">
          {stages.map((stage, idx) => (
            <li
              key={idx}
              className="list-group-item d-flex justify-content-between align-items-center bg-white border rounded-2 mb-1"
            >
              <div>
                <span className="badge bg-secondary me-2">Round {idx + 1}</span>
                <strong>{stage.stageName}</strong>
                <span className="text-muted small ms-2">({stage.stageType})</span>
                {stage.cutOffScore && (
                  <span className="badge bg-light text-dark border ms-2">
                    Cut-off: {stage.cutOffScore}
                  </span>
                )}
                {stage.isFinalStage && (
                  <span className="badge bg-success ms-2">Final Stage</span>
                )}
              </div>
              <button
                type="button"
                className="btn btn-outline-danger btn-sm"
                onClick={() => handleRemove(idx)}
              >
                <i className="bi bi-trash"></i>
              </button>
            </li>
          ))}
        </ul>
      )}

      <div className="row g-2 align-items-end">
        <div className="col-md-4">
          <label className="form-label small fw-semibold">Round Name</label>
          <input
            type="text"
            className="form-control form-control-sm"
            placeholder="e.g. Technical Round 1"
            value={newStage.stageName}
            onChange={(e) => setNewStage({ ...newStage, stageName: e.target.value })}
          />
        </div>
        <div className="col-md-3">
          <label className="form-label small fw-semibold">Type</label>
          <select
            className="form-select form-select-sm"
            value={newStage.stageType}
            onChange={(e) => setNewStage({ ...newStage, stageType: e.target.value })}
          >
            {STAGE_TYPES.map((type) => (
              <option key={type} value={type}>
                {type.replace("_", " ")}
              </option>
            ))}
          </select>
        </div>
        <div className="col-md-2">
          <label className="form-label small fw-semibold">Cut-off</label>
          <input
            type="number"
            className="form-control form-control-sm"
            placeholder="e.g. 70"
            value={newStage.cutOffScore}
            onChange={(e) => setNewStage({ ...newStage, cutOffScore: e.target.value })}
          />
        </div>
        <div className="col-md-2">
          <div className="form-check pt-3">
            <input
              type="checkbox"
              className="form-check-input"
              id="isFinalStage"
              checked={newStage.isFinalStage}
              onChange={(e) =>
                setNewStage({ ...newStage, isFinalStage: e.target.checked })
              }
            />
            <label className="form-check-label small" htmlFor="isFinalStage">
              Final Round
            </label>
          </div>
        </div>
        <div className="col-md-1">
          <button
            type="button"
            className="btn btn-primary btn-sm w-100"
            onClick={handleAdd}
          >
            Add
          </button>
        </div>
      </div>
    </div>
  );
};

export default InterviewRoundManager;
