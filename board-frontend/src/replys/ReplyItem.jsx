import React, { useState } from "react";
import ReplyForm from "./ReplyForm";

const ReplyItem = ({ reply, bno, refresh, depth = 0 }) => {
  const [showReplyForm, setShowReplyForm] = useState(false);
  const [editing, setEditing] = useState(false);
  const [editedText, setEditedText] = useState(reply.text);
  const currentUser = localStorage.getItem("username");
  console.log("reply.replyer:", reply.replyer);
  console.log("currentUser:", currentUser);
  const handleDelete = async () => {
    if (!window.confirm("댓글을 삭제하시겠습니까?")) return;

    try {
      const token = localStorage.getItem("token");
      const res = await fetch(`/api/replies/${reply.rno}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (res.ok) {
        refresh();
      } else {
        alert("삭제 실패");
      }
    } catch (err) {
      console.error(err);
      alert("서버 오류");
    }
  };

  const handleEditSubmit = async () => {
    try {
      const token = localStorage.getItem("token");
      const res = await fetch(`/api/replies/${reply.rno}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ text: editedText }),
      });

      if (res.ok) {
        setEditing(false);
        refresh();
      } else {
        alert("수정 실패");
      }
    } catch (err) {
      console.error(err);
      alert("서버 오류");
    }
  };

  return (
    <div
      className={`mt-2 ${depth === 0 ? "border-l border-zinc-300" : ""}`}
      style={{ marginLeft: `${depth * 12}px`, paddingLeft: "12px" }}
    >
      {editing ? (
        <div className="space-y-1">
          <textarea
            className="w-full p-1 border text-sm"
            value={editedText}
            onChange={(e) => setEditedText(e.target.value)}
          />
          <div className="space-x-2">
            <button
              onClick={handleEditSubmit}
              className="text-xs text-blue-600 hover:underline"
            >
              저장
            </button>
            <button
              onClick={() => setEditing(false)}
              className="text-xs text-gray-500 hover:underline"
            >
              취소
            </button>
          </div>
        </div>
      ) : (
      <p className="text-sm">
        <strong>{reply.replyer}</strong>: {reply.text}
      </p>
      )}

      <div className="flex items-center gap-2 mt-1">
      <button
        onClick={() => setShowReplyForm(!showReplyForm)}
        className="text-xs text-blue-500 hover:underline mt-1"
      >
        답글 달기
      </button>

      {reply.username === currentUser && !editing && (
          <>
            <button
              onClick={() => setEditing(true)}
              className="text-xs text-green-600 hover:underline"
            >
              수정
            </button>
            <button
              onClick={handleDelete}
              className="text-xs text-red-600 hover:underline"
            >
              삭제
            </button>
          </>
        )}
      </div>
      {showReplyForm && (
        <div className="mt-2">
          <ReplyForm bno={bno} parentRno={reply.rno} onSubmit={refresh} />
        </div>
      )}

      <div className="mt-2 space-y-2">
        {reply.children?.map((child) => (
          <ReplyItem
            key={child.rno}
            reply={child}
            bno={bno}
            refresh={refresh}
            depth={depth + 1} // 💡 자식일수록 들여쓰기 깊어짐
          />
        ))}
      </div>
    </div>
  );
};

export default ReplyItem;
