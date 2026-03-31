

2. USERS
Method	Endpoint	Mô tả
GET	/api/users	Danh sách user (admin)
GET	/api/users/{id}	Chi tiết user
GET	/api/users/me	User hiện tại
PATCH	/api/users/{id}	Cập nhật user
DELETE	/api/users/{id}	Soft delete
3. ADDRESSES
Method	Endpoint	Mô tả
GET	/api/users/{userId}/addresses	Danh sách địa chỉ
POST	/api/users/{userId}/addresses	Tạo địa chỉ
PUT	/api/addresses/{id}	Cập nhật
DELETE	/api/addresses/{id}	Xóa
4. CATEGORIES
Method	Endpoint	Mô tả
GET	/api/categories	Danh sách
GET	/api/categories/tree	Cây category
GET	/api/categories/{id}	Chi tiết
POST	/api/categories	Tạo (admin)
PUT	/api/categories/{id}	Cập nhật
DELETE	/api/categories/{id}	Xóa
5. PRODUCTS
Method	Endpoint	Mô tả
GET	/api/products	Danh sách (filter, paging)
GET	/api/products/{id}	Chi tiết
GET	/api/products/search	Tìm kiếm
POST	/api/products	Tạo
PUT	/api/products/{id}	Cập nhật
DELETE	/api/products/{id}	Xóa
6. PRODUCT ITEMS (SKU)
Method	Endpoint	Mô tả
GET	/api/products/{productId}/items	Danh sách SKU
POST	/api/products/{productId}/items	Tạo SKU
PUT	/api/product-items/{id}	Cập nhật
DELETE	/api/product-items/{id}	Xóa
7. PRODUCT IMAGES
Method	Endpoint	Mô tả
POST	/api/products/{productId}/images	Upload
DELETE	/api/product-images/{id}	Xóa
8. CART
Method	Endpoint	Mô tả
GET	/api/cart	Lấy giỏ hàng
POST	/api/cart/items	Thêm item
PATCH	/api/cart/items/{id}	Cập nhật số lượng
DELETE	/api/cart/items/{id}	Xóa item
DELETE	/api/cart	Clear cart
9. ORDERS
Method	Endpoint	Mô tả
POST	/api/orders	Tạo order
GET	/api/orders	Danh sách order
GET	/api/orders/{id}	Chi tiết
PATCH	/api/orders/{id}/cancel	Hủy
PATCH	/api/orders/{id}/status	Update trạng thái (admin)
10. ORDER ITEMS (internal)
Method	Endpoint	Mô tả
GET	/api/orders/{orderId}/items	Danh sách item
11. PAYMENTS
Method	Endpoint	Mô tả
POST	/api/orders/{orderId}/payments	Thanh toán
POST	/api/payments/webhook	Callback gateway
12. REVIEWS
Method	Endpoint	Mô tả
GET	/api/products/{productId}/reviews	Danh sách
POST	/api/products/{productId}/reviews	Tạo
PUT	/api/reviews/{id}	Cập nhật
DELETE	/api/reviews/{id}	Xóa
13. REVIEW IMAGES
Method	Endpoint	Mô tả
POST	/api/reviews/{reviewId}/images	Upload
DELETE	/api/review-images/{id}	Xóa
14. ATTRIBUTE SYSTEM
Method	Endpoint	Mô tả
GET	/api/attributes	Danh sách
POST	/api/attributes	Tạo
POST	/api/product-options	Tạo option
GET	/api/products/{productId}/attributes	Lấy attribute
15. SALES / COUPONS
Method	Endpoint	Mô tả
GET	/api/sales	Danh sách
POST	/api/sales	Tạo
POST	/api/coupons/apply	Áp dụng
16. EMAIL LOGS (ADMIN)
Method	Endpoint	Mô tả
GET	/api/email-logs	Xem log