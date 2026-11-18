# Tech Device Shop - Comprehensive API Documentation

## Overview

## Role and permissions

### Role
    - ADMIN
    - CUSTOMER
    - BUSINESS

### Permissions
    - PROFILE_READ

    - PRODUCT_READ
    - PRODUCT_CREATE
    - PRODUCT_UPDATE
    - PRODUCT_DELETE

    - CART_READ
    - CART_CREATE
    - CART_UPDATE
    - CART_DELETE

    - USER_CREATE
    - USER_UPDATE
    - USER_DELETE
    - USER_READ = PROFILE_READ

    - ORDER_READ
    - ORDER_DELETE
    - ORDER_UPDATE
    - ORDER_CREATE

    - PAYMENT_CREATE
    - PAYMENT_UPDATE
    - PAYMENT_DELETE
    - PAYMENT_READ
    
    - ADDRESS_READ
    - ADDRESS_CREATE
    - ADDRESS_UPDATE
    - ADDRESS_DELETE
    
### authority
    - BUSINESS:
        - PROFILE_READ

        - PRODUCT_READ
        - PRODUCT_CREATE
        - PRODUCT_UPDATE
        - PRODUCT_DELETE

        - CART_READ
        - CART_CREATE
        - CART_UPDATE
        - CART_DELETE
        
        - USER_CREATE
        - USER_UPDATE

        - ORDER_READ
        - ORDER_UPDATE
        - ORDER_CREATE

        - PAYMENT_CREATE
        - PAMENT_READ

        - ADDRESS_READ

    - CUSTOMER:
        - PROFILE_READ

        - PRODUCT_READ

        - CART_READ
        - CART_CREATE
        - CART_UPDATE
        - CART_DELETE
        
        - USER_CREATE
        - USER_UPDATE

        - ORDER_READ
        - ORDER_UPDATE
        - ORDER_CREATE

        - PAYMENT_CREATE
        - PAMENT_READ

        - ADDRESS_READ
    

    - ADMIN:
        - PRODUCT_READ
        - PRODUCT_DELETE

        - USER_CREATE
        - USER_DELETE

        - PAYMENT_DELETE
        - PAYMENT_READ

        - ORDER_READ

        - ADDRESS_CREATE
        - ADDRESS_UPDATE
        - ADDRESS_DELETE

## Triển khai mô hình phân quyền
    user n -- n role || role n -- n permission 

    USER N --> ROLE 

The Tech Device Shop API is a RESTful e-commerce backend built with Spring Boot. It provides endpoints for managing products, users, shopping carts, orders, reviews, categories, addresses, and more.

### Base URL
```
http://localhost:8080
```

### Default Response Format
All endpoints return responses wrapped in an `ApiResponse` object:

```json
{
  "status": 200,
  "message": "OK",
  "data": {}
}
```

### Headers Required for Protected Endpoints
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

---

## 1. AUTHENTICATION ENDPOINTS

### 1.1 Login
- **Method:** `POST`
- **Endpoint:** `/auth/access`
- **Description:** Authenticate user and receive JWT tokens
- **Authentication:** None (Public)
- **Status Codes:** 200 OK, 400 Bad Request

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "userId": 1
}
```

**Response (400 Bad Request):**
```json
{
  "status": 400,
  "message": "Invalid email or password",
  "data": null
}
```

---

### 1.2 Register
- **Method:** `POST`
- **Endpoint:** `/auth/register`
- **Description:** Create a new user account
- **Authentication:** None (Public)
- **Status Codes:** 201 Created, 400 Bad Request

**Request Body:**
```json
{
  "email": "newuser@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "phone": "+1234567890"
}
```

**Request Headers:**
```
Content-Type: application/json
```

**Response (201 Created):**
```json
{
  "status": 201,
  "message": "Created",
  "data": {
    "id": 1,
    "email": "newuser@example.com",
    "fullName": "John Doe",
    "phone": "+1234567890",
    "role": "CUSTOMER",
    "createdAt": "2024-01-15T10:30:00",
    "address": null
  }
}
```

**Validations:**
- Email must be unique and valid format
- Password must be at least 8 characters
- Full name and phone are required

---

### 1.3 Refresh Token
- **Method:** `POST`
- **Endpoint:** `/auth/refresh`
- **Description:** Get new access token using refresh token
- **Authentication:** Required (Bearer token in header)
- **Status Codes:** 200 OK, 401 Unauthorized

**Request Headers:**
```
Authorization: Bearer <refresh_token>
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "userId": 1
}
```

---

### 1.4 Logout
- **Method:** `POST`
- **Endpoint:** `/auth/logout`
- **Description:** Invalidate current session
- **Authentication:** Required (Bearer token)
- **Status Codes:** 204 No Content, 401 Unauthorized

**Request Headers:**
```
Authorization: Bearer <access_token>
```

**Response (204 No Content):** No body

---

### 1.5 Forgot Password
- **Method:** `POST`
- **Endpoint:** `/auth/forgot-password`
- **Description:** Request password reset link via email
- **Authentication:** None (Public)
- **Status Codes:** 200 OK, 400 Bad Request

**Request Body:**
```
"user@example.com"
```

**Response (200 OK):**
```
"Reset password link sent to your email"
```

---

### 1.6 Reset Password
- **Method:** `POST`
- **Endpoint:** `/auth/reset-password`
- **Description:** Reset password using secret key from email
- **Authentication:** None (Public)
- **Status Codes:** 200 OK, 400 Bad Request

**Request Body:**
```
"secret-key-from-email"
```

**Response (200 OK):**
```
"Password reset successfully"
```

---

### 1.7 Change Password
- **Method:** `POST`
- **Endpoint:** `/auth/change-password`
- **Description:** Change password for authenticated user
- **Authentication:** Required (Bearer token)
- **Status Codes:** 200 OK, 400 Bad Request

**Request Body:**
```json
{
  "secretKey": "old-password",
  "password": "new-password",
  "confirmPassword": "new-password"
}
```

**Response (200 OK):**
```
"Password changed successfully"
```

**Validations:**
- New password must match confirm password
- Old password must be correct
- New password must be different from old password

---

### 1.8 OAuth Authorization
- **Method:** `GET`
- **Endpoint:** `/oauth/authorize/{provider}`
- **Description:** Get authorization URL for OAuth provider
- **Parameters:** 
  - `provider` (string) - OAuth provider name (google, facebook, github, etc.)
- **Authentication:** None (Public)
- **Status Codes:** 200 OK, 400 Bad Request

**Response (200 OK):**
```json
{
  "authorizationUrl": "https://accounts.google.com/o/oauth2/v2/auth?..."
}
```

---

### 1.9 OAuth Callback
- **Method:** `GET`
- **Endpoint:** `/oauth/callback`
- **Description:** Handle OAuth provider callback after authorization
- **Query Parameters:**
  - `code` (string) - Authorization code from provider
  - `state` (string) - State parameter (provider name)
- **Authentication:** None (Public)
- **Status Codes:** 200 OK, 400 Bad Request

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "userId": 1
}
```

