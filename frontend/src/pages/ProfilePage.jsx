import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-toastify'
import ArticleList from '../components/ArticleList.jsx'
import { useAuth } from '../context/AuthContext.jsx'

const EMAIL_REGEX = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/
const IRANIAN_PHONE_REGEX = /^(\+98|0)?9\d{9}$/
const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{6,}$/

export default function ProfilePage() {
  const { user, updateUserProfile, changeUserPassword, logout, refreshProfile } = useAuth()
  const navigate = useNavigate()

  const [email, setEmail] = useState('')
  const [phone, setPhone] = useState('')
  const [updatingProfile, setUpdatingProfile] = useState(false)

  const [oldPassword, setOldPassword] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [updatingPassword, setUpdatingPassword] = useState(false)

  useEffect(() => {
    refreshProfile()
  }, [])

  useEffect(() => {
    if (user) {
      setEmail(user.email || '')
      setPhone(user.phone || '')
    }
  }, [user])

  async function handleUpdateProfile(e) {
    e.preventDefault()

    const trimmedEmail = email.trim()
    const trimmedPhone = phone.trim()

    if (!trimmedEmail && !trimmedPhone) {
      toast.error('At least one of Email or Phone Number is required.')
      return
    }

    if (trimmedEmail && !EMAIL_REGEX.test(trimmedEmail)) {
      toast.error('Invalid email format.')
      return
    }

    if (trimmedPhone && !IRANIAN_PHONE_REGEX.test(trimmedPhone)) {
      toast.error('Invalid Iranian mobile phone number format.')
      return
    }

    setUpdatingProfile(true)
    try {
      await updateUserProfile(trimmedEmail, trimmedPhone)
      toast.success('Profile updated successfully.')
    } catch (err) {
      toast.error(err?.message || 'Failed to update profile.')
    } finally {
      setUpdatingProfile(false)
    }
  }

  async function handleChangePassword(e) {
    e.preventDefault()

    if (!oldPassword || !newPassword) {
      toast.error('Both old and new passwords are required.')
      return
    }

    if (!PASSWORD_REGEX.test(newPassword)) {
      toast.error('New password must be at least 6 characters long and contain at least one uppercase letter, one lowercase letter, and one digit.')
      return
    }

    setUpdatingPassword(true)
    try {
      await changeUserPassword(oldPassword, newPassword)
      toast.success('Password changed successfully.')
      setOldPassword('')
      setNewPassword('')
    } catch (err) {
      toast.error(err?.message || 'Failed to change password.')
    } finally {
      setUpdatingPassword(false)
    }
  }

  function handleLogout() {
    logout()
    toast.info('Logged out.')
    navigate('/login')
  }

  if (!user) {
    return <p className="muted">Loading profile...</p>
  }

  return (
    <div className="profile-container">
      <div className="profile-header">
        <h1>User Profile</h1>
        <button onClick={handleLogout} className="button-logout">
          Logout
        </button>
      </div>

      <div className="profile-card">
        <h2>Account Information</h2>
        <p><strong>Username:</strong> {user.username}</p>
        <p><strong>Display Name:</strong> {user.name}</p>
        <p><strong>Email:</strong> {user.email || 'Not provided'}</p>
        <p><strong>Phone:</strong> {user.phone || 'Not provided'}</p>
      </div>

      <div className="profile-section">
        <h2>Edit Email & Phone</h2>
        <form onSubmit={handleUpdateProfile} className="auth-form">
          <label>
            Email
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              disabled={updatingProfile}
              placeholder="user@example.com"
            />
          </label>

          <label>
            Phone Number
            <input
              type="text"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              disabled={updatingProfile}
              placeholder="09123456789"
            />
          </label>

          <button type="submit" disabled={updatingProfile}>
            {updatingProfile ? 'Saving...' : 'Update Contact Info'}
          </button>
        </form>
      </div>

      <div className="profile-section">
        <h2>Change Password</h2>
        <form onSubmit={handleChangePassword} className="auth-form">
          <label>
            Old Password
            <input
              type="password"
              value={oldPassword}
              onChange={(e) => setOldPassword(e.target.value)}
              disabled={updatingPassword}
              required
            />
          </label>

          <label>
            New Password
            <input
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              disabled={updatingPassword}
              required
            />
            <small className="muted">Minimum 6 characters, including uppercase, lowercase, and digit.</small>
          </label>

          <button type="submit" disabled={updatingPassword}>
            {updatingPassword ? 'Changing...' : 'Change Password'}
          </button>
        </form>
      </div>

      <div className="profile-section">
        <h2>My Published Articles</h2>
        <ArticleList
          articles={user.articles}
          emptyText="You have not published any articles yet."
        />
      </div>
    </div>
  )
}
