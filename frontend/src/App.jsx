import { Link, Route, Routes } from 'react-router-dom'
import { ToastContainer } from 'react-toastify'

import HomePage from './pages/HomePage.jsx'
import AddArticlePage from './pages/AddArticlePage.jsx'
import ArticlePage from './pages/ArticlePage.jsx'
import NotFoundPage from './pages/NotFoundPage.jsx'

function App() {
  return (
    <div className="app">
      <header className="header">
        <nav className="nav">
          <Link to="/">Home</Link>
          <Link to="/add">Add article</Link>
        </nav>
      </header>

      <main className="main">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/add" element={<AddArticlePage />} />
          <Route path="/article" element={<ArticlePage />} />
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </main>

      <ToastContainer position="top-right" autoClose={2500} />
    </div>
  )
}

export default App
