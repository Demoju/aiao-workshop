import { Header } from '../../shared/Header'

export default function TableManagementPage() {
  return (
    <div>
      <Header title="테이블 관리" showBack />
      <div className="p-4">
        <p className="text-center text-gray-500">테이블 관리 기능 (대시보드에서 통합 관리)</p>
      </div>
    </div>
  )
}
