import React, { useRef, useState } from "react";
import CommentForm from "../comment/CommentForm.tsx";
import { AddFeedbackPoint, FeedbackPoint } from "../../types.ts";
import PDFPage from "./PDFPage.tsx";
import useResumeStore from "../../store/ResumeStore.ts";

type ResumePageProps = {
  pageNumber: number;
  feedbackPoints: FeedbackPoint[];
  addFeedbackPoint: (point: Omit<AddFeedbackPoint, "id">) => void;
  deleteFeedbackPoint: (id: number) => void;
  editFeedbackPoint: (item: AddFeedbackPoint) => void;
  hoveredCommentId: number | null;
  setHoveredCommentId: (id: number | null) => void;
};

function ResumePage({
  pageNumber,
  feedbackPoints,
  addFeedbackPoint,
  editFeedbackPoint,
  hoveredCommentId,
  setHoveredCommentId,
}: ResumePageProps) {
  const pageRef = useRef<HTMLDivElement>(null);
  const [addingFeedback, setAddingFeedback] = useState<{
    x: number;
    y: number;
    pageNumber: number;
  } | null>(null);
  const [editingFeedback, setEditingFeedback] = useState<FeedbackPoint | null>(
    null
  );

  const handleClick = (e: React.MouseEvent<HTMLDivElement, MouseEvent>) => {
    if (!pageRef.current) return;

    const rect = pageRef.current.getBoundingClientRect();
    const x = ((e.clientX - rect.left) / rect.width) * 100; // 백분율
    const y = ((e.clientY - rect.top) / rect.height) * 100; // 백분율

    setAddingFeedback({ x, y, pageNumber });
  };

  const handleMarkerClick = (point: FeedbackPoint) => {
    setEditingFeedback(point);
  };

  const handleAddSubmit = (comment: string) => {
    if (addingFeedback) {
      addFeedbackPoint({
        pageNumber: addingFeedback.pageNumber,
        xCoordinate: addingFeedback.x,
        yCoordinate: addingFeedback.y,
        content: comment,
      });
      setAddingFeedback(null);
    }
  };

  const handleEditSubmit = () => {
    if (editingFeedback) {
      const updatedPoint: AddFeedbackPoint = { ...editingFeedback };
      editFeedbackPoint(updatedPoint);
      setEditingFeedback(null);
    }
  };

  const handleCancel = () => {
    setAddingFeedback(null);
    setEditingFeedback(null);
  };

  const { ResumeUrl } = useResumeStore();

  return (
    <div className="relative mb-8">
      <div
        ref={pageRef}
        className="w-full h-[903px] items-center relative cursor-pointer -mt-1"
        onClick={handleClick}
      >
        {/* PDF 미리보기 */}
        <PDFPage pdfUrl={ResumeUrl} />
        <>
          {feedbackPoints.map((point) => (
            <div
              key={point.id}
              className={`absolute w-4 h-4 rounded-full transform -translate-x-1/2 -translate-y-1/2 cursor-pointer ${
                point.id === hoveredCommentId ? "bg-sky-500" : "bg-red-500"
              }`}
              style={{
                left: `${point.xCoordinate}%`,
                top: `${point.yCoordinate}%`,
              }}
              onMouseEnter={() => setHoveredCommentId(point.id)}
              onMouseLeave={() => setHoveredCommentId(null)}
              onClick={(e) => {
                e.stopPropagation(); // 페이지 클릭 방지
                handleMarkerClick(point);
              }}
            ></div>
          ))}
          {/* 피드백 추가 폼 */}
          {addingFeedback && (
            <CommentForm
              position={{ x: addingFeedback.x, y: addingFeedback.y }}
              onSubmit={handleAddSubmit}
              onCancel={handleCancel}
            />
          )}
          {/* 피드백 수정 폼 */}
          {editingFeedback && (
            <CommentForm
              position={{
                x: editingFeedback.xCoordinate,
                y: editingFeedback.yCoordinate,
              }}
              initialComment={editingFeedback.content}
              onSubmit={handleEditSubmit}
              onCancel={handleCancel}
            />
          )}
        </>
      </div>
    </div>
  );
}

export default ResumePage;
