import { useEffect, useState, useRef } from "react";
import axios from "@/lib/axiosInstance";
import { useUser } from "@/context/UserContext";
import useMediasoupClient from "@/hooks/useMediaSoupClient";
import { useRealtime } from "@/context/RealtimeContext";

export default function Sidebar2({
  dmMode,
  serverId,
  onSelectFriendPanel,
  onSelectDMRoom,
  onSelectChannel
}) {
  const { user } = useUser();
  const currentUserId = user?.id;
  const {
    createSendTransport,
    sendAudio,
    createRecvTransport,
    consumeSpecificAudio
  } = useMediasoupClient();

  const [friends, setFriends] = useState([]);
  const [channels, setChannels] = useState([]);
  const [showCreate, setShowCreate] = useState(false);
  const [newName, setNewName] = useState("");
  const [newType, setNewType] = useState("TEXT");
  const [inviteCode, setInviteCode] = useState("");
  const [inviteChannelId, setInviteChannelId] = useState(null);
  const initialLoadRef = useRef(false);
  
  // ✅ RealtimeContext에서 dmRooms와 refreshDmRooms 가져오기
  const { state, dispatch, ready, refreshDmRooms } = useRealtime();
  const dmRooms = state.dmRooms;

  useEffect(() => {
    if (dmMode) {
      axios.get("/friends")
        .then(res => setFriends(Array.isArray(res.data) ? res.data : []))
        .catch(() => setFriends([]));
    }
  }, [dmMode]);

  useEffect(() => {
    if (!dmMode && serverId) fetchChannels();
    else setChannels([]);
  }, [dmMode, serverId]);

  function fetchChannels() {
    axios.get(`/servers/${serverId}/channels`)
      .then(res => setChannels(Array.isArray(res.data) ? res.data : []))
      .catch(() => setChannels([]));
  }

  // ✅ DM 모드일 때만 DM 목록 초기 로딩 (RealtimeContext에서 관리)
  useEffect(() => {
    if (dmMode && user?.id && ready) {
      if (dmRooms.length === 0 && !initialLoadRef.current) {
        console.log("🟢 Sidebar2: 최초 DM 목록 로드 수행");
        refreshDmRooms?.();
        initialLoadRef.current = true;
      }
    }
  }, [dmMode, user?.id, ready]);

  // ✅ 디버깅용 로그 추가
  useEffect(() => {
    console.log("📋 Sidebar2 DM 목록 상태:", {
      dmRoomsCount: dmRooms.length,
      dmRooms: dmRooms,
      ready: ready,
      dmMode: dmMode
    });
  }, [dmRooms, ready, dmMode]);

  function handleCreateChannel() {
    if (!newName.trim()) return;
    if (!serverId) {
      alert("serverId가 비어있음. 서버를 선택해주세요.");
      return;
    }
    if (!dmMode) {
      axios.post(`/chatrooms`, {
        name: newName,
        type: newType,
        description: "",
        serverId,
        roomType: "SERVER",
      }).then(() => {
        setShowCreate(false);
        setNewName("");
        setNewType("TEXT");
        fetchChannels();
      });
    }
  }

  function handleDeleteChannel(channelId) {
    if (!window.confirm("정말 삭제할까요?")) return;
    axios.delete(`/chatrooms/${channelId}`).then(fetchChannels);
  }

  function handleInviteCode(channelId) {
    axios.post(`/chatrooms/${channelId}/invite`)
      .then(res => {
        setInviteCode(res.data.code || res.data.inviteCode || "");
        setInviteChannelId(channelId);
      });
  }

  function closeInviteModal() {
    setInviteCode("");
    setInviteChannelId(null);
  }

  function handleDeleteDmRoom(roomId) {
    if (!window.confirm("이 DM방을 목록에서 삭제하시겠습니까?")) return;

    axios.delete(`/dm/room/${roomId}/hide/${currentUserId}`)
      .then(() => {
        // ✅ 전역 상태에서도 즉시 제거
        dispatch({
          type: "SET_DM_ROOMS",
          payload: state.dmRooms.filter((room) => room.id !== roomId)
        });
        console.log("✅ DM방 삭제 완료 - UI에서 즉시 제거됨");
      })
      .catch(err => {
        console.error("❌ DM 삭제 실패:", err);
        alert("DM 삭제에 실패했습니다.");
      });
  }

  const textChannels = channels.filter(ch => (ch?.type || '').toUpperCase().trim() === "TEXT");
  const voiceChannels = channels.filter(ch => (ch?.type || '').toUpperCase().trim() === "VOICE");

  if (dmMode) {
    return (
      <div className="w-[260px] min-w-[200px] max-w-[320px] flex flex-col h-full bg-[#2b2d31] border-r border-[#232428]">
        <div className="border-b border-[#232428] p-2">
          <button
            className="w-full text-left px-3 py-2 rounded text-white bg-[#2b2d31] hover:bg-[#36393f] transition font-bold"
            onClick={() => onSelectFriendPanel(null)}
          >
            친구
          </button>
        </div>
        
        <div className="flex items-center justify-between px-4 py-3">
          <div className="text-xs text-zinc-400 font-bold">다이렉트 메시지</div>
          {/* ✅ 디버깅용 새로고침 버튼 (필요시 제거) */}
          <button
            onClick={refreshDmRooms}
            className="text-xs text-zinc-500 hover:text-white transition"
            title="DM 목록 새로고침"
          >
            🔄
          </button>
        </div>

        <ul className="px-2 flex-1 overflow-y-auto">
          {/* ✅ 로딩 상태 표시 */}
          {!ready && (
            <li className="px-3 py-2 text-zinc-500 text-sm">
              연결 중...
            </li>
          )}
          
          {/* ✅ DM 목록이 비어있을 때 */}
          {ready && dmRooms.length === 0 && (
            <li className="px-3 py-2 text-zinc-500 text-sm">
              DM 목록이 없습니다
            </li>
          )}

          {/* ✅ DM 목록 렌더링 */}
          {dmRooms
            ?.filter(room => room.visible)
            .map((room) => (
            <li
              key={room.id}
              className="px-3 py-2 rounded group flex items-center justify-between hover:bg-zinc-800 cursor-pointer transition"
              onClick={() => onSelectDMRoom(room.id)}
            >         
              <span className="text-base truncate flex-1">
                {room?.name || "이름없음"}
              </span>
              <button
                className="dm-delete-btn text-zinc-400 hover:text-red-500 opacity-0 group-hover:opacity-100 transition ml-2 flex-shrink-0"
                onClick={(e) => {
                  e.stopPropagation();
                  handleDeleteDmRoom(room.id);
                }}
                title="DM 삭제"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="h-4 w-4"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                  strokeWidth={2}
                >
                  <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>  
            </li>
          ))}
        </ul>
      </div>
    );
  }

  return (
    <div className="w-[260px] min-w-[200px] max-w-[320px] h-full bg-[#2b2d31] flex flex-col border-r border-[#232428]">
      <div className="flex-1 flex flex-col">
        {/* 텍스트 채널 */}
        <div className="flex items-center justify-between px-4 mt-4 mb-1">
          <span className="text-xs text-zinc-400 font-bold">텍스트 채널</span>
          <button
            className="text-xs text-[#3ba55d] hover:text-white bg-[#232428] rounded px-2 py-1 ml-2"
            onClick={() => {
              setNewType("TEXT");
              setShowCreate(true);
            }}
            title="채널 생성"
          >＋</button>
        </div>
        <ul className="mb-3 px-2">
          {textChannels.length === 0 && <div className="text-zinc-500 px-2 py-2">없음</div>}
          {textChannels.map((ch, i) => (
            <li
              key={ch.id ?? `textch-${i}`}
              className="flex items-center gap-2 px-2 py-2 rounded hover:bg-zinc-800 group cursor-pointer transition"
              onClick={() => onSelectChannel && onSelectChannel(ch.id)}
            >
              <span className="text-[#8e9297] font-bold">#</span>
              <span className="flex-1">{ch?.name || "이름없음"}</span>
              <button
                className="text-xs text-zinc-500 hover:text-red-400 opacity-0 group-hover:opacity-100 transition"
                onClick={e => { e.stopPropagation(); handleDeleteChannel(ch.id); }}
                title="채널 삭제"
              >－</button>
              <button
                className="text-xs bg-zinc-700 text-white rounded px-2 py-0.5 ml-1"
                onClick={e => { e.stopPropagation(); handleInviteCode(ch.id); }}
              >초대</button>
            </li>
          ))}
        </ul>

        {/* 음성 채널 */}
        <div className="flex items-center justify-between px-4 mt-2 mb-1">
          <span className="text-xs text-zinc-400 font-bold">음성 채널</span>
          <button
            className="text-xs text-[#3ba55d] hover:text-white bg-[#232428] rounded px-2 py-1 ml-2"
            onClick={() => {
              setNewType("VOICE");
              setShowCreate(true);
            }}
            title="음성 채널 생성"
          >＋</button>
        </div>
        <ul className="px-2">
          {voiceChannels.length === 0 && <div className="text-zinc-500 px-2 py-2">없음</div>}
          {voiceChannels.map((ch, i) => (
            <li
              key={ch.id ?? `voicech-${i}`}
              className="flex items-center gap-2 px-2 py-2 rounded hover:bg-zinc-800 group cursor-pointer transition"
              onClick={async () => {
                console.log("🔊 음성 채널 클릭:", ch.id);
                if (onSelectChannel) onSelectChannel(ch.id);
                try {
                  await createSendTransport();
                  await sendAudio();
                  await createRecvTransport();
                  console.log("🎤 음성 채널 입장 완료:", ch.id);
                } catch (err) {
                  console.error("❌ 음성 송수신 실패:", err);
                }
              }}
            >
              <span>🔊</span>
              <span className="flex-1">{ch?.name || "이름없음"}</span>
              <button
                className="text-xs text-zinc-500 hover:text-red-400 opacity-0 group-hover:opacity-100 transition"
                onClick={e => { e.stopPropagation(); handleDeleteChannel(ch.id); }}
                title="채널 삭제"
              >－</button>
            </li>
          ))}
        </ul>

        {/* 채널 생성 모달 */}
        {showCreate && (
          <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-40">
            <div className="bg-zinc-900 p-4 rounded w-80 flex flex-col gap-2">
              <div className="text-white font-bold mb-2">채널 개설</div>
              <input
                className="p-2 rounded"
                value={newName}
                onChange={e => setNewName(e.target.value)}
                placeholder="채널명"
              />
              <div className="flex gap-2 mt-2">
                <button
                  onClick={handleCreateChannel}
                  className="flex-1 bg-blue-600 text-white rounded py-1"
                >생성</button>
                <button
                  onClick={() => setShowCreate(false)}
                  className="flex-1 bg-zinc-700 text-white rounded py-1"
                >취소</button>
              </div>
            </div>
          </div>
        )}

        {/* 초대코드 모달 */}
        {inviteCode && (
          <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50">
            <div className="bg-zinc-900 p-4 rounded w-80 flex flex-col gap-2">
              <div className="text-white font-bold mb-2">초대코드</div>
              <div className="bg-zinc-800 rounded px-4 py-2 font-mono text-xl text-center mb-2">
                {inviteCode}
              </div>
              <button
                onClick={() => navigator.clipboard.writeText(inviteCode)}
                className="bg-blue-600 text-white rounded px-3 py-1 mb-2"
              >코드 복사</button>
              <button
                onClick={closeInviteModal}
                className="bg-zinc-700 text-white rounded px-3 py-1"
              >닫기</button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}