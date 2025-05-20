import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useUser } from "@/context/UserContext";

 const Navbar = ({onLogout}) => {
  const { name } = useUser();
  const isLoggedIn = !!localStorage.getItem("token");
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.clear();
    navigate("/");
    onLogout?.();
  };

   return (
    <nav className="fixed top-0 w-full z-50 bg-white text-black shadow-md">
  <div className="flex items-center justify-between px-6 py-4 w-full">
    {/* 왼쪽: 로고 */}
    <div className="text-xl font-bold text-indigo-600">
      <Link to="/">Simple Board</Link>
    </div>

    {/* 오른쪽: 메뉴 */}
    <div className="flex items-center space-x-4 text-sm">
      {/* 로그인 사용자 닉네임 표시 */}
    {isLoggedIn && name && (
    <span className="text-gray-500"> 닉네임: {name}</span>
    )}
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
  </div>
</nav>
  );
};

export default Navbar;