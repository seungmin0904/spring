import React, { useEffect } from "react";
import { Link } from "react-router-dom";

const recentPosts = [
  {
    id: 1,
    title: "UX review presentations",
    description: "How to get meaningful feedback from your team",
    image: "/img1.jpg",
  },
  {
    id: 2,
    title: "Grid system for better UI",
    description: "Master the 12-column layout in CSS",
    image: "/img2.jpg",
  },
];
const allPosts = [
  {
    id: 3,
    title: "PM mental models",
    image: "/img3.jpg",
  },
  {
    id: 4,
    title: "What is Wireframing?",
    image: "/img4.jpg",
  },
  {
    id: 5,
    title: "Our top 10 Javascript frameworks",
    image: "/img5.jpg",
  },
  {
    id: 6,
    title: "Podcast: Creating a better CK Community",
    image: "/img6.jpg",
  },
];

const HomePage = () => {
  useEffect(() => {
    fetch("/api/boards?page=0&size=5")
      .then((res) => res.json())
      .catch((err) => console.error("게시글 로딩 실패", err));
  }, []);

  return (
    <div className="min-h-screen pt-24 px-4 bg-gray-50 dark:bg-[#18181b]">
      <main className="max-w-[1280px] mx-auto px-4 py-10">
        {/* 타이틀 */}
        <h1 className="text-6xl font-black tracking-tight mb-10 text-gray-900 dark:text-white">
          THE BLOG
        </h1>
        {/* Recent Posts */}
        <section>
          <h2 className="text-2xl font-bold mb-4 text-gray-900 dark:text-white">Recent blog posts</h2>
          <div className="grid md:grid-cols-2 gap-8 mb-10">
            {recentPosts.map((post) => (
              <Link key={post.id} to={`/posts/${post.id}`}>
                <div className="group rounded-2xl overflow-hidden bg-white dark:bg-zinc-900 shadow hover:shadow-lg transition border border-zinc-200 dark:border-zinc-700">
                  <img src={post.image} alt={post.title} className="w-full h-56 object-cover group-hover:scale-105 transition" />
                  <div className="p-6">
                    <div className="text-lg font-semibold mb-2 text-gray-900 dark:text-white">{post.title}</div>
                    <div className="text-gray-500 dark:text-gray-300 text-sm">{post.description}</div>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </section>
        {/* All Blog Posts */}
        <section>
          <h2 className="text-2xl font-bold mb-4 text-gray-900 dark:text-white">All blog posts</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
            {allPosts.map((post) => (
              <Link key={post.id} to={`/posts/${post.id}`}>
                <div className="rounded-xl overflow-hidden bg-white dark:bg-zinc-900 shadow hover:shadow-lg transition border border-zinc-200 dark:border-zinc-700">
                  <img src={post.image} alt={post.title} className="w-full h-40 object-cover" />
                  <div className="p-4">
                    <div className="font-semibold text-gray-800 dark:text-white">{post.title}</div>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </section>
        {/* Footer */}
        <footer className="mt-16 text-sm text-gray-400 dark:text-gray-500 text-center border-t border-gray-200 dark:border-gray-800 py-8">
          &copy; 2024 Simple Blog. All rights reserved.
        </footer>
      </main>
    </div>
  );
};

export default HomePage;
