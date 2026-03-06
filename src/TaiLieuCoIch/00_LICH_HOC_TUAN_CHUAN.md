# 📚 LỘ TRÌNH HỌC CHUẨN - Từ Cơ Bản đến Chuyên Sâu

**Mục tiêu:** Học từng bước, hiểu rõ bản chất, không bỏ sót gì

**Thời gian:** 3-4 tuần (nếu code mỗi ngày 2-3 giờ)

---

## 🎯 NGUYÊN TẮC HỌC

1. **Hiểu → Làm → Test**: Phải hiểu khái niệm trước khi code
2. **Từ đơn giản → Phức tạp**: Bắt đầu từ hàm đơn, sau đó API phức tạp
3. **Từ backend → frontend**: Code backend trước, frontend sau
4. **Test mỗi bước**: Dùng Postman test trước khi làm frontend
5. **Commit thường xuyên**: Lưu code sau mỗi tính năng

---

## 📖 TUẦN 1: Hiểu Cơ Bản Backend

### Ngày 1-2: Hiểu Spring Boot Architecture (4 giờ)

**Khái niệm cần hiểu:**
```
Request → Controller → Service → Repository → Database
  ↓          ↓          ↓          ↓            ↓
GET /api   Nhận     Xử lý    Lấy dữ     Truy vấn
request   request  business  từ DB      SQL
           từ URL   logic
```

**File học:**
1. Đọc: `ARCHITECTURE_DIAGRAM.md` (15 phút) - Hiểu sơ đồ
2. Đọc: `TEMPLATE_CODE_LIEN_KET_API.md` - Phần Backend (30 phút)
3. Vẽ diagram riêng: Khi nào Controller được gọi? (30 phút)

**Thực hành:**
- Mở Postman
- Call GET http://localhost:8080/api/staffs
- Xem response trả về (dù chưa có data)
- Hiểu request-response flow

---

### Ngày 3: Tạo Service đầu tiên (3 giờ)

**Học:**
1. Service là gì? (10 phút)
   - Chứa business logic
   - Tương tác với Repository
   - Không liên quan UI

2. Xem code `AccountService.java` (15 phút)
   - changePassword() method
   - resetPassword() method
   - Hiểu từng dòng code

**Thực hành:**
1. Tạo `PropertyService.java` (1 giờ)
   ```java
   @Service
   public class PropertyService {
       // Phương thức 1: Lấy tất cả BĐS
       public List<Property> getAllProperties() {
           return propertyRepository.findAll();
       }
       
       // Phương thức 2: Lấy theo ID
       public Property getPropertyById(Integer id) {
           return propertyRepository.findById(id)
               .orElseThrow(() -> new Exception("Not found"));
       }
   }
   ```

2. Test build:
   ```bash
   gradlew build
   ```
   - ✅ Build success = Đã hiểu cơ bản

3. Commit:
   ```
   git add PropertyService.java
   git commit -m "feat: add PropertyService with getAll and getById"
   ```

---

### Ngày 4: Tạo REST API Controller (3 giờ)

**Học:**
1. Controller là gì? (10 phút)
   - Tiếp nhận HTTP request
   - Gọi Service
   - Trả về response

2. Mapping annotations (15 phút)
   - @GetMapping → GET request
   - @PostMapping → POST request
   - @PutMapping → PUT request
   - @DeleteMapping → DELETE request
   - @PathVariable → Lấy ID từ URL
   - @RequestBody → Lấy data từ request body

**Thực hành:**
1. Tạo `PropertyController.java` (1 giờ)
   ```java
   @RestController
   @RequestMapping("/api/properties")
   public class PropertyController {
       @Autowired
       private PropertyService service;
       
       // GET /api/properties
       @GetMapping
       public List<Property> getAllProperties() {
           return service.getAllProperties();
       }
       
       // GET /api/properties/{id}
       @GetMapping("/{id}")
       public Property getPropertyById(@PathVariable Integer id) {
           return service.getPropertyById(id);
       }
   }
   ```

2. Test trên Postman:
   ```
   GET http://localhost:8080/api/properties
   → Xem response JSON
   
   GET http://localhost:8080/api/properties/1
   → Xem response của property ID 1
   ```

3. Commit:
   ```
   git add PropertyController.java
   git commit -m "feat: add PropertyController with GET endpoints"
   ```

---

### Ngày 5: Hoàn thiện CRUD (4 giờ)

**Học:**
1. POST (Create): Thêm dữ liệu mới (30 phút)
   - @PostMapping
   - @RequestBody
   - Validation

