import React, {
  useState,
  ChangeEvent,
  FormEvent,
  useRef,
  useEffect,
} from "react";
import { postAiFeedback } from "../../api/feedbackApi";
import { useParams } from "react-router-dom";

interface Position {
  x: number; // 백분율
  y: number; // 백분율
}

interface CommentFormProps {
  onAdd?: (comment: string) => void; // 사이드바용
  onSubmit?: (comment: string) => void; // 메인 영역용
  onCancel?: () => void; // 메인 영역용
  position?: Position; // 메인 영역용
  initialComment?: string; // 메인 영역용 (수정 시)
  onAiFeedback?: () => void; // AI 피드백 버튼 클릭 시 부모로 넘길 콜백(필요하다면)
  disabled?: boolean;

  resumeId?: number;
}

function CommentForm({
  onAdd,
  onSubmit,
  onCancel,
  position,
  initialComment = "",
  disabled = false,
}: CommentFormProps) {
  const [comment, setComment] = useState<string>(initialComment);

  // textarea에 대한 참조 생성
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  const handleChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
    setComment(e.target.value);
  };
  const { id } = useParams();
  const resumeId = Number(id);

  const handleSubmit = (
    e: FormEvent<HTMLFormElement> | React.MouseEvent<HTMLButtonElement>
  ) => {
    e.preventDefault();
    if (comment.trim() === "") return;

    if (onAdd) {
      onAdd(comment);
    }

    if (onSubmit) {
      onSubmit(comment);
    }

    setComment("");
  };

  const handleAiFeedback = async () => {
    console.log("AI 피드백 요청 중, resumeId:", resumeId);
    if (!resumeId) {
      console.error("resumeId가 없습니다.");
      return;
    }

    try {
      console.log("AI 피드백 요청 중, resumeId:", resumeId);
      const response = await postAiFeedback(resumeId);

      if (!response?.data?.result?.feedback) {
        console.log("AI 피드백 API 응답:", response);
        console.error("AI 피드백 응답이 올바르지 않습니다.", response);
        return;
      }

      const aiFeedbackContent = response.data.result.feedback;

      // 기존 댓글 추가 방식과 동일하게 처리
      onAdd?.(`AI피드백: ${aiFeedbackContent}`);
    } catch (error) {
      console.error("AI 피드백 요청 실패:", error);
    }
  };

  // 컴포넌트가 마운트될 때 textarea에 포커스 설정
  useEffect(() => {
    if (textareaRef.current) {
      textareaRef.current.focus();
    }
  }, []);

  // 폼이 메인 영역에 위치할 경우 스타일 적용
  const formStyles: React.CSSProperties = position
    ? {
        position: "absolute",
        left: `${position.x}%`,
        top: `${position.y}%`,
        transform: "translate(0%, -100%)",
        width: "400px",
        zIndex: 10,
        marginLeft: "10px",
        marginTop: "-10px",
      }
    : {
        position: "relative",
        zIndex: 10,
      };

  return (
    <div
      className="bg-white border rounded shadow-lg p-2 z-10 transition-transform duration-300 ease-in-out"
      style={formStyles}
      onClick={(e) => e.stopPropagation()} // 이벤트 전파 중단
    >
      <form onSubmit={handleSubmit} className="flex flex-col">
        <textarea
          ref={textareaRef} // textarea에 ref 할당
          placeholder={
            onAdd ? "댓글을 입력하세요..." : "피드백을 입력하세요..."
          }
          className="w-full h-24 p-2 border rounded resize-none"
          value={comment}
          onChange={handleChange}
          disabled={disabled}
        />
        <div className="flex justify-end mt-2 space-x-2">
          {/* 메인 영역용 취소 버튼 */}
          {onCancel && (
            <button
              type="button"
              className="px-3 py-1 bg-gray-300 text-gray-700 rounded"
              onClick={onCancel}
              disabled={disabled}
            >
              취소
            </button>
          )}
          <button
            type="submit"
            className={`px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600 ${
              disabled ? "opacity-50 cursor-not-allowed" : ""
            }`}
            disabled={disabled}
          >
            {onSubmit ? (initialComment ? "수정" : "추가") : "댓글 추가"}
          </button>
          <button
            type="button"
            className="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
            onClick={handleAiFeedback}
            disabled={disabled}
          >
            AI 피드백
          </button>
        </div>
      </form>
    </div>
  );
}

export default CommentForm;
