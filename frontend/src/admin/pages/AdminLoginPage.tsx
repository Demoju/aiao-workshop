import { useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { adminLoginSchema, type AdminLoginFormData } from '../../types/schemas'
import { useAuth } from '../../hooks/useAuth'
import { useState } from 'react'

export default function AdminLoginPage() {
  const navigate = useNavigate()
  const { login } = useAuth()
  const [error, setError] = useState('')
  const { register, handleSubmit, formState: { errors } } = useForm<AdminLoginFormData>({
    resolver: zodResolver(adminLoginSchema),
    defaultValues: { storeId: 1, username: '', password: '' },
  })

  const onSubmit = async (data: AdminLoginFormData) => {
    try {
      setError('')
      await login(data)
      navigate('/admin/dashboard')
    } catch {
      setError('로그인 정보가 올바르지 않습니다')
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center p-4">
      <form onSubmit={handleSubmit(onSubmit)} className="w-full max-w-sm space-y-4">
        <h1 className="text-2xl font-bold text-center">관리자 로그인</h1>
        <div>
          <input {...register('storeId', { valueAsNumber: true })} type="number" placeholder="매장 ID" className="w-full rounded border p-3 min-h-[44px]" />
        </div>
        <div>
          <input {...register('username')} placeholder="아이디" className="w-full rounded border p-3 min-h-[44px]" />
          {errors.username && <p className="mt-1 text-sm text-red-500">{errors.username.message}</p>}
        </div>
        <div>
          <input {...register('password')} type="password" placeholder="비밀번호" className="w-full rounded border p-3 min-h-[44px]" />
          {errors.password && <p className="mt-1 text-sm text-red-500">{errors.password.message}</p>}
        </div>
        {error && <p className="text-sm text-red-500">{error}</p>}
        <button type="submit" className="w-full rounded bg-blue-500 py-3 text-white min-h-[44px]">로그인</button>
      </form>
    </div>
  )
}
