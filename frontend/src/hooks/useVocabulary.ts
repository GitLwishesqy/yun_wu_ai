import { useState, useCallback } from 'react';
import { vocabApi, type VocabWord, type VocabListData, type LibraryWord } from '../lib/api';

export function useVocabulary() {
  const [data, setData] = useState<VocabListData | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async (params?: Record<string, string>) => {
    setLoading(true); setError(null);
    try { const d = await vocabApi.myVocab(params); setData(d); } catch (e: any) { setError(e.message); }
    finally { setLoading(false); }
  }, []);

  const updateStatus = useCallback(async (id: number, status: string) => {
    try { await vocabApi.updateStatus(id, status); } catch (e: any) { setError(e.message); }
  }, []);

  const getReviewDue = useCallback(async (limit = 20): Promise<VocabWord[]> => {
    try { return await vocabApi.reviewDue(limit); } catch { return []; }
  }, []);

  const completeReview = useCallback(async (id: number) => {
    try { await vocabApi.completeReview(id); } catch (e: any) { setError(e.message); }
  }, []);

  const searchLibrary = useCallback(async (params?: Record<string, string>): Promise<LibraryWord[]> => {
    try { return await vocabApi.searchLibrary(params); } catch { return []; }
  }, []);

  return { data, loading, error, load, updateStatus, getReviewDue, completeReview, searchLibrary };
}
