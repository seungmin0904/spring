// ChannelList.jsx
import { useState, useEffect } from "react";
import axios from "axios";
import InviteModal from "./InviteModal";

export default function ChannelSidebar({ onSelectRoom, selectedRoomId }) {
  const [channels, setChannels] = useState([]);
  const [newName, setNewName] = useState("");
  const [inviteRoom, setInviteRoom] = useState(null);
  // 토큰기반 인증 헤더 
  const token = localStorage.getItem("token");
  const axiosConfig = { headers: { Authorization: `Bearer ${token}` } };

  // 채널목록 로딩
  useEffect(() => {
    axios.get("/api/chatrooms", axiosConfig).then(res => {
      setChannels(Array.isArray(res.data) ? res.data : []);
    });
  }, []);

  // 채널 생성
  const handleCreate = () => {
    if (!newName.trim()) return;
    axios
      .post(
        "/api/chatrooms",
        { name: newName, description: "" },
        axiosConfig
      )
      .then(() => {
        setNewName("");
        return axios.get("/api/chatrooms", axiosConfig);
      })
      .then(res => setChannels(Array.isArray(res.data) ? res.data : []))
      .catch(err => {
        if (err.response && err.response.status === 409) {
          alert(err.response.data?.error || "이미 존재하는 채널명입니다!");
        } else {
          alert("채널 생성 실패");
        }
      });
  };
  // 채널 삭제
  const handleDelete = roomId => {
    axios
      .delete(`/api/chatrooms/${roomId}`, axiosConfig)
      .then(() => axios.get("/api/chatrooms", axiosConfig))
      .then(res => {
        setChannels(Array.isArray(res.data) ? res.data : []);
        // 현재 선택중인 채널이 삭제된 채널이면 선택 해제
        if (selectedRoomId === roomId) {
          onSelectRoom(null);
        }
      });
  };

  return (
    <>
      <div className="w-64 bg-neutral-800 h-full p-2 flex flex-col gap-2">
        <div className="font-bold text-white text-lg mb-2">채널 목록</div>
        <div className="flex flex-col gap-1">
          {channels.map(room => (
            <div key={room.id} className="flex items-center gap-2">
              <button
                className={`flex-1 text-left p-2 rounded ${room.id === selectedRoomId ? "bg-neutral-600" : "hover:bg-neutral-700"} text-white`}
                onClick={() => onSelectRoom(room.id)}
              >
                #{room.name}
              </button>
              <button
                className="bg-blue-400 hover:bg-blue-600 rounded px-2 py-1 text-xs text-white"
                onClick={() => setInviteRoom(room)}
              >
                초대
              </button>
              <span
                className="text-xs text-red-400 cursor-pointer ml-1"
                onClick={e => {
                  e.stopPropagation();
                  handleDelete(room.id);
                }}
              >
                삭제
              </span>
            </div>
          ))}
        </div>
        <div className="mt-4 flex gap-1">
          <input
            value={newName}
            onChange={e => setNewName(e.target.value)}
            className="w-3/4 p-1 rounded bg-neutral-700 text-white"
            placeholder="새 채널명"
          />
          <button
            onClick={handleCreate}
            className="ml-1 px-2 py-1 bg-blue-500 text-white rounded"
          >
            추가
          </button>
        </div>
      </div>
      <InviteModal
        open={!!inviteRoom}
        room={inviteRoom}
        onClose={() => setInviteRoom(null)}
      />
    </>
  );
}
