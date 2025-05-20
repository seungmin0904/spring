import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "@/lib/axiosInstance";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";



const RegisterPage = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    username: "",
    password: "",
    name: "",
    code: "",
  });

  const [error, setError] = useState(null);
  const [codeSent, setCodeSent] = useState(false);
  const [verified, setVerified] = useState(false);
  const [expiryDate, setExpiryDate] = useState(null);
  const [secondsLeft, setSecondsLeft] = useState(null);
  const [isAvailable, setIsAvailable] = useState(null); // 닉네임 중복 확인 상태
  const [message, setMessage] = useState("");



  const handleChange = (e) => {
    setForm((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));

    if (e.target.name === "name") {
      setIsAvailable(null); // 닉네임 바꾸면 중복 상태 초기화
      setMessage("");
    }
  };

  const checkNickname = async () => {
    if (!form.name) return;
    try {
      const res = await axiosInstance.get("/members/check-nickname", {
        params: { nickname: form.name },
      });

      if (res.data === true) {
        setIsAvailable(false);
        setMessage("이미 사용 중인 닉네임입니다.");
      } else {
        setIsAvailable(true);
        setMessage("사용 가능한 닉네임입니다.");
      }
    } catch (e) {
      console.error(e);
      setIsAvailable(null);
      setMessage("중복 확인 실패");
    }
  };

  const handleSendCode = async () => {
    try {
     const res = await axiosInstance.post("/auth/email/send", {
        username: form.username,
      });
      alert("인증코드가 전송되었습니다.");
      setCodeSent(true);
      setVerified(false);
      setExpiryDate(res.data.expiryDate);
    } catch (err) {
      alert("코드 전송 실패: " + (err.response?.data?.error || "에러"));
    }
  };

  const handleVerifyCode = async () => {
    try {
      const res = await axiosInstance.post("/auth/email/verify", {
        username: form.username,
        code: form.code,
      });
      alert(res.data || "인증 성공");
      setVerified(true);
    } catch (err) {
      alert("인증 실패: " + (err.response?.data?.error || "에러"));
    }
  };


  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    if (!verified) {
      alert("이메일 인증을 완료해주세요.");
      return;
    }

    if (!isAvailable) {
      alert("닉네임 중복 확인을 완료해주세요.");
      return;
    }

    try {
      await axiosInstance.post("/members/register", form);
      alert("회원가입 완료!");
      navigate("/login");
    } catch (err) {
      const message = err.response?.data?.error || "회원가입 실패";
      setError(message);
    }
  };

  useEffect(() => {
    if (!expiryDate) return;

    const end = new Date(expiryDate).getTime();

    const interval = setInterval(() => {
      const now = Date.now();
      const remaining = Math.max(0, Math.floor((end - now) / 1000));
      setSecondsLeft(remaining);

      if (remaining <= 0) {
        clearInterval(interval);
        setCodeSent(false); // 인증 입력창 숨기기
        alert("⏰ 인증 시간이 만료되었습니다. 다시 요청해주세요.");
      }
    }, 1000);
    return () => clearInterval(interval);
  }, [expiryDate]);

  return (
    <div className="pt-24 px-4 max-w-md mx-auto">
      <Card>
        <CardHeader>
          <CardTitle>회원가입</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <Label htmlFor="name">닉네임</Label>
              <Input
                id="name"
                name="name"
                value={form.name}
                onChange={handleChange}
                required
              />
              <Button
                type="button"
                onClick={checkNickname}
                className="mt-2"
              >
                중복 확인
              </Button>
              {message && (
                <p className={`text-sm ${isAvailable ? "text-green-500" : "text-red-500"}`}>
                  {message}
                </p>
              )}
            </div>
            <div>
              <Label htmlFor="username">이메일</Label>
              <Input
                id="username"
                name="username"
                type="email"
                value={form.username}
                onChange={handleChange}
                required
              />
              <Button
                type="button"
                onClick={handleSendCode}
                className="mt-2"
                disabled={!form.username}
              >
                인증코드 발송
              </Button>
            </div>

            {codeSent && (
              <div>
              {!verified && <Label htmlFor="code">인증코드</Label>}
                {verified ? (
         <div className="text-green-600 text-sm font-semibold mt-1">
              인증이 완료되었습니다.
            </div>
               ) : (
                 <>
                <div className="flex space-x-2">
                  <Input
                    id="code"
                    name="code"
                    value={form.code}
                    onChange={handleChange}
                    required
                  />
                  <Button
                    type="button"
                    onClick={handleVerifyCode}
                  >
                    확인
                  </Button>
                </div>
                {secondsLeft !== null && (
                    <span className="text-sm text-gray-500">
                      남은 시간: {Math.floor(secondsLeft / 60)}:
                      {String(secondsLeft % 60).padStart(2, "0")}
                    </span>
                  )}
                  </>
                )}
              </div>
            )}
            
            <div>
              <Label htmlFor="password">비밀번호</Label>
              <Input
                id="password"
                name="password"
                type="password"
                value={form.password}
                onChange={handleChange}
                required
              />
            </div>
            {error && <p className="text-red-500 text-sm">{error}</p>}
            <Button type="submit" className="w-full">가입하기</Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};

export default RegisterPage;