2. PUT (Update): Cập nhật dữ liệu (30 phút)
   - @PutMapping
   - Check ID có tồn tại không

3. DELETE (Delete): Xóa dữ liệu (30 phút)
   - @DeleteMapping
   - Confirm xóa thành công

**Thực hành:**
1. Thêm method POST vào PropertyService + Controller (1 giờ)
2. Thêm method PUT vào PropertyService + Controller (1 giờ)
3. Thêm method DELETE vào PropertyService + Controller (1 giờ)

4. Test trên Postman:
   ```
   POST /api/properties
   {
       "tenBDS": "Căn hộ 123",
       "loaiBDS": "Căn hộ",
       ...
   }
   
   PUT /api/properties/1
   { ...updated data... }
   
   DELETE /api/properties/1
   ```

5. Commit:
   ```
   git commit -m "feat: complete CRUD for properties API"
   ```

---

## 📖 TUẦN 2: Hoàn Thiện Backend

### Ngày 1-2: Tạo Service & Controller cho 5 thực thể khác (8 giờ)

**Thực hành (Copy pattern từ PropertyService):**

1. **TenantService + TenantController** (1.5 giờ)
   - Copy từ PropertyService
   - Đổi property → tenant
   - Đổi trường dữ liệu

2. **ContractService + ContractController** (1.5 giờ)
3. **PaymentService + PaymentController** (1.5 giờ)
4. **OwnerService + OwnerController** (1.5 giờ)
5. **UserService + UserController** (1.5 giờ)

**Test:** Mỗi API test trên Postman
**Commit:** Một commit cho mỗi entity

---

### Ngày 3: Error Handling & Validation (3 giờ)

**Học:**
1. Exception handling (20 phút)
   - Khi Resource not found
   - Khi validation fail
   - Khi database error

2. Response format chuẩn (20 phút)
   ```java
   // Success
   {
       "success": true,
       "data": {...},
       "message": "Success"
   }
   
   // Error
   {
       "success": false,
       "error": "Error message",
       "code": 404
   }
   ```

**Thực hành:**
1. Tạo `ErrorResponse.java` (30 phút)
2. Tạo `SuccessResponse.java` (30 phút)
3. Thêm try-catch vào mỗi Controller method (1 giờ)

**Commit:**
```
git commit -m "feat: add error handling and response wrapper"
```

---

### Ngày 4-5: Authentication (4 giờ)

**Học:**
1. Login flow (20 phút) - Xem FLOW_DANG_NHAP_VA_PHAN_QUYEN.md
2. Session vs JWT (15 phút) - Sự khác biệt
3. Password hashing (15 phút) - BCrypt

**Thực hành:**
1. Tạo `AuthController.java` (1 giờ)
   ```java
   @PostMapping("/login")
   public ResponseEntity<?> login(@RequestBody LoginRequest request) {
       // Validate user
       // Return session/token
   }
   ```

2. Test login (1 giờ)
   - Postman POST /api/auth/login
   - Xem response

3. Commit:
   ```
   git commit -m "feat: add authentication/login endpoint"
   ```

---

## 📖 TUẦN 3: Frontend Integration

### Ngày 1-2: Hiểu JavaScript Fetch (4 giờ)

**Không code gì, chỉ hiểu khái niệm:**

1. **HTTP Request (20 phút)**
   - GET: Lấy dữ liệu
   - POST: Gửi dữ liệu
   - PUT: Cập nhật dữ liệu
   - DELETE: Xóa dữ liệu

2. **JavaScript fetch API (30 phút)**
   ```javascript
   // Basic fetch
   fetch('/api/url')
       .then(response => response.json())
       .then(data => console.log(data))
       .catch(error => console.error(error))
   ```

3. **Async/Await (30 phút)**
   ```javascript
   async function loadData() {
       const response = await fetch('/api/url');
       const data = await response.json();
       return data;
   }
   ```

4. **Error handling (30 phút)**
   ```javascript
   try {
       const response = await fetch('/api/url');
       if (!response.ok) throw new Error('API Error');
       const data = await response.json();
   } catch (error) {
       console.error(error);
   }
   ```

**Học từ:**
- `TEMPLATE_CODE_LIEN_KET_API.md` - Phần Frontend
- `CHEATSHEET_NHANH.md` - Code example

---

### Ngày 3: Login Form (3 giờ)

**Thực hành:**
1. **Hiểu code trong `index.html`** (30 phút)
   - Form input
   - Form submit event
   - Validation

