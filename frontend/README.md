# Tech Device Frontend

Giao diện React + Vite cho backend `tech-devices-shop`.

Cần Node.js từ phiên bản 20.19.0 để chạy công cụ build và Sass.

```powershell
cd frontend
npm install
npm run dev
```

Mở địa chỉ Vite hiển thị trong terminal (thường là `http://localhost:5173`). Khởi chạy backend tại `http://localhost:8080`; Vite chuyển tiếp các đường dẫn `/api`, `/auth` và `/oauth` tới backend, không cần thay đổi API hoặc CORS. Nếu backend dùng địa chỉ khác, sửa cấu hình proxy trong `vite.config.js`.

API sản phẩm yêu cầu đăng nhập và hiện không trả về giá. API xóa từng sản phẩm khỏi giỏ có tham số chưa khớp, vì vậy giao diện hiện chỉ hỗ trợ thêm sản phẩm và xóa toàn bộ giỏ. Giao diện chưa triển khai đặt hàng và thanh toán.

`npm run build` tạo bản build trong `dist/`. Khi triển khai bản build, cấu hình máy chủ reverse proxy chuyển tiếp các đường dẫn API kể trên tới backend cùng origin.

## Cấu trúc frontend

- `public/`: tài nguyên tĩnh được Vite phục vụ trực tiếp. `index.html` nằm tại root frontend theo cấu trúc của Vite.
- `src/assets/`: hình ảnh, phông chữ và biểu tượng được import vào ứng dụng.
- `src/components/`: thành phần dùng chung, gồm `common/` và `layout/`.
- `src/features/`: thành phần và logic theo chức năng, hiện có `auth/` và `products/`.
- `src/pages/`: các màn hình cấp trang; màn hình cửa hàng nằm tại `Home/`.
- `src/services/`: giao tiếp với API và quản lý phiên hiện tại.
- `src/styles/`: SCSS dùng chung cho toàn ứng dụng; các component và trang import file SCSS nằm cùng thư mục.
- `src/routes/`, `hooks/`, `context/`, `store/`, `utils/`, `config/`: nơi dành cho các phần dùng chung khi dự án cần đến.
- `tests/`: kiểm tra render ban đầu, chạy bằng `npm run test:smoke`.
