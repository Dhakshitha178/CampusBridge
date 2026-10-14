import apiClient from "./apiClient";

export const getNotifications = async () => {
  const response = await apiClient.get("/notifications");
  return response.data;
};

export const getUnreadNotificationsCount = async () => {
  const response = await apiClient.get("/notifications/unread-count");
  return response.data;
};

export const markNotificationAsRead = async (id) => {
  const response = await apiClient.put(`/notifications/${id}/read`);
  return response.data;
};

export const markAllNotificationsAsRead = async () => {
  const response = await apiClient.put("/notifications/mark-all-read");
  return response.data;
};
