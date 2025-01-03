// ../components/uploadpage/UploadBox.tsx
import React from "react";

interface UploadBoxProps {
  resume: File | null;
  handleFileChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  handleCancel: () => void;
}

function FileUploadBox({
  resume,
  handleFileChange,
  handleCancel,
}: UploadBoxProps) {
  return (
    <div className="w-full md:w-[50rem] h-auto md:h-[42rem] flex-shrink-0 rounded-[0.3125rem] border border-[#CEDAF9] bg-[#F8FAFF]">
      {/* SVG 아이콘: lg 이상에서만 보임 */}
      <div className="flex-shrink-0 ml-[2rem] md:ml-[5rem] mt-[4rem] relative hidden lg:block cursor-pointer">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          className="w-[5rem] h-[5rem] md:w-[7rem] md:h-[7rem] fill-[#EFEFEF]"
          viewBox="0 0 114 114"
        >
          <circle cx="57" cy="57" r="57" />
          <path
            d="M60.7671 22.5488V40.7682C60.7671 45.3471 56.1425 49.059 50.4378 49.059H11.0969C5.39219 49.059 0.767578 45.3471 0.767578 40.7682V13.1832C0.767578 8.60427 5.39219 4.89233 11.0969 4.89233H24.9332C28.1553 4.89233 30.7674 6.98889 30.7674 9.57513C30.8196 12.1313 33.4164 14.1815 36.6015 14.1812H50.4378C53.1939 14.1811 55.8359 15.0651 57.7758 16.6366C59.7157 18.208 60.7927 20.3366 60.7671 22.5488Z"
            fill="#0060FF"
            transform="translate(25, 28)"
          />
        </svg>
      </div>

      {/* 텍스트: sm 이상에서만 보임 */}
      <div className="ml-[2rem] md:ml-[5rem] mt-[1rem] hidden sm:block">
        <div className="w-[12rem] md:w-[15rem] h-[2rem] text-black font-pretendard text-[1.2rem] md:text-[1.5rem] font-bold">
          첨부파일 업로드
        </div>
        <div className="text-black font-pretendard text-[0.9rem] md:text-[1.1rem]">
          여기에 파일을 끌어다 놓으세요
        </div>
      </div>

      {/* FileUpload 디자인 */}
      <label
        htmlFor="uploadFile"
        className="bg-white text-gray-500 font-semibold text-base rounded w-[40rem] h-[18rem] flex flex-col items-center justify-center cursor-pointer border-2 border-gray-300 border-dashed mx-auto font-sans mt-[2rem]"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          className="w-[7rem] mb-2 fill-gray-500"
          viewBox="0 0 32 32"
        >
          <path
            d="M23.75 11.044a7.99 7.99 0 0 0-15.5-.009A8 8 0 0 0 9 27h3a1 1 0 0 0 0-2H9a6 6 0 0 1-.035-12 1.038 1.038 0 0 0 1.1-.854 5.991 5.991 0 0 1 11.862 0A1.08 1.08 0 0 0 23 13a6 6 0 0 1 0 12h-3a1 1 0 0 0 0 2h3a8 8 0 0 0 .75-15.956z"
            fill="#C5C5C5"
          />
          <path
            d="M20.293 19.707a1 1 0 0 0 1.414-1.414l-5-5a1 1 0 0 0-1.414 0l-5 5a1 1 0 0 0 1.414 1.414L15 16.414V29a1 1 0 0 0 2 0V16.414z"
            fill="#C5C5C5"
          />
        </svg>
        Upload file
        <input
          type="file"
          id="uploadFile"
          className="hidden"
          onChange={handleFileChange}
        />
        <p className="text-xs font-medium text-gray-400 mt-2">
          PDF is allowed.
        </p>
      </label>

      {/* 파일명 및 취소 버튼 */}
      <div className="w-[90%] md:w-[40rem] flex items-center mt-[1rem] ml-[2rem] md:ml-[5rem]">
        {resume ? (
          <div className="text-gray-600 font-medium overflow-hidden text-ellipsis whitespace-nowrap flex-1">
            선택된 파일: {resume.name}
          </div>
        ) : (
          <div className="flex-1" />
        )}
        <button
          className="w-[6rem] md:w-[7.5rem] h-[2.5rem] text-[#C5C5C5] font-pretendard text-[0.8rem] md:text-[1rem] bg-transparent border border-[#C5C5C5] rounded"
          onClick={handleCancel}
        >
          Cancel
        </button>
      </div>
    </div>
  );
}

export default FileUploadBox;
