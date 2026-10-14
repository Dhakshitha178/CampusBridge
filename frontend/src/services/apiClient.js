import axios from "axios";

// Central axios instance for all backend calls.
// Change baseURL if your Spring Boot server runs on a different port.
const apiClient = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

// Attach auth token (if present) to every request
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("cb_token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default apiClient;