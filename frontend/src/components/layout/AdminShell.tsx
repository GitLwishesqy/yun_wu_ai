import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { LayoutDashboard, Users, BookOpenCheck, ShieldCheck, Settings, FileText, ChevronLeft } from 'lucide-react';
import Logo from '../ui/Logo';
import { useState } from 'react';

const menus = [
  { path: '/admin', icon: LayoutDashboard, label: '仪表盘' },
  { path: '/admin/users', icon: Users, label: '用户管理' },
  { path: '/admin/scenes', icon: BookOpenCheck, label: '场景管理' },
  { path: '/admin/review', icon: ShieldCheck, label: '内容审核' },
  { path: '/admin/settings', icon: Settings, label: '系统配置' },
  { path: '/admin/audit', icon: FileText, label: '审计日志' },
];

export default function AdminShell() {
  const { pathname } = useLocation();
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState(false);

  return (
    <div className="min-h-dvh flex bg-gray-50">
      {/* 侧边栏 */}
      <aside className={`${collapsed ? 'w-16' : 'w-56'} bg-white border-r border-gray-200 flex flex-col transition-all duration-300 sticky top-0 h-dvh`}>
        <div className="h-14 flex items-center px-4 border-b border-gray-100">
          {!collapsed && <Logo size="sm" />}
          <button onClick={() => setCollapsed(!collapsed)}
            className="ml-auto p-1.5 rounded-lg hover:bg-gray-100 text-gray-400">
            <ChevronLeft size={18} className={`transition-transform ${collapsed ? 'rotate-180' : ''}`} />
          </button>
        </div>
        <nav className="flex-1 p-2 space-y-1">
          {menus.map(m => {
            const active = pathname === m.path;
            const Icon = m.icon;
            return (
              <button key={m.path} onClick={() => navigate(m.path)}
                className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all
                  ${active ? 'bg-primary-100 text-primary-700 shadow-sm' : 'text-gray-500 hover:bg-gray-100'}
                  ${collapsed ? 'justify-center' : ''}`}>
                <Icon size={20} />
                {!collapsed && m.label}
              </button>
            );
          })}
        </nav>
      </aside>

      {/* 主内容 */}
      <main className="flex-1 p-6">
        <Outlet />
      </main>
    </div>
  );
}
