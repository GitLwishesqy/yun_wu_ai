import { useState, useEffect } from 'react';
import { useVocabulary } from '../../hooks/useVocabulary';
import type { VocabWord } from '../../lib/api';
import Button from '../ui/Button';
import { RotateCcw, ArrowRight, Lightbulb } from 'lucide-react';

export default function ReviewMode({ onClose }: { onClose: () => void }) {
  const { getReviewDue, completeReview } = useVocabulary();
  const [words, setWords] = useState<VocabWord[]>([]);
  const [index, setIndex] = useState(0);
  const [flipped, setFlipped] = useState(false);
  const [done, setDone] = useState(false);

  useEffect(() => { getReviewDue(20).then(setWords); }, []);

  const handleReview = async (known: boolean) => {
    if (known) await completeReview(words[index].id);
    if (index + 1 < words.length) {
      setIndex(i => i + 1); setFlipped(false);
    } else {
      setDone(true);
    }
  };

  if (words.length === 0) {
    return (
      <div className="text-center py-16 space-y-4">
        <Lightbulb size={40} className="mx-auto text-warm-500" />
        <p className="text-gray-500">暂无待复习词汇 🎉</p>
        <Button variant="secondary" onClick={onClose}>返回词汇本</Button>
      </div>
    );
  }

  if (done) {
    return (
      <div className="text-center py-16 space-y-4 animate-achieve">
        <div className="text-5xl">🎉</div>
        <p className="text-xl font-bold text-gray-800">复习完成!</p>
        <p className="text-gray-500">已复习 {words.length} 个词汇</p>
        <Button onClick={() => { setIndex(0); setDone(false); setFlipped(false); getReviewDue(20).then(setWords); }} icon={<RotateCcw size={16} />}>再来一轮</Button>
      </div>
    );
  }

  const w = words[index];

  return (
    <div className="space-y-6">
      {/* 进度 */}
      <div className="flex items-center justify-between text-sm text-gray-500">
        <span>{index + 1} / {words.length}</span>
        <div className="flex-1 mx-4 h-1.5 bg-gray-100 rounded-full overflow-hidden">
          <div className="h-full bg-primary-500 rounded-full transition-all" style={{ width: `${((index + 1) / words.length) * 100}%` }} />
        </div>
        <button onClick={onClose} className="text-primary-600 text-sm font-medium">退出</button>
      </div>

      {/* 翻转卡片 */}
      <div
        onClick={() => setFlipped(!flipped)}
        className={`relative w-full aspect-[3/2] cursor-pointer perspective-1000 ${flipped ? 'rotate-y-180' : ''}`}>
        <div className={`absolute inset-0 bg-white rounded-2xl border-2 border-primary-200 shadow-lg flex flex-col items-center justify-center p-8 transition-all duration-500 ${flipped ? 'opacity-0' : 'opacity-100'}`}>
          <p className="text-3xl font-bold text-gray-800 mb-3">{w.word}</p>
          <p className="text-gray-400 text-sm">点击翻转查看释义</p>
        </div>
        <div className={`absolute inset-0 bg-primary-50 rounded-2xl border-2 border-primary-200 shadow-lg flex flex-col items-center justify-center p-8 transition-all duration-500 ${flipped ? 'opacity-100' : 'opacity-0'}`}>
          <p className="text-2xl font-bold text-primary-700 mb-2">{w.translation}</p>
          <p className="text-lg text-gray-800">{w.word}</p>
          <p className="text-xs text-gray-400 mt-3">见过 {w.seenCount} 次 · 正确 {w.correctCount} 次</p>
        </div>
      </div>

      {/* 记住/没记住 */}
      {flipped && (
        <div className="flex gap-3 animate-bubble-in">
          <Button variant="danger" className="flex-1" onClick={() => handleReview(false)}>没记住</Button>
          <Button className="flex-1" onClick={() => handleReview(true)} icon={<ArrowRight size={16} />}>记住了</Button>
        </div>
      )}
    </div>
  );
}
