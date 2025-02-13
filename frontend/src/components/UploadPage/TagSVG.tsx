import { ReactNode } from "react";

type TagSVGProps = {
  text: ReactNode;
  isSelected: boolean;
  onClick: () => void;
};

const TagSVG = ({ text, isSelected, onClick }: TagSVGProps) => (
  <div
    onClick={onClick}
    className={`flex justify-center items-center cursor-pointer rounded-lg transition-all ${
      isSelected ? "border-2 border-[#0060FF]" : "border border-transparent"
    }`}
    style={{
      boxSizing: "border-box",
      maxWidth: "7rem",
      minWidth: "5rem",
      width: "100%",
      height: "2rem",
    }}
  >
    <div
      style={{
        width: "100%",
        height: "100%",
        backgroundColor: "#F3F3F3",
        borderRadius: "5px",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      <span
        style={{
          color: "#040404",
          fontFamily: "pretendard",
          fontSize: "14px", // 반응형에서 글자 크기 조정
          fontWeight: "400",
          textAlign: "center",
          whiteSpace: "nowrap", // 글자가 줄 바뀌지 않도록 설정
        }}
      >
        {text}
      </span>
    </div>
  </div>
);

export default TagSVG;
