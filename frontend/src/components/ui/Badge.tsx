import { type ReactNode } from 'react';

const colors = {
  green: 'bg-primary-100 text-primary-700',
  warm: 'bg-warm-300/50 text-orange-700',
  sky: 'bg-sky-300/50 text-blue-700',
  red: 'bg-error/20 text-red-700',
  gray: 'bg-gray-100 text-gray-600',
} as const;

export default function Badge({ children, color = 'green', className = '' }: {
  children: ReactNode; color?: keyof typeof colors; className?: string;
}) {
  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 text-xs font-medium rounded-full ${colors[color]} ${className}`}>
      {children}
    </span>
  );
}
