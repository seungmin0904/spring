import { useEffect, useRef, useState, useCallback } from 'react';
import Stomp from 'stompjs';
// useWebSocket.js
export const useWebSocket = (token, onConnect) => {
  const stompRef = useRef(null);
  const [connected, setConnected] = useState(false);

  const connect = useCallback(() => {
    if (!token) return () => {};

    const socket = new WebSocket("ws://localhost:8080/ws-chat");
    const client = Stomp.over(socket);
    client.debug = () => {};
    client.connect(
      { Authorization: "Bearer " + localStorage.getItem("token"), },
      () => {
        stompRef.current = client;
        setConnected(true);
        onConnect?.();
      },
      err => {
        console.error("❌ WS connection error:", err);
        setConnected(false);
      }
    );

    return () => {
      if (stompRef.current) {
        stompRef.current.disconnect(() => {
          setConnected(false);
        });
      }
    };
  }, [token]);

  const subscribe = useCallback((topic, callback) => {
    if (!stompRef.current || !connected) {
      console.warn(`⛔ Cannot subscribe to ${topic} – not connected`);
      return { unsubscribe: () => {} };
    }
    return stompRef.current.subscribe(topic, msg => {
      callback(JSON.parse(msg.body));
    });
  }, [connected]);

  const send = useCallback((destination, body) => {
    if (stompRef.current && connected) {
      stompRef.current.send(destination, {}, JSON.stringify(body));
    }
  }, [connected]);

  useEffect(() => {
    if (!token) return;
    const cleanup = connect();
    return () => cleanup();
  }, [token, connect]);

  return { connected, subscribe, send };
};

