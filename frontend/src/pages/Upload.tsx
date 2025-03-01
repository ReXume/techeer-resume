import { useState } from "react";
import Slider from "@mui/material/Slider";
import { postResume } from "../api/resumeApi";
import FileUploadBox from "../components/UploadPage/UploadBox.tsx";
import TagSVG from "../components/UploadPage/TagSVG.tsx";
import Navbar from "../components/common/Navbar";

function Upload() {
  const [resume_file, setResumeFile] = useState<File | null>(null);
  const [career, setCareer] = useState<number>(0);
  const [position, setPosition] = useState<string>("");
  const [applyingCompany, setApplyingCompany] = useState<string[]>([]);
  const [techStack, setTechStack] = useState<string[]>([]);

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      setResumeFile(event.target.files[0]);
      console.log("선택된 파일:", event.target.files[0].name); // 파일명 출력
    }
  };

  const handleCancel = () => {
    setResumeFile(null);
  };

  const handleUpload = async () => {
    console.log("포지션", position);
    if (resume_file) {
      const resume = {
        position: position,
        career: career,
        company_names: applyingCompany,
        tech_stack_names: techStack,
      };

      try {
        const response = await postResume(resume_file, resume);
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
  // const educations = ["전공자", "비전공자"];
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
          className="w-full max-w-[7rem] min-w-[5rem] h-[33px] bg-[#F3F3F3] flex items-center justify-center rounded-[0.3rem] text-black text-center outline-none"
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
    const marks = [...Array(11)].map((_, i) => ({ value: i, label: `${i}년` }));

    return (
      <Slider
        value={value}
        onChange={(_event, newValue) => {
          onChange(newValue as number);
          console.log("선택된 경력:", newValue);
        }}
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

  // const [selectedEducation, setSelectedEducation] = useState<string | null>(
  //   null
  // );

  const handlePositionClick = (positionTag: string) => {
    setPosition((prevPosition) =>
      prevPosition.toUpperCase() === positionTag.toUpperCase()
        ? ""
        : positionTag.toUpperCase()
    );
    console.log("선택된 포지션:", positionTag.toUpperCase());
  };

  // const handleEducationClick = (education: string) => {
  //   setSelectedEducation((prevEducation) =>
  //     prevEducation === education ? "" : education
  //   );
  //   console.log("선택된 학력:", education);
  // };

  const handleCompanyClick = (company: string) => {
    setApplyingCompany((prevCompanies) =>
      prevCompanies.includes(company)
        ? prevCompanies.filter((c) => c !== company)
        : [...prevCompanies, company]
    );
    console.log("선택된 회사:", company);
  };

  const [positionTags] = useState<string[]>(positions);
  const [stackTags, setStackTags] = useState<string[]>(stacks);
  // const [educationTags] = useState<string[]>(educations);
  const [companyTags] = useState<string[]>(companies);

  const handleStackClick = (stack: string) => {
    const upperStack = stack.toUpperCase(); // 대문자로 변환

    setTechStack((prevStacks) =>
      prevStacks.includes(upperStack)
        ? prevStacks.filter((s) => s !== upperStack)
        : [...prevStacks, upperStack]
    );

    console.log("선택된 스택:", upperStack);
  };

  const handleAddStack = (newTag: string) =>
    setStackTags([...stackTags, newTag]);

  return (
    <div>
      <div className="pt-5"></div>
      <Navbar />
      <div className="mt-[5rem] flex flex-col md:flex-row justify-between mx-4">
        {/* 업로드 박스 */}
        <div className=" flex-[3] max-w-[70%] min-w-[300px] w-full p-4">
          <FileUploadBox
            resume_file={resume_file}
            handleFileChange={handleFileChange}
            handleCancel={handleCancel}
          />
        </div>

        {/* 태그 선택 부분 */}
        <div className="flex-[2] max-w-[30%] min-w-[300px] w-full flex flex-col space-y-6 text-black font-pretendard text-[0.9rem] md:text-[1rem] font-normal p-4">
          {/* 포지션 */}
          <div className="ml-2 md:ml-4"># 포지션</div>
          <div className="grid grid-cols-2 md:grid-cols-3 gap-4 mb-4">
            {positionTags.map((positionTag) => (
              <TagSVG
                key={positionTag}
                text={positionTag}
                isSelected={position == positionTag.toUpperCase()}
                onClick={() => handlePositionClick(positionTag)}
              />
            ))}
          </div>

          {/* 스택 */}
          <div className="flex flex-col justify-start">
            <div className="ml-2 md:ml-4"># 스택</div>
            <div className="grid grid-cols-2 md:grid-cols-3 gap-4 mb-4">
              {stackTags.map((stack) => (
                <TagSVG
                  key={stack}
                  text={stack}
                  isSelected={techStack.includes(stack.toUpperCase())} // 대문자로 변환 후 비교
                  onClick={() => handleStackClick(stack)}
                />
              ))}
              <DirectInputTag existingTags={stackTags} onAdd={handleAddStack} />
            </div>
          </div>

          {/* 경력 슬라이더 */}
          <div className="flex flex-col justify-start">
            <div className="ml-2 md:ml-4"># 경력</div>
            <div className="ml-2 md:ml-4">
              <ExperienceSlider value={career} onChange={setCareer} />
            </div>
          </div>

          <div className="flex flex-col justify-start">
            {/* <div className="ml-2 md:ml-4"># 학력</div> */}
            {/* <div className="grid grid-cols-2 gap-4 mb-4">
              {educationTags.map((education) => (
                <TagSVG
                  key={education}
                  text={education}
                  isSelected={selectedEducation === education}
                  onClick={() => handleEducationClick(education)}
                />
              ))}
            </div> */}
          </div>

          {/* 회사 */}
          <div className="flex flex-col justify-start">
            <div className="ml-2 md:ml-4"># 회사</div>
            <div className="grid grid-cols-2 md:grid-cols-3 gap-4 mb-4">
              {companyTags.map((company) => (
                <TagSVG
                  key={company}
                  text={company}
                  isSelected={applyingCompany.includes(company)}
                  onClick={() => handleCompanyClick(company)}
                />
              ))}
            </div>
          </div>

          {/* 최종 업로드 버튼 */}
          <div className="flex justify-end">
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
  );
}
export default Upload;
