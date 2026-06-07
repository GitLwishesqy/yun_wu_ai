import { Keyboard, Mic } from 'lucide-react';

export type InputMode = 'text' | 'voice';

interface Props {
  mode: InputMode;
  onChange: (mode: InputMode) => void;
}

export default function InputModeToggle({ mode, onChange }: Props) {
  return (
    <div className="flex bg-gray-100 rounded-full p-1">
      <button
        onClick={() => onChange('text')}
        className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full text-xs font-medium transition-all
          ${mode === 'text' ? 'bg-white text-gray-800 shadow-sm' : 'text-gray-500'}`}>
        <Keyboard size={14} /> 打字
      </button>
      <button
        onClick={() => onChange('voice')}
        className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full text-xs font-medium transition-all
          ${mode === 'voice' ? 'bg-white text-primary-600 shadow-sm' : 'text-gray-500'}`}>
        <Mic size={14} /> 语音
      </button>
    </div>
  );
}
