import { useState, useEffect } from 'react';
import { Users, Clock, TrendingUp, AlertTriangle, Plus, Eye, Settings, ArrowLeft, AlarmClock, CheckCircle2 } from 'lucide-react';
import { parentApi } from '../lib/api';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import Badge from '../components/ui/Badge';
import Input from '../components/ui/Input';

export default function Parent() {
  const [bindings, setBindings] = useState<any[]>([]);
  const [view, setView] = useState<'list' | 'add' | 'detail'>('list');
  const [overview, setOverview] = useState<any>(null);
  const [phone, setPhone] = useState('');
  const [rel, setRel] = useState('MOTHER');
  const [loading, setLoading] = useState(true);

  useEffect(() => { parentApi.bindings().then(d => { setBindings(d); setLoading(false); }); }, []);

  const handleBind = async () => {
    await parentApi.requestBind(phone, rel);
    setView('list');
    parentApi.bindings().then(setBindings);
  };

  const viewStudent = async (studentId: number) => {
    const ov = await parentApi.studentOverview(studentId);
    setOverview(ov); setView('detail');
  };

  if (loading) return <div className="py-12 text-center text-gray-400">加载中...</div>;

  // ==================== 学生详情 ====================
  if (view === 'detail' && overview) {
    return (
      <div className="py-6 space-y-6">
        <button onClick={() => setView('list')} className="flex items-center gap-1 text-sm text-primary-600"><ArrowLeft size={16} />返回</button>
        <div className="flex items-center gap-4">
          <div className="w-16 h-16 rounded-full bg-primary-200 flex items-center justify-center text-2xl font-bold text-primary-700">
            {overview.nickname?.charAt(0) || '?'}
          </div>
          <div>
            <h1 className="text-xl font-bold text-gray-800">{overview.nickname}</h1>
            <div className="flex gap-1 mt-1"><Badge color="green">{overview.gradeLevel}</Badge><Badge color="sky">{overview.cefrLevel}</Badge></div>
          </div>
        </div>

        {/* 今日 */}
        <div className="grid grid-cols-2 gap-3">
          {[
            { icon: Users, v: overview.today?.sessions || 0, l: '今日会话', color: 'text-primary-500' },
            { icon: Clock, v: `${overview.today?.minutes || 0}分钟`, l: '学习时长', color: 'text-sky-500' },
            { icon: AlarmClock, v: `${overview.today?.remainingMinutes || 0}分钟`, l: '剩余时长', color: 'text-warm-500' },
            { icon: CheckCircle2, v: overview.today?.checkedIn ? '已打卡' : '未打卡', l: '打卡', color: overview.today?.checkedIn ? 'text-success' : 'text-gray-400' },
          ].map(s => (
            <Card key={s.l} className="p-4 text-center">
              <s.icon size={22} className={`mx-auto mb-1 ${s.color}`} />
              <p className="text-lg font-bold text-gray-800">{s.v}</p>
              <p className="text-xs text-gray-400">{s.l}</p>
            </Card>
          ))}
        </div>

        {/* 本周 */}
        <Card className="p-4">
          <h3 className="font-semibold text-gray-800 mb-3">本周统计</h3>
          <div className="grid grid-cols-4 gap-2 text-center">
            {['sessions','minutes','avgScore','scoreChange'].map(k => (
              <div key={k}><p className="text-lg font-bold text-gray-800">{overview.week?.[k] ?? '--'}</p><p className="text-[10px] text-gray-400">{k==='sessions'?'会话':k==='minutes'?'分钟':k==='avgScore'?'均分':'变化'}</p></div>
            ))}
          </div>
        </Card>

        {/* 薄弱点 */}
        {overview.weaknesses && (
          <Card className="p-4 border-l-4 border-l-warm-500">
            <h3 className="font-semibold text-gray-800 mb-2 flex items-center gap-2"><AlertTriangle size={16} className="text-warm-500" />薄弱点</h3>
            <p className="text-sm text-gray-600">{overview.weaknesses}</p>
          </Card>
        )}

        {/* 最近会话 */}
        {overview.recentSessions?.length > 0 && (
          <div>
            <h3 className="font-semibold text-gray-800 mb-2">最近会话</h3>
            <div className="space-y-2">
              {overview.recentSessions.map((s: any) => (
                <Card key={s.id} className="p-3 flex items-center justify-between">
                  <div><p className="text-sm font-medium text-gray-800">{s.sceneName}</p><p className="text-xs text-gray-400">{s.date} · {s.durationMinutes}分钟</p></div>
                  <Badge color="green">{s.score}分</Badge>
                </Card>
              ))}
            </div>
          </div>
        )}
      </div>
    );
  }

  // ==================== 添加绑定 ====================
  if (view === 'add') {
    return (
      <div className="py-6 space-y-6">
        <button onClick={() => setView('list')} className="flex items-center gap-1 text-sm text-primary-600"><ArrowLeft size={16} />返回</button>
        <h1 className="text-2xl font-bold text-gray-800">绑定学生</h1>
        <Card className="p-5 space-y-4">
          <Input placeholder="学生手机号" value={phone} onChange={e => setPhone(e.target.value)} />
          <div>
            <label className="text-sm font-medium text-gray-700 mb-1 block">关系</label>
            <div className="flex gap-2">
              {['MOTHER','FATHER','GUARDIAN'].map(r => (
                <button key={r} onClick={() => setRel(r)}
                  className={`px-4 py-2 rounded-lg text-sm font-medium transition-all
                    ${rel === r ? 'bg-primary-500 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}>
                  {r === 'MOTHER' ? '妈妈' : r === 'FATHER' ? '爸爸' : '监护人'}
                </button>
              ))}
            </div>
          </div>
          <Button className="w-full" onClick={handleBind}>发送绑定请求</Button>
        </Card>
      </div>
    );
  }

  // ==================== 绑定列表 ====================
  return (
    <div className="py-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">家长监控</h1>
          <p className="text-gray-500 mt-1">关注孩子的学习成长</p>
        </div>
        <Button size="sm" icon={<Plus size={16} />} onClick={() => setView('add')}>绑定学生</Button>
      </div>

      {bindings.length === 0 ? (
        <div className="text-center py-16 space-y-4">
          <Users size={48} className="mx-auto text-gray-300" />
          <p className="text-gray-500">还没有绑定学生</p>
          <Button onClick={() => setView('add')} icon={<Plus size={16} />}>绑定学生</Button>
        </div>
      ) : (
        <div className="space-y-3">
          {bindings.map(b => (
            <Card key={b.id} className="p-4 flex items-center gap-4">
              <div className="w-12 h-12 rounded-full bg-primary-200 flex items-center justify-center text-lg font-bold text-primary-700">
                {b.studentId?.toString()?.slice(-2) || '?'}
              </div>
              <div className="flex-1">
                <p className="font-medium text-gray-800">学生 {b.studentId}</p>
                <div className="flex gap-2 mt-1">
                  <Badge color={b.bindingStatus === 'ACTIVE' ? 'green' : 'warm'}>{b.bindingStatus}</Badge>
                  {b.dailyTimeLimitMinutes && <Badge color="sky">{b.dailyTimeLimitMinutes}分钟/天</Badge>}
                </div>
              </div>
              <Button variant="ghost" size="sm" icon={<Eye size={16} />} onClick={() => viewStudent(b.studentId)}>查看</Button>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}


