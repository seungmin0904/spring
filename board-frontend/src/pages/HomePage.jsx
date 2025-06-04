import { useState } from "react";
import ServerSidebar from "@/components/sidebar/ServerSidebar";
import ChannelSidebar from "@/components/sidebar/ChannelSidebar";
import DmSidebar from "@/components/sidebar/DmSidebar";
import FriendSidebar from "@/components/sidebar/FriendSidebar";
import ChannelMemberList from "@/components/channel/ChannelMemberList";
import DmMemberList from "@/components/room/DmMemberList";
import ChatRoom from "@/components/room/ChatRoom";
import DmRoom from "@/components/room/DmRoom";
import FriendListPage from "@/pages/FriendListPage";

export default function HomePage() {
  // Discord 분기 상태
  const [activeSection, setActiveSection] = useState("server"); // "server" | "dm" | "friend"
  const [selectedServerId, setSelectedServerId] = useState(null);
  const [selectedChannelId, setSelectedChannelId] = useState(null);
  const [selectedDmRoomId, setSelectedDmRoomId] = useState(null);

  // 클릭시 분기
  const handleSelectServer = (serverId) => {
    setActiveSection("server");
    setSelectedServerId(serverId);
    setSelectedChannelId(null);
    setSelectedDmRoomId(null);
  };
  const handleSelectDm = () => {
    setActiveSection("dm");
    setSelectedServerId(null);
    setSelectedChannelId(null);
    setSelectedDmRoomId(null);
  };
  const handleSelectFriend = () => {
    setActiveSection("friend");
    setSelectedServerId(null);
    setSelectedChannelId(null);
    setSelectedDmRoomId(null);
  };

  return (
    <div className="flex w-full h-full bg-[#222]">
      {/* 1열: 서버/DM/친구 아이콘바 */}
      <ServerSidebar
        onSelectServer={handleSelectServer}
        onSelectDm={handleSelectDm}
        onSelectFriend={handleSelectFriend}
        selectedServerId={selectedServerId}
        activeSection={activeSection}
      />
      {/* 2열: 분기형 사이드바 */}
      {activeSection === "server" && (
        <ChannelSidebar
          serverId={selectedServerId}
          onSelectChannel={setSelectedChannelId}
          selectedChannelId={selectedChannelId}
        />
      )}
      {activeSection === "dm" && (
        <DmSidebar
          onSelectDmRoom={setSelectedDmRoomId}
          selectedDmRoomId={selectedDmRoomId}
        />
      )}
      {activeSection === "friend" && <FriendSidebar />}
      {/* 중앙: 메인 컨텐츠 */}
      <div className="flex-1 bg-neutral-900 flex flex-col">
        {activeSection === "server" && selectedChannelId && (
          <ChatRoom roomId={selectedChannelId} />
        )}
        {activeSection === "dm" && selectedDmRoomId && (
          <DmRoom roomId={selectedDmRoomId} />
        )}
        {activeSection === "friend" && <FriendListPage />}
        {/* 안내문 */}
        {activeSection === "server" && !selectedChannelId && (
          <div className="text-white text-2xl m-auto">채널을 선택하세요.</div>
        )}
        {activeSection === "dm" && !selectedDmRoomId && (
          <div className="text-white text-2xl m-auto">DM방을 선택하세요.</div>
        )}
      </div>
      {/* 우측: 참여자 분기 */}
      {activeSection === "server" && (
        <ChannelMemberList channelId={selectedChannelId} />
      )}
      {activeSection === "dm" && (
        <DmMemberList roomId={selectedDmRoomId} />
      )}
      {/* friend 모드에는 우측 패널 없음 */}
    </div>
  );
}