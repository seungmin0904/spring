// src/context/RealtimeContext.jsx
import { useState, createContext, useContext, useReducer, useEffect } from 'react';
import axiosInstance from '@/lib/axiosInstance';
import { useUser } from './UserContext';
import { usePing } from '@/hooks/usePing';

const RealtimeContext = createContext();

const initialState = {
  onlineUsers: new Set(),
  notifications: [],
  typingUsers: new Map(),
  readStatus: new Map(),
  receivedRequests: [], // ✅ 추가
  sentRequests: [],
  friends: [],// ✅ 추가
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
      case 'SET_RECEIVED':
        return { ...state, receivedRequests: action.payload };
      case 'SET_SENT':
        return { ...state, sentRequests: action.payload };
      case 'SET_FRIENDS':
      return { ...state, friends: action.payload };  
      case 'REMOVE_FRIEND':
      return {
        ...state,
        friends: state.friends.filter(f => f.friendId !== action.payload)
      };
    

      
    default:
      return state;
  }
}

export function RealtimeProvider({ children, socket }) {
  const [state, dispatch] = useReducer(realtimeReducer, initialState);
  const [ready, setReady] = useState(false);
  const { user } = useUser();
  const username = user?.username;
  const token = user?.token;

  const { connected, subscribe, connect, disconnect } = socket;
  usePing();

  useEffect(() => {
    if (token) {
      console.log("🟥 RealtimeProvider Mounted");
      connect(token, () => {
        console.log("🟢 WebSocket connected → setReady(true)");
        setReady(true);
      });
    }

    return () => {
      disconnect();
    };
  }, [token]);

  useEffect(() => {
    if (!connected || !ready) return;

   
    
    axiosInstance.get('/friends/online')
      .then(res => {
        dispatch({ type: 'SET_ONLINE_USERS', payload: res.data || [] });
      })
      .catch(err => {
        console.error("❌ /friends/online 실패:", err);
      });
  }, [connected, ready]);

  useEffect(() => {
    if (!connected || !ready || !username) return;

    const subStatus = subscribe(`/user/queue/status`, ev => {
      console.log("🟢 실시간 상태 수신:", ev);
      dispatch({ type: 'USER_STATUS_CHANGE', payload: ev });
    });

    const subNoti = subscribe(`/user/queue/notifications.${username}`, msg => {
      dispatch({ type: 'ADD_NOTIFICATION', payload: msg });
    });

    
    const subFriend = subscribe(`/user/queue/friend`, async payload => {
      try {
        if (payload.type === "REQUEST_RECEIVED" || payload.type === "REQUEST_CANCELLED") {

          const res = await axiosInstance.get("/friends/requests/received");
          dispatch({ type: "SET_RECEIVED", payload: res.data || [] });

        } else if (payload.type === "REQUEST_ACCEPTED" || payload.type === "REQUEST_REJECTED") {

          const res = await axiosInstance.get("/friends/requests/sent");
          dispatch({ type: "SET_SENT", payload: res.data || [] });

          const friendRes = await axiosInstance.get("/friends");
          dispatch({ type: "SET_FRIENDS", payload: friendRes.data || [] });

         
          const onlineRes = await axiosInstance.get("/friends/online");
          dispatch({ type: "SET_ONLINE_USERS", payload: onlineRes.data || [] });

          
        } else if (payload.type === "FRIEND_DELETED") {
          const friendId = payload.payload?.requestId;
          if (friendId) {
            dispatch({ type: "REMOVE_FRIEND", payload: friendId });
          } else {
            console.warn("⚠️ FRIEND_DELETED 이벤트에 friendId 없음:", payload);
          }
        }
        
      } catch (err) {
        console.error("❌ 친구 요청 WebSocket 처리 실패:", err);
      }
    });

    return () => {
      subStatus.unsubscribe();
      subNoti.unsubscribe();
      subFriend.unsubscribe();
    };
  }, [connected, ready, subscribe, username]);
  

  return (
    <RealtimeContext.Provider value={{ state, dispatch }}>
      {children}
    </RealtimeContext.Provider>
  );
}

export const useRealtime = () => useContext(RealtimeContext);
