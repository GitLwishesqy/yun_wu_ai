import { type InputHTMLAttributes, forwardRef } from 'react';

const Input = forwardRef<HTMLInputElement, InputHTMLAttributes<HTMLInputElement> & { error?: string }>(
  ({ className = '', error, ...props }, ref) => (
    <div className="w-full">
      <input ref={ref}
        className={`w-full px-4 py-3 rounded-lg border bg-white text-gray-700 placeholder-gray-400
          focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-primary-400
          transition-all duration-200 ${error ? 'border-error ring-error/20' : 'border-gray-300'} ${className}`}
        {...props} />
      {error && <p className="mt-1 text-sm text-error">{error}</p>}
    </div>
  )
);
Input.displayName = 'Input';
export default Input;
