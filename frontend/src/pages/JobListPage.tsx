import { useState, useEffect, useRef, useCallback } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useInfiniteQuery } from "@tanstack/react-query";
import { Briefcase, MapPin, Clock, ChevronDown, ChevronUp, SlidersHorizontal, X } from "lucide-react";
import GlobalLayout from "../components/Layout/GlobalLayout";
import { searchJobs, JobPosting, JobSearchFilters } from "../api/jobApi";
import useJobStore from "../store/jobStore";

const SOURCE_COLORS: Record<string, string> = {
  원티드: "bg-blue-100 text-blue-700",
  사람인: "bg-green-100 text-green-700",
  잡코리아: "bg-orange-100 text-orange-700",
  링크드인: "bg-sky-100 text-sky-700",
  default: "bg-gray-100 text-gray-600",
};

const POSITIONS = ["프론트엔드", "백엔드", "풀스택", "데브옵스", "데이터엔지니어", "ML엔지니어", "iOS", "Android"];
const EXPERIENCES = ["신입", "1~3년", "3~5년", "5년 이상"];
const LOCATIONS = ["서울", "경기", "인천", "부산", "대구", "원격"];
const SOURCES = ["원티드", "사람인", "잡코리아", "링크드인"];

interface FilterSectionProps {
  title: string;
  options: string[];
  selected: string | undefined;
  onSelect: (value: string | undefined) => void;
}

