import { createContext, useContext, useMemo } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import * as authApi from '../api/auth'
import { queryKeys } from '../api/queryKeys'

const sessionRefreshIntervalMs = 4 * 60 * 1000

type AuthContextValue = {
  initialized: boolean
  isAuthenticated: boolean
  username: string | null
  login: (login: string, password: string) => Promise<void>
  register: (username: string, email: string, password: string) => Promise<void>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const qc = useQueryClient()

  const sessionQuery = useQuery({
    queryKey: queryKeys.session,
    queryFn: authApi.keepSessionAlive,
    retry: false,
    refetchInterval: (query) => query.state.data?.authenticated ? sessionRefreshIntervalMs : false,
    refetchIntervalInBackground: true,
  })

  const userQuery = useQuery({
    queryKey: queryKeys.user,
    queryFn: authApi.getUserInfo,
    enabled: !!sessionQuery.data?.authenticated,
  })

  const loginMutation = useMutation({
    mutationFn: ({ login, password }: { login: string; password: string }) => authApi.login(login, password),
    onSuccess: async () => {
      await qc.invalidateQueries({ queryKey: queryKeys.session })
      await qc.invalidateQueries({ queryKey: queryKeys.user })
    },
  })

  const registerMutation = useMutation({
    mutationFn: ({ username, email, password }: { username: string; email: string; password: string }) =>
      authApi.register(username, email, password),
  })

  const logoutMutation = useMutation({
    mutationFn: authApi.logout,
    onSuccess: async () => {
      qc.removeQueries({ queryKey: queryKeys.user })
      await qc.invalidateQueries({ queryKey: queryKeys.session })
    },
  })

  const value = useMemo<AuthContextValue>(
    () => ({
      initialized: !sessionQuery.isLoading,
      isAuthenticated: !!sessionQuery.data?.authenticated,
      username: userQuery.data?.username ?? null,
      login: async (login, password) => loginMutation.mutateAsync({ login, password }).then(() => undefined),
      register: async (u, e, p) => registerMutation.mutateAsync({ username: u, email: e, password: p }).then(() => undefined),
      logout: async () => logoutMutation.mutateAsync().then(() => undefined),
    }),
    [sessionQuery.isLoading, sessionQuery.data?.authenticated, userQuery.data?.username, loginMutation, registerMutation, logoutMutation],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider')
  return ctx
}
