import React, { createContext, useState } from "react";

export const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem("cb_user");
    return stored ? JSON.parse(stored) : null;
  });

  const login = (userData, token) => {
    setUser(userData);
    localStorage.setItem("cb_user", JSON.stringify(userData));
    localStorage.setItem("cb_token", token || "");
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem("cb_user");
    localStorage.removeItem("cb_token");
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};