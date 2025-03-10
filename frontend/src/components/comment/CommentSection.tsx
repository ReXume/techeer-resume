import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import CommentList from "./CommentList";
import CommentForm from "./CommentForm";
import ErrorMessage from "../UI/ErrorMessage.tsx";
import LoadingSpinner from "../UI/LoadingSpinner.tsx";
import { AddFeedbackPoint, FeedbackPoint } from "../../types.ts";
import useAuthStore from "../../store/authStore.ts";
import { postAiFeedback } from "../../api/feedbackApi"; // API 호출 함수 임포트

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
  const [isLogin, setIsLogin] = useState(false);
  const { isAuthenticated } = useAuthStore();

  useEffect(() => {
    setIsLogin(isAuthenticated);
  }, [isAuthenticated]);

  const { id } = useParams();
  const resumeId = Number(id);

  // 일반 댓글 추가 함수
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

  // AI 피드백 추가 함수 (일반 댓글 추가 로직과 동일하게 작동)
  const handleAiFeedback = async () => {
    if (!resumeId) {
      console.error("resumeId가 없습니다.");
      return;
    }
    try {
      const response = await postAiFeedback(resumeId);
      if (!response?.data?.result?.feedback) {
        console.error("AI 피드백 응답이 올바르지 않습니다.", response);
        return;
      }
      const aiFeedbackContent = response.data.result.feedback;
      // AI 피드백을 일반 댓글 추가와 동일한 방식으로 추가
      addFeedbackPoint({
        content: `AI피드백: ${aiFeedbackContent}`,
        xCoordinate: 0,
        yCoordinate: 0,
        pageNumber: 1,
      });
    } catch (error) {
      console.error("Failed to fetch AI feedback:", error);
    }
  };

  return (
    <div className="flex flex-col h-full justify-between">
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
