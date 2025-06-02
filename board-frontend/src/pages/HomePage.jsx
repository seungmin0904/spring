import { useState } from "react";
import ChannelSidebar from "@/chat/ChannelList";
import ChannelMemberList from "@/chat/ChannelMemberList";
import ChatRoom from "@/chat/ChatRoom";

export default function DiscordHome() {
  const [selectedRoomId, setSelectedRoomId] = useState(null);

  return (
    <div className="flex w-screen h-screen">
      <ChannelSidebar selectedRoomId={selectedRoomId} onSelectRoom={setSelectedRoomId} />
      <div className="flex-1 bg-neutral-950 flex flex-col">
        {selectedRoomId ?
          <ChatRoom roomId={selectedRoomId} />
          : <div className="text-white text-2xl m-auto">채널을 선택하세요.</div>
        }
      </div>
      <ChannelMemberList roomId={selectedRoomId} />
    </div>
  );
}

