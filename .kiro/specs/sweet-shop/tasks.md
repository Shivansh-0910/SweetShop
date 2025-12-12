# Implementation Plan

- [x] 1. Set up project structure and dependencies



  - Initialize Spring Boot project with Maven
  - Add dependencies: Spring Web, Spring Data JPA, Spring Security, PostgreSQL Driver, Flyway, Lombok, Validation, JWT libraries (jjwt-api, jjwt-impl, jjwt-jackson), Spring Boot Test, Spring Security Test, Testcontainers
  - Create package structure: config, controller, dto, entity, exception, repository, security, service
  - Configure application.yml with database connection and JWT properties
  - _Requirements: 11.1, 11.2_



- [ ] 2. Set up database and migrations
  - Create docker-compose.yml for local PostgreSQL instance
  - Create Flyway migration V1__init.sql with users and sweets tables
  - Add indexes for email, category, and price columns
  - Configure Flyway in application.yml
  - _Requirements: 11.1, 11.3, 11.4_

- [ ]* 2.1 Write integration test for database connectivity
  - Test that application starts successfully with database connection


  - Test that Flyway migrations execute correctly
  - _Requirements: 11.1, 11.4_

- [ ] 3. Implement User entity and repository
  - Create User entity with id, email, password, role fields
  - Create Role enum with USER and ADMIN values
  - Create UserRepository interface extending JpaRepository
  - Add findByEmail and existsByEmail query methods
  - _Requirements: 1.1, 1.5, 2.1_

- [x]* 3.1 Write unit tests for UserRepository


  - Test findByEmail returns user when exists
  - Test existsByEmail returns true for existing email
  - Test existsByEmail returns false for non-existent email
  - _Requirements: 1.1, 2.1_

- [ ] 4. Implement JWT token provider
  - Create JwtConfig class to hold JWT secret and expiration properties
  - Create JwtTokenProvider class with generateToken method
  - Implement token generation with user ID, email, and role in payload
  - Set token expiration to 1 hour
  - Implement validateToken method to verify token signature and expiration
  - Implement extractUserIdFromToken, extractEmailFromToken methods
  - _Requirements: 2.4, 2.5, 3.1, 3.2, 3.3_

- [x]* 4.1 Write unit tests for JWT token provider


  - Test generateToken creates valid token with correct payload
  - Test validateToken returns true for valid token
  - Test validateToken returns false for expired token
  - Test validateToken returns false for malformed token
  - Test extractUserIdFromToken returns correct user ID
  - _Requirements: 2.4, 2.5, 3.1, 3.2, 3.3_

- [ ] 5. Implement authentication service
  - Create RegisterRequest and LoginRequest DTOs with validation annotations
  - Create LoginResponse DTO with token, email, and role fields
  - Create AuthService interface with register and login methods
  - Implement AuthServiceImpl with BCrypt password hashing
  - Implement register method: validate unique email, hash password, assign USER role, save user
  - Implement login method: find user by email, verify password, generate JWT token
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 2.2, 2.3, 12.1, 12.2, 12.3, 12.4_

- [ ]* 5.1 Write property test for password hashing
  - **Property 1: Password hashing is irreversible**
  - **Validates: Requirements 12.1, 12.2**
  - For any valid password string, the hashed password should never equal the original password
  - For any valid password string, hashing twice should produce different hashes (due to salt)

- [x]* 5.2 Write unit tests for authentication service


  - Test register creates user with hashed password and USER role
  - Test register rejects duplicate email
  - Test register rejects invalid email format
  - Test register rejects password shorter than 8 characters
  - Test login returns JWT token for valid credentials
  - Test login rejects incorrect password
  - Test login rejects non-existent email
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 2.2, 2.3, 12.3_

- [ ] 6. Implement authentication controller
  - Create AuthController with register and login endpoints
  - Add @PostMapping for /api/auth/register (public access)
  - Add @PostMapping for /api/auth/login (public access)
  - Add @Valid annotation for request validation


  - Return 201 Created for successful registration
  - Return 200 OK with LoginResponse for successful login
  - _Requirements: 1.1, 2.1_

- [ ]* 6.1 Write integration tests for authentication endpoints
  - Test POST /api/auth/register with valid data returns 201
  - Test POST /api/auth/register with duplicate email returns 400


  - Test POST /api/auth/login with valid credentials returns 200 and token
  - Test POST /api/auth/login with invalid credentials returns 401
  - _Requirements: 1.1, 1.2, 2.1, 2.2, 2.3_

