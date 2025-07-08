import { useRef, useState, useCallback, useEffect } from 'react';
import Stomp from 'stompjs';
import refreshAxios from '@/lib/axiosInstance';

export const useWebSocket = (token, onConnect) => {
  const stompRef = useRef(null);
  const [connected, setConnected] = useState(false);
  const [reconnectTrigger, setReconnectTrigger] = useState(0);
  const tokenRef = useRef(token);
  const reconnectAttempt = useRef(0);
  const reconnectTimer = useRef(null);
  const subscriptionsRef = useRef([]);

  useEffect(() => {
    tokenRef.current = token;
  }, [token]);

  const hardDisconnect = () => {
    if (stompRef.current) {
      try {
        if (stompRef.current.connected) {
          stompRef.current.disconnect(() => {
            console.log("🔌 STOMP disconnected");
          });
        } else if (stompRef.current.ws?.readyState !== WebSocket.CLOSED) {
          console.log("❌ Forcibly closing socket");
          stompRef.current.ws.close();
        }
      } catch (e) {
        console.warn("⚠️ Disconnect error", e);
      }
    }
    stompRef.current = null;
    subscriptionsRef.current = [];
    setConnected(false);
  };

  const attemptRefreshToken = async () => {
    try {
      const refreshToken = localStorage.getItem("refresh_token");
      const res = await refreshAxios.post('/auth/refresh', { refreshToken });
      const newToken = res.data.token;

      if (newToken) {
        localStorage.setItem("token", newToken);
        tokenRef.current = newToken;
        return newToken;
      }
    } catch (err) {
      console.error("❌ Token refresh failed", err);
      localStorage.clear();
      window.location.href = "/login";
    }
    return null;
  };

  const connect = useCallback(async (tokenArg, callback) => {
    let authToken = tokenArg || tokenRef.current;
    if (!authToken) return;

    if (stompRef.current) {
      try {
        stompRef.current.disconnect();
      } catch (e) {
        console.warn("⚠️ Disconnect error during cleanup", e);
      }
      stompRef.current = null;
    }

    const socket = new WebSocket("ws://localhost:8080/ws-chat");
    const client = Stomp.over(socket);
    client.debug = () => {};
    stompRef.current = client;

    client.onWebSocketError = (e) => {
      console.error("❌ WebSocket Error", e);
      hardDisconnect();
      setReconnectTrigger(prev => prev + 1);
    };

    client.onWebSocketClose = () => {
      console.warn("🔌 WebSocket Closed");
      hardDisconnect();
      setReconnectTrigger(prev => prev + 1);
    };

    client.connect(
      { Authorization: "Bearer " + authToken },
      () => {
        console.log("✅ WebSocket, STOMP CONNECTED");
        setConnected(true);
        reconnectAttempt.current = 0;
        reconnectTimer.current = null;
        onConnect?.();
        callback?.();

        // 🔁 재구독
        subscriptionsRef.current.forEach(({ topic, callback }) => {
          try {
            client.subscribe(topic, msg => {
              callback(JSON.parse(msg.body));
            });
            console.log(`🔁 재구독 완료: ${topic}`);
          } catch (e) {
            console.error(`❌ 재구독 실패: ${topic}`, e);
          }
        });
      },
      async (err) => {
        const msg = err?.headers?.message || "";
        console.warn("❌ STOMP connect error", msg);

        if (msg.includes("Invalid JWT token")) {
          const newToken = await attemptRefreshToken();
          if (newToken) {
            console.log("🔄 Retrying connection with new token");
            connect(newToken);
            return;
          }
        }

        hardDisconnect();
        setReconnectTrigger(prev => prev + 1);
      }
    );
  }, [onConnect]);

  useEffect(() => {
    if (!tokenRef.current || reconnectTimer.current) return;

    const delay = Math.min(5000 * 2 ** reconnectAttempt.current, 30000);
    console.warn(`🔁 Reconnecting in ${delay / 1000}s`);

    reconnectTimer.current = setTimeout(() => {
      reconnectTimer.current = null;
      reconnectAttempt.current += 1;
      console.log(`🔁 [STOMP RECONNECT ATTEMPT ${reconnectAttempt.current}] Starting retry...`);
      connect(tokenRef.current);
    }, delay);

    return () => {
      if (reconnectTimer.current) {
        clearTimeout(reconnectTimer.current);
        reconnectTimer.current = null;
      }
    };
  }, [reconnectTrigger, connect]);

  const disconnect = useCallback(() => {
    hardDisconnect();
    reconnectAttempt.current = 0;
  }, []);

  const subscribe = useCallback((topic, callback) => {
    const existing = subscriptionsRef.current.find(sub => sub.topic === topic);
    if (existing) {
      console.warn(`⚠️ Already subscribed to ${topic}`);
      return {
        unsubscribe: () => {
          const client = stompRef.current;
          const wsReady = client?.ws?.readyState === WebSocket.OPEN;

          if (client?.connected && wsReady) {
            try {
              client.unsubscribe(topic);
              subscriptionsRef.current = subscriptionsRef.current.filter(sub => sub.topic !== topic);
              console.log(`🧹 구독 해제 완료: ${topic}`);
            } catch (e) {
              console.warn(`❌ unsubscribe 실패: ${topic}`, e);
            }
          } else {
            console.warn(`❌ WebSocket not ready. Cannot unsubscribe ${topic}`);
          }
        }
      };
    }

    subscriptionsRef.current.push({ topic, callback });

    const client = stompRef.current;
    const wsReady = client?.ws?.readyState === WebSocket.OPEN;

    if (!client || !client.connected || !wsReady) {
      console.warn(`⛔ Cannot subscribe to ${topic} – WebSocket not fully open`);
      return {
        unsubscribe: () => {
          console.warn(`⛔ Dummy unsubscribe for ${topic} (client not ready)`);
        }
      };
    }

    try {
      const subscription = client.subscribe(topic, msg => {
        console.log("🛬 수신 원본 메시지", msg.body);
        callback(JSON.parse(msg.body));
      });

      return {
        unsubscribe: () => {
          try {
            if (client.connected && wsReady) {
              subscription.unsubscribe();
              subscriptionsRef.current = subscriptionsRef.current.filter(sub => sub.topic !== topic);
              console.log(`🧹 구독 해제 완료: ${topic}`);
            } else {
              console.warn(`❌ WebSocket not open – unsubscribe skipped for ${topic}`);
            }
          } catch (err) {
            console.warn(`❌ unsubscribe 실패: ${topic}`, err);
          }
        }
      };
    } catch (e) {
      console.error(`❌ Error subscribing to ${topic}:`, e);
      return {
        unsubscribe: () => {
          console.warn(`⛔ Dummy unsubscribe after subscribe error for ${topic}`);
        }
      };
    }
  }, []);

  const send = useCallback((destination, body) => {
    const socketReady = stompRef.current?.ws?.readyState === WebSocket.OPEN;
    if (stompRef.current && connected && socketReady) {
      stompRef.current.send(destination, {}, JSON.stringify(body));
    } else {
      console.warn("❌ Cannot send message – WebSocket not ready", {
        connected,
        readyState: stompRef.current?.ws?.readyState,
      });
    }
  }, [connected]);

  return {
    connected,
    subscribe,
    send,
    connect,
    disconnect,
  };
};
