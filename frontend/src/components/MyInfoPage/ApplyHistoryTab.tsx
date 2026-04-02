import { useQuery } from "@tanstack/react-query";
import { ExternalLink, Clock } from "lucide-react";
import { getApplyHistory } from "../../api/jobApi";

function ApplyHistoryTab() {
  const { data, isLoading, isError } = useQuery({
    queryKey: ["applyHistory"],
    queryFn: getApplyHistory,
    retry: false,
  });

  if (isLoading) {
    return (
      <div className="bg-white rounded-lg shadow-sm p-6">
        <div className="flex items-center gap-2 text-gray-400">
          <div className="w-4 h-4 border-2 border-blue-400 border-t-transparent rounded-full animate-spin" />
          <span className="text-sm">지원 이력을 불러오는 중...</span>
        </div>
      </div>
    );
  }

  if (isError) {
    return (
      <div className="bg-white rounded-lg shadow-sm p-6 text-center text-gray-400 py-8">
        지원 이력을 불러오지 못했습니다
      </div>
    );
  }

  const history = data ?? [];

  return (
    <div className="bg-white rounded-lg shadow-sm p-6">
      <div className="flex justify-between items-center mb-6">
        <h3 className="text-lg font-semibold">지원 이력</h3>
        <p className="text-sm text-gray-500">{history.length}개</p>
      </div>

      {history.length === 0 ? (
        <div className="text-center text-gray-400 py-10">
          아직 지원한 공고가 없습니다
        </div>
      ) : (
        <div className="divide-y divide-gray-100">
          {history.map((item) => (
            <div
              key={item.id}
              className="flex items-center justify-between py-4 gap-4"
            >
              <div className="min-w-0 flex-1">
                <p className="text-sm font-medium text-gray-900 truncate">
                  {item.title}
                </p>
                <p className="text-xs text-gray-500 mt-0.5">{item.companyName}</p>
              </div>
              <div className="flex items-center gap-3 flex-shrink-0">
                <div className="flex items-center gap-1 text-xs text-gray-400">
                  <Clock className="w-3.5 h-3.5" />
                  {new Date(item.clickedAt).toLocaleDateString("ko-KR")}
                </div>
                <a
                  href={item.sourceUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="flex items-center gap-1 px-3 py-1.5 text-xs font-medium text-blue-600 border border-blue-200 rounded-lg hover:bg-blue-50 transition-colors"
                >
                  공고 보기 <ExternalLink className="w-3 h-3" />
                </a>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default ApplyHistoryTab;
