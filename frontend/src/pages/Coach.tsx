import { useState, useEffect, useRef } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ArrowLeft, Send, Settings2, AlertCircle, CheckCircle2, Phone } from 'lucide-react';
import { useCoachSession } from '../hooks/useCoachSession';
import { sceneApi, type SceneData } from '../lib/api';
import MessageBubble from '../components/coach/MessageBubble';
import SceneSelector from '../components/coach/SceneSelector';
import VoiceRecorder from '../components/coach/VoiceRecorder';
import VoiceRecorderV2 from '../components/coach/VoiceRecorderV2';
import InputModeToggle, { type InputMode } from '../components/coach/InputModeToggle';
import Badge from '../components/ui/Badge';
import Button from '../components/ui/Button';

export default function Coach() {
  const { sceneId } = useParams();
  const navigate = useNavigate();
  const { session, messages, isTyping, error, startSession, sendMessage, endSession, setError } = useCoachSession();
  const [input, setInput] = useState('');
  const [scene, setScene] = useState<SceneData | null>(null);
  const [showScenePicker, setShowScenePicker] = useState(false);
  const [sessionEnded, setSessionEnded] = useState(false);
  const [inputMode, setInputMode] = useState<InputMode>('text');
  const bottomRef = useRef<HTMLDivElement>(null);

  // 初始化场景
  useEffect(() => {
    if (sceneId) {
      sceneApi.detail(Number(sceneId)).then(s => {
        setScene(s);
        startSession(s.id);
      }).catch(() => setError('场景加载失败'));
    } else {
      setShowScenePicker(true);
    }
  }, [sceneId]);

  // 滚动到底部
  useEffect(() => { bottomRef.current?.scrollIntoView({ behavior: 'smooth' }); }, [messages, isTyping]);

  // 选择场景
  const handleSelectScene = async (s: SceneData) => {
    setShowScenePicker(false);
    setScene(s);
    setSessionEnded(false);
    await startSession(s.id);
    navigate(`/coach/${s.id}`, { replace: true });
  };

  // 发送消息
  const handleSend = () => {
    if (!input.trim() || isTyping || sessionEnded) return;
    sendMessage(input.trim());
    setInput('');
  };

  // 结束会话
  const handleEnd = async () => {
    await endSession();
    setSessionEnded(true);
    setScene(null);
    setShowScenePicker(true);
  };

  // 语音录制回调
  const handleVoiceRecorded = (blob: Blob) => {
    // TODO: 上传到 OSS → 调 ASR → 填入 input
    console.log('录音完成:', blob.size, 'bytes');
  };

  // 首次进入 — 显示场景选择
  if (showScenePicker) {
    return (
      <div className="min-h-[calc(100dvh-3.5rem)] flex flex-col items-center justify-center p-8">
        <SceneSelector onSelect={handleSelectScene} onClose={() => navigate('/scenes')} />
      </div>
    );
  }

  // 会话已结束 — 显示总结
  if (sessionEnded && scene) {
    return (
      <div className="min-h-[calc(100dvh-3.5rem)] flex flex-col items-center justify-center p-8 text-center space-y-6">
        <div className="w-16 h-16 rounded-full bg-success/20 flex items-center justify-center animate-achieve">
          <CheckCircle2 size={36} className="text-success" />
        </div>
        <div>
          <h2 className="text-xl font-bold text-gray-800">会话完成!</h2>
          <p className="text-gray-500 mt-1">场景: {scene.name}</p>
        </div>
        <div className="grid grid-cols-3 gap-3">
          {[{ v: String(messages.length), l: '消息' }, { v: '--', l: '评分' }, { v: '--', l: '纠错' }]
            .map(s => (<div key={s.l} className="p-3 rounded-xl bg-white border border-gray-200"><p className="text-lg font-bold text-gray-800">{s.v}</p><p className="text-xs text-gray-400">{s.l}</p></div>))}
        </div>
        <div className="flex gap-3">
          <Button variant="secondary" onClick={() => { setSessionEnded(false); setShowScenePicker(true); }}>换个场景</Button>
          <Button onClick={() => navigate(`/report`)}>查看报告</Button>
        </div>
      </div>
    );
  }

  if (!scene) return null;

  return (
    <div className="flex flex-col h-[calc(100dvh-3.5rem)]">
      {/* 顶栏 */}
      <header className="sticky top-0 z-10 bg-white/80 backdrop-blur-md border-b border-gray-200">
        <div className="max-w-3xl mx-auto flex items-center gap-3 px-4 h-14">
          <button onClick={() => navigate('/scenes')} className="p-1.5 rounded-lg hover:bg-gray-100"><ArrowLeft size={20} /></button>
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-2">
              <h2 className="font-semibold text-gray-800 truncate">{scene.name}</h2>
              <Badge color="green">{scene.cefrLevel || 'A1'}</Badge>
            </div>
            <p className="text-[10px] text-gray-400 truncate">{scene.nameEn} · 难度 {scene.difficulty}/9</p>
          </div>
          <button onClick={() => setShowScenePicker(true)} className="p-2 rounded-lg hover:bg-gray-100" title="切换场景">
            <Settings2 size={17} className="text-gray-400" />
          </button>
          <button onClick={handleEnd} className="px-3 py-1.5 text-xs font-medium text-error hover:bg-error/10 rounded-lg transition-colors">结束</button>
        </div>
      </header>

      {/* 错误提示 */}
      {error && (
        <div className="mx-4 mt-2 px-3 py-2 bg-error/10 border border-error/30 rounded-lg text-sm text-error flex items-center gap-2">
          <AlertCircle size={14} /> {error}
          <button onClick={() => setError(null)} className="ml-auto text-xs underline">关闭</button>
        </div>
      )}

      {/* 消息列表 */}
      <div className="flex-1 overflow-y-auto px-4 py-4 space-y-4 max-w-3xl mx-auto w-full">
        {messages.length === 0 && !isTyping && (
          <div className="text-center py-12 text-gray-400">
            <p className="text-lg">开始你的英语对话吧 🎯</p>
            <p className="text-sm mt-1">AI 会扮演 {scene.roles?.[0]?.name || '英语陪练'} 的角色</p>
            {scene.keywords && scene.keywords.length > 0 && (
              <div className="flex flex-wrap justify-center gap-1.5 mt-3">
                {scene.keywords.map(k => <Badge key={k.word} color="sky">{k.word}</Badge>)}
              </div>
            )}
          </div>
        )}
        {messages.map(msg => <MessageBubble key={msg.id} msg={msg} />)}
        {isTyping && (
          <div className="flex justify-start animate-bubble-in">
            <div className="bg-white border border-gray-200 rounded-2xl rounded-tl-md px-4 py-3 shadow-sm">
              <span className="text-sm text-gray-400">AI 正在输入</span>
              <span className="typing-cursor text-primary-500 font-bold" />
            </div>
          </div>
        )}
        <div ref={bottomRef} />
      </div>

      {/* 输入区 */}
      <div className="sticky bottom-0 bg-white/80 backdrop-blur-md border-t border-gray-200 px-4 py-3 safe-area-bottom">
        <div className="flex items-center gap-2 max-w-3xl mx-auto">
          {inputMode === 'voice' ? (
            /* 语音模式: 大录音按钮居中 */
            <div className="flex-1 flex items-center justify-center gap-3">
              <VoiceRecorderV2 onRecorded={handleVoiceRecorded} disabled={isTyping} />
              {isTyping && <span className="text-sm text-gray-400">AI 回复中...</span>}
            </div>
          ) : (
            /* 打字模式: 输入框 + 发送 */
            <>
              <div className="flex-1 relative">
                <input
                  value={input} onChange={e => setInput(e.target.value)}
                  onKeyDown={e => e.key === 'Enter' && !e.shiftKey && handleSend()}
                  placeholder={isTyping ? 'AI 正在回复...' : '输入英语...'}
                  disabled={isTyping}
                  className="w-full px-4 py-2.5 pr-10 rounded-full border border-gray-300 bg-gray-50 text-sm
                    focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-400 focus:bg-white
                    disabled:bg-gray-100 disabled:text-gray-400 transition-all"
                />
              </div>
              <button onClick={handleSend} disabled={!input.trim() || isTyping}
                className="p-2.5 rounded-full bg-primary-500 text-white hover:bg-primary-600 disabled:opacity-40 transition-all shrink-0 shadow-md">
                <Send size={18} />
              </button>
            </>
          )}
          {/* 模式切换 */}
          <InputModeToggle mode={inputMode} onChange={setInputMode} />
        </div>
      </div>

      {/* 场景选择弹窗 */}
      {showScenePicker && <SceneSelector onSelect={handleSelectScene} onClose={() => setShowScenePicker(false)} />}
    </div>
  );
}
