import { Bookmark } from "lucide-react";

interface BookmarkItemProps {
  bookmark: {
    id: number;
    title: string;
    views: number;
    date: string;
  };
}

function BookmarkItem({ bookmark }: BookmarkItemProps) {
  return (
    <div className="flex items-center justify-between p-4 border rounded-lg hover:bg-gray-50">
      <div className="flex items-center space-x-4">
        <Bookmark className="w-5 h-5 text-gray-400" />
        <div>
          <h4 className="font-medium">{bookmark.title}</h4>
          <p className="text-sm text-gray-500">조회수 {bookmark.views}</p>
        </div>
      </div>
      <div className="flex items-center space-x-4">
        <span className="text-sm text-gray-500">{bookmark.date}</span>
      </div>
    </div>
  );
}

export default BookmarkItem;
