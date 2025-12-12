# Design Document

## Overview

The Sweet Shop Management System is a RESTful API built with Spring Boot that provides e-commerce functionality for a sweet shop. The system follows a layered architecture with clear separation between presentation (controllers), business logic (services), and data access (repositories). Authentication is handled via JWT tokens, and authorization is enforced through Spring Security with role-based access control.

The backend uses PostgreSQL for data persistence with Flyway for database migrations. The system is designed to be stateless, with all authentication state contained in JWT tokens, making it horizontally scalable.

## Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Client Layer                          │
│                   (React Frontend / API Clients)             │
└────────────────────────────┬────────────────────────────────┘
                             │ HTTPS/REST
┌────────────────────────────▼────────────────────────────────┐
│                     Controller Layer                         │
│              (AuthController, SweetController)               │
│                    - Request validation                      │
│                    - Response formatting                     │
│                    - HTTP status mapping                     │
└────────────────────────────┬────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────┐
│                      Security Layer                          │
│              (JWT Filter, Security Config)                   │
│                    - Token validation                        │
│                    - Role-based authorization                │
└────────────────────────────┬────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────┐
│                      Service Layer                           │
│            (AuthService, SweetService)                       │
│                    - Business logic                          │
│                    - Transaction management                  │
│                    - Domain validation                       │
└────────────────────────────┬────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────┐
│                    Repository Layer                          │
│            (UserRepository, SweetRepository)                 │
│                    - Data access                             │
│                    - Query methods                           │
└────────────────────────────┬────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────┐
│                    PostgreSQL Database                       │
│                    - Users table                             │
│                    - Sweets table                            │
└─────────────────────────────────────────────────────────────┘
```

### Package Structure

```
com.sweetshop
├── config
│   ├── SecurityConfig.java          # Spring Security configuration
│   └── JwtConfig.java                # JWT properties
├── controller
│   ├── AuthController.java           # Authentication endpoints
│   └── SweetController.java          # Sweet management endpoints
├── dto
│   ├── RegisterRequest.java          # Registration input
│   ├── LoginRequest.java             # Login input
│   ├── LoginResponse.java            # JWT token response
│   ├── SweetRequest.java             # Sweet creation/update input
│   ├── SweetResponse.java            # Sweet output
│   ├── PurchaseRequest.java          # Purchase input
│   └── RestockRequest.java           # Restock input
├── entity
│   ├── User.java                     # User entity
│   └── Sweet.java                    # Sweet entity
├── exception
│   ├── GlobalExceptionHandler.java   # Centralized exception handling
│   ├── ResourceNotFoundException.java
│   ├── InsufficientStockException.java
│   └── AuthenticationException.java
├── repository
│   ├── UserRepository.java           # User data access
│   └── SweetRepository.java          # Sweet data access
├── security
│   ├── JwtTokenProvider.java         # JWT generation/validation
│   ├── JwtAuthenticationFilter.java  # Request filter
│   └── UserPrincipal.java            # Security user details
├── service
│   ├── AuthService.java              # Authentication logic
│   └── SweetService.java             # Sweet business logic
└── SweetShopApplication.java         # Main application class
```

## Components and Interfaces

### Entity Models

#### User Entity
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;  // BCrypt hashed
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;  // USER or ADMIN
}
```

#### Sweet Entity
```java
@Entity
@Table(name = "sweets")
public class Sweet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String category;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer quantity;
}
```

### Repository Interfaces

#### UserRepository
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

#### SweetRepository
```java
public interface SweetRepository extends JpaRepository<Sweet, Long> {
    List<Sweet> findByNameContainingIgnoreCase(String name);
    List<Sweet> findByCategory(String category);
    List<Sweet> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Custom query for complex search
    @Query("SELECT s FROM Sweet s WHERE " +
           "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:category IS NULL OR s.category = :category) AND " +
           "(:minPrice IS NULL OR s.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR s.price <= :maxPrice)")
    List<Sweet> searchSweets(@Param("name") String name,
                            @Param("category") String category,
                            @Param("minPrice") BigDecimal minPrice,
                            @Param("maxPrice") BigDecimal maxPrice);
}
```

### Service Interfaces

#### AuthService
```java
public interface AuthService {
    void register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}
```

#### SweetService
```java
public interface SweetService {
    SweetResponse createSweet(SweetRequest request);
    List<SweetResponse> getAllSweets();
    SweetResponse getSweetById(Long id);
    List<SweetResponse> searchSweets(String name, String category, 
                                     BigDecimal minPrice, BigDecimal maxPrice);
    SweetResponse updateSweet(Long id, SweetRequest request);
    void deleteSweet(Long id);
    void purchaseSweet(Long id, PurchaseRequest request);
    void restockSweet(Long id, RestockRequest request);
}
```

### Controller Endpoints

#### AuthController
- `POST /api/auth/register` - Register new user (public)
- `POST /api/auth/login` - Authenticate and get JWT (public)

#### SweetController
- `POST /api/sweets` - Create sweet (ADMIN only)
- `GET /api/sweets` - List all sweets (public)
- `GET /api/sweets/{id}` - Get sweet by ID (public)
- `GET /api/sweets/search` - Search sweets (public)
- `PUT /api/sweets/{id}` - Update sweet (ADMIN only)
- `DELETE /api/sweets/{id}` - Delete sweet (ADMIN only)
- `POST /api/sweets/{id}/purchase` - Purchase sweet (authenticated)
- `POST /api/sweets/{id}/restock` - Restock sweet (ADMIN only)

## Data Models

### Database Schema

#### Users Table
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
```

#### Sweets Table
```sql
CREATE TABLE sweets (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    price NUMERIC(10,2) NOT NULL CHECK (price >= 0),
    quantity INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_sweets_category ON sweets(category);
CREATE INDEX idx_sweets_price ON sweets(price);
```

### DTOs

#### Request DTOs
```java
public class RegisterRequest {
    @NotBlank @Email
    private String email;
    
    @NotBlank @Size(min = 8)
    private String password;
}

public class LoginRequest {
    @NotBlank @Email
    private String email;
    
    @NotBlank
    private String password;
}

public class SweetRequest {
    @NotBlank
    private String name;
    
    private String category;
    
    @NotNull @DecimalMin("0.0")
    private BigDecimal price;
    
    @NotNull @Min(0)
    private Integer quantity;
}

public class PurchaseRequest {
    @NotNull @Min(1)
    private Integer quantity;
}

public class RestockRequest {
    @NotNull @Min(1)
    private Integer quantity;
}
```

#### Response DTOs
```java
public class LoginResponse {
    private String token;
    private String email;
    private String role;
}

public class SweetResponse {
    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private Integer quantity;
}
```

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

