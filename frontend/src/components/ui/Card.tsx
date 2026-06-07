import { type ReactNode } from 'react';

export default function Card({ children, className = '', hover = false, onClick }: {
  children: ReactNode; className?: string; hover?: boolean; onClick?: () => void;
}) {
  return (
    <div
      onClick={onClick}
      className={`bg-white rounded-xl border border-gray-200 shadow-sm
        ${hover ? 'hover:shadow-md hover:border-primary-300 hover:-translate-y-0.5 cursor-pointer' : ''}
        transition-all duration-200 ${className}`}
    >
      {children}
    </div>
  );
}
