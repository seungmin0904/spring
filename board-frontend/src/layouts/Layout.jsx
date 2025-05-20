// src/layout/Layout.jsx
import Navbar from "@/components/ui/Navbar";
import { Outlet } from "react-router-dom";

const Layout = ({onLogout }) => {
  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navbar onLogout={onLogout} />
      <main className="w-full flex justify-center pt-24 px-4">
        <div className="w-full max-w-screen-xl flex justify-center">
          <Outlet />
          </div>
      </main>
    </div>
  );
};

export default Layout;