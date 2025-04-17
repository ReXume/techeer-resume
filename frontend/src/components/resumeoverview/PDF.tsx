import React, { useState, useRef, useEffect } from "react";
import * as pdfjsLib from "pdfjs-dist";
import "pdfjs-dist/web/pdf_viewer.css";
import CommentForm from "../comment/CommentForm";
import { FeedbackPoint } from "../../types";

// PDF.js Worker 설정
pdfjsLib.GlobalWorkerOptions.workerSrc = `https://unpkg.com/pdfjs-dist@${pdfjsLib.version}/build/pdf.worker.min.js`;

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

const PDF = ({
  pdf,
  pageNumber,
  feedback,
  addFeedbackPoint,
  feedbackPoints,
  editFeedbackPoint,
}: any) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const renderTaskRef = useRef<any>(null);

  const [selectedArea, setSelectedArea] = useState<{
    x: number;
    y: number;
    width: number;
    height: number;
  } | null>(null);
  const [isSelecting, setIsSelecting] = useState(false);
  const [startPos, setStartPos] = useState({ x: 0, y: 0 });
  const [addingFeedback, setAddingFeedback] = useState<{
    x1: number;
    x2: number;
    y1: number;
    y2: number;
    pageNumber: number;
  } | null>(null);
  const [editingFeedback, setEditingFeedback] = useState<FeedbackPoint | null>(
    null
  );

  useEffect(() => {
    let cancelled = false;

    const loadPage = async () => {
      const page = await pdf.getPage(pageNumber);
      const viewport = page.getViewport({ scale: 2, rotation: 0 });
      const canvas = canvasRef.current!;
      const context = canvas.getContext("2d")!;

      canvas.width = viewport.width;
      canvas.height = viewport.height;

      // 이전 렌더링이 있다면 취소
      if (renderTaskRef.current) {
        renderTaskRef.current.cancel();
      }

      // 새로운 렌더링 시작
      renderTaskRef.current = page.render({
        canvasContext: context,
        viewport,
      });

      try {
        await renderTaskRef.current.promise;
        if (cancelled) return;
      } catch (err: any) {
        // 취소된 경우만 무시
        if (err?.name !== "RenderingCancelledException") {
          console.error("PDF 렌더링 에러:", err);
        }
      }
    };

    loadPage();
    console.log(feedback);

    return () => {
      cancelled = true;
      if (renderTaskRef.current) {
        renderTaskRef.current.cancel();
      }
    };
  }, [pdf, pageNumber]);

  const handleMouseDown = (e: React.MouseEvent) => {
    const rect = e.currentTarget.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    setStartPos({ x, y });
    setSelectedArea({ x, y, width: 0, height: 0 });
    setIsSelecting(true);
  };

  const handleMouseMove = (e: React.MouseEvent) => {
    if (!isSelecting || !selectedArea) return;
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

  const handleMouseUp = (e: React.MouseEvent) => {
    setIsSelecting(false);
    if (selectedArea) {
      const containerRect = e.currentTarget.getBoundingClientRect();
      const percentX1 = (selectedArea.x / containerRect.width) * 100;
      const percentX2 =
        ((selectedArea.x + selectedArea.width) / containerRect.width) * 100;
      const percentY1 = (selectedArea.y / containerRect.height) * 100;
      const percentY2 =
        ((selectedArea.y + selectedArea.height) / containerRect.height) * 100;
      setAddingFeedback({
        x1: percentX1,
        x2: percentX2,
        y1: percentY1,
        y2: percentY2,
        pageNumber,
      });
    }
  };

  const handleAddSubmit = (comment: string) => {
    if (addingFeedback) {
      addFeedbackPoint({
        pageNumber: addingFeedback.pageNumber,
        x1: addingFeedback.x1,
        x2: addingFeedback.x2,
        y1: addingFeedback.y1,
        y2: addingFeedback.y2,
        content: comment,
      });
      setAddingFeedback(null);
    }
  };

  return (
    <div
      style={{ position: "relative", marginBottom: 20 }}
      onMouseDown={handleMouseDown}
      onMouseMove={handleMouseMove}
      onMouseUp={handleMouseUp}
    >
      <canvas ref={canvasRef} style={{ display: "block" }} />

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
      {feedbackPoints
        .filter((item) => item.pageNumber === pageNumber)
        .map((item) => {
          const left = item.x1;
          const top = item.y1;
          const width = item.x2 - item.x1;
          const height = item.y2 - item.y1;
          return (
            <div
              key={item.feedbackId}
              style={{
                position: "absolute",
                left: `${left}%`,
                top: `${top}%`,
                width: `${width}%`,
                height: `${height}%`,
                border: "2px solid red",
                background: "rgba(255,0,0,0.3)",
              }}
              onClick={() => setEditingFeedback(item)}
            >
              <div
                style={{
                  position: "absolute",
                  top: -25,
                  left: 0,
                  background: "rgba(255,255,255,0.8)",
                  padding: "2px 5px",
                  border: "1px solid #ccc",
                  borderRadius: 3,
                  fontSize: "0.9em",
                }}
              >
                {item.content}
              </div>
            </div>
          );
        })}

      {addingFeedback && (
        <CommentForm
          position={{ x1: addingFeedback.x1, y1: addingFeedback.y1 }}
          onSubmit={handleAddSubmit}
        />
      )}
      {editingFeedback && (
        <CommentForm
          position={{
            x1: editingFeedback.xCoordinate,
            y1: editingFeedback.yCoordinate,
          }}
          initialComment={editingFeedback.content}
        />
      )}
    </div>
  );
};

export default PDF;
