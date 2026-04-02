import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { useNavigate, Link } from "react-router-dom";
import { Search, ChevronRight, Star, Clock, User } from "lucide-react";
import GlobalNavbar from "../components/common/GlobalNavbar";
import MatchScoreBadge from "../components/common/MatchScoreBadge";
import SourceBadge from "../components/common/SourceBadge";
import SkillTag from "../components/common/SkillTag";
import useAuthStore from "../store/authStore";
import {
  getRecommendations,
  getPopularJobs,
  RecommendationItem,
  JobPosting,
} from "../api/jobApi";

function MainPage() {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuthStore();
  const [searchText, setSearchText] = useState("");

  // Determine if profile is complete (has recommendations available)
  // We attempt to fetch recommendations; if it returns data the profile is complete
  const {
    data: recommendationData,
    isLoading: recLoading,
    isError: recError,
  } = useQuery({
    queryKey: ["recommendations"],
    queryFn: () => getRecommendations(0, 8),
    enabled: isAuthenticated,
    retry: false,
  });

  const { data: popularJobs, isLoading: popularLoading } = useQuery({
    queryKey: ["popularJobs"],
    queryFn: getPopularJobs,
    retry: false,
  });

  const handleSearch = () => {
    const q = searchText.trim();
    if (!q) return;
    navigate(`/jobs/search?q=${encodeURIComponent(q)}`);
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") handleSearch();
  };

  const recommendations: RecommendationItem[] =
    recommendationData?.content ?? [];
  const profileComplete = isAuthenticated && recommendations.length > 0;
  const profileIncomplete = isAuthenticated && !recLoading && !profileComplete;

  return (
    <div className="min-h-screen bg-slate-50">
      <GlobalNavbar />

      {/* Hero Section */}
      <section className="bg-gradient-to-br from-blue-600 to-indigo-700 text-white py-20 px-4">
        <div className="max-w-3xl mx-auto text-center">
          <h1 className="text-4xl sm:text-5xl font-bold mb-4 leading-tight">
            나에게 맞는 채용공고를
            <br />
            찾아보세요
          </h1>
          <p className="text-blue-100 text-lg mb-10">
            기술 스택, 포지션, 경력을 기반으로 최적의 채용공고를 추천해 드립니다
          </p>
          <div className="flex items-center bg-white rounded-full shadow-lg overflow-hidden max-w-xl mx-auto">
            <input
              type="text"
              placeholder="직무, 회사, 기술스택 검색"
              className="flex-1 pl-6 pr-2 py-4 text-gray-800 text-base focus:outline-none"
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              onKeyDown={handleKeyDown}
            />
            <button
              onClick={handleSearch}
              className="m-1.5 px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-full font-medium flex items-center gap-2 transition-colors"
            >
              <Search className="w-4 h-4" />
              검색
            </button>
          </div>
        </div>
      </section>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 space-y-14">
        {/* Recommendation Section */}
        {isAuthenticated && (
          <section>
            {recLoading ? (
              <div className="flex items-center gap-3 py-8">
                <div className="w-5 h-5 border-2 border-blue-400 border-t-transparent rounded-full animate-spin" />
                <span className="text-gray-500">추천 공고를 불러오는 중...</span>
              </div>
            ) : profileComplete ? (
              <>
                <div className="flex items-center justify-between mb-5">
                  <div className="flex items-center gap-2">
                    <Star className="w-5 h-5 text-yellow-400 fill-yellow-400" />
                    <h2 className="text-xl font-bold text-gray-900">추천 채용공고</h2>
                  </div>
                  <Link
                    to="/jobs?sort=recommendation"
                    className="flex items-center gap-1 text-sm text-blue-600 hover:text-blue-700 font-medium"
                  >
                    더보기 <ChevronRight className="w-4 h-4" />
                  </Link>
                </div>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                  {recommendations.map((item) => (
                    <RecommendationCard key={item.id} item={item} />
                  ))}
                </div>
              </>
            ) : profileIncomplete && !recError ? (
              <ProfileIncompletePrompt />
            ) : recError ? (
              <ProfileIncompletePrompt />
            ) : null}
          </section>
        )}

        {/* Popular Jobs Section */}
        <section>
          <div className="flex items-center justify-between mb-5">
            <div className="flex items-center gap-2">
              <Star className="w-5 h-5 text-orange-400 fill-orange-400" />
              <h2 className="text-xl font-bold text-gray-900">인기 채용공고</h2>
            </div>
            <Link
              to="/jobs"
              className="flex items-center gap-1 text-sm text-blue-600 hover:text-blue-700 font-medium"
            >
              전체보기 <ChevronRight className="w-4 h-4" />
            </Link>
          </div>
          {popularLoading ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              {Array.from({ length: 4 }).map((_, i) => (
                <div key={i} className="bg-white rounded-xl border border-gray-100 p-4 animate-pulse h-44" />
              ))}
            </div>
          ) : popularJobs && popularJobs.length > 0 ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              {popularJobs.slice(0, 8).map((job) => (
                <JobCard key={job.id} job={job} />
              ))}
            </div>
          ) : (
            <EmptyState message="인기 채용공고가 없습니다" />
          )}
        </section>

        {/* Latest Jobs Section */}
        <section>
          <div className="flex items-center justify-between mb-5">
            <div className="flex items-center gap-2">
              <Clock className="w-5 h-5 text-blue-500" />
              <h2 className="text-xl font-bold text-gray-900">최신 채용공고</h2>
            </div>
            <Link
              to="/jobs"
              className="flex items-center gap-1 text-sm text-blue-600 hover:text-blue-700 font-medium"
            >
              전체보기 <ChevronRight className="w-4 h-4" />
            </Link>
          </div>
          <div className="text-center py-12 bg-white rounded-xl border border-gray-100">
            <p className="text-gray-400">
              최신 채용공고를 보려면{" "}
              <Link to="/jobs" className="text-blue-600 hover:underline font-medium">
                채용공고 목록
              </Link>
              을 확인하세요
            </p>
          </div>
        </section>
      </div>
    </div>
  );
}

