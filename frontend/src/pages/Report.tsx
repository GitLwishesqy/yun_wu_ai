import { useState } from 'react';
import { TrendingUp, Clock, MessageSquare, Target, Download } from 'lucide-react';
import Card from '../components/ui/Card';
import Button from '../components/ui/Button';
import Badge from '../components/ui/Badge';

export default function Report() {
  const [period, setPeriod] = useState<'WEEKLY' | 'MONTHLY'>('WEEKLY');

  return (
    <div className="py-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">学习报告</h1>
          <p className="text-gray-500 mt-1">2026.06.01 - 2026.06.07</p>
        </div>
        <div className="flex gap-1 bg-white rounded-lg border border-gray-200 p-1">
          {(['WEEKLY', 'MONTHLY'] as const).map(p => (
            <button key={p} onClick={() => setPeriod(p)}
              className={`px-3 py-1.5 text-sm rounded-md font-medium transition-all
                ${period === p ? 'bg-primary-500 text-white shadow-sm' : 'text-gray-500 hover:bg-gray-50'}`}>
              {p === 'WEEKLY' ? '周报' : '月报'}
            </button>
          ))}
        </div>
      </div>

      {/* 数据概览 */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
        {[
          { icon: TrendingUp, label: '平均分', value: '82.5', change: '+3.2', color: 'text-primary-500' },
          { icon: Clock, label: '学习时长', value: '180分钟', change: '+30', color: 'text-sky-500' },
          { icon: MessageSquare, label: '对话次数', value: '12次', change: '+2', color: 'text-warm-500' },
          { icon: Target, label: '新词汇', value: '34词', change: '+12', color: 'text-success' },
        ].map(stat => (
          <Card key={stat.label} className="p-4 text-center">
            <stat.icon size={22} className={`mx-auto mb-2 ${stat.color}`} />
            <p className="text-2xl font-bold text-gray-800">{stat.value}</p>
            <p className="text-xs text-gray-500">{stat.label}</p>
            <Badge color="green" className="mt-1">{stat.change}</Badge>
          </Card>
        ))}
      </div>

      {/* 维度趋势 */}
      <Card className="p-4">
        <h3 className="font-semibold text-gray-800 mb-3">能力维度</h3>
        <div className="space-y-3">
          {[
            { label: '语法', current: 85, change: 2 },
            { label: '发音', current: 72, change: 5 },
            { label: '词汇', current: 80, change: 4 },
            { label: '流利度', current: 76, change: 1 },
            { label: '逻辑', current: 82, change: 3 },
          ].map(d => (
            <div key={d.label} className="flex items-center gap-3">
              <span className="w-14 text-sm text-gray-600">{d.label}</span>
              <div className="flex-1 h-3 bg-gray-100 rounded-full overflow-hidden">
                <div className="h-full bg-primary-500 rounded-full transition-all duration-500" style={{ width: `${d.current}%` }} />
              </div>
              <span className="text-sm font-bold text-gray-700 w-8 text-right">{d.current}</span>
              <Badge color={d.change > 0 ? 'green' : 'red'}>{d.change > 0 ? '+' : ''}{d.change}</Badge>
            </div>
          ))}
        </div>
      </Card>

      {/* 改进建议 */}
      <Card className="p-4 border-l-4 border-l-warm-500">
        <h3 className="font-semibold text-gray-800 mb-2">需要关注</h3>
        <ul className="space-y-2 text-sm text-gray-600">
          <li>· 过去式动词错误 (12次) — <span className="text-primary-600 cursor-pointer hover:underline">去练习 →</span></li>
          <li>· "th" 发音需要加强 (8次) — <span className="text-primary-600 cursor-pointer hover:underline">去练习 →</span></li>
        </ul>
      </Card>

      <Button variant="secondary" className="w-full" icon={<Download size={16} />}>导出 PDF 报告</Button>
    </div>
  );
}
