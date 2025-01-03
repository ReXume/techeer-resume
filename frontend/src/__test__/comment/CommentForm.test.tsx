import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import { vi, describe, it, expect } from "vitest";
import CommentForm from "../../components/comment/CommentForm";

describe("CommentForm 테스트", () => {
  const mockOnAdd = vi.fn();
  const mockOnSubmit = vi.fn();
  const mockOnCancel = vi.fn();

  it("댓글 입력 필드 렌더링 확인", () => {
    // Given: 사용자가 댓글을 작성하려는 상황
    render(<CommentForm onAdd={mockOnAdd} />);

    // When: 폼이 렌더링될 때
    const textarea = screen.getByPlaceholderText("댓글을 입력하세요...");

    // Then: 입력 필드가 화면에 렌더링되어 있어야 한다
    expect(textarea).toBeInTheDocument();
    expect(textarea).toHaveValue("");
  });

  it("댓글 추가 버튼 작동 확인 (onAdd 호출)", () => {
    // Given: 사용자가 댓글을 입력한 상황
    render(<CommentForm onAdd={mockOnAdd} />);
    const textarea = screen.getByPlaceholderText("댓글을 입력하세요...");
    fireEvent.change(textarea, { target: { value: "새로운 댓글" } });

    // When: "추가" 버튼을 클릭했을 때
    fireEvent.click(screen.getByText("댓글 추가"));

    // Then: onAdd 함수가 호출되고 입력값이 전달되어야 한다
    expect(mockOnAdd).toHaveBeenCalledWith("새로운 댓글");
    expect(textarea).toHaveValue(""); // 입력 필드는 초기화되어야 함
  });

  it("피드백 추가 버튼 작동 확인 (onSubmit 호출)", () => {
    // Given: 사용자가 피드백을 작성하려는 상황
    render(<CommentForm onSubmit={mockOnSubmit} />);
    const textarea = screen.getByPlaceholderText("피드백을 입력하세요...");
    fireEvent.change(textarea, { target: { value: "새로운 피드백" } });

    // When: "추가" 버튼을 클릭했을 때
    fireEvent.click(screen.getByText("추가"));

    // Then: onSubmit 함수가 호출되고 입력값이 전달되어야 한다
    expect(mockOnSubmit).toHaveBeenCalledWith("새로운 피드백");
    expect(textarea).toHaveValue(""); // 입력 필드는 초기화되어야 함
  });

  it("수정 버튼 작동 확인", () => {
    // Given: 사용자가 기존 댓글을 수정하려는 상황
    render(<CommentForm initialComment="기존 댓글" onSubmit={mockOnSubmit} />);

    const textarea = screen.getByDisplayValue("기존 댓글");
    fireEvent.change(textarea, { target: { value: "수정된 댓글" } });

    // When: "수정" 버튼을 클릭했을 때
    fireEvent.click(screen.getByText("수정"));

    // Then: onSubmit 함수가 호출되고 수정된 댓글이 전달되어야 한다
    expect(mockOnSubmit).toHaveBeenCalledWith("수정된 댓글");
  });

  it("취소 버튼 작동 확인", () => {
    // Given: 사용자가 작성 도중 취소하려는 상황
    render(<CommentForm onCancel={mockOnCancel} />);
    const cancelButton = screen.getByText("취소");

    // When: "취소" 버튼을 클릭했을 때
    fireEvent.click(cancelButton);

    // Then: onCancel 함수가 호출되어야 한다
    expect(mockOnCancel).toHaveBeenCalled();
  });

  it("폼의 위치 스타일 적용 확인 (position props)", () => {
    // Given: 메인 영역에서 폼이 특정 위치에 렌더링되도록 설정된 상황
    const position = { x: 50, y: 50 };
    render(<CommentForm position={position} />);

    // When: 폼이 렌더링되었을 때
    const form = screen.getByRole("textbox").closest("div");

    // Then: 폼의 스타일이 position props를 반영해야 한다
    expect(form).toHaveStyle({
      position: "absolute",
      left: "50%",
      top: "50%",
      transform: "translate(0%, -100%)",
    });
  });

  it("입력 필드 비활성화 확인 (disabled props)", () => {
    // Given: 폼이 비활성화된 상태
    render(<CommentForm disabled={true} onAdd={mockOnAdd} />);

    // When: "댓글을 입력하세요..."는 onAdd props가 있을 때 사용
    const textarea = screen.getByPlaceholderText("댓글을 입력하세요...");
    const submitButton = screen.getByText("댓글 추가");

    // Then: 입력 필드와 버튼이 비활성화되어야 한다
    expect(textarea).toBeDisabled();
    expect(submitButton).toBeDisabled();
  });
});