---

## 2. USER MANAGEMENT ENDPOINTS

### 2.1 Get All Users
- **Method:** `GET`
- **Endpoint:** `/api/users`
- **Description:** Retrieve all users (paginated)
- **Authentication:** Required (Bearer token, Admin only)
- **Status Codes:** 200 OK, 401 Unauthorized, 403 Forbidden

**Query Parameters:**
- `page` (int) - Page number (0-indexed, default: 0)
- `pageSize` (int) - Items per page (default: 10)
- `sortBy` (string) - Field to sort by (default: createdAt)
- `sortDirection` (string) - ASC or DESC (default: DESC)

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "email": "user@example.com",
      "fullName": "John Doe",
      "phone": "+1234567890",
      "role": "CUSTOMER",
      "createdAt": "2024-01-15T10:30:00",
      "address": {
        "id": 1,
        "receiverName": "John Doe",
        "phone": "+1234567890",
        "province": "California",
        "district": "Los Angeles",
        "ward": "Downtown",
        "detail": "123 Main Street"
      }
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 50,
  "totalPages": 5,
  "isFirst": true,
  "isLast": false
}
```

---

### 2.2 Get Current User
- **Method:** `GET`
- **Endpoint:** `/api/users/me`
- **Description:** Get authenticated user's profile
- **Authentication:** Required (Bearer token)
- **Status Codes:** 200 OK, 401 Unauthorized

**Response (200 OK):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "fullName": "John Doe",
  "phone": "+1234567890",
  "role": "CUSTOMER",
  "createdAt": "2024-01-15T10:30:00",
  "address": {
    "id": 1,
    "receiverName": "John Doe",
    "phone": "+1234567890",
    "province": "California",
    "district": "Los Angeles",
    "ward": "Downtown",
    "detail": "123 Main Street"
  }
}
```

---

### 2.3 Get User by ID
- **Method:** `GET`
- **Endpoint:** `/api/users/{id}`
- **Description:** Retrieve specific user by ID
- **Parameters:** 
  - `id` (Long) - User ID
- **Authentication:** Required (Bearer token)
- **Status Codes:** 200 OK, 404 Not Found, 401 Unauthorized

**Response (200 OK):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "fullName": "John Doe",
  "phone": "+1234567890",
  "role": "CUSTOMER",
  "createdAt": "2024-01-15T10:30:00",
  "address": {...}
}
```

---

### 2.4 Update User
- **Method:** `PATCH`
- **Endpoint:** `/api/users/{id}`
- **Description:** Update user profile information
- **Parameters:** 
  - `id` (Long) - User ID
- **Authentication:** Required (Bearer token)
- **Status Codes:** 200 OK, 404 Not Found, 400 Bad Request

**Request Body:**
```json
{
  "fullName": "Jane Doe",
  "phone": "+0987654321",
  "addressId": 1
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "fullName": "Jane Doe",
  "phone": "+0987654321",
  "role": "CUSTOMER",
  "createdAt": "2024-01-15T10:30:00",
  "address": {...}
}
```

---

### 2.5 Delete User
- **Method:** `DELETE`
- **Endpoint:** `/api/users/{id}`
- **Description:** Delete user account (soft delete)
- **Parameters:** 
  - `id` (Long) - User ID
- **Authentication:** Required (Bearer token, Admin only)
- **Status Codes:** 204 No Content, 404 Not Found, 403 Forbidden

**Response (204 No Content):** No body

---

## 3. PRODUCT ENDPOINTS

### 3.1 Get All Products
- **Method:** `GET`
- **Endpoint:** `/api/products`
- **Description:** Retrieve all products (paginated, filterable)
- **Authentication:** None (Public)
- **Status Codes:** 200 OK

**Query Parameters:**
- `page` (int) - Page number (0-indexed, default: 0)
- `pageSize` (int) - Items per page (default: 10)
- `sortBy` (string) - Field to sort by (createdAt, price, rating, etc.)
- `sortDirection` (string) - ASC or DESC
- `status` (string) - ACTIVE, INACTIVE, etc.
- `categoryId` (Long) - Filter by category

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Dell Laptop XPS 13",
      "specifications": "Intel i7, 16GB RAM, 512GB SSD",
      "description": "High-performance ultrabook",
      "status": "ACTIVE",
      "ratingAvg": 4.5,
      "reviewCount": 24,
      "createdAt": "2024-01-10T10:00:00",
      "updatedAt": "2024-01-15T10:00:00",
      "categoryId": 1,
      "businessOwnerId": 5,
      "images": [
        {
          "id": 1,
          "bucketName": "images",
          "originalFileName": "laptop1.jpg",
          "fileName": "1/uuid_laptop1.jpg",
          "fileSize": 204800,
          "contentType": "image/jpeg",
          "publicUrl": "https://cdn.example.com/1/uuid_laptop1.jpg",
          "productId": 1
        }
      ]
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 100,
  "totalPages": 10,
  "isFirst": true,
  "isLast": false
}
```

