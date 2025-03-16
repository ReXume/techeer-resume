import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from "@radix-ui/react-select";

interface PositionSelectorProps {
  positions: string[];
  selectedPosition: string;
  onSelect: (position: string) => void;
}

function PositionSelector(props: PositionSelectorProps) {
  const { positions, selectedPosition, onSelect } = props;

  return (
    <div>
      <h4 className="text-md font-medium text-gray-700 mb-2 flex items-center">
        <span className="text-blue-500 mr-1">#</span> 포지션
      </h4>
      <Select value={selectedPosition} onValueChange={onSelect}>
        <SelectTrigger className="w-full bg-white border-gray-300 text-gray-700 focus:ring-blue-500 focus:border-blue-500">
          <SelectValue placeholder="포지션을 선택하세요">
            {selectedPosition !== "" ? selectedPosition : ""}
          </SelectValue>
        </SelectTrigger>
        <SelectContent>
          {positions.map(function (position) {
            return (
              <SelectItem key={position} value={position}>
                {position}
              </SelectItem>
            );
          })}
        </SelectContent>
      </Select>
    </div>
  );
}

export default PositionSelector;
