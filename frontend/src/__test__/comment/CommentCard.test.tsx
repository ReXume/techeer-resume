import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import { vi, describe, it, expect } from "vitest";
import CommentCard from "../../components/comment/CommentCard";

describe("일반 코멘트 카드 테스트", () => {
  const mockComment = {
    id: 1,
    text: "임시 댓글",
    timestamp: new Date(),
    modified: false,
  };

  //외부에서 사용된 함수는 vi로 모킹한다.
  const mockOnDelete = vi.fn();
  const mockOnEdit = vi.fn();

  it("댓글 렌더링 확인", () => {
    //Given : 사용자가 댓글을 확인하고 있다

    //When : 댓글이 렌더링 되어있을 때
    render(
      <CommentCard
        {...mockComment}
        onDelete={mockOnDelete}
        onEdit={mockOnEdit}
      />
    );
    //Then : 댓글이 렌더링 되어있다.
    expect(screen.getByText("임시 댓글")).toBeInTheDocument();
  });

  it("삭제 버튼 작동확인", () => {
    //Given : 사용자가 댓글을 삭제하고 싶은 상횡
    render(
      <CommentCard
        {...mockComment}
        onDelete={mockOnDelete}
        onEdit={mockOnEdit}
      />
    );

    //When : 삭제 버튼을 클릭했을 때
    fireEvent.click(screen.getByText("삭제"));

    //Then : onDelete 함수가 호출되어야 한다.
    expect(mockOnDelete).toHaveBeenCalledWith(mockComment.id);
  });

  it("수정 버튼 작동 확인(handleEditToggle)", () => {
    //Given : 사용자가 댓글을 수정하고 싶은 상황
    render(
      <CommentCard
        {...mockComment}
        onDelete={mockOnDelete}
        onEdit={mockOnEdit}
      />
    );
    //When : 수정 버튼을 클릭했을 때
    fireEvent.click(screen.getByText("수정"));
    //Then : 수정할 수 있는 input이 렌더링 되어야 한다.
    expect(screen.getByDisplayValue(mockComment.text)).toBeInTheDocument();
  });

  it("저장 버튼 작동 확인", () => {
    //Given : 사용자가 댓글을 수정하고 저장하고 싶은 상황
    render(
      <CommentCard
        {...mockComment}
        onDelete={mockOnDelete}
        onEdit={mockOnEdit}
      />
    );
    //When : 수정 버튼을 클릭하고 새로운 내용을 입력하고 저장 버튼을 클릭했을 때
    fireEvent.click(screen.getByText("수정"));
    const input = screen.getByDisplayValue(mockComment.text);
    fireEvent.change(input, { target: { value: "Updated comment" } });
    fireEvent.click(screen.getByText("저장"));
    //Then : onEdit 함수가 호출되어야 한다.
    expect(mockOnEdit).toHaveBeenCalledWith(mockComment.id, "Updated comment");
  });

  it("수정 취소 버튼 작동 확인", () => {
    //Given : 사용자가 댓글을 수정하고 취소하고 싶은 상황
    render(
      <CommentCard
        {...mockComment}
        onDelete={mockOnDelete}
        onEdit={mockOnEdit}
      />
    );
    const toggleButton = screen.getByTestId("EditButton");

    //When : 수정 버튼을 클릭하고 새로운 내용을 입력하고 취소 버튼을 클릭했을 때
    fireEvent.click(screen.getByText("수정"));
    const input = screen.getByDisplayValue(mockComment.text);
    fireEvent.change(input, { target: { value: "Updated comment" } });
    fireEvent.click(toggleButton);

    //Then : 수정할 수 있는 input이 사라져야 한다.
    expect(
      screen.queryByDisplayValue("Updated comment")
    ).not.toBeInTheDocument();
  });
});

describe("수정된 코멘트 카드 테스트", () => {
  const mockComment = {
    id: 1,
    text: "수정된 댓글",
    timestamp: new Date(),
    modified: true,
  };

  const mockOnDelete = vi.fn();
  const mockOnEdit = vi.fn();

  it("수정된 댓글 렌더링 확인", () => {
    //Given : 사용자가 댓글을 확인하고 있다

    //When : 댓글이 렌더링 되어있을 때
    render(
      <CommentCard
        {...mockComment}
        onDelete={mockOnDelete}
        onEdit={mockOnEdit}
      />
    );
    //Then : 댓글이 렌더링 되어있다.
    expect(
      screen.getByText((content) => content.includes("(수정됨)"))
    ).toBeInTheDocument();
  });
});
