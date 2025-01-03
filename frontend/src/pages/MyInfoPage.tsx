import { useState } from "react";
import { Bookmark, FileText, User } from "lucide-react";
import Navbar from "../components/common/Navbar.tsx";
import ResumeItem from "../components/MyInfoPage/ResumeItem.tsx";
import BookmarkItem from "../components/MyInfoPage/BookmarkItem.tsx";

interface Resume {
  id: number;
  version: string;
  views: number;
  date: string;
  status: "최종" | "임시";
}

interface Bookmark {
  id: number;
  title: string;
  views: number;
  date: string;
}

function MyInfoPage() {
  const [activeTab, setActiveTab] = useState<"resume" | "bookmark">("resume");
  const [role] = useState<"테커" | "외부" | "기업">("테커");
  const [userName] = useState("김테커");

  const resumes: Resume[] = [
    {
      id: 1,
      version: "이력서 v1",
      views: 45,
      date: "2024-01-15",
      status: "최종",
    },
    {
      id: 2,
      version: "이력서 v2",
      views: 23,
      date: "2024-01-20",
      status: "임시",
    },
  ];

  const bookmarks: Bookmark[] = [
    {
      id: 1,
      title: "정테커의 이력서",
      views: 100,
      date: "2024-01-25",
    },
    {
      id: 2,
      title: "이테커의 이력서",
      views: 75,
      date: "2024-02-10",
    },
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="pt-5 bg-white">
        <Navbar />
      </div>
      <div className="p-5 mt-5">
        <div className="max-w-6xl mx-auto px-4 py-8 mt-10">
          <div className="flex gap-8">
            <div className="w-72 bg-white rounded-lg shadow-sm p-6 h-fit">
              <div className="flex flex-col items-center">
                <div className="w-24 h-24 bg-gray-200 rounded-full mb-4 flex items-center justify-center">
                  <User className="w-12 h-12 text-gray-400" />
                </div>
                <h2 className="text-xl font-bold mb-1">{userName}</h2>
                <p className="text-gray-500 mb-4">{role}</p>
                <p className="text-sm text-gray-600 text-center">한 줄 소개</p>
              </div>
            </div>

            <div className="flex-1">
              <div className="flex space-x-1 mb-6">
                <button
                  onClick={() => setActiveTab("resume")}
                  className={`flex items-center px-6 py-3 rounded-lg ${
                    activeTab === "resume"
                      ? "bg-gray-100 text-gray-900"
                      : "text-gray-500 hover:bg-gray-50"
                  }`}
                >
                  <FileText className="w-5 h-5 mr-2" />
                  나의 이력서
                </button>
                <button
                  onClick={() => setActiveTab("bookmark")}
                  className={`flex items-center px-6 py-3 rounded-lg ${
                    activeTab === "bookmark"
                      ? "bg-gray-100 text-gray-900"
                      : "text-gray-500 hover:bg-gray-50"
                  }`}
                >
                  <Bookmark className="w-5 h-5 mr-2" />
                  북마크
                </button>
              </div>

              {activeTab === "resume" && (
                <div className="bg-white rounded-lg shadow-sm p-6">
                  <div className="flex justify-between items-center mb-6">
                    <h3 className="text-lg font-semibold">나의 이력서 목록</h3>
                    <span className="text-sm text-gray-500">
                      {resumes.length}개
                    </span>
                  </div>
                  <div className="space-y-4">
                    {resumes.map((resume) => (
                      <ResumeItem key={resume.id} resume={resume} />
                    ))}
                  </div>
                </div>
              )}

              {activeTab === "bookmark" && (
                <div className="bg-white rounded-lg shadow-sm p-6">
                  <h3 className="text-lg font-semibold mb-6">
                    북마크한 이력서
                  </h3>
                  <div className="space-y-4">
                    {bookmarks.length > 0 ? (
                      bookmarks.map((bookmark) => (
                        <BookmarkItem key={bookmark.id} bookmark={bookmark} />
                      ))
                    ) : (
                      <div className="text-center text-gray-500 py-8">
                        북마크한 이력서가 없습니다
                      </div>
                    )}
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default MyInfoPage;
