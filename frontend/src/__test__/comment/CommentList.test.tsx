import { describe, it, expect, vi } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import CommentList from "../../components/comment/CommentList";
import { FeedbackPoint } from "../../types";

describe("CommentList 테스트 ", () => {
  const mockFeedbackPoints: FeedbackPoint[] = [
    {
      id: 1,
      content: "첫번째 댓글",
      xCoordinate: 10,
      yCoordinate: 20,
      pageNumber: 1,
      createdAt: "2024-12-10T10:00:00Z",
      updatedAt: "2024-12-10T12:00:00Z",
    },
    {
      id: 2,
      content: "두번쨰 댓글",
      xCoordinate: 15,
      yCoordinate: 25,
      pageNumber: 1,
      createdAt: "2024-12-11T10:00:00Z",
      updatedAt: "2024-12-11T12:00:00Z",
    },
  ];

  const deleteFeedbackPoint = vi.fn();
  const editFeedbackPoint = vi.fn();

  it("렌더링 확인", () => {
    //Given: 사용자가 댓글을 확인하려고 할때

    // When: 댓글이 렌더링 되어있을 때
    render(
      <CommentList
        feedbackPoints={mockFeedbackPoints}
        deleteFeedbackPoint={vi.fn()}
        editFeedbackPoint={vi.fn()}
        hoveredCommentId={null}
        setHoveredCommentId={vi.fn()}
      />
    );

    // Then: 피드백이 렌더링
    mockFeedbackPoints.forEach((item) => {
      expect(screen.getByText(item.content)).toBeInTheDocument();
    });
  });

  it("delete 버튼 작동", () => {
    //Given: 사용자가 댓글을 삭제하고 싶은 상황
    render(
      <CommentList
        feedbackPoints={mockFeedbackPoints}
        deleteFeedbackPoint={deleteFeedbackPoint}
        editFeedbackPoint={vi.fn()}
        hoveredCommentId={null}
        setHoveredCommentId={vi.fn()}
      />
    );

    //When: 삭제 버튼을 클릭했을 떄
    const deleteButtons = screen.getAllByText("Delete");
    //모든 delete 버튼을 찾아서 첫번째 버튼을 클릭
    // getAllByText를 사용하는 것이  data-testid="EditButton"를 사용하는 것보다 더 좋을까?
    //-> 텍스트에 변동이 생기는 경우가 아니면 getAllByText를 사용하는 것이 덜 번거롭다고 생각됨
    fireEvent.click(deleteButtons[0]);

    //Then: deleteFeedbackPoint 함수가 호출
    expect(deleteFeedbackPoint).toHaveBeenCalledWith(mockFeedbackPoints[0].id);
  });

  it("edit 버튼 작동", () => {
    //Given: 사용자가 댓글을 수정하고 싶은 상황
    render(
      <CommentList
        feedbackPoints={mockFeedbackPoints}
        deleteFeedbackPoint={vi.fn()}
        editFeedbackPoint={editFeedbackPoint}
        hoveredCommentId={null}
        setHoveredCommentId={vi.fn()}
      />
    );

    //When: 수정 버튼을 클릭했을 때
    const editButtons = screen.getAllByText("Edit");
    fireEvent.click(editButtons[1]);

    //Then: editFeedbackPoint 함수가 호출
    expect(editFeedbackPoint).toHaveBeenCalledWith(mockFeedbackPoints[1]);
  });

  it("마우스 hover시 배경색 변경", () => {
    const setHoveredCommentId = vi.fn();
    render(
      <CommentList
        feedbackPoints={mockFeedbackPoints}
        deleteFeedbackPoint={vi.fn()}
        editFeedbackPoint={vi.fn()}
        hoveredCommentId={1}
        setHoveredCommentId={setHoveredCommentId}
      />
    );

    const firstComment = screen.getByText("첫번째 댓글").closest("li");
    expect(firstComment).toHaveClass("bg-blue-100");
  });
});
