import { ApiError } from '../api/client'
import type { BudgetRole } from '../types/domain'

const roleLabels: Record<string, string> = {
  OWNER: 'Владелец',
  ADMIN: 'Администратор',
  EDITOR: 'Редактор',
  VIEWER: 'Наблюдатель',
  READER: 'Наблюдатель',
}

export function roleLabel(role: BudgetRole): string {
  return roleLabels[role] ?? role
}

function normalizeKnownMojibake(text: string): string {
  const map: Record<string, string> = {
    'РЎРµСЂРІРёСЃ РІСЂРµРјРµРЅРЅРѕ РЅРµРґРѕСЃС‚СѓРїРµРЅ. РџРѕРїСЂРѕР±СѓР№С‚Рµ РїРѕР·Р¶Рµ.':
      'Сервис временно недоступен. Попробуйте позже.',
    'РџСЂРѕРёР·РѕС€Р»Р° РѕС€РёР±РєР°. РџРѕРїСЂРѕР±СѓР№С‚Рµ РµС‰Рµ СЂР°Р·.':
      'Произошла ошибка. Попробуйте еще раз.',
  }
  return map[text] ?? text
}

function fieldLabel(field: string): string {
  const labels: Record<string, string> = {
    name: 'Название',
    description: 'Описание',
    amount: 'Сумма',
    completedDate: 'Дата операции',
    categoryId: 'Категория',
    login: 'Логин',
    password: 'Пароль',
    email: 'Email',
    username: 'Имя пользователя',
  }
  return labels[field] ?? field
}

export function humanizeError(err: unknown, fallback = 'Произошла ошибка. Попробуйте еще раз.'): string {
  if (!(err instanceof ApiError)) {
    if (err instanceof Error && err.message) return normalizeKnownMojibake(err.message)
    return fallback
  }

  const details = err.payload?.errors
  if (details && Object.keys(details).length > 0) {
    const first = Object.entries(details)[0]
    if (first) {
      const [field, messages] = first
      const firstMessage = messages[0]
      return normalizeKnownMojibake(`${fieldLabel(field)}: ${firstMessage ?? 'Некорректное значение'}`)
    }
  }

  if (err.status === 401) return 'Нужно войти в систему.'
  if (err.status === 403) return 'Недостаточно прав для выполнения действия.'
  if (err.status === 404) return 'Запрашиваемый объект не найден.'
  if (err.status === 409) return 'Конфликт данных. Обновите страницу и попробуйте снова.'
  if (err.status >= 500) return 'Сервис временно недоступен. Попробуйте позже.'

  return normalizeKnownMojibake(err.message || fallback)
}
