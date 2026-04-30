import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import * as budgetApi from '../api/budget'
import { queryKeys } from '../api/queryKeys'
import { Pagination } from '../components/Pagination'
import { ThemeSwitcher } from '../components/ThemeSwitcher'
import { useAuth } from '../state/auth'
import { useToast } from '../state/toast'
import type { AccessibleBudget } from '../types/domain'
import { humanizeError, roleLabel } from '../utils/uiText'

export function BudgetsPage() {
  const { logout, username } = useAuth()
  const toast = useToast()
  const navigate = useNavigate()
  const qc = useQueryClient()
  const [pageNum, setPageNum] = useState(0)
  const [editing, setEditing] = useState<AccessibleBudget | null>(null)

  const budgetsQuery = useQuery({
    queryKey: queryKeys.budgets(pageNum),
    queryFn: () => budgetApi.listBudgets(pageNum),
  })

  const createMutation = useMutation({
    mutationFn: budgetApi.createBudget,
    onSuccess: async () => qc.invalidateQueries({ queryKey: ['budgets'] }),
  })

  const updateMutation = useMutation({
    mutationFn: ({ id, name, description }: { id: string; name: string; description: string }) =>
      budgetApi.updateBudget(id, { name, description }),
    onSuccess: async () => qc.invalidateQueries({ queryKey: ['budgets'] }),
  })

  const deleteMutation = useMutation({
    mutationFn: budgetApi.deleteBudget,
    onSuccess: async () => qc.invalidateQueries({ queryKey: ['budgets'] }),
  })

  useEffect(() => {
    if (budgetsQuery.error) {
      toast.error(humanizeError(budgetsQuery.error, 'Не удалось загрузить бюджеты.'))
    }
  }, [budgetsQuery.error, toast])

  async function onCreate(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const form = new FormData(e.currentTarget)
    try {
      await createMutation.mutateAsync({ name: String(form.get('name')), description: String(form.get('description')) })
      e.currentTarget.reset()
      toast.success('Бюджет создан.')
    } catch (err) {
      toast.error(humanizeError(err, 'Не удалось создать бюджет.'))
    }
  }

  async function onUpdate(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    if (!editing) return
    const form = new FormData(e.currentTarget)
    try {
      await updateMutation.mutateAsync({ id: editing.id, name: String(form.get('name')), description: String(form.get('description')) })
      setEditing(null)
      toast.success('Бюджет обновлен.')
    } catch (err) {
      toast.error(humanizeError(err, 'Не удалось обновить бюджет.'))
    }
  }

  const page = budgetsQuery.data

  return (
    <main className="screen">
      <header className="topbar">
        <div><h1>Бюджеты</h1><p>{username ? `Пользователь: ${username}` : ''}</p></div>
        <div className="row"><ThemeSwitcher /><button onClick={() => void logout()}>Выйти</button></div>
      </header>

      <section className="grid-2">
        <article className="card">
          <h3>Создать бюджет</h3>
          <form className="form-grid" onSubmit={(e) => void onCreate(e)}>
            <label>Название<input name="name" minLength={4} maxLength={255} required /></label>
            <label>Описание<textarea name="description" maxLength={1500} required /></label>
            <button type="submit">Создать</button>
          </form>
        </article>

        {editing && (
          <article className="card">
            <h3>Редактирование бюджета</h3>
            <form className="form-grid" onSubmit={(e) => void onUpdate(e)}>
              <label>Название<input name="name" defaultValue={editing.name} minLength={4} maxLength={255} required /></label>
              <label>Описание<textarea name="description" defaultValue={editing.description} maxLength={1500} required /></label>
              <div className="row"><button type="submit">Сохранить</button><button type="button" onClick={() => setEditing(null)}>Отмена</button></div>
            </form>
          </article>
        )}
      </section>

      <section className="card">
        <h3>Список доступных бюджетов</h3>
        {budgetsQuery.isLoading && <p>Загрузка...</p>}
        {page && (
          <>
            <table>
              <thead><tr><th>Название</th><th>Описание</th><th>Роль</th><th>Действия</th></tr></thead>
              <tbody>
                {page.items.map((item) => (
                  <tr key={item.id}>
                    <td>{item.name}</td><td>{item.description}</td><td>{roleLabel(item.role)}</td>
                    <td className="row">
                      <button onClick={() => navigate(`/budgets/${item.id}`)}>Открыть</button>
                      <button onClick={() => setEditing(item)}>Редактировать</button>
                      <button
                        onClick={async () => {
                          try {
                            await deleteMutation.mutateAsync(item.id)
                            toast.success('Бюджет удален.')
                          } catch (err) {
                            toast.error(humanizeError(err, 'Не удалось удалить бюджет.'))
                          }
                        }}
                      >
                        Удалить
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <Pagination page={page} onPageChange={setPageNum} />
          </>
        )}
      </section>
    </main>
  )
}
