import { Trophy, Flame, BookOpen, Star, Target, Zap } from 'lucide-react';
import Card from '../components/ui/Card';
import Badge from '../components/ui/Badge';

const iconMap: Record<string, typeof Trophy> = {
  Trophy, Flame, BookOpen, Star, Target, Zap,
};

const ACHS = {
  completed: [
    { code: 'FIRST_SESSION', name: '初次见面', nameEn: 'First Steps', icon: 'Trophy', cat: '学习', pts: 10 },
    { code: 'STREAK_3', name: '三天打鱼', nameEn: '3-Day Streak', icon: 'Flame', cat: '坚持', pts: 20 },
  ],
  inProgress: [
    { code: 'STREAK_7', name: '周而不息', nameEn: '7-Day Streak', icon: 'Flame', cat: '坚持', pts: 50, progress: { current: 5, target: 7 } },
    { code: 'VOCAB_50', name: '词汇新手', nameEn: '50 Words', icon: 'BookOpen', cat: '词汇', pts: 30, progress: { current: 34, target: 50 } },
  ],
  locked: [
    { code: 'STREAK_30', name: '月学不辍', nameEn: '30-Day Streak', icon: 'Flame', cat: '坚持', pts: 200 },
    { code: 'PERFECT_SCORE', name: '满分达成', nameEn: 'Perfect Score', icon: 'Star', cat: '成绩', pts: 100 },
  ],
};

export default function Achievements() {
  return (
    <div className="py-6 space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-800">成就徽章</h1>
        <p className="text-gray-500 mt-1">4/10 已解锁</p>
      </div>

      {(['completed', 'inProgress', 'locked'] as const).map(section => (
        <div key={section}>
          <h3 className="text-sm font-semibold text-gray-400 uppercase tracking-wider mb-3">
            {section === 'completed' ? '✅ 已达成' : section === 'inProgress' ? '🔓 进行中' : '🔒 未解锁'}
          </h3>
          <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
            {ACHS[section].map(a => {
              const Icon = iconMap[a.icon] || Trophy;
              const pct = a.progress ? Math.round(a.progress.current / a.progress.target * 100) : 100;
              return (
                <Card key={a.code} className={`p-4 text-center ${section === 'locked' ? 'opacity-40' : ''}`}>
                  <div className="relative inline-block">
                    <Icon size={32} className={`mx-auto mb-2 ${section === 'completed' ? 'text-warm-500' : 'text-gray-400'}`} />
                    {section === 'completed' && <div className="absolute -top-1 -right-1 text-lg animate-achieve">✨</div>}
                  </div>
                  <p className="font-semibold text-gray-800 text-sm">{a.name}</p>
                  <p className="text-xs text-gray-400">{a.nameEn}</p>
                  {a.progress && (
                    <div className="mt-2">
                      <div className="h-1.5 bg-gray-100 rounded-full overflow-hidden">
                        <div className="h-full bg-primary-500 rounded-full transition-all" style={{ width: `${pct}%` }} />
                      </div>
                      <p className="text-[10px] text-gray-400 mt-1">{a.progress.current}/{a.progress.target}</p>
                    </div>
                  )}
                  <Badge color={section === 'completed' ? 'warm' : 'gray'} className="mt-2">+{a.pts}分</Badge>
                </Card>
              );
            })}
          </div>
        </div>
      ))}
    </div>
  );
}
