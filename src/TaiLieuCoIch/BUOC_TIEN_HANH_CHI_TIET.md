# 🎯 BƯỚC TIẾP THEO CHI TIẾT - HOÀN THIỆN HỆ THỐNG

## 📊 TÌNH TRẠNG HIỆN TẠI (13/02/2026)

### ✅ Đã Có:
- ✔️ 7 Entity Models (User, Property, Tenant, Contract, Owner, Payment, Account)
- ✔️ 7 Repositories (JPA)
- ✔️ 7 Services (Business logic)
- ✔️ 7 API Controllers (REST endpoints)
- ✔️ 9 HTML Templates (dashboard.html, properties.html, ...)
- ✔️ CSS & JavaScript cơ bản
- ✔️ MySQL Database

### ❌ Chưa Có (Cần Làm):
- ❌ Frontend chưa gọi API (JavaScript)
- ❌ Authentication/Login chưa hoạt động đúng
- ❌ Error handling & validation response
- ❌ Data binding HTML ↔ API
- ❌ Modal forms để thêm/sửa/xóa
- ❌ Search & filter functionality
- ❌ Dashboard hiển thị statistics

---

## 📋 PRIORITY ROADMAP (TỪ CAO ĐẾN THẤP)

### **🔴 PHASE 1: AUTHENTICATION (Priority: CRITICAL) - 1-2 ngày**

**Mục đích:** Hệ thống đăng nhập hoạt động → Admin vào được Dashboard

#### Bước 1.1: Fix Login Flow
**Thời gian:** ~2 giờ

**Status hiện tại:**
- ✅ API POST `/api/accounts/login` đã có
- ❌ Frontend form chưa gọi API đúng
- ❌ Session/Token chưa lưu

**Cần làm:**
```javascript
// index.html → Script gọi API login
// Lưu token vào localStorage
// Redirect sang dashboard
// Check login state khi load page
```

**File cần sửa:**
- `src/main/resources/static/js/script.js` - Thêm hàm handleLogin()
- `src/main/resources/templates/index.html` - Connect button sign-in
- `src/main/java/com/example/ltw_quanlybds/api/AccountController.java` - Verify login logic

**Test:**
```bash
# 1. Chạy server
gradlew bootRun

# 2. Truy cập http://localhost:8080
# 3. Nhập username/password
# 4. Kiểm tra: 
#    - Có redirect sang dashboard?
#    - localStorage có token?
#    - Refresh page vẫn ở dashboard?
```

---

#### Bước 1.2: Protect Dashboard Routes
**Thời gian:** ~1 giờ

**Cần làm:**
```javascript
// Tất cả file JavaScript trong templates cần check token
// Nếu không có token → redirect về login
// Nếu hết hạn token → logout + redirect

// Function kiểm tra login
function checkAuth() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/';
    }
}

// Gọi checkAuth() trước khi load data
checkAuth();
```

**File cần sửa:**
- `src/main/resources/templates/dashboard.html` - Thêm script checkAuth()
- `src/main/resources/templates/properties.html`
- `src/main/resources/templates/staff.html`
- `src/main/resources/templates/owners.html`
- `src/main/resources/templates/tenants.html`
- `src/main/resources/templates/contracts.html`
- `src/main/resources/templates/payments.html`

---

### **🟠 PHASE 2: FRONTEND ↔ BACKEND INTEGRATION (Priority: HIGH) - 2-3 ngày**

**Mục đích:** Tất cả trang HTML hiển thị dữ liệu từ API

#### Bước 2.1: Dashboard - Hiển thị Statistics
**Thời gian:** ~3 giờ

**Cần làm:**
1. API: Tạo endpoint GET `/api/dashboard/stats` trong PropertyController
   ```java
   @GetMapping("/api/dashboard/stats")
   public ResponseEntity<?> getDashboardStats() {
       return ResponseEntity.ok(new DashboardStats(
           propertyService.getAllProperties().size(),
           tenantService.getAllTenants().size(),
           // ...
       ));
   }
   ```

