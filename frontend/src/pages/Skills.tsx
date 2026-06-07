import { useState, useEffect } from 'react';
import { Headphones, BookOpen, PenLine, Play, Pause, CheckCircle2, XCircle, Sparkles, ArrowLeft, Send } from 'lucide-react';
import { skillApi } from '../lib/api';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import Badge from '../components/ui/Badge';

const TABS = [
  { key: 'listening', label: '听力', icon: Headphones },
  { key: 'reading', label: '阅读', icon: BookOpen },
  { key: 'writing', label: '写作', icon: PenLine },
] as const;

type Tab = typeof TABS[number]['key'];

// ==================== 听力面板 ====================
function ListeningPanel() {
  const [items, setItems] = useState<any[]>([]);
  const [detail, setDetail] = useState<any>(null);
  const [playing, setPlaying] = useState(false);
  const [answers, setAnswers] = useState<Record<number, string>>({});
  const [result, setResult] = useState<any>(null);

  useEffect(() => { skillApi.listListening({ size: '20' }).then(setItems); }, []);

  const openDetail = async (id: number) => {
    const d = await skillApi.getListening(id); setDetail(d); setResult(null); setAnswers({});
  };

  const submit = async () => {
    if (!detail) return;
    const ans = detail.questions?.map((_: any, i: number) => ({ answer: answers[i] || '' })) || [];
    const r = await skillApi.submitListening(detail.id, ans); setResult(r);
  };

  if (detail) return (
    <div className="space-y-4">
      <button onClick={() => setDetail(null)} className="flex items-center gap-1 text-sm text-primary-600"><ArrowLeft size={16} />返回</button>
      <h2 className="text-xl font-bold text-gray-800">{detail.title}</h2>
      <Card className="p-4 flex items-center gap-4">
        <button onClick={() => setPlaying(!playing)}
          className="w-12 h-12 rounded-full bg-primary-500 text-white flex items-center justify-center hover:bg-primary-600 transition-colors shadow-md">
          {playing ? <Pause size={22} /> : <Play size={22} className="ml-0.5" />}
        </button>
        <div className="flex-1 h-2 bg-gray-200 rounded-full overflow-hidden">
          <div className={`h-full bg-primary-500 rounded-full transition-all ${playing ? 'animate-pulse' : ''}`} style={{ width: playing ? '60%' : '0%' }} />
        </div>
        <span className="text-sm text-gray-500">{detail.durationSeconds || 0}s</span>
      </Card>
      {!result ? (
        <>
          {detail.transcript && <p className="text-sm text-gray-500 bg-gray-50 p-3 rounded-lg whitespace-pre-wrap">{detail.transcript}</p>}
          {detail.questions?.map((q: any, i: number) => (
            <Card key={i} className="p-4 space-y-2">
              <p className="font-medium text-gray-800">{i + 1}. {q.question}</p>
              {q.options?.map((opt: string) => (
                <label key={opt} className={`flex items-center gap-2 px-3 py-2 rounded-lg cursor-pointer transition-colors
                  ${answers[i] === opt ? 'bg-primary-100 border border-primary-300' : 'hover:bg-gray-50 border border-transparent'}`}>
                  <input type="radio" name={`q${i}`} value={opt} checked={answers[i] === opt}
                    onChange={() => setAnswers(a => ({ ...a, [i]: opt }))} className="hidden" />
                  {opt}
                </label>
              ))}
            </Card>
          ))}
          <Button className="w-full" onClick={submit} icon={<Send size={16} />}>提交答案</Button>
        </>
      ) : (
        <div className="space-y-4 animate-bubble-in">
          <Card className="p-4 text-center">
            <p className="text-3xl font-bold text-primary-600">{result.score}分</p>
            <p className="text-gray-500">正确 {result.correctCount}/{result.totalQuestions}</p>
          </Card>
          {result.results?.map((r: any, i: number) => (
            <div key={i} className={`flex items-center gap-2 p-3 rounded-lg ${r.correct ? 'bg-success/10' : 'bg-error/10'}`}>
              {r.correct ? <CheckCircle2 size={18} className="text-success" /> : <XCircle size={18} className="text-error" />}
              <span className="text-sm text-gray-700">第{i + 1}题 {r.correct ? '正确' : `正确答案: ${r.correctAnswer}`}</span>
            </div>
          ))}
          <Button variant="secondary" className="w-full" onClick={() => setDetail(null)}>返回列表</Button>
        </div>
      )}
    </div>
  );

  return (
    <div className="space-y-3">
      {items.map(item => (
        <Card key={item.id} hover onClick={() => openDetail(item.id)} className="p-4 flex items-center gap-4">
          <div className="w-10 h-10 rounded-full bg-primary-100 flex items-center justify-center shrink-0">
            <Headphones size={18} className="text-primary-600" />
          </div>
          <div className="flex-1 min-w-0">
            <p className="font-medium text-gray-800 truncate">{item.title}</p>
            <p className="text-xs text-gray-400">{item.durationSeconds || 0}秒 · {item.cefrLevel || 'A1'}</p>
          </div>
          <Badge color="green">难度 {item.difficulty}</Badge>
        </Card>
      ))}
    </div>
  );
}

