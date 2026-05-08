import { API_BASE_URL } from '../config'
import type { ApiProblem } from '../types/domain'

export class ApiError extends Error {
  status: number
  payload: ApiProblem | null

  constructor(message: string, status: number, payload: ApiProblem | null = null) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.payload = payload
  }
}

const jsonHeaders = { 'Content-Type': 'application/json' }
const refreshPath = '/api/session/refresh'

async function parseBody(response: Response): Promise<unknown> {
  const contentType = response.headers.get('content-type') ?? ''
  if (contentType.includes('application/json')) return response.json()
  const text = await response.text()
  return text ? { detail: text } : null
}

async function refreshSession(): Promise<boolean> {
  const response = await fetch(`${API_BASE_URL}${refreshPath}`, {
    method: 'POST',
    credentials: 'include',
    headers: jsonHeaders,
  })

  if (!response.ok) return false

  const body = (await parseBody(response)) as { authenticated?: boolean } | null
  return body?.authenticated === true
}

function shouldRetryAfterRefresh(path: string, init: RequestInit): boolean {
  const method = (init.method ?? 'GET').toUpperCase()
  return path !== refreshPath && !path.startsWith('/api/session/') && method !== 'DELETE'
}

export async function request<T>(path: string, init: RequestInit = {}, retryAfterRefresh = true): Promise<T> {
  const isFormData = init.body instanceof FormData
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    credentials: 'include',
    headers: {
      ...(isFormData ? {} : jsonHeaders),
      ...(init.headers ?? {}),
    },
  })

  if (!response.ok) {
    if (response.status === 401 && retryAfterRefresh && shouldRetryAfterRefresh(path, init)) {
      const refreshed = await refreshSession()
      if (refreshed) return request<T>(path, init, false)
    }

    const payload = (await parseBody(response)) as ApiProblem | null
    const message = payload?.detail ?? payload?.title ?? `HTTP ${response.status}`
    throw new ApiError(message, response.status, payload)
  }

  if (response.status === 204) return undefined as T
  return (await parseBody(response)) as T
}

