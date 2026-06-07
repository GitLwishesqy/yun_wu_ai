import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Coffee, Plane, Briefcase, GraduationCap, Users } from 'lucide-react';
import { sceneApi, type SceneData } from '../lib/api';
import Card from '../components/ui/Card';
import Badge from '../components/ui/Badge';
import Input from '../components/ui/Input';

const categories = [{key:'',label:'全部'},{key:'DAILY_LIFE',label:'日常',icon:Coffee},{key:'TRAVEL',label:'旅行',icon:Plane},{key:'BUSINESS',label:'商务',icon:Briefcase},{key:'ACADEMIC',label:'学术',icon:GraduationCap},{key:'EXAM',label:'考试',icon:Users},{key:'SOCIAL',label:'社交',icon:Users}];

export default function ScenesReal() {
  const [scenes, setScenes] = useState<SceneData[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [activeCat, setActiveCat] = useState('');
  const navigate = useNavigate();

  useEffect(() => { sceneApi.list({size:'50'}).then(d=>{setScenes(d.items);setLoading(false)}).catch(()=>setLoading(false)); },[]);

  const filtered = scenes.filter(s=>(!activeCat||s.category===activeCat)&&(!search||s.name.includes(search)||(s.nameEn||'').toLowerCase().includes(search.toLowerCase())));

  return (
    <div className="py-6 space-y-6">
      <div><h1 className="text-2xl font-bold text-gray-800">选择场景</h1><p className="text-gray-500 mt-1">共 {scenes.length} 个场景</p></div>
      <Input placeholder="搜索场景..." value={search} onChange={e=>setSearch(e.target.value)} />
      <div className="flex gap-2 overflow-x-auto pb-2">
        {categories.map(c=>{const active=activeCat===c.key;return <button key={c.key} onClick={()=>setActiveCat(active?'':c.key)} className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full text-sm whitespace-nowrap transition-all shrink-0 ${active?'bg-primary-500 text-white shadow-md':'bg-white border border-gray-200 text-gray-600 hover:border-primary-300'}`}>{c.icon&&<c.icon size={14} />}{c.label}</button>;})}
      </div>
      {loading ? <div className="text-center py-12"><div className="w-10 h-10 rounded-full border-4 border-primary-200 border-t-primary-500 animate-spin mx-auto" /></div> :
        filtered.length===0 ? <p className="text-center text-gray-400 py-12">暂无匹配场景</p> :
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {filtered.map(s=><Card key={s.id} hover onClick={()=>navigate(`/coach/${s.id}`)} className="p-4"><div className="flex items-start justify-between mb-3"><div><h3 className="font-semibold text-gray-800">{s.name}</h3><p className="text-xs text-gray-400">{s.nameEn}</p></div><Badge color="green">{s.cefrLevel||'A1'}</Badge></div><div className="flex items-center gap-2"><Badge color="gray">难度 {s.difficulty}/9</Badge><Badge color="sky">{s.estimatedDuration||15}分钟</Badge></div></Card>)}
        </div>}
    </div>
  );
}
