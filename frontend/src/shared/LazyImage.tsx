import { useRef, useState, useEffect } from 'react'

interface LazyImageProps {
  src: string
  alt: string
  className?: string
}

export function LazyImage({ src, alt, className }: LazyImageProps) {
  const imgRef = useRef<HTMLImageElement>(null)
  const [loaded, setLoaded] = useState(false)

  useEffect(() => {
    const observer = new IntersectionObserver(([entry]) => {
      if (entry.isIntersecting) {
        setLoaded(true)
        observer.disconnect()
      }
    })
    if (imgRef.current) observer.observe(imgRef.current)
    return () => observer.disconnect()
  }, [])

  return (
    <img
      ref={imgRef}
      src={loaded ? src : undefined}
      alt={alt}
      className={className}
      onError={(e) => { (e.target as HTMLImageElement).src = '/placeholder.png' }}
    />
  )
}
