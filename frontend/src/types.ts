export type FeedbackPoint = {
  id: number;
  content: string;
  xCoordinate: number;
  yCoordinate: number;
  pageNumber: number | 1;
  createdAt: string;
  updatedAt: string;
  deletedAt?: string | null;
};

export type ResumeData = {
  resumeId: number;
  userName: string;
  position: string;
  career: number;
  techStackNames: string[];
  fileUrl: string;
  feedbackResponses: FeedbackPoint[];
  previousResumeId: number | null;
  laterResumeId: number | null;
};

export type AddFeedbackPoint = {
  content: string;
  x1: number;
  x2: number;
  y1: number;
  y2: number;
  pageNumber: number;
};
