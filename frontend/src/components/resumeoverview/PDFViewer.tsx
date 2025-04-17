import { useEffect, useState } from "react";
import * as pdfjsLib from "pdfjs-dist";
import PDF from "./PDF";

pdfjsLib.GlobalWorkerOptions.workerSrc = `https://unpkg.com/pdfjs-dist@${pdfjsLib.version}/build/pdf.worker.min.js`;

const PDFViewer = ({
  pdfSrc,
  feedbackData,
  addFeedbackPoint,
  editFeedbackPoint,
  feedbackPoints,
  hoveredCommentId,
  setHoveredCommentId,
}: any) => {
  const [pdf, setPdf] = useState(null);
  const [numPages, setNumPages] = useState(0);

  useEffect(() => {
    const loadPdf = async () => {
      const loadingTask = pdfjsLib.getDocument(pdfSrc);
      const loadedPdf = await loadingTask.promise;
      setPdf(loadedPdf);
      setNumPages(loadedPdf.numPages);
    };
    loadPdf();
  }, [pdfSrc]);

  if (!pdf) return <div>PDF 로딩 중...</div>;

  return (
    <div
      style={{
        width: 1200,
        margin: "auto",
        overflowY: "auto",
        maxHeight: "90vh",
      }}
    >
      {Array.from({ length: numPages }, (_, index) => (
        <PDF
          key={index + 1}
          pdf={pdf}
          pageNumber={index + 1}
          feedback={feedbackData && feedbackData[index + 1]}
          addFeedbackPoint={addFeedbackPoint}
          editFeedbackPoint={editFeedbackPoint}
          feedbackPoints={feedbackPoints}
          hoveredCommentId={hoveredCommentId}
          setHoveredCommentId={setHoveredCommentId}
        />
      ))}
    </div>
  );
};

export default PDFViewer;
