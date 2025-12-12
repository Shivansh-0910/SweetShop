import { useState, useEffect } from 'react'
import api from '../api/axios'
import SweetCard from './SweetCard'

function Dashboard({ user }) {
  const [sweets, setSweets] = useState([])
  const [filteredSweets, setFilteredSweets] = useState([])
  const [loading, setLoading] = useState(true)
  const [searchName, setSearchName] = useState('')
  const [searchCategory, setSearchCategory] = useState('')
  const [minPrice, setMinPrice] = useState('')
  const [maxPrice, setMaxPrice] = useState('')

  useEffect(() => {
    fetchSweets()
  }, [])

  useEffect(() => {
    filterSweets()
  }, [sweets, searchName, searchCategory, minPrice, maxPrice])

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

  const filterSweets = () => {
    let filtered = [...sweets]

    if (searchName) {
      filtered = filtered.filter(sweet =>
        sweet.name.toLowerCase().includes(searchName.toLowerCase())
      )
    }

    if (searchCategory) {
      filtered = filtered.filter(sweet =>
        sweet.category.toLowerCase().includes(searchCategory.toLowerCase())
      )
    }

    if (minPrice) {
      filtered = filtered.filter(sweet => sweet.price >= parseFloat(minPrice))
    }

    if (maxPrice) {
      filtered = filtered.filter(sweet => sweet.price <= parseFloat(maxPrice))
    }

    setFilteredSweets(filtered)
  }

  const handlePurchase = async (sweetId, quantity) => {
    try {
      await api.post(`/sweets/${sweetId}/purchase`, { quantity })
      fetchSweets() // Refresh the list
    } catch (error) {
      alert(error.response?.data?.message || 'Purchase failed')
    }
  }

  const categories = [...new Set(sweets.map(s => s.category))]

  if (loading) {
    return <div className="loading">Loading sweets...</div>
  }

  return (
    <div className="container">
      <div className="dashboard">
        <div className="dashboard-header">
          <h2>🍬 Available Sweets</h2>
          <p>Browse our delicious collection of sweets</p>
        </div>

        <div className="search-bar">
          <input
            type="text"
            placeholder="Search by name..."
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
          />
          <select
            value={searchCategory}
            onChange={(e) => setSearchCategory(e.target.value)}
          >
            <option value="">All Categories</option>
            {categories.map(cat => (
              <option key={cat} value={cat}>{cat}</option>
            ))}
          </select>
          <input
            type="number"
            placeholder="Min Price"
            value={minPrice}
            onChange={(e) => setMinPrice(e.target.value)}
            step="0.01"
          />
          <input
            type="number"
            placeholder="Max Price"
            value={maxPrice}
            onChange={(e) => setMaxPrice(e.target.value)}
            step="0.01"
          />
        </div>

        {filteredSweets.length === 0 ? (
          <div className="empty-state">
            <h3>No sweets found</h3>
            <p>Try adjusting your search filters</p>
          </div>
        ) : (
          <div className="sweets-grid">
            {filteredSweets.map(sweet => (
              <SweetCard
                key={sweet.id}
                sweet={sweet}
                onPurchase={handlePurchase}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

export default Dashboard
