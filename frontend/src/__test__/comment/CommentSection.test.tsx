import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import { vi, describe, it, expect } from "vitest";
import CommentSection from "../../components/comment/CommentSection";

describe("CommentSection 테스트", () => {
  const mockAddFeedbackPoint = vi.fn();
  const mockDeleteFeedbackPoint = vi.fn();
  const mockEditFeedbackPoint = vi.fn();
  const mockSetHoveredCommentId = vi.fn();
  const mockHandleAiFeedback = vi.fn();

  const mockFeedbackPoints = [
    {
      id: 1,
      content: "Test feedback",
      xCoordinate: 100,
      yCoordinate: 200,
      pageNumber: 1,
      createdAt: "2024-12-10T10:00:00Z",
      updatedAt: "2024-12-10T12:00:00Z",
    },
  ];

  it("댓글 목록과 로딩 상태 렌더링 확인", () => {
    //Given : 사용자가 댓글을 확인하고 있는 상황

    //when : 아직 댓글을 호출했지만 댓글이 로딩중인 상황
    render(
      <CommentSection
        feedbackPoints={mockFeedbackPoints}
        addFeedbackPoint={mockAddFeedbackPoint}
        deleteFeedbackPoint={mockDeleteFeedbackPoint}
        editFeedbackPoint={mockEditFeedbackPoint}
        hoveredCommentId={null}
        setHoveredCommentId={mockSetHoveredCommentId}
        handleAiFeedback={mockHandleAiFeedback}
        loading={true}
        error=""
      />
    );

    //Then : 로딩화면이 렌더링된다
    expect(screen.getByText("Loading..."));
  });

  it("에러 메시지 렌더링 확인", () => {
    //Given : 사용자가 댓글을 확인하고 있는 상황

    //When : 댓글을 호출했지만 에러가 발생한 상황
    render(
      <CommentSection
        feedbackPoints={[]}
        addFeedbackPoint={mockAddFeedbackPoint}
        deleteFeedbackPoint={mockDeleteFeedbackPoint}
        editFeedbackPoint={mockEditFeedbackPoint}
        hoveredCommentId={null}
        setHoveredCommentId={mockSetHoveredCommentId}
        handleAiFeedback={mockHandleAiFeedback}
        loading={false}
        error="Error occurred"
      />
    );
    //Then : 에러 메시지가 렌더링된다
    expect(screen.getByText("Error occurred")).toBeInTheDocument();
  });
});
