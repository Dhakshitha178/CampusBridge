import apiClient from "./apiClient";

export const getRecruiterDashboard = async () => {
  const response = await apiClient.get("/recruiter/dashboard");
  return response.data;
};

export const getCompanyProfile = async () => {
  const response = await apiClient.get("/recruiter/company");
  return response.data;
};

export const updateCompanyProfile = async (data) => {
  const response = await apiClient.put("/recruiter/company", data);
  return response.data;
};

export const createDrive = async (driveData) => {
  const response = await apiClient.post("/recruiter/drives", driveData);
  return response.data;
};

export const getRecruiterDrives = async () => {
  const response = await apiClient.get("/recruiter/drives");
  return response.data;
};

export const getDriveApplicants = async (driveId) => {
  const response = await apiClient.get(`/recruiter/drives/${driveId}/applicants`);
  return response.data;
};

export const updateApplicationStage = async (applicationId, stageData) => {
  const response = await apiClient.put(`/recruiter/applications/${applicationId}/stage`, stageData);
  return response.data;
};

export const checkInterviewConflicts = async (checkData) => {
  const response = await apiClient.post("/recruiter/interviews/check-conflicts", checkData);
  return response.data;
};

export const scheduleInterview = async (interviewData) => {
  const response = await apiClient.post("/recruiter/interviews", interviewData);
  return response.data;
};

export const getScheduledInterviews = async () => {
  const response = await apiClient.get("/recruiter/interviews");
  return response.data;
};

export const submitEvaluation = async (evaluationData) => {
  const response = await apiClient.post("/recruiter/evaluations", evaluationData);
  return response.data;
};

export const getInterviewEvaluation = async (interviewId) => {
  const response = await apiClient.get(`/recruiter/evaluations/interview/${interviewId}`);
  return response.data;
};

export const createOffer = async (offerData) => {
  const response = await apiClient.post("/recruiter/offers", offerData);
  return response.data;
};

export const getRecruiterOffers = async () => {
  const response = await apiClient.get("/recruiter/offers");
  return response.data;
};

export const getPanels = async () => {
  const response = await apiClient.get("/recruiter/panels");
  return response.data;
};

export const createPanel = async (panelData) => {
  const response = await apiClient.post("/recruiter/panels", panelData);
  return response.data;
};

export const getInterviewers = async () => {
  const response = await apiClient.get("/recruiter/interviewers");
  return response.data;
};

export const createInterviewer = async (interviewerData) => {
  const response = await apiClient.post("/recruiter/interviewers", interviewerData);
  return response.data;
};

export const getCollegeRecommendations = async (params) => {
  const response = await apiClient.get("/recruiter/recommendations/colleges", { params });
  return response.data;
};
