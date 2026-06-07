import { useState, useEffect } from 'react';
import { Calendar, Target, Clock, CheckCircle2, Circle, Sparkles, BookOpen, Headphones, Mic } from 'lucide-react';
import { planApi } from '../lib/api';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import Badge from '../components/ui/Badge';

const EFFORTS = [
  { key: 'LIGHT', label: '轻松', desc: '每天10分钟', icon: '🌱' },
  { key: 'MEDIUM', label: '适中', desc: '每天20分钟', icon: '🌿' },
  { key: 'INTENSIVE', label: '密集', desc: '每天30分钟', icon: '🌳' },
];

const typeIcons: Record<string, typeof BookOpen> = {
  COACH_SESSION: Mic, VOCAB_REVIEW: BookOpen, LISTENING: Headphones, READING: BookOpen, WRITING: BookOpen,
};

export default function Plans() {
  const [plan, setPlan] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [showGenerate, setShowGenerate] = useState(false);
  const [form, setForm] = useState({ name: '', targetLevel: 'A2', weeklyEffort: 'MEDIUM' });
  const [generating, setGenerating] = useState(false);

  useEffect(() => { planApi.getActive().then(p => { setPlan(p); setLoading(false); }).catch(() => setLoading(false)); }, []);

  const handleGenerate = async () => {
    setGenerating(true);
    const today = new Date();
    const p = await planApi.generate({
      ...form,
      startDate: today.toISOString().slice(0, 10),
      endDate: new Date(today.getTime() + 30 * 86400000).toISOString().slice(0, 10),
    });
    setPlan(p); setShowGenerate(false); setGenerating(false);
  };

  if (loading) return <div className="py-12 text-center text-gray-400">加载中...</div>;

  if (!plan && !showGenerate) {
    return (
      <div className="py-16 text-center space-y-6">
        <Target size={48} className="mx-auto text-gray-300" />
        <div>
          <h1 className="text-2xl font-bold text-gray-800">学习计划</h1>
          <p className="text-gray-500 mt-1">还没有学习计划，让 AI 帮你生成一个</p>
        </div>
        <Button onClick={() => setShowGenerate(true)} icon={<Sparkles size={16} />}>AI 生成计划</Button>
      </div>
    );
  }

  if (showGenerate) {
    return (
      <div className="py-6 space-y-6">
        <h1 className="text-2xl font-bold text-gray-800">AI 生成学习计划</h1>
        <Card className="p-5 space-y-4">
          <div>
            <label className="text-sm font-medium text-gray-700">计划名称</label>
            <input value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))}
              placeholder="如: 7月暑假特训" className="w-full mt-1 px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-primary-300 text-sm" />
          </div>
          <div>
            <label className="text-sm font-medium text-gray-700">目标等级</label>
            <div className="flex gap-2 mt-1">
              {['A1', 'A2', 'B1', 'B2', 'C1'].map(l => (
                <button key={l} onClick={() => setForm(f => ({ ...f, targetLevel: l }))}
                  className={`px-4 py-2 rounded-lg text-sm font-medium transition-all
                    ${form.targetLevel === l ? 'bg-primary-500 text-white shadow-md' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}>{l}</button>
              ))}
            </div>
          </div>
          <div>
            <label className="text-sm font-medium text-gray-700">学习强度</label>
            <div className="grid grid-cols-3 gap-2 mt-1">
              {EFFORTS.map(e => (
                <button key={e.key} onClick={() => setForm(f => ({ ...f, weeklyEffort: e.key }))}
                  className={`p-3 rounded-xl text-center transition-all
                    ${form.weeklyEffort === e.key ? 'bg-primary-100 border-2 border-primary-500 shadow-sm' : 'bg-gray-50 border-2 border-transparent hover:bg-gray-100'}`}>
                  <p className="text-2xl">{e.icon}</p>
                  <p className="text-sm font-bold text-gray-800 mt-1">{e.label}</p>
                  <p className="text-[10px] text-gray-400">{e.desc}</p>
                </button>
              ))}
            </div>
          </div>
          <div className="flex gap-3 pt-2">
            <Button variant="secondary" className="flex-1" onClick={() => setShowGenerate(false)}>取消</Button>
            <Button className="flex-1" onClick={handleGenerate} loading={generating} icon={<Sparkles size={16} />}>生成</Button>
          </div>
        </Card>
      </div>
    );
  }

  const pct = plan.progress?.completionPct || 0;

  return (
    <div className="py-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">{plan.name}</h1>
          <p className="text-gray-500 text-sm mt-1">
            {plan.startDate} ~ {plan.endDate} · 目标: {plan.targetLevel}
          </p>
        </div>
        <Button variant="secondary" size="sm" onClick={() => setShowGenerate(true)}>重新生成</Button>
      </div>

      {/* 总进度 */}
      <Card className="p-5">
        <div className="flex items-center justify-between mb-2">
          <span className="text-sm font-medium text-gray-700">总进度</span>
          <span className="text-sm text-gray-500">{plan.progress?.completedItems}/{plan.progress?.totalItems} 项</span>
        </div>
        <div className="h-3 bg-gray-100 rounded-full overflow-hidden">
          <div className="h-full bg-gradient-to-r from-primary-400 to-primary-600 rounded-full transition-all duration-500"
            style={{ width: `${pct}%` }} />
        </div>
        <p className="text-center text-sm text-gray-400 mt-2">{Math.round(pct)}% 完成</p>
      </Card>

      {/* 今日任务 */}
      <div>
        <h3 className="font-semibold text-gray-800 mb-3 flex items-center gap-2">
          <Calendar size={18} className="text-primary-500" />今日任务
        </h3>
        <div className="space-y-2">
          {(plan.todayItems || []).map((item: any) => {
            const Icon = typeIcons[item.itemType] || BookOpen;
            return (
              <Card key={item.id} className={`p-3 flex items-center gap-3 transition-all ${item.isCompleted ? 'opacity-60' : ''}`}>
                {item.isCompleted
                  ? <CheckCircle2 size={22} className="text-success shrink-0" />
                  : <Circle size={22} className="text-gray-300 shrink-0" />}
                <div className="w-8 h-8 rounded-lg bg-primary-100 flex items-center justify-center shrink-0">
                  <Icon size={16} className="text-primary-600" />
                </div>
                <div className="flex-1 min-w-0">
                  <p className={`text-sm font-medium ${item.isCompleted ? 'text-gray-400 line-through' : 'text-gray-800'}`}>
                    {item.itemName}
                  </p>
                  <p className="text-xs text-gray-400">{item.estimatedMinutes}分钟 · +{item.pointsReward}分</p>
                </div>
                <Badge color={item.isCompleted ? 'green' : 'warm'}>{item.isCompleted ? '完成' : '待做'}</Badge>
              </Card>
            );
          })}
          {(!plan.todayItems || plan.todayItems.length === 0) && (
            <p className="text-center text-gray-400 py-4">今日暂无任务 🎉</p>
          )}
        </div>
      </div>
    </div>
  );
}
