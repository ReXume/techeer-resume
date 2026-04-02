import { jsonAxios } from "./axios.config.ts";
import * as Sentry from "@sentry/browser";

export interface JobSearchFilters {
  position?: string;
  experience?: string;
  skills?: string[];
  source?: string;
  location?: string;
}

export interface JobSearchParams {
  query?: string;
  filters?: JobSearchFilters;
  page?: number;
  size?: number;
}

export interface JobPosting {
  id: number;
  title: string;
  companyName: string;
  location: string;
  experienceLevel: string;
  position: string;
  skills: string[];
  source: string;
  sourceUrl: string;
  salary?: string;
  deadline?: string;
  description?: string;
  createdAt: string;
}

export interface JobSearchResponse {
  content: JobPosting[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface AutocompleteItem {
  keyword: string;
  type: string;
}

export interface EventPayload {
  jobId: number;
  eventType: "APPLY_CLICK" | "VIEW" | "BOOKMARK";
  sourceUrl?: string;
}

export const searchJobs = async (params: JobSearchParams): Promise<JobSearchResponse> => {
  try {
    const queryParams = new URLSearchParams();
    if (params.query) queryParams.set("q", params.query);
    if (params.page !== undefined) queryParams.set("page", String(params.page));
    if (params.size !== undefined) queryParams.set("size", String(params.size));
    if (params.filters?.position) queryParams.set("position", params.filters.position);
    if (params.filters?.experience) queryParams.set("experience", params.filters.experience);
    if (params.filters?.source) queryParams.set("source", params.filters.source);
    if (params.filters?.location) queryParams.set("location", params.filters.location);
    if (params.filters?.skills?.length) {
      params.filters.skills.forEach((s) => queryParams.append("skills", s));
    }

    const response = await jsonAxios.get(`/api/v2/jobs/search?${queryParams.toString()}`);
    return response.data;
  } catch (error) {
    Sentry.captureException(error);
    throw error;
  }
};

export const getJobDetail = async (id: number): Promise<JobPosting> => {
  try {
    const response = await jsonAxios.get(`/api/v1/jobs/${id}`);
    return response.data;
  } catch (error) {
    Sentry.captureException(error);
    throw error;
  }
};

export const getSimilarJobs = async (id: number): Promise<JobPosting[]> => {
  try {
    const response = await jsonAxios.get(`/api/v2/jobs/${id}/similar`);
    return response.data;
  } catch (error) {
    Sentry.captureException(error);
    throw error;
  }
};

export const getAutocomplete = async (prefix: string): Promise<AutocompleteItem[]> => {
  try {
    const response = await jsonAxios.get(
      `/api/v2/jobs/search/autocomplete?prefix=${encodeURIComponent(prefix)}`
    );
    return response.data;
  } catch (error) {
    Sentry.captureException(error);
    throw error;
  }
};

export const recordEvent = async (event: EventPayload): Promise<void> => {
  try {
    await jsonAxios.post("/api/v1/events", event);
  } catch (error) {
    Sentry.captureException(error);
    throw error;
  }
};

export interface RecommendationItem {
  id: number;
  title: string;
  companyName: string;
  matchScore: number;
  matchReasons: string[];
  source: string;
  sourceUrl: string;
  location?: string;
  experienceLevel?: string;
}

export interface RecommendationResponse {
  content: RecommendationItem[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface UserProfileData {
  skills?: string[];
  desiredPosition?: string;
  experienceLevel?: string;
  preferredLocations?: string[];
  remotePreferred?: boolean;
}

export interface ApplyHistoryItem {
  id: number;
  jobId: number;
  title: string;
  companyName: string;
  sourceUrl: string;
  clickedAt: string;
}

export const getRecommendations = async (
  page = 0,
  size = 10
): Promise<RecommendationResponse> => {
  try {
    const response = await jsonAxios.get(
      `/api/v1/recommendations?page=${page}&size=${size}`
    );
    return response.data;
  } catch (error) {
    Sentry.captureException(error);
    throw error;
  }
};

export const updateProfile = async (data: UserProfileData): Promise<void> => {
  try {
    await jsonAxios.put("/api/v1/users/profile", data);
  } catch (error) {
    Sentry.captureException(error);
    throw error;
  }
};

export const getPopularJobs = async (): Promise<JobPosting[]> => {
  try {
    const response = await jsonAxios.get("/api/v1/events/popular");
    return response.data;
  } catch (error) {
    Sentry.captureException(error);
    throw error;
  }
};

export const getApplyHistory = async (): Promise<ApplyHistoryItem[]> => {
  try {
    const response = await jsonAxios.get(
      "/api/v1/events/history?eventType=APPLY_CLICK"
    );
    return response.data;
  } catch (error) {
    Sentry.captureException(error);
    throw error;
  }
};
