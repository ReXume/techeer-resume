import React, { useState, useRef, useEffect } from "react";
import * as pdfjsLib from "pdfjs-dist";
import "pdfjs-dist/web/pdf_viewer.css";
import CommentForm from "../comment/CommentForm";
import { FeedbackPoint } from "../../types";

// PDF.js Worker 설정 (프로젝트에 맞게 경로 수정)
pdfjsLib.GlobalWorkerOptions.workerSrc =
  "https://unpkg.com/pdfjs-dist@3.4.120/build/pdf.worker.min.js";

// 영역 피드백 데이터 예시
const Comment = [
  {
    feedback_id: 1,
    resume_id: 1,
    content: "Great work on your resume!",
    page_number: 1,
    xcoordinate1: 25,
    xcoordinate2: 60,
    ycoordinate1: 40,
    ycoordinate2: 60,
  },
  {
    feedback_id: 2,
    resume_id: 1,
    content: "Maybe add more details about your experience.",
    page_number: 2,
    xcoordinate1: 25,
    xcoordinate2: 80,
    ycoordinate1: 40,
    ycoordinate2: 90,
  },
];

const PDF = ({ pdf, pageNumber, addFeedbackPoint, editFeedbackPoint }) => {
  const canvasRef = useRef(null);
  const [selectedArea, setSelectedArea] = useState(null);
  const [isSelecting, setIsSelecting] = useState(false);
  const [startPos, setStartPos] = useState({ x: 0, y: 0 });
  const [addingFeedback, setAddingFeedback] = useState(null);
  const [editingFeedback, setEditingFeedback] = useState<FeedbackPoint | null>(
    null
  );

  useEffect(() => {
    const loadPage = async () => {
      const page = await pdf.getPage(pageNumber);
      const viewport = page.getViewport({ scale: 2 });
      const canvas = canvasRef.current;
      const context = canvas.getContext("2d");
      canvas.width = viewport.width;
      canvas.height = viewport.height;
      const renderContext = {
        canvasContext: context,
        viewport,
      };
      page.render(renderContext);
    };
    loadPage();
  }, [pdf, pageNumber]);

  const handleMouseDown = (e) => {
    const rect = e.currentTarget.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    setStartPos({ x, y });
    setSelectedArea({ x, y, width: 0, height: 0 });
    setIsSelecting(true);
  };

  const handleMouseMove = (e) => {
    if (!isSelecting) return;
    const rect = e.currentTarget.getBoundingClientRect();
    const currentX = e.clientX - rect.left;
    const currentY = e.clientY - rect.top;
    setSelectedArea({
      x: Math.min(startPos.x, currentX),
      y: Math.min(startPos.y, currentY),
      width: Math.abs(currentX - startPos.x),
      height: Math.abs(currentY - startPos.y),
    });
  };

  const handleMouseUp = (e) => {
    setIsSelecting(false);
    if (selectedArea) {
      // 컨테이너 크기에 대한 백분율로 변환
      const containerRect = e.currentTarget.getBoundingClientRect();
      const percentX = (selectedArea.x / containerRect.width) * 100;
      const percentY = (selectedArea.y / containerRect.height) * 100;
      setAddingFeedback({ x: percentX, y: percentY, pageNumber });
    }
  };

  const handleAddSubmit = (comment) => {
    if (addingFeedback) {
      addFeedbackPoint({
        pageNumber: addingFeedback.pageNumber,
        xcoordinate: addingFeedback.x,
        ycoordinate: addingFeedback.y,
        content: comment,
      });
      setAddingFeedback(null);
    }
  };

  return (
    <div
      style={{ position: "relative", marginBottom: "20px" }}
      onMouseDown={handleMouseDown}
      onMouseMove={handleMouseMove}
      onMouseUp={handleMouseUp}
    >
      <canvas ref={canvasRef} style={{ display: "block" }} />

      {/* 마우스로 선택한 영역 표시 */}
      {selectedArea && (
        <div
          style={{
            position: "absolute",
            left: selectedArea.x,
            top: selectedArea.y,
            width: selectedArea.width,
            height: selectedArea.height,
            border: "2px dashed blue",
            pointerEvents: "none",
          }}
        />
      )}

      {/* Comment 배열의 각 피드백 영역 표시 (현재 페이지에 해당하는 항목만) */}
      {Comment &&
        Array.isArray(Comment) &&
        Comment.filter((item) => item.page_number === pageNumber).map(
          (item) => {
            // 두 좌표 값 중 작은 값이 left/top, 큰 값의 차이가 width/height가 됩니다.
            const left = Math.min(item.xcoordinate1, item.xcoordinate2);
            const top = Math.min(item.ycoordinate1, item.ycoordinate2);
            const width = Math.abs(item.xcoordinate2 - item.xcoordinate1);
            const height = Math.abs(item.ycoordinate2 - item.ycoordinate1);
            return (
              <div
                key={item.feedback_id}
                style={{
                  position: "absolute",
                  left: `${left}%`,
                  top: `${top}%`,
                  width: `${width}%`,
                  height: `${height}%`,
                  border: "2px solid red",
                  background: "rgba(255,0,0,0.3)",
                }}
              >
                <div
                  style={{
                    position: "absolute",
                    top: -25,
                    left: 0,
                    background: "rgba(255,255,255,0.8)",
                    padding: "2px 5px",
                    border: "1px solid #ccc",
                    borderRadius: "3px",
                    fontSize: "0.9em",
                  }}
                >
                  {item.content}
                </div>
              </div>
            );
          }
        )}

      {addingFeedback && (
        <CommentForm
          position={{ x: addingFeedback.x, y: addingFeedback.y }}
          onSubmit={handleAddSubmit}
        />
      )}
      {editingFeedback && (
        <CommentForm
          position={{
            x: editingFeedback.xCoordinate,
            y: editingFeedback.yCoordinate,
          }}
          initialComment={editingFeedback.content}
        />
      )}
    </div>
  );
};

export default PDF;
