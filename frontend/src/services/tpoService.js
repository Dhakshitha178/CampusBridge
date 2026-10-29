import apiClient from "./apiClient";

export const getTpoDashboard = async () => {
  const response = await apiClient.get("/tpo/dashboard-stats");
  return response.data;
};

export const getStudentsList = async (params) => {
  const response = await apiClient.get("/tpo/students", { params });
  return response.data;
};

export const getCompaniesList = async () => {
  const response = await apiClient.get("/tpo/companies");
  return response.data;
};

export const verifyCompany = async (companyId, verified) => {
  const response = await apiClient.post(`/tpo/companies/${companyId}/verify?verified=${verified}`);
  return response.data;
};

export const getPendingDrives = async () => {
  // Backend returns all drives; filter PENDING_APPROVAL on frontend
  const response = await apiClient.get("/tpo/drives");
  const result = response.data;
  if (result.success && result.data) {
    result.data = result.data.filter(
      (d) => d.status === "PENDING_APPROVAL" || d.status === "PENDING"
    );
  }
  return result;
};

export const approveDrive = async (driveId, approvalNotes) => {
  const response = await apiClient.post(`/tpo/drives/${driveId}/review`, {
    approved: true,
    notes: approvalNotes,
  });
  return response.data;
};

export const rejectDrive = async (driveId, rejectionReason) => {
  const response = await apiClient.post(`/tpo/drives/${driveId}/review`, {
    approved: false,
    notes: rejectionReason,
  });
  return response.data;
};

export const getLiveMonitor = async () => {
  const response = await apiClient.get("/tpo/live-monitor");
  return response.data;
};

export const getCentralCalendar = async () => {
  const response = await apiClient.get("/tpo/calendar");
  return response.data;
};

export const getRescheduleRequests = async () => {
  const response = await apiClient.get("/tpo/reschedule-requests");
  return response.data;
};

export const approveRescheduleRequest = async (requestId, payload) => {
  const response = await apiClient.post(`/tpo/reschedule-requests/${requestId}/review`, {
    approved: true,
    tpoRemarks: payload.coordinatorNotes,
    newDate: payload.newInterviewDate,
    newStartTime: payload.newStartTime,
    newEndTime: payload.newEndTime,
  });
  return response.data;
};

export const rejectRescheduleRequest = async (requestId, rejectionReason) => {
  const response = await apiClient.post(`/tpo/reschedule-requests/${requestId}/review`, {
    approved: false,
    tpoRemarks: rejectionReason,
  });
  return response.data;
};

export const getPlacementAnalytics = async () => {
  const response = await apiClient.get("/tpo/analytics");
  return response.data;
};

export const getWeeklyReports = async () => {
  const response = await apiClient.get("/tpo/reports");
  return response.data;
};

export const generateWeeklyReport = async () => {
  const response = await apiClient.post("/tpo/reports/generate");
  return response.data;
};

export const downloadReportPdf = async (reportId) => {
  const token = localStorage.getItem("cb_token");
  const response = await fetch(`http://localhost:8080/api/tpo/reports/${reportId}/pdf`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  if (!response.ok) throw new Error("Failed to download PDF");
  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = `CampusBridge_Weekly_Report_${reportId}.pdf`;
  document.body.appendChild(a);
  a.click();
  a.remove();
};

export const exportPlacementExcel = async () => {
  const token = localStorage.getItem("cb_token");
  const response = await fetch(`http://localhost:8080/api/tpo/reports/export/excel`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  if (!response.ok) throw new Error("Failed to export Excel");
  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = `CampusBridge_Placement_Master_${new Date().toISOString().slice(0, 10)}.xlsx`;
  document.body.appendChild(a);
  a.click();
  a.remove();
};

export const getAnnouncements = async () => {
  const response = await apiClient.get("/tpo/announcements");
  return response.data;
};

export const createAnnouncement = async (announcementData) => {
  const response = await apiClient.post("/tpo/announcements", announcementData);
  return response.data;
};

// NOTE: Backend DELETE endpoint for announcements is not yet implemented
export const deleteAnnouncement = async (id) => {
  const response = await apiClient.delete(`/tpo/announcements/${id}`);
  return response.data;
};

export const getPendingUsers = async () => {
  const response = await apiClient.get("/tpo/pending-users");
  return response.data;
};

export const approveUser = async (userId) => {
  const response = await apiClient.post(`/tpo/users/${userId}/approve`);
  return response.data;
};
