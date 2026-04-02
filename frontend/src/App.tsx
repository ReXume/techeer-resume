import { BrowserRouter, Routes, Route } from "react-router-dom";
import MainPage from "./pages/MainPage";
import ResumeFeedbackPage from "./pages/ResumeFeedbackPage";
import SearchPage from "./pages/SearchPage";
import Upload from "./pages/ResumeUpload";
import Login from "./pages/LoginPage";
import ProtectedRoute from "./utils/Token";
import MyInfoPage from "./pages/MyInfoPage";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import AuthCallback from "./pages/AuthCallback";
import JobListPage from "./pages/JobListPage";
import JobDetailPage from "./pages/JobDetailPage";
import JobSearchPage from "./pages/JobSearchPage";

// QueryClient 생성
const queryClient = new QueryClient();

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <div className="flex flex-col min-h-screen">
          <div className="flex-grow">
            <Routes>
              <Route path="/" element={<MainPage />} />
              <Route path="/login" element={<Login />} />
              <Route path="/search" element={<SearchPage />} />
              <Route path="/feedback/:id" element={<ResumeFeedbackPage />} />
              <Route path="/jobs" element={<JobListPage />} />
              <Route path="/jobs/search" element={<JobSearchPage />} />
              <Route path="/jobs/:id" element={<JobDetailPage />} />
              <Route element={<ProtectedRoute />}>
                <Route path="/upload" element={<Upload />} />
                <Route path="/myInfo" element={<MyInfoPage />} />
              </Route>
              <Route path="/Auth" element={<AuthCallback />} />
            </Routes>
          </div>
        </div>
      </BrowserRouter>
    </QueryClientProvider>
  );
}
