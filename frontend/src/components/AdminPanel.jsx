import { useState, useEffect } from 'react'
import api from '../api/axios'
import SweetCard from './SweetCard'

function AdminPanel({ user }) {
  const [sweets, setSweets] = useState([])
  const [loading, setLoading] = useState(true)
  const [editingSweet, setEditingSweet] = useState(null)
  
  // Form state
  const [name, setName] = useState('')
  const [category, setCategory] = useState('')
  const [price, setPrice] = useState('')
  const [quantity, setQuantity] = useState('')
  const [restockQuantity, setRestockQuantity] = useState('')

  useEffect(() => {
    fetchSweets()
  }, [])

  const fetchSweets = async () => {
    try {
      const response = await api.get('/sweets')
      setSweets(response.data)
      setLoading(false)
    } catch (error) {
      console.error('Error fetching sweets:', error)
      setLoading(false)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    
    try {
      const sweetData = {
        name,
        category,
        price: parseFloat(price),
        quantity: parseInt(quantity)
      }

      if (editingSweet) {
        await api.put(`/sweets/${editingSweet.id}`, sweetData)
      } else {
        await api.post('/sweets', sweetData)
      }

      resetForm()
      fetchSweets()
    } catch (error) {
      alert(error.response?.data?.message || 'Operation failed')
    }
  }

  const handleUpdate = (sweet) => {
    setEditingSweet(sweet)
    setName(sweet.name)
    setCategory(sweet.category)
    setPrice(sweet.price.toString())
    setQuantity(sweet.quantity.toString())
  }

  const handleDelete = async (id) => {
    if (!confirm('Are you sure you want to delete this sweet?')) return

    try {
      await api.delete(`/sweets/${id}`)
      fetchSweets()
    } catch (error) {
      alert(error.response?.data?.message || 'Delete failed')
    }
  }

  const handleRestock = async (sweetId) => {
    if (!restockQuantity || restockQuantity < 1) {
      alert('Please enter a valid quantity')
      return
    }

    try {
      await api.post(`/sweets/${sweetId}/restock`, {
        quantity: parseInt(restockQuantity)
      })
      setRestockQuantity('')
      fetchSweets()
    } catch (error) {
      alert(error.response?.data?.message || 'Restock failed')
    }
  }

  const resetForm = () => {
    setEditingSweet(null)
    setName('')
    setCategory('')
    setPrice('')
    setQuantity('')
  }

  if (loading) {
    return <div className="loading">Loading...</div>
  }

  return (
    <div className="container">
      <div className="dashboard">
        <div className="dashboard-header">
          <h2>🔧 Admin Panel</h2>
          <p>Manage your sweet inventory</p>
        </div>

        <div className="admin-panel">
          <h3>{editingSweet ? 'Edit Sweet' : 'Add New Sweet'}</h3>
          <form onSubmit={handleSubmit}>
            <div className="admin-form">
              <input
                type="text"
                placeholder="Name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
              <input
                type="text"
                placeholder="Category"
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                required
              />
              <input
                type="number"
                placeholder="Price"
                value={price}
                onChange={(e) => setPrice(e.target.value)}
                step="0.01"
                min="0"
                required
              />
              <input
                type="number"
                placeholder="Quantity"
                value={quantity}
                onChange={(e) => setQuantity(e.target.value)}
                min="0"
                required
              />
            </div>
            <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
              <button type="submit" className="btn btn-primary">
                {editingSweet ? 'Update Sweet' : 'Add Sweet'}
              </button>
              {editingSweet && (
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={resetForm}
                >
                  Cancel
                </button>
              )}
            </div>
          </form>
        </div>

        <div className="admin-panel" style={{ marginTop: '2rem' }}>
          <h3>Restock Sweet</h3>
          <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
            <select
              style={{ flex: 1, padding: '0.8rem', borderRadius: '6px', border: '2px solid #e5e7eb' }}
              onChange={(e) => {
                const sweetId = e.target.value
                if (sweetId) {
                  const sweet = sweets.find(s => s.id === parseInt(sweetId))
                  if (sweet) handleRestock(sweet.id)
                }
              }}
              value=""
            >
              <option value="">Select sweet to restock</option>
              {sweets.map(sweet => (
                <option key={sweet.id} value={sweet.id}>
                  {sweet.name} (Current: {sweet.quantity})
                </option>
              ))}
            </select>
            <input
              type="number"
              placeholder="Quantity to add"
              value={restockQuantity}
              onChange={(e) => setRestockQuantity(e.target.value)}
              min="1"
              style={{ width: '150px', padding: '0.8rem', borderRadius: '6px', border: '2px solid #e5e7eb' }}
            />
          </div>
        </div>

        <h3 style={{ color: 'white', marginTop: '2rem', marginBottom: '1rem' }}>
          Current Inventory
        </h3>
        {sweets.length === 0 ? (
          <div className="empty-state">
            <h3>No sweets in inventory</h3>
            <p>Add your first sweet using the form above</p>
          </div>
        ) : (
          <div className="sweets-grid">
            {sweets.map(sweet => (
              <SweetCard
                key={sweet.id}
                sweet={sweet}
                isAdmin={true}
                onUpdate={handleUpdate}
                onDelete={handleDelete}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

export default AdminPanel
