import { BookOpen, Eye, MessageSquare, CheckCircle2, XCircle } from 'lucide-react';
import type { VocabWord } from '../../lib/api';
import Badge from '../ui/Badge';

const statusColors: Record<string, { color: 'green' | 'sky' | 'warm' | 'gray'; label: string }> = {
  NEW: { color: 'sky', label: '新词' },
  LEARNING: { color: 'warm', label: '学习中' },
  REVIEWING: { color: 'warm', label: '复习中' },
  KNOWN: { color: 'green', label: '已掌握' },
  MASTERED: { color: 'green', label: '已精通' },
};

export default function WordCard({ word, onStatusChange }: {
  word: VocabWord; onStatusChange?: (id: number, status: string) => void;
}) {
  const s = statusColors[word.status] || statusColors.NEW;
  const accuracy = word.seenCount > 0 ? Math.round((word.correctCount || 0) / word.seenCount * 100) : 0;

  return (
    <div className="bg-white rounded-xl border border-gray-200 p-4 hover:border-primary-300 hover:shadow-sm transition-all group">
      <div className="flex items-start justify-between mb-3">
        <div>
          <h4 className="text-lg font-bold text-gray-800">{word.word}</h4>
          <p className="text-sm text-gray-500">{word.translation}</p>
        </div>
        <div className="flex flex-col items-end gap-1.5">
          <Badge color={s.color}>{s.label}</Badge>
          {onStatusChange && (
            <select
              value={word.status}
              onChange={e => onStatusChange(word.id, e.target.value)}
              className="text-[10px] border border-gray-200 rounded px-1 py-0.5 bg-gray-50 opacity-0 group-hover:opacity-100 transition-opacity">
              <option value="NEW">新词</option>
              <option value="LEARNING">学习中</option>
              <option value="REVIEWING">复习中</option>
              <option value="KNOWN">已掌握</option>
              <option value="MASTERED">已精通</option>
            </select>
          )}
        </div>
      </div>

      <div className="grid grid-cols-4 gap-2 text-center">
        {[
          { icon: Eye, v: word.seenCount, l: '见过' },
          { icon: MessageSquare, v: word.usedCount, l: '用过' },
          { icon: CheckCircle2, v: word.correctCount, l: '正确' },
          { icon: XCircle, v: word.errorCount, l: '错误' },
        ].map(({ icon: Icon, v, l }) => (
          <div key={l} className="flex flex-col items-center gap-0.5">
            <Icon size={14} className="text-gray-400" />
            <span className="text-sm font-bold text-gray-700">{v}</span>
            <span className="text-[10px] text-gray-400">{l}</span>
          </div>
        ))}
      </div>

      {/* 准确率条 */}
      {word.seenCount > 0 && (
        <div className="mt-3 flex items-center gap-2">
          <div className="flex-1 h-1.5 bg-gray-100 rounded-full overflow-hidden">
            <div className={`h-full rounded-full transition-all ${accuracy >= 80 ? 'bg-success' : accuracy >= 50 ? 'bg-warm-500' : 'bg-error'}`}
              style={{ width: `${accuracy}%` }} />
          </div>
          <span className="text-[10px] text-gray-400">{accuracy}%</span>
        </div>
      )}
    </div>
  );
}
