// src/layout/Layout.jsx
import Navbar from "@/components/ui/Navbar";
import { Outlet } from "react-router-dom";

const Layout = ({ onLogout }) => (
  <div className="min-h-screen bg-gray-50 dark:bg-[#18181b] flex flex-col">
    <Navbar onLogout={onLogout} />
    <main className="flex-1 w-full pt-24 px-0">  {/* ← px-0, justify-center 제거 */}
      <Outlet />
    </main>
  </div>
);

export default Layout;