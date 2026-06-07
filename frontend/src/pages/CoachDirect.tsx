import { useState, useEffect, useRef } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ArrowLeft, Send, AlertCircle, CheckCircle2 } from 'lucide-react';
import { useCoachSession } from '../hooks/useCoachSession';
import { sceneApi, type SceneData } from '../lib/api';
import MessageBubble from '../components/coach/MessageBubble';
import Badge from '../components/ui/Badge';
import Button from '../components/ui/Button';
import InputModeToggle, { type InputMode } from '../components/coach/InputModeToggle';
import PhoneCallVoice from '../components/coach/PhoneCallVoice';

export default function CoachDirect() {
  const { sceneId } = useParams();
  const navigate = useNavigate();
  const { session, messages, isTyping, error, startSession, sendMessage, endSession, setError } = useCoachSession();
  const [input, setInput] = useState('');
  const [scene, setScene] = useState<SceneData | null>(null);
  const [inputMode, setInputMode] = useState<InputMode>('text');
  const [loading, setLoading] = useState(true);
  const [ended, setEnded] = useState(false);
  const bottomRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!sceneId) { navigate('/scenes'); return; }
    setLoading(true); setError(null);
    sceneApi.detail(Number(sceneId)).then(s => { setScene(s); return startSession(s.id); })
      .then(session => { if (!session) { setError('创建会话失败'); navigate('/scenes'); } })
      .catch(() => { setError('加载失败'); navigate('/scenes'); })
      .finally(() => setLoading(false));
  }, [sceneId]);

  useEffect(() => { bottomRef.current?.scrollIntoView({ behavior: 'smooth' }); }, [messages, isTyping]);

  const lastAiMsg = messages.filter(m => m.role === 'AI').pop();
  useEffect(() => { if (lastAiMsg && inputMode === 'voice') { const s = (window as any).__phoneCallSpeak; if (s) s(lastAiMsg.content); } }, [lastAiMsg?.id]);

  const handleSend = () => { if (!input.trim() || isTyping || ended) return; sendMessage(input.trim()); setInput(''); };
  const handleTranscribed = (text: string) => { if (!text.trim() || isTyping || ended) return; sendMessage(text.trim()); };
  const handleEnd = async () => { await endSession(); setEnded(true); };

  if (loading) return <div className="min-h-[calc(100dvh-3.5rem)] flex items-center justify-center"><div className="w-12 h-12 rounded-full border-4 border-primary-200 border-t-primary-500 animate-spin mx-auto" /></div>;
  if (ended && scene) return (
    <div className="min-h-[calc(100dvh-3.5rem)] flex flex-col items-center justify-center p-8 text-center space-y-6">
      <CheckCircle2 size={48} className="text-success" /><h2 className="text-xl font-bold text-gray-800">会话完成</h2>
      <div className="flex gap-3"><Button variant="secondary" onClick={() => navigate('/scenes')}>换场景</Button><Button onClick={() => navigate('/report')}>查看报告</Button></div>
    </div>
  );
  if (!scene) return null;

  return (
    <div className="flex flex-col h-[calc(100dvh-3.5rem)]">
      <header className="sticky top-0 z-10 bg-white/80 backdrop-blur-md border-b border-gray-200"><div className="max-w-3xl mx-auto flex items-center gap-3 px-4 h-14"><button onClick={() => navigate('/scenes')} className="p-1.5 rounded-lg hover:bg-gray-100"><ArrowLeft size={20} /></button><div className="flex-1 min-w-0"><div className="flex items-center gap-2"><h2 className="font-semibold text-gray-800 truncate">{scene.name}</h2><Badge color="green">{scene.cefrLevel||'A1'}</Badge></div></div><button onClick={handleEnd} className="px-3 py-1.5 text-xs font-medium text-error hover:bg-error/10 rounded-lg">结束</button></div></header>
      {error && <div className="mx-4 mt-2 px-3 py-2 bg-error/10 rounded-lg text-sm text-error flex items-center gap-2"><AlertCircle size={14} />{error}</div>}
      <div className="flex-1 overflow-y-auto px-4 py-4 space-y-4 max-w-3xl mx-auto w-full">
        {messages.map(msg => <MessageBubble key={msg.id} msg={msg} />)}
        {isTyping && <div className="flex justify-start animate-bubble-in"><div className="bg-white border rounded-2xl rounded-tl-md px-4 py-3"><span className="text-sm text-gray-400">AI 回复中...</span></div></div>}
        <div ref={bottomRef} />
      </div>
      <div className="sticky bottom-0 bg-white/80 backdrop-blur-md border-t border-gray-200 px-4 py-3">
        <div className="flex items-center gap-2 max-w-3xl mx-auto">
          {inputMode === 'voice' ? <div className="w-full"><PhoneCallVoice onTranscribed={handleTranscribed} onSpeak={()=>{}} disabled={isTyping} /></div> :
            <><div className="flex-1 relative"><input value={input} onChange={e=>setInput(e.target.value)} onKeyDown={e=>e.key==='Enter'&&!e.shiftKey&&handleSend()} placeholder={isTyping?'AI 回复中...':'输入英语...'} disabled={isTyping} className="w-full px-4 py-2.5 rounded-full border border-gray-300 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-primary-300 disabled:bg-gray-100 transition-all" /></div>
            <button onClick={handleSend} disabled={!input.trim()||isTyping} className="p-2.5 rounded-full bg-primary-500 text-white hover:bg-primary-600 disabled:opacity-40 shadow-md shrink-0"><Send size={18} /></button></>}
          <InputModeToggle mode={inputMode} onChange={setInputMode} />
        </div>
      </div>
    </div>
  );
}