2. Frontend: Gọi API trong dashboard.html
   ```javascript
   function loadDashboardStats() {
       fetch('/api/dashboard/stats', {
           headers: {'Authorization': 'Bearer ' + localStorage.getItem('token')}
       })
       .then(res => res.json())
       .then(data => {
           document.getElementById('totalProperties').innerHTML = data.totalProperties;
           // ...
       });
   }
   ```

**File cần sửa:**
- `src/main/java/com/example/ltw_quanlybds/api/PropertyController.java` - Thêm getDashboardStats()
- `src/main/resources/templates/dashboard.html` - Thêm JS loadDashboardStats()
- `src/main/java/com/example/ltw_quanlybds/dto/DashboardStats.java` - Tạo DTO mới

---

#### Bước 2.2: Properties - Hiển thị Danh Sách + CRUD
**Thời gian:** ~4 giờ

**Cần làm:**
1. **Hiển thị danh sách** - GET /api/properties
   ```javascript
   // properties.html
   function loadProperties() {
       fetch('/api/properties')
           .then(res => res.json())
           .then(data => {
               // Vòng lặp tạo rows trong table
               data.forEach(prop => {
                   // Thêm <tr> vào tbody
               });
           });
   }
   ```

2. **Thêm mới** - POST /api/properties
   ```html
   <!-- Modal form thêm property -->
   <form id="addPropertyForm" onsubmit="handleAddProperty(event)">
       <input type="text" name="name" placeholder="Tên bất động sản">
       <input type="text" name="address" placeholder="Địa chỉ">
       <!-- ... -->
       <button type="submit">Thêm</button>
   </form>
   ```

3. **Cập nhật** - PUT /api/properties/{id}
   - Tạo modal edit
   - Gọi fetch PUT

4. **Xóa** - DELETE /api/properties/{id}
   - Xác nhận trước khi xóa
   - Gọi fetch DELETE

**File cần sửa:**
- `src/main/resources/templates/properties.html` - Thêm JS handleLoadProperties(), handleAddProperty(), handleEditProperty(), handleDeleteProperty()
- `src/main/resources/static/js/script.js` - Các hàm helper chung

---

#### Bước 2.3: Staff (Nhân Viên + Account)
**Thời gian:** ~3 giờ

**Cần làm:**
1. Hiển thị danh sách Staff + Account Info
   ```javascript
   // staff.html
   // Gọi /api/users để lấy nhân viên
   // Hiển thị: fullName, phone, role, account.username
   ```

2. Thêm nhân viên → Tự tạo account
   - Form: fullName, phone, role
   - Backend tự sinh username & password
   - Trả về password cho admin xem lần đầu

3. Reset password
   - Button "Reset" → gọi PUT `/api/accounts/{id}/reset-password`

**File cần sửa:**
- `src/main/resources/templates/staff.html`
- `src/main/java/com/example/ltw_quanlybds/api/UserController.java` - Thêm method reset password

---

#### Bước 2.4: Owners, Tenants, Contracts, Payments
**Thời gian:** ~6 giờ (mỗi trang ~1.5 giờ)

**Pattern giống Properties:**
- Hiển thị danh sách
- Thêm/Sửa/Xóa
- Search filter

---

### **🟡 PHASE 3: ADVANCED FEATURES (Priority: MEDIUM) - 2-3 ngày**

#### Bước 3.1: Search & Filter
**Thời gian:** ~4 giờ

**Ví dụ: Properties**
```javascript
// Thêm input search
<input type="text" id="searchProperty" placeholder="Tìm kiếm...">

// JavaScript
document.getElementById('searchProperty').addEventListener('input', (e) => {
    const keyword = e.target.value;
    fetch(`/api/properties/search?keyword=${keyword}`)
        .then(res => res.json())
        .then(data => renderProperties(data));
});
```

**Backend:**
```java
// PropertyRepository
List<Property> findByNameContainingIgnoreCase(String name);

// PropertyController
@GetMapping("/search")
public ResponseEntity<?> search(@RequestParam String keyword) {
    return ResponseEntity.ok(propertyService.searchByName(keyword));
}
```