function RecommendationCard({ item }: { item: RecommendationItem }) {
  const navigate = useNavigate();
  return (
    <div
      onClick={() => navigate(`/jobs/${item.id}`)}
      className="bg-white rounded-xl border border-gray-100 shadow-sm p-4 cursor-pointer hover:shadow-md hover:border-blue-200 transition-all flex flex-col gap-3"
    >
      <div className="flex items-start justify-between gap-2">
        <div className="min-w-0">
          <p className="text-xs text-gray-400 mb-0.5 truncate">{item.companyName}</p>
          <h3 className="text-sm font-semibold text-gray-900 line-clamp-2 leading-snug">
            {item.title}
          </h3>
        </div>
        <MatchScoreBadge score={item.matchScore} />
      </div>

      {item.matchReasons.length > 0 && (
        <div className="flex flex-wrap gap-1">
          {item.matchReasons.slice(0, 3).map((reason) => (
            <SkillTag key={reason} label={reason} />
          ))}
        </div>
      )}

      <div className="mt-auto">
        <SourceBadge source={item.source} />
      </div>
    </div>
  );
}

function JobCard({ job }: { job: JobPosting }) {
  const navigate = useNavigate();
  return (
    <div
      onClick={() => navigate(`/jobs/${job.id}`)}
      className="bg-white rounded-xl border border-gray-100 shadow-sm p-4 cursor-pointer hover:shadow-md hover:border-blue-200 transition-all flex flex-col gap-3"
    >
      <div>
        <p className="text-xs text-gray-400 mb-0.5 truncate">{job.companyName}</p>
        <h3 className="text-sm font-semibold text-gray-900 line-clamp-2 leading-snug">
          {job.title}
        </h3>
      </div>
      {job.skills.length > 0 && (
        <div className="flex flex-wrap gap-1">
          {job.skills.slice(0, 3).map((s) => (
            <SkillTag key={s} label={s} />
          ))}
        </div>
      )}
      <div className="mt-auto flex items-center justify-between">
        <SourceBadge source={job.source} />
        <span className="text-xs text-gray-400">{job.location}</span>
      </div>
    </div>
  );
}

function ProfileIncompletePrompt() {
  return (
    <div className="bg-blue-50 border border-blue-100 rounded-xl p-6 flex flex-col sm:flex-row items-center justify-between gap-4">
      <div className="flex items-center gap-4">
        <div className="w-12 h-12 rounded-full bg-blue-100 flex items-center justify-center flex-shrink-0">
          <User className="w-6 h-6 text-blue-600" />
        </div>
        <div>
          <p className="font-semibold text-gray-800">
            프로필을 완성하면 맞춤 공고를 추천받을 수 있어요
          </p>
          <p className="text-sm text-gray-500 mt-0.5">
            기술 스택, 희망 포지션, 경력 정보를 입력해 보세요
          </p>
        </div>
      </div>
      <Link
        to="/myInfo"
        className="flex-shrink-0 px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium rounded-lg transition-colors"
      >
        프로필 완성하기
      </Link>
    </div>
  );
}

function EmptyState({ message }: { message: string }) {
  return (
    <div className="bg-white rounded-xl border border-gray-100 py-12 text-center">
      <p className="text-gray-400">{message}</p>
    </div>
  );
}

export default MainPage;
