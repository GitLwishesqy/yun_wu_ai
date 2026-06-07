import { type ButtonHTMLAttributes, type ReactNode } from 'react';
import { Loader2 } from 'lucide-react';

type Variant = 'primary' | 'secondary' | 'ghost' | 'warm' | 'danger';
type Size = 'sm' | 'md' | 'lg';

interface Props extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant; size?: Size; loading?: boolean; icon?: ReactNode;
}

const base = "inline-flex items-center justify-center gap-2 font-medium transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-primary-300 disabled:opacity-50 disabled:cursor-not-allowed active:scale-[0.98]";

const variants: Record<Variant, string> = {
  primary: "bg-primary-500 text-white hover:bg-primary-600 shadow-md hover:shadow-lg",
  secondary: "bg-primary-100 text-primary-700 hover:bg-primary-200 border border-primary-300",
  ghost: "bg-transparent text-gray-600 hover:bg-gray-100",
  warm: "bg-warm-500 text-white hover:bg-orange-500 shadow-md",
  danger: "bg-error text-white hover:bg-red-400 shadow-md",
};

const sizes: Record<Size, string> = {
  sm: "px-3 py-1.5 text-sm rounded-md",
  md: "px-5 py-2.5 text-sm rounded-lg",
  lg: "px-7 py-3.5 text-base rounded-xl",
};

export default function Button({ variant = 'primary', size = 'md', loading, icon, children, className = '', ...props }: Props) {
  return (
    <button className={`${base} ${variants[variant]} ${sizes[size]} ${className}`} disabled={loading || props.disabled} {...props}>
      {loading ? <Loader2 size={18} className="animate-spin" /> : icon}
      {children}
    </button>
  );
}
