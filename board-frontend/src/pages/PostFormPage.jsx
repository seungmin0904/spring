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
  const [imageUrl, setImageUrl] = useState(null);
  const [attachmentFiles, setAttachmentFiles] = useState([]);

  useEffect(() => {
    if (isEdit && bno) {
      axiosInstance.get(`/boards/${bno}`).then((res) => {
        setTitle(res.data.title);

        // HTML 태그 모두 제거해서 순수 텍스트만 textarea에 넣음
        const strippedContent = res.data.content.replace(/<[^>]+>/g, "").trim();
        setContent(strippedContent);

        setImageUrl(res.data.imageUrl); // 이미지 미리보기 용
      });
    }
  }, [isEdit, bno]);

  const uploadFile = async (file) => {
    const formData = new FormData();
    formData.append("file", file);
    const res = await axiosInstance.post("/upload", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return res.data; // ex: "/uploads/xxx.png"
  };

  const handleImageChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    try {
      const uploadedUrl = await uploadFile(file);
      setImageUrl(uploadedUrl);
      toast({ title: "이미지가 본문에 추가되었습니다." });
    } catch (error) {
      console.error("이미지 업로드 실패:", error);
      toast({
        title: "이미지 업로드 실패",
        description: "파일을 업로드할 수 없습니다.",
        variant: "destructive",
      });
    }
  };

  const handleAttachmentChange = (e) => {
    setAttachmentFiles([...e.target.files]);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      let attachments = [];

      if (!isEdit && attachmentFiles.length > 0) {
        attachments = await Promise.all(attachmentFiles.map(uploadFile));
      }

      // textarea엔 태그 제거된 content만 있고, 저장 시에는 이미지 태그 포함
      let cleanedContent = content.replace(/<[^>]+>/g, "").trim();

      const finalContent = imageUrl
        ? `<img src="${import.meta.env.VITE_API_BASE_URL}${imageUrl}" />${cleanedContent}`
        : cleanedContent;

      const payload = {
        title,
        content: finalContent,
        ...(isEdit ? {} : { imageUrl, attachments }),
      };

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
    <div className="w-[600px] mx-auto pt-24">
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

              {imageUrl && (
                <img
                  src={`${import.meta.env.VITE_API_BASE_URL}${imageUrl}`}
                  alt="본문 이미지 미리보기"
                  className="max-w-full rounded border"
                />
              )}

              <Textarea
                id="content"
                placeholder="내용을 입력하세요"
                value={content}
                onChange={(e) => setContent(e.target.value)}
                rows={20}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="thumbnail">썸네일 이미지</Label>
              <Input
                type="file"
                accept="image/*"
                onChange={handleImageChange}
              />
            </div>

            {!isEdit && (
              <div className="space-y-2">
                <Label htmlFor="attachments">첨부파일</Label>
                <Input
                  type="file"
                  multiple
                  onChange={handleAttachmentChange}
                />
              </div>
            )}

            <Button type="submit" className="w-full text-lg py-6">
              {isEdit ? "수정하기" : "등록하기"}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};

export default PostFormPage;
  // <div className="w-full flex justify-center pt-24 px-4">
  //   <div className="w-full max-w-5xl">
  //     <Card className="shadow-md rounded-xl">
  //       <CardHeader>
  //         <CardTitle className="text-3xl text-center">
  //           {isEdit ? "✏️ 게시글 수정" : "📝 새 게시글 작성"}
  //         </CardTitle>
  //       </CardHeader>
  //       <CardContent>
  //         <form onSubmit={handleSubmit} className="space-y-6">
  //           <div className="space-y-2">
  //             <Label htmlFor="title">제목</Label>
  //             <Input
  //               id="title"
  //               placeholder="제목을 입력하세요"
  //               value={title}
  //               onChange={(e) => setTitle(e.target.value)}
  //             />
  //           </div>

  //           <div className="space-y-2">
  //             <Label htmlFor="content">내용</Label>
  //             <Textarea
  //               id="content"
  //               placeholder="내용을 입력하세요"
  //               value={content}
  //               onChange={(e) => setContent(e.target.value)}
  //               rows={20} // ✅ 행 수 증가
  //             />
  //           </div>

  //           <Button type="submit" className="w-full text-lg py-6">
  //             {isEdit ? "수정하기" : "등록하기"}
  //           </Button>
  //         </form>
  //       </CardContent>
  //     </Card>
  //   </div>
  // </div>


