import { useTheme } from '../state/theme'

export function ThemeSwitcher() {
  const { mode, setMode } = useTheme()

  return (
    <label className="theme-switcher">
      Тема
      <select value={mode} onChange={(e) => setMode(e.target.value as 'system' | 'light' | 'dark')}>
        <option value="system">Системная</option>
        <option value="light">Светлая</option>
        <option value="dark">Тёмная</option>
      </select>
    </label>
  )
}

