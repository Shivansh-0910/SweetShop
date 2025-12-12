import { useState, useEffect } from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import Navbar from './components/Navbar'
import Auth from './components/Auth'
import Dashboard from './components/Dashboard'
import AdminPanel from './components/AdminPanel'

function App() {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    // Check if user is logged in
    const token = localStorage.getItem('token')
    const email = localStorage.getItem('email')
    const role = localStorage.getItem('role')

    if (token && email && role) {
      setUser({ email, role, token })
    }
    setLoading(false)
  }, [])

  const handleLogin = (userData) => {
    localStorage.setItem('token', userData.token)
    localStorage.setItem('email', userData.email)
    localStorage.setItem('role', userData.role)
    setUser(userData)
  }

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('email')
    localStorage.removeItem('role')
    setUser(null)
  }

  if (loading) {
    return <div className="loading">Loading...</div>
  }

  return (
    <Router>
      <div className="App">
        <Navbar user={user} onLogout={handleLogout} />
        <Routes>
          <Route
            path="/auth"
            element={
              user ? <Navigate to="/" /> : <Auth onLogin={handleLogin} />
            }
          />
          <Route
            path="/"
            element={
              user ? (
                <Dashboard user={user} />
              ) : (
                <Navigate to="/auth" />
              )
            }
          />
          <Route
            path="/admin"
            element={
              user && user.role === 'ADMIN' ? (
                <AdminPanel user={user} />
              ) : (
                <Navigate to="/" />
              )
            }
          />
        </Routes>
      </div>
    </Router>
  )
}

export default App
