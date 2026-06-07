import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Coffee, Plane, Briefcase, GraduationCap, Users } from 'lucide-react';
import Card from '../components/ui/Card';
import Badge from '../components/ui/Badge';
import Button from '../components/ui/Button';
import Input from '../components/ui/Input';

const categories = [
  { key: 'DAILY_LIFE', label: '日常', icon: Coffee },
  { key: 'TRAVEL', label: '旅行', icon: Plane },
  { key: 'BUSINESS', label: '商务', icon: Briefcase },
  { key: 'ACADEMIC', label: '学术', icon: GraduationCap },
  { key: 'SOCIAL', label: '社交', icon: Users },
];

// 模拟数据
const SCENES = Array.from({ length: 15 }, (_, i) => ({
  id: i + 1,
  name: ['餐厅点餐', '机场值机', '自我介绍', '商务会议', '酒店入住', '商场购物', '看医生', '面试', '问路', '电话预约', '咖啡店', '图书馆', '运动会', '生日派对', '网上购物'][i],
  nameEn: ['Restaurant', 'Airport', 'Self Intro', 'Meeting', 'Hotel', 'Shopping', 'Doctor', 'Interview', 'Directions', 'Phone Call', 'Cafe', 'Library', 'Sports', 'Birthday', 'Online'][i],
  category: categories[i % 5].key,
  difficulty: (i % 5) + 1,
  cefrLevel: ['A1', 'A1', 'A1', 'A2', 'A2', 'A2', 'B1', 'B1', 'B1', 'B2', 'B2', 'B2', 'C1', 'C1', 'C1'][i],
  duration: 15,
  tags: ['daily'],
}));

export default function Scenes() {
  const [search, setSearch] = useState('');
  const [activeCat, setActiveCat] = useState('');
  const navigate = useNavigate();

  const filtered = SCENES.filter(s =>
    (!activeCat || s.category === activeCat) &&
    (!search || s.name.includes(search) || s.nameEn.toLowerCase().includes(search.toLowerCase()))
  );

  return (
    <div className="py-6 space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-800">选择场景</h1>
        <p className="text-gray-500 mt-1">开始你的英语陪练之旅</p>
      </div>

      <Input placeholder="搜索场景..." value={search} onChange={e => setSearch(e.target.value)} />

      <div className="flex gap-2 overflow-x-auto pb-2 -mx-4 px-4">
        {categories.map(c => {
          const Icon = c.icon;
          const active = activeCat === c.key;
          return (
            <button key={c.key} onClick={() => setActiveCat(active ? '' : c.key)}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full text-sm whitespace-nowrap transition-all shrink-0
                ${active ? 'bg-primary-500 text-white shadow-md' : 'bg-white border border-gray-200 text-gray-600 hover:border-primary-300'}`}>
              <Icon size={14} />{c.label}
            </button>
          );
        })}
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {filtered.map(s => (
          <Card key={s.id} hover onClick={() => navigate(`/coach/${s.id}`)} className="p-4">
            <div className="flex items-start justify-between mb-3">
              <div>
                <h3 className="font-semibold text-gray-800">{s.name}</h3>
                <p className="text-xs text-gray-400">{s.nameEn}</p>
              </div>
              <Badge color="green">{s.cefrLevel}</Badge>
            </div>
            <div className="flex items-center gap-2">
              <Badge color="gray">难度 {s.difficulty}/9</Badge>
              <Badge color="sky">{s.duration}分钟</Badge>
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
}
