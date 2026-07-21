import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-toastify'
import { createArticle } from '../api/articles.js'
import { useAuth } from '../context/AuthContext.jsx'

export default function AddArticlePage() {
  const navigate = useNavigate()
  const { token, user } = useAuth()

  const [title, setTitle] = useState('')
  const [abstractText, setAbstractText] = useState('')
  const [body, setBody] = useState('')
  const [categoryName, setCategoryName] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function onSubmit(e) {
    e.preventDefault()

    setSubmitting(true)
    try {
      const created = await createArticle(token, { title, abstractText, body, categoryName })
      toast.success('Article added successfully.')

      const nextTitle = created?.title || title
      navigate(`/article?title=${encodeURIComponent(nextTitle)}`)
    } catch (err) {
      toast.error(err?.message || 'Failed to add article.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div>
      <h1>Add Article</h1>

      <form onSubmit={onSubmit}>
        <label>
          Author
          <input
            name="authorName"
            value={user?.name || ''}
            disabled
            className="read-only-input"
          />
        </label>

        <label>
          Title
          <input
            name="title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            disabled={submitting}
            required
          />
        </label>

        <label>
          Abstract
          <textarea
            name="abstract"
            rows={4}
            value={abstractText}
            onChange={(e) => setAbstractText(e.target.value)}
            disabled={submitting}
            required
          />
        </label>

        <label>
          Body
          <textarea
            name="body"
            rows={10}
            value={body}
            onChange={(e) => setBody(e.target.value)}
            disabled={submitting}
            required
          />
        </label>

        <label>
          Category
          <input
            name="categoryName"
            value={categoryName}
            onChange={(e) => setCategoryName(e.target.value)}
            disabled={submitting}
          />
        </label>

        <button type="submit" disabled={submitting}>
          {submitting ? 'Adding...' : 'Add'}
        </button>
      </form>
    </div>
  )
}
