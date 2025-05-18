// src/pages/PostFormPage.jsx
import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axiosInstance from "@/lib/axiosInstance";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { useToast } from "@/hooks/use-toast";

const PostFormPage = ({ isEdit = false }) => {
  const { bno } = useParams();
  const navigate = useNavigate();
  const { toast } = useToast();

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");

  useEffect(() => {
    if (isEdit && bno) {
      axiosInstance.get(`/boards/${bno}`).then((res) => {
        setTitle(res.data.title);
        setContent(res.data.content);
      });
    }
  }, [isEdit, bno]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = { title, content };

    try {
      if (isEdit && bno) {
        await axiosInstance.put(`/boards/${bno}`, payload);
        toast({ title: "수정 완료" });
        navigate(`/posts/${bno}`);
      } else {
        const res = await axiosInstance.post("/boards", payload);
        toast({ title: "등록 완료" });
        navigate(`/posts/${res.data.bno}`);
      }
    } catch (error) {
      console.error(error);
      toast({
        title: "저장 실패",
        description: "서버 오류 발생",
        variant: "destructive",
      });
    }
  };

  return (
    <div className="max-w-3xl mx-auto">
      <Card className="shadow-md rounded-xl">
        <CardHeader>
          <CardTitle className="text-2xl text-center">
            {isEdit ? "✏️ 게시글 수정" : "📝 새 게시글 작성"}
          </CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-2">
              <Label htmlFor="title">제목</Label>
              <Input
                id="title"
                placeholder="제목을 입력하세요"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="content">내용</Label>
              <Textarea
                id="content"
                placeholder="내용을 입력하세요"
                value={content}
                onChange={(e) => setContent(e.target.value)}
                rows={12}
              />
            </div>

            <Button type="submit" className="w-full">
              {isEdit ? "수정하기" : "등록하기"}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};

export default PostFormPage;