---

### 3.2 Search Products
- **Method:** `GET`
- **Endpoint:** `/api/products/search`
- **Description:** Search and filter products with advanced filtering
- **Authentication:** None (Public)
- **Status Codes:** 200 OK

**Query Parameters:**
- `keyword` (string) - Search term (searches in product name and description)
- `brand` (string) - Brand filter
- `status` (string) - Status filter (ACTIVE, INACTIVE)
- `categoryId` (Long) - Category ID filter
- `minPrice` (BigDecimal) - Minimum price filter
- `maxPrice` (BigDecimal) - Maximum price filter
- `minRating` (Double) - Minimum rating filter (0-5)
- `page` (int) - Page number (default: 0)
- `pageSize` (int) - Items per page (default: 10)
- `sortBy` (string) - Sort field (price, rating, createdAt, etc.)
- `sortDirection` (string) - ASC or DESC (default: DESC)

**Response (200 OK):**
```json
{
  "content": [...],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 100,
  "totalPages": 10,
  "isFirst": true,
  "isLast": false
}
```

**Example Request:**
```
GET /api/products/search?keyword=laptop&minPrice=500&maxPrice=2000&categoryId=1&minRating=4&page=0&pageSize=20&sortBy=price&sortDirection=ASC
```

---

### 3.3 Get Product by ID
- **Method:** `GET`
- **Endpoint:** `/api/products/{id}`
- **Description:** Retrieve specific product details
- **Parameters:** 
  - `id` (Long) - Product ID
- **Authentication:** None (Public)
- **Status Codes:** 200 OK, 404 Not Found

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Dell Laptop XPS 13",
  "specifications": "Intel i7, 16GB RAM, 512GB SSD",
  "description": "High-performance ultrabook",
  "status": "ACTIVE",
  "ratingAvg": 4.5,
  "reviewCount": 24,
  "createdAt": "2024-01-10T10:00:00",
  "updatedAt": "2024-01-15T10:00:00",
  "categoryId": 1,
  "businessOwnerId": 5,
  "images": [...]
}
```

---

### 3.4 Create Product
- **Method:** `POST`
- **Endpoint:** `/api/products`
- **Description:** Create new product (Admin/Seller only)
- **Authentication:** Required (Bearer token, Admin/Seller role)
- **Status Codes:** 201 Created, 400 Bad Request, 403 Forbidden

**Request Body:**
```json
{
  "name": "Dell Laptop XPS 13",
  "specifications": "Intel i7, 16GB RAM, 512GB SSD",
  "description": "High-performance ultrabook",
  "status": "ACTIVE",
  "categoryId": 1
}
```

**Response (201 Created):**
```json
{
  "status": 201,
  "message": "Created",
  "data": {
    "id": 1,
    "name": "Dell Laptop XPS 13",
    "specifications": "Intel i7, 16GB RAM, 512GB SSD",
    "description": "High-performance ultrabook",
    "status": "ACTIVE",
    "ratingAvg": 0,
    "reviewCount": 0,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00",
    "categoryId": 1,
    "businessOwnerId": 5,
    "images": []
  }
}
```

**Validations:**
- Product name is required
- Category must exist
- Status must be valid

---

### 3.5 Update Product
- **Method:** `PUT`
- **Endpoint:** `/api/products/{id}`
- **Description:** Update product details
- **Parameters:** 
  - `id` (Long) - Product ID
- **Authentication:** Required (Bearer token, Admin/Seller)
- **Status Codes:** 200 OK, 404 Not Found, 403 Forbidden

**Request Body:**
```json
{
  "name": "Dell Laptop XPS 13 (Updated)",
  "specifications": "Intel i9, 32GB RAM, 1TB SSD",
  "description": "High-performance ultrabook with new features",
  "status": "ACTIVE",
  "categoryId": 1
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Dell Laptop XPS 13 (Updated)",
  "specifications": "Intel i9, 32GB RAM, 1TB SSD",
  "description": "High-performance ultrabook with new features",
  "status": "ACTIVE",
  "ratingAvg": 4.5,
  "reviewCount": 24,
  "createdAt": "2024-01-10T10:00:00",
  "updatedAt": "2024-01-15T11:00:00",
  "categoryId": 1,
  "businessOwnerId": 5,
  "images": [...]
}
```

---

### 3.6 Delete Product
- **Method:** `DELETE`
- **Endpoint:** `/api/products/{id}`
- **Description:** Delete product
- **Parameters:** 
  - `id` (Long) - Product ID
- **Authentication:** Required (Bearer token, Admin only)
- **Status Codes:** 204 No Content, 404 Not Found, 403 Forbidden

**Response (204 No Content):** No body

---

## 4. CATEGORY ENDPOINTS

### 4.1 Get All Categories
- **Method:** `GET`
- **Endpoint:** `/api/categories`
- **Description:** Retrieve all categories (flat list)
- **Authentication:** None (Public)
- **Status Codes:** 200 OK

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Laptops",
    "slug": "laptops",
    "parentId": null
  },
  {
    "id": 2,
    "name": "Gaming Laptops",
    "slug": "gaming-laptops",
    "parentId": 1
  },
  {
    "id": 3,
    "name": "Accessories",
    "slug": "accessories",
    "parentId": null
  }
]
```

---

