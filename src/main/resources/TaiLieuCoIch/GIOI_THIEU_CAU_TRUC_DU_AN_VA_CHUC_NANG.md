# Giới thiệu dự án & cấu trúc thư mục (Meani - Quản lý BĐS)

## 1) Mục tiêu hệ thống
Đây là ứng dụng quản lý bất động sản (cho thuê) theo mô hình **Web MVC**:
- **Frontend**: HTML/CSS/JS (render bằng Thymeleaf template).
- **Backend**: Spring Boot REST API + Controller render view.
- **Database**: MySQL (schema `quanlybatdongsan`).

Các nhóm nghiệp vụ chính:
- Quản lý **BĐS** (properties)
- Quản lý **Hợp đồng** (contracts)
- Quản lý **Thanh toán** (payments)
- Quản lý **Khách thuê** (tenants)
- Quản lý **Chủ sở hữu** (owners)
- Quản lý **Nhân viên** (staff)
- Quản lý **Tài khoản & phân quyền** (accounts/auth)

---

## 2) Cấu trúc thư mục quan trọng

### 2.1. Backend (Java)
Nằm trong `src/main/java/com/example/ltw_quanlybds/`

Gợi ý các package (tuỳ repo hiện tại của bạn có thể khác đôi chút):
- `api/`
  - Chứa các REST Controller kiểu `/api/...` (JSON)
  - Ví dụ: `PropertyController`, `TenantController`, `PaymentController`, ...
- `controller/` hoặc `web/` (nếu có)
  - Chứa các Controller render view (trả về trang HTML): `/dashboard`, `/properties`, ...
- `entity/`
  - Các JPA Entity map với bảng DB (BDS, Owner, Tenant, Contract, Payment, Staff, Account...)
- `repository/`
  - Spring Data JPA repositories
- `service/`
  - Logic nghiệp vụ (validate, mapping, xử lý quyền, tính toán doanh thu, ...)
- `security/` (nếu bạn có dùng Spring Security)
  - Cấu hình đăng nhập/phân quyền, filter, các rule chặn endpoint

> Lưu ý: hiện code của bạn đang dùng Spring Boot + Tomcat, các endpoint `/api/...` là REST.

### 2.2. Frontend (templates + static)
Nằm trong `src/main/resources/`:

- `templates/` (Thymeleaf HTML)
  - Các trang giao diện chính
  - Ví dụ (đang có trong repo):
    - `index.html` (trang chủ)
    - `dashboard.html` (dashboard)
    - `properties.html`, `contracts.html`, `payments.html`, `tenants.html`, `owners.html`, `staff.html`, `accounts.html`

- `static/` (file tĩnh)
  - `static/css/` (CSS dùng chung: `dashboard.css`, ...)
  - `static/js/` (JS: `darkmode.js`, `pagination.js`, `search-utils.js`, ...)
  - `static/img/` (ảnh: logo/house/login.png, ...)

**Nguyên tắc**:
- HTML nằm trong `templates/`
- CSS/JS/IMG nằm trong `static/`
- Trên HTML, link kiểu:
  - `href="/css/dashboard.css"`
  - `src="/js/pagination.js"`
  - `src="/img/login.png"`

### 2.3. Tài liệu dự án
- `src/main/resources/TaiLieuCoIch/`
  - Nơi lưu các file `.md` hướng dẫn/ghi chú trong dự án.
- `docs/` hoặc `bin/main/db/docs/...`
  - Một số tài liệu SRS/đặc tả.

---

## 3) Các trang (Frontend) & chức năng chính

### 3.1. Trang `Dashboard`
- Xem tổng quan số lượng: BĐS, hợp đồng, thanh toán...
- Xem doanh thu theo tháng/năm (tuỳ bạn triển khai)

### 3.2. Trang `BĐS (Properties)`
- Danh sách BĐS
- Lọc theo trạng thái
- Tìm kiếm (không dấu)
- Thêm/Sửa/Xoá (tuỳ theo quyền)

### 3.3. Trang `Khách thuê (Tenants)`
- Danh sách khách thuê
- Tìm kiếm theo tên/CCCD/điện thoại
- Thêm/Sửa
- (Tuỳ thiết kế) hạn chế xoá theo quyền

