import { createContext, useContext, useEffect, useState } from 'react'
import {
  changePassword,
  getProfile,
  loginUser,
  registerUser,
  updateProfile,
} from '../api/auth.js'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('token') || null)
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let active = true

    async function loadUser() {
      if (!token) {
        if (active) {
          setUser(null)
          setLoading(false)
        }
        return
      }

      try {
        const profile = await getProfile(token)
        if (active) {
          setUser(profile)
        }
      } catch {
        if (active) {
          localStorage.removeItem('token')
          setToken(null)
          setUser(null)
        }
      } finally {
        if (active) {
          setLoading(false)
        }
      }
    }

    loadUser()

    return () => {
      active = false
    }
  }, [token])

  async function login(username, password) {
    const res = await loginUser({ username, password })
    localStorage.setItem('token', res.token)
    setToken(res.token)
    setUser({
      username: res.username,
      name: res.name,
      email: res.email,
      phone: res.phone,
      articles: [],
    })
    return res
  }

  async function register(username, name, password, email, phone) {
    const res = await registerUser({ username, name, password, email, phone })
    localStorage.setItem('token', res.token)
    setToken(res.token)
    setUser({
      username: res.username,
      name: res.name,
      email: res.email,
      phone: res.phone,
      articles: [],
    })
    return res
  }

  function logout() {
    localStorage.removeItem('token')
    setToken(null)
    setUser(null)
  }

  async function updateUserProfile(email, phone) {
    if (!token) throw new Error('Not authenticated')
    const updated = await updateProfile(token, { email, phone })
    setUser(updated)
    return updated
  }

  async function changeUserPassword(oldPassword, newPassword) {
    if (!token) throw new Error('Not authenticated')
    await changePassword(token, { oldPassword, newPassword })
  }

  async function refreshProfile() {
    if (!token) return
    try {
      const profile = await getProfile(token)
      setUser(profile)
    } catch {
      // ignore
    }
  }

  const value = {
    token,
    user,
    loading,
    login,
    register,
    logout,
    updateUserProfile,
    changeUserPassword,
    refreshProfile,
    isAuthenticated: Boolean(token && user),
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}
