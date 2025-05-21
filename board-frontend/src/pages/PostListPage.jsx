import { useEffect, useState,useCallback } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "@/lib/axiosInstance";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";

const PostListPage = () => {
  const navigate = useNavigate();

  const [posts, setPosts] = useState([]);
  const [type, setType] = useState("t");
  const [keyword, setKeyword] = useState("");
  const [page, setPage] = useState(1);
  const [pageInfo, setPageInfo] = useState(null);

  const fetchPosts = useCallback(async () => {
    try {
      const res = await axiosInstance.get("/boards", {
        params: {
          type,
          keyword,
          page,
          size: 10,
          sort: "DESC",
        },
      });
      setPosts(res.data.content);
      setPageInfo({
        page: res.data.page,
        totalPages: res.data.totalPages,
        isFirst: res.data.isFirst,
        isLast: res.data.isLast,
      });
    } catch (error) {
      console.error("게시글 불러오기 실패", error);
    }
  },[type, keyword, page]);
  
  useEffect(() => {
    fetchPosts(type, keyword, page);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  const handleSearch = (e) => {
    e.preventDefault();
    
    if (!keyword.trim()) {
    alert("검색어를 입력하세요.");
    return;
  }
    setPage(1); // 검색 시 첫 페이지로
    fetchPosts();
  };

  return (
   <div className="pt-24 px-4 max-w-4xl w-full mx-auto">
  <div className="flex justify-between items-center mb-6">
    <h2 className="text-2xl font-bold">📋 게시글 목록</h2>
    <Button onClick={() => navigate("/posts/new")}>✏️ 글쓰기</Button>
  </div>

    {/* 검색 폼 */}
      <form onSubmit={handleSearch} className="flex gap-2 mb-6">
        <select
          value={type}
          onChange={(e) => setType(e.target.value)}
          className="border px-2 py-1 rounded"
        >
          <option value="t">제목</option>
          <option value="c">내용</option>
          <option value="w">작성자</option>
          <option value="tc">제목+내용</option>
        </select>
        <Input
          className="w-60"
          placeholder="검색어를 입력하세요"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
        />
        <Button type="submit">🔍 검색</Button>
      </form>
    
  {/* 게시글 목록 */}
  {posts.length === 0 ? (
    <p className="text-gray-400">게시글이 없습니다.</p>
  ) : (
    <div className="space-y-2 divide-y divide-gray-200">
      {posts.map((post) => (
  <div
    key={post.bno}
    className="w-full py-4 px-2 flex gap-4 hover:bg-gray-100 cursor-pointer"
    onClick={() => navigate(`/posts/${post.bno}`)}
  >
    {post.imageUrl && post.imageUrl.trim() !== "" ? (
      <img
        src={`${import.meta.env.VITE_API_BASE_URL}${post.imageUrl}?v=${Date.now()}`}
        alt="썸네일"
        className="w-32 h-24 object-cover rounded border"
      />
    ) : (
      <div className="w-32 h-24 bg-white-200 text-white-500 flex items-center justify-center text-sm rounded border">
        
      </div>
    )}

    <div className="flex-1">
      <h3 className="text-lg font-semibold">{post.title}</h3>
      <p className="text-sm text-gray-500">
        {post.writerName} · {post.createdDate}
      </p>
    </div>
  </div>
      ))}
    </div>
  )}

   {/* 페이지네이션 */}
      {pageInfo && (
        <div className="flex justify-center gap-4 mt-8">
          <Button
            onClick={() => setPage((prev) => prev - 1)}
            disabled={pageInfo.isFirst}
          >
            ◀ 이전
          </Button>
          <span className="text-sm text-gray-600">
            {pageInfo.page + 1} / {pageInfo.totalPages}
          </span>
          <Button
            onClick={() => setPage((prev) => prev + 1)}
            disabled={pageInfo.isLast}
          >
            다음 ▶
          </Button>
        </div>
      )}

</div>
  );
};

export default PostListPage;