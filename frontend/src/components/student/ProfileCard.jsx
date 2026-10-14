import React from "react";

const ProfileCard = ({ profile }) => {
  if (!profile) return null;

  return (
    <div className="card shadow-sm border-0 rounded-3 mb-4">
      <div className="card-body p-4">
        <div className="d-flex align-items-center">
          <div
            className="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center fw-bold fs-3 me-3"
            style={{ width: "64px", height: "64px" }}
          >
            {profile.fullName ? profile.fullName.charAt(0).toUpperCase() : "S"}
          </div>
          <div>
            <h5 className="fw-bold mb-1">{profile.fullName}</h5>
            <p className="text-muted small mb-1">
              {profile.department} | Roll No: {profile.rollNumber}
            </p>
            <div className="d-flex gap-2">
              <span className="badge bg-light text-dark border">CGPA: {profile.cgpa}</span>
              <span className="badge bg-light text-dark border">Backlogs: {profile.backlogs ?? 0}</span>
              <span className={`badge ${profile.placementStatus === "PLACED" ? "bg-success" : "bg-warning text-dark"}`}>
                {profile.placementStatus || "UNPLACED"}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfileCard;