2. **Code handleLogin function** (1 giờ)
   ```javascript
   // Thay form submission bằng fetch
   async function handleLogin(event) {
       event.preventDefault();
       const username = document.getElementById('username').value;
       const password = document.getElementById('password').value;
       
       try {
           const response = await fetch('/api/auth/login', {
               method: 'POST',
               headers: { 'Content-Type': 'application/json' },
               body: JSON.stringify({ username, password })
           });
           
           if (!response.ok) throw new Error('Invalid credentials');
           
           const data = await response.json();
           localStorage.setItem('user', JSON.stringify(data));
           window.location.href = '/dashboard';
       } catch (error) {
           alert('❌ ' + error.message);
       }
   }
   ```

3. **Test trên browser** (1 giờ)
   - Open http://localhost:8080/
   - Nhập username/password
   - Check xem login có hoạt động không

**Commit:**
```
git commit -m "feat: integrate login API with frontend"
```

---

### Ngày 4: Dashboard Auth Check (2 giờ)

**Thực hành:**
1. **Add auth check vào dashboard.html** (30 phút)
   ```javascript
   function checkAuth() {
       const user = localStorage.getItem('user');
       if (!user) {
           alert('Please login first');
           window.location.href = '/';
       }
   }
   
   // Gọi khi page load
   document.addEventListener('DOMContentLoaded', checkAuth);
   ```

2. **Add logout function** (30 phút)
   ```javascript
   function handleLogout() {
       localStorage.removeItem('user');
       window.location.href = '/';
   }
   ```

3. **Test** (1 giờ)
   - Logout
   - Truy cập dashboard trực tiếp
   - Phải redirect về login

**Commit:**
```
git commit -m "feat: add auth check and logout"
```

---

### Ngày 5: Staff CRUD (4 giờ)

**Thực hành (Đây là nơi áp dụng tất cả):**

1. **Hiểu code trong staff.html** (30 phút)
   - Modal form
   - Table display
   - Button actions

2. **Code loadStaff function** (1 giờ)
   ```javascript
   async function loadStaff() {
       try {
           const response = await fetch('/api/staffs');
           const data = await response.json();
           renderTable(data);
       } catch (error) {
           console.error(error);
       }
   }
   ```

3. **Code saveStaff function** (1 giờ)
   - POST để tạo mới
   - PUT để cập nhật
   - Xác định bằng dataset.editingId

4. **Code deleteStaff function** (30 phút)
   - Confirm dialog
   - DELETE request
   - Reload list

5. **Test toàn bộ** (1 giờ)
   - Thêm nhân viên
   - Sửa nhân viên
   - Xóa nhân viên
   - Refresh xem dữ liệu tồn tại

**Commit:**
```
git commit -m "feat: integrate staff CRUD with API"
```

---

## 📖 TUẦN 4: Copy Pattern & Polish

### Ngày 1-2: Copy Pattern sang 5 trang khác (5 giờ)

**Properties, Contracts, Payments, Tenants, Owners**

Mỗi trang:
1. Copy code từ staff.html (15 phút)
2. Đổi API endpoint (5 phút)
3. Đổi field names (15 phút)
4. Test (10 phút)

**Commit:**
```
git commit -m "feat: integrate all CRUD pages with API"
```

---

### Ngày 3: Change Password (2 giờ)

**Đã có sẵn 100%, không cần sửa**

Chỉ cần hiểu code:
1. Đọc change-password.html (30 phút)
2. Hiểu validation logic (30 phút)
3. Test flow (1 giờ)
   - Login lần đầu
   - Redirect change-password
   - Change password
   - Login lần 2 với password mới

---

### Ngày 4-5: Testing & Optimization (3 giờ)

**Test toàn bộ flow:**
1. Login → Dashboard → Staff CRUD (30 phút)
2. Search functionality (30 phút)
3. Error cases (30 phút)
4. Fix lỗi & optimize (1.5 giờ)

**Commit:**
```
git commit -m "fix: final testing and bug fixes"
```

---

## 📚 FILE NÀO ĐỌC LÚC NÀO

### Tuần 1:
- ✅ ARCHITECTURE_DIAGRAM.md (Ngày 1)
- ✅ TEMPLATE_CODE_LIEN_KET_API.md (Ngày 1-2)
- ✅ FLOW_DANG_NHAP_VA_PHAN_QUYEN.md (Ngày 4)

