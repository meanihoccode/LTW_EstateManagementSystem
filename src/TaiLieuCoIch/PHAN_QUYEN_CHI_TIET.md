# 🔐 PHÂN QUYỀN TRONG DỰ ÁN MEANI REAL ESTATE

---

## 1. TỔNG QUAN

Hệ thống có **3 role** lưu trong cột `quyen_han` bảng `taikhoan`:

| Role | Quyền hạn |
|------|-----------|
| `Admin` | Toàn quyền, quản lý tài khoản + nhân viên |
| `Quản lý` | Thêm/sửa/xóa BĐS, khách thuê, chủ sở hữu, hợp đồng, thanh toán |
| `Nhân viên` | Xem BĐS + sửa BĐS do mình quản lý, thêm/sửa khách thuê |

---

## 2. LUỒNG HOẠT ĐỘNG KHI ĐĂNG NHẬP

```
User nhập username/password
        ↓
Spring Security gọi UserDetailsServiceImpl.loadUserByUsername()
        ↓
Tìm Account trong bảng taikhoan theo username
        ↓
Lấy cột quyen_han → thêm prefix "ROLE_"
  "Admin"     → ROLE_Admin
  "Quản lý"   → ROLE_Quản lý
  "Nhân viên" → ROLE_Nhân viên
        ↓
Spring Security lưu vào SecurityContext
        ↓
Mỗi request sau đó → kiểm tra role trong SecurityContext
```

---

## 3. CÁC LỚP BẢO VỆ (3 lớp)

### Lớp 1: SecurityConfig (Backend - URL level)
```
File: security/SecurityConfig.java
```

Kiểm soát **URL nào** được phép truy cập với **role nào**:

```java
// Chỉ Admin
.requestMatchers("/staff", "/accounts").hasRole("Admin")
.requestMatchers("/api/staffs/**").hasRole("Admin")
.requestMatchers("/api/accounts/**").hasRole("Admin")

// Admin + Quản lý + Nhân viên (trang xem)
.requestMatchers("/properties", "/owners", "/tenants")
    .hasAnyRole("Admin", "Quản lý", "Nhân viên")

// GET: Tất cả đều xem được
.requestMatchers(HttpMethod.GET, "/api/properties/**").authenticated()

// POST/DELETE: Admin + Quản lý
.requestMatchers(HttpMethod.POST, "/api/properties/**")
    .hasAnyRole("Admin", "Quản lý")
.requestMatchers(HttpMethod.DELETE, "/api/properties/**")
    .hasAnyRole("Admin", "Quản lý")

// PUT properties: Admin + Quản lý + Nhân viên
// (Nhân viên chỉ sửa của mình - kiểm tra ở Service)
.requestMatchers(HttpMethod.PUT, "/api/properties/**")
    .hasAnyRole("Admin", "Quản lý", "Nhân viên")

// POST/PUT tenants: Admin + Quản lý + Nhân viên
.requestMatchers(HttpMethod.POST, "/api/tenants/**")
    .hasAnyRole("Admin", "Quản lý", "Nhân viên")
.requestMatchers(HttpMethod.PUT, "/api/tenants/**")
    .hasAnyRole("Admin", "Quản lý", "Nhân viên")
```

---

### Lớp 2: Service (Backend - Business logic)
```
File: service/PropertyService.java
```

Kiểm tra **nghiệp vụ chi tiết hơn** (ví dụ: nhân viên chỉ sửa BĐS của mình):

```java
public Property updateProperty(Integer id, Property propertyDetails) {
    Property property = getPropertyById(id);

    // Lấy user đang đăng nhập từ Spring Security
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();
    Account account = accountRepository.findByUsername(username);

    // Nếu là Nhân viên → kiểm tra BĐS có phải của mình không
    if (account != null && "Nhân viên".equals(account.getRole())) {
        User currentStaff = userRepository.findByAccount(account);

        // BĐS không phải của nhân viên này → từ chối 403
        if (currentStaff == null
            || property.getStaff() == null
            || !currentStaff.getId().equals(property.getStaff().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Bạn chỉ có thể sửa BĐS do mình quản lý");
        }
    }
    // ... tiếp tục cập nhật
}
```

**Tại sao cần lớp này?**
> SecurityConfig chỉ kiểm tra được "nhân viên có được PUT không" (có).
> Nhưng không biết được "BĐS này có phải của nhân viên này không".
> → Cần Service kiểm tra logic nghiệp vụ chi tiết.

---

### Lớp 3: Frontend (HTML + JavaScript)
```
Files: properties.html, tenants.html, owners.html
```

**Ẩn/hiện UI** dựa trên role để trải nghiệm người dùng tốt hơn:

#### 3a. Lấy role từ backend
```javascript
// Gọi endpoint /api/me/role để lấy role + staffId
async function fetchUserRole() {
    const response = await fetch('/api/me/role');
    const data = await response.json();
    userRole = data.role;     // "Admin" / "Quản lý" / "Nhân viên"
    myStaffId = data.staffId; // ID nhân viên đang login
}
```

#### 3b. Thymeleaf sec:authorize (ẩn element khi render HTML)
```html
<!-- Nút Thêm BĐS: chỉ Admin + Quản lý thấy -->
<button sec:authorize="hasAnyRole('Admin', 'Quản lý')">+ Thêm BĐS</button>

<!-- Menu Staff: chỉ Admin thấy -->
<a href="/staff" sec:authorize="hasRole('Admin')">Nhân viên</a>

<!-- Menu Tài khoản: chỉ Admin thấy -->
<a href="/accounts" sec:authorize="hasRole('Admin')">Tài khoản</a>
```

