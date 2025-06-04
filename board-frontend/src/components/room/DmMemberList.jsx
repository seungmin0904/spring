import { useEffect, useState } from "react";
import axiosInstance from "@/lib/axiosInstance";

export default function DmMemberList({ roomId }) {
  const [members, setMembers] = useState([]);

  useEffect(() => {
    if (!roomId) {
      setMembers([]);
      return;
    }
    axiosInstance.get(`/api/dm/room/${roomId}/members`)
      .then(res => setMembers(Array.isArray(res.data) ? res.data : []))
      .catch(() => setMembers([]));
  }, [roomId]);

  if (!roomId) {
    return (
      <div className="w-60 bg-neutral-950 border-l border-neutral-800 flex flex-col py-2">
        <div className="px-4 text-neutral-300 mb-3 font-bold">참여자</div>
        <div className="px-4 py-2 text-neutral-400">DM방을 선택하세요.</div>
      </div>
    );
  }

  return (
    <div className="w-60 bg-neutral-950 border-l border-neutral-800 flex flex-col py-2">
      <div className="px-4 text-neutral-300 mb-3 font-bold">참여자</div>
      {members.length === 0 && (
        <div className="px-4 py-2 text-neutral-400">참여자가 없습니다.</div>
      )}
      {members.map(member => (
        <div
          key={member.mno ?? member.id}
          className="px-4 py-2 text-neutral-300 border-b border-neutral-800 flex items-center gap-2"
        >
          <span className="w-7 h-7 rounded-full bg-neutral-700 flex items-center justify-center">
            {/* 프로필 이미지가 있다면 <img src={member.profile} ... /> */}
            {member.name ? member.name[0] : "?"}
          </span>
          <span>{member.name}</span>
        </div>
      ))}
    </div>
  );
}