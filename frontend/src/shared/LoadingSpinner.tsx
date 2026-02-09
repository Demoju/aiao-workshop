import { useLoadingStore } from '../store/loadingStore'

export function LoadingSpinner() {
  const isLoading = useLoadingStore((s) => s.isLoading)
  if (!isLoading) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
      <div className="h-12 w-12 animate-spin rounded-full border-4 border-gray-300 border-t-blue-500" />
    </div>
  )
}
