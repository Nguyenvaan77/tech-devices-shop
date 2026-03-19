# Tech Device Shop - Swagger/OpenAPI Quick Reference

## 🎯 Quick Links

### Access Your API Documentation

| Item | URL |
|------|-----|
| **Swagger UI (Interactive)** | http://localhost:8080/swagger-ui.html |
| **OpenAPI JSON** | http://localhost:8080/v3/api-docs |
| **OpenAPI YAML** | http://localhost:8080/v3/api-docs.yaml |

---

## 🚀 How to Use

### 1️⃣ Start the Application
```bash
cd d:\IT\Code\techdevice\tech-devices-shop-backend
mvn spring-boot:run
```

### 2️⃣ Open Swagger UI
Navigate to: **http://localhost:8080/swagger-ui.html**

You should see:
- 7 API tags (Users, Products, Categories, Orders, Addresses, Cart, Reviews)
- All endpoints listed with methods and paths
- Full request/response documentation

### 3️⃣ Test an Endpoint
1. Click on any endpoint (e.g., `GET /api/products`)
2. Click "Try it out"
3. Fill in required parameters
4. Click "Execute"
5. View the response

---

## 📋 API Tags

Each group of endpoints is organized by tags:

1. **Users** - User management (Register, Login, Get User)
2. **Products** - Product CRUD & Search with Pagination
3. **Categories** - Category management
4. **Shopping Cart** - Cart operations
5. **Orders** - Order management
6. **Addresses** - User address management
7. **Reviews** - Product reviews

---

## 🔍 What You Can Do in Swagger UI

✅ **View all endpoints** - See all available API routes
✅ **Read documentation** - Description for every endpoint
✅ **Check parameters** - See required and optional fields
✅ **View schemas** - JSON structure for requests/responses
✅ **Try endpoints** - Test API without external tools
✅ **See examples** - Sample request/response data
✅ **Check status codes** - All possible response codes

---

## 📚 Key Features

### Endpoint Documentation Includes:
- **Summary** - One-line description
- **Description** - Detailed explanation
- **Parameters** - Required/optional query, path, body params
- **Request Body** - JSON schema and examples
- **Responses** - Status codes and response schemas
- **Error Codes** - Possible error responses

### Response Format:
All endpoints return this standardized format:
```json
{
  "status": 200,
  "message": "OK",
  "data": {
    // Your response data here
  }
}
```

---

## 🛠️ For Different Tools

### Postman
1. Open Postman
2. Click "Import"
3. Paste: `http://localhost:8080/v3/api-docs`
4. All endpoints imported automatically

### Visual Studio Code (REST Client)
```
GET http://localhost:8080/api/products/search?keyword=laptop&page=0&pageSize=10
```

### JavaScript (Fetch)
```javascript
const response = await fetch('http://localhost:8080/api/products/search?keyword=laptop');
const data = await response.json();
console.log(data.data.content);
```

### cURL
```bash
curl http://localhost:8080/api/products/search?keyword=laptop&page=0&pageSize=10
```

---

## ✨ Controllers Updated

All 7 controllers now use proper type hints:

```java
// ❌ Old Format (Removed)
public Object getProducts() { ... }

// ✅ New Format (All Controllers)
public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts() { ... }
```

Benefits:
- Type safety
- Better IDE autocomplete
- Clearer API contracts
- Swagger generates better documentation

---

## 📝 Key Endpoints

### Search & Pagination Example
```
GET /api/products/search
```
Parameters:
- `keyword=laptop` - Search term
- `minPrice=500` - Minimum price
- `maxPrice=2000` - Maximum price
- `page=0` - Page number
- `pageSize=10` - Items per page
- `sortBy=price` - Sort field
- `sortDirection=ASC` - Sort direction

### Create Resource Example
```
POST /api/products
Content-Type: application/json

{
  "name": "Dell XPS 13",
  "brand": "Dell",
  "price": 1299.99,
  "categoryId": 1
}
```

### Delete Resource Example
```
DELETE /api/products/1
```

---

## 🔐 Common HTTP Status Codes

| Code | Meaning | When Used |
|------|---------|-----------|
| **200** | OK | GET, PUT, DELETE with response |
| **201** | Created | POST successful |
| **204** | No Content | DELETE successful |
| **400** | Bad Request | Invalid input data |
| **404** | Not Found | Resource doesn't exist |
| **409** | Conflict | Resource conflict (duplicate) |
| **500** | Server Error | Unexpected error |

---

## 🎓 Learning Path

1. **Start with Swagger UI** - Read through all endpoints
2. **Try Simple Endpoints** - GET requests first
3. **Test Search** - Try `/api/products/search` with filters
4. **Create Resources** - Try POST endpoints
5. **Test Errors** - Try invalid data to see error responses
6. **Advanced Filtering** - Combine multiple filters

---

## 🚨 Troubleshooting

### Issue: Swagger UI shows "Error Loading API definition"
**Solution:** 
- Make sure app is running on port 8080
- Check `application.properties` has correct configuration
- Try accessing `http://localhost:8080/v3/api-docs` directly

### Issue: Endpoints not showing up
**Solution:**
- Verify controllers have `@Tag` annotation
- Check endpoints have `@Operation` annotation
- Rebuild project: `mvn clean compile`

### Issue: Can't test endpoint
**Solution:**
- Check required parameters are filled
- Verify data format matches schema
- Look at error message for validation issues

---

## 📊 Dependencies Added

```xml
<!-- Springdoc OpenAPI (Swagger UI) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>

<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-api</artifactId>
    <version>2.3.0</version>
</dependency>
```

---

## 🎨 Customization

### Update API Info
Edit `OpenAPIConfig.java`:
```java
.title("My Custom Title")
.version("2.0.0")
.description("My custom description")
```

### Change Swagger UI Path
In `application.properties`:
```properties
springdoc.swagger-ui.path=/docs
```

Then access at: `http://localhost:8080/docs`

---

## ✅ Verification Checklist

Before deployment:
- ✅ All controllers return `ResponseEntity<ApiResponse<T>>`
- ✅ All endpoints have `@Operation` annotations
- ✅ All DTOs have `@Schema` annotations
- ✅ Swagger UI loads at `/swagger-ui.html`
- ✅ Can test endpoints interactively
- ✅ OpenAPI spec available at `/v3/api-docs`
- ✅ All error responses documented

---

## 🔗 References

- [Springdoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI 3.0 Specification](https://spec.openapis.org/oas/v3.0.3)
- [Swagger UI Guide](https://swagger.io/tools/swagger-ui/)
- [Spring Boot & OpenAPI Best Practices](https://spring.io/projects/spring-graphql)

---

## 💡 Tips & Tricks

1. **Export API Definition** - Right-click on Swagger UI and export as JSON/YAML
2. **Language Docs** - Generate client libraries from OpenAPI spec
3. **Team Sharing** - Share the Swagger UI URL with frontend team
4. **API Versioning** - Update version in OpenAPIConfig for releases
5. **Postman Sync** - Keep Postman collection synced with Swagger

---

## 🎉 You're Done!

Your API is now:
- ✨ Fully documented with Swagger UI
- 🔒 Type-safe with proper return types
- 📖 OpenAPI 3.0 compliant
- 🧪 Interactive and testable
- 🚀 Production-ready

**Access your API documentation at:**
# 👉 http://localhost:8080/swagger-ui.html

