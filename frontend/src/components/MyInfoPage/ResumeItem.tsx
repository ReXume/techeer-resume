import { FileText } from "lucide-react";
import { ResumeType } from "../../dataType";

type ResumeItemProps = { resume: ResumeType };

const ResumeItem = ({ resume }: ResumeItemProps) => {
  return (
    <div className="flex items-center justify-between p-4 border rounded-lg hover:bg-gray-50">
      <div className="flex items-center space-x-4">
        <FileText className="w-5 h-5 text-gray-500" />
        <div>
          <h4 className="font-medium">{resume.version}</h4>
        </div>
      </div>
      <div className="flex items-center space-x-4">
        <span className="text-sm text-gray-500">{resume.date}</span>
      </div>
    </div>
  );
};

export default ResumeItem;
