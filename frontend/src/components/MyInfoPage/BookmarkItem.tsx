/* eslint-disable @typescript-eslint/no-unused-vars */
import { Bookmark } from "lucide-react";
import { BookmarkType } from "../../dataType";

type BookmarkItemProps = {
  bookmark: BookmarkType;
};

//북마크 된 이력서 추가정보 반영되면 주석 풀어서 사용
function BookmarkItem({ bookmark }: BookmarkItemProps) {
  return (
    <div className="flex items-center justify-between p-4 border rounded-lg hover:bg-gray-50">
      <div className="flex items-center space-x-4">
        <Bookmark className="w-5 h-5 text-gray-500" />
        <div>
          {/* <h4 className="font-medium">{bookmark.title}</h4> */}
          <h4 className="font-medium">2024 테커 상반기 채용 이력서</h4>

          {/* <p className="text-sm text-gray-500">{bookmark.user_name}</p> */}
          <p className="text-sm text-gray-500">정유진</p>
        </div>
      </div>
      <div className="flex items-center space-x-4">
        {/* <span className="text-sm text-gray-500">{bookmark.date}</span> */}
        <span className="text-sm text-gray-500">2025-01-11</span>
      </div>
    </div>
  );
}

export default BookmarkItem;
