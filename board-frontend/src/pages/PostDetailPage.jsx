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
    <div className="pt-24 px-4 max-w-3xl mx-auto space-y-6">
      <Card>
        <CardHeader>
          <CardTitle className="text-2xl">{board.title}</CardTitle>
          <p className="text-sm text-zinc-500">작성자: {board.writerName}</p>
          <p className="text-xs text-zinc-400">
            작성일: {new Date(board.createdDate).toLocaleString("ko-KR", {
              year: "numeric",
              month: "2-digit",
              day: "2-digit",
              hour: "2-digit",
              minute: "2-digit",
            })}
          </p>
        </CardHeader>
        <CardContent className="text-zinc-800 whitespace-pre-line">
          {board.content}
        </CardContent>
      </Card>

      {board.writerName === name && (
        <div className="flex gap-2 justify-end">
          <Button variant="outline" onClick={() => navigate(`/posts/${bno}/edit`)}>
            수정
          </Button>
          <Button variant="destructive" onClick={handleDelete}>
            삭제
          </Button>
        </div>
      )}

      <ReplyList bno={bno} />
    </div>
  );
};

export default PostDetailPage;
