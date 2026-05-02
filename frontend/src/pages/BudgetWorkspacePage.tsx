import { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import Highcharts from 'highcharts'
import { HighchartsReact } from 'highcharts-react-official'
import { useNavigate, useParams } from 'react-router-dom'
import * as analyticsApi from '../api/analytics'
import * as budgetApi from '../api/budget'
import * as categoryApi from '../api/category'
import { queryKeys } from '../api/queryKeys'
import * as transactionApi from '../api/transaction'
import { Pagination } from '../components/Pagination'
import { ThemeSwitcher } from '../components/ThemeSwitcher'
import { useToast } from '../state/toast'
import { formatForTable, formatIsoDateToInput, moneyLabel, toIsoFromInput } from '../utils/format'
import { humanizeError } from '../utils/uiText'

type Tab = 'transactions' | 'categories' | 'assistant' | 'analytics' | 'settings'
const allowedTabs: Tab[] = ['transactions', 'categories', 'assistant', 'analytics', 'settings']
type AnalyticsPreset = '3m' | '6m' | '12m' | 'custom'

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
  const navigate = useNavigate()
  const qc = useQueryClient()
  const [txPage, setTxPage] = useState(0)
  const [catPage, setCatPage] = useState(0)
  const [selectedTransactionId, setSelectedTransactionId] = useState<string | null>(null)
  const [selectedCategoryId, setSelectedCategoryId] = useState<string | null>(null)
  const [analyticsPreset, setAnalyticsPreset] = useState<AnalyticsPreset>('6m')
  const [analyticsToPeriod, setAnalyticsToPeriod] = useState(() => toMonthInputValue(new Date()))
  const [analyticsFromPeriod, setAnalyticsFromPeriod] = useState(() => shiftPeriod(toMonthInputValue(new Date()), -5))

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

  const selectedTransaction = transactionsQuery.data?.items.find((x) => x.id === selectedTransactionId) ?? null
  const selectedCategory = categoriesQuery.data?.items.find((x) => x.id === selectedCategoryId) ?? null
  const categoryMap = useMemo(() => new Map((categoriesQuery.data?.items ?? []).map((c) => [c.id, c])), [categoriesQuery.data?.items])

  const invalidateCurrent = async () => {
    await Promise.all([
      qc.invalidateQueries({ queryKey: queryKeys.transactions(id, txPage) }),
      qc.invalidateQueries({ queryKey: queryKeys.categories(id, catPage) }),
      qc.invalidateQueries({ queryKey: queryKeys.budget(id) }),
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
  const updateBudget = useMutation({
    mutationFn: ({ id: bid, name, description }: { id: string; name: string; description: string }) =>
      budgetApi.updateBudget(bid, { name, description }),
    onSuccess: invalidateCurrent,
  })

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

          {selectedTransaction && <article className="card"><h3>Редактирование транзакции</h3><form className="form-grid" onSubmit={(e) => void onUpdateTransaction(e)}>
            <label>Категория<select name="categoryId" defaultValue={selectedTransaction.categoryId} required>{(categoriesQuery.data?.items ?? []).map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}</select></label>
            <label>Дата и время<input name="completedDate" defaultValue={formatIsoDateToInput(selectedTransaction.completedDate)} type="datetime-local" required /></label>
            <label>Сумма<input name="amount" defaultValue={selectedTransaction.amount} type="number" step="0.01" min="0.01" required /></label>
            <div className="row"><button type="submit">Сохранить</button><button type="button" onClick={() => setSelectedTransactionId(null)}>Отмена</button></div>
          </form></article>}

          <article className="card span-2"><h3>Транзакции</h3><table><thead><tr><th>Дата</th><th>Категория</th><th>Сумма</th><th>Действия</th></tr></thead><tbody>{transactionsQuery.data.items.map((item) => <tr key={item.id}><td>{formatForTable(item.completedDate)}</td><td>{categoryMap.get(item.categoryId)?.name ?? item.categoryId}</td><td>{moneyLabel(item.amount)}</td><td className="row"><button onClick={() => setSelectedTransactionId(item.id)}>Редактировать</button><button onClick={async () => {
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

      {currentTab === 'assistant' && <section className="card"><h3>ИИ-ассистент</h3><p>Интерфейс подготовлен. Логика чата будет подключаться через API Gateway отдельным endpoint-ом.</p><div className="chat-mock"><div className="chat-line bot">Привет! Я помогу с анализом бюджета.</div><div className="chat-line user">Покажи топ-3 категории расходов за месяц.</div><div className="chat-line bot">Функционал скоро будет доступен.</div></div></section>}

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
            <article className="kpi"><span>Доход</span><strong>{moneyLabel(analyticsTotals.income)}</strong></article>
            <article className="kpi"><span>Расход</span><strong>{moneyLabel(analyticsTotals.expenses)}</strong></article>
            <article className="kpi"><span>Баланс</span><strong>{moneyLabel(analyticsTotals.balance)}</strong></article>
            <article className="kpi"><span>Средняя норма сбережений</span><strong>{avgSavingsRate.toFixed(2)}%</strong></article>
          </div>

          {analyticsQuery.isLoading && <p className="muted">Загружаем аналитику...</p>}
          {!analyticsQuery.isLoading && analyticsSeries.length > 0 && (
            <div className="charts-row charts-real">
              <HighchartsReact
                highcharts={Highcharts}
                options={{
                  chart: { type: 'line', backgroundColor: 'transparent' },
                  title: { text: 'Доходы, расходы и баланс' },
                  xAxis: { categories: periodLabels },
                  yAxis: { title: { text: 'Сумма (RUB)' } },
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
                  chart: { type: 'column', backgroundColor: 'transparent' },
                  title: { text: 'Норма сбережений по месяцам' },
                  xAxis: { categories: periodLabels },
                  yAxis: { title: { text: '%' } },
                  credits: { enabled: false },
                  series: [{ type: 'column', name: 'Savings rate', data: analyticsSeries.map((x) => Number(x.savingsRate)), color: '#0b8a6d' }],
                }}
              />
              <HighchartsReact
                highcharts={Highcharts}
                options={{
                  chart: { type: 'pie', backgroundColor: 'transparent' },
                  title: { text: `Топ категорий расходов (${latestSummary?.period ?? ''})` },
                  credits: { enabled: false },
                  series: [
                    {
                      type: 'pie',
                      name: 'Доля',
                      data: (latestSummary?.topExpenseCategories ?? []).map((item) => ({
                        name: categoryMap.get(item.categoryId)?.name ?? item.categoryId.slice(0, 8),
                        y: Number(item.total),
                      })),
                    },
                  ],
                }}
              />
            </div>
          )}
          {!analyticsQuery.isLoading && analyticsSeries.length === 0 && <p className="muted">Нет данных за выбранный период.</p>}
        </section>
      )}

      {currentTab === 'settings' && budgetQuery.data && <section className="card"><h3>Настройки бюджета</h3><form className="form-grid" onSubmit={async (e) => {
        e.preventDefault()
        const form = new FormData(e.currentTarget)
        try {
          await updateBudget.mutateAsync({ id, name: String(form.get('name')), description: String(form.get('description')) })
          toast.success('Настройки бюджета сохранены.')
        } catch (err) {
          toast.error(humanizeError(err, 'Не удалось сохранить настройки бюджета.'))
        }
      }}><label>Название<input name="name" defaultValue={budgetQuery.data.name} minLength={4} maxLength={255} required /></label><label>Описание<textarea name="description" defaultValue={budgetQuery.data.description} maxLength={1500} required /></label><button type="submit">Сохранить</button></form><p className="muted">Участники/роли: UI будет расширен на следующем этапе.</p></section>}
    </main>
  )
}
