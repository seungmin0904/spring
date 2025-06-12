import { useEffect, useRef, useCallback } from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

export const useWebSocket = (token) => {
  const stompRef = useRef(null);
  const subscriptions = useRef(new Map());

  const connect = useCallback(() => {
    if (!token) return () => {};

    const socket = new SockJS('http://localhost:8080/ws');
    const client = Stomp.over(socket);

    // 반드시 헤더로 Authorization 전달
    client.connect(
      { Authorization: `Bearer ${token}` },
      () => {
        console.log('WebSocket Connected');
        stompRef.current = client;
      }
    );

    return () => {
      if (stompRef.current) {
        stompRef.current.disconnect();
      }
    };
  }, [token]);

  const subscribe = useCallback((topic, callback) => {
    if (!stompRef.current) return;

    const subscription = stompRef.current.subscribe(topic, (message) => {
      const payload = JSON.parse(message.body);
      callback(payload);
    });

    subscriptions.current.set(topic, subscription);
    return () => subscription.unsubscribe();
  }, []);

  const unsubscribe = useCallback((topic) => {
    const subscription = subscriptions.current.get(topic);
    if (subscription) {
      subscription.unsubscribe();
      subscriptions.current.delete(topic);
    }
  }, []);

  useEffect(() => {
    const cleanup = connect(() => {
      // 여기서 subscribe
      subscribe('/topic/online-users', (msg) => {
        console.log("Received online-users", msg);
        // ...setOnlineUsers 등
      });
    });

    return () => {
      cleanup();
      subscriptions.current.forEach((subscription) => subscription.unsubscribe());
      subscriptions.current.clear();
    };
  }, [connect]);

  return { subscribe, unsubscribe };
};