### 4.2 Get Category Tree
- **Method:** `GET`
- **Endpoint:** `/api/categories/tree`
- **Description:** Retrieve categories in hierarchical tree structure
- **Authentication:** None (Public)
- **Status Codes:** 200 OK

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Laptops",
    "slug": "laptops",
    "parentId": null,
    "children": [
      {
        "id": 2,
        "name": "Gaming Laptops",
        "slug": "gaming-laptops",
        "parentId": 1,
        "children": []
      }
    ]
  }
]
```

---

### 4.3 Get Category by ID
- **Method:** `GET`
- **Endpoint:** `/api/categories/{id}`
- **Description:** Retrieve specific category
- **Parameters:** 
  - `id` (Long) - Category ID
- **Authentication:** None (Public)
- **Status Codes:** 200 OK, 404 Not Found

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Laptops",
  "slug": "laptops",
  "parentId": null
}
```

---

### 4.4 Create Category
- **Method:** `POST`
- **Endpoint:** `/api/categories`
- **Description:** Create new category
- **Authentication:** Required (Bearer token, Admin only)
- **Status Codes:** 201 Created, 400 Bad Request

**Request Body:**
```json
{
  "name": "Laptops",
  "slug": "laptops",
  "parentId": null
}
```

**Response (201 Created):**
```json
{
  "status": 201,
  "message": "Created",
  "data": {
    "id": 1,
    "name": "Laptops",
    "slug": "laptops",
    "parentId": null
  }
}
```

**Validations:**
- Category name is required
- Slug must be unique

---

### 4.5 Update Category
- **Method:** `PUT`
- **Endpoint:** `/api/categories/{id}`
- **Description:** Update category details
- **Parameters:** 
  - `id` (Long) - Category ID
- **Authentication:** Required (Bearer token, Admin only)
- **Status Codes:** 200 OK, 404 Not Found

**Request Body:**
```json
{
  "name": "Laptops & Computers",
  "slug": "laptops-computers",
  "parentId": null
}
```

---

### 4.6 Delete Category
- **Method:** `DELETE`
- **Endpoint:** `/api/categories/{id}`
- **Description:** Delete category
- **Parameters:** 
  - `id` (Long) - Category ID
- **Authentication:** Required (Bearer token, Admin only)
- **Status Codes:** 204 No Content, 404 Not Found

**Response (204 No Content):** No body

---

## 5. ADDRESS ENDPOINTS

### 5.1 Get User Addresses
- **Method:** `GET`
- **Endpoint:** `/api/users/{userId}/addresses`
- **Description:** Get all addresses for a specific user
- **Parameters:** 
  - `userId` (Long) - User ID
- **Authentication:** Required (Bearer token)
- **Status Codes:** 200 OK, 404 Not Found

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "receiverName": "John Doe",
    "phone": "+1234567890",
    "province": "California",
    "district": "Los Angeles",
    "ward": "Downtown",
    "detail": "123 Main Street"
  },
  {
    "id": 2,
    "receiverName": "John Doe",
    "phone": "+1234567890",
    "province": "New York",
    "district": "Manhattan",
    "ward": "Midtown",
    "detail": "456 Broadway"
  }
]
```

---

### 5.2 Create Address
- **Method:** `POST`
- **Endpoint:** `/api/users/{userId}/addresses`
- **Description:** Create new address for user
- **Parameters:** 
  - `userId` (Long) - User ID
- **Authentication:** Required (Bearer token)
- **Status Codes:** 201 Created, 400 Bad Request

**Request Body:**
```json
{
  "receiverName": "John Doe",
  "phone": "+1234567890",
  "province": "California",
  "district": "Los Angeles",
  "ward": "Downtown",
  "detail": "123 Main Street"
}
```

**Response (201 Created):**
```json
{
  "status": 201,
  "message": "Created",
  "data": {
    "id": 1,
    "receiverName": "John Doe",
    "phone": "+1234567890",
    "province": "California",
    "district": "Los Angeles",
    "ward": "Downtown",
    "detail": "123 Main Street"
  }
}
```

**Validations:**
- All fields are required
- Phone number format must be valid

---

### 5.3 Update Address
- **Method:** `PUT`
- **Endpoint:** `/api/addresses/{id}`
- **Description:** Update address details
- **Parameters:** 
  - `id` (Long) - Address ID
- **Authentication:** Required (Bearer token)
- **Status Codes:** 200 OK, 404 Not Found

**Request Body:**
```json
{
  "receiverName": "Jane Doe",
  "phone": "+0987654321",
  "province": "New York",
  "district": "Manhattan",
  "ward": "Midtown",
  "detail": "456 Broadway"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "receiverName": "Jane Doe",
  "phone": "+0987654321",
  "province": "New York",
  "district": "Manhattan",
  "ward": "Midtown",
  "detail": "456 Broadway"
}
```

---

### 5.4 Delete Address
- **Method:** `DELETE`
- **Endpoint:** `/api/addresses/{id}`
- **Description:** Delete address
- **Parameters:** 
  - `id` (Long) - Address ID
- **Authentication:** Required (Bearer token)
- **Status Codes:** 204 No Content, 404 Not Found

**Response (204 No Content):** No body

---

## 6. CART ENDPOINTS

### 6.1 Get Cart
- **Method:** `GET`
- **Endpoint:** `/api/cart`
- **Description:** Retrieve current shopping cart for user
- **Query Parameters:** 
  - `userId` (Long) - User ID
- **Authentication:** Required (Bearer token)
- **Status Codes:** 200 OK

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "items": [
    {
      "id": 1,
      "productId": 1,
      "productName": "Dell Laptop XPS 13",
      "price": 1299.99,
      "quantity": 1,
      "subtotal": 1299.99
    },
    {
      "id": 2,
      "productId": 2,
      "productName": "USB-C Cable",
      "price": 19.99,
      "quantity": 2,
      "subtotal": 39.98
    }
  ],
  "totalItems": 3,
  "totalPrice": 1339.97
}
```

---

### 6.2 Add to Cart
- **Method:** `POST`
- **Endpoint:** `/api/cart/items`
- **Description:** Add product to shopping cart
- **Authentication:** Required (Bearer token)
- **Status Codes:** 200 OK, 400 Bad Request

