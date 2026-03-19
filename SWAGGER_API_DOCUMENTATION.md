# Tech Device Shop - Swagger API Documentation Guide

## 🎯 Overview
Your API now has comprehensive Swagger/OpenAPI documentation with full interactive testing capabilities.

## 📚 What's Included

### 1. **Springdoc OpenAPI Integration**
- ✅ **Swagger UI** - Interactive API documentation and testing interface
- ✅ **OpenAPI 3.0 Specification** - Machine-readable API definition
- ✅ **JSON Schema** - Detailed request/response schemas
- ✅ **Try-it-Out** - Test endpoints directly from the browser

### 2. **API Documentation Coverage**
All 7 API endpoints are documented:
- ✅ Users API (Register, Login, Get User, Get All Users)
- ✅ Products API (CRUD + Advanced Search with Pagination)
- ✅ Categories API (CRUD)
- ✅ Shopping Cart API (Get, Add, Remove)
- ✅ Orders API (Create, Get User Orders, Get Order)
- ✅ Addresses API (Create, Get, Delete)
- ✅ Reviews API (Create, Get Product Reviews)

### 3. **Response Types**
All endpoints return standardized `ResponseEntity<ApiResponse<T>>` format:
```json
{
  "status": 200,
  "message": "OK",
  "data": {
    // Response payload
  }
}
```

---

## 🚀 Quick Start

### 1. **Start the Application**
```bash
mvn spring-boot:run
```

### 2. **Access Swagger UI**
Open your browser and navigate to:
```
http://localhost:8080/swagger-ui.html
```

### 3. **View API Documentation**
```
http://localhost:8080/v3/api-docs
```
or in YAML format:
```
http://localhost:8080/v3/api-docs.yaml
```

---

## 📖 API Endpoints Documentation

### Users API - `/api/users`

