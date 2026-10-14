import React, { useContext } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";

const RoleBasedRoute = ({ children, allowedRoles }) => {
  const { user } = useContext(AuthContext);
  const location = useLocation();

  if (!user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    // Redirect to their own dashboard
    if (user.role === "STUDENT") {
      return <Navigate to="/student/dashboard" replace />;
    } else if (user.role === "TPO") {
      return <Navigate to="/tpo/dashboard" replace />;
    } else if (user.role === "RECRUITER") {
      return <Navigate to="/recruiter/dashboard" replace />;
    }
    return <Navigate to="/login" replace />;
  }

  return children;
};

export default RoleBasedRoute;