> **sec:authorize** được xử lý phía server (Thymeleaf) → Element không tồn tại trong HTML trả về → Người dùng không thể inspect để tìm thấy.

#### 3c. JavaScript ẩn nút Sửa/Xóa động
```javascript
// Hàm quyết định hiện nút nào cho từng BĐS
function buildActions(p) {
    if (userRole !== 'Nhân viên') {
        // Admin/Quản lý: thấy đủ Sửa + Xóa
        return `<button onclick="editProperty(${p.id})">Sửa</button>
                <button onclick="deleteProperty(${p.id})">Xóa</button>`;
    }
    // Nhân viên: chỉ thấy Sửa nếu BĐS do mình quản lý
    if (p.staff && p.staff.id === myStaffId) {
        return `<button onclick="editProperty(${p.id})">Sửa</button>`;
    }
    return ''; // BĐS của người khác → không có nút
}
```

#### 3d. Modal ẩn phần chọn nhân viên khi là Nhân viên
```javascript
async function editProperty(id) {
    // ...load data...

    if (userRole === 'Nhân viên') {
        // Ẩn phần chọn nhân viên quản lý
        document.getElementById('staffRow').style.display = 'none';
        // Tự gán chính mình
        document.getElementById('nhanVienQuanLy').value = myStaffId;
    } else {
        document.getElementById('staffRow').style.display = '';
        document.getElementById('nhanVienQuanLy').value = property.staff?.id;
    }
}
```

---

## 4. ENDPOINT /api/me/role

```
File: WebController.java
```

Endpoint đặc biệt để frontend biết role của người đang đăng nhập:

```java
@GetMapping("/api/me/role")
@ResponseBody
public Map<String, Object> getMyRole() {
    // Lấy thông tin người đang đăng nhập từ Spring Security
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();

    Account account = accountRepository.findByUsername(username);

    // Tìm nhân viên tương ứng → lấy staffId
    User staff = userRepository.findByAccount(account);

    return Map.of(
        "role", account.getRole(),      // "Nhân viên"
        "username", username,           // "nhanvien01"
        "staffId", staff.getId()        // 3
    );
}
```

**Tại sao cần endpoint này?**
> Không dùng `/api/staffs` vì endpoint đó chỉ Admin mới gọi được.
> `/api/me/role` ai đăng nhập cũng gọi được → lấy đúng thông tin của mình.

---

## 5. BẢNG TÓM TẮT QUYỀN

| Chức năng | Admin | Quản lý | Nhân viên |
|-----------|-------|---------|-----------|
| Xem Dashboard | ✅ | ✅ | ✅ |
| Xem BĐS | ✅ | ✅ | ✅ |
| Thêm BĐS | ✅ | ✅ | ❌ |
| Sửa BĐS | ✅ | ✅ | ✅ (chỉ BĐS của mình) |
| Xóa BĐS | ✅ | ✅ | ❌ |
| Xem Khách thuê | ✅ | ✅ | ✅ |
| Thêm Khách thuê | ✅ | ✅ | ✅ |
| Sửa Khách thuê | ✅ | ✅ | ✅ |
| Xóa Khách thuê | ✅ | ✅ | ❌ |
| Xem Chủ sở hữu | ✅ | ✅ | ✅ |
| Thêm/Sửa/Xóa Chủ sở hữu | ✅ | ✅ | ❌ |
| Hợp đồng | ✅ | ✅ | ✅ |
| Thanh toán | ✅ | ✅ | ✅ |
| Quản lý Nhân viên | ✅ | ❌ | ❌ |
| Quản lý Tài khoản | ✅ | ❌ | ❌ |

---

## 6. SƠ ĐỒ LUỒNG KHI NHÂN VIÊN SỬA BĐS

```
Nhân viên click "Sửa" BĐS #2
        ↓
Frontend: editProperty(2)
  → Kiểm tra: p.staff.id === myStaffId ? (Frontend guard)
  → Nếu không phải: return (không gọi API)
  → Nếu đúng: mở modal, ẩn staffRow, gán myStaffId
        ↓
Nhân viên sửa thông tin → Submit
        ↓
Frontend: PUT /api/properties/2
  → staffId = myStaffId (tự gán, không cho chọn)
        ↓
SecurityConfig: Nhân viên được PUT /api/properties/** → ✅ PASS
        ↓
PropertyService.updateProperty(2, ...)
  → Lấy Authentication → username
  → Tìm Account → role = "Nhân viên"
  → Tìm Staff theo Account → staffId = 3
  → Lấy property #2 → property.staff.id = 3
  → 3 === 3 → ✅ PASS
        ↓
Lưu vào database thành công ✅
```

---

## 7. LƯU Ý QUAN TRỌNG

### Tại sao cần cả 3 lớp bảo vệ?

| Lớp | Bảo vệ gì | Nếu thiếu |
|-----|-----------|-----------|
| SecurityConfig | URL/HTTP method | Nhân viên gọi thẳng API xóa BĐS |
| Service | Logic nghiệp vụ | Nhân viên gọi API sửa BĐS của người khác |
| Frontend | Ẩn UI | Người dùng thấy nút không có quyền → UX xấu |

> **Frontend chỉ là UX, không phải bảo mật thật sự!**
> Người dùng có thể mở DevTools, gọi API trực tiếp bỏ qua frontend.
> → Backend (SecurityConfig + Service) mới là bảo mật thật sự.

