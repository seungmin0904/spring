import { createContext, useContext, useReducer, useEffect } from 'react';
import { useWebSocket } from '../hooks/useWebSocket';
import axios from '@/lib/axiosInstance';

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

function getUsernameFromToken(token) {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.sub;
  } catch {
    return null;
  }
}

export function RealtimeProvider({ children, token }) {
  const [state, dispatch] = useReducer(realtimeReducer, initialState);
  const { connected, subscribe } = useWebSocket(token);

  // 1) 최초 HTTP 스냅샷
  useEffect(() => {
    if (!connected) return;
    axios
      .get('/friends/online', { headers: { Authorization: `Bearer ${token}` } })
      .then(res => {
        const list = Array.isArray(res.data) ? res.data : [];
        dispatch({ type: 'SET_ONLINE_USERS', payload: list });
      })
      .catch(console.error);
  }, [connected, token]);

  // 2) WebSocket 실시간 구독
  useEffect(() => {
    if (!connected) return;
    const username = getUsernameFromToken(token);
    if (!username) return;

    const subStatus = subscribe(`/topic/online-users.${username}`, ev => {
      dispatch({ type: 'USER_STATUS_CHANGE', payload: ev });
    });
    const subNoti = subscribe('/user/queue/notifications.*', msg => {
      dispatch({ type: 'ADD_NOTIFICATION', payload: msg });
    });

    return () => {
      subStatus.unsubscribe();
      subNoti.unsubscribe();
    };
  }, [connected, subscribe, token]);

  return (
    <RealtimeContext.Provider value={{ state, dispatch }}>
      {children}
    </RealtimeContext.Provider>
  );
}

export const useRealtime = () => useContext(RealtimeContext);
