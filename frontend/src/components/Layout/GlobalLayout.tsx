import React from "react";
import GlobalNavbar from "../common/GlobalNavbar";

interface GlobalLayoutProps {
  children: React.ReactNode;
}

function GlobalLayout({ children }: GlobalLayoutProps): React.ReactElement {
  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <GlobalNavbar />
      <main className="flex-1">{children}</main>
      <footer className="bg-white border-t border-gray-200 mt-auto">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="flex flex-col md:flex-row justify-between items-start gap-6">
            <div>
              <span className="text-xl font-bold text-gray-900">
                Re<span className="text-blue-600">X</span>ume
              </span>
              <p className="mt-2 text-sm text-gray-500 max-w-xs">
                개발자를 위한 채용공고 및 이력서 플랫폼
              </p>
            </div>
            <div className="flex gap-12 text-sm text-gray-500">
              <div className="space-y-2">
                <p className="font-semibold text-gray-700">서비스</p>
                <p className="hover:text-gray-900 cursor-pointer">채용공고</p>
                <p className="hover:text-gray-900 cursor-pointer">이력서 업로드</p>
                <p className="hover:text-gray-900 cursor-pointer">마이페이지</p>
              </div>
              <div className="space-y-2">
                <p className="font-semibold text-gray-700">지원</p>
                <p className="hover:text-gray-900 cursor-pointer">이용약관</p>
                <p className="hover:text-gray-900 cursor-pointer">개인정보처리방침</p>
              </div>
            </div>
          </div>
          <p className="mt-6 text-xs text-gray-400 border-t border-gray-100 pt-4">
            © 2025 ReXume. All rights reserved.
          </p>
        </div>
      </footer>
    </div>
  );
}

export default GlobalLayout;
