import { useState, useRef, useCallback, useEffect } from 'react';
import { Mic, MicOff, Volume2, VolumeX } from 'lucide-react';

declare global { interface Window { SpeechRecognition: any; webkitSpeechRecognition: any; } }

interface Props { onTranscribed: (text: string) => void; onSpeak: (text: string) => void; disabled?: boolean; }

export default function PhoneCallVoice({ onTranscribed, onSpeak, disabled }: Props) {
  const [listening, setListening] = useState(false);
  const [transcript, setTranscript] = useState('');
  const [muted, setMuted] = useState(false);
  const recognitionRef = useRef<any>(null);
  const synthRef = useRef<SpeechSynthesis | null>(null);

  useEffect(() => { synthRef.current = window.speechSynthesis; }, []);

  const speak = useCallback((text: string) => {
    if (muted || !synthRef.current) return;
    synthRef.current.cancel();
    const utter = new SpeechSynthesisUtterance(text);
    utter.lang = 'en-US'; utter.rate = 0.9;
    const voices = synthRef.current.getVoices();
    const enVoice = voices.find(v => v.lang.startsWith('en') && v.name.includes('Female')) || voices.find(v => v.lang.startsWith('en'));
    if (enVoice) utter.voice = enVoice;
    synthRef.current.speak(utter);
  }, [muted]);

  useEffect(() => { (window as any).__phoneCallSpeak = speak; return () => { delete (window as any).__phoneCallSpeak; }; }, [speak]);

  const start = useCallback(() => {
    const SR = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SR) return;
    const rec = new SR();
    rec.lang = 'en-US'; rec.interimResults = true; rec.continuous = true;
    let finalText = '';
    rec.onresult = (e: any) => {
      for (let i = e.resultIndex; i < e.results.length; i++) {
        if (e.results[i].isFinal) finalText += e.results[i][0].transcript + ' ';
        else setTranscript(finalText + e.results[i][0].transcript);
      }
      if (finalText) setTranscript(finalText);
    };
    rec.onerror = (e: any) => { if (e.error === 'no-speech' || e.error === 'aborted') return; setListening(false); };
    let silenceTimer: number;
    rec.onspeechend = () => { silenceTimer = window.setTimeout(() => { rec.stop(); setListening(false); if (finalText.trim() || transcript.trim()) { onTranscribed((finalText || transcript).trim()); setTranscript(''); } }, 1500); };
    rec.onspeechstart = () => clearTimeout(silenceTimer);
    rec.start(); recognitionRef.current = rec; setListening(true); setTranscript('');
  }, [transcript, onTranscribed]);

  const stop = useCallback(() => {
    recognitionRef.current?.stop(); setListening(false);
    if (transcript.trim()) { onTranscribed(transcript.trim()); setTranscript(''); }
  }, [transcript, onTranscribed]);

  return (
    <div className="flex flex-col items-center gap-4 py-6">
      <div className={`w-20 h-20 rounded-full flex items-center justify-center transition-all duration-300 shadow-lg ${listening ? 'bg-error animate-pulse shadow-error/30 scale-110' : 'bg-primary-100'}`}>
        {listening ? <Mic size={36} className="text-white" /> : <Mic size={36} className="text-primary-500" />}
      </div>
      <p className="text-sm text-gray-500">{listening ? '正在聆听... (说完会自动识别)' : '按下开始说话'}</p>
      {transcript && <div className="w-full max-w-md px-4 py-2 bg-gray-50 rounded-lg text-sm text-gray-600 text-center italic">"{transcript}"</div>}
      <div className="flex items-center gap-3">
        <button onClick={listening ? stop : start} disabled={disabled}
          className={`px-6 py-3 rounded-full font-medium text-sm transition-all shadow-md ${listening ? 'bg-error text-white hover:bg-red-500' : 'bg-primary-500 text-white hover:bg-primary-600 disabled:opacity-40'}`}>
          {listening ? '停止' : '开始说话'}
        </button>
        <button onClick={() => setMuted(!muted)} className={`p-3 rounded-full transition-colors ${muted ? 'bg-error/10 text-error' : 'bg-gray-100 text-gray-500'}`}>
          {muted ? <VolumeX size={18} /> : <Volume2 size={18} />}
        </button>
      </div>
    </div>
  );
}
