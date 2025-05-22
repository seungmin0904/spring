import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axiosInstance from "@/lib/axiosInstance";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Card, CardHeader, CardTitle } from "@/components/ui/card";
import { useToast } from "@/hooks/use-toast";
import { EditorContent, useEditor } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import Image from "@tiptap/extension-image";
import "@/tiptap.css";
import CustomPlaceholder from '@/extensions/CustomPlaceholder';

const PostFormPage = ({ isEdit = false }) => {
  const { bno } = useParams();
  const navigate = useNavigate();
  const { toast } = useToast();

  const [title, setTitle] = useState("");
  const [attachmentFiles, setAttachmentFiles] = useState([]);

  const editor = useEditor({
  extensions: [
    StarterKit,
    Image.configure({ inline: false }),
    CustomPlaceholder.configure({
      placeholder:'내용을 입력하세요.',
      emptyEditorClass: 'is-editor-empty',
      showOnlyWhenEditable: true,
      showOnlyCurrent: false,
    })
  ],
  content: '',
})

  // 수정 모드일 때 게시글 불러오기
  useEffect(() => {
    if (isEdit && bno) {
      axiosInstance.get(`/boards/${bno}`).then((res) => {
        const { title, content } = res.data;
        setTitle(title);
        if (editor) editor.commands.setContent(content);
      });
    }
  }, [isEdit, bno, editor]);

  // 파일 업로드 처리
  const uploadFile = async (file) => {
    const formData = new FormData();
    formData.append("file", file);
    const res = await axiosInstance.post("/upload", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return res.data;
  };

  // 이미지 삽입
  const handleImageChange = async (e) => {
    const file = e.target.files[0];
    if (!file || !editor) return;

    try {
      const uploadedUrl = await uploadFile(file);
      const fullUrl = `${import.meta.env.VITE_API_BASE_URL}${uploadedUrl}`;
      editor.commands.insertContent({
        type: "image",
        attrs: {
          src: fullUrl,
          alt: "본문 이미지",
        },
      });
      toast({ title: "이미지가 본문에 삽입되었습니다." });
    } catch (error) {
      console.error("이미지 업로드 실패:", error);
      toast({
        title: "이미지 업로드 실패",
        description: "파일을 업로드할 수 없습니다.",
        variant: "destructive",
      });
    }
  };

  // 첨부파일 변경
  const handleAttachmentChange = (e) => {
    setAttachmentFiles([...e.target.files]);
  };

  // 제출 처리
  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      let attachments = [];

      if (!isEdit && attachmentFiles.length > 0) {
        attachments = await Promise.all(attachmentFiles.map(uploadFile));
      }

      const payload = {
        title,
        content: editor?.getHTML(),
        ...(isEdit ? {} : { attachments }),
      };

      if (isEdit && bno) {
        await axiosInstance.put(`/boards/${bno}`, payload);
        toast({ title: "수정 완료" });
        navigate(`/posts/${bno}`);
        window.location.reload();
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
    <div className="min-h-screen bg-gray-50 py-16 px-4 flex flex-col">
  <div className="w-full min-w-[1000px] max-w-[1200px] mx-auto flex flex-col flex-1">
    <div className="flex flex-col flex-1 bg-white shadow-lg rounded-xl p-10 overflow-auto border border-zinc-200">
      <CardHeader>
        <CardTitle className="text-3xl font-bold text-left">
          {isEdit ? "✏️ 게시글 수정" : "📝 새 게시글 작성"}
        </CardTitle>
      </CardHeader>

      <form
        onSubmit={handleSubmit}
        className="flex flex-col flex-1 space-y-6 mt-6"
      >
        {/* 제목 */}
        <div className="flex-none">
          <Label htmlFor="title">제목</Label>
          <Input
            id="title"
            placeholder="제목을 입력하세요"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="w-full"
          />
        </div>

        {/* 내용 */}
        <div className="flex-1 flex flex-col">
          <Label htmlFor="content">내용</Label>
          <div className="flex-1">
            <EditorContent editor={editor}
            className="tiptap-editor relative w-full h-full min-h-[700px] border border-zinc-300 rounded-md p-4"
            onClick={() => editor?.commands.focus()}
            />
          </div>
        </div>

        {/* 이미지 업로드 */}
        <div className="flex-none">
          <Label htmlFor="image">이미지 업로드</Label>
          <Input
            type="file"
            accept="image/*"
            onChange={handleImageChange}
            className="w-full"
          />
        </div>

        {/* 첨부파일 */}
        {!isEdit && (
          <div className="flex-none">
            <Label htmlFor="attachments">첨부파일</Label>
            <Input
              type="file"
              multiple
              onChange={handleAttachmentChange}
              className="w-full"
            />
          </div>
        )}

        {/* 버튼 */}
        <div className="flex justify-end">
          <Button type="submit" className="text-lg">
            {isEdit ? "수정하기" : "등록하기"}
          </Button>
        </div>
      </form>
    </div>
  </div>
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


