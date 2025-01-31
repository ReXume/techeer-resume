import { ReactNode } from "react";
import { useNavigate } from "react-router-dom";

function BannerCard({
  title,
  comment,
  btncomment,
  imgurl,
  pageurl,
}: {
  title: ReactNode;
  comment: string;
  btncomment: string;
  imgurl: string;
  pageurl: string;
}) {
  const navigate = useNavigate();

  const movePage = () => {
    navigate(`/${pageurl}`);
  };

  return (
    <div>
      {/* 카드 영역 */}
      <div className="flex flex-row justify-between items-center w-60 sm:w-68 md:w-92 lg:w-125 xl:w-140 h-75 rounded-3xl bg-[#4177FF] p-8">
        {/* 텍스트 영역 */}
        <div className="flex flex-col space-y-4">
          <p className="flex justify-start text-left font-bold leading-loose text-xl text-white md:text-2xl">
            {title}
          </p>
          {/* sm 사이즈 이상에서만 표시 */}
          <p className="text-white text-sm hidden sm:block">{comment}</p>
          <button
            className="bg-white text-[#4177FF] px-6 py-3 rounded-full font-bold text-sm hover:scale-110 transition-transform duration-200 md:text-lg"
            onClick={movePage}
          >
            {btncomment}
          </button>
        </div>
        {/* 이미지 영역 - lg 사이즈 이상에서만 표시 */}
        <div className="flex hidden lg:block">
          <img
            src={imgurl}
            alt="캐릭터 이미지"
            className="w-20 md:w-40 lg:w-72 h-60"
          />
        </div>
      </div>
    </div>
  );
}

export default BannerCard;
