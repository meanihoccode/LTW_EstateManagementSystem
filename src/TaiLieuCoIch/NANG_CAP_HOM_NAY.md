# 🚀 Tổng Hợp Nâng Cấp - Ngày 11/03/2026

---

## 1. 🔐 Phân Quyền Đăng Nhập (Spring Security)

### Vấn đề
- Form login dùng `action="/login" method="post"` → Spring Security tự redirect thẳng `/dashboard`, **không check `firstLogin`**
- Logic check `firstLogin` nằm trong JS `handleLogin()` nhưng form không gọi hàm đó

### Giải pháp: `CustomLoginSuccessHandler.java`
```java
// Sau khi Spring Security xác thực xong, handler này kiểm tra:
// - firstLogin = true  → redirect /change-password
// - firstLogin = false → redirect /dashboard
// - Admin           → không bị bắt đổi mật khẩu
```
- Tạo file `CustomLoginSuccessHandler.java` trong package `security`
- `SecurityConfig` dùng `.successHandler(loginSuccessHandler)` thay vì `.defaultSuccessUrl(...)`
- Form `index.html` giữ nguyên `action="/login"` — Spring Security xử lý

---

## 2. 🔑 Trang Change Password

### Vấn đề
- Cũ: JS đọc `accountId` từ URL param (`?accountId=123`)
- Mới dùng Spring Security session → redirect `/change-password` **không có param** → alert "Invalid access"

### Giải pháp
- Thêm `accountId` vào response của `/api/me/role`
- `change-password.html` fetch `/api/me/role` khi load trang để lấy `accountId` từ server, không dùng URL param nữa

```javascript
// Cũ - đọc từ URL (bị lỗi)
const accountId = urlParams.get('accountId');

// Mới - fetch từ server session
const res = await fetch('/api/me/role');
const data = await res.json();
currentAccountId = data.accountId;
```

---

## 3. 🛡️ Fix SecurityConfig - Lỗi 403

### Vấn đề
- `PUT /api/auth/accounts/{id}/change-password` bị chặn vì rule `.requestMatchers("/api/auth/accounts/**").hasAuthority("ROLE_Admin")`
- Rule đặt sai thứ tự → endpoint change-password bị Admin rule chặn trước

### Giải pháp
- Đặt rule change-password **trước** rule Admin:
```java
.requestMatchers(HttpMethod.PUT, "/api/auth/accounts/*/change-password").authenticated()
.requestMatchers("/api/auth/accounts/**").hasAuthority("ROLE_Admin")
```

---

## 4. 🏷️ Fix Role Encoding - Nhân Viên Không Vào Được Trang

### Vấn đề
- `hasRole("Nhân viên")` → Spring Security tìm `ROLE_Nhân viên`
- Ký tự tiếng Việt có thể bị encode khác nhau → không match

### Giải pháp
- Đổi toàn bộ `hasRole()` / `hasAnyRole()` sang `hasAuthority()` / `hasAnyAuthority()` với full prefix `ROLE_`:
```java
// Cũ
.hasAnyRole("Admin", "Quản lý", "Nhân viên")

// Mới
.hasAnyAuthority("ROLE_Admin", "ROLE_Quản lý", "ROLE_Nhân viên")
```

---

## 5. 🎭 Fix Role Nhân Viên - Chức Danh vs Quyền Hạn

### Vấn đề
- `UserService.createUser()` dùng `user.getRole()` (chức danh: "Nhân viên kinh doanh", "Nhân viên kỹ thuật"...) để set `quyen_han` trong bảng tài khoản
- → Account bị lưu role sai, không match với `hasAuthority("ROLE_Nhân viên")`

### Giải pháp
**Frontend (`staff.html`):**
- Thêm `onchange="autoSetQuyenHan()"` vào dropdown chức vụ
- Hidden input `quyenHan` tự động được set:
```javascript
function autoSetQuyenHan(vaiTro) {
    // "Nhân viên kinh doanh", "Nhân viên kỹ thuật"... → "Nhân viên"
    // "Quản lý" → "Quản lý"
    const quyen = vaiTro.startsWith('Nhân viên') ? 'Nhân viên' : vaiTro;
    document.getElementById('quyenHan').value = quyen;
}
```

**Backend (`User.java`):**
```java
@Transient  // Không lưu vào DB, chỉ nhận từ frontend
private String quyenHan;
```

**`UserService.createUser()`:**
```java
// Cũ
String role = user.getRole() != null ? user.getRole() : "NhanVien";

// Mới - dùng quyenHan riêng
String quyenHan = (user.getQuyenHan() != null && !user.getQuyenHan().isBlank())
        ? user.getQuyenHan()
        : "Nhân viên";  // mặc định
```

---

## 6. 📊 Fix `fetchRole` trong `dashboard.js`

### Vấn đề
- Cũ: `fetchRole()` đọc `username` từ `localStorage` rồi gọi `/api/staffs` để tìm role → phức tạp, không chính xác

### Giải pháp
- Đơn giản hóa: dùng `/api/me/role` trả về thẳng từ Spring Security session
```javascript
// Mới - gọn và chính xác
async function fetchRole() {
    const res = await fetch('/api/me/role');
    const data = await res.json();
    roleElement.textContent = `👤 ${data.username} (${data.role})`;
}
```

---

## 7. 💉 Fix Dependency Injection - PasswordEncoder

### Vấn đề
- `UserService` và `AccountService` đều tạo `new BCryptPasswordEncoder()` riêng thay vì dùng Bean từ Spring context

### Giải pháp
- Dùng `@Autowired PasswordEncoder` thay vì `new BCryptPasswordEncoder()`:
```java
// Cũ
private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

// Mới
@Autowired
private PasswordEncoder passwordEncoder;
```

---

## 8. 📝 `/api/me/role` - Thêm `accountId` và `authorities`

- Endpoint trả thêm `accountId` để `change-password.html` dùng
- Trả thêm `authorities` để debug role thực tế Spring Security đang dùng

---

## 📋 Tóm Tắt File Đã Thay Đổi

| File | Thay đổi |
|------|----------|
| `security/CustomLoginSuccessHandler.java` | **Tạo mới** - redirect đúng sau login |
| `security/SecurityConfig.java` | Dùng `successHandler`, đổi sang `hasAuthority`, fix thứ tự rule |
| `security/UserDetailsServiceImpl.java` | Xóa log debug |
| `templates/change-password.html` | Lấy `accountId` từ `/api/me/role` thay vì URL param |
| `templates/staff.html` | Thêm `autoSetQuyenHan()`, hidden input `quyenHan` |
| `entity/User.java` | Thêm `@Transient String quyenHan` |
| `service/UserService.java` | Dùng `quyenHan` khi tạo account, `@Autowired PasswordEncoder` |
| `service/AccountService.java` | `@Autowired PasswordEncoder` |
| `WebController.java` | Thêm `accountId`, `authorities` vào `/api/me/role` |
| `static/js/dashboard.js` | Đơn giản hóa `fetchRole()` dùng `/api/me/role` |

