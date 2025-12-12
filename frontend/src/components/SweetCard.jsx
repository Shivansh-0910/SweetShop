import { useState } from 'react'

function SweetCard({ sweet, onPurchase, isAdmin, onUpdate, onDelete }) {
  const [quantity, setQuantity] = useState(1)
  const [purchasing, setPurchasing] = useState(false)

  const handlePurchase = async () => {
    if (quantity < 1 || quantity > sweet.quantity) {
      alert('Invalid quantity')
      return
    }

    setPurchasing(true)
    try {
      await onPurchase(sweet.id, quantity)
      setQuantity(1)
    } finally {
      setPurchasing(false)
    }
  }

  return (
    <div className="sweet-card">
      <h3>{sweet.name}</h3>
      <span className="sweet-category">{sweet.category}</span>
      <div className="sweet-price">${sweet.price.toFixed(2)}</div>
      <div className={`sweet-quantity ${sweet.quantity === 0 ? 'out-of-stock' : ''}`}>
        {sweet.quantity === 0 ? 'Out of Stock' : `${sweet.quantity} available`}
      </div>

      {!isAdmin && (
        <div className="sweet-actions">
          <input
            type="number"
            min="1"
            max={sweet.quantity}
            value={quantity}
            onChange={(e) => setQuantity(parseInt(e.target.value) || 1)}
            disabled={sweet.quantity === 0}
          />
          <button
            className="btn btn-success"
            onClick={handlePurchase}
            disabled={sweet.quantity === 0 || purchasing}
            style={{ flex: 1 }}
          >
            {purchasing ? 'Buying...' : 'Purchase'}
          </button>
        </div>
      )}

      {isAdmin && (
        <div className="sweet-actions">
          <button
            className="btn btn-primary"
            onClick={() => onUpdate(sweet)}
            style={{ flex: 1 }}
          >
            Edit
          </button>
          <button
            className="btn btn-danger"
            onClick={() => onDelete(sweet.id)}
          >
            Delete
          </button>
        </div>
      )}
    </div>
  )
}

export default SweetCard
