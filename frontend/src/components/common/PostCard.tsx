import { MouseEventHandler } from "react";
import avatar from "../../assets/avatar.webp";
import baggage from "../../assets/baggage3.svg";
import pngegg from "../../assets/pngegg2.svg";

function PostCard({
  name,
  role,
  experience,
  education,
  skills,
  onClick,
}: {
  name: string;
  role: string;
  experience: number | string;
  education: string;
  skills: string[];
  onClick?: MouseEventHandler<HTMLDivElement>;
}) {
  return (
    <div
      onClick={onClick}
      className="w-80 h-48 relative bg-white rounded-[7.01px] border border-[#aabdeb] p-6 "
    >
      {/* Name & Role Section */}
      <div className="flex items-center space-x-4">
        {/* 프로필 사진 */}
        <img
          className="w-12 h-12 bg-[#608dff] rounded-full"
          src={avatar}
          alt="avatar"
        />
        {/* 이름 & 포지션 */}
        <div>
          <div className="text-black text-[22.42px] font-extrabold">{name}</div>
          <div className="text-gray-600 text-sm font-medium">{role}</div>
        </div>
      </div>

      <div className="flex flex-row mt-6 px-3">
        {/* 경력 */}
        <div className="flex items-center space-x-2">
          <img className="w-6 h-6" src={baggage} alt="baggage" />
          <div className="text-gray-600 text-sm font-medium">
            {experience === 0 ? "신입" : `${experience}년`}
          </div>
        </div>

        {/* 학력 */}
        <div className="flex items-center space-x-2 ml-8">
          <img className="w-6 h-6" src={pngegg} alt="pngegg" />
          <div className="text-gray-600 text-sm font-medium">{education}</div>
        </div>
      </div>

      {/* 기술 스택 */}
      <div className="flex space-x-2 mt-6">
        {skills && skills.length > 0 ? (
          skills.slice(0, 3).map((skill, index) => (
            <div
              key={index}
              className="flex items-center justify-center px-4 py-1 bg-[#d6e0f5] rounded-[14px] text-center text-[#2446b3] text-xs font-semibold"
            >
              {skill}
            </div>
          ))
        ) : (
          <div className="flex items-center justify-center px-4 py-1 bg-[#d6e0f5] rounded-[14px] text-center text-[#2446b3] text-xs font-semibold">
            <p>NoSkill</p>
          </div>
        )}
      </div>
    </div>
  );
}

export default PostCard;
