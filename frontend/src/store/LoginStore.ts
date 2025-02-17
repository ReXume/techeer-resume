import { create } from "zustand";

interface UserInfo {
  username: string;
  email: string;
  role: string;
}

interface UserInfoStore {
  userData: UserInfo | null; // userData를 객체로 정의 (초기값은 null)
  setUserData: (info: UserInfo) => void;
}

export const useUserInfo = create<UserInfoStore>((set) => ({
  userData: null,
  setUserData: (info) => set({ userData: info }),
}));
