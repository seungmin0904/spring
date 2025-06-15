import { useEffect, useRef, useState, useCallback } from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

export const useWebSocket = (token) => {
  const stompRef = useRef(null);
  const [connected, setConnected] = useState(false);

  const connect = useCallback(() => {
    if (!token) {
      console.log('WS 📡 no token, skipping connect');
      return () => {};
    }
    console.log('WS 🚀 connecting with token…');
    const socket = new SockJS(`http://localhost:8080/ws-chat?token=${token}`);
    const client = Stomp.over(socket);
    client.debug = () => {}; // 로그 비활성화

    
client.connect(
  { Authorization: `Bearer ${token}` },   // ← 여기
  () => {
    console.log('✅ WS connected');
    stompRef.current = client;
    setConnected(true);
  },
  (err) => {
    console.error('❌ WS connection error:', err);
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

  // topic 구독 helper
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

  // mount 시 connect(), unmount 시 cleanup()
  useEffect(() => {
    console.log("▶️ useWebSocket token:", token);
    if (!token) return;           // ← token 유무 체크
    const cleanup = connect();    // token 이 있을 때만 connect
    return () => cleanup();
  }, [token, connect]);
  

  return { connected, subscribe };
};
