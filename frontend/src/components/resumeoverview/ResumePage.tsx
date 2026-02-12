import { useRef } from "react";
import { AddFeedbackPoint, FeedbackPoint } from "../../types.ts";
import useResumeStore from "../../store/ResumeStore.ts";
import PDFViewer from "./PDFViewer.tsx";

type ResumePageProps = {
  pageNumber: number;
  feedbackPoints: FeedbackPoint[];
  addFeedbackPoint: (point: Omit<AddFeedbackPoint, "id">) => void;
  deleteFeedbackPoint: (id: number) => void;
  editFeedbackPoint: (item: FeedbackPoint) => void;
  hoveredCommentId: number | null;
  setHoveredCommentId: (id: number | null) => void;
  setClickedCommentId: (id: number | null) => void;
};

function ResumePage({
  pageNumber,
  feedbackPoints,
  addFeedbackPoint,
  editFeedbackPoint,
  hoveredCommentId,
  setHoveredCommentId,
  setClickedCommentId,
}: ResumePageProps) {
  const pageRef = useRef<HTMLDivElement>(null);
  const { ResumeUrl } = useResumeStore();

  return (
    <div className="relative mb-8">
      <div
        ref={pageRef}
        className="w-full h-[903px] items-center relative cursor-pointer -mt-1"
      >
        <PDFViewer
          pdfSrc={ResumeUrl}
          pageNumber={pageNumber}
          addFeedbackPoint={addFeedbackPoint}
          editFeedbackPoint={editFeedbackPoint}
          feedbackPoints={feedbackPoints}
          hoveredCommentId={hoveredCommentId}
          setHoveredCommentId={setHoveredCommentId}
          setClickedCommentId={setClickedCommentId}
        />
      </div>
    </div>
  );
}

export default ResumePage;
