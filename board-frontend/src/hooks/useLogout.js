import { useNavigate } from "react-router-dom";
import axiosInstance from "@/lib/axiosInstance";
import { useToast } from "@/hooks/use-toast"; // optional

export const useLogout = () => {
  const navigate = useNavigate();
  const { toast } = useToast(); // optional

  return async () => {
    try {
      await axiosInstance.post("/members/logout");

      // 로컬 저장소 초기화
      localStorage.removeItem("token");
      localStorage.removeItem("refresh_token");
      localStorage.removeItem("username");
      localStorage.removeItem("name");

      toast({
        title: "로그아웃 완료",
        description: "정상적으로 로그아웃되었습니다.",
      });

      navigate("/login");
    } catch (error) {
      console.error("🚫 로그아웃 실패:", error);
      toast({
        title: "로그아웃 실패",
        description: "서버 요청 중 문제가 발생했습니다.",
        variant: "destructive",
      });
    }
  };
};
