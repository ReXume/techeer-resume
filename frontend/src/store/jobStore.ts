import { create } from "zustand";
import { JobSearchFilters } from "../api/jobApi";

interface JobStore {
  searchQuery: string;
  filters: JobSearchFilters;
  setSearchQuery: (query: string) => void;
  setFilter: (key: keyof JobSearchFilters, value: string | string[] | undefined) => void;
  resetFilters: () => void;
}

const initialFilters: JobSearchFilters = {
  position: undefined,
  experience: undefined,
  skills: [],
  source: undefined,
  location: undefined,
};

const useJobStore = create<JobStore>((set) => ({
  searchQuery: "",
  filters: initialFilters,
  setSearchQuery: (query) => set({ searchQuery: query }),
  setFilter: (key, value) =>
    set((state) => ({
      filters: { ...state.filters, [key]: value },
    })),
  resetFilters: () => set({ filters: initialFilters, searchQuery: "" }),
}));

export default useJobStore;
