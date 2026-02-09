import { describe, it, expect } from 'vitest'
import { tableLoginSchema, adminLoginSchema } from '../../types/schemas'

describe('Validation Schemas', () => {
  // TC-VAL-001
  it('tableLoginSchema - 유효한 입력 통과', () => {
    const result = tableLoginSchema.safeParse({ storeId: 1, tableNumber: '1', password: 'pass' })
    expect(result.success).toBe(true)
  })

  // TC-VAL-002
  it('tableLoginSchema - 빈 tableNumber 실패', () => {
    const result = tableLoginSchema.safeParse({ storeId: 1, tableNumber: '', password: 'pass' })
    expect(result.success).toBe(false)
  })

  // TC-VAL-003
  it('adminLoginSchema - 유효한 입력 통과', () => {
    const result = adminLoginSchema.safeParse({ storeId: 1, username: 'admin', password: 'pass' })
    expect(result.success).toBe(true)
  })
})
