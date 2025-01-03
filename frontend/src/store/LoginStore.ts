import { create } from "zustand";

interface ResumeStore {
  loginStatus: number;
  setLoginStatus: (id: number) => void;
}

const useLoginStatus = create<ResumeStore>((set) => ({
  loginStatus: 0,
  setLoginStatus: (id) => set({ loginStatus: id }),
}));

export default useLoginStatus;
