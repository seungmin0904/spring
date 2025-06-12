// board-frontend/src/context/RealtimeContext.jsx
import { createContext, useContext, useReducer, useEffect } from 'react';
import { useWebSocket } from '../hooks/useWebSocket';

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
      return {
        ...state,
        onlineUsers: new Set(action.payload)
      };
    case 'USER_STATUS_CHANGE':
      const newOnlineUsers = new Set(state.onlineUsers);
      if (action.payload.status === 'ONLINE') {
        newOnlineUsers.add(action.payload.username);
      } else if (action.payload.status === 'OFFLINE') {
        newOnlineUsers.delete(action.payload.username);
      }
      return {
        ...state,
        onlineUsers: newOnlineUsers
      };
    case 'ADD_NOTIFICATION':
      return {
        ...state,
        notifications: [...state.notifications, action.payload],
      };
    case 'TYPING_STATUS':
      return {
        ...state,
        typingUsers: new Map(state.typingUsers).set(
          action.payload.username,
          action.payload.isTyping
        ),
      };
    // ... 기타 액션들
    default:
      return state;
  }
}

export function RealtimeProvider({ children, token }) {
  const [state, dispatch] = useReducer(realtimeReducer, initialState);
  const { subscribe } = useWebSocket(token);

  useEffect(() => {
    // 사용자 상태 구독
    const userStatusSub = subscribe('/topic/user.*', (message) => {
      console.log('User status change:', message);
      dispatch({ type: 'USER_STATUS_CHANGE', payload: message });
    });

    // 온라인 사용자 목록 구독
    const onlineUsersSub = subscribe('/topic/online-users', (message) => {
      console.log('Online users update:', message);
      dispatch({ 
        type: 'SET_ONLINE_USERS', 
        payload: message 
      });
    });

    // 알림 구독
    const notificationSub = subscribe('/user/queue/notifications.*', (message) => {
      dispatch({ type: 'ADD_NOTIFICATION', payload: message });
    });

    return () => {
      userStatusSub?.unsubscribe();
      onlineUsersSub?.unsubscribe();
      notificationSub?.unsubscribe();
    };
  }, [subscribe]);

  return (
    <RealtimeContext.Provider value={{ state, dispatch }}>
      {children}
    </RealtimeContext.Provider>
  );
}

export const useRealtime = () => useContext(RealtimeContext);