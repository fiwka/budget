import { useState } from 'react'
import type { FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { ThemeSwitcher } from '../components/ThemeSwitcher'
import { useAuth } from '../state/auth'
import { useToast } from '../state/toast'
import { humanizeError } from '../utils/uiText'

export function AuthPage() {
  const { login, register } = useAuth()
  const toast = useToast()
  const navigate = useNavigate()
  const [tab, setTab] = useState<'login' | 'register'>('login')

  async function onLoginSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const form = new FormData(e.currentTarget)
    try {
      await login(String(form.get('login')), String(form.get('password')))
      toast.success('Вход выполнен.')
      navigate('/budgets')
    } catch (err) {
      toast.error(humanizeError(err, 'Ошибка входа.'))
    }
  }

  async function onRegisterSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const form = new FormData(e.currentTarget)
    try {
      await register(String(form.get('username')), String(form.get('email')), String(form.get('password')))
      toast.success('Аккаунт создан. Теперь войдите в систему.')
      setTab('login')
    } catch (err) {
      toast.error(humanizeError(err, 'Ошибка регистрации.'))
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
      </section>
    </main>
  )
}
