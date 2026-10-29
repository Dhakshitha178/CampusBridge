import apiClient from "./apiClient";

export const registerStudent = async (data) => {
  const response = await apiClient.post("/auth/register", data);
  return response.data;
};

export const registerRecruiter = async (data) => {
  const response = await apiClient.post("/auth/register-recruiter", data);
  return response.data;
};

export const registerTpo = async (data) => {
  const response = await apiClient.post("/auth/register-tpo", data);
  return response.data;
};

export const loginUser = async (credentials) => {
  const response = await apiClient.post("/auth/login", credentials);
  return response.data;
};

export const getColleges = async () => {
  const response = await apiClient.get("/auth/colleges");
  return response.data;
};

export const getCompanies = async () => {
  const response = await apiClient.get("/auth/companies");
  return response.data;
};

export const getDepartments = async (collegeId) => {
  const url = collegeId ? `/auth/departments?collegeId=${collegeId}` : "/auth/departments";
  const response = await apiClient.get(url);
  return response.data;
};

export const forgotPassword = async (email) => {
  const response = await apiClient.post("/auth/forgot-password", { email });
  return response.data;
};

export const resetPassword = async (token, newPassword) => {
  const response = await apiClient.post("/auth/reset-password", { token, newPassword });
  return response.data;
};

export const verifyEmail = async (email, code) => {
  const response = await apiClient.post("/auth/verify-email", { email, code });
  return response.data;
};

export const checkUserStatus = async (email) => {
  const response = await apiClient.get(`/auth/status?email=${encodeURIComponent(email)}`);
  return response.data;
};

export const getPendingUsers = async () => {
  const response = await apiClient.get("/auth/pending-users");
  return response.data;
};

export const approveUser = async (userId, email) => {
  let url = "/auth/approve-user?";
  if (userId) url += `userId=${userId}`;
  else if (email) url += `email=${encodeURIComponent(email)}`;
  const response = await apiClient.post(url);
  return response.data;
};
