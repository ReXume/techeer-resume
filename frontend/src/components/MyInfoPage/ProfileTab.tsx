import { useState } from "react";
import { Check, MapPin, Laptop, Briefcase } from "lucide-react";
import SkillTag from "../common/SkillTag";
import { updateProfile, UserProfileData } from "../../api/jobApi";

const SKILL_OPTIONS = [
  "JavaScript", "TypeScript", "React", "Vue", "Angular", "Node.js",
  "Python", "Java", "Kotlin", "Spring", "Django", "FastAPI",
  "Go", "Rust", "C++", "C#", "Swift", "Flutter", "Docker", "Kubernetes",
  "AWS", "GCP", "Azure", "MySQL", "PostgreSQL", "MongoDB", "Redis",
];

const POSITION_OPTIONS = [
  "프론트엔드 개발자", "백엔드 개발자", "풀스택 개발자",
  "모바일 개발자", "데이터 엔지니어", "ML 엔지니어",
  "DevOps 엔지니어", "QA 엔지니어", "보안 엔지니어",
];

const EXPERIENCE_OPTIONS = ["신입", "1~3년", "3~5년", "5~7년", "7~10년", "10년 이상"];

const LOCATION_OPTIONS = [
  "서울", "경기", "인천", "부산", "대구", "광주", "대전", "울산",
  "세종", "강원", "충북", "충남", "전북", "전남", "경북", "경남", "제주",
];

interface ProfileTabProps {
  initialData?: UserProfileData;
}

