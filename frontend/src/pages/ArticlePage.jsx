import { useEffect, useMemo, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { toast } from 'react-toastify'

import { getArticleByTitle } from '../api/articles.js'

function formatDate(iso) {
  if (!iso) return ''
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return iso
  return d.toLocaleString()
}

export default function ArticlePage() {
  const [searchParams] = useSearchParams()

  const title = useMemo(() => (searchParams.get('title') || '').trim(), [searchParams])

  const [loading, setLoading] = useState(false)
  const [article, setArticle] = useState(null)

  useEffect(() => {
    let cancelled = false

    async function load() {
      if (!title) return

      setLoading(true)
      try {
        const data = await getArticleByTitle(title)
        if (!cancelled) setArticle(data)
      } catch (err) {
        if (!cancelled) toast.error(err?.message || 'Failed to load article.')
      } finally {
        if (!cancelled) setLoading(false)
      }
    }

    load()

    return () => {
      cancelled = true
    }
  }, [title])

  if (!title) {
    return (
      <div>
        <p className="muted">Missing title.</p>
        <p>
          <Link to="/">Back</Link>
        </p>
      </div>
    )
  }

  return (
    <div>
      <p>
        <Link to="/">Back</Link>
      </p>

      {loading ? (
        <p className="muted">Loading...</p>
      ) : !article ? (
        <p className="muted">No article.</p>
      ) : (
        <>
          <h1>{article.title}</h1>
          <p className="muted">Published at: {formatDate(article.createdAt)}</p>
          <p className="muted">Author: {article.authorName || 'Unknown Author'}</p>
          <p className="muted">Category: {article.categoryName || 'General'}</p>

          <h2>Abstract</h2>
          <p>{article.abstractText}</p>

          <h2>Body</h2>
          <pre>{article.body}</pre>
        </>
      )}
    </div>
  )
}
