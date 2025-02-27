import { FeedbackPoint } from "../../types";

type CommentListProps = {
  feedbackPoints: FeedbackPoint[];
  deleteFeedbackPoint: (id: number) => void;
  editFeedbackPoint: (item: FeedbackPoint) => void;
  hoveredCommentId: number | null;
  setHoveredCommentId: (id: number | null) => void;
};

function CommentList({
  feedbackPoints,
  deleteFeedbackPoint,
  editFeedbackPoint,
  hoveredCommentId,
  setHoveredCommentId,
}: CommentListProps) {
  return (
    <ul>
      {feedbackPoints.map((item) => (
        <li
          key={item.id}
          className={`mb-2 p-2 border rounded flex justify-between items-center ${
            item.id === hoveredCommentId ? "bg-blue-100" : ""
          }`}
          onMouseEnter={() => setHoveredCommentId(item.id)}
          onMouseLeave={() => setHoveredCommentId(null)}
        >
          <div>
            {/* 
              AI피드백인지 일반 피드백인지 구분해서 보여주는 로직 추가
              만약 content가 "AI피드백: ~"로 시작하면 AI피드백으로 표시
            */}
            {item.content.startsWith("AI피드백:") ? (
              <p>
                <strong>AI피드백:</strong>
                {item.content.slice("AI피드백:".length)}
              </p>
            ) : (
              <p>
                <strong>피드백:</strong>{" "}
                {item.content ?? "No feedback available"}
              </p>
            )}
            <p className="text-sm text-gray-500"></p>
          </div>
          <div className="flex space-x-2">
            <button
              className="px-2 py-1 bg-yellow-500 text-white rounded hover:bg-yellow-600"
              onClick={() => editFeedbackPoint(item)}
            >
              Edit
            </button>
            <button
              className="px-2 py-1 bg-red-500 text-white rounded hover:bg-red-600"
              onClick={() => deleteFeedbackPoint(item.id)}
            >
              Delete
            </button>
          </div>
        </li>
      ))}
    </ul>
  );
}

export default CommentList;
