import type { Page } from '../types/domain'

type Props<T> = {
  page: Page<T>
  onPageChange: (next: number) => void
}

export function Pagination<T>({ page, onPageChange }: Props<T>) {
  return (
    <div className="pagination">
      <button disabled={page.page <= 0} onClick={() => onPageChange(page.page - 1)}>
        Назад
      </button>
      <span>
        Страница {page.page + 1} / {Math.max(page.totalPages, 1)}
      </span>
      <button disabled={page.page + 1 >= page.totalPages} onClick={() => onPageChange(page.page + 1)}>
        Вперёд
      </button>
    </div>
  )
}

