import { BrowserRouter, Routes, Route, Outlet } from "react-router-dom";
import { useEffect, useState } from "react";
import { ThemeProvider } from "@/context/ThemeContext";
import { UserContext } from "@/context/UserContext";
import { ChatProvider } from "@/context/ChatContext";
import HomePage from "@/pages/HomePage";
import LoginPage from "@/pages/LoginPage";
import PostListPage from "@/pages/PostListPage";
import PostDetailPage from "@/pages/PostDetailPage";
import PostFormPage from "@/pages/PostFormPage";
import RegisterPage from "@/pages/RegisterPage";
import MyPage from "@/pages/MyPage";
import Layout from "@/layouts/Layout";
import axiosInstance from "@/lib/axiosInstance";
import Navbar from "@/components/ui/Navbar";

function RootLayout({ onLogout }) {
  return (
    <div>
      <Navbar onLogout={onLogout} />
      <div className="pt-16 h-[calc(100vh-4rem)]">
        <Outlet />
      </div>
    </div>
  );
}

function App() {
  const [token, setToken] = useState(null);
  const [name, setName] = useState(null);

  useEffect(() => {
    const savedToken = localStorage.getItem("token");
    const savedName = localStorage.getItem("name");
    if (savedToken && savedName) {
      setToken(savedToken);
      setName(savedName);
    }
  }, []);

  const handleLogin = (token) => {
    if (token) {
      localStorage.setItem("token", token);
      setToken(token);
      axiosInstance.get("/members/me").then((res) => {
        setName(res.data.name);
        localStorage.setItem("name", res.data.name);
      });
    } else {
      localStorage.clear();
      setToken(null);
      setName(null);
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    setToken(null);
    setName(null);
  };

    return (
    <ChatProvider>
      <ThemeProvider>
        <UserContext.Provider value={{ name, setName }}>
          <BrowserRouter>
            <Routes>
              {/* 네비바 + Outlet 구조 */}
              <Route path="/" element={<RootLayout onLogout={handleLogout} />}>
                {/* "/" => 디스코드 UI */}
                <Route index element={<Layout />} />
                {/* 게시판/마이페이지 등 */}
                <Route path="posts" element={<PostListPage />} />
                <Route path="posts/new" element={<PostFormPage />} />
                <Route path="posts/:bno" element={<PostDetailPage name={name} />} />
                <Route path="posts/:bno/edit" element={<PostFormPage isEdit={true} />} />
                <Route path="register" element={<RegisterPage />} />
                <Route path="mypage" element={<MyPage />} />
              </Route>
              {/* 로그인은 네비바 없는 단독 */}
              <Route path="/login" element={<LoginPage onLogin={handleLogin} />} />
            </Routes>
          </BrowserRouter>
        </UserContext.Provider>
      </ThemeProvider>
    </ChatProvider>
  );
}


export default App;