import React, { useContext } from "react";
import { Link, useNavigate } from "react-router-dom";
import { AuthContext } from "../../context/AuthContext";
import NotificationBell from "./NotificationBell";

const Navbar = () => {
  const { user, logout } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const getRoleBadge = (role) => {
    switch (role) {
      case "STUDENT":
        return <span className="badge bg-success ms-2">STUDENT</span>;
      case "TPO":
        return <span className="badge bg-primary ms-2">TPO COORDINATOR</span>;
      case "RECRUITER":
        return <span className="badge bg-warning text-dark ms-2">RECRUITER</span>;
      default:
        return null;
    }
  };

  const getHomeRoute = () => {
    if (!user) return "/login";
    switch (user.role) {
      case "STUDENT":
        return "/student/dashboard";
      case "TPO":
        return "/tpo/dashboard";
      case "RECRUITER":
        return "/recruiter/dashboard";
      default:
        return "/login";
    }
  };

  return (
    <header className="top-navbar d-flex justify-content-between align-items-center">
      <div className="d-flex align-items-center gap-3">
        <Link to={getHomeRoute()} className="brand-title">
          <i className="bi bi-mortarboard-fill text-info fs-4"></i>
          <span>CampusBridge</span>
        </Link>
        <span className="brand-badge d-none d-md-inline-block">Naan Mudhalvan Edition</span>
      </div>

      <div className="d-flex align-items-center gap-3">
        {user ? (
          <>
            <div className="text-end d-none d-sm-block">
              <div className="small fw-bold text-white d-flex align-items-center justify-content-end">
                <span>{user.fullName}</span>
                {getRoleBadge(user.role)}
              </div>
              <div className="text-light opacity-75" style={{ fontSize: "0.75rem" }}>
                {user.collegeName || user.companyName || user.email}
              </div>
            </div>

            <NotificationBell />

            <button
              onClick={handleLogout}
              className="btn btn-outline-light btn-sm d-flex align-items-center gap-1"
              title="Logout"
            >
              <i className="bi bi-box-arrow-right"></i>
              <span className="d-none d-md-inline">Logout</span>
            </button>
          </>
        ) : (
          <div className="d-flex gap-2">
            <Link to="/login" className="btn btn-sm btn-outline-light">
              Sign In
            </Link>
            <Link to="/register" className="btn btn-sm btn-info text-dark fw-bold">
              Register Student
            </Link>
          </div>
        )}
      </div>
    </header>
  );
};

export default Navbar;