// ✅ App.jsx
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { useEffect, useState } from "react";
import { ThemeProvider } from "@/context/ThemeContext";
import { UserContext } from "@/context/UserContext";
import { ChatProvider } from "@/context/ChatContext";
import { RealtimeProvider } from "@/context/RealtimeContext";
import RootLayout from "@/layouts/RootLayout";
import Layout from "@/layouts/Layout";
import LoginPage from "@/pages/LoginPage";
import PostListPage from "@/pages/PostListPage";
import PostDetailPage from "@/pages/PostDetailPage";
import PostFormPage from "@/pages/PostFormPage";
import RegisterPage from "@/pages/RegisterPage";
import MyPage from "@/pages/MyPage";
import axiosInstance from "@/lib/axiosInstance";
import { useWebSocket } from "@/hooks/useWebSocket";
import { WebSocketContext } from "@/context/WebSocketContext";
import { Toaster } from "@/components/ui/toaster";
import InviteJoinPage from "@/pages/InviteJoinPage";


function App() {
  const [token, setToken] = useState(null);
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  const ws = useWebSocket(token); // ✅ 단일 생성

    // ✅ 다크모드 강제 적용 (시크릿 모드 대응)
    useEffect(() => {
      document.documentElement.classList.add('dark');
    }, []);

  // 로컬스토리지 복원
  useEffect(() => {
    const savedToken = localStorage.getItem("token");
    const savedUserRaw = localStorage.getItem("user");

    if (savedToken && savedUserRaw) {
      try {
        const parsedUser = JSON.parse(savedUserRaw);
        setToken(savedToken);
        setUser(parsedUser);
        console.log("✅ localStorage 복원 성공", parsedUser);
      } catch (err) {
        console.error("❌ localStorage user 파싱 실패", err);
        localStorage.removeItem("token");
        localStorage.removeItem("user");
      }
    }
    setIsLoading(false);
  }, []);

  // useEffect(() => {
  //   // ✅ 테스트용 오디오 엘리먼트 삽입 (자동 재생 확인용)
  //   const testAudio = document.createElement('audio');
  //   testAudio.src = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3";
  //   testAudio.autoplay = true;
  //   testAudio.controls = true;
  //   testAudio.volume = 1.0;
  //   testAudio.style.position = 'fixed';
  //   testAudio.style.bottom = '10px';
  //   testAudio.style.left = '10px';
  //   document.body.appendChild(testAudio);
  // }, []);

  const handleLogin = async (token) => {
    try {
      localStorage.setItem("token", token);
      setToken(token);

      const res = await axiosInstance.get("/members/me");
      const full = { ...res.data, token };
      console.log("👤 /members/me 결과:", full);
      localStorage.setItem("user", JSON.stringify(full));
      setUser(full);

      // window.location.href = "/";
    } catch (e) {
      console.error("로그인 처리 중 오류", e);
    }
  };

  const handleLogout = () => {
    ws.disconnect(); // ✅ 위에서 생성한 ws 활용
    localStorage.clear();
    setToken(null);
    setUser(null);
    window.location.href = "/login";
  };

  if (isLoading) return <div>Loading...</div>;

  return (
    <WebSocketContext.Provider value={ws}>
    <ChatProvider>
      <ThemeProvider>
        <UserContext.Provider value={{ user, setUser }}>
          <RealtimeProvider socket={ws}> {/* ✅ ws 주입 */}
            <Toaster />
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
                <Route path="/invite/:code" element={<InviteJoinPage />} />
              </Routes>
            </BrowserRouter>
          </RealtimeProvider>
        </UserContext.Provider>
      </ThemeProvider>
    </ChatProvider>
    </WebSocketContext.Provider>
  );
}

export default App;
