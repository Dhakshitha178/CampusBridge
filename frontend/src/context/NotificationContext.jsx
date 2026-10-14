import React, { createContext, useState, useEffect, useContext, useCallback } from "react";
import { AuthContext } from "./AuthContext";
import {
  getNotifications,
  getUnreadNotificationsCount,
  markNotificationAsRead,
  markAllNotificationsAsRead,
} from "../services/notificationService";

export const NotificationContext = createContext(null);

export const NotificationProvider = ({ children }) => {
  const { user } = useContext(AuthContext);
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);

  const fetchNotifications = useCallback(async () => {
    if (!user) return;
    try {
      const res = await getNotifications();
      if (res.success && res.data) {
        setNotifications(res.data);
      }
      const countRes = await getUnreadNotificationsCount();
      if (countRes.success) {
        setUnreadCount(countRes.data);
      }
    } catch (err) {
      console.error("Error fetching notifications:", err);
    }
  }, [user]);

  useEffect(() => {
    if (user) {
      fetchNotifications();
      const interval = setInterval(fetchNotifications, 20000); // refresh every 20s
      return () => clearInterval(interval);
    } else {
      setNotifications([]);
      setUnreadCount(0);
    }
  }, [user, fetchNotifications]);

  const markRead = async (id) => {
    try {
      await markNotificationAsRead(id);
      fetchNotifications();
    } catch (err) {
      console.error("Error marking notification read:", err);
    }
  };

  const markAllRead = async () => {
    try {
      await markAllNotificationsAsRead();
      fetchNotifications();
    } catch (err) {
      console.error("Error marking all notifications read:", err);
    }
  };

  return (
    <NotificationContext.Provider
      value={{
        notifications,
        unreadCount,
        fetchNotifications,
        markRead,
        markAllRead,
      }}
    >
      {children}
    </NotificationContext.Provider>
  );
};
