# Sweet Shop Frontend

React-based single-page application for the Sweet Shop Management System.

## Features

- 🔐 User authentication (Login/Register)
- 🍬 Browse sweets with search and filters
- 🛒 Purchase sweets with quantity selection
- 👨‍💼 Admin panel for inventory management
- 📱 Responsive design
- 🎨 Modern UI with gradient backgrounds

## Tech Stack

- **React 18** - UI library
- **Vite** - Build tool and dev server
- **React Router** - Client-side routing
- **Axios** - HTTP client
- **CSS3** - Styling

## Setup Instructions

### Prerequisites

- Node.js 16+ and npm

### Installation

```bash
cd frontend
npm install
```

### Running the Application

```bash
npm run dev
```

The application will be available at: **http://localhost:3000**

### Building for Production

```bash
npm run build
```

The build output will be in the `dist` folder.

## Project Structure

```
frontend/
├── src/
│   ├── api/
│   │   └── axios.js          # API client configuration
│   ├── components/
│   │   ├── AdminPanel.jsx    # Admin inventory management
│   │   ├── Auth.jsx          # Login/Register forms
│   │   ├── Dashboard.jsx     # Main sweet browsing page
│   │   ├── Navbar.jsx        # Navigation bar
│   │   └── SweetCard.jsx     # Sweet display card
│   ├── App.jsx               # Main app component
│   ├── main.jsx              # Entry point
│   └── index.css             # Global styles
├── index.html                # HTML template
├── vite.config.js            # Vite configuration
└── package.json              # Dependencies
```

## Features by User Role

### Regular Users
- Register and login
- Browse all available sweets
- Search by name, category, price range
- Purchase sweets (disabled when out of stock)
- View real-time quantity updates

### Admin Users
- All regular user features
- Access to admin panel
- Add new sweets
- Update sweet details
- Delete sweets
- Restock inventory

## API Integration

The frontend connects to the Spring Boot backend at `http://localhost:8080/api`.

All authenticated requests include JWT token in the Authorization header.

## Environment Variables

The Vite proxy is configured to forward `/api` requests to `http://localhost:8080`.

To change the backend URL, edit `vite.config.js`:

```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://your-backend-url',
      changeOrigin: true
    }
  }
}
```

## Screenshots

### Login Page
Clean authentication interface with email/password fields.

### Dashboard
Grid layout displaying all sweets with search and filter options.

### Admin Panel
Comprehensive inventory management with add, edit, delete, and restock functionality.

## Development Notes

- The app uses localStorage to persist authentication tokens
- Automatic redirect to login on 401 responses
- Form validation for all inputs
- Responsive design for mobile and desktop
- Loading states for async operations

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

---

Built with ❤️ using React and Vite
