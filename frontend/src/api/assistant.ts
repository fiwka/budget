import { API_BASE_URL } from '../config'
import { request } from './client'

export type CreateChatSessionResponse = {
  sessionId: string
}

export type ChatMessageResponse = {
  response: string
}

export async function createSession(budgetId: string): Promise<CreateChatSessionResponse> {
  return request<CreateChatSessionResponse>('/api/ai/chat/sessions', {
    method: 'POST',
    body: JSON.stringify({ budgetId }),
  })
}

export async function deleteSession(sessionId: string): Promise<void> {
  await request<void>(`/api/ai/chat/sessions/${sessionId}`, { method: 'DELETE' })
}

export async function sendMessage(sessionId: string, message: string): Promise<ChatMessageResponse> {
  return request<ChatMessageResponse>(`/api/ai/chat/sessions/${sessionId}/messages`, {
    method: 'POST',
    body: JSON.stringify({ message }),
  })
}

export async function streamMessage(
  sessionId: string,
  message: string,
  onChunk: (chunk: string) => void,
  onStatus?: (status: string) => void
): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/api/ai/chat/sessions/${sessionId}/messages/stream`, {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      Accept: 'text/event-stream',
      'Cache-Control': 'no-cache',
    },
    body: JSON.stringify({ message }),
  })

  if (!response.ok) {
    const text = await response.text()
    throw new Error(text || `HTTP ${response.status}`)
  }

  if (!response.body) {
    throw new Error('Пустой поток ответа от сервера')
  }

  const reader = response.body.getReader()
  const decoder = new TextDecoder('utf-8')
  let buffer = ''
  let currentEvent = 'message'
  let currentData: string[] = []

  const parsePayload = (raw: string) => {
    try {
      const parsed = JSON.parse(raw) as { content?: string }
      return parsed.content ?? ''
    } catch {
      return raw
    }
  }

  const flushEvent = () => {
    if (currentData.length === 0) {
      currentEvent = 'message'
      return false
    }

    const payload = parsePayload(currentData.join('\n'))
    const eventName = currentEvent
    currentEvent = 'message'
    currentData = []

    if (eventName === 'done') return true
    if (!payload) return false
    if (eventName === 'status') {
      onStatus?.(payload)
      return false
    }

    onChunk(payload)
    return false
  }

  while (true) {
    const { done, value } = await reader.read()
    if (done) {
      flushEvent()
      break
    }

    buffer += decoder.decode(value, { stream: true })
    const lines = buffer.split(/\r?\n/)
    buffer = lines.pop() ?? ''

    for (const line of lines) {
      if (line.startsWith('event:')) {
        currentEvent = line.slice(6).trim()
        continue
      }

      if (line.startsWith('data:')) {
        currentData.push(line.slice(5).trimStart())
        continue
      }

      if (line.trim() === '') {
        if (flushEvent()) return
      }
    }
  }
}
