import { useEffect, useState, useContext } from "react";
import axiosInstance from "@/lib/axiosInstance";
import { UserContext } from "@/context/UserContext";

export default function DmSidebar({ selectedDmRoomId, onSelectDmRoom }) {
  const [dms, setDms] = useState([]);
  const { name } = useContext(UserContext); // 실제로는 mno 등 유저 고유 ID가 더 좋음

  useEffect(() => {
    if (!name) return;
    axiosInstance.get(`/api/dm/rooms/${name}`) // userId로 바꾸는 게 더 안전
      .then(res => setDms(Array.isArray(res.data) ? res.data : []))
      .catch(() => setDms([]));
  }, [name]);

  return (
    <div className="w-52 bg-neutral-950 border-r border-neutral-800 flex flex-col py-2">
      <div className="px-4 text-neutral-300 mb-3 font-bold">DM</div>
      {dms.map(dm => (
        <button
          key={dm.id}
          className={`px-4 py-2 text-left w-full ${selectedDmRoomId === dm.id ? "bg-neutral-800 text-white" : "hover:bg-neutral-800 text-neutral-400"}`}
          onClick={() => onSelectDmRoom(dm.id)}
        >
          {dm.name}
        </button>
      ))}
    </div>
  );
}