// ==================== 阅读面板 ====================
function ReadingPanel() {
  const [items, setItems] = useState<any[]>([]);
  const [detail, setDetail] = useState<any>(null);
  const [answers, setAnswers] = useState<Record<number, string>>({});
  const [result, setResult] = useState<any>(null);

  useEffect(() => { skillApi.listReading({ size: '20' }).then(setItems); }, []);

  const openDetail = async (id: number) => {
    const d = await skillApi.getReading(id); setDetail(d); setResult(null); setAnswers({});
  };

  const submit = async () => {
    if (!detail) return;
    const ans = detail.questions?.map((_: any, i: number) => ({ answer: answers[i] || '' })) || [];
    const r = await skillApi.submitReading(detail.id, ans); setResult(r);
  };

  if (detail) return (
    <div className="space-y-4">
      <button onClick={() => setDetail(null)} className="flex items-center gap-1 text-sm text-primary-600"><ArrowLeft size={16} />返回</button>
      <h2 className="text-xl font-bold text-gray-800">{detail.title}</h2>
      <Badge color="gray">{detail.wordCount || 0}词 · {detail.cefrLevel || 'A1'}</Badge>
      <div className="prose prose-sm max-w-none text-gray-700 bg-white p-5 rounded-xl border border-gray-200 leading-relaxed whitespace-pre-wrap">
        {detail.content}
      </div>
      {!result ? (
        <>
          {detail.questions?.map((q: any, i: number) => (
            <Card key={i} className="p-4 space-y-2">
              <p className="font-medium text-gray-800">{i + 1}. {q.question}</p>
              {q.options?.map((opt: string) => (
                <label key={opt} className={`flex items-center gap-2 px-3 py-2 rounded-lg cursor-pointer transition-colors
                  ${answers[i] === opt ? 'bg-primary-100 border border-primary-300' : 'hover:bg-gray-50 border border-transparent'}`}>
                  <input type="radio" name={`rq${i}`} value={opt} checked={answers[i] === opt}
                    onChange={() => setAnswers(a => ({ ...a, [i]: opt }))} className="hidden" />
                  {opt}
                </label>
              ))}
            </Card>
          ))}
          <Button className="w-full" onClick={submit} icon={<Send size={16} />}>提交答案</Button>
        </>
      ) : (
        <div className="space-y-4 animate-bubble-in">
          <Card className="p-4 text-center">
            <p className="text-3xl font-bold text-primary-600">{result.score}分</p>
            <p className="text-gray-500">正确 {result.correctCount}/{result.totalQuestions}</p>
          </Card>
          {result.results?.map((r: any, i: number) => (
            <div key={i} className={`flex items-center gap-2 p-3 rounded-lg ${r.correct ? 'bg-success/10' : 'bg-error/10'}`}>
              {r.correct ? <CheckCircle2 size={18} className="text-success" /> : <XCircle size={18} className="text-error" />}
              <span className="text-sm text-gray-700">第{i + 1}题 {r.correct ? '正确' : `正确答案: ${r.correctAnswer}`}</span>
            </div>
          ))}
          <Button variant="secondary" className="w-full" onClick={() => setDetail(null)}>返回列表</Button>
        </div>
      )}
    </div>
  );

  return (
    <div className="space-y-3">
      {items.map(item => (
        <Card key={item.id} hover onClick={() => openDetail(item.id)} className="p-4 flex items-center gap-4">
          <div className="w-10 h-10 rounded-full bg-sky-100 flex items-center justify-center shrink-0">
            <BookOpen size={18} className="text-sky-600" />
          </div>
          <div className="flex-1 min-w-0">
            <p className="font-medium text-gray-800 truncate">{item.title}</p>
            <p className="text-xs text-gray-400">{item.wordCount || 0}词 · {item.cefrLevel || 'A1'}</p>
          </div>
          <Badge color="sky">难度 {item.difficulty}</Badge>
        </Card>
      ))}
    </div>
  );
}

