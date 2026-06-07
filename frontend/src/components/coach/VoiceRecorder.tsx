import { useState, useRef, useCallback } from 'react';
import { Mic, Square } from 'lucide-react';

interface Props { onRecorded: (blob: Blob) => void; disabled?: boolean }

export default function VoiceRecorder({ onRecorded, disabled }: Props) {
  const [recording, setRecording] = useState(false);
  const [duration, setDuration] = useState(0);
  const mediaRecorder = useRef<MediaRecorder | null>(null);
  const timer = useRef<number>(0);

  const start = useCallback(async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const mr = new MediaRecorder(stream, { mimeType: 'audio/webm' });
      const chunks: Blob[] = [];
      mr.ondataavailable = e => chunks.push(e.data);
      mr.onstop = () => {
        onRecorded(new Blob(chunks, { type: 'audio/webm' }));
        stream.getTracks().forEach(t => t.stop());
      };
      mr.start();
      mediaRecorder.current = mr;
      setRecording(true);
      setDuration(0);
      timer.current = window.setInterval(() => setDuration(d => d + 1), 1000);
    } catch (e) {
      console.warn('麦克风不可用:', e);
    }
  }, [onRecorded]);

  const stop = useCallback(() => {
    mediaRecorder.current?.stop();
    setRecording(false);
    clearInterval(timer.current);
  }, []);

  return (
    <div className="relative flex items-center gap-2">
      {recording && (
        <div className="flex items-center gap-2">
          <span className="w-2 h-2 rounded-full bg-error animate-pulse" />
          <span className="text-sm text-error font-mono tabular-nums">
            {String(Math.floor(duration / 60)).padStart(2, '0')}:{String(duration % 60).padStart(2, '0')}
          </span>
        </div>
      )}
      <button
        onClick={recording ? stop : start}
        disabled={disabled}
        className={`p-2.5 rounded-full transition-all shrink-0
          ${recording
            ? 'bg-error text-white animate-pulse shadow-lg shadow-error/30'
            : 'bg-primary-100 text-primary-600 hover:bg-primary-200 disabled:opacity-40'}`}>
        {recording ? <Square size={18} /> : <Mic size={20} />}
      </button>
    </div>
  );
}
