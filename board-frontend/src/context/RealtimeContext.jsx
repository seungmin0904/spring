// src/context/RealtimeContext.jsx
import { useState, createContext, useContext, useReducer, useEffect } from 'react';
import axiosInstance from '@/lib/axiosInstance';
import { useUser } from './UserContext';
import { usePing } from '@/hooks/usePing';
import { useToast } from "@/hooks/use-toast";

const RealtimeContext = createContext();

const initialState = {
  onlineUsers: new Set(),
  notifications: [],
  typingUsers: new Map(),
  readStatus: new Map(),
  receivedRequests: [],
  sentRequests: [],
  friends: [],
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
  const { toast } = useToast();
  usePing();

  const initFriendState = async () => {
    try {
      const [friendsRes, receivedRes, sentRes, onlineRes] = await Promise.all([
        axiosInstance.get("/friends"),
        axiosInstance.get("/friends/requests/received"),
        axiosInstance.get("/friends/requests/sent"),
        axiosInstance.get("/friends/online"),
      ]);
      dispatch({ type: "SET_FRIENDS", payload: friendsRes.data || [] });
      dispatch({ type: "SET_RECEIVED", payload: receivedRes.data || [] });
      dispatch({ type: "SET_SENT", payload: sentRes.data || [] });
      dispatch({ type: "SET_ONLINE_USERS", payload: onlineRes.data || [] });
      console.log("✅ 친구 상태 초기화 완료");
    } catch (err) {
      console.error("❌ 친구 상태 초기화 실패:", err);
    }
  };

  useEffect(() => {
    if (token) {
      console.log("🟥 RealtimeProvider Mounted");
      connect(token, () => {
        console.log("🟢 WebSocket connected → setReady(true)");
        initFriendState();
        subscribeAll();
        setReady(true);
      });
    }

    return () => {
      disconnect();
    };
  }, [token]);

  function subscribeAll() {
    if (!username) return;
    

    const subStatus = subscribe(`/user/queue/status`, ev => {
      console.log("🟢 실시간 상태 수신:", ev);
      dispatch({ type: 'USER_STATUS_CHANGE', payload: ev });
       if (ev.username !== username) {
        
      toast({
        title: ev.status === "ONLINE" ? "🔔 친구 접속" : "🔕 친구 퇴장",
        description: `${ev.username}님이 ${ev.status} 상태가 되었습니다.`,
      });
    }
  
    });

    
  
    const subBroadcast = subscribe(`/topic/status`, ev => {
      console.log("📣 브로드캐스트 상태 수신:", ev);
      dispatch({ type: 'USER_STATUS_CHANGE', payload: ev });
    });
  
    const subNoti = subscribe(`/user/queue/notifications.${username}`, msg => {
      dispatch({ type: 'ADD_NOTIFICATION', payload: msg });
    });
  
    const subFriend = subscribe(`/user/queue/friend`, async payload => {
      try {
        const type = payload.type;
  
        if (["REQUEST_RECEIVED", "REQUEST_CANCELLED", "REQUEST_ACCEPTED", "REQUEST_REJECTED"].includes(type)) {
          const [friendsRes, receivedRes, sentRes, onlineRes] = await Promise.all([
            axiosInstance.get("/friends"),
            axiosInstance.get("/friends/requests/received"),
            axiosInstance.get("/friends/requests/sent"),
            axiosInstance.get("/friends/online"),
          ]);
  
          dispatch({ type: "SET_FRIENDS", payload: friendsRes.data || [] });
          dispatch({ type: "SET_RECEIVED", payload: receivedRes.data || [] });
          dispatch({ type: "SET_SENT", payload: sentRes.data || [] });
          dispatch({ type: "SET_ONLINE_USERS", payload: onlineRes.data || [] });
        }
  
        else if (type === "FRIEND_DELETED") {
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
  
    // ✅ 언마운트 시 구독 해제용 반환
    return () => {
      subStatus.unsubscribe();
      subBroadcast.unsubscribe();
      subNoti.unsubscribe();
      subFriend.unsubscribe();
    };
  }

  return (
    <RealtimeContext.Provider value={{ state, dispatch }}>
      {children}
    </RealtimeContext.Provider>
  );
}

export const useRealtime = () => useContext(RealtimeContext);
