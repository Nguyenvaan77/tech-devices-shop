Triển khai sau cùng

1. Sử dụng mô hình phân quyền RBAC với các bảng Users - Roles - Permissions
User N --> N Role , Role N --> N Permission

 - Gồm các bảng: 
    1. users (Đã có sẵn trong DB)
    2. roles
    3. permissions
    4. user_roles
    5. role_permissions

 - Các giá trị của role 
    Role(ENUM) -> Customer 
        -> Business
        -> Admin

 - Ví dụ cho Permission -> CREATE_REVIEW, DELETE_REVIEWS, UPLOAD_PRODUCT_VIDEO, UPLOAD_PRODUCT_IMAGES, ...

 - Quyền hạn cho các permission
    A. ROLE_CUSTOMER
        1. PRODUCT_READ
        2. ORDER_CREATE
        3. ORDER_VIEW
        4. PROFILE_UPDATE
    B. ROLE_BUSINESS
        1.  PRODUCT_CREATE
        2.  PRODUCT_UPDATE
        3.  PRODUCT_DELETE
        4.  PRODUCT_READ
        5.  ORDER_VIEW
    c. ROLE_ADMIN
        1. USER_MANAGE
        2. PRODUCT_DELETE
        3. ORDER_VIEW
        4. SYSTEM_CONFIG

Tác dụng: Dùng để phân chia quyền cho user theo roles
Ví dụ User(An) --> Role(Brand) --> Permission (UPDATE_PRODUCT, CREATE_PRODUCT) 
=> Chỉ biết là mình có quyền cập nhật và tạo sản phẩm, nhưng Nhược điểm là An hoàn toàn thao tác được với tất cả các Sản phẩm của bất cứ ai (Không chấp nhận được)

2. Method Security (SpEL) => Giải vấn đề tồn đọng của mục 1 => Dùng để kiểm ra sự tương thuộc để cấp quyền của An chỉ đúng với tài nguyên An sở hữu (check product.brand_id == user.id)

Sử dụng annotation của Spring Security.

@EnableMethodSecurity
Ví dụ
@PreAuthorize("@productSecurity.isOwner(#productId, authentication)")
public void updateProduct(Long productId)
Security component
@Component
public class ProductSecurity {

    public boolean isOwner(Long productId, Authentication auth) {

        Long userId = ((CustomUserDetails)auth.getPrincipal()).getId();

        Product product = productRepository.findById(productId).orElseThrow();

        return product.getOwnerId().equals(userId);
    }
}
Ưu điểm
logic phân quyền tập trung
controller/service sạch hơn 
Nhược điểm
khó debug hơn

3. Tăng hiệu suất Bằng cách cache Role + Permission Bằng Redis nhằm giảm request xuống database lấy Permission