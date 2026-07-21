import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { toast } from 'react-toastify'
import { useAuth } from '../context/AuthContext.jsx'

const EMAIL_REGEX = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/
const IRANIAN_PHONE_REGEX = /^(\+98|0)?9\d{9}$/
const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{6,}$/

export default function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()

  const [username, setUsername] = useState('')
  const [name, setName] = useState('')
  const [password, setPassword] = useState('')
  const [email, setEmail] = useState('')
  const [phone, setPhone] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function onSubmit(e) {
    e.preventDefault()

    const trimmedUsername = username.trim()
    const trimmedName = name.trim()
    const trimmedEmail = email.trim()
    const trimmedPhone = phone.trim()

    if (!trimmedUsername) {
      toast.error('Username is required.')
      return
    }

    if (!trimmedName) {
      toast.error('Display Name is required.')
      return
    }

    if (!PASSWORD_REGEX.test(password)) {
      toast.error('Password must be at least 6 characters long and contain at least one uppercase letter, one lowercase letter, and one digit.')
      return
    }

    if (!trimmedEmail && !trimmedPhone) {
      toast.error('At least one of Email or Phone Number is required.')
      return
    }

    if (trimmedEmail && !EMAIL_REGEX.test(trimmedEmail)) {
      toast.error('Invalid email format.')
      return
    }

    if (trimmedPhone && !IRANIAN_PHONE_REGEX.test(trimmedPhone)) {
      toast.error('Invalid Iranian mobile phone number format (e.g. 09123456789).')
      return
    }

    setSubmitting(true)
    try {
      await register(trimmedUsername, trimmedName, password, trimmedEmail, trimmedPhone)
      toast.success('Registration successful!')
      navigate('/')
    } catch (err) {
      toast.error(err?.message || 'Registration failed.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="auth-container">
      <h1>Register</h1>
      <form onSubmit={onSubmit} className="auth-form">
        <label>
          Username *
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            disabled={submitting}
            required
          />
        </label>

        <label>
          Display Name *
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            disabled={submitting}
            required
          />
        </label>

        <label>
          Password *
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            disabled={submitting}
            required
          />
          <small className="muted">Minimum 6 characters, including uppercase, lowercase, and digit.</small>
        </label>

        <label>
          Email (Optional if Phone provided)
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            disabled={submitting}
            placeholder="user@example.com"
          />
        </label>

        <label>
          Phone Number (Optional if Email provided)
          <input
            type="text"
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
            disabled={submitting}
            placeholder="09123456789"
          />
        </label>

        <button type="submit" disabled={submitting}>
          {submitting ? 'Registering...' : 'Register'}
        </button>
      </form>

      <p className="auth-switch">
        Already have an account? <Link to="/login">Login here</Link>
      </p>
    </div>
  )
}
