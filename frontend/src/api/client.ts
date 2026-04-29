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

async function parseBody(response: Response): Promise<unknown> {
  const contentType = response.headers.get('content-type') ?? ''
  if (contentType.includes('application/json')) return response.json()
  const text = await response.text()
  return text ? { detail: text } : null
}

export async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    credentials: 'include',
    headers: {
      ...jsonHeaders,
      ...(init.headers ?? {}),
    },
  })

  if (!response.ok) {
    const payload = (await parseBody(response)) as ApiProblem | null
    const message = payload?.detail ?? payload?.title ?? `HTTP ${response.status}`
    throw new ApiError(message, response.status, payload)
  }

  if (response.status === 204) return undefined as T
  return (await parseBody(response)) as T
}

