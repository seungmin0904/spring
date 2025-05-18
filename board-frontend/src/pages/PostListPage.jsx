import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "@/lib/axiosInstance";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

const PostListPage = () => {
  const [posts, setPosts] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    axiosInstance.get("/boards").then((res) => setPosts(res.data.content));
  }, []);

  return (
   <div className="pt-24 px-4 max-w-4xl w-full mx-auto">
  <div className="flex justify-between items-center mb-6">
    <h2 className="text-2xl font-bold">📋 게시글 목록</h2>
    <Button onClick={() => navigate("/posts/new")}>✏️ 글쓰기</Button>
  </div>

  {posts.length === 0 ? (
    <p className="text-gray-400">게시글이 없습니다.</p>
  ) : (
    <div className="space-y-2 divide-y divide-gray-200">
      {posts.map((post) => (
        <div
          key={post.bno}
          className="w-full py-4 px-2 hover:bg-gray-100 cursor-pointer"
          onClick={() => navigate(`/posts/${post.bno}`)}
        >
          <h3 className="text-lg font-semibold">{post.title}</h3>
          <p className="text-sm text-gray-500">
            {post.writerName} · {post.createdDate}
          </p>
        </div>
      ))}
    </div>
  )}
</div>
  );
};

export default PostListPage;