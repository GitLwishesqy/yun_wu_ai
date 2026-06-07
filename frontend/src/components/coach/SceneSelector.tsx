import { useState, useEffect } from 'react';
import { Search, X } from 'lucide-react';
import { sceneApi, type SceneData } from '../../lib/api';
import Button from '../ui/Button';
import Input from '../ui/Input';
import Badge from '../ui/Badge';

const CATS = [
  { key: '', label: '全部' },
  { key: 'DAILY_LIFE', label: '日常' },
  { key: 'TRAVEL', label: '旅行' },
  { key: 'BUSINESS', label: '商务' },
  { key: 'ACADEMIC', label: '学术' },
  { key: 'EXAM', label: '考试' },
];

export default function SceneSelector({ onSelect, onClose }: {
  onSelect: (scene: SceneData) => void; onClose: () => void;
}) {
  const [scenes, setScenes] = useState<SceneData[]>([]);
  const [search, setSearch] = useState('');
  const [cat, setCat] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    sceneApi.list({ size: '50' }).then(d => { setScenes(d.items); setLoading(false); }).catch(() => setLoading(false));
  }, []);

  const filtered = scenes.filter(s =>
    (!cat || s.category === cat) &&
    (!search || s.name.includes(search) || (s.nameEn || '').toLowerCase().includes(search.toLowerCase()))
  );

  return (
    <div className="fixed inset-0 z-50 bg-black/40 backdrop-blur-sm flex items-end sm:items-center justify-center" onClick={onClose}>
      <div className="bg-white w-full sm:max-w-lg sm:rounded-2xl max-h-[85dvh] flex flex-col shadow-2xl animate-bubble-in" onClick={e => e.stopPropagation()}>
        <div className="flex items-center justify-between px-5 py-4 border-b border-gray-100">
          <h3 className="text-lg font-bold text-gray-800">选择场景</h3>
          <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-gray-100"><X size={20} /></button>
        </div>

        <div className="px-4 py-3 space-y-3">
          <Input placeholder="搜索场景..." value={search} onChange={e => setSearch(e.target.value)} />
          <div className="flex gap-2 overflow-x-auto pb-1">
            {CATS.map(c => (
              <button key={c.key} onClick={() => setCat(c.key)}
                className={`px-3 py-1 rounded-full text-xs font-medium whitespace-nowrap transition-all shrink-0
                  ${cat === c.key ? 'bg-primary-500 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}>
                {c.label}
              </button>
            ))}
          </div>
        </div>

        <div className="flex-1 overflow-y-auto px-4 pb-6 space-y-2">
          {loading ? (
            <p className="text-center text-gray-400 py-8">加载中...</p>
          ) : filtered.length === 0 ? (
            <p className="text-center text-gray-400 py-8">无匹配场景</p>
          ) : (
            filtered.map(s => (
              <button key={s.id} onClick={() => onSelect(s)}
                className="w-full text-left px-4 py-3 rounded-xl border border-gray-200 hover:border-primary-300 hover:shadow-sm transition-all group">
                <div className="flex items-center justify-between">
                  <span className="font-medium text-gray-800 group-hover:text-primary-700">{s.name}</span>
                  <div className="flex gap-1.5">
                    <Badge color="green">{s.cefrLevel || 'A1'}</Badge>
                    <Badge color="gray">难度 {s.difficulty}/9</Badge>
                  </div>
                </div>
                <p className="text-xs text-gray-400 mt-0.5">{s.nameEn} · 约{s.estimatedDuration || 15}分钟</p>
              </button>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
