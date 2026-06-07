/** API 客户端 — 封装 fetch + 自动 Token */
const BASE = '/api/v1';

let token: string | null = localStorage.getItem('token');

export function setToken(t: string) { token = t; localStorage.setItem('token', t); }
export function getToken() { return token; }
export function clearToken() { token = null; localStorage.removeItem('token'); }

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const headers: Record<string, string> = { 'Content-Type': 'application/json' };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${BASE}${path}`, { ...options, headers: { ...headers, ...options?.headers } });
  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: res.statusText }));
    throw new Error(err.message || `HTTP ${res.status}`);
  }
  const json = await res.json();
  if (json.code !== 0) throw new Error(json.message || 'unknown error');
  return json.data;
}

// ==================== 鉴权 ====================
export const authApi = {
  sendCode: (phone: string) => request('/auth/send-code', { method: 'POST', body: JSON.stringify({ phone, purpose: 'LOGIN' }) }),
  login: (phone: string, code: string) => request<LoginData>('/auth/login', { method: 'POST', body: JSON.stringify({ phone, code }) }),
};

// ==================== 陪练 ====================
export const coachApi = {
  createSession: (sceneId: number) => request<SessionData>('/sessions', { method: 'POST', body: JSON.stringify({ sceneId, sessionType: 'SCENE' }) }),
  sendMessage: (sessionId: number, content: string) =>
    request<MessageData>(`/sessions/${sessionId}/messages`, { method: 'POST', body: JSON.stringify({ content, contentType: 'TEXT' }) }),
  getMessages: (sessionId: number) => request<MessageData[]>(`/sessions/${sessionId}/messages`),
  completeSession: (sessionId: number) => request<SessionData>(`/sessions/${sessionId}/complete`, { method: 'POST' }),
};

// ==================== 词汇 ====================
export const vocabApi = {
  myVocab: (params?: Record<string, string>) => request<VocabListData>(`/vocabulary/my${params ? '?' + new URLSearchParams(params) : ''}`),
  updateStatus: (id: number, status: string) => request(`/vocabulary/${id}/status`, { method: 'PATCH', body: JSON.stringify({ status }) }),
  reviewDue: (limit = 20) => request<VocabWord[]>(`/vocabulary/review-due?limit=${limit}`),
  completeReview: (id: number) => request(`/vocabulary/review/${id}`, { method: 'POST' }),
  searchLibrary: (params?: Record<string, string>) => request<LibraryWord[]>(`/vocabulary/library${params ? '?' + new URLSearchParams(params) : ''}`),
};

// ==================== 技能训练 ====================
export const skillApi = {
  listListening: (params?: Record<string, string>) => request<any[]>(`/skills/listening${params ? '?' + new URLSearchParams(params) : ''}`),
  getListening: (id: number) => request<any>(`/skills/listening/${id}`),
  submitListening: (id: number, answers: { answer: string }[]) => request<any>(`/skills/listening/${id}/submit`, { method: 'POST', body: JSON.stringify({ answers }) }),
  listReading: (params?: Record<string, string>) => request<any[]>(`/skills/reading${params ? '?' + new URLSearchParams(params) : ''}`),
  getReading: (id: number) => request<any>(`/skills/reading/${id}`),
  submitReading: (id: number, answers: { answer: string }[]) => request<any>(`/skills/reading/${id}/submit`, { method: 'POST', body: JSON.stringify({ answers }) }),
  listWriting: (params?: Record<string, string>) => request<any[]>(`/skills/writing${params ? '?' + new URLSearchParams(params) : ''}`),
  getWriting: (id: number) => request<any>(`/skills/writing/${id}`),
  submitWriting: (id: number, content: string) => request<any>(`/skills/writing/${id}/submit`, { method: 'POST', body: JSON.stringify({ content, timeTakenSeconds: 0 }) }),
};

// ==================== 激励 ====================
export const incentiveApi = {
  checkIn: () => request<any>('/check-ins', { method: 'POST' }),
  calendar: (month: string) => request<any>(`/check-ins/calendar?month=${month}`),
  points: () => request<any>('/points/records'),
  leaderboard: (limit = 50) => request<any>(`/leaderboard?limit=${limit}`),
};

// ==================== 学习计划 ====================
export const planApi = {
  list: (isActive?: boolean) => request<any[]>(`/plans${isActive !== undefined ? `?isActive=${isActive}` : ''}`),
  getActive: () => request<any>('/plans/active'),
  generate: (data: { name: string; startDate: string; endDate: string; targetLevel: string; weeklyEffort: string }) =>
    request<any>('/plans/generate', { method: 'POST', body: JSON.stringify(data) }),
};

// ==================== 纠错 ====================
export const correctionApi = {
  history: (errorType?: string, page = 1, size = 20) => request<any>(`/corrections/history?page=${page}&size=${size}${errorType ? `&errorType=${errorType}` : ''}`),
  analysis: () => request<any>('/users/me/error-analysis'),
  reviewDue: (limit = 20) => request<any[]>(`/corrections/review-due?limit=${limit}`),
  completeReview: (id: number) => request(`/corrections/review/${id}`, { method: 'POST' }),
};

// ==================== 家长 ====================
export const parentApi = {
  bindings: () => request<any[]>('/parent/bindings'),
  requestBind: (studentPhone: string, relationship: string) => request('/parent/bindings', { method: 'POST', body: JSON.stringify({ studentPhone, relationship }) }),
  approve: (id: number) => request(`/parent/bindings/${id}/approve`, { method: 'POST' }),
  reject: (id: number) => request(`/parent/bindings/${id}/reject`, { method: 'POST' }),
  updateSettings: (id: number, data: any) => request(`/parent/bindings/${id}/settings`, { method: 'PATCH', body: JSON.stringify(data) }),
  studentOverview: (studentId: number) => request<any>(`/parent/students/${studentId}/overview`),
};

// ==================== 场景 ====================
export const sceneApi = {
  list: (params?: Record<string, string>) => {
    const qs = params ? '?' + new URLSearchParams(params).toString() : '';
    return request<SceneListData>(`/scenes${qs}`);
  },
  detail: (id: number) => request<SceneData>(`/scenes/${id}`),
};

// ==================== 类型 ====================
export interface LoginData { user: { id: number; nickname: string; role: string; gradeLevel?: string; cefrLevel?: string }; tokens: { accessToken: string; refreshToken: string; expiresIn: number } }
export interface SessionData { id: number; sceneId: number; sessionType: string; title?: string; status: string; startedAt: string; endedAt?: string; sceneInfo?: SceneData }
export interface MessageData { id: number; sessionId: number; role: 'USER' | 'AI'; content: string; contentType: string; audioUrl?: string; hasCorrection?: boolean; sequenceNum: number; createdAt: string; correction?: CorrectionData }
export interface CorrectionData { errorType: string; errorSubtype?: string; severity: string; originalText: string; errorSpan: string; correctedText: string; explanation: string; improvementTip?: string }
export interface SceneListData { items: SceneData[]; pagination: { page: number; size: number; total: number } }
export interface SceneData { id: number; name: string; nameEn?: string; category: string; gradeLevel?: string; difficulty: number; cefrLevel?: string; estimatedDuration?: number; roles?: { name: string; nameEn?: string }[]; keywords?: { word: string; translation: string }[]; targetSentences?: { sentence: string; explanation: string }[]; tags?: string[] }
export interface VocabWord { id: number; word: string; translation: string; status: string; seenCount: number; usedCount: number; errorCount: number; correctCount: number; lastSeenAt?: string; nextReviewAt?: string }
export interface VocabListData { items: VocabWord[]; stats: Record<string, number>; pagination: { page: number; size: number; total: number; totalPages: number } }
export interface LibraryWord { id: number; word: string; pronunciation: string; translation: string; partOfSpeech: string; definitionEn?: string; cefrLevel?: string; difficulty?: number }
