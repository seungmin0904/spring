// src/components/layout/MainLayout.jsx
import { useState, useEffect } from "react";
import Sidebar1 from "./Sidebar1";
import Sidebar2 from "./Sidebar2";
import Sidebar3 from "./Sidebar3";
import Sidebar4 from "./Sidebar4";
import { Outlet } from "react-router-dom";

export default function MainLayout() {
  const [selectedDM, setSelectedDM] = useState(false);
  const [selectedServerId, setSelectedServerId] = useState(null);
  const [selectedRoomId, setSelectedRoomId] = useState(null);
  const [friendMode, setFriendMode] = useState(false); // 친구 패널 3열 상태

  // 최초 마운트시 로컬스토리지에서 복원
  useEffect(() => {
    const savedServerId = localStorage.getItem("selectedServerId");
    const savedDM = localStorage.getItem("selectedDM");
    const savedRoomId = localStorage.getItem("selectedRoomId");
    const savedFriendMode = localStorage.getItem("friendMode");

    if (savedDM === "true") {
      setSelectedDM(true);
      setSelectedServerId(null);
    } else if (savedServerId) {
      setSelectedServerId(Number(savedServerId));
      setSelectedDM(false);
    }
    if (savedRoomId) {
      setSelectedRoomId(Number(savedRoomId));
    }
    setFriendMode(savedFriendMode === "true");
  }, []);

  // --- 저장 ---
  function handleSelectDM() {
    setSelectedDM(true);
    setSelectedServerId(null);
    setSelectedRoomId(null);
    setFriendMode(false);

    localStorage.setItem("selectedDM", "true");
    localStorage.removeItem("selectedServerId");
    localStorage.removeItem("selectedRoomId");
    localStorage.setItem("friendMode", "false");
  }
  function handleSelectServer(id) {
    setSelectedDM(false);
    setSelectedServerId(id);
    setSelectedRoomId(null);
    setFriendMode(false);

    localStorage.setItem("selectedServerId", id);
    localStorage.setItem("selectedDM", "false");
    localStorage.removeItem("selectedRoomId");
    localStorage.setItem("friendMode", "false");
  }
  function handleSelectChannel(id) {
    setSelectedRoomId(id);
    setFriendMode(false);
    localStorage.setItem("selectedRoomId", id);
    localStorage.setItem("friendMode", "false");
  }
  // --- DM에서 친구버튼 클릭 (3열에 친구패널)
  function handleSelectFriendPanel() {
    setSelectedRoomId(null);
    setFriendMode(true);
    localStorage.removeItem("selectedRoomId");
    localStorage.setItem("friendMode", "true");
  }
  // --- DM목록에서 유저 클릭(3열에 DM채팅)
  function handleSelectDMRoom(id) {
    setSelectedRoomId(id);
    setFriendMode(false);
    localStorage.setItem("selectedRoomId", id);
    localStorage.setItem("friendMode", "false");
  }

  return (
    <div className="flex h-screen w-screen">
       
      <Sidebar1
        onSelectDM={handleSelectDM}
        onSelectServer={handleSelectServer}
      />
      <Sidebar2 
      dmMode={selectedDM} 
      serverId={selectedServerId}
      // DM 모드에서 친구버튼/DM목록 분기
      onSelectFriendPanel={handleSelectFriendPanel} // 친구패널 진입
      onSelectDMRoom={handleSelectDMRoom}           // DM방 진입 
      onSelectChannel={handleSelectChannel}/>
      <Sidebar3 
      dmMode={selectedDM} 
      serverId={selectedServerId} 
      roomId={selectedRoomId}
      friendMode={friendMode}
      />
      <Sidebar4 serverId={selectedServerId} roomId={selectedRoomId}/>
      <Outlet />
      </div>
      
  );
}
