import React, { useState } from "react";

const ReplyForm = ({ bno, parentRno = null, onSubmit }) => {
  const [content, setContent] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem("token");

    if (!token) {
      alert("로그인 후 댓글을 작성할 수 있습니다.");
      return;
    }

    try {
      const response = await fetch("/api/replies", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          bno,
          text: content,     // ✅ 중요! 필드명 맞춰야 함
          parentRno,
        }),
      });

      if (!response.ok) {
        const error = await response.json().catch(() => ({}));
        console.error("댓글 등록 실패:", error);
        alert(`댓글 등록 실패: ${response.status}`);
        return;
      }

      setContent("");
      onSubmit(); // 등록 후 목록 다시 불러오기
    } catch (err) {
      console.error("댓글 등록 중 오류:", err);
      alert("서버 오류로 댓글을 등록할 수 없습니다.");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-2">
      <textarea
        value={content}
        onChange={(e) => setContent(e.target.value)}
        required
        placeholder="댓글을 입력하세요"
        className="w-full p-2 border rounded"
      />
      <button type="submit" className="px-4 py-1 border rounded">댓글 등록</button>
    </form>
  );
};

export default ReplyForm;