import { z } from 'zod/v4'

export const tableLoginSchema = z.object({
  storeId: z.number(),
  tableNumber: z.string().min(1, '테이블 번호를 입력해주세요'),
  password: z.string().min(1, '비밀번호를 입력해주세요'),
})

export const adminLoginSchema = z.object({
  storeId: z.number(),
  username: z.string().min(1, '아이디를 입력해주세요'),
  password: z.string().min(1, '비밀번호를 입력해주세요'),
})

export type TableLoginFormData = z.infer<typeof tableLoginSchema>
export type AdminLoginFormData = z.infer<typeof adminLoginSchema>
