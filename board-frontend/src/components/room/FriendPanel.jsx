import { useEffect, useState } from "react";
import axios from "@/lib/axiosInstance";

export default function FriendPanel() {
  const [friends, setFriends] = useState([]);
  const [showAdd, setShowAdd] = useState(false);
  const [search, setSearch] = useState("");
  const [result, setResult] = useState([]);
  const [adding, setAdding] = useState(false);

  // 친구 목록 불러오기
  useEffect(() => {
    axios.get("/friends").then(res => setFriends(res.data || []));
  }, []);

  
  // 친구 검색
  const handleSearch = () => {
    if (!search.trim()) return;
    setResult([]);
    setAdding(true);
    axios.get(`/members/search?name=${encodeURIComponent(search)}`)
      .then(res => setResult(res.data || []))
      .finally(() => setAdding(false));
  };

  // 친구 추가
  const handleAdd = (id) => {
    axios.post("/friends", { targetMemberId: id })
      .then(() => {
        // 검색 결과가 id(memberId) 기반이면 id로, memberId 기반이면 memberId로!
        setFriends(f => [...f, result.find(r => r.id === id || r.memberId === id)]);
        setShowAdd(false);
        setSearch(""); setResult([]);
      });
  };

  return (
    <div className="h-full bg-[#313338] flex flex-col">
      <div className="flex items-center justify-between p-4 border-b border-zinc-800">
        <span className="text-white text-lg font-bold">친구</span>
        <button
          onClick={() => setShowAdd(true)}
          className="bg-blue-600 text-white rounded px-3 py-1 hover:bg-blue-700 transition"
        >친구 추가하기</button>
      </div>
      <div className="flex-1 overflow-y-auto p-4">
        {friends.length === 0 && <div className="text-zinc-400 text-center py-10">친구 없음</div>}
        {friends.map(f => (
          <div key={f.friendId} className="flex items-center gap-3 py-2 px-2 rounded hover:bg-zinc-800">
            <div className="w-10 h-10 rounded-full bg-zinc-700 flex items-center justify-center">
              {f?.name?.[0] || "?"}
            </div>
            <div>
              <div className="text-white font-semibold">{f.name}</div>
              <div className="text-zinc-400 text-xs">오프라인</div>
            </div>
          </div>
        ))}
      </div>
      {showAdd && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50">
          <div className="bg-zinc-900 rounded p-6 w-80 flex flex-col gap-3">
            <div className="text-white font-bold mb-2">친구 검색</div>
            <div className="flex gap-2">
              <input
                className="flex-1 rounded p-2"
                value={search}
                onChange={e => setSearch(e.target.value)}
                placeholder="닉네임 입력"
                onKeyDown={e => { if (e.key === "Enter") handleSearch(); }}
              />
              <button
                className="bg-blue-600 text-white rounded px-3 py-1"
                onClick={handleSearch}
                disabled={adding}
              >검색</button>
            </div>
            {adding && <div className="text-zinc-400 text-sm">검색중...</div>}
            <div>
              {result.map(u => (
                <div key={u.id ?? u.memberId} className="flex items-center justify-between mt-2 px-2 py-1 bg-zinc-800 rounded">
                  <span className="text-white">{u.name}</span>
                  <button
                    onClick={() => handleAdd(u.id ?? u.memberId)}
                    className="bg-green-600 text-white rounded px-2 py-1"
                  >추가</button>
                </div>
              ))}
            </div>
            <button
              onClick={() => setShowAdd(false)}
              className="bg-zinc-700 text-white rounded px-3 py-1 mt-2"
            >닫기</button>
          </div>
        </div>
      )}
    </div>
  );
}