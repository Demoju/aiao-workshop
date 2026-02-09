interface ConfirmDialogProps {
  isOpen: boolean
  title: string
  message: string
  onConfirm: () => void
  onCancel: () => void
}

export function ConfirmDialog({ isOpen, title, message, onConfirm, onCancel }: ConfirmDialogProps) {
  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div className="mx-4 w-full max-w-sm rounded-lg bg-white p-6">
        <h2 className="text-lg font-bold">{title}</h2>
        <p className="mt-2 text-gray-600">{message}</p>
        <div className="mt-4 flex justify-end gap-2">
          <button onClick={onCancel} className="rounded border px-4 py-2 min-h-[44px]">취소</button>
          <button onClick={onConfirm} className="rounded bg-blue-500 px-4 py-2 text-white min-h-[44px]">확인</button>
        </div>
      </div>
    </div>
  )
}
