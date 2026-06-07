import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import { MessageCircle, BookOpen, BarChart3, Trophy, User, Mic } from 'lucide-react';
import Logo from '../ui/Logo';

const tabs = [
  { path: '/coach', icon: Mic, label: '陪练' },
  { path: '/scenes', icon: BookOpen, label: '场景' },
  { path: '/report', icon: BarChart3, label: '报告' },
  { path: '/achievements', icon: Trophy, label: '成就' },
  { path: '/me', icon: User, label: '我的' },
];

export default function StudentShell() {
  const { pathname } = useLocation();
  const navigate = useNavigate();

  return (
    <div className="min-h-dvh flex flex-col bg-gray-50">
      {/* 顶部导航 */}
      <header className="sticky top-0 z-50 bg-white/80 backdrop-blur-md border-b border-gray-200">
        <div className="max-w-4xl mx-auto px-4 h-14 flex items-center justify-between">
          <Logo size="sm" />
          <nav className="hidden sm:flex items-center gap-1">
            {tabs.map(t => {
              const active = pathname.startsWith(t.path);
              return (
                <button key={t.path} onClick={() => navigate(t.path)}
                  className={`px-3 py-1.5 rounded-lg text-sm font-medium transition-all duration-200
                    ${active ? 'bg-primary-100 text-primary-700' : 'text-gray-500 hover:bg-gray-100'}`}>
                  {t.label}
                </button>
              );
            })}
          </nav>
          <div className="w-8 h-8 rounded-full bg-primary-200" />
        </div>
      </header>

      {/* 内容区域 */}
      <main className="flex-1 max-w-4xl mx-auto w-full px-4 pb-24 sm:pb-8">
        <Outlet />
      </main>

      {/* 底部快捷导航 (移动端) */}
      <nav className="sm:hidden fixed bottom-0 left-0 right-0 z-50 bg-white border-t border-gray-200 safe-area-bottom">
        <div className="flex items-center justify-around h-16">
          {tabs.map(t => {
            const active = pathname.startsWith(t.path);
            const Icon = t.icon;
            return (
              <button key={t.path} onClick={() => navigate(t.path)}
                className={`flex flex-col items-center gap-0.5 min-w-0 px-2 py-1 rounded-lg transition-colors
                  ${active ? 'text-primary-600' : 'text-gray-400'}`}>
                <Icon size={22} strokeWidth={active ? 2.5 : 2} />
                <span className="text-[10px] font-medium">{t.label}</span>
              </button>
            );
          })}
        </div>
      </nav>
    </div>
  );
}