#### 1️⃣ Register New User
```
POST /api/users
```
**Request:**
```json
{
  "email": "user@example.com",
  "password": "securePassword123",
  "firstName": "John",
  "lastName": "Doe"
}
```
**Response (201 Created):**
```json
{
  "status": 201,
  "message": "Resource created",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

#### 2️⃣ User Login
```
POST /api/users/login
```
**Request:**
```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```
**Response (200 OK):**
```json
{
  "status": 200,
  "message": "OK",
  "data": "LOGIN_SUCCESS"
}
```

#### 3️⃣ Get User by ID
```
GET /api/users/{id}
```
**Response (200 OK):**
```json
{
  "status": 200,
  "message": "OK",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

#### 4️⃣ Get All Users
```
GET /api/users
```
**Response (200 OK):**
```json
{
  "status": 200,
  "message": "OK",
  "data": [
    {
      "id": 1,
      "email": "user1@example.com"
    },
    {
      "id": 2,
      "email": "user2@example.com"
    }
  ]
}
```

---

### Products API - `/api/products`

#### 1️⃣ Get All Products
```
GET /api/products
```

#### 2️⃣ Search Products with Filters & Pagination
```
GET /api/products/search
```
**Query Parameters:**
```
GET /api/products/search?keyword=laptop&brand=Dell&minPrice=500&maxPrice=2000&page=0&pageSize=10&sortBy=price&sortDirection=ASC&categoryId=1&minRating=4.0
```

**Supported Parameters:**
- `keyword` - Search in product name/description
- `brand` - Filter by brand
- `status` - Filter by status (ACTIVE, INACTIVE, etc.)
- `categoryId` - Filter by category
- `minPrice` - Minimum price range
- `maxPrice` - Maximum price range
- `minRating` - Minimum rating (1-5)
- `page` - Page number (0-indexed, default: 0)
- `pageSize` - Items per page (default: 10)
- `sortBy` - Sort field (default: id)
- `sortDirection` - ASC or DESC (default: DESC)

**Response (200 OK):**
```json
{
  "status": 200,
  "message": "OK",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Dell XPS 13",
        "brand": "Dell",
        "price": 1299.99,
        "ratingAvg": 4.5,
        "ratingCount": 120
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 150,
    "totalPages": 15,
    "isFirst": true,
    "isLast": false
  }
}
```

#### 3️⃣ Get Product by ID
```
GET /api/products/{id}
```

#### 4️⃣ Create Product
```
POST /api/products
```
**Request:**
```json
{
  "name": "Dell XPS 13",
  "brand": "Dell",
  "description": "High-performance laptop",
  "price": 1299.99,
  "categoryId": 1
}
```

#### 5️⃣ Update Product
```
PUT /api/products/{id}
```

#### 6️⃣ Delete Product
```
DELETE /api/products/{id}
```

---

### Categories API - `/api/categories`

#### 1️⃣ Get All Categories
```
GET /api/categories
```

#### 2️⃣ Get Category by ID
```
GET /api/categories/{id}
```

#### 3️⃣ Create Category
```
POST /api/categories
```
**Request:**
```json
{
  "name": "Laptops",
  "description": "Computer Laptops"
}
```

---

### Shopping Cart API - `/api/carts`

#### 1️⃣ Get User's Cart
```
GET /api/carts/{userId}
```

#### 2️⃣ Add Item to Cart
```
POST /api/carts/{userId}/items
```
**Request:**
```json
{
  "productId": 1,
  "quantity": 2
}
```

#### 3️⃣ Remove Item from Cart
```
DELETE /api/carts/{userId}/items/{productId}
```

---

### Orders API - `/api/orders`

#### 1️⃣ Create Order
```
POST /api/orders/{userId}
```
**Request:**
```json
{
  "paymentMethod": "CREDIT_CARD",
  "totalPrice": 2599.98
}
```

#### 2️⃣ Get User's Orders
```
GET /api/orders/user/{userId}
```

#### 3️⃣ Get Order by ID
```
GET /api/orders/{id}
```

---

### Addresses API - `/api/addresses`

#### 1️⃣ Create Address
```
POST /api/addresses/{userId}
```
**Request:**
```json
{
  "receiverName": "John Doe",
  "phone": "+84123456789",
  "province": "Ho Chi Minh",
  "district": "District 1",
  "ward": "Ward 1",
  "detail": "123 Main Street"
}
```

#### 2️⃣ Get User's Addresses
```
GET /api/addresses/user/{userId}
```

#### 3️⃣ Delete Address
```
DELETE /api/addresses/{id}
```

---

### Reviews API - `/api/reviews`

#### 1️⃣ Create Product Review
```
POST /api/reviews/{userId}
```
**Request:**
```json
{
  "productId": 1,
  "rating": 5,
  "comment": "Excellent product! Highly recommended.",
  "title": "Great experience"
}
```

#### 2️⃣ Get Product Reviews
```
GET /api/reviews/products/{productId}
```

---

## 🔍 Swagger UI Features

### Feature 1: Endpoint Overview
- View all available endpoints grouped by tags
- See HTTP method and path for each endpoint
- Quick description of what each endpoint does

### Feature 2: Detailed Documentation
- **Parameters** - All query, path, and body parameters
- **Request/Response** - Example payloads and schemas
- **Status Codes** - All possible response status codes
- **Authentication** - Required field indicators

### Feature 3: Try It Out
1. Click the "Try it out" button on any endpoint
2. Fill in the required parameters
3. Click "Execute"
4. View the actual API response
5. See curl command and response headers

### Feature 4: Schema Details
- Detailed JSON schema for every request/response
- Field descriptions and data types
- Required vs optional fields
- Example values for each field

---

## 🔐 Error Responses

All endpoints return consistent error responses:

### 400 Bad Request
```json
{
  "status": 400,
  "message": "Product name is required",
  "data": null
}
```

### 404 Not Found
```json
{
  "status": 404,
  "message": "Product not found with id: 999",
  "data": null
}
```

### 409 Conflict
```json
{
  "status": 409,
  "message": "Email already registered",
  "data": null
}
```

### 500 Internal Server Error
```json
{
  "status": 500,
  "message": "Internal Server Error",
  "data": null
}
```

---

## 📊 API Documentation Location

| Resource | URL |
|----------|-----|
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` |
| **OpenAPI JSON** | `http://localhost:8080/v3/api-docs` |
| **OpenAPI YAML** | `http://localhost:8080/v3/api-docs.yaml` |

---

## 🛠️ Configuration

### Springdoc Properties (application.properties)
```properties
# Swagger UI path
springdoc.swagger-ui.path=/swagger-ui.html

# API documentation path
springdoc.api-docs.path=/v3/api-docs

# Enable/disable Swagger UI
springdoc.swagger-ui.enabled=true

# Display request duration
springdoc.swagger-ui.display-request-duration=true

# Display operation ID
springdoc.swagger-ui.display-operation-id=true
```

---

## 📝 Best Practices

### For Developers Using This API:

1. **Check Swagger UI First**
   - Always check `/swagger-ui.html` for latest API documentation
   - Test endpoints using the "Try it out" button

2. **Use Correct Status Codes**
   - 200 for GET, PUT, DELETE (with data)
   - 201 for POST (resource created)
   - 204 for DELETE (no content)
   - 400 for bad requests
   - 404 for not found
   - 409 for conflicts

3. **Follow Response Format**
   - All responses follow: `{status, message, data}`
   - Check `status` field for success/failure
   - Use `data` field for actual payload

4. **Pagination Tips**
   - Always include `page` and `pageSize` for search endpoints
   - Default page: 0 (first page)
   - Check `isLast` to know when to stop pagination
   - Use `totalPages` to show pagination controls

5. **Error Handling**
   - Always check `status` code for errors
   - Log the `message` field for debugging
   - Handle different status codes appropriately

---

## 🔗 Integration Examples

### JavaScript/Fetch
```javascript
// Get products with search
fetch('http://localhost:8080/api/products/search?keyword=laptop&page=0&pageSize=10')
  .then(res => res.json())
  .then(data => {
    if (data.status === 200) {
      console.log('Products:', data.data.content);
    } else {
      console.error('Error:', data.message);
    }
  });
```

### cURL
```bash
# Search products
curl "http://localhost:8080/api/products/search?keyword=laptop&page=0&pageSize=10"

# Create product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dell XPS 13",
    "price": 1299.99,
    "categoryId": 1
  }'
```

### Postman
1. Open Postman
2. Paste: `http://localhost:8080/v3/api-docs`
3. Click "Import"
4. All endpoints will be imported automatically!

---

## ✅ All Controllers Updated

All controllers now return `ResponseEntity<ApiResponse<T>>` format:

- ✅ UserController
- ✅ ProductController
- ✅ CartController
- ✅ CategoryController
- ✅ OrderController
- ✅ AddressController
- ✅ ReviewController

---

## 📚 Additional Resources

- **Springdoc OpenAPI Docs**: https://springdoc.org/
- **OpenAPI 3.0 Spec**: https://spec.openapis.org/oas/v3.0.3
- **Swagger UI Docs**: https://swagger.io/
- **RESTful API Best Practices**: https://restfulapi.net/

---

## ✨ Summary

Your API is now:
- ✅ Fully documented with Swagger UI
- ✅ OpenAPI 3.0 compliant
- ✅ Interactive testing available
- ✅ Consistent response format
- ✅ Comprehensive error handling
- ✅ Production-ready documentation

Visit `http://localhost:8080/swagger-ui.html` to explore your API!

