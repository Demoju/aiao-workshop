import { Component, type ReactNode } from 'react'

interface Props { children: ReactNode }
interface State { hasError: boolean }

export class ErrorBoundary extends Component<Props, State> {
  state: State = { hasError: false }

  static getDerivedStateFromError() { return { hasError: true } }

  componentDidCatch(error: Error) { console.error(error) }

  render() {
    if (this.state.hasError) {
      return (
        <div className="flex min-h-screen flex-col items-center justify-center p-4">
          <h1 className="text-2xl font-bold">오류가 발생했습니다</h1>
          <button
            onClick={() => { this.setState({ hasError: false }); window.location.href = '/' }}
            className="mt-4 rounded bg-blue-500 px-4 py-2 text-white min-h-[44px]"
          >
            홈으로 이동
          </button>
        </div>
      )
    }
    return this.props.children
  }
}
