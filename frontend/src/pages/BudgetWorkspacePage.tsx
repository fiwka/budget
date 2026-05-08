import { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import Highcharts from 'highcharts'
import { HighchartsReact } from 'highcharts-react-official'
import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import { useNavigate, useParams } from 'react-router-dom'
import * as assistantApi from '../api/assistant'
import * as analyticsApi from '../api/analytics'
import * as bankStatementImportApi from '../api/bankStatementImport'
import * as budgetApi from '../api/budget'
import * as categoryApi from '../api/category'
import { queryKeys } from '../api/queryKeys'
import * as transactionApi from '../api/transaction'
import { Pagination } from '../components/Pagination'
import { ThemeSwitcher } from '../components/ThemeSwitcher'
import { useAuth } from '../state/auth'
import { useToast } from '../state/toast'
import type { BudgetRole } from '../types/domain'
import { formatForTable, formatIsoDateToInput, moneyLabel, toIsoFromInput } from '../utils/format'
import { humanizeError, roleLabel } from '../utils/uiText'

type Tab = 'transactions' | 'categories' | 'assistant' | 'analytics' | 'settings'
const allowedTabs: Tab[] = ['transactions', 'categories', 'assistant', 'analytics', 'settings']
type AnalyticsPreset = '3m' | '6m' | '12m' | 'custom'
type AssistantMessage = { id: string; role: 'user' | 'assistant'; content: string }

function toMoneyAmount(raw: FormDataEntryValue | null) {
  const parsed = Number(raw)
  return Math.round((parsed + Number.EPSILON) * 100) / 100
}

function toMonthInputValue(date: Date) {
  return date.toISOString().slice(0, 7)
}

function shiftPeriod(period: string, deltaMonths: number) {
  const [year, month] = period.split('-').map(Number)
  const base = new Date(Date.UTC(year, month - 1 + deltaMonths, 1))
  return `${base.getUTCFullYear()}-${String(base.getUTCMonth() + 1).padStart(2, '0')}`
}

function transactionSource(appendix: unknown) {
  if (appendix && typeof appendix === 'object' && 'source' in appendix) {
    const source = (appendix as { source?: unknown }).source
    if (typeof source === 'string' && source.trim()) return source
  }

  return 'Добавлен вручную'
}

function periodsBetween(from: string, to: string) {
  if (from > to) return []
  const periods: string[] = []
  let cursor = from
  while (cursor <= to && periods.length < 36) {
    periods.push(cursor)
    cursor = shiftPeriod(cursor, 1)
  }
  return periods
}

export function BudgetWorkspacePage() {
  const { budgetId, tab } = useParams<{ budgetId: string; tab: string }>()
  const toast = useToast()
  const { username } = useAuth()
  const navigate = useNavigate()
  const qc = useQueryClient()
  const [txPage, setTxPage] = useState(0)
  const [catPage, setCatPage] = useState(0)
  const [selectedTransactionId, setSelectedTransactionId] = useState<string | null>(null)
  const [selectedCategoryId, setSelectedCategoryId] = useState<string | null>(null)
  const [analyticsPreset, setAnalyticsPreset] = useState<AnalyticsPreset>('6m')
  const [analyticsToPeriod, setAnalyticsToPeriod] = useState(() => toMonthInputValue(new Date()))
  const [analyticsFromPeriod, setAnalyticsFromPeriod] = useState(() => shiftPeriod(toMonthInputValue(new Date()), -5))
  const [chatSessionId, setChatSessionId] = useState<string | null>(null)
  const [assistantInput, setAssistantInput] = useState('')
  const [assistantSending, setAssistantSending] = useState(false)
  const [assistantStatus, setAssistantStatus] = useState('')
  const [bankStatementFileName, setBankStatementFileName] = useState('')
  const [lastImportResult, setLastImportResult] = useState<{
    importedCount: number
    skippedCount: number
    preview: Array<{ transactionId: string; merchantName?: string | null; amount: number }>
  } | null>(null)
  const [assistantMessages, setAssistantMessages] = useState<AssistantMessage[]>([
    {
      id: 'welcome',
      role: 'assistant',
      content: 'Привет! Я помогу с анализом личного бюджета. Спроси, например: **топ расходов за месяц**.',
    },
  ])

  if (!budgetId) return <div className="screen">Budget id is required</div>
  const id = budgetId
  const currentTab: Tab = allowedTabs.includes(tab as Tab) ? (tab as Tab) : 'transactions'

  const analyticsPeriods = useMemo(() => {
    if (analyticsPreset === 'custom') return periodsBetween(analyticsFromPeriod, analyticsToPeriod)
    const presetSize = analyticsPreset === '3m' ? 3 : analyticsPreset === '6m' ? 6 : 12
    return Array.from({ length: presetSize }, (_, idx) => shiftPeriod(analyticsToPeriod, -(presetSize - idx - 1)))
  }, [analyticsFromPeriod, analyticsPreset, analyticsToPeriod])

  const budgetQuery = useQuery({ queryKey: queryKeys.budget(id), queryFn: () => budgetApi.readBudget(id) })
  const transactionsQuery = useQuery({ queryKey: queryKeys.transactions(id, txPage), queryFn: () => transactionApi.listTransactions(id, txPage) })
  const categoriesQuery = useQuery({ queryKey: queryKeys.categories(id, catPage), queryFn: () => categoryApi.listCategories(id, catPage) })
  const analyticsQuery = useQuery({
    queryKey: queryKeys.analytics(id, analyticsPeriods.join(',')),
    enabled: currentTab === 'analytics' && analyticsPeriods.length > 0,
    queryFn: async () => Promise.all(analyticsPeriods.map((period) => analyticsApi.getMonthlySummary(id, period))),
  })
  const membersQuery = useQuery({
    queryKey: queryKeys.budgetMembers(id),
    enabled: currentTab === 'settings',
    queryFn: () => budgetApi.listBudgetMembers(id),
    retry: false,
  })

  const selectedTransaction = transactionsQuery.data?.items.find((x) => x.id === selectedTransactionId) ?? null
  const selectedCategory = categoriesQuery.data?.items.find((x) => x.id === selectedCategoryId) ?? null
  const categoryMap = useMemo(() => new Map((categoriesQuery.data?.items ?? []).map((c) => [c.id, c])), [categoriesQuery.data?.items])
  const currentMember = membersQuery.data?.find((member) => member.username === username) ?? null
  const canAssignAdmin = currentMember?.role === 'OWNER'

  const invalidateCurrent = async () => {
    await Promise.all([
      qc.invalidateQueries({ queryKey: queryKeys.transactions(id, txPage) }),
      qc.invalidateQueries({ queryKey: queryKeys.categories(id, catPage) }),
      qc.invalidateQueries({ queryKey: queryKeys.budget(id) }),
      qc.invalidateQueries({ queryKey: queryKeys.budgetMembers(id) }),
    ])
  }

  const createCategory = useMutation({ mutationFn: categoryApi.createCategory, onSuccess: invalidateCurrent })
  const updateCategory = useMutation({
    mutationFn: ({ id: cid, budgetId: b, name, isConsumption }: { id: string; budgetId: string; name: string; isConsumption: boolean }) =>
      categoryApi.updateCategory(cid, { budgetId: b, name, isConsumption }),
    onSuccess: invalidateCurrent,
  })
  const deleteCategory = useMutation({ mutationFn: categoryApi.deleteCategory, onSuccess: invalidateCurrent })
  const createTx = useMutation({ mutationFn: transactionApi.createTransaction, onSuccess: invalidateCurrent })
  const updateTx = useMutation({
    mutationFn: ({ id: tid, payload }: { id: string; payload: transactionApi.TransactionFields }) =>
      transactionApi.updateTransaction(tid, payload),
    onSuccess: invalidateCurrent,
  })
  const deleteTx = useMutation({ mutationFn: transactionApi.deleteTransaction, onSuccess: invalidateCurrent })
  const importBankStatement = useMutation({
    mutationFn: bankStatementImportApi.importBankStatement,
    onSuccess: async () => {
      await Promise.all([
        invalidateCurrent(),
        qc.invalidateQueries({ queryKey: queryKeys.analytics(id, analyticsPeriods.join(',')) }),
      ])
    },
  })
  const updateBudget = useMutation({
    mutationFn: ({ id: bid, name, description }: { id: string; name: string; description: string }) =>
      budgetApi.updateBudget(bid, { name, description }),
    onSuccess: invalidateCurrent,
  })
  const addMember = useMutation({
    mutationFn: ({ login, role }: { login: string; role: BudgetRole }) => budgetApi.addBudgetMember(id, { login, role }),
    onSuccess: invalidateCurrent,
  })
  const updateMemberRole = useMutation({
    mutationFn: ({ userId, role }: { userId: string; role: BudgetRole }) => budgetApi.updateBudgetMemberRole(id, userId, role),
    onSuccess: invalidateCurrent,
  })
  const removeMember = useMutation({
    mutationFn: (userId: string) => budgetApi.removeBudgetMember(id, userId),
    onSuccess: invalidateCurrent,
  })
  const createAssistantSession = useMutation({ mutationFn: assistantApi.createSession })
  const clearAssistantSession = useMutation({ mutationFn: assistantApi.deleteSession })

  useEffect(() => {
    if (budgetQuery.error) toast.error(humanizeError(budgetQuery.error, 'Не удалось загрузить бюджет.'))
  }, [budgetQuery.error, toast])

  useEffect(() => {
    if (transactionsQuery.error) toast.error(humanizeError(transactionsQuery.error, 'Не удалось загрузить транзакции.'))
  }, [transactionsQuery.error, toast])

  useEffect(() => {
    if (categoriesQuery.error) toast.error(humanizeError(categoriesQuery.error, 'Не удалось загрузить категории.'))
  }, [categoriesQuery.error, toast])

  useEffect(() => {
    if (analyticsQuery.error) toast.error(humanizeError(analyticsQuery.error, 'Не удалось загрузить аналитику.'))
  }, [analyticsQuery.error, toast])

  useEffect(() => {
    if (membersQuery.error && currentTab === 'settings') {
      toast.error(humanizeError(membersQuery.error, 'Не удалось загрузить участников бюджета.'))
    }
  }, [currentTab, membersQuery.error, toast])

  async function onCreateCategory(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const form = new FormData(e.currentTarget)
    try {
      await createCategory.mutateAsync({ budgetId: id, name: String(form.get('name')), isConsumption: String(form.get('isConsumption')) === 'true' })
      e.currentTarget.reset()
      toast.success('Категория создана.')
    } catch (err) {
      toast.error(humanizeError(err, 'Не удалось создать категорию.'))
    }
  }

  async function onUpdateCategory(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    if (!selectedCategory) return
    const form = new FormData(e.currentTarget)
    try {
      await updateCategory.mutateAsync({
        id: selectedCategory.id,
        budgetId: id,
        name: String(form.get('name')),
        isConsumption: String(form.get('isConsumption')) === 'true',
      })
      setSelectedCategoryId(null)
      toast.success('Категория обновлена.')
    } catch (err) {
      toast.error(humanizeError(err, 'Не удалось обновить категорию.'))
    }
  }

  async function onCreateTransaction(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const form = new FormData(e.currentTarget)
    try {
      await createTx.mutateAsync({
        categoryId: String(form.get('categoryId')),
        completedDate: toIsoFromInput(String(form.get('completedDate'))),
        amount: toMoneyAmount(form.get('amount')),
      })
      e.currentTarget.reset()
      toast.success('Транзакция создана.')
    } catch (err) {
      toast.error(humanizeError(err, 'Не удалось создать транзакцию.'))
    }
  }

  async function onUpdateTransaction(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    if (!selectedTransaction) return
    const form = new FormData(e.currentTarget)
    try {
      await updateTx.mutateAsync({
        id: selectedTransaction.id,
        payload: {
          categoryId: String(form.get('categoryId')),
          completedDate: toIsoFromInput(String(form.get('completedDate'))),
          amount: toMoneyAmount(form.get('amount')),
        },
      })
      setSelectedTransactionId(null)
      toast.success('Транзакция обновлена.')
    } catch (err) {
      toast.error(humanizeError(err, 'Не удалось обновить транзакцию.'))
    }
  }

  async function onImportBankStatement(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const formElement = e.currentTarget
    const form = new FormData(formElement)
    const file = form.get('file')
    if (!(file instanceof File) || file.size === 0) {
      toast.error('Выберите PDF-файл выписки.')
      return
    }

    try {
      const response = await importBankStatement.mutateAsync({
        budgetId: id,
        file,
        bank: 'VTB',
        format: 'PDF',
        zoneId: String(form.get('zoneId') || Intl.DateTimeFormat().resolvedOptions().timeZone || ''),
      })
      setLastImportResult({
        importedCount: response.importedCount,
        skippedCount: response.skippedCount,
        preview: response.transactions.slice(0, 5).map((item) => ({
          transactionId: item.transactionId,
          merchantName: item.merchantName,
          amount: item.amount,
        })),
      })
      setBankStatementFileName('')
      formElement.reset()
      toast.success(`Импортировано: ${response.importedCount}. Пропущено дублей: ${response.skippedCount}.`)
    } catch (err) {
      toast.error(humanizeError(err, 'Не удалось импортировать выписку.'))
    }
  }

  async function sendAssistantMessage() {
    const message = assistantInput.trim()
    if (!message || assistantSending) return

    const userMessage: AssistantMessage = { id: `u-${Date.now()}`, role: 'user', content: message }
    const assistantMessageId = `a-${Date.now()}`
    setAssistantInput('')
    setAssistantSending(true)
    setAssistantStatus('Отправляю сообщение...')
    setAssistantMessages((prev) => [...prev, userMessage, { id: assistantMessageId, role: 'assistant', content: '' }])

    try {
      let sessionId = chatSessionId
      if (!sessionId) {
        const created = await createAssistantSession.mutateAsync(id)
        sessionId = created.sessionId
        setChatSessionId(sessionId)
      }

      await assistantApi.streamMessage(
        sessionId,
        message,
        (chunk) => {
          setAssistantStatus('')
          setAssistantMessages((prev) =>
            prev.map((item) => (item.id === assistantMessageId ? { ...item, content: item.content + chunk } : item))
          )
        },
        setAssistantStatus
      )
    } catch (err) {
      setAssistantMessages((prev) =>
        prev.map((item) =>
          item.id === assistantMessageId
            ? { ...item, content: `Не удалось получить ответ: ${humanizeError(err, 'Ошибка ассистента.')}` }
            : item
        )
      )
      toast.error(humanizeError(err, 'Не удалось отправить сообщение ассистенту.'))
    } finally {
      setAssistantSending(false)
      setAssistantStatus('')
    }
  }

  async function onClearAssistantSession() {
    try {
      if (chatSessionId) {
        await clearAssistantSession.mutateAsync(chatSessionId)
      }
      setChatSessionId(null)
      setAssistantMessages([
        {
          id: `welcome-${Date.now()}`,
          role: 'assistant',
          content: 'Сессия очищена. Готов продолжать работу с бюджетом.',
        },
      ])
      toast.success('Сессия ассистента очищена.')
    } catch (err) {
      toast.error(humanizeError(err, 'Не удалось очистить сессию ассистента.'))
    }
  }

  async function onAddMember(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const form = new FormData(e.currentTarget)
    try {
      await addMember.mutateAsync({
        login: String(form.get('login')).trim(),
        role: String(form.get('role')) as BudgetRole,
      })
      e.currentTarget.reset()
      toast.success('Участник добавлен.')
    } catch (err) {
      toast.error(humanizeError(err, 'Не удалось добавить участника.'))
    }
  }

  async function onChangeMemberRole(userId: string, role: BudgetRole) {
    try {
      await updateMemberRole.mutateAsync({ userId, role })
      toast.success('Роль участника обновлена.')
    } catch (err) {
      toast.error(humanizeError(err, 'Не удалось изменить роль участника.'))
    }
  }

  async function onRemoveMember(userId: string) {
    try {
      await removeMember.mutateAsync(userId)
      toast.success('Участник удален из бюджета.')
    } catch (err) {
      toast.error(humanizeError(err, 'Не удалось удалить участника.'))
    }
  }

  const analyticsSeries = analyticsQuery.data ?? []
  const periodLabels = analyticsSeries.map((item) =>
    new Intl.DateTimeFormat('ru-RU', { month: 'short', year: 'numeric' }).format(new Date(`${item.period}-01T00:00:00Z`))
  )
  const analyticsTotals = analyticsSeries.reduce(
    (acc, item) => ({
      income: acc.income + Number(item.income),
      expenses: acc.expenses + Number(item.expenses),
      balance: acc.balance + Number(item.balance),
    }),
    { income: 0, expenses: 0, balance: 0 }
  )
  const avgSavingsRate = analyticsSeries.length
    ? analyticsSeries.reduce((acc, item) => acc + Number(item.savingsRate), 0) / analyticsSeries.length
    : 0
  const latestSummary = analyticsSeries[analyticsSeries.length - 1]
  const isDarkTheme = document.documentElement.dataset.theme === 'dark'
  const chartTextColor = isDarkTheme ? '#e6edf7' : '#1b1f2a'
  const chartMutedTextColor = isDarkTheme ? '#c4d0e2' : '#3d475a'
  const chartGridColor = isDarkTheme ? '#324158' : '#c8d2df'
  const legendStyle = { color: chartTextColor, fontWeight: '600' }
  const axisLabelStyle = { color: chartMutedTextColor, fontSize: '12px' }
  const axisTitleStyle = { color: chartTextColor, fontWeight: '600' }
  const pieData = (latestSummary?.topExpenseCategories ?? [])
    .map((item) => ({
      name: categoryMap.get(item.categoryId)?.name ?? item.categoryId.slice(0, 8),
      y: Math.abs(Number(item.total)),
    }))
    .filter((item) => Number.isFinite(item.y) && item.y > 0)

  return (
    <main className="screen">
      <header className="topbar">
        <div>
          <button onClick={() => navigate('/budgets')}>{'<- К бюджетам'}</button>
          <h1>{budgetQuery.data?.name ?? 'Бюджет'}</h1>
          <p>{budgetQuery.data?.description}</p>
        </div>
        <ThemeSwitcher />
      </header>

      <nav className="tabs">
        <button className={currentTab === 'transactions' ? 'active' : ''} onClick={() => navigate(`/budgets/${id}/transactions`)}>Транзакции</button>
        <button className={currentTab === 'categories' ? 'active' : ''} onClick={() => navigate(`/budgets/${id}/categories`)}>Категории</button>
        <button className={currentTab === 'assistant' ? 'active' : ''} onClick={() => navigate(`/budgets/${id}/assistant`)}>ИИ-ассистент</button>
        <button className={currentTab === 'analytics' ? 'active' : ''} onClick={() => navigate(`/budgets/${id}/analytics`)}>Аналитика</button>
        <button className={currentTab === 'settings' ? 'active' : ''} onClick={() => navigate(`/budgets/${id}/settings`)}>Настройки</button>
      </nav>

      {currentTab === 'transactions' && transactionsQuery.data && (
        <section className="grid-2">
          <article className="card"><h3>Новая транзакция</h3><form className="form-grid" onSubmit={(e) => void onCreateTransaction(e)}>
            <label>Категория<select name="categoryId" required><option value="">Выберите...</option>{(categoriesQuery.data?.items ?? []).map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}</select></label>
            <label>Дата и время<input name="completedDate" type="datetime-local" required /></label>
            <label>Сумма<input name="amount" type="number" step="0.01" min="0.01" required /></label>
            <button type="submit">Создать</button></form></article>

          <article className="card">
            <h3>Импорт выписки</h3>
            <form className="form-grid" onSubmit={(e) => void onImportBankStatement(e)}>
              <label>
                Банк
                <select name="bank" defaultValue="VTB" disabled>
                  <option value="VTB">ВТБ</option>
                </select>
              </label>
              <label>
                Файл PDF
                <input
                  name="file"
                  type="file"
                  accept="application/pdf,.pdf"
                  required
                  onChange={(event) => setBankStatementFileName(event.currentTarget.files?.[0]?.name ?? '')}
                />
              </label>
              <input type="hidden" name="zoneId" value={Intl.DateTimeFormat().resolvedOptions().timeZone} />
              {bankStatementFileName && <p className="muted import-file-name">{bankStatementFileName}</p>}
              <button type="submit" disabled={importBankStatement.isPending}>
                {importBankStatement.isPending ? 'Импорт...' : 'Импортировать'}
              </button>
            </form>
            {lastImportResult && (
              <div className="import-result">
                <div className="row">
                  <span className="badge">Добавлено: {lastImportResult.importedCount}</span>
                  <span className="badge">Дубли: {lastImportResult.skippedCount}</span>
                </div>
                {lastImportResult.preview.length > 0 && (
                  <ul>
                    {lastImportResult.preview.map((item) => (
                      <li key={item.transactionId}>
                        <span>{item.merchantName ?? 'Операция'}</span>
                        <strong>{moneyLabel(item.amount)}</strong>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            )}
          </article>

          {selectedTransaction && <article className="card"><h3>Редактирование транзакции</h3><form className="form-grid" onSubmit={(e) => void onUpdateTransaction(e)}>
            <label>Категория<select name="categoryId" defaultValue={selectedTransaction.categoryId} required>{(categoriesQuery.data?.items ?? []).map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}</select></label>
            <label>Дата и время<input name="completedDate" defaultValue={formatIsoDateToInput(selectedTransaction.completedDate)} type="datetime-local" required /></label>
            <label>Сумма<input name="amount" defaultValue={selectedTransaction.amount} type="number" step="0.01" min="0.01" required /></label>
            <div className="row"><button type="submit">Сохранить</button><button type="button" onClick={() => setSelectedTransactionId(null)}>Отмена</button></div>
          </form></article>}

          <article className="card span-2"><h3>Транзакции</h3><table><thead><tr><th>Дата</th><th>Категория</th><th>Сумма</th><th>Источник</th><th>Действия</th></tr></thead><tbody>{transactionsQuery.data.items.map((item) => <tr key={item.id}><td>{formatForTable(item.completedDate)}</td><td>{categoryMap.get(item.categoryId)?.name ?? item.categoryId}</td><td>{moneyLabel(item.amount)}</td><td>{transactionSource(item.appendix)}</td><td className="row"><button onClick={() => setSelectedTransactionId(item.id)}>Редактировать</button><button onClick={async () => {
            try {
              await deleteTx.mutateAsync(item.id)
              toast.success('Транзакция удалена.')
            } catch (err) {
              toast.error(humanizeError(err, 'Не удалось удалить транзакцию.'))
            }
          }}>Удалить</button></td></tr>)}</tbody></table><Pagination page={transactionsQuery.data} onPageChange={setTxPage} /></article>
        </section>
      )}

      {currentTab === 'categories' && categoriesQuery.data && (
        <section className="grid-2">
          <article className="card"><h3>Новая категория</h3><form className="form-grid" onSubmit={(e) => void onCreateCategory(e)}><label>Название<input name="name" minLength={2} maxLength={255} required /></label><label>Тип<select name="isConsumption" defaultValue="true"><option value="true">Расход</option><option value="false">Доход</option></select></label><button type="submit">Создать</button></form></article>
          {selectedCategory && <article className="card"><h3>Редактирование категории</h3><form className="form-grid" onSubmit={(e) => void onUpdateCategory(e)}><label>Название<input name="name" defaultValue={selectedCategory.name} minLength={2} maxLength={255} required /></label><label>Тип<select name="isConsumption" defaultValue={String(selectedCategory.isConsumption)}><option value="true">Расход</option><option value="false">Доход</option></select></label><div className="row"><button type="submit">Сохранить</button><button type="button" onClick={() => setSelectedCategoryId(null)}>Отмена</button></div></form></article>}
          <article className="card span-2"><h3>Категории</h3><table><thead><tr><th>Название</th><th>Тип</th><th>Действия</th></tr></thead><tbody>{categoriesQuery.data.items.map((item) => <tr key={item.id}><td>{item.name}</td><td>{item.isConsumption ? 'Расход' : 'Доход'}</td><td className="row"><button onClick={() => setSelectedCategoryId(item.id)}>Редактировать</button><button onClick={async () => {
            try {
              await deleteCategory.mutateAsync(item.id)
              toast.success('Категория удалена.')
            } catch (err) {
              toast.error(humanizeError(err, 'Не удалось удалить категорию.'))
            }
          }}>Удалить</button></td></tr>)}</tbody></table><Pagination page={categoriesQuery.data} onPageChange={setCatPage} /></article>
        </section>
      )}

      {currentTab === 'assistant' && (
        <section className="card assistant-card">
          <div className="assistant-header">
            <div>
              <h3>ИИ-ассистент</h3>
              <p className="muted">Контекст привязан к текущему бюджету. Поддерживается Markdown.</p>
            </div>
            <button type="button" onClick={() => void onClearAssistantSession()} disabled={assistantSending}>
              Очистить сессию
            </button>
          </div>
          <div className="assistant-chat">
            {assistantMessages.map((item) => (
              <div key={item.id} className={`assistant-line ${item.role === 'assistant' ? 'assistant-bot' : 'assistant-user'}`}>
                {item.role === 'assistant' ? (
                  <ReactMarkdown remarkPlugins={[remarkGfm]}>
                    {item.content || (assistantSending ? assistantStatus || 'Жду первый фрагмент ответа...' : '')}
                  </ReactMarkdown>
                ) : (
                  <p>{item.content}</p>
                )}
              </div>
            ))}
          </div>
          <form
            className="assistant-input"
            onSubmit={(e) => {
              e.preventDefault()
              void sendAssistantMessage()
            }}
          >
            <textarea
              value={assistantInput}
              onChange={(e) => setAssistantInput(e.target.value)}
              placeholder="Спроси про баланс, топ расходов, категории, тренды..."
              maxLength={4000}
              disabled={assistantSending}
            />
            <button type="submit" disabled={assistantSending || !assistantInput.trim()}>
              {assistantSending ? 'Отправка...' : 'Отправить'}
            </button>
          </form>
        </section>
      )}

      {currentTab === 'analytics' && (
        <section className="card analytics-dashboard">
          <h3>Аналитика</h3>
          <div className="analytics-controls">
            <div className="segmented">
              <button className={analyticsPreset === '3m' ? 'active' : ''} onClick={() => setAnalyticsPreset('3m')}>3 месяца</button>
              <button className={analyticsPreset === '6m' ? 'active' : ''} onClick={() => setAnalyticsPreset('6m')}>6 месяцев</button>
              <button className={analyticsPreset === '12m' ? 'active' : ''} onClick={() => setAnalyticsPreset('12m')}>12 месяцев</button>
              <button className={analyticsPreset === 'custom' ? 'active' : ''} onClick={() => setAnalyticsPreset('custom')}>Свой диапазон</button>
            </div>
            <div className="row">
              <label>До<input type="month" value={analyticsToPeriod} onChange={(e) => setAnalyticsToPeriod(e.target.value)} /></label>
              {analyticsPreset === 'custom' && (
                <label>От<input type="month" value={analyticsFromPeriod} onChange={(e) => setAnalyticsFromPeriod(e.target.value)} /></label>
              )}
            </div>
          </div>

          <div className="analytics-kpi">
            <article className="kpi kpi-income"><span>Доход</span><strong>{moneyLabel(analyticsTotals.income)}</strong></article>
            <article className="kpi kpi-expense"><span>Расход</span><strong>{moneyLabel(analyticsTotals.expenses)}</strong></article>
            <article className="kpi kpi-balance"><span>Баланс</span><strong>{moneyLabel(analyticsTotals.balance)}</strong></article>
            <article className="kpi kpi-savings"><span>Средняя норма сбережений</span><strong>{avgSavingsRate.toFixed(2)}%</strong></article>
          </div>

          {analyticsQuery.isLoading && <p className="muted">Загружаем аналитику...</p>}
          {!analyticsQuery.isLoading && analyticsSeries.length > 0 && (
            <div className="charts-row charts-real">
              <HighchartsReact
                highcharts={Highcharts}
                options={{
                  chart: { type: 'line', backgroundColor: 'transparent', height: 300 },
                  title: { text: 'Доходы, расходы и баланс', style: { color: chartTextColor, fontWeight: '700' } },
                  legend: { itemStyle: legendStyle, itemHoverStyle: legendStyle },
                  xAxis: { categories: periodLabels, labels: { style: axisLabelStyle }, lineColor: chartGridColor, tickColor: chartGridColor },
                  yAxis: {
                    title: { text: 'Сумма (RUB)', style: axisTitleStyle },
                    labels: { style: axisLabelStyle },
                    gridLineColor: chartGridColor,
                  },
                  credits: { enabled: false },
                  series: [
                    { type: 'line', name: 'Доход', data: analyticsSeries.map((x) => Number(x.income)), color: '#16a34a' },
                    { type: 'line', name: 'Расход', data: analyticsSeries.map((x) => Number(x.expenses)), color: '#dc2626' },
                    { type: 'line', name: 'Баланс', data: analyticsSeries.map((x) => Number(x.balance)), color: '#2563eb' },
                  ],
                }}
              />
              <HighchartsReact
                highcharts={Highcharts}
                options={{
                  chart: { type: 'column', backgroundColor: 'transparent', height: 300 },
                  title: { text: 'Норма сбережений по месяцам', style: { color: chartTextColor, fontWeight: '700' } },
                  legend: { itemStyle: legendStyle, itemHoverStyle: legendStyle },
                  xAxis: { categories: periodLabels, labels: { style: axisLabelStyle }, lineColor: chartGridColor, tickColor: chartGridColor },
                  yAxis: {
                    title: { text: '%', style: axisTitleStyle },
                    labels: { style: axisLabelStyle },
                    gridLineColor: chartGridColor,
                  },
                  credits: { enabled: false },
                  series: [{ type: 'column', name: 'Savings rate', data: analyticsSeries.map((x) => Number(x.savingsRate)), color: '#0b8a6d' }],
                }}
              />
              <HighchartsReact
                highcharts={Highcharts}
                options={{
                  chart: { type: 'pie', backgroundColor: 'transparent', height: 300 },
                  title: { text: `Топ категорий расходов (${latestSummary?.period ?? ''})`, style: { color: chartTextColor, fontWeight: '700' } },
                  legend: { itemStyle: legendStyle, itemHoverStyle: legendStyle },
                  tooltip: {
                    style: { color: chartTextColor },
                  },
                  credits: { enabled: false },
                  plotOptions: {
                    pie: {
                      dataLabels: {
                        enabled: true,
                        style: { color: chartTextColor, textOutline: 'none', fontWeight: '600' },
                        format: '{point.name}: {point.percentage:.1f}%',
                      },
                    },
                  },
                  series: [
                    {
                      type: 'pie',
                      name: 'Доля',
                      data: pieData,
                    },
                  ],
                }}
              />
            </div>
          )}
          {!analyticsQuery.isLoading && analyticsSeries.length > 0 && pieData.length === 0 && (
            <p className="muted">Для pie-диаграммы нет корректных данных (только нули или пусто).</p>
          )}
          {!analyticsQuery.isLoading && analyticsSeries.length === 0 && <p className="muted">Нет данных за выбранный период.</p>}
        </section>
      )}

      {currentTab === 'settings' && budgetQuery.data && (
        <section className="grid-2">
          <article className="card">
            <h3>Настройки бюджета</h3>
            <form className="form-grid" onSubmit={async (e) => {
              e.preventDefault()
              const form = new FormData(e.currentTarget)
              try {
                await updateBudget.mutateAsync({ id, name: String(form.get('name')), description: String(form.get('description')) })
                toast.success('Настройки бюджета сохранены.')
              } catch (err) {
                toast.error(humanizeError(err, 'Не удалось сохранить настройки бюджета.'))
              }
            }}>
              <label>Название<input name="name" defaultValue={budgetQuery.data.name} minLength={4} maxLength={255} required /></label>
              <label>Описание<textarea name="description" defaultValue={budgetQuery.data.description} maxLength={1500} required /></label>
              <button type="submit">Сохранить</button>
            </form>
          </article>

          <article className="card">
            <h3>Добавить участника</h3>
            <form className="form-grid" onSubmit={(e) => void onAddMember(e)}>
              <label>Username или email<input name="login" maxLength={255} required /></label>
              <label>Роль<select name="role" defaultValue="READER">
                <option value="READER">{roleLabel('READER')}</option>
                <option value="EDITOR">{roleLabel('EDITOR')}</option>
                {canAssignAdmin && <option value="ADMIN">{roleLabel('ADMIN')}</option>}
              </select></label>
              <button type="submit" disabled={addMember.isPending}>Добавить</button>
            </form>
            <p className="muted">Владельца передать нельзя. Администраторов назначает и меняет только владелец.</p>
          </article>

          <article className="card span-2">
            <h3>Участники и роли</h3>
            {membersQuery.isLoading && <p className="muted">Загружаем участников...</p>}
            {membersQuery.error && <p className="muted">Управление участниками доступно администраторам и владельцу.</p>}
            {membersQuery.data && (
              <table>
                <thead><tr><th>Пользователь</th><th>Email</th><th>Роль</th><th>Действия</th></tr></thead>
                <tbody>{membersQuery.data.map((member) => {
                  const isSelf = member.username === username
                  const isOwner = member.role === 'OWNER'
                  const canEditMember = !isSelf && !isOwner && (canAssignAdmin || member.role !== 'ADMIN')
                  return (
                    <tr key={member.userId}>
                      <td>{member.username}</td>
                      <td>{member.email}</td>
                      <td>{roleLabel(member.role)}</td>
                      <td className="row">
                        <select
                          value={member.role}
                          disabled={!canEditMember || updateMemberRole.isPending}
                          onChange={(e) => void onChangeMemberRole(member.userId, e.target.value as BudgetRole)}
                        >
                          <option value="READER">{roleLabel('READER')}</option>
                          <option value="EDITOR">{roleLabel('EDITOR')}</option>
                          {(canAssignAdmin || member.role === 'ADMIN') && <option value="ADMIN">{roleLabel('ADMIN')}</option>}
                          {member.role === 'OWNER' && <option value="OWNER">{roleLabel('OWNER')}</option>}
                        </select>
                        <button
                          type="button"
                          disabled={!canEditMember || removeMember.isPending}
                          onClick={() => void onRemoveMember(member.userId)}
                        >
                          Удалить
                        </button>
                      </td>
                    </tr>
                  )
                })}</tbody>
              </table>
            )}
          </article>
        </section>
      )}
    </main>
  )
}
