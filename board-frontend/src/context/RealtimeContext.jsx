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
  dmRooms: [],
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
    case 'SET_DM_ROOMS':
      return { ...state, dmRooms: action.payload };
    // ✅ 개별 DM방 추가/업데이트 액션 추가
    case 'ADD_OR_UPDATE_DM_ROOM':
      const existingIndex = state.dmRooms.findIndex(room => room.id === action.payload.id);
      if (existingIndex >= 0) {
        // 기존 방 업데이트
        const updatedRooms = [...state.dmRooms];
        updatedRooms[existingIndex] = action.payload;
        return { ...state, dmRooms: updatedRooms };
      } else {
        // 새 방 추가
        return { ...state, dmRooms: [...state.dmRooms, action.payload] };
      }
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
      const [friendsRes, receivedRes, sentRes, onlineRes, dmRoomsRes] = await Promise.all([
        axiosInstance.get("/friends"),
        axiosInstance.get("/friends/requests/received"),
        axiosInstance.get("/friends/requests/sent"),
        axiosInstance.get("/friends/online"),
        axiosInstance.get(`/dm/rooms/${user.id}`),
      ]);
      dispatch({ type: "SET_FRIENDS", payload: friendsRes.data || [] });
      dispatch({ type: "SET_RECEIVED", payload: receivedRes.data || [] });
      dispatch({ type: "SET_SENT", payload: sentRes.data || [] });
      dispatch({ type: "SET_ONLINE_USERS", payload: onlineRes.data || [] });
      dispatch({ type: "SET_DM_ROOMS", payload: dmRoomsRes.data || [] });
      console.log("✅ 친구 상태 초기화 완료");
    } catch (err) {
      console.error("❌ 친구 상태 초기화 실패:", err);
    }
  };

  // ✅ DM 목록 새로고침 함수 추가
  const refreshDmRooms = async () => {
    try {
      console.log("🔄 DM 목록 새로고침 시작...");
      const dmRoomsRes = await axiosInstance.get(`/dm/rooms/${user.id}`);
      dispatch({ type: "SET_DM_ROOMS", payload: dmRoomsRes.data || [] });
      console.log("✅ DM 목록 새로고침 완료:", dmRoomsRes.data);
    } catch (err) {
      console.error("❌ DM 목록 새로고침 실패:", err);
    }
  };

  useEffect(() => {
    if (token && user?.id) {
      console.log("🟥 RealtimeProvider Mounted");

      connect(token, () => {
        console.log("🟢 WebSocket connected → setReady(true)");
        initFriendState();
        const unsubscribeFn = subscribeAll();
        setReady(true);

        // ✅ cleanup 시 구독 해제
        return () => {
          unsubscribeFn?.();
          disconnect();
        };
      });
    }

    return () => {
      disconnect();
    };
  }, [token, user?.id]); // ✅ user.id 의존성 추가

  function subscribeAll() {
    if (!username || !user?.id) {
      console.warn("⚠️ username 또는 user.id가 없어서 구독을 건너뜁니다.");
      return () => {};
    }

    console.log("🔔 WebSocket 구독 시작:", username);

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

    // ✅ ✅ ✅ DM 복구 알림 구독 (핵심 수정 부분)
    const subDmRestore = subscribe(`/user/queue/dm-restore`, async (payload) => {
      console.log("📥 DM 복구 알림 수신:", payload);
      
      try {
        // ✅ 즉시 DM 목록 새로고침
        await refreshDmRooms();
        
        // ✅ 사용자에게 알림 표시
        toast({
          title: "💬 DM 복구",
          description: payload.status === "NEW" ? "새로운 DM방이 생성되었습니다." : "숨겨진 DM방이 복구되었습니다.",
        });
      } catch (err) {
        console.error("❌ DM 복구 처리 실패:", err);
      }
    });

    console.log("✅ 모든 WebSocket 구독 완료");

    // ✅ 안전 unsubscribe 반환
    return () => {
      console.log("🔄 WebSocket 구독 해제 시작");
      try {
        subStatus?.unsubscribe?.();
        subBroadcast?.unsubscribe?.();
        subNoti?.unsubscribe?.();
        subFriend?.unsubscribe?.();
        subDmRestore?.unsubscribe?.();
        console.log("✅ WebSocket 구독 해제 완료");
      } catch (err) {
        console.error("❌ WebSocket 구독 해제 중 오류:", err);
      }
    };
  }

  // ✅ Context value에 refreshDmRooms 함수 추가
  const contextValue = {
    state,
    dispatch,
    ready,
    refreshDmRooms, // 외부에서 수동으로 DM 목록 새로고침 가능
  };

  return (
    <RealtimeContext.Provider value={contextValue}>
      {children}
    </RealtimeContext.Provider>
  );
}

export const useRealtime = () => useContext(RealtimeContext);