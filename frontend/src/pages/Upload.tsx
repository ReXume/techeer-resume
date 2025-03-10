import { useState, useRef } from "react";
import Slider from "@mui/material/Slider";
import { postResume } from "../api/resumeApi";
import TagSVG from "../components/UploadPage/TagSVG.tsx";
import Navbar from "../components/common/Navbar";
import { useNavigate } from "react-router-dom";
import { FileUp as FileUpload } from "lucide-react";

function Upload() {
  const [resume_file, setResumeFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [career, setCareer] = useState<number>(0);
  const [position, setPosition] = useState<string>("");
  const [applyingCompany, setApplyingCompany] = useState<string[]>([]);
  const [techStack, setTechStack] = useState<string[]>([]);

  const fileInputRef = useRef<HTMLInputElement>(null);
  const navigate = useNavigate();

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      const file = event.target.files[0];
      setResumeFile(file);

      // Create preview URL for PDF
      const url = URL.createObjectURL(file);
      setPreviewUrl(url);

      console.log("선택된 파일:", file.name);
    }
  };

  const handleCancel = () => {
    setResumeFile(null);
    setPreviewUrl(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };

  const handleUpload = async () => {
    if (!position) {
      alert("포지션을 선택해주세요.");
      return;
    }

    if (!resume_file) {
      alert("이력서 파일을 선택해주세요.");
      return;
    }

    const resume = {
      position: position,
      career: career,
      company_names: applyingCompany,
      tech_stack_names: techStack,
    };

    try {
      const response = await postResume(resume_file, resume);
      console.log("업로드성공:", response);
      alert("등록이 완료되었습니다");
      navigate("/");
    } catch (error) {
      console.error("업로드 에러:", error);
      alert("업로드 중 오류가 발생했습니다. 다시 시도해주세요.");
    }
  };

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

  const handlePositionClick = (positionTag: string) => {
    setPosition((prevPosition) =>
      prevPosition.toUpperCase() === positionTag.toUpperCase()
        ? ""
        : positionTag.toUpperCase()
    );
  };

  const handleCompanyClick = (company: string) => {
    setApplyingCompany((prevCompanies) =>
      prevCompanies.includes(company)
        ? prevCompanies.filter((c) => c !== company)
        : [...prevCompanies, company]
    );
  };

  const [positionTags] = useState<string[]>(positions);
  const [stackTags, setStackTags] = useState<string[]>(stacks);
  const [companyTags] = useState<string[]>(companies);

  const handleStackClick = (stack: string) => {
    const upperStack = stack.toUpperCase();
    setTechStack((prevStacks) =>
      prevStacks.includes(upperStack)
        ? prevStacks.filter((s) => s !== upperStack)
        : [...prevStacks, upperStack]
    );
  };

  const handleAddStack = (newTag: string) =>
    setStackTags([...stackTags, newTag]);

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-7xl mx-auto px-4 py-8">
        <div className="flex flex-col md:flex-row gap-8">
          {/* Left Column - Upload and Preview */}
          <div className="flex-1">
            <div className="flex flex-col h-full">
              {/* Upload Box */}
              <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
                <div
                  className={`flex flex-col items-center justify-center border-2 border-dashed rounded-lg p-8 cursor-pointer transition-all duration-200 ${
                    resume_file
                      ? "border-blue-500 bg-blue-50"
                      : "border-gray-300 hover:border-blue-400 hover:bg-blue-50"
                  }`}
                  onClick={() => fileInputRef.current?.click()}
                >
                  <div className="relative">
                    <FileUpload
                      className={`w-16 h-16 mb-4 ${resume_file ? "text-blue-500" : "text-gray-400"}`}
                    />
                    {resume_file && (
                      <div className="absolute -top-2 -right-2 w-6 h-6 bg-blue-500 rounded-full flex items-center justify-center">
                        <svg
                          className="w-4 h-4 text-white"
                          fill="none"
                          stroke="currentColor"
                          viewBox="0 0 24 24"
                        >
                          <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth="2"
                            d="M5 13l4 4L19 7"
                          />
                        </svg>
                      </div>
                    )}
                  </div>
                  <input
                    type="file"
                    ref={fileInputRef}
                    onChange={handleFileChange}
                    accept=".pdf"
                    className="hidden"
                  />
                  <h3
                    className={`text-lg font-medium mb-2 ${resume_file ? "text-blue-600" : "text-gray-700"}`}
                  >
                    {resume_file
                      ? "파일이 선택되었습니다"
                      : "PDF 파일을 업로드하세요"}
                  </h3>
                  <p className="text-sm text-gray-500 mb-1">
                    {resume_file
                      ? resume_file.name
                      : "클릭하거나 파일을 드래그하세요"}
                  </p>
                  <p className="text-xs text-gray-400">최대 10MB</p>
                </div>

                {resume_file && (
                  <div className="flex justify-end mt-4">
                    <button
                      onClick={handleCancel}
                      className="text-sm text-gray-600 hover:text-red-500 flex items-center gap-1 transition-colors"
                    >
                      <svg
                        className="w-4 h-4"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth="2"
                          d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                        />
                      </svg>
                      파일 삭제
                    </button>
                  </div>
                )}
              </div>

              {/* PDF Preview */}
              {previewUrl && (
                <div className="bg-white rounded-lg shadow-sm p-6 flex-1">
                  <h3 className="text-lg font-medium mb-4 text-blue-600">
                    미리보기
                  </h3>
                  <div className="h-[calc(100vh-24rem)] border-2 border-blue-100 rounded-lg overflow-hidden">
                    <iframe
                      src={previewUrl}
                      className="w-full h-full"
                      title="PDF Preview"
                    />
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Right Column - Tags and Options */}
          <div className="flex-1">
            <div className="bg-white rounded-lg shadow-sm p-6">
              {/* Position */}
              <section className="mb-8">
                <h3 className="text-lg font-medium mb-4"># 포지션</h3>
                <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                  {positionTags.map((positionTag) => (
                    <TagSVG
                      key={positionTag}
                      text={positionTag}
                      isSelected={position === positionTag.toUpperCase()}
                      onClick={() => handlePositionClick(positionTag)}
                    />
                  ))}
                </div>
              </section>

              {/* Stack */}
              <section className="mb-8">
                <h3 className="text-lg font-medium mb-4"># 스택</h3>
                <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                  {stackTags.map((stack) => (
                    <TagSVG
                      key={stack}
                      text={stack}
                      isSelected={techStack.includes(stack.toUpperCase())}
                      onClick={() => handleStackClick(stack)}
                    />
                  ))}
                  <DirectInputTag
                    existingTags={stackTags}
                    onAdd={handleAddStack}
                  />
                </div>
              </section>

              {/* Career */}
              <section className="mb-8">
                <h3 className="text-lg font-medium mb-4"># 경력</h3>
                <div className="px-4">
                  <ExperienceSlider value={career} onChange={setCareer} />
                </div>
              </section>

              {/* Company */}
              <section className="mb-8">
                <h3 className="text-lg font-medium mb-4"># 회사</h3>
                <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                  {companyTags.map((company) => (
                    <TagSVG
                      key={company}
                      text={company}
                      isSelected={applyingCompany.includes(company)}
                      onClick={() => handleCompanyClick(company)}
                    />
                  ))}
                </div>
              </section>

              {/* Submit Button */}
              <div className="flex justify-end mt-8">
                <button
                  className="px-8 py-3 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition-colors shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 active:translate-y-0 active:shadow-lg"
                  onClick={handleUpload}
                >
                  이력서 등록하기
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Upload;
