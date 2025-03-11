import React, { useState, useRef, useEffect } from "react";
import * as pdfjsLib from "pdfjs-dist";
import "pdfjs-dist/web/pdf_viewer.css";
import CommentForm from "../comment/CommentForm";
import { AddFeedbackPoint, FeedbackPoint } from "../../types";

// PDF.js Worker 설정 (프로젝트에 맞게 경로 수정)
pdfjsLib.GlobalWorkerOptions.workerSrc =
  "https://unpkg.com/pdfjs-dist@3.4.120/build/pdf.worker.min.js";

const PDF = ({
  pdf,
  feedback,
  pageNumber,
  addFeedbackPoint,
  editFeedbackPoint,
  hoveredCommentId,
  setHoveredCommentId,
}) => {
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

  useEffect(() => {
    console.log(feedback);
  }, [feedback]);

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
      // 부모 요소의 크기를 가져와 백분율로 변환
      const containerRect = e.currentTarget.getBoundingClientRect();
      const percentX = (selectedArea.x / containerRect.width) * 100;
      const percentY = (selectedArea.y / containerRect.height) * 100;
      setAddingFeedback({ x: percentX, y: percentY, pageNumber });
    }
  };

  const handleAddSubmit = (comment: string) => {
    if (addingFeedback) {
      console.log(addingFeedback);
      addFeedbackPoint({
        pageNumber: addingFeedback.pageNumber,
        xcoordinate: addingFeedback.x,
        ycoordinate: addingFeedback.y,
        content: comment,
      });
      setAddingFeedback(null);
    }
  };

  const handleEditSubmit = () => {
    if (editingFeedback) {
      const updatedPoint: AddFeedbackPoint = { ...editingFeedback };
      editFeedbackPoint(updatedPoint);
      setEditingFeedback(null);
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
        >
          {feedback && feedback.text && (
            <div
              style={{
                position: "absolute",
                top: -30,
                left: 0,
                background: "rgba(255,255,255,0.8)",
                padding: "2px 5px",
                border: "1px solid #ccc",
                borderRadius: "3px",
                fontSize: "0.9em",
              }}
            >
              {feedback.text}
            </div>
          )}
        </div>
      )}
      {addingFeedback && (
        <CommentForm
          position={{ x: addingFeedback.x, y: addingFeedback.y }}
          onSubmit={handleAddSubmit}
          //onCancel={handleCancel}
        />
      )}
      {editingFeedback && (
        <CommentForm
          position={{
            x: editingFeedback.xCoordinate,
            y: editingFeedback.yCoordinate,
          }}
          initialComment={editingFeedback.content}
          onSubmit={handleEditSubmit}
          onCancel={handleCancel}
        />
      )}
    </div>
  );
};

export default PDF;
