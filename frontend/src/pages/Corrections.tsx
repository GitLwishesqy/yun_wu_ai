import { useState, useEffect } from 'react';
import { AlertCircle, TrendingUp, Target, RotateCcw, CheckCircle2 } from 'lucide-react';
import { correctionApi } from '../lib/api';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import Badge from '../components/ui/Badge';

const DIMENSIONS = ['grammar', 'pronunciation', 'vocabulary', 'fluency', 'logic'];
const DIM_LABELS: Record<string, string> = { grammar: '语法', pronunciation: '发音', vocabulary: '词汇', fluency: '流利度', logic: '逻辑' };

export default function Corrections() {
  const [tab, setTab] = useState<'analysis' | 'history' | 'review'>('analysis');
  const [analysis, setAnalysis] = useState<any>(null);
  const [history, setHistory] = useState<any[]>([]);
  const [reviewDue, setReviewDue] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (tab === 'analysis') correctionApi.analysis().then(d => { setAnalysis(d); setLoading(false); });
    if (tab === 'history') correctionApi.history().then(d => { setHistory(d?.items || []); setLoading(false); });
    if (tab === 'review') correctionApi.reviewDue().then(d => { setReviewDue(d); setLoading(false); });
  }, [tab]);

  const handleReview = async (id: number) => {
    await correctionApi.completeReview(id);
    setReviewDue(prev => prev.filter(r => r.id !== id));
  };

  const TABS = [
    { key: 'analysis' as const, label: '薄弱分析', icon: Target },
    { key: 'history' as const, label: '纠错历史', icon: AlertCircle },
    { key: 'review' as const, label: '待复习', icon: RotateCcw },
  ];

  return (
    <div className="py-6 space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-800">纠错与反馈</h1>
        <p className="text-gray-500 mt-1">追踪你的每一个进步</p>
      </div>

      <div className="flex gap-1 bg-white rounded-xl border border-gray-200 p-1">
        {TABS.map(t => {
          const active = tab === t.key; const Icon = t.icon;
          return (
            <button key={t.key} onClick={() => { setTab(t.key); setLoading(true); }}
              className={`flex-1 flex items-center justify-center gap-2 py-2.5 rounded-lg text-sm font-medium transition-all
                ${active ? 'bg-primary-500 text-white shadow-md' : 'text-gray-500 hover:bg-gray-50'}`}>
              <Icon size={18} />{t.label}
            </button>
          );
        })}
      </div>

      {/* ==================== 薄弱分析 ==================== */}
      {tab === 'analysis' && analysis && (
        <div className="space-y-6 animate-bubble-in">
          {/* 雷达图模拟 */}
          <Card className="p-5">
            <h3 className="font-semibold text-gray-800 mb-4">五维能力雷达</h3>
            <div className="grid grid-cols-5 gap-2">
              {DIMENSIONS.map(dim => {
                const v = analysis.radar?.[dim] || 0;
                const pct = Math.round(v * 100);
                return (
                  <div key={dim} className="text-center space-y-1">
                    <div className="relative w-full aspect-square rounded-full bg-gray-100 flex items-center justify-center mx-auto max-w-[64px]">
                      <div className="absolute inset-1 rounded-full border-4 border-primary-400"
                        style={{ clipPath: `inset(${100 - pct}% 0 0 0)` }} />
                      <span className="text-sm font-bold text-gray-800 relative z-10">{pct}%</span>
                    </div>
                    <p className="text-[10px] text-gray-500">{DIM_LABELS[dim]}</p>
                  </div>
                );
              })}
            </div>
          </Card>

          {/* Top 错误 */}
          <Card className="p-4">
            <h3 className="font-semibold text-gray-800 mb-3">高频错误</h3>
            <div className="space-y-2">
              {(analysis.topErrors || []).map((e: any, i: number) => (
                <div key={i} className="flex items-center justify-between py-2 border-b border-gray-50 last:border-0">
                  <div className="flex items-center gap-2">
                    <span className="w-6 h-6 rounded-full bg-error/10 text-error text-xs font-bold flex items-center justify-center">{i + 1}</span>
                    <span className="text-sm font-medium text-gray-700">{e.errorType}</span>
                  </div>
                  <Badge color="red">{e.count}次</Badge>
                </div>
              ))}
              {(!analysis.topErrors || analysis.topErrors.length === 0) && (
                <p className="text-center text-gray-400 py-4">暂无数据</p>
              )}
            </div>
          </Card>

          {/* 趋势 */}
          {analysis.dailyTrend?.length > 0 && (
            <Card className="p-4">
              <h3 className="font-semibold text-gray-800 mb-3 flex items-center gap-2"><TrendingUp size={18} />每日错误趋势</h3>
              <div className="flex items-end gap-1 h-24">
                {analysis.dailyTrend.map((d: any, i: number) => (
                  <div key={i} className="flex-1 flex flex-col items-center gap-1">
                    <div className="w-full bg-primary-400 rounded-t-sm transition-all hover:bg-primary-500"
                      style={{ height: `${Math.min(d.count * 10, 100)}%` }} />
                    <span className="text-[8px] text-gray-400 rotate-45 origin-left whitespace-nowrap">{d.date?.slice(5)}</span>
                  </div>
                ))}
              </div>
            </Card>
          )}

          {/* 薄弱点 */}
          <Card className="p-4">
            <h3 className="font-semibold text-gray-800 mb-3">需要关注</h3>
            <div className="space-y-2">
              {(analysis.weakPoints || []).slice(0, 5).map((w: any) => (
                <div key={w.id} className="flex items-center justify-between p-2 rounded-lg bg-warm-50">
                  <div>
                    <p className="text-sm font-medium text-gray-800">{w.errorPattern || w.errorSubtype}</p>
                    <p className="text-xs text-gray-400">{w.errorType} · {w.totalCount}次</p>
                  </div>
                  <Badge color={w.masteryStatus === 'MASTERED' ? 'green' : 'warm'}>{w.masteryStatus}</Badge>
                </div>
              ))}
            </div>
          </Card>
        </div>
      )}

      {/* ==================== 纠错历史 ==================== */}
      {tab === 'history' && (
        <div className="space-y-3">
          {history.map((c: any) => (
            <Card key={c.id} className="p-4 space-y-2">
              <div className="flex items-center justify-between">
                <Badge color={c.errorType === 'GRAMMAR' ? 'red' : c.errorType === 'PRONUNCIATION' ? 'warm' : 'sky'}>
                  {c.errorType}
                </Badge>
                <span className="text-xs text-gray-400">{new Date(c.createdAt).toLocaleDateString()}</span>
              </div>
              <div className="flex items-center gap-2 text-sm">
                <span className="text-error line-through">{c.originalText}</span>
                <span className="text-success font-medium">→ {c.correctedText}</span>
              </div>
              {c.explanation && <p className="text-xs text-gray-500">{c.explanation}</p>}
            </Card>
          ))}
          {history.length === 0 && <p className="text-center text-gray-400 py-8">暂无纠错记录</p>}
        </div>
      )}

      {/* ==================== 待复习 ==================== */}
      {tab === 'review' && (
        <div className="space-y-3">
          <p className="text-sm text-gray-500">基于艾宾浩斯遗忘曲线，以下知识点需要复习</p>
          {reviewDue.map((r: any) => (
            <Card key={r.id} className="p-4 flex items-center justify-between">
              <div>
                <p className="font-medium text-gray-800">{r.errorPattern || r.errorSubtype}</p>
                <p className="text-xs text-gray-400">{r.errorType} · 复习{r.reviewCount}次 · 下次 {r.nextReviewAt?.slice(0, 10)}</p>
              </div>
              <Button size="sm" onClick={() => handleReview(r.id)} icon={<CheckCircle2 size={14} />}>完成复习</Button>
            </Card>
          ))}
          {reviewDue.length === 0 && (
            <div className="text-center py-12 space-y-3">
              <CheckCircle2 size={40} className="mx-auto text-success" />
              <p className="text-gray-500">暂无待复习内容 🎉</p>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
