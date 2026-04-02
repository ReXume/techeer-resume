interface SourceBadgeProps {
  source: string;
}

const SOURCE_STYLES: Record<string, string> = {
  원티드: "bg-blue-100 text-blue-700",
  사람인: "bg-green-100 text-green-700",
  잡코리아: "bg-orange-100 text-orange-700",
  링크드인: "bg-sky-100 text-sky-700",
  WANTED: "bg-blue-100 text-blue-700",
  SARAMIN: "bg-green-100 text-green-700",
  JOBKOREA: "bg-orange-100 text-orange-700",
  LINKEDIN: "bg-sky-100 text-sky-700",
};

const SOURCE_LABELS: Record<string, string> = {
  WANTED: "원티드",
  SARAMIN: "사람인",
  JOBKOREA: "잡코리아",
  LINKEDIN: "링크드인",
};

function SourceBadge({ source }: SourceBadgeProps) {
  const label = SOURCE_LABELS[source] ?? source;
  const colorClass = SOURCE_STYLES[source] ?? SOURCE_STYLES[label] ?? "bg-gray-100 text-gray-600";

  return (
    <span className={`inline-block px-2 py-0.5 rounded text-xs font-medium ${colorClass}`}>
      {label}
    </span>
  );
}

export default SourceBadge;
