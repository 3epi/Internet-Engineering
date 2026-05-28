import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { toast } from 'react-toastify'

import ArticleList from '../components/ArticleList.jsx'
import { listArticles, searchArticles } from '../api/articles.js'

export default function HomePage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()

  const q = useMemo(() => (searchParams.get('q') || '').trim(), [searchParams])

  const [loading, setLoading] = useState(false)
  const [newest, setNewest] = useState([])
  const [searchResult, setSearchResult] = useState(null)

  const [inputValue, setInputValue] = useState(q)

  useEffect(() => {
    setInputValue(q)
  }, [q])

  useEffect(() => {
    let cancelled = false

    async function load() {
      setLoading(true)
      try {
        if (!q) {
          const data = await listArticles()
          if (!cancelled) {
            setNewest(Array.isArray(data) ? data : [])
            setSearchResult(null)
          }
        } else {
          const data = await searchArticles(q)
          if (!cancelled) {
            setSearchResult(data)
          }
        }
      } catch (err) {
        if (!cancelled) {
          toast.error(err?.message || 'Failed to load articles.')
        }
      } finally {
        if (!cancelled) setLoading(false)
      }
    }

    load()

    return () => {
      cancelled = true
    }
  }, [q])

  function onSubmit(e) {
    e.preventDefault()
    const next = (inputValue || '').trim()
    if (next) {
      navigate(`/?q=${encodeURIComponent(next)}`)
    } else {
      navigate('/')
    }
  }

  return (
    <div>
      <h1>Articles</h1>

      <form onSubmit={onSubmit}>
        <label>
          Search
          <input
            name="q"
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
          />
        </label>
        <button type="submit" disabled={loading}>
          Search
        </button>
      </form>

      {loading ? (
        <p className="muted">Loading...</p>
      ) : !q ? (
        <>
          <h2>Newest</h2>
          <ArticleList articles={newest} emptyText="No articles." />
        </>
      ) : (
        <>
          <h2>Results for: {q}</h2>

          <h3>Matches in title</h3>
          <ArticleList
            articles={searchResult?.titleMatches}
            emptyText="No matches in title."
          />

          <h3>Matches in abstract</h3>
          <ArticleList
            articles={searchResult?.abstractOnlyMatches}
            emptyText="No matches in abstract."
          />
        </>
      )}
    </div>
  )
}
