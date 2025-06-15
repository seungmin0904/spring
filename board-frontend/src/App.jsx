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
  const [user, setUser] = useState(null); // user: { id, name, ... }

  useEffect(() => {
    const savedToken = localStorage.getItem("token");
    const savedUser = localStorage.getItem("user");
    if (savedToken && savedUser) {
      setToken(savedToken);
      setUser(JSON.parse(savedUser));
    }
  }, []);


  const handleLogin = tok => {
    localStorage.setItem("token", tok);
    setToken(tok);
    axiosInstance.get("/members/me").then(res => {
      // res.data 에 토큰 합치기!
      const full = { ...res.data, token: tok };
      localStorage.setItem("user", JSON.stringify(full));
      setUser(full);
    });
  };


  const handleLogout = () => {
    localStorage.clear();
    setToken(null);
    setUser(null);
  };

  return (
    <ChatProvider>
      <ThemeProvider>
        <UserContext.Provider value={{ user, setUser }}>
          <BrowserRouter>
            <Routes>
              <Route path="/" element={<RootLayout onLogout={handleLogout} />}>
                <Route index element={<Layout />} />
                <Route path="posts" element={<PostListPage />} />
                <Route path="posts/new" element={<PostFormPage />} />
                <Route path="posts/:bno" element={<PostDetailPage name={user?.name} />} />
                <Route path="posts/:bno/edit" element={<PostFormPage isEdit={true} />} />
                <Route path="register" element={<RegisterPage />} />
                <Route path="mypage" element={<MyPage />} />
              </Route>
              <Route path="/login" element={<LoginPage onLogin={handleLogin} />} />
            </Routes>
          </BrowserRouter>
        </UserContext.Provider>
      </ThemeProvider>
    </ChatProvider>
  );
}


export default App;