import { AlertCircle, CheckCircle2, Lightbulb } from 'lucide-react';
import type { CorrectionData } from '../../lib/api';

const severityColors = {
  HIGH: 'border-l-error bg-error/5',
  MEDIUM: 'border-l-warm-500 bg-warm-500/5',
  LOW: 'border-l-sky-500 bg-sky-500/5',
} as const;

export default function CorrectionCard({ correction: c }: { correction: CorrectionData }) {
  const color = severityColors[c.severity as keyof typeof severityColors] || severityColors.MEDIUM;

  return (
    <div className={`mt-2 px-3 py-2.5 border-l-2 rounded-r-lg text-xs animate-bubble-in ${color}`}>
      {/* 错误类型标签 */}
      <div className="flex items-center gap-1.5 mb-1.5">
        <AlertCircle size={12} className="text-warm-500" />
        <span className="font-medium text-gray-700">{c.errorType}</span>
        {c.severity && (
          <span className={`text-[10px] px-1 rounded font-medium
            ${c.severity === 'HIGH' ? 'text-error bg-error/10' : c.severity === 'MEDIUM' ? 'text-warm-500 bg-warm-500/10' : 'text-sky-500 bg-sky-500/10'}`}>
            {c.severity}
          </span>
        )}
      </div>

      {/* 纠错对比 */}
      <div className="flex items-center gap-1.5 flex-wrap">
        <span className="text-error line-through">{c.originalText}</span>
        <CheckCircle2 size={12} className="text-success" />
        <span className="text-success font-medium">{c.correctedText}</span>
      </div>

      {/* 解释 */}
      {c.explanation && <p className="text-gray-500 mt-1 leading-relaxed">{c.explanation}</p>}

      {/* 改进建议 */}
      {c.improvementTip && (
        <p className="text-gray-500 mt-1 flex items-center gap-1">
          <Lightbulb size={11} className="text-warm-500 shrink-0" />
          {c.improvementTip}
        </p>
      )}
    </div>
  );
}
