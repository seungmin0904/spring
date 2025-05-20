import { useEffect, useState } from "react";
import axiosInstance from "@/lib/axiosInstance"; // ← 토큰 포함 axios 인스턴스
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useUser } from "@/context/UserContext";

const MyPage = () => {
  const [nickname, setNickname] = useState("");
  const [originalNickname, setOriginalNickname] = useState("");
  const [isAvailable, setIsAvailable] = useState(null);
  const [message, setMessage] = useState("");
  const { setName } = useUser(); 

  // 현재 사용자 닉네임 불러오기
  useEffect(() => {
    axiosInstance.get("/members/me").then((res) => {
      setNickname(res.data.nickname || "");
      setOriginalNickname(res.data.nickname);
    });
  }, []);

  const checkNickname = async () => {
    if (!nickname) return;
    try {
      const res = await axiosInstance.get("/members/check-nickname", {
        params: { nickname },
      });

      if (nickname === originalNickname) {
        setIsAvailable(true);
        setMessage("현재 사용 중인 닉네임입니다.");
      } else if (res.data === true) {
        setIsAvailable(false);
        setMessage("이미 사용 중인 닉네임입니다.");
      } else {
        setIsAvailable(true);
        setMessage("사용 가능한 닉네임입니다.");
      }
    } catch (e) {
      console.error(e);
    }
  };

  const handleSave = async () => {
    if (!isAvailable) return alert("닉네임 중복 확인이 필요합니다.");

    try {
      await axiosInstance.put("/members/nickname", { nickname });
      setOriginalNickname(nickname);
      setNickname(nickname);
      setName(nickname);
      alert("닉네임이 변경되었습니다.");
    } catch (e) {
        console.log(e)
      alert("변경 실패");
    }
  };

  return (
    <div className="max-w-md mx-auto mt-24 space-y-6">
      <h2 className="text-2xl font-bold">마이페이지</h2>

      <div className="space-y-2">
        <h3>닉네임 변경</h3>
        <Input
          value={nickname}
          onChange={(e) => {
            setNickname(e.target.value);
            setIsAvailable(null);
            setMessage("");
          }}
        />
        <Button variant="outline" onClick={checkNickname}>
          중복 확인
        </Button>
        {message && (
          <p className={`text-sm ${isAvailable ? "text-green-500" : "text-red-500"}`}>
            {message}
          </p>
        )}
      </div>

      <Button onClick={handleSave} disabled={isAvailable !== true}>
        저장
      </Button>
    </div>
  );
};

export default MyPage;