// src/pages/LoginPage.jsx
import { useForm } from "react-hook-form";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useToast } from "@/hooks/use-toast";
import axiosInstance from "@/lib/axiosInstance";
import { useNavigate } from "react-router-dom";


const LoginPage = ({ onLogin }) => {
  const { register, handleSubmit } = useForm();
  const { toast } = useToast();
   const navigate = useNavigate();
  const onSubmit = async (data) => {
  try {
    const response = await axiosInstance.post("/members/login", {
      username: data.email,
      password: data.password,
    });

    const result = response.data;

    if (result.token && result.username) {
      localStorage.setItem("token", result.token);
      localStorage.setItem("username", result.username);
      localStorage.setItem("name", result.name); // 선택

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
        description: "이메일 또는 비밀번호가 올바르지 않습니다.",
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
          placeholder="이메일"
          type="email"
          {...register("email", { required: true })}
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
    </div>
  );
};

export default LoginPage;