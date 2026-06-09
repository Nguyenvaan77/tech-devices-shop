# Kế Hoạch Chi Tiết: Đơn Giản Hóa Database (Bỏ product_items & attributes)

## 📋 Tóm Tắt Thay Đổi

### ❌ Bỏ Đi (Xóa Hoàn Toàn)
1. **product_items** (bảng SKU/biến thể)
2. **attribute_type** (loại thuộc tính)
3. **product_item_has_attribute_type** (ánh xạ thuộc tính)
4. **product_option** (tùy chọn sản phẩm)
5. **Inventory** entity (nếu có)

### ✏️ Cập Nhật (Sửa Đổi)
1. **cart_items**: `product_item_id` → `product_id` (FK trực tiếp PRODUCTS)
2. **order_items**: `product_item_id` → `product_id` (FK trực tiếp PRODUCTS)
3. **products**: Thêm `quantity_in_stock` (từ product_option) + `price` (từ product_items.original_price)
4. **product_items entity**: ✂️ DELETE
5. **AttributeType entity**: ✂️ DELETE
6. **ProductItemHasAttributeType entity**: ✂️ DELETE
7. **ProductOption entity**: ✂️ DELETE
8. **Inventory entity**: ✂️ DELETE

---

## 🗄️ SCHEMA SQL - Thay Đổi

### Bảng PRODUCTS - Thêm Cột
```sql
ALTER TABLE products ADD COLUMN (
    price NUMERIC(12,2) NOT NULL,           -- Giá bán (từ product_items.original_price)
    quantity_in_stock INT DEFAULT 0         -- Tồn kho (từ product_option.quantity_in_stock)
);
```

### Bảng CART_ITEMS - Thay Đổi FK
```sql
-- Xóa cột product_item_id, thay bằng product_id
ALTER TABLE cart_items DROP CONSTRAINT fk_cart_items_product_item;
ALTER TABLE cart_items DROP COLUMN product_item_id;
ALTER TABLE cart_items ADD COLUMN product_id BIGINT NOT NULL;
ALTER TABLE cart_items ADD CONSTRAINT fk_cart_items_product 
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT;
CREATE INDEX idx_cart_items_product ON cart_items(product_id);
```

### Bảng ORDER_ITEMS - Thay Đổi FK
```sql
-- Xóa cột product_item_id, thay bằng product_id
ALTER TABLE order_items DROP CONSTRAINT fk_order_items_product_item;
ALTER TABLE order_items DROP COLUMN product_item_id;
ALTER TABLE order_items ADD COLUMN product_id BIGINT NOT NULL;
ALTER TABLE order_items ADD CONSTRAINT fk_order_items_product 
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT;
CREATE INDEX idx_order_items_product ON order_items(product_id);
```

### Bảng Bỏ Đi
```sql
-- Xóa toàn bộ các bảng không cần
DROP TABLE product_option;
DROP TABLE product_item_has_attribute_type;
DROP TABLE attribute_type;
DROP TABLE product_items;
DROP TABLE inventory;  -- Nếu tồn tại
```

---

## 🏗️ Java Entities - Thay Đổi

### 1. Product.java - Thêm Trường
```java
@Column(name = "price", nullable = false)
private BigDecimal price;  // Giá sản phẩm

@Column(name = "quantity_in_stock")
private Integer quantityInStock = 0;  // Tồn kho
```

### 2. CartItem.java - Thay Đổi Quan Hệ
```java
// ❌ Xóa: @ManyToOne private ProductItem productItem;
// ✅ Thêm:
@ManyToOne
@JoinColumn(name = "product_id", nullable = false)
private Product product;  // FK trực tiếp PRODUCTS
```

### 3. OrderItem.java - Thay Đổi Quan Hệ
```java
// ❌ Xóa: @ManyToOne private ProductItem productItem;
// ✅ Thêm:
@ManyToOne
@JoinColumn(name = "product_id", nullable = false)
private Product product;  // FK trực tiếp PRODUCTS
```

### 4. Entities Bỏ Đi
```
❌ ProductItem.java
❌ AttributeType.java
❌ ProductItemHasAttributeType.java
❌ ProductOption.java
❌ Inventory.java
```

---

## 📦 Repository - Thay Đổi

### CartItemRepository.java
```java
// ❌ Xóa: findByProductItem()
// ✅ Thêm:
@Query("SELECT ci FROM CartItem ci WHERE ci.product.id = :productId AND ci.cart.id = :cartId")
Optional<CartItem> findByProductIdAndCartId(Long productId, Long cartId);
```

