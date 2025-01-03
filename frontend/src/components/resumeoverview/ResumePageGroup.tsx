import ResumePage from "./ResumePage";
import { AddFeedbackPoint, FeedbackPoint } from "../../types.ts";

type ResumePageGroupProps = {
  // pages: number;
  feedbackPoints: FeedbackPoint[];
  addFeedbackPoint: (point: Omit<AddFeedbackPoint, "id" | "type">) => void;
  deleteFeedbackPoint: (id: number) => void;
  editFeedbackPoint: (item: AddFeedbackPoint) => void;
  hoveredCommentId: number | null;
  setHoveredCommentId: (id: number | null) => void;
};

function ResumePageGroup({
  // pages,
  feedbackPoints,
  addFeedbackPoint,
  deleteFeedbackPoint,
  editFeedbackPoint,
  hoveredCommentId,
  setHoveredCommentId,
}: ResumePageGroupProps) {
  // 단일 페이지만 렌더링
  return (
    <ResumePage
      key={1}
      pageNumber={1}
      feedbackPoints={feedbackPoints}
      addFeedbackPoint={addFeedbackPoint}
      deleteFeedbackPoint={deleteFeedbackPoint}
      editFeedbackPoint={editFeedbackPoint}
      hoveredCommentId={hoveredCommentId}
      setHoveredCommentId={setHoveredCommentId}
    />
  );
}

export default ResumePageGroup;
