import React, { useContext } from "react";
import Navbar from "./components/common/Navbar";
import Sidebar from "./components/common/Sidebar";
import Footer from "./components/common/Footer";
import AppRoutes from "./routes/AppRoutes";
import { AuthContext } from "./context/AuthContext";

function App() {
  const { user } = useContext(AuthContext);

  return (
    <div className="app-wrapper">
      <Navbar />
      {user ? (
        <div className="portal-layout">
          <Sidebar />
          <main className="portal-main-content">
            <AppRoutes />
          </main>
        </div>
      ) : (
        <main className="flex-grow-1 d-flex flex-column align-items-center justify-content-center py-4 px-3 bg-light">
          <AppRoutes />
        </main>
      )}
      <Footer />
    </div>
  );
}

export default App;