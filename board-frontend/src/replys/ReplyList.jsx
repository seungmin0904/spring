import React, { useState, useEffect, useCallback } from "react";
import ReplyItem from "./ReplyItem";
import ReplyForm from "./ReplyForm";

const ReplyList = ({ bno }) => {
  const [replies, setReplies] = useState([]);

  const fetchReplies = useCallback(async () => {
    try {
      const token = localStorage.getItem("token");

      const res = await fetch(`/api/replies?bno=${bno}`, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      if (!res.ok) {
        throw new Error(`댓글 목록 요청 실패: ${res.status}`);
      }

      const data = await res.json();

      if (Array.isArray(data)) {
        setReplies(data);
      } else {
        console.warn("댓글 응답이 배열이 아님:", data);
        setReplies([]);
      }
    } catch (error) {
      console.error("댓글 목록 불러오기 오류:", error);
      setReplies([]); // fallback: 빈 배열로 초기화
    }
  }, [bno]);

  useEffect(() => {
    fetchReplies();
  }, [fetchReplies]);

  const countTotalReplies = (list) =>
    list.reduce(
      (sum, reply) =>
        sum + 1 + (reply.children ? countTotalReplies(reply.children) : 0),
      0
    );

  const totalCount = countTotalReplies(replies);

  return (
    <div className="mt-8 space-y-4">
      <ReplyForm bno={bno} onSubmit={fetchReplies} />
    <div>
        <h2 className="text-lg font-semibold">💬 댓글</h2>
      <h3 className="font-bold mb-2">댓글 {totalCount}개</h3>
   </div>
   

      <div className="mt-4 space-y-3">
        {replies.map((reply) => (
          <ReplyItem
            key={reply.rno}
            reply={reply}
            bno={bno}
            refresh={fetchReplies}
          />
        ))}
      </div>
    </div>
  );
};

export default ReplyList;