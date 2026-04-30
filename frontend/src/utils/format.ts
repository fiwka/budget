export function formatIsoDateToInput(iso: string) {
  return new Date(iso).toISOString().slice(0, 16)
}

export function formatForTable(iso: string) {
  return new Intl.DateTimeFormat('ru-RU', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(iso))
}

export function toIsoFromInput(value: string) {
  return new Date(value).toISOString()
}

export function moneyLabel(amount: string | number) {
  const parsed = Number(amount)
  if (Number.isNaN(parsed)) return String(amount)
  return new Intl.NumberFormat('ru-RU', { style: 'currency', currency: 'RUB' }).format(parsed)
}