### 3.4. Trang `Chủ sở hữu (Owners)`
- Danh sách chủ sở hữu
- Thêm/Sửa/Xoá (tuỳ theo quyền)

### 3.5. Trang `Hợp đồng (Contracts)`
- Danh sách hợp đồng
- Liên kết với BĐS + Khách thuê
- Thêm/Sửa/Xoá (tuỳ theo quyền)

### 3.6. Trang `Thanh toán (Payments)`
- Danh sách thanh toán theo hợp đồng
- Thêm/Sửa/Xoá
- Tìm kiếm/lọc trạng thái

### 3.7. Trang `Nhân viên (Staff)`
- Danh sách nhân viên
- Thêm/Sửa/Xoá
- (Tuỳ logic) tạo account tự động sau khi thêm nhân viên

### 3.8. Trang `Tài khoản (Accounts)`
- Hiển thị các nhân viên đã có tài khoản
- Reset mật khẩu (tạo mật khẩu tạm thời)

---

## 4) API Backend (REST)
Các endpoint thường có dạng:
- `GET /api/{resource}`: lấy danh sách
- `GET /api/{resource}/{id}`: lấy chi tiết
- `POST /api/{resource}`: tạo mới
- `PUT /api/{resource}/{id}`: cập nhật
- `DELETE /api/{resource}/{id}`: xoá

Ví dụ theo repo của bạn (thường gặp):
- `/api/properties`
- `/api/tenants`
- `/api/owners`
- `/api/contracts`
- `/api/payments`
- `/api/staffs`

Auth (tuỳ bạn đang cấu hình):
- `POST /api/auth/login`
- `PUT /api/auth/accounts/{id}/reset-password`
- `PUT /api/auth/accounts/{id}/change-password`

---

## 5) Phân quyền (theo cột `quyen_han` trong bảng tài khoản)
Hệ thống đang hướng tới phân quyền mức **role**:
- `Admin`
  - Quản lý tài khoản, nhân viên, toàn quyền.
- `Quản lý`
  - Có quyền quản lý nhân viên và nghiệp vụ chính.
- `Nhân viên`
  - Có quyền xem một số trang.
  - Một số thao tác giới hạn theo nghiệp vụ (VD: chỉ sửa BĐS do mình quản lý).

Gợi ý UX (frontend):
- Vẫn cho nhân viên vào trang BĐS/Khách thuê/Chủ sở hữu
- Nhưng **ẩn nút Thêm/Xoá** hoặc disable theo role
- Ở backend vẫn phải chặn bằng Security/Service (không chỉ dựa UI)

---

## 6) Chạy dự án (nhanh)
Trong Windows CMD, bạn đang chạy OK bằng:

```bat
cmd /c "cd /d D:\LTW_QuanLyBDS\LTW_QuanLyBDS && gradlew bootRun"
```

Sau khi chạy, vào:
- Trang chủ: `http://localhost:8080/`
- Dashboard (nếu mapping): `http://localhost:8080/dashboard`

---

## 7) Những điểm dễ sai (checklist)
- CSS/JS không ăn: kiểm tra file nằm trong `src/main/resources/static/...` và link bắt đầu bằng `/css/...`, `/js/...`.
- 404 khi vào trang: chưa có controller mapping view (VD `/properties` trả về `properties.html`).
- API trả HTML thay vì JSON: thường do bị redirect sang trang login hoặc route trùng controller view.
- Lỗi 403: do Spring Security chặn endpoint theo role (cần cấu hình matchers đúng).
- `[]` dù DB có dữ liệu: thường do kết nối DB sai schema hoặc mapping entity/table/column.

---

## 8) Định hướng phát triển tiếp (solo-friendly)
1) Chuẩn hoá Auth/Role (đăng nhập, lưu session/JWT tuỳ chọn)
2) Hoàn thiện rule phân quyền (frontend ẩn nút + backend chặn thật)
3) Chuẩn hoá validate + error response (để FE show lỗi rõ)
4) Nâng cấp phân trang (server-side nếu dữ liệu lớn)
5) Thống kê dashboard (doanh thu theo tháng, số hợp đồng sắp hết hạn, ...)

---

### Ghi chú
Tài liệu này mang tính “map nhanh” để bạn nhìn vào là biết:
- File nằm ở đâu
- Trang nào làm gì
- API nào phục vụ trang nào
- Các lỗi thường gặp để debug nhanh

