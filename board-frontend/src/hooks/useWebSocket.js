import { useEffect, useRef, useState, useCallback } from 'react';
import Stomp from 'stompjs';

export const useWebSocket = (token,onConnect) => {
  const stompRef = useRef(null);
  const [connected, setConnected] = useState(false);

  const connect = useCallback(() => {
    if (!token) {
      console.log('WS 📡 no token, skipping connect');
      return () => {};
    }

    console.log('WS 🚀 connecting with token via WebSocket…');
    
    // ✅ SockJS 제거, 표준 WebSocket 사용
    const socket = new WebSocket("ws://localhost:8080/ws-chat");
    const client = Stomp.over(socket);
    client.debug = () => {}; // 로그 비활성화

   client.connect(
  { Authorization: `Bearer ${token}` },
  () => {
    stompRef.current = client;
    setConnected(true);
    onConnect?.(); // ⬅️ 안전하게 호출
  },
  (err) => {
    console.error("❌ WS connection error:", err);
    setConnected(false);
  }
);

    return () => {
      if (stompRef.current) {
        stompRef.current.disconnect(() => {
          console.log('🛑 WS disconnected');
          setConnected(false);
        });
      }
    };
  }, [token]);

  const subscribe = useCallback(
    (topic, callback) => {
      if (!stompRef.current || !connected) {
        console.warn(`⛔ Cannot subscribe to ${topic} – not connected`);
        return { unsubscribe: () => {} };
      }
      const sub = stompRef.current.subscribe(topic, (msg) => {
        callback(JSON.parse(msg.body));
      });
      return sub;
    },
    [connected]
  );

  useEffect(() => {
    if (!token) return;
    const cleanup = connect();
    return () => cleanup();
  }, [token, connect]);

  return { connected, subscribe };
};
