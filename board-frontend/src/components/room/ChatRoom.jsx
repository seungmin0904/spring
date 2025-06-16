import { useEffect, useState } from "react";
import axios from "@/lib/axiosInstance"; 

export default function ChatRoom({ roomId, currentUser, subscribe, send }) {
  const [messageMap, setMessageMap] = useState({});
  const [input, setInput] = useState("");
  const messages = messageMap[roomId] || [];

  // 메시지 로딩
  useEffect(() => {
    if (!roomId) return;
    if (messageMap[roomId]) return;

    axios.get(`/chat/${roomId}`)
      .then(res => {
        setMessageMap(prev => ({
          ...prev,
          [roomId]: res.data || []
        }));
      })
      .catch(() => {
        setMessageMap(prev => ({
          ...prev,
          [roomId]: []
        }));
      });
  }, [roomId]);

  // 구독
  useEffect(() => {
    if (!roomId) return;
    const sub = subscribe(`/topic/chatroom.${roomId}`, payload => {
      setMessageMap(prev => ({
        ...prev,
        [roomId]: [...(prev[roomId] || []), payload]
      }));
    });
    return () => {
      sub?.unsubscribe?.();
    };
  }, [roomId, subscribe]);

  function sendMessage() {
    if (!input.trim()) return;
    send(`/app/chat.send/${roomId}`, {
      message: input,
    });
    setInput("");
  }

  return (
    <div className="flex flex-col h-full">
      <div className="flex-1 overflow-y-auto p-2 bg-zinc-950">
        {messages.map((msg, i) => (
          <div key={i}>
            {msg.sender && <span className="font-bold">{msg.sender}:</span>}{" "}
            {msg.message}
          </div>
        ))}
      </div>
      <div className="flex gap-2 p-2 bg-zinc-900 border-t border-zinc-700">
        <input
          className="flex-1 bg-zinc-800 rounded p-2 text-white"
          value={input}
          onChange={e => setInput(e.target.value)}
          onKeyDown={e => e.key === "Enter" && sendMessage()}
          placeholder="메시지 입력"
        />
        <button className="bg-blue-600 text-white rounded px-4" onClick={sendMessage}>
          전송
        </button>
      </div>
    </div>
  );
}