import { useEffect, useState } from "react";
import axiosInstance from "@/lib/axiosInstance";

export default function ServerSidebar({
  onSelectServer,
  onSelectDm,
  onSelectFriend,
  selectedServerId,
  activeSection,
}) {
  const [servers, setServers] = useState([]);

  useEffect(() => {
    axiosInstance.get("/servers")
      .then(res => setServers(Array.isArray(res.data) ? res.data : []))
      .catch(() => setServers([]));
  }, []);

  return (
    <div className="w-16 flex flex-col items-center py-3 bg-neutral-900 border-r border-neutral-800 gap-2">
      {/* DM 버튼 */}
      <button
        className={`w-12 h-12 rounded-full flex items-center justify-center mb-1 ${activeSection === "dm" ? "bg-blue-600" : "bg-neutral-700"}`}
        onClick={onSelectDm}
      >
        <span role="img" aria-label="DM" className="text-xl">💬</span>
      </button>
      {/* 서버 리스트 */}
      {servers.map(server => (
        <button
          key={server.id}
          className={`w-12 h-12 rounded-full flex items-center justify-center mb-1 ${selectedServerId === server.id && activeSection === "server" ? "bg-green-600" : "bg-neutral-700"}`}
          onClick={() => onSelectServer(server.id)}
        >
          <span className="text-lg font-bold">{server.name[0]}</span>
        </button>
      ))}
      {/* 친구 버튼 */}
      <button
        className={`w-12 h-12 rounded-full flex items-center justify-center mt-auto ${activeSection === "friend" ? "bg-pink-600" : "bg-neutral-700"}`}
        onClick={onSelectFriend}
      >
        <span role="img" aria-label="친구" className="text-xl">👥</span>
      </button>
    </div>
  );
}