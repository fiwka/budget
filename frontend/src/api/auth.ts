import { request } from './client'
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
  return request<SessionStatus>('/api/session/status')
}

export function getUserInfo() {
  return request<{ id: string; username: string; email: string }>('/api/user/info')
}

