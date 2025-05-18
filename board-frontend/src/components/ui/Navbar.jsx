import React from "react";
import { Link, useNavigate } from "react-router-dom";

const Navbar = () => {
  const isLoggedIn = !!localStorage.getItem("token");
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.clear();
    navigate("/");
  };

  return (
    <nav className="fixed top-0 w-full z-50 bg-white shadow-md px-6 py-4 flex justify-between items-center">
      <Link to="/" className="text-xl font-bold">Simple Board</Link>

      <div className="space-x-4">
        <Link to="/posts" className="hover:underline">게시판</Link>
        {isLoggedIn ? (
          <>
            <Link to="/mypage" className="hover:underline">마이페이지</Link>
            <button onClick={handleLogout} className="hover:underline">로그아웃</button>
          </>
        ) : (
          <>
            <Link to="/login" className="hover:underline">로그인</Link>
            <Link to="/register" className="hover:underline">회원가입</Link>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;