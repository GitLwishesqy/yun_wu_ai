import { useState, useEffect } from 'react';
import { Calendar, Flame, Trophy, Coins, TrendingUp, Sparkles } from 'lucide-react';
import { incentiveApi } from '../lib/api';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import Badge from '../components/ui/Badge';

export default function Incentive() {
  const [tab, setTab] = useState<'checkin' | 'points' | 'leaderboard'>('checkin');
  const [checkinData, setCheckinData] = useState<any>(null);
  const [pointsData, setPointsData] = useState<any>(null);
  const [leaderboard, setLeaderboard] = useState<any>(null);
  const [checkinAnim, setCheckinAnim] = useState(false);
  const [currentMonth] = useState(() => new Date().toISOString().slice(0, 7));

  useEffect(() => {
    if (tab === 'checkin') incentiveApi.calendar(currentMonth).then(setCheckinData);
    if (tab === 'points') incentiveApi.points().then(setPointsData);
    if (tab === 'leaderboard') incentiveApi.leaderboard().then(setLeaderboard);
  }, [tab]);

  const handleCheckIn = async () => {
    try {
      const r = await incentiveApi.checkIn();
      setCheckinAnim(true);
      setTimeout(() => setCheckinAnim(false), 2000);
      incentiveApi.calendar(currentMonth).then(setCheckinData);
    } catch (e: any) { alert(e.message); }
  };

  const today = new Date().toISOString().slice(0, 10);
  const isChecked = checkinData?.checkedDates?.includes(today);

  const TABS = [
    { key: 'checkin' as const, label: '打卡', icon: Calendar },
    { key: 'points' as const, label: '积分', icon: Coins },
    { key: 'leaderboard' as const, label: '排行', icon: Trophy },
  ];

  return (
    <div className="py-6 space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-800">日常激励</h1>
        <p className="text-gray-500 mt-1">坚持学习，收获成长</p>
      </div>

      <div className="flex gap-1 bg-white rounded-xl border border-gray-200 p-1">
        {TABS.map(t => {
          const active = tab === t.key; const Icon = t.icon;
          return (
            <button key={t.key} onClick={() => setTab(t.key)}
              className={`flex-1 flex items-center justify-center gap-2 py-2.5 rounded-lg text-sm font-medium transition-all
                ${active ? 'bg-primary-500 text-white shadow-md' : 'text-gray-500 hover:bg-gray-50'}`}>
              <Icon size={18} />{t.label}
            </button>
          );
        })}
      </div>

      {/* ==================== 打卡 ==================== */}
      {tab === 'checkin' && (
        <div className="space-y-6">
          {/* 打卡按钮 */}
          <div className="text-center relative">
            <button onClick={handleCheckIn} disabled={isChecked}
              className={`w-28 h-28 rounded-full font-bold text-white transition-all duration-300 shadow-xl
                ${isChecked ? 'bg-success cursor-default' : 'bg-warm-500 hover:bg-warm-600 hover:scale-105 active:scale-95'}
                ${checkinAnim ? 'animate-achieve' : ''}`}>
              {isChecked ? <><CheckIcon /><span className="block text-sm mt-1">已打卡</span></>
                : <><Flame size={32} className="mx-auto" /><span className="block text-sm mt-1">打卡</span></>}
            </button>
            {checkinAnim && <SparklesAnim />}
          </div>

          {/* 连续天数 */}
          <div className="grid grid-cols-3 gap-3">
            {[
              { icon: Flame, v: checkinData?.currentStreak || 0, l: '连续天数', color: 'text-warm-500' },
              { icon: Calendar, v: checkinData?.totalThisMonth || 0, l: '本月打卡', color: 'text-primary-500' },
              { icon: Coins, v: checkinData?.checkedDates?.length ? checkinData.checkedDates.length * 5 : 0, l: '累计积分', color: 'text-sky-500' },
            ].map(s => (
              <Card key={s.l} className="p-4 text-center">
                <s.icon size={22} className={`mx-auto mb-1 ${s.color}`} />
                <p className="text-xl font-bold text-gray-800">{s.v}</p>
                <p className="text-xs text-gray-400">{s.l}</p>
              </Card>
            ))}
          </div>

          {/* 日历 */}
          <Card className="p-4">
            <h3 className="font-semibold text-gray-800 mb-3 flex items-center gap-2"><Calendar size={18} />{currentMonth}</h3>
            <div className="grid grid-cols-7 gap-1 text-center">
              {['日', '一', '二', '三', '四', '五', '六'].map(d => (
                <span key={d} className="text-xs text-gray-400 py-1">{d}</span>
              ))}
              {Array.from({ length: 35 }, (_, i) => {
                const d = i - new Date(currentMonth + '-01').getDay() + 1;
                const dateStr = `${currentMonth}-${String(d).padStart(2, '0')}`;
                const checked = checkinData?.checkedDates?.includes(dateStr);
                const isToday = dateStr === today;
                return (
                  <div key={i} className={`aspect-square flex items-center justify-center rounded-lg text-sm font-medium
                    ${d < 1 || d > 31 ? 'invisible' : ''}
                    ${checked ? 'bg-primary-500 text-white' : 'bg-gray-50 text-gray-600'}
                    ${isToday && !checked ? 'ring-2 ring-primary-300' : ''}`}>
                    {d > 0 && d <= 31 ? d : ''}
                  </div>
                );
              })}
            </div>
          </Card>
        </div>
      )}

      {/* ==================== 积分 ==================== */}
      {tab === 'points' && (
        <div className="space-y-4">
          <Card className="p-5 text-center bg-gradient-to-br from-warm-50 to-primary-50">
            <Coins size={36} className="mx-auto text-warm-500 mb-2" />
            <p className="text-4xl font-bold text-gray-800">{pointsData?.totalPoints || 0}</p>
            <p className="text-gray-500 text-sm">总积分</p>
          </Card>

          <h3 className="font-semibold text-gray-800">积分记录</h3>
          <div className="space-y-2">
            {(pointsData?.records || []).map((r: any) => (
              <Card key={r.id} className="p-3 flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-800">{r.actionDesc}</p>
                  <p className="text-xs text-gray-400">{new Date(r.createdAt).toLocaleDateString()}</p>
                </div>
                <div className="text-right">
                  <span className={`font-bold ${r.points > 0 ? 'text-success' : 'text-error'}`}>
                    {r.points > 0 ? '+' : ''}{r.points}
                  </span>
                  <p className="text-xs text-gray-400">余额 {r.balanceAfter}</p>
                </div>
              </Card>
            ))}
            {(!pointsData?.records || pointsData.records.length === 0) && (
              <p className="text-center text-gray-400 py-8">暂无积分记录</p>
            )}
          </div>
        </div>
      )}

      {/* ==================== 排行榜 ==================== */}
      {tab === 'leaderboard' && (
        <div className="space-y-2">
          {leaderboard?.entries?.map((e: any, i: number) => (
            <Card key={e.userId} className="p-3 flex items-center gap-3">
              <span className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold
                ${i === 0 ? 'bg-warm-500 text-white' : i === 1 ? 'bg-gray-300 text-white' : i === 2 ? 'bg-orange-400 text-white' : 'bg-gray-100 text-gray-500'}`}>
                {i + 1}
              </span>
              <div className="w-9 h-9 rounded-full bg-primary-200 flex items-center justify-center text-primary-700 font-bold text-sm">
                {e.nickname?.charAt(0) || '?'}
              </div>
              <div className="flex-1 min-w-0">
                <p className="font-medium text-gray-800 truncate">{e.nickname}</p>
              </div>
              <div className="flex items-center gap-1 text-warm-500 font-bold">
                <Trophy size={14} />{e.totalPoints}
              </div>
            </Card>
          ))}
          {(!leaderboard?.entries || leaderboard.entries.length === 0) && (
            <p className="text-center text-gray-400 py-8">暂无排行数据</p>
          )}
        </div>
      )}
    </div>
  );
}

function CheckIcon() {
  return (
    <svg width="32" height="32" viewBox="0 0 32 32" fill="none" className="mx-auto">
      <path d="M8 16L14 22L24 10" stroke="white" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round"/>
    </svg>
  );
}

function SparklesAnim() {
  return (
    <div className="absolute inset-0 pointer-events-none">
      {Array.from({ length: 12 }).map((_, i) => (
        <span key={i} className="absolute w-2 h-2 rounded-full animate-ping"
          style={{
            left: `${50 + (Math.random() - 0.5) * 80}%`,
            top: `${50 + (Math.random() - 0.5) * 80}%`,
            backgroundColor: ['#F0A858', '#7EC8A0', '#F5C26B', '#68B8D8'][i % 4],
            animationDelay: `${Math.random() * 0.5}s`,
            animationDuration: `${1 + Math.random()}s`,
          }} />
      ))}
    </div>
  );
}
