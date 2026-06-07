import { Volume2 } from 'lucide-react';
import type { MessageData } from '../../lib/api';
import CorrectionCard from './CorrectionCard';

export default function MessageBubble({ msg, isStreaming }: { msg: MessageData; isStreaming?: boolean }) {
  const isUser = msg.role === 'USER';

  return (
    <div className={`flex ${isUser ? 'justify-end' : 'justify-start'} animate-bubble-in`}>
      <div className={`max-w-[82%] sm:max-w-[70%] ${isUser ? '' : 'w-full'}`}>
        {/* 气泡 */}
        <div className={`px-4 py-3 text-sm leading-relaxed whitespace-pre-wrap break-words
          ${isUser
            ? 'bg-primary-500 text-white rounded-2xl rounded-tr-md'
            : 'bg-white border border-gray-200 rounded-2xl rounded-tl-md text-gray-700'}`}>
          {msg.content}
          {isStreaming && <span className="typing-cursor text-primary-500 font-bold" />}

          {/* 语音播放按钮 */}
          {msg.audioUrl && (
            <button
              onClick={() => new Audio(msg.audioUrl).play()}
              className="mt-2 flex items-center gap-1.5 text-xs opacity-60 hover:opacity-100 transition-opacity">
              <Volume2 size={14} /> 播放语音
            </button>
          )}

          {/* 时间戳 */}
          <p className={`text-[10px] mt-1.5 ${isUser ? 'text-white/50' : 'text-gray-400'}`}>
            {new Date(msg.createdAt).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}
          </p>
        </div>

        {/* 纠错卡片 */}
        {msg.correction && <CorrectionCard correction={msg.correction} />}
      </div>
    </div>
  );
}
