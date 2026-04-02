import React, { useState } from "react";
import { Bookmark, FileText, User, History } from "lucide-react";
import GlobalNavbar from "../components/common/GlobalNavbar";
import ResumeTap from "../components/MyInfoPage/ResumeTap.tsx";
import UserInfo from "../components/MyInfoPage/UserInfo.tsx";
import ProfileTab from "../components/MyInfoPage/ProfileTab.tsx";
import ApplyHistoryTab from "../components/MyInfoPage/ApplyHistoryTab.tsx";

const BookmarkTap = React.lazy(
  () => import("../components/MyInfoPage/BookmarkTap.tsx")
);

type TabId = "profile" | "history" | "bookmark" | "resume";

interface Tab {
  id: TabId;
  label: string;
  icon: React.ReactNode;
}

const TABS: Tab[] = [
  { id: "profile", label: "프로필", icon: <User className="w-4 h-4" /> },
  { id: "history", label: "지원 이력", icon: <History className="w-4 h-4" /> },
  { id: "bookmark", label: "북마크", icon: <Bookmark className="w-4 h-4" /> },
  { id: "resume", label: "내 이력서", icon: <FileText className="w-4 h-4" /> },
];

function MyInfoPage() {
  const [activeTab, setActiveTab] = useState<TabId>("profile");

  const renderContent = () => {
    switch (activeTab) {
      case "profile":
        return <ProfileTab />;
      case "history":
        return <ApplyHistoryTab />;
      case "bookmark":
        return (
          <React.Suspense
            fallback={
              <div className="bg-white rounded-lg shadow-sm p-6 text-gray-400 text-sm">
                로딩 중...
              </div>
            }
          >
            <BookmarkTap />
          </React.Suspense>
        );
      case "resume":
        return <ResumeTap />;
      default:
        return null;
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <GlobalNavbar />

      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        <div className="flex flex-col lg:flex-row gap-8">
          {/* Sidebar */}
          <aside className="w-full lg:w-72 flex-shrink-0">
            <UserInfo />
          </aside>

          {/* Main content */}
          <div className="flex-1 min-w-0">
            {/* Tabs */}
            <div className="flex gap-1 bg-white rounded-xl shadow-sm p-1.5 mb-6">
              {TABS.map((tab) => (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`flex items-center gap-1.5 flex-1 justify-center px-3 py-2.5 rounded-lg text-sm font-medium transition-all ${
                    activeTab === tab.id
                      ? "bg-blue-600 text-white shadow-sm"
                      : "text-gray-500 hover:text-gray-800 hover:bg-gray-100"
                  }`}
                >
                  {tab.icon}
                  <span className="hidden sm:inline">{tab.label}</span>
                </button>
              ))}
            </div>

            {renderContent()}
          </div>
        </div>
      </div>
    </div>
  );
}

export default MyInfoPage;
