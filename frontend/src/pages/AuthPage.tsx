import { useState } from 'react'
import type { FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../state/auth'
import { ThemeSwitcher } from '../components/ThemeSwitcher'

export function AuthPage() {
  const { login, register } = useAuth()
  const navigate = useNavigate()
  const [tab, setTab] = useState<'login' | 'register'>('login')
  const [error, setError] = useState<string | null>(null)

  async function onLoginSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const form = new FormData(e.currentTarget)
    try {
      setError(null)
      await login(String(form.get('login')), String(form.get('password')))
      navigate('/budgets')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Ошибка входа')
    }
  }

  async function onRegisterSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const form = new FormData(e.currentTarget)
    try {
      setError(null)
      await register(String(form.get('username')), String(form.get('email')), String(form.get('password')))
      setTab('login')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Ошибка регистрации')
    }
  }

  return (
    <main className="screen auth-screen">
      <header className="topbar">
        <h1>Budget Manager</h1>
        <ThemeSwitcher />
      </header>

      <section className="auth-card">
        <div className="segmented">
          <button className={tab === 'login' ? 'active' : ''} onClick={() => setTab('login')}>Вход</button>
          <button className={tab === 'register' ? 'active' : ''} onClick={() => setTab('register')}>Регистрация</button>
        </div>

        {tab === 'login' ? (
          <form onSubmit={onLoginSubmit} className="form-grid">
            <label>Логин или Email<input name="login" required /></label>
            <label>Пароль<input name="password" type="password" required /></label>
            <button type="submit">Войти</button>
          </form>
        ) : (
          <form onSubmit={onRegisterSubmit} className="form-grid">
            <label>Username<input name="username" minLength={3} required /></label>
            <label>Email<input name="email" type="email" required /></label>
            <label>Пароль<input name="password" type="password" minLength={6} required /></label>
            <button type="submit">Создать аккаунт</button>
          </form>
        )}

        {error && <p className="error">{error}</p>}
      </section>
    </main>
  )
}
