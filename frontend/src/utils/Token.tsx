import { Outlet, Navigate } from "react-router-dom";
import { useEffect } from "react";
import useAuthStore from "../store/authStore";

function ProtectedRoute() {
  // const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);
  // const { setLoginStatus } = useLoginStatus();
  // const { setUserData } = useUserInfo();
  const { checkAuth, isAuthenticated } = useAuthStore();

  useEffect(() => {
    // const checkAuth = async () => {
    //   try {
    //     const response = await axios.get(`${BASE_URL}/user`, {
    //       withCredentials: true, // HTTP-Only 쿠키 포함
    //     });

    //     // 응답 코드가 USER_200일 경우 인증 성공
    //     if (response.data?.code === "USER_200") {
    //       setIsAuthenticated(true);
    //       setLoginStatus(1);
    //       setUserData(response.data?.result);
    //     } else {
    //       setIsAuthenticated(false);
    //       setLoginStatus(0);
    //     }
    //   } catch (error) {
    //     console.error("인증 요청 실패: ", error);
    //     setIsAuthenticated(false);
    //     setLoginStatus(0);
    //   }
    // };
    // checkAuth();
    checkAuth();
  }, [checkAuth]);

  if (isAuthenticated === null) {
    return <div>Loading...</div>;
  }

  return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace />;
}

export default ProtectedRoute;
