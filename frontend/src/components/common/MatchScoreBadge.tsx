interface MatchScoreBadgeProps {
  score: number; // 0-100
}

function MatchScoreBadge({ score }: MatchScoreBadgeProps) {
  const colorClass =
    score >= 80
      ? "text-green-600 bg-green-50 border-green-200"
      : score >= 60
      ? "text-yellow-600 bg-yellow-50 border-yellow-200"
      : "text-gray-500 bg-gray-50 border-gray-200";

  const ringColor =
    score >= 80
      ? "#16a34a"
      : score >= 60
      ? "#ca8a04"
      : "#9ca3af";

  const circumference = 2 * Math.PI * 16;
  const offset = circumference - (score / 100) * circumference;

  return (
    <div
      className={`inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full border text-xs font-semibold ${colorClass}`}
      title={`매칭 점수: ${score}%`}
    >
      <svg width="20" height="20" viewBox="0 0 40 40" className="flex-shrink-0">
        <circle cx="20" cy="20" r="16" fill="none" stroke="#e5e7eb" strokeWidth="4" />
        <circle
          cx="20"
          cy="20"
          r="16"
          fill="none"
          stroke={ringColor}
          strokeWidth="4"
          strokeDasharray={circumference}
          strokeDashoffset={offset}
          strokeLinecap="round"
          transform="rotate(-90 20 20)"
        />
      </svg>
      {score}%
    </div>
  );
}

export default MatchScoreBadge;
