//ChannelMemberList.jsx
import { useEffect, useState } from "react";
import axios from "axios";

export default function ChannelMemberList({ roomId }) {
  const [members, setMembers] = useState([]);
  const token = localStorage.getItem("token");
  const axiosConfig = { headers: { Authorization: `Bearer ${token}` } };


  useEffect(() => {
    if (!roomId) {
      setMembers([]);   // 방이 없으면 참여자목록 초기화
      return;
    }
    axios
      .get(`/api/channel-members/room/${roomId}`, axiosConfig)
      .then(res => setMembers(Array.isArray(res.data) ? res.data : []));
  }, [roomId]);

  return (
    <div className="w-64 bg-neutral-900 h-full p-2 text-white flex flex-col gap-2">
      <div className="font-bold mb-2">참여자 목록</div>
      {members.map(m => (
        <div key={m.id} className="p-2 rounded bg-neutral-800 flex items-center gap-2">
          {m.profile && (
            <img
              src={m.profile}
              alt=""
              className="w-8 h-8 rounded-full object-cover mr-2"
            />
          )}
          <span>{m.name}</span>
          <span>{m.role === "ADMIN" ? "👑" : ""}</span>
          {m.muted && <span className="text-red-400 ml-2">뮤트됨</span>}
          {m.banned && <span className="text-red-500 ml-2">밴됨</span>}
        </div>
      ))}
    </div>
  );
}