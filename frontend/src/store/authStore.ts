import { create } from "zustand";
import { axiosInstance } from "../api/axios.config";

interface AuthState {
  isAuthenticated: boolean;
  user: any | null;
  hasChecked: boolean;

  checkAuth: () => Promise<void>;
  logout: () => Promise<void>;
}

const useAuthStore = create<AuthState>((set, get) => ({
  isAuthenticated: false,
  user: null,
  hasChecked: false,

  checkAuth: async () => {
    console.log(get().hasChecked);
    if (get().hasChecked) return; //API 요청을 한 번만 보내도록 설정

    try {
      const response = await axiosInstance.get("/user");
      set({
        isAuthenticated: true,
        user: response.data.result.username,
        hasChecked: true,
      });
      console.log(response);
    } catch (error) {
      set({ isAuthenticated: false, user: null, hasChecked: true });
      throw error;
    }
  },
  logout: async () => {
    try {
      await axiosInstance.post("/logout");
      set({ isAuthenticated: false, user: null, hasChecked: false });
    } catch (error) {
      console.error("로그아웃 실패!", error);
    }
  },
}));

export default useAuthStore;
