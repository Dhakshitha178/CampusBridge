import React, { useContext } from "react";
import { NavLink } from "react-router-dom";
import { AuthContext } from "../../context/AuthContext";

const Sidebar = () => {
  const { user } = useContext(AuthContext);

  if (!user) return null;

  const renderStudentLinks = () => (
    <>
      <div className="sidebar-heading">Student Portal</div>
      <NavLink
        to="/student/dashboard"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-speedometer2"></i> Dashboard
      </NavLink>
      <NavLink
        to="/student/drives"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-briefcase"></i> Browse Drives
      </NavLink>
      <NavLink
        to="/student/applications"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-layers"></i> Applications & Tracker
      </NavLink>
      <NavLink
        to="/student/calendar"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-calendar-event"></i> Unified Calendar
      </NavLink>
      <NavLink
        to="/student/offers"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-award"></i> My Offers
      </NavLink>
      <NavLink
        to="/student/history"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-bar-chart"></i> Placement History
      </NavLink>
      <NavLink
        to="/student/profile"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-person-circle"></i> My Profile
      </NavLink>
    </>
  );

  const renderTpoLinks = () => (
    <>
      <div className="sidebar-heading">Placement Cell (TPO)</div>
      <NavLink
        to="/tpo/dashboard"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-speedometer2"></i> TPO Dashboard
      </NavLink>
      <NavLink
        to="/tpo/live-monitor"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-activity"></i> Live Drive Monitor
      </NavLink>
      <NavLink
        to="/tpo/drives"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-check2-circle"></i> Drive Approvals
      </NavLink>
      <NavLink
        to="/tpo/calendar"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-calendar-week"></i> Central Calendar
      </NavLink>
      <NavLink
        to="/tpo/reschedules"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-arrow-repeat"></i> Reschedule Requests
      </NavLink>
      <NavLink
        to="/tpo/students"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-people"></i> Student Directory
      </NavLink>
      <NavLink
        to="/tpo/companies"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-building"></i> Company Registry
      </NavLink>
      <NavLink
        to="/tpo/analytics"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-pie-chart"></i> Placement Analytics
      </NavLink>
      <NavLink
        to="/tpo/reports"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-file-earmark-pdf"></i> Reports & Export
      </NavLink>
      <NavLink
        to="/tpo/announcements"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-megaphone"></i> Announcements
      </NavLink>
    </>
  );

  const renderRecruiterLinks = () => (
    <>
      <div className="sidebar-heading">Recruiter Portal</div>
      <NavLink
        to="/recruiter/dashboard"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-speedometer2"></i> Dashboard
      </NavLink>
      <NavLink
        to="/recruiter/create-drive"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-plus-circle"></i> Create Drive
      </NavLink>
      <NavLink
        to="/recruiter/applicants"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-person-lines-fill"></i> Applicants
      </NavLink>
      <NavLink
        to="/recruiter/interviews"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-camera-video"></i> Interview Scheduling
      </NavLink>
      <NavLink
        to="/recruiter/panels"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-person-badge"></i> Panels & Interviewers
      </NavLink>
      <NavLink
        to="/recruiter/evaluations"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-star"></i> Candidate Evaluations
      </NavLink>
      <NavLink
        to="/recruiter/offers"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-send-check"></i> Offer Management
      </NavLink>
      <NavLink
        to="/recruiter/recommendations"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-compass"></i> College Recommendations
      </NavLink>
      <NavLink
        to="/recruiter/profile"
        className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
      >
        <i className="bi bi-building"></i> Company Profile
      </NavLink>
    </>
  );

  return (
    <aside className="portal-sidebar">
      {user.role === "STUDENT" && renderStudentLinks()}
      {user.role === "TPO" && renderTpoLinks()}
      {user.role === "RECRUITER" && renderRecruiterLinks()}
    </aside>
  );
};

export default Sidebar;
