import { useEffect, useState } from "react";
import axios from "@/lib/axiosInstance";

const FriendListPage = () => {
  const [friends, setFriends] = useState([]);

  useEffect(() => {
    const fetchFriends = async () => {
      try {
        const response = await axios.get("/members/all");
        setFriends(response.data);
      } catch (error) {
        console.error("친구 목록 불러오기 실패:", error);
      }
    };

    fetchFriends();
  }, []);

  return (
    <div className="max-w-xl mx-auto mt-10">
      <h1 className="text-2xl font-bold mb-4">로그인한 사용자 목록</h1>
      {friends.length === 0 ? (
        <p>아직 사용자 목록이 없습니다.</p>
      ) : (
        <ul className="list-disc pl-4">
          {friends.map((friend) => (
            <li key={friend.mno}>{friend.name} ({friend.username})</li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default FriendListPage;