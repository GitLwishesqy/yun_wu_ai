import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Sprout, Mic, Sparkles, TrendingUp } from 'lucide-react';
import Button from '../components/ui/Button';
import Input from '../components/ui/Input';
import Logo from '../components/ui/Logo';

export default function Home() {
  const [phone, setPhone] = useState('');
  const [code, setCode] = useState('');
  const [step, setStep] = useState<'phone' | 'code'>('phone');
  const navigate = useNavigate();

  const handleSendCode = () => {
    if (phone.length === 11) setStep('code');
  };

  const handleLogin = () => {
    navigate('/coach');
  };

  return (
    <div className="min-h-dvh flex flex-col lg:flex-row">
      {/* 左侧品牌区 */}
      <div className="flex-1 bg-gradient-to-br from-primary-50 via-white to-primary-100 flex flex-col items-center justify-center p-8 lg:p-16">
        <div className="max-w-md text-center lg:text-left">
          {/* 装饰元素 */}
          <div className="inline-flex items-center gap-2 px-3 py-1.5 bg-primary-100/80 rounded-full text-primary-700 text-sm font-medium mb-8">
            <Sparkles size={16} /> AI 英语陪练智能体
          </div>

          <div className="flex items-center gap-3 mb-6 justify-center lg:justify-start">
            <div className="relative animate-float">
              <Sprout size={48} className="text-primary-500" strokeWidth={1.5} />
              <div className="absolute -top-1 -right-1 w-3 h-3 bg-warm-500 rounded-full animate-pulse" />
            </div>
            <h1 className="text-4xl lg:text-5xl font-bold text-gray-800 tracking-tight">
              云悟英语
            </h1>
          </div>

          <h2 className="text-xl lg:text-2xl text-gray-600 font-medium mb-3">
            Say It Naturally
          </h2>
          <p className="text-gray-500 mb-10 leading-relaxed">
            你的 AI 英语陪练伙伴。从这里开始，让每一次开口都算数。
          </p>

          {/* 特性卡片 */}
          <div className="grid grid-cols-3 gap-4">
            {[{ icon: Mic, label: '场景陪练' }, { icon: Sparkles, label: '智能纠错' }, { icon: TrendingUp, label: '学习报告' }]
              .map(({ icon: Icon, label }) => (
                <div key={label} className="flex flex-col items-center gap-2 p-4 rounded-xl bg-white/60 border border-primary-100">
                  <Icon size={24} className="text-primary-500" />
                  <span className="text-xs font-medium text-gray-600">{label}</span>
                </div>
              ))}
          </div>
        </div>
      </div>

      {/* 右侧登录区 */}
      <div className="flex-1 flex items-center justify-center p-8 bg-white lg:shadow-2xl lg:rounded-l-3xl">
        <div className="w-full max-w-sm">
          <div className="mb-8 lg:hidden">
            <Logo size="md" />
          </div>

          {step === 'phone' ? (
            <>
              <h3 className="text-2xl font-bold text-gray-800 mb-2">欢迎回来</h3>
              <p className="text-gray-500 mb-6">请输入手机号开始学习</p>
              <Input
                type="tel" placeholder="输入手机号" maxLength={11}
                value={phone} onChange={e => setPhone(e.target.value)} />
              <Button className="w-full mt-4" size="lg" onClick={handleSendCode} disabled={phone.length < 11}>
                获取验证码
              </Button>
            </>
          ) : (
            <>
              <h3 className="text-2xl font-bold text-gray-800 mb-2">输入验证码</h3>
              <p className="text-gray-500 mb-6">已发送至 {phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')}</p>
              <Input
                type="text" placeholder="6位验证码" maxLength={6}
                value={code} onChange={e => setCode(e.target.value)} />
              <Button className="w-full mt-4" size="lg" onClick={handleLogin} disabled={code.length < 4}>
                开始体验
              </Button>
              <button onClick={() => setStep('phone')} className="w-full mt-3 text-sm text-primary-600 hover:underline">
                更换手机号
              </button>
            </>
          )}

          <p className="text-center text-xs text-gray-400 mt-8">
            登录即表示同意 <span className="text-primary-600 cursor-pointer">服务条款</span> 和 <span className="text-primary-600 cursor-pointer">隐私政策</span>
          </p>
        </div>
      </div>
    </div>
  );
}
