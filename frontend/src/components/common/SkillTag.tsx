interface SkillTagProps {
  label: string;
  onRemove?: () => void;
  onClick?: () => void;
  selected?: boolean;
}

function SkillTag({ label, onRemove, onClick, selected }: SkillTagProps) {
  return (
    <span
      onClick={onClick}
      className={`inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-medium transition-colors ${
        selected
          ? "bg-blue-600 text-white"
          : onClick
          ? "bg-gray-100 text-gray-700 cursor-pointer hover:bg-blue-50 hover:text-blue-700"
          : "bg-blue-50 text-blue-700"
      }`}
    >
      {label}
      {onRemove && (
        <button
          type="button"
          onClick={(e) => {
            e.stopPropagation();
            onRemove();
          }}
          className="ml-0.5 text-current opacity-60 hover:opacity-100"
          aria-label={`${label} 제거`}
        >
          &times;
        </button>
      )}
    </span>
  );
}

export default SkillTag;
