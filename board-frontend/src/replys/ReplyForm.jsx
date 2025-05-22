import React, { useState } from "react";
import { Button } from "@/components/ui/button";
// eslint-disable-next-line no-unused-vars
const ReplyForm = ({ bno, parentRno = null, onSubmit }) => {
  const [content, setContent] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    // ...생략(기존 로직 동일)
  };

 return (
    <form
      onSubmit={handleSubmit}
      className="flex flex-col w-full gap-2"
    >
      <textarea
        value={content}
        onChange={(e) => setContent(e.target.value)}
        required
        placeholder="댓글을 입력하세요"
        className="w-full resize-none min-h-[48px] rounded-2xl border border-zinc-300 p-3 bg-white shadow focus:outline-none focus:ring-2 focus:ring-primary"
      />
      <div className="flex justify-end">
        <Button
          type="submit"
          className="rounded-xl border-zinc-300 bg-zinc-50 text-zinc-700 hover:bg-zinc-100 hover:text-zinc-900 h-9 px-5 text-sm font-semibold"
          variant="outline"
        >
          댓글 등록
        </Button>
      </div>
    </form>
  );
};

export default ReplyForm;