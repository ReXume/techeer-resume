// import { useState, useEffect } from "react";
import ResumeItem from "../../components/MyInfoPage/ResumeItem.tsx";
import { ResumeType } from "../../dataType.ts";

function MyInfoPage() {
  const resumes: ResumeType[] = [
    {
      id: 1,
      version: "이력서 v1",
      date: "2024-01-15",
    },
    {
      id: 2,
      version: "이력서 v2",
      date: "2024-01-20",
    },
  ];
  return (
    <div className="flex gap-8">
      <div className="flex-1">
        <div className="bg-white rounded-lg shadow-sm p-6">
          <div className="flex justify-between items-center mb-6">
            <h3 className="text-lg font-semibold">나의 이력서 목록</h3>
            <span className="text-sm text-gray-500">{resumes.length}개</span>
          </div>
          <div className="space-y-4">
            {resumes.map((resume) => (
              <ResumeItem key={resume.id} resume={resume} />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

export default MyInfoPage;
