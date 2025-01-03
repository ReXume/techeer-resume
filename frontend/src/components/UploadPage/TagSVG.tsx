import { ReactNode } from "react";

type TagSVGProps = {
  text: ReactNode;
  isSelected: boolean;
  onClick: () => void;
};

const TagSVG = ({ text, isSelected, onClick }: TagSVGProps) => (
  <div
    onClick={onClick}
    className={`flex justify-center items-center cursor-pointer rounded-lg ${
      isSelected ? "border-2 border-[#0060FF]" : "border border-transparent"
    }`}
    style={{
      boxSizing: "border-box",
      width: "121px",
      height: "33px",
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
          fontSize: "16px",
          fontWeight: "400",
          textAlign: "center",
        }}
      >
        {text}
      </span>
    </div>
  </div>
);

export default TagSVG;
