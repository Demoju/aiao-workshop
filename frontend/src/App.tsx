import { lazy, Suspense } from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { ErrorBoundary } from './shared/ErrorBoundary'
import { LoadingSpinner } from './shared/LoadingSpinner'
import './i18n'
import './index.css'

const MenuPage = lazy(() => import('./customer/pages/MenuPage'))
const CartPage = lazy(() => import('./customer/pages/CartPage'))
const OrderHistoryPage = lazy(() => import('./customer/pages/OrderHistoryPage'))
const TableLoginPage = lazy(() => import('./customer/pages/TableLoginPage'))
const AdminLoginPage = lazy(() => import('./admin/pages/AdminLoginPage'))
const AdminDashboardPage = lazy(() => import('./admin/pages/AdminDashboardPage'))
const TableManagementPage = lazy(() => import('./admin/pages/TableManagementPage'))
const MenuManagementPage = lazy(() => import('./admin/pages/MenuManagementPage'))

function App() {
  return (
    <ErrorBoundary>
      <BrowserRouter>
        <LoadingSpinner />
        <Suspense fallback={<div className="flex min-h-screen items-center justify-center">로딩 중...</div>}>
          <Routes>
            <Route path="/" element={<Navigate to="/customer/login" replace />} />
            <Route path="/customer/login" element={<TableLoginPage />} />
            <Route path="/customer/menu" element={<MenuPage />} />
            <Route path="/customer/cart" element={<CartPage />} />
            <Route path="/customer/orders" element={<OrderHistoryPage />} />
            <Route path="/admin/login" element={<AdminLoginPage />} />
            <Route path="/admin/dashboard" element={<AdminDashboardPage />} />
            <Route path="/admin/tables" element={<TableManagementPage />} />
            <Route path="/admin/menus" element={<MenuManagementPage />} />
          </Routes>
        </Suspense>
      </BrowserRouter>
    </ErrorBoundary>
  )
}

export default App