### OrderItemRepository.java
```java
// Tương tự CartItemRepository
// Thay đổi queries từ productItem → product
```

### ProductRepository.java
```java
// Có thể thêm query cho lọc theo giá, tồn kho
@Query("SELECT p FROM Product p WHERE p.quantityInStock > 0")
List<Product> findInStock();

@Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
```

---

## 🔧 Service - Thay Đổi

### CartService
```java
// addToCart(Long cartId, Long productId, int quantity)
// ❌ Cũ: productItemRepository.findById()
// ✅ Mới: productRepository.findById() → kiểm tra quantityInStock

public void addToCart(Long cartId, Long productId, int quantity) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    
    if (product.getQuantityInStock() < quantity) {
        throw new InsufficientStockException("Not enough stock");
    }
    
    Cart cart = cartRepository.findById(cartId)
        .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
    
    CartItem cartItem = cartItemRepository
        .findByProductIdAndCartId(productId, cartId)
        .orElse(new CartItem());
    
    cartItem.setProduct(product);
    cartItem.setQuantity(quantity);
    cartItemRepository.save(cartItem);
}
```

### OrderService
```java
// createOrder(Long userId, List<CartItem> cartItems)
// ❌ Cũ: Xử lý productItem
// ✅ Mới: Xử lý trực tiếp product

public Order createOrder(Long userId, List<CartItem> cartItems) {
    Order order = new Order();
    order.setUser(userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found")));
    
    BigDecimal totalPrice = BigDecimal.ZERO;
    List<OrderItem> orderItems = new ArrayList<>();
    
    for (CartItem cartItem : cartItems) {
        Product product = cartItem.getProduct();
        
        if (product.getQuantityInStock() < cartItem.getQuantity()) {
            throw new InsufficientStockException("Not enough stock for " + product.getName());
        }
        
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(product.getPrice());
        orderItems.add(orderItem);
        
        totalPrice = totalPrice.add(product.getPrice()
            .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        
        // Cập nhật tồn kho
        product.setQuantityInStock(product.getQuantityInStock() - cartItem.getQuantity());
        productRepository.save(product);
    }
    
    order.setOrderItems(orderItems);
    order.setTotalPrice(totalPrice);
    return orderRepository.save(order);
}
```

### ProductService
```java
// Thêm phương thức kiểm tra tồn kho
public boolean isInStock(Long productId, int quantity) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    return product.getQuantityInStock() >= quantity;
}

// Cập nhật tồn kho khi bán
public void decreaseStock(Long productId, int quantity) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    
    if (product.getQuantityInStock() < quantity) {
        throw new InsufficientStockException("Not enough stock");
    }
    
    product.setQuantityInStock(product.getQuantityInStock() - quantity);
    productRepository.save(product);
}

// Hoàn lại tồn kho khi hủy đơn
public void increaseStock(Long productId, int quantity) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    product.setQuantityInStock(product.getQuantityInStock() + quantity);
    productRepository.save(product);
}
```

---

## 📄 DTO - Thay Đổi

### CartItemDTO
```java
@Data
@Builder
public class CartItemDTO {
    private Long id;
    private Long productId;      // ❌ productItemId → ✅ productId
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal; // quantity * price
}
```

### OrderItemDTO
```java
@Data
@Builder
public class OrderItemDTO {
    private Long id;
    private Long productId;      // ❌ productItemId → ✅ productId
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal; // quantity * price
}
```

### ProductDTO
```java
@Data
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;           // ✅ Thêm
    private Integer quantityInStock;    // ✅ Thêm
    private Double ratingAvg;
    private Integer reviewCount;
    private Long categoryId;
    private List<String> imageUrls;
    private List<String> videoUrls;
}
```

---

## 📋 Danh Sách File Cần Sửa

### 1. SQL Files
```
✏️ db/create-schema.sql
✏️ db/migration-simplify.sql (NEW - Migration script)
```

### 2. Entity Classes (src/main/java/com/example/web/entity/)
```
✏️ Product.java (thêm price, quantityInStock)
✏️ CartItem.java (productItem → product)
✏️ OrderItem.java (productItem → product)
❌ ProductItem.java (XÓA)
❌ AttributeType.java (XÓA)
❌ ProductItemHasAttributeType.java (XÓA)
❌ ProductOption.java (XÓA)
❌ Inventory.java (XÓA nếu có)
```