// ==================== 写作面板 ====================
function WritingPanel() {
  const [items, setItems] = useState<any[]>([]);
  const [detail, setDetail] = useState<any>(null);
  const [essay, setEssay] = useState('');
  const [result, setResult] = useState<any>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => { skillApi.listWriting({ size: '20' }).then(setItems); }, []);

  const openDetail = async (id: number) => {
    const d = await skillApi.getWriting(id); setDetail(d); setResult(null); setEssay('');
  };

  const submit = async () => {
    if (!essay.trim()) return;
    setSubmitting(true);
    const r = await skillApi.submitWriting(detail.id, essay);
    setResult(r); setSubmitting(false);
  };

  if (detail) return (
    <div className="space-y-4">
      <button onClick={() => setDetail(null)} className="flex items-center gap-1 text-sm text-primary-600"><ArrowLeft size={16} />返回</button>
      <h2 className="text-xl font-bold text-gray-800">{detail.title}</h2>
      <div className="flex gap-2">
        {detail.wordLimitMin && <Badge color="sky">{detail.wordLimitMin}-{detail.wordLimitMax}词</Badge>}
        {detail.timeLimitMinutes && <Badge color="warm">{detail.timeLimitMinutes}分钟</Badge>}
        <Badge color="green">{detail.cefrLevel || 'A1'}</Badge>
      </div>
      <Card className="p-4 bg-primary-50 border-primary-200">
        <p className="text-gray-700 leading-relaxed">{detail.prompt}</p>
        {detail.promptEn && <p className="text-sm text-gray-400 mt-1">{detail.promptEn}</p>}
      </Card>
      {!result ? (
        <>
          <textarea value={essay} onChange={e => setEssay(e.target.value)}
            placeholder="开始写作..."
            className="w-full h-48 p-4 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-primary-300 resize-none text-sm leading-relaxed" />
          <div className="flex items-center justify-between">
            <span className="text-sm text-gray-400">{essay.split(/\s+/).filter(Boolean).length} 词</span>
            <Button onClick={submit} loading={submitting} icon={<Sparkles size={16} />}>AI 批改</Button>
          </div>
        </>
      ) : (
        <div className="space-y-4 animate-bubble-in">
          <Card className="p-4 text-center">
            <p className="text-3xl font-bold text-primary-600">{result.score}分</p>
            <div className="grid grid-cols-4 gap-2 mt-3">
              {Object.entries(result.dimensionScores || {}).map(([k, v]) => (
                <div key={k} className="text-center"><p className="font-bold text-gray-800">{String(v)}</p><p className="text-[10px] text-gray-400">{k}</p></div>
              ))}
            </div>
          </Card>
          <Card className="p-4">
            <h4 className="font-semibold text-gray-800 mb-2">AI 评语</h4>
            <p className="text-sm text-gray-600 leading-relaxed">{result.feedbackSummary}</p>
          </Card>
          {result.polishedVersion && (
            <Card className="p-4 border-l-4 border-l-primary-500">
              <h4 className="font-semibold text-gray-800 mb-2 flex items-center gap-1"><Sparkles size={14} className="text-primary-500" />润色版本</h4>
              <p className="text-sm text-gray-600 leading-relaxed">{result.polishedVersion}</p>
            </Card>
          )}
          <div className="flex gap-3">
            <Button variant="secondary" className="flex-1" onClick={() => setDetail(null)}>返回列表</Button>
            <Button className="flex-1" onClick={() => { setResult(null); setEssay(''); }}>再写一篇</Button>
          </div>
        </div>
      )}
    </div>
  );

  return (
    <div className="space-y-3">
      {items.map(item => (
        <Card key={item.id} hover onClick={() => openDetail(item.id)} className="p-4 flex items-center gap-4">
          <div className="w-10 h-10 rounded-full bg-blossom flex items-center justify-center shrink-0">
            <PenLine size={18} className="text-pink-500" />
          </div>
          <div className="flex-1 min-w-0">
            <p className="font-medium text-gray-800 truncate">{item.title}</p>
            <p className="text-xs text-gray-400">{item.wordLimitMin}-{item.wordLimitMax}词 · {item.cefrLevel || 'A1'}</p>
          </div>
          <Badge color="warm">难度 {item.difficulty}</Badge>
        </Card>
      ))}
    </div>
  );
}

// ==================== 主页面 ====================
export default function Skills() {
  const [tab, setTab] = useState<Tab>('listening');

  return (
    <div className="py-6 space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-800">技能训练</h1>
        <p className="text-gray-500 mt-1">听说读写全面提升</p>
      </div>

      <div className="flex gap-1 bg-white rounded-xl border border-gray-200 p-1">
        {TABS.map(t => {
          const active = tab === t.key;
          const Icon = t.icon;
          return (
            <button key={t.key} onClick={() => setTab(t.key)}
              className={`flex-1 flex items-center justify-center gap-2 py-2.5 rounded-lg text-sm font-medium transition-all
                ${active ? 'bg-primary-500 text-white shadow-md' : 'text-gray-500 hover:bg-gray-50'}`}>
              <Icon size={18} />{t.label}
            </button>
          );
        })}
      </div>

      {tab === 'listening' && <ListeningPanel />}
      {tab === 'reading' && <ReadingPanel />}
      {tab === 'writing' && <WritingPanel />}
    </div>
  );
}
