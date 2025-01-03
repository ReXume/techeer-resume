import { useState } from "react";
import Slider from "@mui/material/Slider";
import { postResume } from "../api/resumeApi";
import FileUploadBox from "../components/UploadPage/UploadBox.tsx";
import TagSVG from "../components/UploadPage/TagSVG.tsx";
import Navbar from "../components/common/Navbar";

function Upload() {
  const [resume, setResume] = useState<File | null>(null);
  const [career, setCareer] = useState<number>(0);
  const [position, setPosition] = useState<string>("");
  const [applyingCompany, setApplyingCompany] = useState<string[]>([]);
  const [techStack, setTechStack] = useState<string[]>([]);

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      setResume(event.target.files[0]);
      console.log("선택된 파일:", event.target.files[0].name); // 파일명 출력
    }
  };

  const handleCancel = () => {
    setResume(null);
  };

  const handleUpload = async () => {
    if (resume) {
      const createResumeReq = {
        username: "testuser",
        position: position.toUpperCase(),
        career: career,
        company_names: applyingCompany,
        tech_stack_names: techStack.map((stack) => stack.toUpperCase()),
      };

      try {
        const response = await postResume(resume, createResumeReq);
        console.log("업로드성공:", response);
      } catch (error) {
        console.error("업로드 에러:", error);
      }
    } else {
      alert("이력서 파일을 선택하세요");
    }
  };

  // 포지션, 학력, 회사, 스택 데이터
  const positions = [
    "Frontend",
    "Backend",
    "FullStack",
    "DevOps",
    "Designer",
    "AI",
    "Android",
    "IOS",
    "Data",
  ];
  const educations = ["전공자", "비전공자"];
  const companies = [
    "IT대기업",
    "스타트업",
    "서비스",
    "SI",
    "금융권",
    "제조업",
    "핀테크",
    "유니콘",
  ];
  const stacks = [
    "JAVA",
    "Go",
    "JavaScript",
    "Spring Boot",
    "Flask",
    "MySQL",
    "AWS",
    "React",
    "PostgreSQL",
    "TypeScript",
    "Next.js",
  ];

  const DirectInputTag = ({
    existingTags,
    onAdd,
  }: {
    existingTags: string[];
    onAdd: (newTag: string) => void;
  }) => {
    const [inputValue, setInputValue] = useState<string>("");

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      setInputValue(e.target.value);
    };

    const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
      if (
        e.key === "Enter" &&
        inputValue.trim() !== "" &&
        !existingTags.includes(inputValue.trim())
      ) {
        onAdd(inputValue.trim());
        setInputValue("");
      }
    };

    return (
      <div className="flex cursor-pointer">
        <input
          type="text"
          value={inputValue}
          onChange={handleInputChange}
          onKeyPress={handleKeyPress}
          className="w-[121px] h-[33px] bg-[#F3F3F3] flex items-center justify-center rounded-[0.3rem] text-black text-center outline-none"
          placeholder="직접입력"
        />
      </div>
    );
  };

  const ExperienceSlider = ({
    value,
    onChange,
  }: {
    value: number;
    onChange: (newValue: number) => void;
  }) => {
    const marks = [
      { value: 0, label: "신입" },
      { value: 1, label: "1년" },
      { value: 2, label: "2년" },
      { value: 3, label: "3년" },
      { value: 4, label: "4년" },
      { value: 5, label: "5년" },
      { value: 6, label: "6년" },
      { value: 7, label: "7년" },
      { value: 8, label: "8년" },
      { value: 9, label: "9년" },
      { value: 10, label: "10년" },
    ];

    return (
      <Slider
        value={value}
        onChange={(_event, newValue) => onChange(newValue as number)} // Update with correct type
        aria-labelledby="experience-slider"
        step={1}
        marks={marks}
        min={0}
        max={10}
        valueLabelDisplay="auto"
        sx={{
          "& .MuiSlider-thumb": { color: "#007bff" },
          "& .MuiSlider-track": { color: "#007bff" },
          "& .MuiSlider-rail": { color: "#dddddd" },
        }}
      />
    );
  };

  const [selectedEducation, setSelectedEducation] = useState<string | null>(
    null
  );

  const handlePositionClick = (positionTag: string) => {
    setPosition((prevPosition) =>
      prevPosition === positionTag ? "" : positionTag
    );
  };
  const handleEducationClick = (education: string) => {
    setSelectedEducation((prevEducation) =>
      prevEducation === education ? "" : education
    );
  };
  const handleCompanyClick = (applyingCompany: string) => {
    setApplyingCompany((prevCompanies) =>
      prevCompanies.includes(applyingCompany)
        ? prevCompanies.filter((company) => company !== applyingCompany)
        : [...prevCompanies, applyingCompany]
    );
  };

  const [positionTags] = useState<string[]>(positions);
  const [stackTags, setStackTags] = useState<string[]>(stacks);
  const [educationTags] = useState<string[]>(educations);
  const [companyTags] = useState<string[]>(companies);

  const handleStackClick = (techStack: string) => {
    setTechStack((prevStacks) =>
      prevStacks.includes(techStack)
        ? prevStacks.filter((stack) => stack !== techStack)
        : [...prevStacks, techStack]
    );
  };

  const handleAddStack = (newTag: string) =>
    setStackTags([...stackTags, newTag]);

  return (
    <div>
      <div className="pt-5"></div>
      <Navbar />
      <div className="flex flex-col lg:flex-row justify-center items-start p-4 space-y-4 lg:space-y-0 lg:space-x-8">
        {/* 업로드 박스 */}
        <div className="w-full lg:w-2/3">
          <FileUploadBox
            resume={resume}
            handleFileChange={handleFileChange}
            handleCancel={handleCancel}
          />
        </div>

        {/* 태그 선택 부분: lg 이상에서만 보이도록 설정 */}
        <div className="w-full lg:w-1/3 flex flex-col space-y-6 text-black font-pretendard text-[0.9rem] md:text-[1rem] font-normal">
          <div className="ml-[0.5rem] md:ml-[1rem]"># 포지션</div>
          <div className="grid grid-cols-2 md:grid-cols-3 gap-3 mb-4">
            {positionTags.map((positionTag) => (
              <TagSVG
                key={positionTag}
                text={positionTag}
                isSelected={position == positionTag}
                onClick={() => handlePositionClick(positionTag)}
              />
            ))}
          </div>

          {/* 스택 */}
          <div className="flex flex-col justify-start mt-[1rem]">
            <div className="ml-[0.5rem] md:ml-[1rem]"># 스택</div>
            <div className="grid grid-cols-2 md:grid-cols-3 gap-2 mb-4">
              {stackTags.map((stackTag) => (
                <TagSVG
                  key={stackTag}
                  text={stackTag}
                  isSelected={techStack.includes(stackTag)}
                  onClick={() => handleStackClick(stackTag)}
                />
              ))}
              <DirectInputTag existingTags={stackTags} onAdd={handleAddStack} />
            </div>
          </div>

          {/* 경력 슬라이더 */}
          <div className="flex flex-col justify-start mt-[1rem]">
            <div className="ml-[0.5rem] md:ml-[1rem]"># 경력</div>
            <div className="ml-[0.5rem] md:ml-[1rem]">
              <ExperienceSlider value={career} onChange={setCareer} />
            </div>
          </div>

          {/* 학력 */}
          <div className="flex flex-col justify-start mt-[1rem]">
            <div className="ml-[0.5rem] md:ml-[1rem]"># 학력</div>
            <div className="grid grid-cols-2 md:grid-cols-3 gap-2 mb-4">
              {educationTags.map((education) => (
                <TagSVG
                  key={education}
                  text={education}
                  isSelected={selectedEducation === education}
                  onClick={() => handleEducationClick(education)}
                />
              ))}
            </div>
          </div>

          {/* 회사 */}
          <div className="flex flex-col justify-start mt-[1rem]">
            <div className="ml-[0.5rem] md:ml-[1rem]"># 회사</div>
            <div className="grid grid-cols-2 md:grid-cols-3 gap-2 mb-4">
              {companyTags.map((company) => (
                <TagSVG
                  key={company}
                  text={company}
                  isSelected={applyingCompany.includes(company)}
                  onClick={() => handleCompanyClick(company)}
                />
              ))}
            </div>

            {/* 최종 업로드 버튼 */}
            <div className="flex justify-end mt-8">
              <button
                className="w-[8rem] md:w-[10rem] h-[3rem] text-white font-pretendard text-[1rem] font-semibold bg-[#0060FF] rounded"
                onClick={handleUpload}
              >
                Submit Resume
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
export default Upload;