**Request Body:**
```json
{
  "productId": 1,
  "quantity": 1
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "productId": 1,
  "productName": "Dell Laptop XPS 13",
  "price": 1299.99,
  "quantity": 1,
  "subtotal": 1299.99
}
```

**Validations:**
- `quantity` must be > 0
- Product must exist
- Sufficient stock must be available

---

### 6.3 Update Cart Item Quantity
- **Method:** `PATCH`
- **Endpoint:** `/api/cart/items/{id}`
- **Description:** Update quantity of cart item
- **Parameters:** 
  - `id` (Long) - Cart item ID
- **Authentication:** Required (Bearer token)
- **Status Codes:** 200 OK, 404 Not Found

**Request Body:**
```json
{
  "productId": 1,
  "quantity": 2
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "productId": 1,
  "productName": "Dell Laptop XPS 13",
  "price": 1299.99,
  "quantity": 2,
  "subtotal": 2599.98
}
```

---

### 6.4 Remove from Cart
- **Method:** `DELETE`
- **Endpoint:** `/api/cart/items/{id}`
- **Description:** Remove specific item from cart
- **Parameters:** 
  - `id` (Long) - Cart item ID
- **Authentication:** Required (Bearer token)
- **Status Codes:** 200 OK, 404 Not Found

**Response (200 OK):**
```json
{
  "message": "Item removed from cart successfully"
}
```

---

### 6.5 Clear Cart
- **Method:** `DELETE`
- **Endpoint:** `/api/cart`
- **Description:** Remove all items from shopping cart
- **Authentication:** Required (Bearer token)
- **Status Codes:** 204 No Content

**Response (204 No Content):** No body

---

## 7. ORDER ENDPOINTS

### 7.1 Get All Orders
- **Method:** `GET`
- **Endpoint:** `/api/orders`
- **Description:** Retrieve all orders (Admin only)
- **Authentication:** Required (Bearer token, Admin only)
- **Status Codes:** 200 OK, 403 Forbidden

**Query Parameters:**
- `page` (int) - Page number (default: 0)
- `pageSize` (int) - Items per page (default: 10)
- `status` (string) - Filter by status (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "userId": 1,
      "totalPrice": 1299.99,
      "status": "PENDING",
      "items": [
        {
          "id": 1,
          "productId": 1,
          "productName": "Dell Laptop XPS 13",
          "quantity": 1,
          "price": 1299.99,
          "subtotal": 1299.99
        }
      ],
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00"
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 50,
  "totalPages": 5,
  "isFirst": true,
  "isLast": false
}
```

---

### 7.2 Create Order
- **Method:** `POST`
- **Endpoint:** `/api/orders`
- **Description:** Create new order from shopping cart
- **Authentication:** Required (Bearer token)
- **Status Codes:** 201 Created, 400 Bad Request

**Request Body:**
```json
{
  "addressId": 1,
  "paymentMethod": "CREDIT_CARD"
}
```

**Request Headers:**
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Response (201 Created):**
```json
{
  "status": 201,
  "message": "Created",
  "data": {
    "id": 1,
    "userId": 1,
    "addressId": 1,
    "totalPrice": 1299.99,
    "status": "PENDING",
    "paymentMethod": "CREDIT_CARD",
    "items": [
      {
        "id": 1,
        "productId": 1,
        "productName": "Dell Laptop XPS 13",
        "quantity": 1,
        "price": 1299.99,
        "subtotal": 1299.99
      }
    ],
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

**Validations:**
- Cart must not be empty
- Address must exist and belong to user
- Payment method must be valid

---

### 7.3 Get Order by ID
- **Method:** `GET`
- **Endpoint:** `/api/orders/{id}`
- **Description:** Retrieve specific order details
- **Parameters:** 
  - `id` (Long) - Order ID
- **Authentication:** Required (Bearer token)
- **Status Codes:** 200 OK, 404 Not Found

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "addressId": 1,
  "totalPrice": 1299.99,
  "status": "PENDING",
  "paymentMethod": "CREDIT_CARD",
  "items": [...],
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

---

### 7.4 Cancel Order
- **Method:** `PATCH`
- **Endpoint:** `/api/orders/{id}/cancel`
- **Description:** Cancel existing order (only if status is PENDING or CONFIRMED)
- **Parameters:** 
  - `id` (Long) - Order ID
- **Authentication:** Required (Bearer token)
- **Status Codes:** 200 OK, 400 Bad Request, 404 Not Found

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "totalPrice": 1299.99,
  "status": "CANCELLED",
  "items": [...],
  "updatedAt": "2024-01-15T11:00:00"
}
```

**Business Rules:**
- Can only cancel PENDING or CONFIRMED orders
- SHIPPED or DELIVERED orders cannot be cancelled
- Cancellation triggers inventory restoration

---

### 7.5 Update Order Status
- **Method:** `PATCH`
- **Endpoint:** `/api/orders/{id}/status`
- **Description:** Update order status (Admin only)
- **Parameters:** 
  - `id` (Long) - Order ID
- **Query Parameters:** 
  - `status` (string) - New status (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)
- **Authentication:** Required (Bearer token, Admin only)
- **Status Codes:** 200 OK, 400 Bad Request

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "totalPrice": 1299.99,
  "status": "CONFIRMED",
  "items": [...],
  "updatedAt": "2024-01-15T11:00:00"
}
```

**Valid Status Transitions:**
- PENDING → CONFIRMED, CANCELLED
- CONFIRMED → SHIPPED, CANCELLED
- SHIPPED → DELIVERED
- DELIVERED → (terminal state)
- CANCELLED → (terminal state)

---

### 7.6 Get Order Items
- **Method:** `GET`
- **Endpoint:** `/api/orders/{orderId}/items`
- **Description:** Retrieve items in specific order
- **Parameters:** 
  - `orderId` (Long) - Order ID
- **Authentication:** Required (Bearer token)
- **Status Codes:** 200 OK, 404 Not Found

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "orderId": 1,
    "productId": 1,
    "productName": "Dell Laptop XPS 13",
    "quantity": 1,
    "price": 1299.99,
    "subtotal": 1299.99
  }
]
```

---

## 8. REVIEW ENDPOINTS

### 8.1 Get Product Reviews
- **Method:** `GET`
- **Endpoint:** `/api/products/{productId}/reviews`
- **Description:** Get all reviews for a product (paginated)
- **Parameters:** 
  - `productId` (Long) - Product ID
- **Query Parameters:**
  - `page` (int) - Page number (default: 0)
  - `pageSize` (int) - Items per page (default: 10)
  - `sortBy` (string) - Sort field (createdAt, rating)
  - `sortDirection` (string) - ASC or DESC
- **Authentication:** None (Public)
- **Status Codes:** 200 OK

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "userId": 1,
      "userName": "John Doe",
      "productId": 1,
      "rating": 5,
      "comment": "Excellent product! Highly recommended.",
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00"
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 24,
  "totalPages": 3,
  "isFirst": true,
  "isLast": false
}
```

