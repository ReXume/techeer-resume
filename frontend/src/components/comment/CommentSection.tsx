import React, { useEffect, useState } from "react";
import CommentList from "./CommentList";
import CommentForm from "./CommentForm";
import ErrorMessage from "../UI/ErrorMessage.tsx";
import LoadingSpinner from "../UI/LoadingSpinner.tsx";
import { AddFeedbackPoint, FeedbackPoint } from "../../types.ts";
import useAuthStore from "../../store/authStore.ts";

interface CommentSectionProps {
  feedbackPoints: FeedbackPoint[];
  addFeedbackPoint: (point: Omit<AddFeedbackPoint, "id">) => void;
  deleteFeedbackPoint: (id: number) => void;
  editFeedbackPoint: (item: FeedbackPoint) => void;
  hoveredCommentId: number | null;
  setHoveredCommentId: (id: number | null) => void;

  loading?: boolean;
  error?: string;
}

function CommentSection({
  feedbackPoints,
  addFeedbackPoint,
  deleteFeedbackPoint,
  editFeedbackPoint,
  hoveredCommentId,
  setHoveredCommentId,

  loading = false,
  error = "",
}: CommentSectionProps): React.ReactElement {
  // 일반 댓글 추가
  const [isLogin, setIsLogin] = useState(false);
  const { isAuthenticated } = useAuthStore();
  useEffect(() => {
    if (!isAuthenticated) {
      setIsLogin(false);
    } else {
      setIsLogin(true);
    }
  }, []);
  const handleAddComment = async (text: string) => {
    try {
      addFeedbackPoint({
        content: text,
        xCoordinate: 0,
        yCoordinate: 0,
        pageNumber: 1,
      });
    } catch (error) {
      console.error("Failed to add comment", error);
    }
  };

  return (
    <div className="flex flex-col h-full justify-between ">
      {/* 에러 메시지 */}
      {error && <ErrorMessage message={error} />}

      {/* 댓글 목록 */}
      <div className="mt-4 overflow-y-auto h-[33vh]">
        {loading ? (
          <LoadingSpinner />
        ) : (
          <CommentList
            feedbackPoints={feedbackPoints ?? []}
            deleteFeedbackPoint={deleteFeedbackPoint}
            editFeedbackPoint={editFeedbackPoint}
            hoveredCommentId={hoveredCommentId}
            setHoveredCommentId={setHoveredCommentId}
          />
        )}
      </div>

      {/* 댓글 추가 입력 */}
      <div className="mt-4">
        <CommentForm onAdd={handleAddComment} disabled={loading} />
      </div>
    </div>
  );
}

export default CommentSection;