### Tuần 2:
- ✅ TEMPLATE_CODE_LIEN_KET_API.md (Làm bài tập)
- ✅ CHEATSHEET_NHANH.md (Reference)

### Tuần 3-4:
- ✅ CHEATSHEET_NHANH.md (Main reference)
- ✅ TONG_HOP_KET_LUAN.md (Debug)

---

## 🎯 COMMIT HISTORY KỲ VỌNG

```
Tuần 1:
✓ "feat: add PropertyService with CRUD"
✓ "feat: add PropertyController with API"
✓ "feat: complete CRUD for Property"

Tuần 2:
✓ "feat: add all Service classes (Tenant, Contract, Payment, Owner, User)"
✓ "feat: add all REST API Controllers"
✓ "feat: add error handling and response wrapper"
✓ "feat: add authentication/login endpoint"

Tuần 3:
✓ "feat: integrate login API with frontend"
✓ "feat: add auth check and logout"
✓ "feat: integrate staff CRUD with API"

Tuần 4:
✓ "feat: integrate all CRUD pages with API"
✓ "feat: fix final bugs and optimize"
✓ "docs: update README and API documentation"
```

---

## 🧠 CÔNG THỨC HỌC

### Mỗi ngày làm:
1. **Hiểu** (30 phút)
   - Đọc documentation
   - Xem code example
   - Vẽ diagram

2. **Làm** (1-2 giờ)
   - Tạo file Java
   - Code từng method
   - Build & test

3. **Test** (30 phút)
   - Postman test (backend)
   - Browser test (frontend)
   - Fix lỗi

4. **Commit** (10 phút)
   - Lưu code
   - Viết message rõ ràng
   - Push lên Git

---

## ✅ CHECKLIST HỌC TẬP

### Tuần 1:
- [ ] Hiểu Controller → Service → Repository flow
- [ ] Hiểu @GetMapping, @PostMapping, @PutMapping, @DeleteMapping
- [ ] Tạo PropertyService đầu tiên
- [ ] Tạo PropertyController đầu tiên
- [ ] Test GET trên Postman
- [ ] Test POST/PUT/DELETE trên Postman
- [ ] Commit mỗi bước

### Tuần 2:
- [ ] Tạo 5 Service class khác (copy-paste)
- [ ] Tạo 5 Controller class khác (copy-paste)
- [ ] Hiểu error handling pattern
- [ ] Tạo ErrorResponse & SuccessResponse
- [ ] Thêm try-catch vào tất cả Controller
- [ ] Tạo AuthController & login endpoint
- [ ] Test tất cả API trên Postman

### Tuần 3:
- [ ] Hiểu JavaScript fetch API
- [ ] Hiểu async/await
- [ ] Hiểu error handling trong JS
- [ ] Code handleLogin function
- [ ] Test login trên browser
- [ ] Code checkAuth function
- [ ] Code handleLogout function
- [ ] Code loadStaff + saveStaff + deleteStaff

### Tuần 4:
- [ ] Copy pattern từ staff.html sang 5 trang khác
- [ ] Test CRUD trên tất cả 6 trang
- [ ] Hiểu change-password flow
- [ ] Test change-password end-to-end
- [ ] Test search/filter (nếu có)
- [ ] Fix tất cả lỗi
- [ ] Commit final version

---

## 💡 ĐIỂM CẬN NẠP

**Nếu bạn cảm thấy:**
- Bối rối → Quay lại file ARCHITECTURE_DIAGRAM.md
- Quên code → Xem TEMPLATE_CODE_LIEN_KET_API.md
- Muốn copy nhanh → Xem CHEATSHEET_NHANH.md
- Muốn chi tiết → Xem HUONG_DAN_TIEN_TRINH_TIEN_HANH.md

---

## 🎓 KỲ VỌNG SAU 4 TUẦN

Bạn sẽ biết:
✅ Cách tạo Service (business logic)
✅ Cách tạo REST API (CRUD endpoints)
✅ Cách handle lỗi (exception handling)
✅ Cách authentication (login flow)
✅ Cách fetch API từ JavaScript
✅ Cách integrate frontend-backend
✅ Cách test bằng Postman
✅ Cách commit code lên Git

---

## 🚀 BƯỚC TIẾP THEO

### Sau 4 tuần:
1. Deploy lên server
2. Add JWT authentication (advanced)
3. Add role-based access control
4. Add notification system
5. Add analytics dashboard

---

**BẮTĐẦU TUẦN 1 NGÀY MÀI NHÉ! 💪**

Hãy focus vào một thứ một lúc, không cần vội!

