# Requirements Document

## Introduction

The Sweet Shop Management System is a full-stack e-commerce application for managing and selling sweets online. The system provides user authentication, inventory management, product browsing with search capabilities, and purchase functionality. It implements role-based access control with USER and ADMIN roles, where regular users can browse and purchase sweets, while administrators have additional privileges to manage inventory and products.

## Glossary

- **System**: The Sweet Shop Management System
- **User**: A registered person with USER role who can browse and purchase sweets
- **Administrator**: A registered person with ADMIN role who can manage inventory and products
- **Sweet**: A product entity representing a confectionery item with name, category, price, and quantity
- **Inventory**: The collection of all sweets and their available quantities
- **Purchase**: A transaction that reduces the quantity of a sweet
- **Restock**: An operation that increases the quantity of a sweet (ADMIN only)
- **JWT**: JSON Web Token used for authentication
- **Session**: An authenticated period identified by a valid JWT token

## Requirements

### Requirement 1: User Registration

**User Story:** As a new visitor, I want to register an account with email and password, so that I can access the sweet shop and make purchases.

#### Acceptance Criteria

1. WHEN a visitor submits valid registration data with unique email and password, THE System SHALL create a new user account with hashed password
2. WHEN a visitor attempts to register with an existing email, THE System SHALL reject the registration and return an error message
3. WHEN a visitor submits registration data with invalid email format, THE System SHALL reject the registration and return a validation error
4. WHEN a visitor submits registration data with password shorter than 8 characters, THE System SHALL reject the registration and return a validation error
5. WHEN a new user account is created, THE System SHALL assign the USER role by default

### Requirement 2: User Authentication

**User Story:** As a registered user, I want to log in with my credentials, so that I can access protected features and make purchases.

#### Acceptance Criteria

1. WHEN a user submits valid email and password credentials, THE System SHALL authenticate the user and return a JWT token
2. WHEN a user submits incorrect password, THE System SHALL reject the login attempt and return an authentication error
3. WHEN a user submits email that does not exist, THE System SHALL reject the login attempt and return an authentication error
4. WHEN a JWT token is generated, THE System SHALL include user ID, email, and role in the token payload
5. WHEN a JWT token is generated, THE System SHALL set expiration time to 1 hour from creation

### Requirement 3: Session Authorization

**User Story:** As an authenticated user, I want my requests to be automatically authorized, so that I can access protected endpoints without re-entering credentials.

#### Acceptance Criteria

1. WHEN a user includes a valid JWT token in the Authorization header, THE System SHALL authenticate the request and extract user information
2. WHEN a user includes an expired JWT token, THE System SHALL reject the request and return 401 Unauthorized status
3. WHEN a user includes an invalid or malformed JWT token, THE System SHALL reject the request and return 401 Unauthorized status
4. WHEN a user accesses a protected endpoint without a JWT token, THE System SHALL reject the request and return 401 Unauthorized status
5. WHEN a USER role attempts to access an ADMIN-only endpoint, THE System SHALL reject the request and return 403 Forbidden status

### Requirement 4: Sweet Creation

**User Story:** As an administrator, I want to add new sweets to the inventory, so that customers can browse and purchase them.

#### Acceptance Criteria

1. WHEN an administrator submits valid sweet data with name, category, price, and quantity, THE System SHALL create a new sweet record
2. WHEN an administrator submits sweet data with negative price, THE System SHALL reject the creation and return a validation error
3. WHEN an administrator submits sweet data with negative quantity, THE System SHALL reject the creation and return a validation error
4. WHEN an administrator submits sweet data with empty name, THE System SHALL reject the creation and return a validation error
5. WHEN a USER role attempts to create a sweet, THE System SHALL reject the request and return 403 Forbidden status

### Requirement 5: Sweet Listing and Retrieval

**User Story:** As a user, I want to view all available sweets, so that I can browse the inventory and decide what to purchase.

#### Acceptance Criteria

1. WHEN a user requests the list of sweets, THE System SHALL return all sweet records with their details
2. WHEN a user requests a specific sweet by ID, THE System SHALL return the sweet details if it exists
3. WHEN a user requests a sweet by non-existent ID, THE System SHALL return 404 Not Found status
4. WHEN the sweet list is returned, THE System SHALL include name, category, price, and quantity for each sweet
5. WHEN an unauthenticated visitor requests the sweet list, THE System SHALL return the list without requiring authentication

### Requirement 6: Sweet Search

**User Story:** As a user, I want to search for sweets by name, category, and price range, so that I can quickly find products that match my preferences.

