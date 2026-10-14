import apiClient from "./apiClient";

export const scheduleInterviews = async (data) => {
  const res = await apiClient.post("/recruiter/interviews/schedule", data);
  return res.data;
};

export const getCompanyCalendar = async (companyId) => {
  const res = await apiClient.get(`/recruiter/${companyId}/calendar`);
  return res.data;
};

export const getStudentCalendar = async (studentId) => {
  const res = await apiClient.get(`/student/${studentId}/calendar`);
  return res.data;
};

export const getCentralCalendar = async (collegeId) => {
  const res = await apiClient.get("/tpo/calendar", { params: { collegeId } });
  return res.data;
};

export const recordEvaluation = async (interviewId, data, evaluatorName = "Interviewer") => {
  const res = await apiClient.post(`/recruiter/interviews/${interviewId}/evaluate`, data, {
    params: { evaluatorName },
  });
  return res.data;
};

export const submitRescheduleRequest = async (studentId, interviewId, data) => {
  const res = await apiClient.post(`/student/${studentId}/interviews/${interviewId}/reschedule`, data);
  return res.data;
};

export const reviewRescheduleRequest = async (requestId, data, reviewerName = "TPO") => {
  const res = await apiClient.post(`/tpo/reschedule-requests/${requestId}/review`, data, {
    params: { reviewerName },
  });
  return res.data;
};

export const getPendingReschedules = async (collegeId) => {
  const res = await apiClient.get("/tpo/reschedule-requests", { params: { collegeId } });
  return res.data;
};
