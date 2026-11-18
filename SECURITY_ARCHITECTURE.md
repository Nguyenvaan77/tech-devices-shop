# Tech Device Shop - Kiến Trúc Bảo Mật (Security Architecture)

Tài liệu này mô tả chi tiết về kiến trúc bảo mật được áp dụng trong hệ thống **Tech Device Shop**, bao gồm 3 thành phần chính: **JWT (JSON Web Token)**, **OAuth 2.0**, và **RBAC (Role-Based Access Control)**.

---

## 1. Xác thực bằng JWT (JSON Web Token)

Hệ thống sử dụng cơ chế xác thực Stateless thông qua JWT. Thay vì lưu trữ session trên server, toàn bộ thông tin xác thực (claims, authorities) được mã hóa trong token và gửi kèm trong header của mỗi request.

### 1.1. Luồng hoạt động (Workflow)
1. **Đăng nhập**: Người dùng gửi thông tin (email/password) tới endpoint `/api/auth/login`.
2. **Cấp phát Token**: Nếu thông tin hợp lệ, hệ thống (thông qua `JwtService`) sẽ tạo ra `access_token` và `refresh_token` và trả về cho client.
3. **Sử dụng Token**: Client lưu token và gửi kèm trong header `Authorization: Bearer <token>` cho các request tiếp theo.
4. **Xác thực Request (PreFilter)**: 
   - Lớp `PreFilter` (kế thừa `OncePerRequestFilter`) sẽ chặn mọi request.
   - Kiểm tra header `Authorization`. Nếu có token, nó sẽ trích xuất `username` và danh sách quyền (`authorities`).
   - Gọi `jwtService.isValid()` để kiểm tra chữ ký và thời hạn của token.
   - Nếu token hợp lệ, tạo đối tượng `UsernamePasswordAuthenticationToken` và đưa vào `SecurityContextHolder`.

### 1.2. Cấu trúc Token
Token được sinh ra bao gồm:
- **Header**: Thuật toán mã hóa.
- **Payload (Claims)**:
  - `sub`: Username (thường là email).
  - `authorities`: Danh sách các quyền của user (dùng cho hệ thống RBAC).
  - `iat`: Thời điểm tạo (Issued At).
  - `exp`: Thời điểm hết hạn (Expiration).
- **Signature**: Chữ ký điện tử để đảm bảo token không bị giả mạo.

---

## 2. Tích hợp OAuth 2.0 (Đăng nhập qua Mạng xã hội)

Để mang lại sự tiện lợi, hệ thống hỗ trợ xác thực qua các nhà cung cấp bên thứ ba (Google, Facebook, Github) sử dụng chuẩn OAuth 2.0. Việc triển khai nằm chủ yếu trong `OAuth2ServiceImpl`.

### 2.1. Luồng hoạt động (Authorization Code Flow)
1. **Yêu cầu đăng nhập**: Người dùng chọn đăng nhập bằng Google. Client gọi API để lấy URL chuyển hướng xác thực (`getAuthorizationUrl`).
2. **Chuyển hướng (Redirect)**: Client được chuyển hướng tới trang đăng nhập của Google (VD: `https://accounts.google.com/o/oauth2/v2/auth`).
3. **Cấp phép & Trả Authorization Code**: Sau khi người dùng đồng ý, Google sẽ redirect về hệ thống (`/oauth/callback`) kèm theo một `code`.
4. **Đổi Code lấy Token**: Server xử lý gọi API lên Google (endpoint `https://oauth2.googleapis.com/token`) để đổi `code` lấy `access_token` và `id_token`.
5. **Xác thực ID Token**:
   - Sử dụng thư viện `Nimbus JOSE + JWT` để parse và xác thực `id_token`.
   - Kiểm tra `Issuer` (phải là `https://accounts.google.com`).
   - Kiểm tra `Audience` (phải khớp với `CLIENT_ID` của hệ thống).
   - Kiểm tra thời hạn (`Expiration`).
   - Xác minh trạng thái email (`email_verified`).
6. **Xử lý nội bộ**: Sau khi xác minh thành công, hệ thống sẽ trích xuất thông tin người dùng (email, name, picture) từ token. Nếu người dùng chưa tồn tại trong cơ sở dữ liệu, hệ thống tự động tạo tài khoản mới. Cuối cùng, hệ thống tự tạo và trả về JWT nội bộ cho client để sử dụng.

### 2.2. Các nhà cung cấp hỗ trợ
- **Google**: Đã hoàn thiện xác thực và kiểm tra ID Token.
- **Facebook**: Cấu trúc đã được thiết lập.
- **Github**: Cấu trúc đã được thiết lập.

---

## 3. Phân quyền với RBAC (Role-Based Access Control)

Hệ thống quản lý quyền truy cập chi tiết dựa trên Role (Vai trò) và Authority (Quyền hạn), sử dụng Annotation `@PreAuthorize` của Spring Security kết hợp với JWT.

### 3.1. Thiết kế dữ liệu quyền
- **Roles**: Tập hợp các nhóm quyền lớn (VD: `ADMIN`, `CUSTOMER`).
- **Authorities**: Các quyền cụ thể và nhỏ nhất trên từng tài nguyên (VD: `ORDER_READ`, `ORDER_CREATE`, `ORDER_UPDATE`).

### 3.2. Triển khai mức Method-Security
Tại các Controller, hệ thống giới hạn truy cập cho từng API dựa trên quyền cụ thể của người dùng đang thực hiện request. Ví dụ tiêu biểu trong `OrderController`:

- **Tạo đơn hàng**: Yêu cầu người dùng phải có quyền tạo đơn (`ORDER_CREATE`).
  ```java
  @PreAuthorize("hasAuthority('ORDER_CREATE')")
  @PostMapping
  public ResponseEntity<ApiResponse<OrderResponse>> create(...)
  ```
- **Xem danh sách đơn hàng**: Yêu cầu quyền đọc đơn hàng (`ORDER_READ`).
  ```java
  @PreAuthorize("hasAuthority('ORDER_READ')")
  @GetMapping
  public ResponseEntity<ApiResponse<List<OrderResponse>>> getAll()
  ```
- **Cập nhật/Hủy đơn hàng**: Yêu cầu quyền thao tác và thay đổi (`ORDER_UPDATE`).
  ```java
  @PreAuthorize("hasAuthority('ORDER_UPDATE')")
  @PatchMapping("/{id}/cancel")
  ```

### 3.3. Luồng kiểm tra quyền
1. Khi request đi qua `PreFilter`, danh sách quyền (authorities) được giải mã từ Payload của JWT.
2. Danh sách này được convert thành các đối tượng `SimpleGrantedAuthority` và đưa vào `UsernamePasswordAuthenticationToken` trong Spring Security Context.
3. Khi request đến các hàm (method) trên Controller, Spring Security sẽ chặn lại và đánh giá biểu thức SpEL trong `@PreAuthorize`.
4. Nếu danh sách `GrantedAuthority` của user chứa quyền yêu cầu (VD: `ORDER_READ`), request được thực thi bình thường. Nếu không, hệ thống từ chối và trả về lỗi `403 Forbidden`.

---

## Tổng kết

Sự kết hợp giữa **JWT** (cho xác thực không trạng thái, tốc độ cao), **OAuth 2.0** (cho trải nghiệm người dùng liền mạch và an toàn từ bên thứ 3) và **RBAC** (để kiểm soát quyền hạn chặt chẽ đến từng cấp độ API) giúp hệ thống **Tech Device Shop** đạt được tính bảo mật cao, linh hoạt trong mở rộng và đáp ứng tốt các tiêu chuẩn hiện đại của RESTful API.