function ProfileTab({ initialData }: ProfileTabProps) {
  const [skills, setSkills] = useState<string[]>(initialData?.skills ?? []);
  const [desiredPosition, setDesiredPosition] = useState(initialData?.desiredPosition ?? "");
  const [experienceLevel, setExperienceLevel] = useState(initialData?.experienceLevel ?? "");
  const [preferredLocations, setPreferredLocations] = useState<string[]>(
    initialData?.preferredLocations ?? []
  );
  const [remotePreferred, setRemotePreferred] = useState(initialData?.remotePreferred ?? false);
  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const toggleSkill = (skill: string) => {
    setSkills((prev) =>
      prev.includes(skill) ? prev.filter((s) => s !== skill) : [...prev, skill]
    );
  };

  const toggleLocation = (loc: string) => {
    setPreferredLocations((prev) =>
      prev.includes(loc) ? prev.filter((l) => l !== loc) : [...prev, loc]
    );
  };

  // Completeness: 5 fields, count filled ones
  const filledCount = [
    skills.length > 0,
    !!desiredPosition,
    !!experienceLevel,
    preferredLocations.length > 0,
    true, // remote toggle always "filled"
  ].filter(Boolean).length;

  const completeness = Math.round((filledCount / 5) * 100);

  const handleSave = async () => {
    setSaving(true);
    setError(null);
    try {
      await updateProfile({
        skills,
        desiredPosition,
        experienceLevel,
        preferredLocations,
        remotePreferred,
      });
      setSaved(true);
      setTimeout(() => setSaved(false), 2500);
    } catch {
      setError("저장에 실패했습니다. 다시 시도해 주세요.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-sm p-6 space-y-8">
      {/* Completeness bar */}
      <div>
        <div className="flex items-center justify-between mb-2">
          <span className="text-sm font-medium text-gray-700">프로필 완성도</span>
          <span
            className={`text-sm font-bold ${
              completeness >= 80
                ? "text-green-600"
                : completeness >= 60
                ? "text-yellow-600"
                : "text-gray-500"
            }`}
          >
            {completeness}%
          </span>
        </div>
        <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
          <div
            className={`h-full rounded-full transition-all duration-500 ${
              completeness >= 80
                ? "bg-green-500"
                : completeness >= 60
                ? "bg-yellow-400"
                : "bg-blue-400"
            }`}
            style={{ width: `${completeness}%` }}
          />
        </div>
      </div>

      {/* Skills */}
      <div>
        <div className="flex items-center gap-2 mb-3">
          <Briefcase className="w-4 h-4 text-gray-500" />
          <label className="text-sm font-semibold text-gray-800">기술 스택</label>
        </div>
        {skills.length > 0 && (
          <div className="flex flex-wrap gap-1.5 mb-3">
            {skills.map((s) => (
              <SkillTag key={s} label={s} onRemove={() => toggleSkill(s)} />
            ))}
          </div>
        )}
        <div className="flex flex-wrap gap-1.5 p-3 bg-gray-50 rounded-lg border border-gray-100">
          {SKILL_OPTIONS.map((s) => (
            <SkillTag
              key={s}
              label={s}
              selected={skills.includes(s)}
              onClick={() => toggleSkill(s)}
            />
          ))}
        </div>
      </div>

      {/* Position & Experience */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
        <div>
          <label className="block text-sm font-semibold text-gray-800 mb-2">
            희망 포지션
          </label>
          <select
            value={desiredPosition}
            onChange={(e) => setDesiredPosition(e.target.value)}
            className="w-full px-3 py-2.5 text-sm border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
          >
            <option value="">선택하세요</option>
            {POSITION_OPTIONS.map((p) => (
              <option key={p} value={p}>
                {p}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label className="block text-sm font-semibold text-gray-800 mb-2">
            경력 수준
          </label>
          <select
            value={experienceLevel}
            onChange={(e) => setExperienceLevel(e.target.value)}
            className="w-full px-3 py-2.5 text-sm border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
          >
            <option value="">선택하세요</option>
            {EXPERIENCE_OPTIONS.map((e) => (
              <option key={e} value={e}>
                {e}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Preferred Locations */}
      <div>
        <div className="flex items-center gap-2 mb-3">
          <MapPin className="w-4 h-4 text-gray-500" />
          <label className="text-sm font-semibold text-gray-800">선호 지역</label>
        </div>
        <div className="flex flex-wrap gap-2 p-3 bg-gray-50 rounded-lg border border-gray-100">
          {LOCATION_OPTIONS.map((loc) => (
            <button
              key={loc}
              type="button"
              onClick={() => toggleLocation(loc)}
              className={`px-3 py-1.5 text-xs rounded-full border transition-colors ${
                preferredLocations.includes(loc)
                  ? "bg-blue-600 text-white border-blue-600"
                  : "bg-white text-gray-600 border-gray-200 hover:border-blue-300 hover:text-blue-600"
              }`}
            >
              {loc}
            </button>
          ))}
        </div>
      </div>

      {/* Remote toggle */}
      <div className="flex items-center justify-between p-4 bg-gray-50 rounded-lg border border-gray-100">
        <div className="flex items-center gap-3">
          <Laptop className="w-4 h-4 text-gray-500" />
          <div>
            <p className="text-sm font-semibold text-gray-800">원격 근무</p>
            <p className="text-xs text-gray-400">원격 근무 가능한 공고를 선호합니다</p>
          </div>
        </div>
        <button
          type="button"
          onClick={() => setRemotePreferred((v) => !v)}
          className={`relative w-11 h-6 rounded-full transition-colors focus:outline-none ${
            remotePreferred ? "bg-blue-600" : "bg-gray-300"
          }`}
          aria-checked={remotePreferred}
          role="switch"
        >
          <span
            className={`absolute top-0.5 left-0.5 w-5 h-5 bg-white rounded-full shadow transition-transform ${
              remotePreferred ? "translate-x-5" : "translate-x-0"
            }`}
          />
        </button>
      </div>

      {error && (
        <p className="text-sm text-red-500">{error}</p>
      )}

      <div className="flex justify-end">
        <button
          type="button"
          onClick={handleSave}
          disabled={saving}
          className="flex items-center gap-2 px-6 py-2.5 bg-blue-600 hover:bg-blue-700 disabled:opacity-60 text-white text-sm font-medium rounded-lg transition-colors"
        >
          {saved ? (
            <>
              <Check className="w-4 h-4" /> 저장됨
            </>
          ) : saving ? (
            "저장 중..."
          ) : (
            "저장하기"
          )}
        </button>
      </div>
    </div>
  );
}

export default ProfileTab;
