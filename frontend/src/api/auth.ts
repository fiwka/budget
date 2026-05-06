import { ApiError, request } from './client'
import type { SessionStatus } from '../types/domain'

export function login(login: string, password: string) {
  return request<SessionStatus>('/api/session/login', {
    method: 'POST',
    body: JSON.stringify({ login, password }),
  })
}

export function register(username: string, email: string, password: string) {
  return request<{ id: string; username: string; email: string }>('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify({ username, email, password }),
  })
}

export function logout() {
  return request<void>('/api/session/logout', { method: 'POST' })
}

export function getSessionStatus() {
  return request<SessionStatus>('/api/session/status', {}, false)
}

export function refreshSession() {
  return request<SessionStatus>('/api/session/refresh', { method: 'POST' }, false)
}

export async function keepSessionAlive(): Promise<SessionStatus> {
  try {
    const refreshed = await refreshSession()
    if (refreshed.authenticated) return refreshed
  } catch (error) {
    if (!(error instanceof ApiError) || error.status !== 401) throw error
  }

  try {
    return await getSessionStatus()
  } catch (error) {
    if (error instanceof ApiError && error.status === 401) return { authenticated: false }
    throw error
  }
}

export function getUserInfo() {
  return request<{ id: string; username: string; email: string }>('/api/user/info')
}
