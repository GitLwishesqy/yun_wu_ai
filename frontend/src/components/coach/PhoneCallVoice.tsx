import { useState, useRef, useCallback, useEffect } from 'react';
import { Mic, MicOff, Volume2, VolumeX } from 'lucide-react';

interface Props {
  onTranscribed: (text: string) => void;
  onSpeak: (text: string) => void;
  disabled?: boolean;
}

// 扩展 Window 类型以支持 Web Speech API
declare global {
  interface Window {
    SpeechRecognition: any;
    webkitSpeechRecognition: any;
  }
}

export default function PhoneCallVoice({ onTranscribed, onSpeak, disabled }: Props) {
  const [listening, setListening] = useState(false);
  const [transcript, setTranscript] = useState('');
  const [muted, setMuted] = useState(false);
  const recognitionRef = useRef<any>(null);
  const synthRef = useRef<SpeechSynthesis | null>(null);
  const supportedRef = useRef(false);

  // 检查浏览器是否支持 Web Speech API
  useEffect(() => {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    supportedRef.current = !!SpeechRecognition;
    synthRef.current = window.speechSynthesis;
  }, []);

  // 朗读 AI 回复
  useEffect(() => {
    const id = Date.now();
    const timer = setTimeout(() => {
      onSpeak = onSpeak; // placeholder for external speak trigger
    }, 100);
    return () => clearTimeout(timer);
  }, []);

  /** 朗读文字 */
  const speak = useCallback((text: string) => {
    if (muted || !synthRef.current) return;
    synthRef.current.cancel();
    const utter = new SpeechSynthesisUtterance(text);
    utter.lang = 'en-US';
    utter.rate = 0.9;
    utter.pitch = 1.0;
    // 选一个英文女声
    const voices = synthRef.current.getVoices();
    const enVoice = voices.find(v => v.lang.startsWith('en') && v.name.includes('Female'))
      || voices.find(v => v.lang.startsWith('en'));
    if (enVoice) utter.voice = enVoice;
    synthRef.current.speak(utter);
  }, [muted]);

  /** 开始语音识别 */
  const startListening = useCallback(() => {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) return;

    const recognition = new SpeechRecognition();
    recognition.lang = 'en-US';
    recognition.interimResults = true;
    recognition.continuous = true;
    recognition.maxAlternatives = 1;

    recognition.onresult = (event: any) => {
      let final = '';
      let interim = '';
      for (let i = event.resultIndex; i < event.results.length; i++) {
        const r = event.results[i];
        if (r.isFinal) { final += r[0].transcript; }
        else { interim += r[0].transcript; }
      }
      const text = final || interim;
      setTranscript(text);
    };

    recognition.onerror = (event: any) => {
      console.warn('语音识别错误:', event.error);
      if (event.error === 'no-speech' || event.error === 'aborted') return;
      setListening(false);
    };

    // 检测到静默约1.5秒后自动停止并提交
    let silenceTimer: number;
    recognition.onspeechend = () => {
      silenceTimer = window.setTimeout(() => {
        recognition.stop();
        setListening(false);
        if (transcript.trim()) {
          onTranscribed(transcript.trim());
          setTranscript('');
        }
      }, 1500);
    };

    recognition.onspeechstart = () => {
      clearTimeout(silenceTimer);
    };

    recognition.start();
    recognitionRef.current = recognition;
    setListening(true);
    setTranscript('');
  }, [transcript, onTranscribed]);

  /** 停止并提交 */
  const stopListening = useCallback(() => {
    recognitionRef.current?.stop();
    setListening(false);
    if (transcript.trim()) {
      onTranscribed(transcript.trim());
      setTranscript('');
    }
  }, [transcript, onTranscribed]);

  // 暴露 speak 方法给外部
  useEffect(() => {
    (window as any).__phoneCallSpeak = speak;
    return () => { delete (window as any).__phoneCallSpeak; };
  }, [speak]);

  if (!supportedRef.current) {
    return (
      <div className="flex flex-col items-center gap-3 py-8 text-gray-400">
        <MicOff size={32} />
        <p className="text-sm">当前浏览器不支持语音识别</p>
        <p className="text-xs">请使用 Chrome 或 Edge 浏览器</p>
      </div>
    );
  }

  return (
    <div className="flex flex-col items-center gap-4 py-6">
      {/* 状态指示 */}
      <div className={`w-20 h-20 rounded-full flex items-center justify-center transition-all duration-300 shadow-lg
        ${listening ? 'bg-error animate-pulse shadow-error/30 scale-110' : 'bg-primary-100'}`}>
        {listening
          ? <Mic size={36} className="text-white" />
          : <Mic size={36} className="text-primary-500" />}
      </div>

      <p className="text-sm text-gray-500">
        {listening ? '正在聆听... (说完会自动识别)' : '按下开始说话'}
      </p>

      {/* 实时识别文字 */}
      {transcript && (
        <div className="w-full max-w-md px-4 py-2 bg-gray-50 rounded-lg text-sm text-gray-600 text-center italic">
          "{transcript}"
        </div>
      )}

      {/* 按钮组 */}
      <div className="flex items-center gap-3">
        <button
          onClick={listening ? stopListening : startListening}
          disabled={disabled}
          className={`px-6 py-3 rounded-full font-medium text-sm transition-all shadow-md
            ${listening
              ? 'bg-error text-white hover:bg-red-500'
              : 'bg-primary-500 text-white hover:bg-primary-600 disabled:opacity-40'}`}>
          {listening ? '停止' : '开始说话'}
        </button>

        <button
          onClick={() => setMuted(!muted)}
          className={`p-3 rounded-full transition-colors ${muted ? 'bg-error/10 text-error' : 'bg-gray-100 text-gray-500'}`}>
          {muted ? <VolumeX size={18} /> : <Volume2 size={18} />}
        </button>
      </div>
    </div>
  );
}
