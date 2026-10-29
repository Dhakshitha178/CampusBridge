import apiClient from "./apiClient";

export const getStudentProfile = async () => {
  const response = await apiClient.get("/student/profile");
  return response.data;
};

export const updateStudentProfile = async (data) => {
  const response = await apiClient.put("/student/profile", data);
  return response.data;
};

export const getStudentDashboard = async () => {
  const response = await apiClient.get("/student/dashboard");
  return response.data;
};

export const getStudentApplications = async () => {
  const response = await apiClient.get("/student/applications");
  return response.data;
};

export const applyToDrive = async (driveId, notes = "") => {
  const response = await apiClient.post(`/student/applications/${driveId}/apply`, { driveId, notes });
  return response.data;
};

export const withdrawApplication = async (applicationId) => {
  const response = await apiClient.post(`/student/applications/${applicationId}/withdraw`);
  return response.data;
};

export const getStudentCalendar = async () => {
  const response = await apiClient.get("/student/interviews/calendar");
  return response.data;
};

export const getStudentConflicts = async () => {
  const response = await apiClient.get("/student/interviews/conflicts");
  return response.data;
};

export const submitRescheduleRequest = async (requestData) => {
  const response = await apiClient.post("/student/interviews/reschedule-request", requestData);
  return response.data;
};

export const getStudentOffers = async () => {
  const response = await apiClient.get("/student/offers");
  return response.data;
};

export const respondToOffer = async (offerId, accept) => {
  const response = await apiClient.post(`/student/offers/${offerId}/respond?accept=${accept}`);
  return response.data;
};

export const getCompanyPlacementHistory = async () => {
  const response = await apiClient.get("/student/company-history");
  return response.data;
};

export const getInterCollegePlacementHistory = async () => {
  const response = await apiClient.get("/student/inter-college-history");
  return response.data;
};