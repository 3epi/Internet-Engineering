import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-toastify'

import { createArticle } from '../api/articles.js'

export default function AddArticlePage() {
  const navigate = useNavigate()

  const [title, setTitle] = useState('')
  const [abstractText, setAbstractText] = useState('')
  const [body, setBody] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function onSubmit(e) {
    e.preventDefault()

    setSubmitting(true)
    try {
      const created = await createArticle({ title, abstractText, body })
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
          Title
          <input
            name="title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            disabled={submitting}
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
          />
        </label>

        <button type="submit" disabled={submitting}>
          Add
        </button>
      </form>
    </div>
  )
}