---

### 8.2 Create Review
- **Method:** `POST`
- **Endpoint:** `/api/products/{productId}/reviews`
- **Description:** Create review for product (user must have purchased it)
- **Parameters:** 
  - `productId` (Long) - Product ID
- **Authentication:** Required (Bearer token)
- **Status Codes:** 201 Created, 400 Bad Request

**Request Body:**
```json
{
  "rating": 5,
  "comment": "Excellent product! Highly recommended."
}
```

**Response (201 Created):**
```json
{
  "status": 201,
  "message": "Created",
  "data": {
    "id": 1,
    "userId": 1,
    "userName": "John Doe",
    "productId": 1,
    "rating": 5,
    "comment": "Excellent product! Highly recommended.",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

**Validations:**
- `rating` must be between 1 and 5
- `comment` is optional but recommended
- User must have purchased the product
- User can only have one review per product

---

### 8.3 Update Review
- **Method:** `PUT`
- **Endpoint:** `/api/reviews/{id}`
- **Description:** Update existing review (owner only)
- **Parameters:** 
  - `id` (Long) - Review ID
- **Authentication:** Required (Bearer token)
- **Status Codes:** 200 OK, 404 Not Found, 403 Forbidden

**Request Body:**
```json
{
  "rating": 4,
  "comment": "Good product, but could be better."
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "userName": "John Doe",
  "productId": 1,
  "rating": 4,
  "comment": "Good product, but could be better.",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T11:00:00"
}
```

---

### 8.4 Delete Review
- **Method:** `DELETE`
- **Endpoint:** `/api/reviews/{id}`
- **Description:** Delete review (owner or admin)
- **Parameters:** 
  - `id` (Long) - Review ID
- **Authentication:** Required (Bearer token)
- **Status Codes:** 204 No Content, 404 Not Found, 403 Forbidden

**Response (204 No Content):** No body

---

## 9. REVIEW IMAGE ENDPOINTS

### 9.1 Upload Review Image
- **Method:** `POST`
- **Endpoint:** `/api/reviews/{reviewId}/images`
- **Description:** Upload image for review
- **Parameters:** 
  - `reviewId` (Long) - Review ID
- **Authentication:** Required (Bearer token)
- **Status Codes:** 201 Created, 400 Bad Request

**Request Body:**
```json
{
  "bucketName": "reviews",
  "originalFileName": "review-image.jpg",
  "fileName": "reviews/123/uuid_review-image.jpg",
  "fileSize": 102400,
  "contentType": "image/jpeg",
  "publicUrl": "https://cdn.example.com/reviews/123/uuid_review-image.jpg"
}
```

**Response (201 Created):**
```json
{
  "status": 201,
  "message": "Created",
  "data": {
    "id": 1,
    "reviewId": 1,
    "bucketName": "reviews",
    "originalFileName": "review-image.jpg",
    "fileName": "reviews/123/uuid_review-image.jpg",
    "fileSize": 102400,
    "contentType": "image/jpeg",
    "publicUrl": "https://cdn.example.com/reviews/123/uuid_review-image.jpg"
  }
}
```

---

### 9.2 Delete Review Image
- **Method:** `DELETE`
- **Endpoint:** `/api/review-images/{id}`
- **Description:** Delete review image
- **Parameters:** 
  - `id` (Long) - Review image ID
- **Authentication:** Required (Bearer token)
- **Status Codes:** 204 No Content, 404 Not Found

**Response (204 No Content):** No body

---

## 10. PRODUCT IMAGE ENDPOINTS

### 10.1 Upload Product Image
- **Method:** `POST`
- **Endpoint:** `/api/products/{productId}/images`
- **Description:** Upload image for product (Admin/Seller)
- **Parameters:** 
  - `productId` (Long) - Product ID
- **Authentication:** Required (Bearer token, Admin/Seller)
- **Status Codes:** 201 Created, 400 Bad Request

**Request Body:**
```json
{
  "bucketName": "images",
  "originalFileName": "product-image.jpg",
  "fileName": "images/1/uuid_product-image.jpg",
  "fileSize": 204800,
  "contentType": "image/jpeg",
  "publicUrl": "https://cdn.example.com/images/1/uuid_product-image.jpg",
  "productId": 1
}
```

**Response (201 Created):**
```json
{
  "status": 201,
  "message": "Created",
  "data": {
    "id": 1,
    "productId": 1,
    "bucketName": "images",
    "originalFileName": "product-image.jpg",
    "fileName": "images/1/uuid_product-image.jpg",
    "fileSize": 204800,
    "contentType": "image/jpeg",
    "publicUrl": "https://cdn.example.com/images/1/uuid_product-image.jpg"
  }
}
```

---

### 10.2 Delete Product Image
- **Method:** `DELETE`
- **Endpoint:** `/api/product-images/{id}`
- **Description:** Delete product image
- **Parameters:** 
  - `id` (Long) - Product image ID
- **Authentication:** Required (Bearer token, Admin/Seller)
- **Status Codes:** 204 No Content, 404 Not Found

**Response (204 No Content):** No body

---

## 11. FILE/MEDIA ENDPOINTS

### 11.1 Upload Product Video
- **Method:** `POST`
- **Endpoint:** `/api/media/products/{productId}/videos/upload`
- **Description:** Upload video file for product
- **Parameters:** 
  - `productId` (Long) - Product ID
- **Content-Type:** `multipart/form-data`
- **Authentication:** Required (Bearer token, Admin/Seller)
- **Status Codes:** 201 Created, 400 Bad Request

**Form Parameters:**
- `file-video` (MultipartFile) - Video file (MP4, MOV, AVI, etc.)

**Response (201 Created):**
```json
{
  "status": 201,
  "message": "Created",
  "data": {
    "id": 1,
    "productId": 1,
    "bucketName": "videos",
    "originalFileName": "product-demo.mp4",
    "fileName": "videos/1/uuid_product-demo.mp4",
    "fileSize": 10485760,
    "contentType": "video/mp4",
    "publicUrl": "https://cdn.example.com/videos/1/uuid_product-demo.mp4"
  }
}
```

---

### 11.2 Upload Product Image (Multipart)
- **Method:** `POST`
- **Endpoint:** `/api/media/products/{productId}/images/upload`
- **Description:** Upload image file for product (multipart form)
- **Parameters:** 
  - `productId` (Long) - Product ID
- **Content-Type:** `multipart/form-data`
- **Authentication:** Required (Bearer token, Admin/Seller)
- **Status Codes:** 201 Created, 400 Bad Request

**Form Parameters:**
- `file` (MultipartFile) - Image file (JPG, PNG, GIF, etc.)

**Response (201 Created):**
```
"https://cdn.example.com/images/1/uuid_product-image.jpg"
```

---

### 11.3 Delete Product Video
- **Method:** `DELETE`
- **Endpoint:** `/api/media/products/{productId}/videos/{videoId}`
- **Description:** Delete product video
- **Parameters:**
  - `productId` (Long) - Product ID
  - `videoId` (Long) - Video ID
- **Authentication:** Required (Bearer token, Admin/Seller)
- **Status Codes:** 200 OK, 404 Not Found

**Response (200 OK):**
```json
{
  "message": "Video deleted successfully"
}
```

---

### 11.4 Delete Product Image (Media)
- **Method:** `DELETE`
- **Endpoint:** `/api/media/products/{productId}/images/{imageId}`
- **Description:** Delete product image via media endpoint
- **Parameters:**
  - `productId` (Long) - Product ID
  - `imageId` (Long) - Image ID
- **Authentication:** Required (Bearer token, Admin/Seller)
- **Status Codes:** 200 OK, 404 Not Found

**Response (200 OK):**
```json
{
  "message": "Image deleted successfully"
}
```

---

## 12. SALE/PROMOTION ENDPOINTS

### 12.1 Get All Sales
- **Method:** `GET`
- **Endpoint:** `/api/sales`
- **Description:** Retrieve all active sales/promotions
- **Authentication:** None (Public)
- **Status Codes:** 200 OK

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "code": "SUMMER2024",
    "description": "Summer Sale",
    "discountPercent": 20,
    "startDate": "2024-06-01T00:00:00",
    "endDate": "2024-08-31T23:59:59",
    "isActive": true
  }
]
```

