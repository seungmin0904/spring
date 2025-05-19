// src/pages/PostDetailPage.jsx
import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axiosInstance from "@/lib/axiosInstance";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { useToast } from "@/hooks/use-toast";
import ReplyList from "../replys/ReplyList";

const PostDetailPage = ({ name }) => {
  const { bno } = useParams();
  const navigate = useNavigate();
  const { toast } = useToast();
  const [data, setData] = useState(null);

  useEffect(() => {
    axiosInstance.get(`/boards/${bno}/full`).then((res) => {
      setData(res.data);
    });
  }, [bno]);

  const handleDelete = async () => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;

    try {
      await axiosInstance.delete(`/boards/${bno}`);
      toast({ title: "삭제 완료", description: "게시글이 삭제되었습니다." });
      navigate("/posts");
    } catch (error) {
      console.error(error);
      toast({
        title: "삭제 실패",
        description: "서버 오류가 발생했습니다.",
        variant: "destructive",
      });
    }
  };

  if (!data) return <div className="pt-24 text-center text-zinc-500">불러오는 중...</div>;

  const { board } = data;

  return (
    
  <div className="min-h-screen bg-gray-50 px-4 py-10">
    {/* 중앙 고정 카드 */}
    <div className="w-[1200px] mx-auto">
      <Card className="h-[900px] w-full shadow-lg flex flex-col">
        <CardHeader>
          <div className="mb-2 space-y-1">
            <CardTitle className="text-3xl font-bold">{board.title}</CardTitle>
            <div className="text-sm text-zinc-500 flex justify-between items-center">
              <span> 작성자 : {board.writerName}</span>
              <span className="text-xs text-zinc-400">
                {new Date(board.createdDate).toLocaleString("ko-KR", {
                  year: "numeric",
                  month: "2-digit",
                  day: "2-digit",
                  hour: "2-digit",
                  minute: "2-digit",
                })}
              </span>
            </div>
          </div>
        </CardHeader>

        <CardContent className="flex-1 overflow-y-auto text-black whitespace-pre-line text-lg leading-relaxed px-8 py-6">
          {board.content}
        </CardContent>
      </Card>

      {/* 버튼들 */}
      {board.writerName === name && (
        <div className="flex justify-end gap-2 mt-4">
          <Button variant="outline" onClick={() => navigate(`/posts/${bno}/edit`)}>수정</Button>
          <Button variant="destructive" onClick={handleDelete}>삭제</Button>
        </div>
      )}

      {/* 댓글 리스트 */}
      <div className="mt-10">
        <ReplyList bno={bno} />
      </div>
    </div>
  </div>
);
};

export default PostDetailPage;
