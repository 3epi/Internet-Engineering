async function readJsonSafely(response) {
  try {
    return await response.json()
  } catch {
    return null
  }
}

export class ApiError extends Error {
  constructor(status, message) {
    super(message)
    this.name = 'ApiError'
    this.status = status
  }
}

async function requestJson(url, options) {
  const response = await fetch(url, options)
  if (response.ok) {
    return await readJsonSafely(response)
  }

  const data = await readJsonSafely(response)
  const message =
    (data && typeof data.message === 'string' && data.message) ||
    `Request failed with status ${response.status}.`

  throw new ApiError(response.status, message)
}

export async function listArticles() {
  return await requestJson('/api/articles', { method: 'GET' })
}

export async function getArticleByTitle(title) {
  const encoded = encodeURIComponent(title)
  return await requestJson(`/api/articles?title=${encoded}`, { method: 'GET' })
}

export async function searchArticles(q) {
  const encoded = encodeURIComponent(q)
  return await requestJson(`/api/articles/search?q=${encoded}`, { method: 'GET' })
}

export async function createArticle(payload) {
  return await requestJson('/api/articles', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })
}
