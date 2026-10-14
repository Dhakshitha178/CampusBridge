import React from "react";

const STATUS_CONFIG = {
  APPLIED: { label: "Applied", bg: "bg-secondary", text: "text-white" },
  UNDER_REVIEW: { label: "Under Review", bg: "bg-info", text: "text-dark" },
  SHORTLISTED: { label: "Shortlisted", bg: "bg-primary", text: "text-white" },
  ASSESSMENT: { label: "Assessment", bg: "bg-warning", text: "text-dark" },
  INTERVIEW: { label: "Interview", bg: "bg-info", text: "text-white" },
  SELECTED: { label: "Selected", bg: "bg-success", text: "text-white" },
  REJECTED: { label: "Not Selected", bg: "bg-danger", text: "text-white" },
  NOT_SELECTED: { label: "Not Selected", bg: "bg-danger", text: "text-white" },
  WITHDRAWN: { label: "Withdrawn", bg: "bg-dark", text: "text-white" },
};

const ApplicationStatusBadge = ({ status }) => {
  const config = STATUS_CONFIG[status] || {
    label: status || "Unknown",
    bg: "bg-secondary",
    text: "text-white",
  };

  return (
    <span className={`badge ${config.bg} ${config.text} px-3 py-2 rounded-pill fw-semibold`}>
      {config.label}
    </span>
  );
};

export default ApplicationStatusBadge;
