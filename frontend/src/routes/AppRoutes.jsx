import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import RoleBasedRoute from "./RoleBasedRoute";

// Auth Pages
import LoginPage from "../pages/auth/LoginPage";
import RegisterPage from "../pages/auth/RegisterPage";
import RecruiterRegisterPage from "../pages/auth/RecruiterRegisterPage";
import TpoRegisterPage from "../pages/auth/TpoRegisterPage";
import ForgotPasswordPage from "../pages/auth/ForgotPasswordPage";

// Student Pages
import StudentDashboard from "../pages/student/StudentDashboard";
import StudentProfilePage from "../pages/student/StudentProfilePage";
import BrowsedrivesPage from "../pages/student/BrowsedrivesPage";
import DriveDetailsPage from "../pages/student/DriveDetailsPage";
import MyApplicationsPage from "../pages/student/MyApplicationsPage";
import InterviewCalendarPage from "../pages/student/InterviewCalendarPage";
import OfferPage from "../pages/student/OfferPage";
import CompanyHistoryPage from "../pages/student/CompanyHistoryPage";

// TPO Pages
import TpoDashboard from "../pages/tpo/TpoDashboard";
import LiveDriveMonitorPage from "../pages/tpo/LiveDriveMonitorPage";
import ManageDrivesPage from "../pages/tpo/ManageDrivesPage";
import CentralCalendarPage from "../pages/tpo/CentralCalendarPage";
import RescheduleManagementPage from "../pages/tpo/RescheduleManagementPage";
import ManageStudentsPage from "../pages/tpo/ManageStudentsPage";
import ManageCompaniesPage from "../pages/tpo/ManageCompaniesPage";
import AnalyticsPages from "../pages/tpo/AnalyticsPages";
import WeeklyReportsPage from "../pages/tpo/WeeklyReportsPage";
import AnnouncementsPage from "../pages/tpo/AnnouncementsPage";

// Recruiter Pages
import RecruiterDashboard from "../pages/recruiter/RecruiterDashboard";
import CreateDrivePage from "../pages/recruiter/CreateDrivePage";
import ApplicatsPages from "../pages/recruiter/ApplicatsPages";
import InterviewManagementPage from "../pages/recruiter/interviewManagementPage";
import PanelManagementPage from "../pages/recruiter/PanelManagementPage";
import EvaluationPage from "../pages/recruiter/EvaluationPage";
import OffersManagementPage from "../pages/recruiter/OffersManagementPage";
import CollegeRecommendationsPage from "../pages/recruiter/CollegeRecommendationsPage";
import CompanyProfilePage from "../pages/recruiter/CompanyProfilePage";

const AppRoutes = () => {
  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/register-recruiter" element={<RecruiterRegisterPage />} />
      <Route path="/register-tpo" element={<TpoRegisterPage />} />
      <Route path="/forgot-password" element={<ForgotPasswordPage />} />

      {/* STUDENT PORTAL ROUTES */}
      <Route
        path="/student/dashboard"
        element={
          <RoleBasedRoute allowedRoles={["STUDENT"]}>
            <StudentDashboard />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/student/profile"
        element={
          <RoleBasedRoute allowedRoles={["STUDENT"]}>
            <StudentProfilePage />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/student/drives"
        element={
          <RoleBasedRoute allowedRoles={["STUDENT"]}>
            <BrowsedrivesPage />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/student/drives/:driveId"
        element={
          <RoleBasedRoute allowedRoles={["STUDENT"]}>
            <DriveDetailsPage />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/student/applications"
        element={
          <RoleBasedRoute allowedRoles={["STUDENT"]}>
            <MyApplicationsPage />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/student/calendar"
        element={
          <RoleBasedRoute allowedRoles={["STUDENT"]}>
            <InterviewCalendarPage />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/student/offers"
        element={
          <RoleBasedRoute allowedRoles={["STUDENT"]}>
            <OfferPage />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/student/history"
        element={
          <RoleBasedRoute allowedRoles={["STUDENT"]}>
            <CompanyHistoryPage />
          </RoleBasedRoute>
        }
      />

      {/* TPO PORTAL ROUTES */}
      <Route
        path="/tpo/dashboard"
        element={
          <RoleBasedRoute allowedRoles={["TPO"]}>
            <TpoDashboard />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/tpo/live-monitor"
        element={
          <RoleBasedRoute allowedRoles={["TPO"]}>
            <LiveDriveMonitorPage />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/tpo/drives"
        element={
          <RoleBasedRoute allowedRoles={["TPO"]}>
            <ManageDrivesPage />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/tpo/calendar"
        element={
          <RoleBasedRoute allowedRoles={["TPO"]}>
            <CentralCalendarPage />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/tpo/reschedules"
        element={
          <RoleBasedRoute allowedRoles={["TPO"]}>
            <RescheduleManagementPage />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/tpo/students"
        element={
          <RoleBasedRoute allowedRoles={["TPO"]}>
            <ManageStudentsPage />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/tpo/companies"
        element={
          <RoleBasedRoute allowedRoles={["TPO"]}>
            <ManageCompaniesPage />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/tpo/analytics"
        element={
          <RoleBasedRoute allowedRoles={["TPO"]}>
            <AnalyticsPages />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/tpo/reports"
        element={
          <RoleBasedRoute allowedRoles={["TPO"]}>
            <WeeklyReportsPage />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/tpo/announcements"
        element={
          <RoleBasedRoute allowedRoles={["TPO"]}>
            <AnnouncementsPage />
          </RoleBasedRoute>
        }
      />

      {/* RECRUITER PORTAL ROUTES */}
      <Route
        path="/recruiter/dashboard"
        element={
          <RoleBasedRoute allowedRoles={["RECRUITER"]}>
            <RecruiterDashboard />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/recruiter/create-drive"
        element={
          <RoleBasedRoute allowedRoles={["RECRUITER"]}>
            <CreateDrivePage />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/recruiter/applicants"
        element={
          <RoleBasedRoute allowedRoles={["RECRUITER"]}>
            <ApplicatsPages />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/recruiter/interviews"
        element={
          <RoleBasedRoute allowedRoles={["RECRUITER"]}>
            <InterviewManagementPage />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/recruiter/panels"
        element={
          <RoleBasedRoute allowedRoles={["RECRUITER"]}>
            <PanelManagementPage />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/recruiter/evaluations"
        element={
          <RoleBasedRoute allowedRoles={["RECRUITER"]}>
            <EvaluationPage />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/recruiter/offers"
        element={
          <RoleBasedRoute allowedRoles={["RECRUITER"]}>
            <OffersManagementPage />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/recruiter/recommendations"
        element={
          <RoleBasedRoute allowedRoles={["RECRUITER"]}>
            <CollegeRecommendationsPage />
          </RoleBasedRoute>
        }
      />
      <Route
        path="/recruiter/profile"
        element={
          <RoleBasedRoute allowedRoles={["RECRUITER"]}>
            <CompanyProfilePage />
          </RoleBasedRoute>
        }
      />

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
};

export default AppRoutes;
