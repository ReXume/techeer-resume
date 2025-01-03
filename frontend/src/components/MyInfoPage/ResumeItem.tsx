import React from "react";
import { FileText } from "lucide-react";

interface Resume {
  id: number;
  version: string;
  views: number;
  date: string;
  status: "최종" | "임시";
}

interface ResumeItemProps {
  resume: Resume;
}

const ResumeItem: React.FC<ResumeItemProps> = ({ resume }) => {
  return (
    <div className="flex items-center justify-between p-4 border rounded-lg hover:bg-gray-50">
      <div className="flex items-center space-x-4">
        <FileText className="w-5 h-5 text-gray-400" />
        <div>
          <h4 className="font-medium">{resume.version}</h4>
          <p className="text-sm text-gray-500">조회수 {resume.views}</p>
        </div>
      </div>
      <div className="flex items-center space-x-4">
        <span className="text-sm text-gray-500">{resume.date}</span>
        <span
          className={`px-3 py-1 rounded-full text-sm ${
            resume.status === "최종"
              ? "bg-blue-100 text-blue-700"
              : "bg-gray-100 text-gray-700"
          }`}
        >
          {resume.status}
        </span>
      </div>
    </div>
  );
};

export default ResumeItem;
