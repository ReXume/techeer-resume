import React, { useEffect, useState } from "react";

import CommentList from "./CommentList";
import CommentForm from "./CommentForm";
import ErrorMessage from "../UI/ErrorMessage.tsx";
import LoadingSpinner from "../UI/LoadingSpinner.tsx";
import { AddFeedbackPoint, FeedbackPoint } from "../../types.ts";
import useAuthStore from "../../store/authStore.ts";
import { postAiFeedback } from "../../api/feedbackApi";

import { useParams } from "react-router-dom";

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
  const { id } = useParams();
  const resumeId = Number(id);

  const handleAiFeedback = async () => {
    try {
      console.log("AI 피드백 요청 시작:", resumeId);
      const response = await postAiFeedback(resumeId);
      console.log("AI 피드백 응답:", response);

      if (!response) {
        console.error("AI 피드백 응답이 올바르지 않습니다.", response);
        return;
      }

      // handleAddComment 함수를 사용하여 AI 피드백을 추가합니다.
      handleAddComment(`AI피드백: ${response}`);
      console.log("AI 피드백 추가 성공!");
    } catch (error) {
      console.error("Failed to add AI feedback", error);
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
      <div className="mt-6">
        <h4 className="text-md font-medium text-gray-700 mb-2">댓글 작성</h4>
        <CommentForm
          onAdd={handleAddComment}
          onAiFeedback={handleAiFeedback}
          disabled={!isLogin}
        />
      </div>
    </div>
  );
}

export default CommentSection;
