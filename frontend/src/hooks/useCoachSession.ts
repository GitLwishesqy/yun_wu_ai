import { useState, useCallback, useRef } from 'react';
import { coachApi, type MessageData, type SessionData } from '../lib/api';

export function useCoachSession() {
  const [session, setSession] = useState<SessionData | null>(null);
  const [messages, setMessages] = useState<MessageData[]>([]);
  const [isTyping, setIsTyping] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const streamRef = useRef(false);

  const startSession = useCallback(async (sceneId: number) => {
    try {
      setError(null);
      const s = await coachApi.createSession(sceneId);
      setSession(s);
      return s;
    } catch (e: any) {
      setError(e.message);
      return null;
    }
  }, []);

  const sendMessage = useCallback(async (content: string) => {
    if (!session || isTyping) return;
    streamRef.current = true;

    const userMsg: MessageData = {
      id: Date.now(), sessionId: session.id, role: 'USER',
      content, contentType: 'TEXT', sequenceNum: messages.length + 1,
      createdAt: new Date().toISOString(), hasCorrection: false,
    };
    setMessages(prev => [...prev, userMsg]);
    setIsTyping(true);
    setError(null);

    try {
      const resp = await coachApi.sendMessage(session.id, content);
      if (streamRef.current) {
        setMessages(prev => [...prev, resp]);
      }
    } catch (e: any) {
      setError(e.message);
    } finally {
      setIsTyping(false);
    }
  }, [session, isTyping, messages.length]);

  const loadMessages = useCallback(async () => {
    if (!session) return;
    try {
      const msgs = await coachApi.getMessages(session.id);
      setMessages(msgs);
    } catch (e: any) { setError(e.message); }
  }, [session]);

  const endSession = useCallback(async () => {
    if (!session) return;
    streamRef.current = false;
    try { await coachApi.completeSession(session.id); } catch (e: any) { /* ignore */ }
    setSession(null);
    setMessages([]);
  }, [session]);

  return { session, messages, isTyping, error, startSession, sendMessage, loadMessages, endSession, setError };
}
