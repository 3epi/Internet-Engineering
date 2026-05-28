import { Link } from 'react-router-dom'

function formatDate(iso) {
  if (!iso) return ''
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return iso
  return d.toLocaleString()
}

export default function ArticleList({ articles, emptyText }) {
  if (!articles || articles.length === 0) {
    return <p className="muted">{emptyText || 'No articles.'}</p>
  }

  return (
    <ul>
      {articles.map((a) => (
        <li key={a.title}>
          <Link to={`/article?title=${encodeURIComponent(a.title)}`}>{a.title}</Link>{' '}
          <small className="muted">({formatDate(a.createdAt)})</small>
        </li>
      ))}
    </ul>
  )
}
