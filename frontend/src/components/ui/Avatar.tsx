export default function Avatar({ src, name, size = 'md' }: {
  src?: string; name?: string; size?: 'sm' | 'md' | 'lg';
}) {
  const sizes = { sm: 'w-8 h-8 text-xs', md: 'w-10 h-10 text-sm', lg: 'w-14 h-14 text-lg' };
  const initial = name?.charAt(0)?.toUpperCase() || '?';
  if (src) return <img src={src} alt={name} className={`${sizes[size]} rounded-full object-cover ring-2 ring-primary-200`} />;
  return (
    <div className={`${sizes[size]} rounded-full bg-primary-200 text-primary-700 flex items-center justify-center font-bold`}>
      {initial}
    </div>
  );
}
