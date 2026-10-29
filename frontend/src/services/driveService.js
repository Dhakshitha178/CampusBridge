import apiClient from "./apiClient";

export const getActiveDrives = async () => {
  const response = await apiClient.get("/placement-drives");
  return response.data;
};

export const getDriveDetails = async (driveId) => {
  const response = await apiClient.get(`/placement-drives/${driveId}`);
  return response.data;
};

export const checkDriveEligibility = async (driveId) => {
  const response = await apiClient.get(`/placement-drives/${driveId}/eligibility`);
  return response.data;
};
