import { useState, createContext, useContext, useReducer, useEffect } from 'react';
import { useWebSocket } from '../hooks/useWebSocket';
import axiosInstance from '@/lib/axiosInstance';
import { useUser } from './UserContext';

const RealtimeContext = createContext();

const initialState = {
  onlineUsers: new Set(),
  notifications: [],
  typingUsers: new Map(),
  readStatus: new Map(),
};

function realtimeReducer(state, action) {
  switch (action.type) {
    case 'SET_ONLINE_USERS':
      return { ...state, onlineUsers: new Set(action.payload) };
    case 'USER_STATUS_CHANGE': {
      const newSet = new Set(state.onlineUsers);
      if (action.payload.status === 'ONLINE') newSet.add(action.payload.username);
      else if (action.payload.status === 'OFFLINE') newSet.delete(action.payload.username);
      return { ...state, onlineUsers: newSet };
    }
    case 'ADD_NOTIFICATION':
      return { ...state, notifications: [...state.notifications, action.payload] };
    case 'TYPING_STATUS':
      return {
        ...state,
        typingUsers: new Map(state.typingUsers).set(
          action.payload.username,
          action.payload.isTyping
        ),
      };
    default:
      return state;
  }
}

export function RealtimeProvider({ children, token }) {
  const [state, dispatch] = useReducer(realtimeReducer, initialState);
  const [ready, setReady] = useState(false);

  const { connected, subscribe } = useWebSocket(token, () => {
    console.log('🟢 WebSocket connected → setReady(true)');
    setReady(true);
  });

  const { user } = useUser();
  const username = user?.username;

  console.log("🟥 RealtimeProvider Mounted");
  console.log("🟦 WebSocket connected:", connected);
  console.log("🟨 Ready:", ready);
  console.log("🟪 Token:", token);
  console.log("🟧 Username:", username);

  useEffect(() => {
    console.log("🔁 useEffect [connected, ready] 실행됨");
    if (!connected || !ready) {
      console.log("⛔ useEffect 차단됨: connected or ready false");
      return;
    }

    console.log("🟡 /friends/online 요청 시작");

    axiosInstance.get('/friends/online')
      .then(res => {
        console.log("✅ /friends/online 응답:", res.data);
        dispatch({ type: 'SET_ONLINE_USERS', payload: res.data || [] });
      })
      .catch(err => {
        console.error("❌ /friends/online 실패:", err);
      });
  }, [connected, ready]);

  useEffect(() => {
    console.log("🔁 useEffect [connected, ready, username] 실행됨");
    if (!connected || !ready || !username) {
      console.log("⛔ subscribe 차단됨: connected/ready/username 누락");
      return;
    }
  
    console.log("📡 WebSocket 구독 시작:", username);
  
    // ✅ 상태 수신: 친구가 online/offline 될 때 수신
    const subStatus = subscribe(`/user/queue/status`, ev => {
      console.log("📥 [친구 상태 변경 수신] →", ev);
      dispatch({ type: 'USER_STATUS_CHANGE', payload: ev });
    });
  
    const subNoti = subscribe(`/user/queue/notifications.${username}`, msg => {
      console.log("📥 알림 수신:", msg);
      dispatch({ type: 'ADD_NOTIFICATION', payload: msg });
    });
  
    return () => {
      console.log("🧹 WebSocket 구독 해제");
      subStatus.unsubscribe();
      subNoti.unsubscribe();
    };
  }, [connected, ready, subscribe, username]);
  
  return (
    <RealtimeContext.Provider value={{ state, dispatch }}>
      {children}
    </RealtimeContext.Provider>
  );
}

export const useRealtime = () => useContext(RealtimeContext);
