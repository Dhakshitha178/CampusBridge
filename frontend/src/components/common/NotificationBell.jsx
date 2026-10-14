import React, { useState, useContext, useRef, useEffect } from "react";
import { NotificationContext } from "../../context/NotificationContext";

const NotificationBell = () => {
  const { notifications, unreadCount, markRead, markAllRead } = useContext(NotificationContext);
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <div className="position-relative d-inline-block" ref={dropdownRef}>
      <button
        type="button"
        className="btn btn-link text-white position-relative p-1 text-decoration-none"
        onClick={() => setIsOpen(!isOpen)}
        title="Notifications"
      >
        <i className="bi bi-bell-fill fs-5"></i>
        {unreadCount > 0 && (
          <span className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
            {unreadCount}
            <span className="visually-hidden">unread messages</span>
          </span>
        )}
      </button>

      {isOpen && (
        <div
          className="position-absolute end-0 mt-2 card shadow-lg"
          style={{ width: "350px", zIndex: 1050, maxHeight: "420px", overflowY: "auto" }}
        >
          <div className="card-header d-flex justify-content-between align-items-center bg-light py-2">
            <span className="fw-bold small text-dark">
              <i className="bi bi-bell me-1"></i> Notifications ({unreadCount})
            </span>
            {notifications && notifications.length > 0 && (
              <button
                className="btn btn-sm btn-link text-decoration-none p-0 text-primary small"
                onClick={markAllRead}
              >
                Mark all read
              </button>
            )}
          </div>
          <div className="list-group list-group-flush">
            {!notifications || notifications.length === 0 ? (
              <div className="p-3 text-center text-muted small">No notifications yet.</div>
            ) : (
              notifications.slice(0, 10).map((n) => (
                <div
                  key={n.id}
                  className={`list-group-item list-group-item-action p-2 small ${
                    !n.read ? "bg-light border-start border-4 border-primary" : ""
                  }`}
                >
                  <div className="d-flex justify-content-between align-items-start">
                    <strong className="text-dark">{n.title}</strong>
                    {!n.read && (
                      <button
                        className="btn btn-sm btn-link text-muted p-0 ms-2"
                        title="Mark as read"
                        onClick={() => markRead(n.id)}
                      >
                        <i className="bi bi-check-circle"></i>
                      </button>
                    )}
                  </div>
                  <div className="text-secondary mt-1">{n.message}</div>
                  <div className="text-muted text-end mt-1" style={{ fontSize: "0.75rem" }}>
                    {n.createdAt ? new Date(n.createdAt).toLocaleDateString() : ""}
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default NotificationBell;
