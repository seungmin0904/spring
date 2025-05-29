import { useState, useEffect } from "react";
import axiosInstance from "@/lib/axiosInstance";
import ChatMessages from "./ChatMessages";
import ChatInput from "./ChatInput";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

const ChatRoom = () => {
  const [roomId, setRoomId] = useState(null);
  const [roomName, setRoomName] = useState("");
  const [inviteCode, setInviteCode] = useState("");
  const [isJoined, setIsJoined] = useState(false);
  const [messages, setMessages] = useState([]);
  const [stompClient, setStompClient] = useState(null);

  // ✅ 채널 생성 (A 계정)
  const createRoom = async () => {
    const name = prompt("채널 이름을 입력하세요!");
    if (!name) return;

    try {
      const res = await axiosInstance.post("/chatrooms", { name });
      alert(`채널 생성 완료! 초대코드: ${res.data.code}`);
      setRoomId(res.data.id);
      setRoomName(res.data.name);
      setInviteCode(res.data.code); // A 계정도 초대코드 세팅
      setIsJoined(true);
    } catch (error) {
      console.error("채널 생성 실패:", error);
      alert("채널 생성 실패!");
    }
  };

  // ✅ 채널 참여 (B 계정)
  const joinRoom = async () => {
    if (!inviteCode) {
      alert("초대코드를 입력하세요!");
      return;
    }

    try {
      await axiosInstance.post(`/chatrooms/${inviteCode}/join`);
      // ✅ 참여 후 채널 정보 가져오기
      const res = await axiosInstance.get("/chatrooms");
      const found = res.data.find((room) => room.code === inviteCode);

      if (!found) {
        alert("채널 정보 없음!");
        return;
      }

      setRoomId(found.id);
      setRoomName(found.name);
      setIsJoined(true);
    } catch (error) {
      console.error("채널 참여 실패:", error);
      alert("채널 참여 실패!");
    }
  };

  // ✅ 소켓 연결 및 수신 구독
  useEffect(() => {
    if (!isJoined || !roomId) return;

    const socket = new SockJS("http://localhost:8080/ws-chat");
    const client = Stomp.over(socket);

    const token = localStorage.getItem("token");
    const name = localStorage.getItem("name");

    const headers = { 
        Authorization: `Bearer ${token}`,
        name: name,
    };

    client.connect(headers, () => {
      console.log("🔗 WebSocket 연결됨!");
      client.subscribe(`/topic/channel.${roomId}`, (msg) => {
        const newMsg = JSON.parse(msg.body);
        console.log("📩 수신:", newMsg);
        setMessages((prev) => [...prev, newMsg]);
      });
    });

    setStompClient(client);

    return () => {
      if (client && client.connected) {
        client.disconnect(() => console.log("❌ WebSocket 연결 해제됨"));
      }
    };
  }, [isJoined, roomId]);

  // ✅ 메시지 전송
  const sendMessage = (text) => {
    if (stompClient && roomId) {
      const token = localStorage.getItem("token");
      const headers = { Authorization: `Bearer ${token}` };
      stompClient.send(
        "/app/chat.send",
        headers,
        JSON.stringify({
          roomId,
          content: text,
        })
      );
    }
  };

  // ✅ 참여 전 화면
  if (!isJoined) {
    return (
      <div className="p-4 flex flex-col gap-2 items-center">
        <h2 className="text-lg font-bold mb-2">채팅방 테스트</h2>
        <div className="flex gap-2">
          <button
            onClick={createRoom}
            className="bg-green-500 text-white px-3 py-1 rounded"
          >
            채널 생성 (A)
          </button>
          <input
            value={inviteCode}
            onChange={(e) => setInviteCode(e.target.value)}
            placeholder="초대코드 입력"
            className="border p-1 rounded"
          />
          <button
            onClick={joinRoom}
            className="bg-blue-500 text-white px-3 py-1 rounded"
          >
            참여하기 (B)
          </button>
        </div>
      </div>
    );
  }

  // ✅ 채팅 화면
  return (
    <div className="flex flex-col flex-1 p-4">
      <h2 className="text-xl font-bold mb-2">#{roomName}</h2>
      <ChatMessages messages={messages} />
      <ChatInput onSend={sendMessage} />
    </div>
  );
};

export default ChatRoom;