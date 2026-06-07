import { Settings, Clock, BookOpen, LogOut, ChevronRight } from 'lucide-react';
import Card from '../components/ui/Card';
import Avatar from '../components/ui/Avatar';
import Badge from '../components/ui/Badge';
import { useNavigate } from 'react-router-dom';

export default function Profile() {
  const navigate = useNavigate();
  const menus = [
    { icon: BookOpen, label: '我的词汇本', path: '/vocabulary' },
    { icon: Clock, label: '学习计划', path: '/plans' },
    { icon: Settings, label: '纠错记录', path: '/corrections' },
    { icon: LogOut, label: '退出登录', path: '/', danger: true },
  ];

  return (
    <div className="py-6 space-y-6">
      {/* 用户卡片 */}
      <Card className="p-6 text-center">
        <Avatar name="小明" size="lg" />
        <h2 className="text-xl font-bold text-gray-800 mt-3">小明</h2>
        <div className="flex items-center justify-center gap-2 mt-1">
          <Badge color="green">小学</Badge>
          <Badge color="sky">A1</Badge>
        </div>
        <div className="grid grid-cols-3 gap-4 mt-5 pt-4 border-t border-gray-100">
          {[{ v: '20', l: '会话' }, { v: '300', l: '分钟' }, { v: '5天', l: '连续' }]
            .map(s => (
              <div key={s.l}>
                <p className="text-lg font-bold text-gray-800">{s.v}</p>
                <p className="text-xs text-gray-400">{s.l}</p>
              </div>
            ))}
        </div>
      </Card>

      {/* 菜单 */}
      <Card className="divide-y divide-gray-100">
        {menus.map(m => (
          <button key={m.label} onClick={() => navigate(m.path)}
            className={`w-full flex items-center justify-between px-5 py-4 hover:bg-gray-50 transition-colors
              ${m.danger ? 'text-error' : 'text-gray-700'}`}>
            <div className="flex items-center gap-3">
              <m.icon size={20} />
              <span className="font-medium">{m.label}</span>
            </div>
            <ChevronRight size={18} className="text-gray-300" />
          </button>
        ))}
      </Card>
    </div>
  );
}