### 3. Repository Classes (src/main/java/com/example/web/repository/)
```
✏️ CartItemRepository.java (cập nhật queries)
✏️ OrderItemRepository.java (cập nhật queries)
✏️ ProductRepository.java (thêm methods mới)
❌ ProductItemRepository.java (XÓA)
❌ AttributeTypeRepository.java (XÓA)
❌ ProductItemHasAttributeTypeRepository.java (XÓA)
❌ ProductOptionRepository.java (XÓA)
❌ InventoryRepository.java (XÓA nếu có)
```

### 4. Service Classes (src/main/java/com/example/web/service/)
```
✏️ CartService/CartServiceImpl.java (cập nhật logic)
✏️ OrderService/OrderServiceImpl.java (cập nhật logic)
✏️ ProductService/ProductServiceImpl.java (cập nhật logic)
✏️ CartItemService/CartItemServiceImpl.java (nếu có)
✏️ OrderItemService/OrderItemServiceImpl.java (nếu có)
❌ ProductItemService.java (XÓA)
❌ AttributeService.java (XÓA nếu có)
```

### 5. DTO Classes (src/main/java/com/example/web/dto/)
```
✏️ dto/cart/CartItemDTO.java (productId)
✏️ dto/order/OrderItemDTO.java (productId)
✏️ dto/product/ProductDTO.java (thêm price, quantityInStock)
❌ dto/productitem/ (XÓA folder)
❌ dto/attribute/ (XÓA folder)
❌ Các DTO liên quan product_items, attributes
```

### 6. Mapper Classes (src/main/java/com/example/web/mapper/)
```
✏️ CartItemMapper.java (cập nhật mapping)
✏️ OrderItemMapper.java (cập nhật mapping)
✏️ ProductMapper.java (cập nhật mapping)
❌ ProductItemMapper.java (XÓA)
```

### 7. Controller Classes (src/main/java/com/example/web/controller/)
```
✏️ CartController.java (kiểm tra endpoint)
✏️ OrderController.java (kiểm tra endpoint)
✏️ ProductController.java (kiểm tra endpoint)
```

### 8. Documentation
```
✏️ SYSTEM_ARCHITECTURE.md (cập nhật diagram)
✏️ docs/endpoints.md (cập nhật nếu có)
```

---

## ⚠️ Rủi Ro & Lưu Ý

### 1. Data Migration
- **Cảnh báo**: Nếu production DB có data, cần backup trước khi migration
- **Giải pháp**: Tạo migration script phụ (migration-simplify.sql)

### 2. Tồn Kho Trực Tiếp Trên Products
- **Vấn đề**: Nếu cùng sản phẩm có nhiều biến thể, không thể quản lý riêng
- **Giải pháp**: Tạm thời dùng `quantity_in_stock` chung cho cả sản phẩm
- **Tương lai**: Nếu cần lại, có thể thêm lại product_items

### 3. Giá Sản Phẩm
- **Vấn đề**: Tất cả biến thể giá giống nhau
- **Giải pháp**: Dùng single price trên Products table

### 4. Backward Compatibility
- **Issue**: Nếu API clients đang sử dụng productItemId
- **Giải pháp**: Cần cập nhật toàn bộ clients

---

## 🔍 Quy Trình Kiểm Tra

### Sau Khi Cập Nhật:
1. ✅ Build project: `mvn clean package`
2. ✅ Kiểm tra compilation errors
3. ✅ Run migration script trên DB test
4. ✅ Run unit tests (nếu có)
5. ✅ Manual test các endpoint:
   - POST /api/carts/items (thêm vào giỏ)
   - GET /api/carts/{id} (xem giỏ)
   - POST /api/orders (tạo đơn hàng)
   - GET /api/orders/{id} (xem đơn hàng)

---

## 📊 Tỷ Lệ Thay Đổi

| Loại | Số Lượng |
|------|---------|
| File xóa | 8 |
| File sửa | 15+ |
| Bảng xóa | 4 |
| Bảng sửa | 3 |
| Dòng code ảnh hưởng | ~500+ |

---

## ✅ Danh Sách Hoàn Thành (Checklist)

- [ ] Sửa schema SQL
- [ ] Cập nhật Product entity
- [ ] Cập nhật CartItem entity
- [ ] Cập nhật OrderItem entity
- [ ] Xóa ProductItem entity
- [ ] Xóa AttributeType entity
- [ ] Xóa ProductItemHasAttributeType entity
- [ ] Xóa ProductOption entity
- [ ] Cập nhật repositories
- [ ] Cập nhật services
- [ ] Cập nhật DTOs
- [ ] Cập nhật mappers
- [ ] Cập nhật controllers
- [ ] Build & verify
- [ ] Cập nhật SYSTEM_ARCHITECTURE.md

---

Kế hoạch này sẽ được thực hiện từng bước một cách cẩn thận!