---

#### Bước 3.2: Pagination & Sorting
**Thời gian:** ~3 giờ

```java
// PropertyService
public Page<Property> getAllProperties(int page, int size) {
    return propertyRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
}

// PropertyController
@GetMapping
public ResponseEntity<?> getAllProperties(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(propertyService.getAllProperties(page, size));
}
```

---

#### Bước 3.3: Export to Excel/PDF
**Thời gian:** ~2 giờ

Sử dụng Apache POI hoặc iText
```bash
# Thêm dependency trong build.gradle
implementation 'org.apache.poi:poi:5.2.2'
```

---

### **🟢 PHASE 4: POLISH & TESTING (Priority: LOWER) - 1-2 ngày**

#### Bước 4.1: Error Handling & Notifications
**Thời gian:** ~2 giờ

- Toast notifications khi thành công/thất bại
- Modal error messages
- Validation trước khi submit

#### Bước 4.2: CSS Responsive
**Thời gian:** ~2 giờ

- Mobile-friendly
- Sidebar collapse trên mobile

#### Bước 4.3: Testing & Bug Fixes
**Thời gian:** ~2 giờ

- Test tất cả flows
- Fix bugs

---

## 🚀 QUICK START NGAY HÔM NAY

### **Bước 1: Kiểm tra Login Flow Hiện Tại (30 phút)**

```bash
# 1. Build & chạy
gradlew clean build
gradlew bootRun

# 2. Test login API bằng Postman
POST http://localhost:8080/api/accounts/login
Body: {"username": "admin", "password": "admin123"}

# 3. Kiểm tra response
# Nên trả về: {"token": "...", "message": "..."}
```

---

### **Bước 2: Fix Login Frontend (1-2 giờ)**

**File:** `src/main/resources/static/js/script.js`

```javascript
// Thêm hàm này
function handleLogin(event) {
    event.preventDefault();
    
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    
    fetch('/api/accounts/login', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({username, password})
    })
    .then(res => res.json())
    .then(data => {
        if (data.token) {
            localStorage.setItem('token', data.token);
            localStorage.setItem('username', username);
            window.location.href = '/dashboard.html';
        } else {
            alert('Đăng nhập thất bại: ' + data.message);
        }
    })
    .catch(err => alert('Lỗi: ' + err.message));
}
```

**File:** `src/main/resources/templates/index.html`

```html
<form onsubmit="handleLogin(event)">
    <input type="text" id="username" placeholder="Username" required>
    <input type="password" id="password" placeholder="Password" required>
    <button type="submit">Sign In</button>
</form>
```

---

### **Bước 3: Test Dashboard Load (30 phút)**

**File:** `src/main/resources/templates/dashboard.html`

```javascript
<script>
    // Check login
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/';
    }
    
    // Load stats
    fetch('/api/properties')
        .then(res => res.json())
        .then(data => {
            console.log('Properties:', data);
            document.getElementById('totalProperties').textContent = data.length;
        });
</script>
```

---

## ✅ CHECKLIST HÔMAI

- [ ] Test login API bằng Postman
- [ ] Fix login frontend + connect API
- [ ] Test login flow end-to-end
- [ ] Build & chạy lại server
- [ ] Verify localStorage có token khi đăng nhập
- [ ] Verify redirect sang dashboard
- [ ] Verify refresh page vẫn ở dashboard

---

## 📌 TÓMLƯỢC

**Ưu tiên làm theo thứ tự:**

1. **PHASE 1 (Ngay):** Fix Login → Quan trọng nhất
2. **PHASE 2 (Tiếp theo):** Frontend APIs (3-4 ngày)
3. **PHASE 3:** Advanced features (tuỳ chọn)
4. **PHASE 4:** Polish & Testing

**Dự tính toàn bộ:** 7-10 ngày từ bây giờ

---

**Ready? Hãy bắt đầu với PHASE 1! 🔥**

