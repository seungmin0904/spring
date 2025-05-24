import { useForm } from "react-hook-form";
import { Input } from "@/components/ui/Input";
import { Button } from "@/components/ui/Button";
import { useToast } from "@/hooks/use-toast";
import axiosInstance from "@/lib/axiosInstance";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import FindAccountModal from "@/components/ui/FindAccountModal";

const LoginPage = ({ onLogin }) => {
  const { register, handleSubmit } = useForm();
  const { toast } = useToast();
  const navigate = useNavigate();
  const [findMode, setFindMode] = useState(null);

  const onSubmit = async (data) => {
    try {
      const response = await axiosInstance.post("/members/login", {
        name: data.name,          // 아이디로 로그인
        password: data.password,
      });

      const result = response.data;

      if (result.token && result.name) {
        localStorage.setItem("token", result.token);
        localStorage.setItem("name", result.name);         // 로그인 아이디
        localStorage.setItem("username", result.username); // 이메일 (정보찾기/알림용)

        toast({
          title: "로그인 성공 🎉",
          description: `${result.name}님 환영합니다!`,
        });

        onLogin(result.token);
        navigate("/");
      } else {
        throw new Error("서버가 사용자 정보를 반환하지 않았습니다.");
      }
    } catch (error) {
      if (error.response?.status === 401 || error.response?.status === 403) {
        toast({
          title: "인증 오류",
          description: "아이디 또는 비밀번호가 올바르지 않습니다.",
          variant: "destructive",
        });
      } else {
        toast({
          title: "로그인 실패",
          description: error.message || "예상치 못한 오류가 발생했습니다.",
          variant: "destructive",
        });
      }
      onLogin(null);
      console.error("로그인 에러:", error);
    }
  };

  return (
    <div className="max-w-sm mx-auto mt-20 p-6 border rounded-xl shadow">
      <h2 className="text-xl font-bold mb-4">로그인</h2>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <Input
          placeholder="아이디"
          type="text"
          {...register("name", { required: true })}
        />
        <Input
          placeholder="비밀번호"
          type="password"
          {...register("password", { required: true })}
        />
        <Button type="submit" className="w-full">
          로그인
        </Button>
      </form>

      {/* 아이디/비번 찾기 버튼 영역 */}
      <div className="flex justify-between mt-4 text-sm">
        <button
          className="text-blue-500 hover:underline"
          onClick={() => setFindMode("id")}
          type="button"
        >
          아이디 찾기
        </button>
        <button
          className="text-blue-500 hover:underline"
          onClick={() => setFindMode("pw")}
          type="button"
        >
          비밀번호 찾기
        </button>
      </div>

      {/* 모달 (조건부 렌더) */}
      {findMode && (
        <FindAccountModal mode={findMode} onClose={() => setFindMode(null)} />
      )}
    </div>
  );
};

export default LoginPage;