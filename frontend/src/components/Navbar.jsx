import { Link } from 'react-router-dom'

function Navbar({ user, onLogout }) {
  return (
    <nav className="navbar">
      <Link to="/" style={{ textDecoration: 'none' }}>
        <h1>🍬 Sweet Shop</h1>
      </Link>
      <div className="nav-links">
        {user && (
          <>
            <span>Welcome, {user.email}</span>
            {user.role === 'ADMIN' && (
              <Link to="/admin">
                <button className="btn btn-secondary">Admin Panel</button>
              </Link>
            )}
            <button className="btn btn-primary" onClick={onLogout}>
              Logout
            </button>
          </>
        )}
      </div>
    </nav>
  )
}

export default Navbar
