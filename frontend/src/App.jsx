import { Link, Navigate, Route, Routes } from 'react-router-dom'
import { ToastContainer } from 'react-toastify'

import { AuthProvider, useAuth } from './context/AuthContext.jsx'
import AddArticlePage from './pages/AddArticlePage.jsx'
import ArticlePage from './pages/ArticlePage.jsx'
import HomePage from './pages/HomePage.jsx'
import LoginPage from './pages/LoginPage.jsx'
import NotFoundPage from './pages/NotFoundPage.jsx'
import ProfilePage from './pages/ProfilePage.jsx'
import RegisterPage from './pages/RegisterPage.jsx'

function ProtectedRoute({ children }) {
  const { isAuthenticated, loading } = useAuth()

  if (loading) {
    return <div className="muted" style={{ padding: '2rem' }}>Loading...</div>
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  return children
}

function NavigationBar() {
  const { isAuthenticated, user, logout } = useAuth()

  return (
    <header className="header">
      <nav className="nav">
        <Link to="/">Home</Link>
        {isAuthenticated ? (
          <>
            <Link to="/add">Add article</Link>
            <Link to="/profile">Profile ({user?.name || user?.username})</Link>
            <button onClick={logout} className="nav-logout-btn">
              Logout
            </button>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}
      </nav>
    </header>
  )
}

function AppContent() {
  return (
    <div className="app">
      <NavigationBar />

      <main className="main">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/article" element={<ArticlePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route
            path="/add"
            element={
              <ProtectedRoute>
                <AddArticlePage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/profile"
            element={
              <ProtectedRoute>
                <ProfilePage />
              </ProtectedRoute>
            }
          />
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </main>

      <ToastContainer position="top-right" autoClose={2500} />
    </div>
  )
}

export default function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  )
}
