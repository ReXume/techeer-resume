import { useState } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import {
  MapPin,
  Briefcase,
  Clock,
  ExternalLink,
  Bookmark,
  BookmarkCheck,
  ArrowLeft,
  Building2,
  DollarSign,
} from "lucide-react";
import GlobalLayout from "../components/Layout/GlobalLayout";
import { getJobDetail, getSimilarJobs, recordEvent } from "../api/jobApi";

const SOURCE_COLORS: Record<string, string> = {
  원티드: "bg-blue-100 text-blue-700",
  사람인: "bg-green-100 text-green-700",
  잡코리아: "bg-orange-100 text-orange-700",
  링크드인: "bg-sky-100 text-sky-700",
  default: "bg-gray-100 text-gray-600",
};

function SourceBadge({ source }: { source: string }) {
  const cls = SOURCE_COLORS[source] ?? SOURCE_COLORS.default;
  return (
    <span className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-medium ${cls}`}>
      {source}
    </span>
  );
}

function JobDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const jobId = Number(id);
  const [bookmarked, setBookmarked] = useState(false);

  const {
    data: job,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ["job", jobId],
    queryFn: () => getJobDetail(jobId),
    enabled: !!jobId,
  });

  const { data: similarJobs } = useQuery({
    queryKey: ["similarJobs", jobId],
    queryFn: () => getSimilarJobs(jobId),
    enabled: !!jobId,
  });

  const handleApply = async () => {
    if (!job) return;
    try {
      await recordEvent({ jobId, eventType: "APPLY_CLICK", sourceUrl: job.sourceUrl });
    } catch {
      // non-blocking: proceed even if event recording fails
    }
    window.open(job.sourceUrl, "_blank", "noopener,noreferrer");
  };

  const handleBookmark = async () => {
    if (!job) return;
    try {
      await recordEvent({ jobId, eventType: "BOOKMARK" });
    } catch {
      // non-blocking
    }
    setBookmarked((v) => !v);
  };

  if (isLoading) {
    return (
      <GlobalLayout>
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-10 animate-pulse">
          <div className="h-6 bg-gray-200 rounded w-1/4 mb-6" />
          <div className="bg-white rounded-2xl p-8 border border-gray-200">
            <div className="h-8 bg-gray-200 rounded w-2/3 mb-4" />
            <div className="h-4 bg-gray-200 rounded w-1/3 mb-8" />
            <div className="space-y-3">
              {Array.from({ length: 6 }).map((_, i) => (
                <div key={i} className="h-4 bg-gray-100 rounded" />
              ))}
            </div>
          </div>
        </div>
      </GlobalLayout>
    );
  }

  if (isError || !job) {
    return (
      <GlobalLayout>
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-16 text-center">
          <p className="text-lg text-gray-500 mb-4">채용공고를 불러오는 데 실패했습니다.</p>
          <button
            onClick={() => navigate(-1)}
            className="text-blue-600 hover:underline text-sm"
          >
            뒤로 가기
          </button>
        </div>
      </GlobalLayout>
    );
  }

  return (
    <GlobalLayout>
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Breadcrumb */}
        <button
          onClick={() => navigate(-1)}
          className="flex items-center gap-1 text-sm text-gray-500 hover:text-gray-800 mb-6 transition"
        >
          <ArrowLeft className="w-4 h-4" />
          채용공고 목록
        </button>

        {/* Main card */}
        <div className="bg-white border border-gray-200 rounded-2xl p-8 mb-6">
          {/* Header */}
          <div className="flex items-start justify-between gap-4 mb-6">
            <div className="flex-1">
              <div className="flex items-center gap-2 mb-2">
                <SourceBadge source={job.source} />
                {job.position && (
                  <span className="px-3 py-1 bg-gray-100 text-gray-600 rounded-full text-sm">
                    {job.position}
                  </span>
                )}
              </div>
              <h1 className="text-2xl font-bold text-gray-900 mb-1">{job.title}</h1>
              <div className="flex items-center gap-1 text-gray-600">
                <Building2 className="w-4 h-4" />
                <span className="font-medium">{job.companyName}</span>
              </div>
            </div>
          </div>

          {/* Meta info */}
          <div className="flex flex-wrap gap-4 text-sm text-gray-500 mb-6 pb-6 border-b border-gray-100">
            {job.location && (
              <span className="flex items-center gap-1.5">
                <MapPin className="w-4 h-4 text-gray-400" />
                {job.location}
              </span>
            )}
            {job.experienceLevel && (
              <span className="flex items-center gap-1.5">
                <Briefcase className="w-4 h-4 text-gray-400" />
                {job.experienceLevel}
              </span>
            )}
            {job.salary && (
              <span className="flex items-center gap-1.5">
                <DollarSign className="w-4 h-4 text-gray-400" />
                {job.salary}
              </span>
            )}
            {job.deadline && (
              <span className="flex items-center gap-1.5">
                <Clock className="w-4 h-4 text-gray-400" />
                마감일: {job.deadline}
              </span>
            )}
          </div>

          {/* Skills */}
          {job.skills.length > 0 && (
            <div className="mb-6">
              <h2 className="text-sm font-semibold text-gray-700 mb-3">기술 스택</h2>
              <div className="flex flex-wrap gap-2">
                {job.skills.map((skill) => (
                  <span
                    key={skill}
                    className="px-3 py-1 bg-blue-50 text-blue-700 rounded-lg text-sm font-medium"
                  >
                    {skill}
                  </span>
                ))}
              </div>
            </div>
          )}

          {/* Description */}
          {job.description && (
            <div className="mb-8">
              <h2 className="text-sm font-semibold text-gray-700 mb-3">공고 내용</h2>
              <div className="prose prose-sm max-w-none text-gray-700 leading-relaxed whitespace-pre-wrap">
                {job.description}
              </div>
            </div>
          )}

          {/* Action buttons */}
          <div className="flex items-center gap-3 pt-4 border-t border-gray-100">
            <button
              onClick={handleApply}
              className="flex-1 sm:flex-none flex items-center justify-center gap-2 px-8 py-3 bg-blue-600 text-white font-semibold rounded-xl hover:bg-blue-700 active:bg-blue-800 transition"
            >
              <ExternalLink className="w-4 h-4" />
              지원하기
            </button>
            <button
              onClick={handleBookmark}
              className={`flex items-center justify-center gap-2 px-5 py-3 rounded-xl border font-medium transition ${
                bookmarked
                  ? "bg-yellow-50 border-yellow-400 text-yellow-600 hover:bg-yellow-100"
                  : "bg-white border-gray-300 text-gray-600 hover:bg-gray-50"
              }`}
              aria-label="북마크"
            >
              {bookmarked ? (
                <BookmarkCheck className="w-5 h-5" />
              ) : (
                <Bookmark className="w-5 h-5" />
              )}
              <span className="hidden sm:inline">북마크</span>
            </button>
          </div>
        </div>

        {/* Similar jobs */}
        {similarJobs && similarJobs.length > 0 && (
          <section>
            <h2 className="text-base font-bold text-gray-800 mb-4">유사한 채용공고</h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {similarJobs.slice(0, 4).map((similar) => (
                <Link
                  key={similar.id}
                  to={`/jobs/${similar.id}`}
                  className="bg-white border border-gray-200 rounded-xl p-4 hover:shadow-md hover:border-blue-300 transition-all"
                >
                  <div className="flex items-start justify-between gap-2 mb-2">
                    <div>
                      <p className="text-xs text-gray-500 mb-0.5">{similar.companyName}</p>
                      <p className="text-sm font-semibold text-gray-900 line-clamp-2">
                        {similar.title}
                      </p>
                    </div>
                    <span
                      className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium flex-shrink-0 ${
                        SOURCE_COLORS[similar.source] ?? SOURCE_COLORS.default
                      }`}
                    >
                      {similar.source}
                    </span>
                  </div>
                  <div className="flex flex-wrap gap-1">
                    {similar.skills.slice(0, 3).map((s) => (
                      <span key={s} className="px-2 py-0.5 bg-gray-100 text-gray-600 rounded text-xs">
                        {s}
                      </span>
                    ))}
                  </div>
                </Link>
              ))}
            </div>
          </section>
        )}
      </div>
    </GlobalLayout>
  );
}

export default JobDetailPage;