---

### 12.2 Create Sale
- **Method:** `POST`
- **Endpoint:** `/api/sales`
- **Description:** Create new promotion/sale (Admin only)
- **Authentication:** Required (Bearer token, Admin only)
- **Status Codes:** 201 Created, 400 Bad Request

**Request Body:**
```json
{
  "code": "SUMMER2024",
  "description": "Summer Sale",
  "discountPercent": 20,
  "startDate": "2024-06-01T00:00:00",
  "endDate": "2024-08-31T23:59:59"
}
```

**Response (201 Created):**
```json
{
  "status": 201,
  "message": "Created",
  "data": {
    "id": 1,
    "code": "SUMMER2024",
    "description": "Summer Sale",
    "discountPercent": 20,
    "startDate": "2024-06-01T00:00:00",
    "endDate": "2024-08-31T23:59:59",
    "isActive": true
  }
}
```

---

### 12.3 Apply Coupon
- **Method:** `POST`
- **Endpoint:** `/api/coupons/apply`
- **Description:** Apply coupon code to order
- **Authentication:** Required (Bearer token)
- **Status Codes:** 200 OK, 400 Bad Request

**Request Body:**
```json
{
  "couponCode": "SUMMER2024"
}
```

**Response (200 OK):**
```json
{
  "couponCode": "SUMMER2024",
  "discountAmount": 260.00,
  "originalTotal": 1300.00,
  "finalTotal": 1040.00
}
```

**Validations:**
- Coupon code must exist
- Coupon must be active and within valid date range
- Coupon can only be used once per user

---

## 13. PAYMENT ENDPOINTS

### 13.1 Process Payment
- **Method:** `POST`
- **Endpoint:** `/api/orders/{orderId}/payments`
- **Description:** Process payment for order
- **Parameters:** 
  - `orderId` (Long) - Order ID
- **Authentication:** Required (Bearer token)
- **Status Codes:** 201 Created, 400 Bad Request, 404 Not Found

**Request Body (Credit Card):**
```json
{
  "paymentMethod": "CREDIT_CARD",
  "cardNumber": "4111111111111111",
  "cardholderName": "John Doe",
  "expiryDate": "12/25",
  "cvv": "123"
}
```

