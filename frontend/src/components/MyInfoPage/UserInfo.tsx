import { User } from "lucide-react";
import { useState } from "react";

function UserInfo() {
  const [role] = useState<"테커" | "외부" | "기업">("테커");
  const [userName] = useState("김테커");

  return (
    <div>
      <div className="flex gap-8">
        <div className="w-72 bg-white rounded-lg shadow-sm p-6 h-fit">
          <div className="flex flex-col items-center">
            <div className="w-24 h-24 bg-gray-200 rounded-full mb-4 flex items-center justify-center">
              <User className="w-12 h-12 text-gray-400" />
            </div>
            <h2 className="text-xl font-bold mb-1">{userName}</h2>
            <p className="text-gray-500 mb-4">{role}</p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default UserInfo;
