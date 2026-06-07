import { Sprout } from 'lucide-react';

export default function Logo({ size = 'md' }: { size?: 'sm' | 'md' | 'lg' }) {
  const sizes = { sm: 24, md: 32, lg: 48 };
  const fonts = { sm: 'text-lg', md: 'text-xl', lg: 'text-3xl' };
  return (
    <div className="flex items-center gap-2 select-none">
      <div className="relative">
        <Sprout size={sizes[size]} className="text-primary-500" strokeWidth={1.5} />
        <div className="absolute -top-0.5 -right-0.5 w-2 h-2 bg-warm-500 rounded-full animate-pulse" />
      </div>
      <span className={`${fonts[size]} font-semibold text-gray-800 tracking-tight`}>
        云悟英语
      </span>
    </div>
  );
}
