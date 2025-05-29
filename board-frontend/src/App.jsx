import { useEffect, useState } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { ThemeProvider } from "@/context/ThemeContext";
import HomePage from "@/pages/HomePage";
import LoginPage from "@/pages/LoginPage";
import PostListPage from "@/pages/PostListPage";
import PostDetailPage from "@/pages/PostDetailPage";
import PostFormPage from "@/pages/PostFormPage";
import Layout from "@/layouts/Layout";
import axiosInstance from "@/lib/axiosInstance";
import RegisterPage from "@/pages/RegisterPage";
import MyPage from "@/pages/MyPage";
import { UserContext } from "@/context/UserContext";
import FriendListPage from "@/pages/FriendListPage";
import { ChatProvider } from "@/context/ChatContext";

function App() {
  // eslint-disable-next-line no-unused-vars
  const [token,setToken] = useState(null);
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
      <Route path="/friends" element={<FriendListPage />} /> 
      <Route path="/" element={<Layout onLogout={handleLogout} />}>
      <Route index element={<HomePage />} />
      <Route path="posts" element={<PostListPage />} />
      <Route path="posts/new" element={<PostFormPage />} />
      <Route path="posts/:bno" element={<PostDetailPage name={name} />} />
      <Route path="posts/:bno/edit" element={<PostFormPage isEdit={true} />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/mypage" element={<MyPage />} />
   <Route path="/login" element={<LoginPage onLogin={handleLogin} />} />
  </Route>

  </Routes>
  </BrowserRouter>
</UserContext.Provider>
</ThemeProvider>
</ChatProvider>
  );
}

export default App;