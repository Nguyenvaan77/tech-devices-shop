# Tech Device Frontend

Giao diện React + Vite cho backend `tech-devices-shop`.

```powershell
cd frontend
npm install
npm run dev
```

Mở địa chỉ Vite hiển thị trong terminal (thường là `http://localhost:5173`). Khởi chạy backend tại `http://localhost:8080`; Vite chuyển tiếp các đường dẫn `/api`, `/auth` và `/oauth` tới backend, không cần thay đổi API hoặc CORS. Nếu backend dùng địa chỉ khác, sửa cấu hình proxy trong `vite.config.js`.

API sản phẩm yêu cầu đăng nhập và hiện không trả về giá. API xóa từng sản phẩm khỏi giỏ có tham số chưa khớp, vì vậy giao diện hiện chỉ hỗ trợ thêm sản phẩm và xóa toàn bộ giỏ. Giao diện chưa triển khai đặt hàng và thanh toán.

`npm run build` tạo bản build trong `dist/`. Khi triển khai bản build, cấu hình máy chủ reverse proxy chuyển tiếp các đường dẫn API kể trên tới backend cùng origin.
