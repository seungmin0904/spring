import React, { useState } from "react";
import ReplyForm from "./ReplyForm";

const ReplyItem = ({ reply, bno, refresh, depth = 0 }) => {
  const [showReplyForm, setShowReplyForm] = useState(false);

  return (
    <div
      className={`mt-2 ${depth === 0 ? "border-l border-zinc-300" : ""}`}
      style={{ marginLeft: `${depth * 12}px`, paddingLeft: "12px" }}
    >
      <p className="text-sm">
        <strong>{reply.replyer}</strong>: {reply.text}
      </p>

      <button
        onClick={() => setShowReplyForm(!showReplyForm)}
        className="text-xs text-blue-500 hover:underline mt-1"
      >
        답글 달기
      </button>

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
