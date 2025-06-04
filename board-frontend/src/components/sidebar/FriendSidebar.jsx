import { useEffect, useState, useContext } from "react";
import axiosInstance from "@/lib/axiosInstance";
import { UserContext } from "@/context/UserContext";

export default function FriendSidebar() {
  const [friends, setFriends] = useState([]);
  const { name } = useContext(UserContext);

  useEffect(() => {
    if (!name) return;
    axiosInstance.get(`/friends/list/${name}`) // userId 등으로 맞추는 게 안전
      .then(res => setFriends(Array.isArray(res.data) ? res.data : []))
      .catch(() => setFriends([]));
  }, [name]);

  return (
    <div className="w-52 bg-neutral-950 border-r border-neutral-800 flex flex-col py-2">
      <div className="px-4 text-neutral-300 mb-3 font-bold">친구</div>
      {friends.length === 0 && (
        <div className="px-4 py-2 text-neutral-400">친구가 없습니다.</div>
      )}
      {friends.map(friend => (
        <div
          key={friend.id}
          className="px-4 py-2 text-neutral-300 border-b border-neutral-800"
        >
          {friend.name} {/* API 반환 구조에 맞게 */}
        </div>
      ))}
    </div>
  );
}