function FilterSection({ title, options, selected, onSelect }: FilterSectionProps) {
  const [open, setOpen] = useState(true);
  return (
    <div className="border-b border-gray-100 pb-4 mb-4">
      <button
        className="flex items-center justify-between w-full text-sm font-semibold text-gray-700 mb-3"
        onClick={() => setOpen((v) => !v)}
      >
        {title}
        {open ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
      </button>
      {open && (
        <div className="flex flex-wrap gap-2">
          {options.map((opt) => (
            <button
              key={opt}
              onClick={() => onSelect(selected === opt ? undefined : opt)}
              className={`px-3 py-1 rounded-full text-xs font-medium border transition ${
                selected === opt
                  ? "bg-blue-600 text-white border-blue-600"
                  : "bg-white text-gray-600 border-gray-300 hover:border-blue-400 hover:text-blue-600"
              }`}
            >
              {opt}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

function SourceBadge({ source }: { source: string }) {
  const cls = SOURCE_COLORS[source] ?? SOURCE_COLORS.default;
  return (
    <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${cls}`}>
      {source}
    </span>
  );
}

function JobCard({ job, onClick }: { job: JobPosting; onClick: () => void }) {
  return (
    <div
      onClick={onClick}
      className="bg-white border border-gray-200 rounded-xl p-5 cursor-pointer hover:shadow-md hover:border-blue-300 transition-all group"
    >
      <div className="flex items-start justify-between gap-2 mb-3">
        <div>
          <p className="text-xs text-gray-500 mb-1">{job.companyName}</p>
          <h3 className="text-sm font-semibold text-gray-900 group-hover:text-blue-600 transition line-clamp-2">
            {job.title}
          </h3>
        </div>
        <SourceBadge source={job.source} />
      </div>
      <div className="flex flex-wrap gap-1 mb-3">
        {job.skills.slice(0, 4).map((skill) => (
          <span key={skill} className="px-2 py-0.5 bg-gray-100 text-gray-600 rounded text-xs">
            {skill}
          </span>
        ))}
        {job.skills.length > 4 && (
          <span className="px-2 py-0.5 bg-gray-100 text-gray-400 rounded text-xs">
            +{job.skills.length - 4}
          </span>
        )}
      </div>
      <div className="flex items-center gap-3 text-xs text-gray-400">
        {job.location && (
          <span className="flex items-center gap-1">
            <MapPin className="w-3 h-3" />
            {job.location}
          </span>
        )}
        {job.experienceLevel && (
          <span className="flex items-center gap-1">
            <Briefcase className="w-3 h-3" />
            {job.experienceLevel}
          </span>
        )}
        {job.deadline && (
          <span className="flex items-center gap-1 ml-auto">
            <Clock className="w-3 h-3" />
            {job.deadline}
          </span>
        )}
      </div>
    </div>
  );
}

function JobListPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const queryFromUrl = searchParams.get("q") ?? "";
  const { filters, setFilter, resetFilters } = useJobStore();
  const [mobileSidebarOpen, setMobileSidebarOpen] = useState(false);
  const loadMoreRef = useRef<HTMLDivElement | null>(null);

  const activeFiltersCount = Object.values(filters).filter(
    (v) => v !== undefined && (Array.isArray(v) ? v.length > 0 : true)
  ).length;

  const fetchPage = useCallback(
    async ({ pageParam = 0 }) => {
      return searchJobs({ query: queryFromUrl, filters, page: pageParam, size: 12 });
    },
    [queryFromUrl, filters]
  );

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isLoading, isError } =
    useInfiniteQuery({
      queryKey: ["jobs", queryFromUrl, filters],
      queryFn: fetchPage,
      getNextPageParam: (lastPage) => {
        if (lastPage.number + 1 < lastPage.totalPages) return lastPage.number + 1;
        return undefined;
      },
      initialPageParam: 0,
    });

  // Infinite scroll
  useEffect(() => {
    if (!hasNextPage || isFetchingNextPage) return;
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) fetchNextPage();
      },
      { threshold: 0.1 }
    );
    const el = loadMoreRef.current;
    if (el) observer.observe(el);
    return () => {
      if (el) observer.unobserve(el);
    };
  }, [hasNextPage, isFetchingNextPage, fetchNextPage]);

  const allJobs = data?.pages.flatMap((p) => p.content) ?? [];

  const sidebarContent = (
    <aside className="w-full">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-sm font-bold text-gray-800">필터</h2>
        {activeFiltersCount > 0 && (
          <button
            onClick={resetFilters}
            className="text-xs text-blue-600 hover:underline flex items-center gap-1"
          >
            <X className="w-3 h-3" /> 초기화
          </button>
        )}
      </div>
      <FilterSection
        title="포지션"
        options={POSITIONS}
        selected={filters.position}
        onSelect={(v) => setFilter("position", v)}
      />
      <FilterSection
        title="경력"
        options={EXPERIENCES}
        selected={filters.experience}
        onSelect={(v) => setFilter("experience", v)}
      />
      <FilterSection
        title="지역"
        options={LOCATIONS}
        selected={filters.location}
        onSelect={(v) => setFilter("location", v)}
      />
      <FilterSection
        title="출처"
        options={SOURCES}
        selected={filters.source}
        onSelect={(v) => setFilter("source", v)}
      />
    </aside>
  );

  return (
    <GlobalLayout>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Page header */}
        <div className="mb-6 flex items-center justify-between">
          <div>
            <h1 className="text-xl font-bold text-gray-900">
              {queryFromUrl ? `"${queryFromUrl}" 검색 결과` : "채용공고"}
            </h1>
            {data && (
              <p className="text-sm text-gray-500 mt-1">
                총 {data.pages[0]?.totalElements ?? 0}개의 공고
              </p>
            )}
          </div>
          {/* Mobile filter toggle */}
          <button
            className="md:hidden flex items-center gap-2 px-3 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50 transition"
            onClick={() => setMobileSidebarOpen((v) => !v)}
          >
            <SlidersHorizontal className="w-4 h-4" />
            필터
            {activeFiltersCount > 0 && (
              <span className="bg-blue-600 text-white rounded-full text-xs w-5 h-5 flex items-center justify-center">
                {activeFiltersCount}
              </span>
            )}
          </button>
        </div>

        <div className="flex gap-6">
          {/* Desktop sidebar */}
          <div className="hidden md:block w-56 flex-shrink-0">{sidebarContent}</div>

          {/* Mobile sidebar overlay */}
          {mobileSidebarOpen && (
            <div className="fixed inset-0 z-40 md:hidden">
              <div
                className="absolute inset-0 bg-black/40"
                onClick={() => setMobileSidebarOpen(false)}
              />
              <div className="absolute left-0 top-0 h-full w-72 bg-white shadow-xl p-5 overflow-y-auto">
                <button
                  onClick={() => setMobileSidebarOpen(false)}
                  className="mb-4 text-gray-500 hover:text-gray-700"
                >
                  <X className="w-5 h-5" />
                </button>
                {sidebarContent}
              </div>
            </div>
          )}

          {/* Main content */}
          <div className="flex-1 min-w-0">
            {isLoading && (
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                {Array.from({ length: 9 }).map((_, i) => (
                  <div key={i} className="bg-white border border-gray-200 rounded-xl p-5 animate-pulse">
                    <div className="h-3 bg-gray-200 rounded mb-2 w-1/3" />
                    <div className="h-4 bg-gray-200 rounded mb-4 w-3/4" />
                    <div className="flex gap-2 mb-4">
                      <div className="h-5 bg-gray-200 rounded-full w-16" />
                      <div className="h-5 bg-gray-200 rounded-full w-16" />
                    </div>
                    <div className="h-3 bg-gray-100 rounded w-1/2" />
                  </div>
                ))}
              </div>
            )}

            {isError && (
              <div className="text-center py-16 text-gray-500">
                <p className="text-lg mb-2">채용공고를 불러오는 데 실패했습니다.</p>
                <p className="text-sm">잠시 후 다시 시도해 주세요.</p>
              </div>
            )}

            {!isLoading && !isError && allJobs.length === 0 && (
              <div className="text-center py-16 text-gray-500">
                <Briefcase className="w-12 h-12 text-gray-300 mx-auto mb-4" />
                <p className="text-lg mb-2">검색 결과가 없습니다.</p>
                <p className="text-sm">다른 키워드나 필터로 검색해 보세요.</p>
              </div>
            )}

            {!isLoading && allJobs.length > 0 && (
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                {allJobs.map((job) => (
                  <JobCard
                    key={job.id}
                    job={job}
                    onClick={() => navigate(`/jobs/${job.id}`)}
                  />
                ))}
              </div>
            )}

            <div ref={loadMoreRef} className="h-4 mt-4" />
            {isFetchingNextPage && (
              <div className="text-center py-4 text-sm text-gray-400">불러오는 중...</div>
            )}
          </div>
        </div>
      </div>
    </GlobalLayout>
  );
}

export default JobListPage;
