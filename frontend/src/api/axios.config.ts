import axios from "axios";
import { AXIOS_BASE_URL, NETWORK } from "../constants/api.ts";
import { handleAPIError } from "./interceptor.ts";
import camelcaseKeys from "camelcase-keys";
import decamelizeKeys from "decamelize-keys";
const BASE_URL = import.meta.env.VITE_API_BASE_URL;

export const jsonAxios = axios.create({
  baseURL: BASE_URL,
  withCredentials: true, // 필요에 따라 설정
  headers: {
    "Content-Type": "application/json",
  },
});

export const formAxios = axios.create({
  baseURL: BASE_URL,
  withCredentials: true, // 필요에 따라 설정
  headers: {
    "Content-Type": "multipart/form-data",
  },
});

export const jsonFormAxios = axios.create({
  baseURL: BASE_URL,
  withCredentials: true, // 필요에 따라 설정
  headers: {
    "Content-Type": "application/json",
  },
});

export const axiosInstance = axios.create({
  baseURL: AXIOS_BASE_URL,
  timeout: NETWORK.TIMEOUT,
  withCredentials: true, // CORS 설정에 따라 true 또는 false
  // useAuth는 Axios의 기본 설정에 포함되지 않으므로 제거
});

// 응답 인터셉터: 에러 핸들링
axiosInstance.interceptors.response.use((response) => response, handleAPIError);

// 요청 인터셉터: 인증 토큰 자동 첨부 (옵션)
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("authToken");
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 요청 인터셉터 설정
axiosInstance.interceptors.request.use((config) => {
  if (config.data) {
    config.data = decamelizeKeys(config.data);
  }
  return config;
});

// 응답 인터셉터 설정
axiosInstance.interceptors.response.use((response) => {
  if (response.data) {
    response.data = camelcaseKeys(response.data, { deep: true });
  }
  return response;
});
