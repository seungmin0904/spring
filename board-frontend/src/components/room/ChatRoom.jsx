import React, { useEffect, useState, useRef } from "react";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

const ChatRoom = ({ roomId }) => {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const stompClient = useRef(null);
  const isConnected = useRef(false);
  const isSubscribed = useRef(false);
  // 사용자 정보
  const token = localStorage.getItem("token");

  useEffect(() => {
    // JWT를 URL 쿼리파라미터로 전달!
    const socket = new SockJS(`http://localhost:8080/ws-chat?token=${token}`);
    const client = Stomp.over(socket);

    client.connect({}, () => {
      console.log("✅ WebSocket 연결 성공");
      isConnected.current = true;

      // 구독
     if (!isSubscribed.current) { // ✅ 이미 구독했는지 확인!
      client.subscribe(`/topic/chatroom.${roomId}`, (msg) => {
        const received = JSON.parse(msg.body);
        console.log("📩 수신:", received);
        setMessages((prev) => [...prev, received]);
      });
      isSubscribed.current = true; // ✅ 구독했음 표시
    }
  });

    stompClient.current = client;

    // 연결 종료
    return () => {
      if (isConnected.current && client.connected) {
        client.disconnect(() => {
          console.log("🛑 WebSocket 연결 종료");
        });
      }
    };
  }, [roomId]);

  const sendMessage = () => {
    if (!input.trim()) return;

    const chatMessage = { message: input };

    stompClient.current.send(
      `/app/chat.send/${roomId}`,
      {}, // ✅ WebSocket 전송은 헤더 없이!
      JSON.stringify(chatMessage)
    );

    setInput("");
  };

  return (
    <div style={{ padding: "1rem" }}>
      <h2>채팅방: {roomId}</h2>
      <div
        style={{
          border: "1px solid #ccc",
          height: "200px",
          overflowY: "auto",
          marginBottom: "0.5rem",
          padding: "0.5rem",
        }}
      >
      {messages.map((msg, idx) => (
  <div key={idx}>
    {msg.sender && <strong>{msg.sender}:</strong>} {msg.message}
  </div>
        ))}
      </div>
      <input
        value={input}
        onChange={(e) => setInput(e.target.value)}
        placeholder="메시지를 입력하세요"
      />
      <button onClick={sendMessage}>보내기</button>
    </div>
  );
};

export default ChatRoom;