**Request Body (Other Methods):**
```json
{
  "paymentMethod": "BANK_TRANSFER"
}
```

**Response (201 Created):**
```json
{
  "status": 201,
  "message": "Created",
  "data": {
    "id": 1,
    "orderId": 1,
    "paymentMethod": "CREDIT_CARD",
    "amount": 1299.99,
    "status": "COMPLETED",
    "transactionId": "TXN123456789",
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

**Validations:**
- Order must exist
- Order status must be CONFIRMED
- Amount must match order total (minus applied coupons)

---

### 13.2 Payment Webhook
- **Method:** `POST`
- **Endpoint:** `/api/payments/webhook`
- **Description:** Handle payment provider webhooks for asynchronous payment notifications
- **Authentication:** None (Verified by provider signature)
- **Status Codes:** 200 OK, 400 Bad Request

**Request Body (Provider Specific):**
```json
{
  "transactionId": "TXN123456789",
  "orderId": 1,
  "amount": 1299.99,
  "status": "COMPLETED",
  "timestamp": "2024-01-15T10:30:00",
  "signature": "signature_from_provider"
}
```

**Response (200 OK):**
```json
{
  "status": 200,
  "message": "Webhook processed successfully"
}
```

**Note:** This endpoint is called by payment providers (e.g., Stripe, PayPal) and should not be called manually

---

## 14. EMAIL LOG ENDPOINTS

### 14.1 Get Email Logs
- **Method:** `GET`
- **Endpoint:** `/api/email-logs`
- **Description:** Retrieve email send history (Admin only)
- **Authentication:** Required (Bearer token, Admin only)
- **Status Codes:** 200 OK, 403 Forbidden

**Query Parameters:**
- `page` (int) - Page number (default: 0)
- `pageSize` (int) - Items per page (default: 10)
- `status` (string) - Filter by status (SENT, FAILED, BOUNCED)
- `startDate` (string) - Start date (ISO 8601)
- `endDate` (string) - End date (ISO 8601)

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "recipientEmail": "user@example.com",
      "subject": "Order Confirmation",
      "status": "SENT",
      "sentAt": "2024-01-15T10:30:00",
      "failureReason": null
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 1000,
  "totalPages": 100,
  "isFirst": true,
  "isLast": false
}
```

---

## 15. ERROR RESPONSES

### Error Response Format
All error responses follow this format:

```json
{
  "status": <error_code>,
  "message": "<error_message>",
  "data": null
}
```

### Common Error Status Codes

#### 400 Bad Request
```json
{
  "status": 400,
  "message": "Invalid request parameters or validation failed",
  "data": null
}
```

#### 401 Unauthorized
```json
{
  "status": 401,
  "message": "Authentication required or invalid/expired token",
  "data": null
}
```

#### 403 Forbidden
```json
{
  "status": 403,
  "message": "You do not have permission to perform this action",
  "data": null
}
```

#### 404 Not Found
```json
{
  "status": 404,
  "message": "Resource not found",
  "data": null
}
```

#### 409 Conflict
```json
{
  "status": 409,
  "message": "Resource already exists or conflict with existing data",
  "data": null
}
```

#### 500 Internal Server Error
```json
{
  "status": 500,
  "message": "Internal server error occurred",
  "data": null
}
```

---

## 16. AUTHENTICATION & AUTHORIZATION

### Bearer Token Authentication
All protected endpoints require the following header:

```
Authorization: Bearer <access_token>
```

### JWT Token Structure
- **Access Token:** Short-lived token (typically 1 hour) used for API requests
- **Refresh Token:** Long-lived token (typically 7 days) used to obtain new access token
- Both tokens are returned after successful login or registration

### Role-Based Access Control
API endpoints support the following roles:

- **CUSTOMER:** Regular user role
- **ADMIN:** Full administrative access
- **SELLER:** Can manage products and orders

### Token Refresh Flow
1. Request new access token using refresh token
2. Use new access token for subsequent API calls
3. If refresh token expires, user must login again

---

## 17. PAGINATION

### Paginated Endpoints
The following endpoints support pagination:

- Get All Users
- Search Products
- Get All Orders
- Get Product Reviews
- Get Email Logs

### Pagination Query Parameters
- `page` (int, default: 0) - Page number (0-indexed)
- `pageSize` (int, default: 10) - Number of items per page
- `sortBy` (string) - Field to sort by
- `sortDirection` (string, default: DESC) - ASC for ascending, DESC for descending

### Pagination Response Format
```json
{
  "content": [...],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 100,
  "totalPages": 10,
  "isFirst": true,
  "isLast": false
}
```

---

## 18. STATUS CODES SUMMARY

| Code | Meaning | Usage |
|------|---------|-------|
| 200 | OK | Successful GET, PUT, PATCH requests |
| 201 | Created | Successful POST request creating new resource |
| 204 | No Content | Successful DELETE request or action |
| 400 | Bad Request | Invalid input or validation failure |
| 401 | Unauthorized | Missing or invalid authentication token |
| 403 | Forbidden | Insufficient permissions for requested action |
| 404 | Not Found | Requested resource does not exist |
| 409 | Conflict | Resource already exists or data conflict |
| 500 | Internal Server Error | Server-side error |

---

## 19. RATE LIMITING & QUOTAS

Currently, no rate limiting is implemented. However, it is recommended to implement the following:

- API calls per second: 100
- API calls per hour: 10,000
- Maximum request body size: 10 MB
- Maximum response size: 50 MB

---

## 20. VERSIONING

All endpoints use the `/api` prefix for v1. Future versions may use `/api/v2`, `/api/v3`, etc.

Legacy endpoints (if any) use `/api/v1` prefix and are marked as deprecated.

---

**Last Updated:** 2026-01-15
**API Version:** 1.0
**Base URL:** http://localhost:8080