// src/layout/Layout.jsx
import Navbar from "@/components/ui/Navbar";
import { Outlet } from "react-router-dom";

const Layout = ({ name, onLogout }) => {
  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navbar name={name} onLogout={onLogout} />
      <main className="flex-1 px-4 pt-24 flex justify-center">
        <div className="w-full max-w-6xl px-4">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default Layout;