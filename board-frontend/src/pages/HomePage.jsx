import React, { useEffect, useState } from "react";
import Navbar from "../components/ui/Navbar";
import PostPreviewList from "../components/ui/PostPreviewList";

const HomePage = () => {
  const [recentPosts, setRecentPosts] = useState([]);

  useEffect(() => {
    fetch("/api/boards/recent")
      .then((res) => res.json())
      .then((data) => setRecentPosts(data))
      .catch((err) => console.error("게시글 로딩 실패", err));
  }, []);

  return (
    <div>
      {/* ✅ 고정된 상단 Navbar */}
      <Navbar />

      {/* ✅ 본문 내용 */}
      <main className="pt-24 px-4 max-w-5xl mx-auto">
        <header className="text-center py-16 bg-gray-100 rounded-xl shadow mb-10">
          <h1 className="text-4xl font-bold mb-2 text-blue-800">📌 Welcome to My Board</h1>
          <p className="text-gray-600 text-lg">자유롭게 글을 쓰고 소통해보세요</p>
        </header>

        <section>
          <h2 className="text-2xl font-semibold mb-4">🆕 최근 게시글</h2>
          <PostPreviewList posts={recentPosts} />
        </section>
      </main>
    </div>
  );
};

export default HomePage;