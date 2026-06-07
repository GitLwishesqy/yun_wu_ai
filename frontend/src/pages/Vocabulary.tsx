import { useState, useEffect } from 'react';
import { Search, BookOpen, RotateCcw, Library, AlertCircle } from 'lucide-react';
import { useVocabulary } from '../hooks/useVocabulary';
import type { LibraryWord } from '../lib/api';
import WordCard from '../components/vocabulary/WordCard';
import ReviewMode from '../components/vocabulary/ReviewMode';
import Input from '../components/ui/Input';
import Button from '../components/ui/Button';
import Badge from '../components/ui/Badge';

const TABS = [
  { key: '', label: '全部' },
  { key: 'NEW', label: '新词' },
  { key: 'LEARNING', label: '学习中' },
  { key: 'REVIEWING', label: '复习中' },
  { key: 'KNOWN', label: '已掌握' },
];

const STATUS_COLORS: Record<string, { bg: string; text: string }> = {
  NEW: { bg: 'bg-sky-100', text: 'text-sky-700' },
  LEARNING: { bg: 'bg-warm-100', text: 'text-warm-700' },
  REVIEWING: { bg: 'bg-warm-100', text: 'text-warm-700' },
  KNOWN: { bg: 'bg-green-100', text: 'text-green-700' },
  MASTERED: { bg: 'bg-green-100', text: 'text-green-700' },
};

export default function Vocabulary() {
  const { data, loading, error, load, updateStatus } = useVocabulary();
  const [activeTab, setActiveTab] = useState('');
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(1);
  const [mode, setMode] = useState<'list' | 'review'>('list');

  useEffect(() => { load({ status: activeTab || undefined, page: String(page), size: '50', ...(search && { q: search }) }); }, [activeTab, page]);

  const handleSearch = () => { setPage(1); load({ status: activeTab || undefined, q: search, page: '1', size: '50' }); };

  const handleStatusChange = (id: number, status: string) => {
    updateStatus(id, status).then(() => load({ status: activeTab || undefined, page: String(page), size: '50' }));
  };

  const statsTotal = data?.stats ? Object.values(data.stats).reduce((a, b) => a + b, 0) : 0;

  return (
    <div className="py-6 space-y-6">
      {/* 标题 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">我的词汇本</h1>
          <p className="text-gray-500 mt-1">{statsTotal} 个词汇</p>
        </div>
        <Button
          variant={mode === 'review' ? 'secondary' : 'warm'}
          size="sm"
          icon={mode === 'review' ? <BookOpen size={16} /> : <RotateCcw size={16} />}
          onClick={() => setMode(mode === 'list' ? 'review' : 'list')}>
          {mode === 'list' ? '开始复习' : '词汇列表'}
        </Button>
      </div>

      {/* 状态统计条 */}
      {data?.stats && (
        <div className="flex gap-2 overflow-x-auto pb-1">
          {Object.entries(data.stats).map(([k, v]) => (
            <button key={k} onClick={() => setActiveTab(activeTab === k ? '' : k)}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full text-xs font-medium whitespace-nowrap transition-all shrink-0
                ${activeTab === k ? 'bg-primary-500 text-white shadow-md' : 'bg-white border border-gray-200 text-gray-600 hover:border-primary-300'}`}>
              {k} <span className="opacity-70">{v}</span>
            </button>
          ))}
        </div>
      )}

      {/* 错误提示 */}
      {error && <div className="flex items-center gap-2 text-sm text-error bg-error/10 px-3 py-2 rounded-lg"><AlertCircle size={14} />{error}</div>}

      {mode === 'review' ? (
        <ReviewMode onClose={() => setMode('list')} />
      ) : (
        <>
          {/* 搜索 + Tab */}
          <div className="flex gap-2">
            <div className="flex-1">
              <Input placeholder="搜索词汇..." value={search} onChange={e => setSearch(e.target.value)}
                onKeyDown={e => e.key === 'Enter' && handleSearch()} />
            </div>
            <Button variant="secondary" size="md" onClick={handleSearch}><Search size={16} /></Button>
          </div>

          <div className="flex gap-2 overflow-x-auto pb-1">
            {TABS.map(t => (
              <button key={t.key} onClick={() => { setActiveTab(t.key); setPage(1); }}
                className={`px-3 py-1 rounded-full text-xs font-medium whitespace-nowrap transition-all
                  ${activeTab === t.key ? 'bg-primary-500 text-white shadow-md' : 'bg-white border border-gray-200 text-gray-600 hover:border-primary-300'}`}>
                {t.label}
              </button>
            ))}
          </div>

          {/* 词汇网格 */}
          {loading ? (
            <p className="text-center text-gray-400 py-12">加载中...</p>
          ) : data?.items.length === 0 ? (
            <div className="text-center py-16 space-y-3">
              <BookOpen size={40} className="mx-auto text-gray-300" />
              <p className="text-gray-500">暂无词汇</p>
              <p className="text-sm text-gray-400">开始陪练对话，AI 会自动收录你遇到的新词</p>
            </div>
          ) : (
            <>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                {data?.items.map(w => <WordCard key={w.id} word={w} onStatusChange={handleStatusChange} />)}
              </div>
              {/* 分页 */}
              {data && data.pagination.totalPages > 1 && (
                <div className="flex items-center justify-center gap-2">
                  <Button variant="ghost" size="sm" disabled={page <= 1} onClick={() => setPage(p => p - 1)}>上一页</Button>
                  <span className="text-sm text-gray-500">{page} / {data.pagination.totalPages}</span>
                  <Button variant="ghost" size="sm" disabled={page >= data.pagination.totalPages} onClick={() => setPage(p => p + 1)}>下一页</Button>
                </div>
              )}
            </>
          )}
        </>
      )}
    </div>
  );
}