- [ ] 7. Implement JWT authentication filter
  - Create UserPrincipal class implementing UserDetails
  - Create JwtAuthenticationFilter extending OncePerRequestFilter
  - Extract JWT token from Authorization header
  - Validate token and extract user information
  - Set authentication in SecurityContext
  - Handle expired and invalid tokens
  - _Requirements: 3.1, 3.2, 3.3, 3.4_

- [ ] 8. Configure Spring Security
  - Create SecurityConfig class with @EnableWebSecurity


  - Configure HttpSecurity to permit /api/auth/** endpoints
  - Require authentication for all other endpoints
  - Add JWT authentication filter before UsernamePasswordAuthenticationFilter
  - Configure role-based access: ADMIN for specific endpoints
  - Disable CSRF for stateless API
  - Configure exception handling for 401 and 403 responses
  - _Requirements: 3.1, 3.4, 3.5_

- [ ]* 8.1 Write integration tests for security configuration
  - Test accessing protected endpoint without token returns 401
  - Test accessing protected endpoint with valid token returns 200
  - Test accessing ADMIN endpoint with USER role returns 403


  - Test accessing public endpoint without token returns 200
  - _Requirements: 3.1, 3.4, 3.5_

- [ ] 9. Implement Sweet entity and repository
  - Create Sweet entity with id, name, category, price, quantity fields
  - Add validation constraints: price >= 0, quantity >= 0
  - Create SweetRepository interface extending JpaRepository
  - Add searchSweets custom query method with optional parameters
  - _Requirements: 4.1, 5.1, 6.1, 6.5_

- [ ]* 9.1 Write unit tests for SweetRepository
  - Test searchSweets with name parameter returns matching sweets
  - Test searchSweets with category parameter returns matching sweets
  - Test searchSweets with price range returns sweets within range
  - Test searchSweets with multiple parameters applies AND logic
  - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5_

- [ ] 10. Implement sweet service
  - Create SweetRequest, SweetResponse, PurchaseRequest, RestockRequest DTOs
  - Create SweetService interface with all CRUD and business methods
  - Implement SweetServiceImpl with transaction management
  - Implement createSweet: validate input, save sweet, return response
  - Implement getAllSweets: fetch all sweets, map to responses
  - Implement getSweetById: find by ID or throw ResourceNotFoundException
  - Implement searchSweets: call repository search method
  - Implement updateSweet: find sweet, validate input, update fields, save
  - Implement deleteSweet: find sweet, delete from database
  - Implement purchaseSweet: find sweet, check quantity, reduce quantity atomically
  - Implement restockSweet: find sweet, increase quantity atomically
  - _Requirements: 4.1, 5.1, 5.2, 6.1, 7.1, 8.1, 9.1, 9.2, 10.1_

- [ ]* 10.1 Write property test for purchase quantity reduction
  - **Property 2: Purchase reduces quantity correctly**
  - **Validates: Requirements 9.1**
  - For any sweet with quantity Q and purchase amount P where P <= Q, after purchase the quantity should equal Q - P

- [ ]* 10.2 Write property test for restock quantity increase
  - **Property 3: Restock increases quantity correctly**
  - **Validates: Requirements 10.1**
  - For any sweet with quantity Q and restock amount R where R > 0, after restock the quantity should equal Q + R

- [ ]* 10.3 Write property test for search with multiple criteria
  - **Property 4: Search with multiple criteria applies AND logic**
  - **Validates: Requirements 6.5**
  - For any search with multiple parameters, all returned sweets should match every specified criterion

- [ ]* 10.4 Write unit tests for sweet service
  - Test createSweet saves and returns sweet
  - Test createSweet rejects negative price


  - Test createSweet rejects negative quantity
  - Test createSweet rejects empty name
  - Test getAllSweets returns all sweets
  - Test getSweetById returns sweet when exists
  - Test getSweetById throws ResourceNotFoundException when not exists
  - Test searchSweets filters by name case-insensitively
  - Test searchSweets filters by exact category
  - Test searchSweets filters by price range
  - Test updateSweet updates fields correctly
  - Test updateSweet throws ResourceNotFoundException for non-existent sweet
  - Test deleteSweet removes sweet from database
  - Test deleteSweet throws ResourceNotFoundException for non-existent sweet
  - Test purchaseSweet reduces quantity
  - Test purchaseSweet throws InsufficientStockException when quantity insufficient
  - Test purchaseSweet rejects zero or negative quantity
  - Test restockSweet increases quantity
  - Test restockSweet rejects zero or negative quantity
  - _Requirements: 4.1, 4.2, 4.3, 4.4, 5.1, 5.2, 5.3, 6.1, 6.2, 6.3, 6.4, 7.1, 7.2, 8.1, 8.2, 9.1, 9.2, 9.3, 10.1, 10.2_

- [ ] 11. Implement sweet controller
  - Create SweetController with all sweet management endpoints
  - Add @PostMapping for /api/sweets (ADMIN only) - returns 201 Created
  - Add @GetMapping for /api/sweets (public) - returns 200 OK
  - Add @GetMapping for /api/sweets/{id} (public) - returns 200 OK or 404
  - Add @GetMapping for /api/sweets/search (public) - returns 200 OK
  - Add @PutMapping for /api/sweets/{id} (ADMIN only) - returns 200 OK or 404
  - Add @DeleteMapping for /api/sweets/{id} (ADMIN only) - returns 204 No Content or 404
  - Add @PostMapping for /api/sweets/{id}/purchase (authenticated) - returns 200 OK or 404
  - Add @PostMapping for /api/sweets/{id}/restock (ADMIN only) - returns 200 OK or 404


  - Add @Valid annotations for request validation
  - _Requirements: 4.1, 5.1, 5.2, 6.1, 7.1, 8.1, 9.1, 10.1_

- [ ]* 11.1 Write integration tests for sweet controller
  - Test POST /api/sweets with ADMIN role returns 201
  - Test POST /api/sweets with USER role returns 403
  - Test POST /api/sweets with invalid data returns 400
  - Test GET /api/sweets returns all sweets
  - Test GET /api/sweets/{id} returns sweet when exists
  - Test GET /api/sweets/{id} returns 404 when not exists
  - Test GET /api/sweets/search with parameters returns filtered results
  - Test PUT /api/sweets/{id} with ADMIN role updates sweet
  - Test PUT /api/sweets/{id} with USER role returns 403
  - Test DELETE /api/sweets/{id} with ADMIN role deletes sweet
  - Test DELETE /api/sweets/{id} with USER role returns 403
  - Test POST /api/sweets/{id}/purchase with sufficient quantity succeeds


  - Test POST /api/sweets/{id}/purchase with insufficient quantity returns 400
  - Test POST /api/sweets/{id}/restock with ADMIN role increases quantity
  - Test POST /api/sweets/{id}/restock with USER role returns 403
  - _Requirements: 4.1, 4.5, 5.1, 5.2, 5.3, 6.1, 7.1, 7.4, 8.1, 8.3, 9.1, 9.2, 10.1, 10.4_




- [ ] 12. Implement global exception handling
  - Create GlobalExceptionHandler with @ControllerAdvice
  - Create ResourceNotFoundException for 404 responses
  - Create InsufficientStockException for purchase failures
  - Handle validation errors and return 400 Bad Request
  - Handle authentication errors and return 401 Unauthorized
  - Handle authorization errors and return 403 Forbidden
  - Handle ResourceNotFoundException and return 404 Not Found
  - Handle InsufficientStockException and return 400 Bad Request
  - Return consistent error response format with message and timestamp
  - _Requirements: 5.3, 7.2, 8.2, 9.2, 9.4, 10.3_

- [ ]* 12.1 Write unit tests for exception handling
  - Test ResourceNotFoundException returns 404 with error message
  - Test InsufficientStockException returns 400 with error message
  - Test validation errors return 400 with field-specific messages
  - _Requirements: 5.3, 7.2, 8.2, 9.2_

- [ ] 13. Add password security logging protection
  - Review all logging statements to ensure passwords are never logged
  - Ensure DTOs do not include password in toString methods
  - Add @JsonProperty(access = WRITE_ONLY) to password fields
  - Verify exception messages do not expose passwords
  - _Requirements: 12.5_

- [ ] 14. Final checkpoint - Ensure all tests pass
  - Run all unit tests and verify they pass
  - Run all integration tests and verify they pass
  - Run all property-based tests and verify they pass
  - Ensure test coverage meets minimum thresholds
  - Ask the user if questions arise
