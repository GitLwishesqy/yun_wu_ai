import { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { LayoutDashboard, Users, BookOpenCheck, ShieldCheck, Settings, FileText, TrendingUp, AlertTriangle, Search, Edit3, Trash2, Eye, CheckCircle2, XCircle } from 'lucide-react';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import Badge from '../components/ui/Badge';
import Input from '../components/ui/Input';

const MENUS = [
  { key: '', label: '仪表盘', icon: LayoutDashboard },
  { key: 'users', label: '用户管理', icon: Users },
  { key: 'scenes', label: '场景管理', icon: BookOpenCheck },
  { key: 'review', label: '内容审核', icon: ShieldCheck },
  { key: 'settings', label: '系统配置', icon: Settings },
  { key: 'audit', label: '审计日志', icon: FileText },
];

// ==================== 仪表盘 ====================
function Dashboard() {
  return (
    <div className="space-y-6">
      <h2 className="text-xl font-bold text-gray-800">仪表盘</h2>
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
        {[
          { v: '12,500', l: '总用户', c: 'primary' },
          { v: '4,500', l: '本周活跃', c: 'sky' },
          { v: '32,000', l: '本周会话', c: 'warm' },
          { v: '¥12,500', l: 'AI 费用', c: 'success' },
        ].map(s => (
          <Card key={s.l} className="p-4 text-center">
            <p className="text-2xl font-bold text-gray-800">{s.v}</p>
            <p className="text-sm text-gray-500">{s.l}</p>
          </Card>
        ))}
      </div>
      <Card className="p-4">
        <h3 className="font-semibold text-gray-800 mb-3 flex items-center gap-2"><TrendingUp size={18} />最近7天活跃</h3>
        <div className="flex items-end gap-1 h-32">
          {[65,70,72,68,75,80,78].map((v, i) => (
            <div key={i} className="flex-1 flex flex-col items-center gap-1">
              <div className="w-full bg-primary-400 hover:bg-primary-500 rounded-t-sm transition-all" style={{ height: `${v}%` }} />
              <span className="text-[10px] text-gray-400">{['一','二','三','四','五','六','日'][i]}</span>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
}

// ==================== 用户管理 ====================
function UserManagement() {
  const users = [
    { id: 1001, phone: '138****0001', nickname: '小明', role: 'STUDENT', gradeLevel: 'ELEMENTARY', status: 'ACTIVE' },
    { id: 1002, phone: '138****0006', nickname: '张老师', role: 'TEACHER', gradeLevel: null, status: 'ACTIVE' },
    { id: 1003, phone: '138****0005', nickname: '王妈妈', role: 'PARENT', gradeLevel: null, status: 'ACTIVE' },
  ];

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-bold text-gray-800">用户管理</h2>
        <Input placeholder="搜索用户..." className="max-w-xs" />
      </div>
      <Card className="overflow-hidden">
        <table className="w-full text-sm">
          <thead><tr className="bg-gray-50 text-left text-gray-500"><th className="p-3">ID</th><th className="p-3">手机号</th><th className="p-3">昵称</th><th className="p-3">角色</th><th className="p-3">学段</th><th className="p-3">状态</th><th className="p-3">操作</th></tr></thead>
          <tbody>
            {users.map(u => (
              <tr key={u.id} className="border-t border-gray-100 hover:bg-gray-50">
                <td className="p-3 font-mono">{u.id}</td><td className="p-3">{u.phone}</td><td className="p-3">{u.nickname}</td>
                <td className="p-3"><Badge color={u.role==='STUDENT'?'green':u.role==='TEACHER'?'sky':'warm'}>{u.role}</Badge></td>
                <td className="p-3">{u.gradeLevel || '-'}</td>
                <td className="p-3"><Badge color="green">{u.status}</Badge></td>
                <td className="p-3 flex gap-1">
                  <button className="p-1 rounded hover:bg-gray-200"><Eye size={14} /></button>
                  <button className="p-1 rounded hover:bg-gray-200"><Edit3 size={14} /></button>
                  <button className="p-1 rounded hover:bg-red-100 text-error"><Trash2 size={14} /></button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </Card>
    </div>
  );
}

// ==================== 场景管理 ====================
function SceneManagement() {
  const [scenes] = useState([
    { id: 1, name: '餐厅点餐', category: 'DAILY_LIFE', difficulty: 1, isPublished: true, version: 1 },
    { id: 2, name: '机场值机', category: 'TRAVEL', difficulty: 2, isPublished: true, version: 1 },
    { id: 3, name: '商务会议', category: 'BUSINESS', difficulty: 3, isPublished: false, version: 2 },
  ]);
  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-bold text-gray-800">场景管理</h2>
        <Button size="sm">创建场景</Button>
      </div>
      <Card className="overflow-hidden">
        <table className="w-full text-sm">
          <thead><tr className="bg-gray-50 text-left text-gray-500"><th className="p-3">ID</th><th className="p-3">名称</th><th className="p-3">分类</th><th className="p-3">难度</th><th className="p-3">版本</th><th className="p-3">状态</th><th className="p-3">操作</th></tr></thead>
          <tbody>
            {scenes.map(s => (
              <tr key={s.id} className="border-t border-gray-100 hover:bg-gray-50">
                <td className="p-3">{s.id}</td><td className="p-3 font-medium">{s.name}</td>
                <td className="p-3"><Badge color="sky">{s.category}</Badge></td><td className="p-3">{s.difficulty}/9</td>
                <td className="p-3">v{s.version}</td>
                <td className="p-3"><Badge color={s.isPublished ? 'green' : 'gray'}>{s.isPublished ? '已发布' : '草稿'}</Badge></td>
                <td className="p-3 flex gap-1">
                  <button className="p-1 rounded hover:bg-gray-200"><Edit3 size={14} /></button>
                  <button className="p-1 rounded hover:bg-red-100 text-error"><Trash2 size={14} /></button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </Card>
    </div>
  );
}

// ==================== 内容审核 ====================
function ContentReview() {
  const [items] = useState([
    { id: 1, content: '用户输入包含敏感词...', reviewResult: 'BLOCK', riskLevel: 'HIGH', createdAt: '2026-06-06 10:30' },
    { id: 2, content: 'AI 输出需要人工审核...', reviewResult: 'REVIEW', riskLevel: 'MEDIUM', createdAt: '2026-06-06 11:00' },
  ]);
  return (
    <div className="space-y-4">
      <h2 className="text-xl font-bold text-gray-800">内容审核</h2>
      <div className="grid grid-cols-3 gap-3">
        {[{ v: '50,000', l: '已审核', c: 'text-primary-500' }, { v: '120', l: '已屏蔽', c: 'text-error' }, { v: '15', l: '待复审', c: 'text-warm-500' }].map(s => (
          <Card key={s.l} className="p-4 text-center"><p className={`text-2xl font-bold ${s.c}`}>{s.v}</p><p className="text-sm text-gray-500">{s.l}</p></Card>
        ))}
      </div>
      <div className="space-y-2">
        {items.map(item => (
          <Card key={item.id} className="p-4 flex items-center justify-between">
            <div className="flex-1 min-w-0">
              <p className="text-sm text-gray-700 truncate">{item.content}</p>
              <p className="text-xs text-gray-400 mt-1">{item.createdAt}</p>
            </div>
            <div className="flex items-center gap-2 ml-4">
              <Badge color={item.riskLevel === 'HIGH' ? 'red' : 'warm'}>{item.riskLevel}</Badge>
              <Badge color={item.reviewResult === 'BLOCK' ? 'red' : 'warm'}>{item.reviewResult}</Badge>
              <button className="p-1.5 rounded-lg hover:bg-success/10 text-success"><CheckCircle2 size={16} /></button>
              <button className="p-1.5 rounded-lg hover:bg-error/10 text-error"><XCircle size={16} /></button>
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
}

// ==================== 系统配置 ====================
function SystemSettings() {
  return (
    <div className="space-y-6">
      <h2 className="text-xl font-bold text-gray-800">系统配置</h2>
      {[
        { key: 'max_session_rounds', val: '50', desc: '单次会话最大轮次' },
        { key: 'max_daily_sessions', val: '20', desc: '每日最大会话数' },
        { key: 'max_input_length', val: '2000', desc: '用户输入最大字符数' },
        { key: 'content_review_enabled', val: 'true', desc: '内容审核开关' },
        { key: 'points_per_session', val: '10', desc: '每次陪练积分' },
      ].map(c => (
        <Card key={c.key} className="p-4 flex items-center justify-between">
          <div>
            <p className="font-medium text-gray-800">{c.key}</p>
            <p className="text-xs text-gray-400">{c.desc}</p>
          </div>
          <div className="flex items-center gap-2">
            <input defaultValue={c.val} className="w-24 px-3 py-1.5 rounded-lg border border-gray-300 text-sm text-right focus:outline-none focus:ring-2 focus:ring-primary-300" />
            <Button size="sm" variant="secondary">保存</Button>
          </div>
        </Card>
      ))}
    </div>
  );
}

// ==================== 审计日志 ====================
function AuditLog() {
  const logs = [
    { id: 1, user: 'admin', action: 'LOGIN', resource: 'USER', result: 'SUCCESS', ip: '192.168.1.1', time: '2026-06-06 10:00' },
    { id: 2, user: '张老师', action: 'CREATE_SCENE', resource: 'SCENE', result: 'SUCCESS', ip: '192.168.1.2', time: '2026-06-06 10:15' },
    { id: 3, user: '小明', action: 'COMPLETE_SESSION', resource: 'SESSION', result: 'SUCCESS', ip: '192.168.1.3', time: '2026-06-06 10:38' },
  ];
  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-bold text-gray-800">审计日志</h2>
        <Input placeholder="搜索..." className="max-w-xs" />
      </div>
      <Card className="overflow-hidden">
        <table className="w-full text-sm">
          <thead><tr className="bg-gray-50 text-left text-gray-500"><th className="p-3">时间</th><th className="p-3">用户</th><th className="p-3">操作</th><th className="p-3">资源</th><th className="p-3">IP</th><th className="p-3">结果</th></tr></thead>
          <tbody>
            {logs.map(l => (
              <tr key={l.id} className="border-t border-gray-100 hover:bg-gray-50">
                <td className="p-3 text-xs">{l.time}</td><td className="p-3">{l.user}</td>
                <td className="p-3">{l.action}</td><td className="p-3"><Badge color="gray">{l.resource}</Badge></td>
                <td className="p-3 font-mono text-xs">{l.ip}</td>
                <td className="p-3"><Badge color="green">{l.result}</Badge></td>
              </tr>
            ))}
          </tbody>
        </table>
      </Card>
    </div>
  );
}

// ==================== 主组件 ====================
export default function Admin() {
  const { pathname } = useLocation();
  const navigate = useNavigate();
  const current = pathname.replace('/admin/', '').replace('/admin', '');

  return (
    <div className="py-6 space-y-6">
      <div className="flex items-center gap-3 overflow-x-auto pb-2">
        {MENUS.map(m => (
          <button key={m.key} onClick={() => navigate(`/admin/${m.key}`)}
            className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium whitespace-nowrap transition-all
              ${current === m.key ? 'bg-primary-500 text-white shadow-md' : 'text-gray-500 hover:bg-gray-100'}`}>
            <m.icon size={16} />{m.label}
          </button>
        ))}
      </div>

      {current === '' && <Dashboard />}
      {current === 'users' && <UserManagement />}
      {current === 'scenes' && <SceneManagement />}
      {current === 'review' && <ContentReview />}
      {current === 'settings' && <SystemSettings />}
      {current === 'audit' && <AuditLog />}
    </div>
  );
}
