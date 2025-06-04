import { useEffect, useState, useRef, useContext } from "react";
import axiosInstance from "@/lib/axiosInstance";
import { UserContext } from "@/context/UserContext";

// DM 채팅방 본문 (roomId로 메시지 fetch & 송신)
export default function DmRoom({ roomId }) {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const { mno } = useContext(UserContext); // 실제로는 mno 등 ID가 더 안전
  const messagesEndRef = useRef(null);

  // DM 메시지 가져오기
  useEffect(() => {
    if (!roomId) {
      setMessages([]);
      return;
    }
    axiosInstance.get(`/dm/room/${roomId}/messages`)
      .then(res => setMessages(Array.isArray(res.data) ? res.data : []))
      .catch(() => setMessages([]));
  }, [roomId]);

  // 스크롤 자동 하단
  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages]);

  // 메시지 전송
  const handleSend = async (e) => {
    e.preventDefault();
    if (!input.trim() || !roomId) return;
    try {
      await axiosInstance.post(`/dm/room/${roomId}/messages`, {
        sender: mno, // 실제로는 mno 등 유저ID
        content: input,
      });
      setInput("");
      // 다시 fetch (혹은 실시간 socket 연동)
      const res = await axiosInstance.get(`/dm/room/${roomId}/messages`);
      setMessages(Array.isArray(res.data) ? res.data : []);
    } catch {
      // 에러처리
    }
  };

  if (!roomId) {
    return (
      <div className="flex-1 flex items-center justify-center text-2xl text-neutral-400">
        DM방을 선택하세요.
      </div>
    );
  }

  return (
    <div className="flex-1 flex flex-col">
      {/* 메시지 리스트 */}
      <div className="flex-1 overflow-y-auto p-4 space-y-2">
        {messages.map((msg, i) => (
          <div key={i} className="flex items-end gap-2">
            <span className="font-bold text-sm text-blue-400">{msg.senderName ?? msg.sender ?? "알수없음"}</span>
            <span className="text-white bg-neutral-700 rounded-xl px-3 py-1">{msg.content}</span>
            <span className="text-xs text-neutral-400">{msg.time ?? ""}</span>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>
      {/* 입력창 */}
      <form onSubmit={handleSend} className="flex p-2 border-t border-neutral-800 bg-neutral-900">
        <input
          className="flex-1 bg-neutral-800 rounded-lg px-3 py-2 text-white outline-none"
          value={input}
          onChange={e => setInput(e.target.value)}
          placeholder="메시지 입력..."
          autoFocus
        />
        <button
          type="submit"
          className="ml-2 px-4 py-2 rounded-lg bg-blue-600 text-white font-bold"
        >
          전송
        </button>
      </form>
    </div>
  );
}