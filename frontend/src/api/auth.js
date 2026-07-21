import { ApiError } from './articles.js'

async function readJsonSafely(response) {
  try {
    return await response.json()
  } catch {
    return null
  }
}

async function requestJson(url, options = {}) {
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

export async function registerUser(payload) {
  return await requestJson('/api/auth/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })
}

export async function loginUser(payload) {
  return await requestJson('/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })
}

export async function getProfile(token) {
  return await requestJson('/api/profile', {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })
}

export async function updateProfile(token, payload) {
  return await requestJson('/api/profile', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
  })
}

export async function changePassword(token, payload) {
  return await requestJson('/api/profile/password', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
  })
}