#### Acceptance Criteria

1. WHEN a user searches with a name parameter, THE System SHALL return all sweets whose names contain the search term (case-insensitive)
2. WHEN a user searches with a category parameter, THE System SHALL return all sweets matching that category exactly
3. WHEN a user searches with minPrice parameter, THE System SHALL return all sweets with price greater than or equal to minPrice
4. WHEN a user searches with maxPrice parameter, THE System SHALL return all sweets with price less than or equal to maxPrice
5. WHEN a user searches with multiple parameters, THE System SHALL return sweets matching all specified criteria (AND logic)

### Requirement 7: Sweet Update

**User Story:** As an administrator, I want to update sweet details, so that I can correct information or adjust pricing.

#### Acceptance Criteria

1. WHEN an administrator submits valid updated data for an existing sweet, THE System SHALL update the sweet record with new values
2. WHEN an administrator attempts to update a non-existent sweet, THE System SHALL return 404 Not Found status
3. WHEN an administrator submits update data with invalid values, THE System SHALL reject the update and return validation errors
4. WHEN a USER role attempts to update a sweet, THE System SHALL reject the request and return 403 Forbidden status
5. WHEN a sweet is updated, THE System SHALL preserve the sweet ID

### Requirement 8: Sweet Deletion

**User Story:** As an administrator, I want to remove sweets from the inventory, so that I can discontinue products that are no longer available.

#### Acceptance Criteria

1. WHEN an administrator requests deletion of an existing sweet, THE System SHALL remove the sweet record from the database
2. WHEN an administrator attempts to delete a non-existent sweet, THE System SHALL return 404 Not Found status
3. WHEN a USER role attempts to delete a sweet, THE System SHALL reject the request and return 403 Forbidden status
4. WHEN a sweet is deleted, THE System SHALL ensure the sweet ID cannot be retrieved afterward
5. WHEN a sweet is deleted, THE System SHALL complete the operation atomically

### Requirement 9: Sweet Purchase

**User Story:** As a user, I want to purchase sweets, so that I can acquire products and the inventory is updated accordingly.

#### Acceptance Criteria

1. WHEN a user purchases a sweet with sufficient quantity, THE System SHALL reduce the sweet quantity by the purchased amount
2. WHEN a user attempts to purchase more than available quantity, THE System SHALL reject the purchase and return an error
3. WHEN a user attempts to purchase zero or negative quantity, THE System SHALL reject the purchase and return a validation error
4. WHEN a user attempts to purchase a non-existent sweet, THE System SHALL return 404 Not Found status
5. WHEN a purchase is completed, THE System SHALL execute the quantity update atomically

### Requirement 10: Inventory Restocking

**User Story:** As an administrator, I want to restock sweets, so that I can replenish inventory when supplies arrive.

#### Acceptance Criteria

1. WHEN an administrator restocks a sweet with a positive quantity, THE System SHALL increase the sweet quantity by the specified amount
2. WHEN an administrator attempts to restock with zero or negative quantity, THE System SHALL reject the operation and return a validation error
3. WHEN an administrator attempts to restock a non-existent sweet, THE System SHALL return 404 Not Found status
4. WHEN a USER role attempts to restock, THE System SHALL reject the request and return 403 Forbidden status
5. WHEN a restock is completed, THE System SHALL execute the quantity update atomically

### Requirement 11: Data Persistence

**User Story:** As a system operator, I want all data to be persisted in a PostgreSQL database with versioned migrations, so that data is reliable and schema changes are tracked.

#### Acceptance Criteria

1. WHEN the System starts, THE System SHALL apply all pending Flyway migrations to the database schema
2. WHEN data is modified, THE System SHALL persist changes to the PostgreSQL database immediately
3. WHEN the System restarts, THE System SHALL retain all previously stored data
4. WHEN a database migration fails, THE System SHALL prevent application startup and report the error
5. WHEN the database connection is unavailable, THE System SHALL fail health checks and report the error

### Requirement 12: Password Security

**User Story:** As a security-conscious user, I want my password to be securely stored, so that my account cannot be compromised if the database is breached.

#### Acceptance Criteria

1. WHEN a user registers or changes password, THE System SHALL hash the password using BCrypt algorithm
2. WHEN a user password is stored, THE System SHALL never store the plaintext password
3. WHEN a user authenticates, THE System SHALL compare the submitted password against the BCrypt hash
4. WHEN password hashing occurs, THE System SHALL use a salt to prevent rainbow table attacks
5. WHEN passwords are logged or returned in responses, THE System SHALL never include password values
