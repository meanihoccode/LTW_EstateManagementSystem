# 🔐 HƯỚNG DẪN LOGIN & AUTHENTICATION CHI TIẾT

## 📚 PHẦN 1: HIỂU KHÁI NIỆM

### 1.1 LOGIN là gì?

**Định nghĩa đơn giản:**
- User nhập username + password
- Server kiểm tra xem có đúng không
- Nếu đúng → cấp **token** (giấy chứng nhận)
- User dùng token này để chứng minh "Tôi đã đăng nhập"

**Ví dụ:**
```
Sân bay:
1. Bạn xuất trình hộ chiếu
2. Nhân viên kiểm tra (xác thực)
3. Nếu hợp lệ → cấp vé lên máy bay
4. Bạn giữ vé → dùng để lên máy

Web:
1. Bạn nhập username/password
2. Server kiểm tra database
3. Nếu hợp lệ → cấp token
4. Bạn giữ token → dùng để gọi API
```

---

### 1.2 Token là gì?

**Token = Chứng minh thư**

Sau khi đăng nhập, server trả về 1 token:
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Dùng token để làm gì?**
```
GET /api/properties
Header: Authorization: Bearer <token>
```

Server nhìn thấy token → biết "Cái này là user đã đăng nhập" → cho phép truy cập

---

### 1.3 JWT vs Token Thường

#### **A. TOKEN THƯỜNG (Simple Token)**

**Cách tạo:**
```java
// Backend
String token = UUID.randomUUID().toString();  // "abc123xyz"
// Lưu vào database
```

**Kiểm tra:**
```java
// Lần tới user gửi token
// Server: SELECT * FROM account WHERE token = "abc123xyz"
// Nếu có → OK, không → lỗi
```

**Ưu điểm:** Dễ hiểu, dễ làm
**Nhược điểm:** Chậm (mỗi lần kiểm tra vào DB), kém bảo mật

---

#### **B. JWT TOKEN (JSON Web Token)**

**Cách tạo:**
```java
// Backend
String jwt = Jwts.builder()
    .setSubject("john")  // username
    .setIssuedAt(new Date())
    .setExpiration(new Date(System.currentTimeMillis() + 3600000))  // 1 giờ
    .signWith(SignatureAlgorithm.HS512, SECRET_KEY)  // ký số
    .compact();
// Trả về JWT → không lưu DB
```

**Kiểm tra:**
```java
// Lần tới user gửi JWT
// Server: Kiểm tra chữ ký của JWT
// Nếu chữ ký hợp lệ + chưa hết hạn → OK
// Không cần vào DB
```

**Ưu điểm:** Nhanh, an toàn, có hạn sử dụng, stateless (không lưu DB)
**Nhược điểm:** Phức tạp hơn

---

## 🏗️ PHẦN 2: KIẾN TRÚC LOGIN

### 2.1 Flow Login Đơn Giản (Làm BƯỚC 1)

```
┌─────────┐                              ┌─────────┐
│Frontend │                              │ Backend │
│         │                              │         │
└────┬────┘                              └────┬────┘
     │                                        │
     │ 1. User nhập username/password        │
     │ (trong form)                          │
     │                                        │
     │ 2. Frontend POST /api/auth/login       │
     │────────────────────────────────────>  │
     │    Body: {"username": "john",         │
     │            "password": "pass123"}     │
     │                                        │
     │                                  3. Server kiểm tra:
     │                                     - Username có tồn tại?
     │                                     - Password có đúng?
     │                                     - SELECT * FROM Account
     │                                       WHERE username="john"
     │                                        │
     │                                  4. Nếu OK:
     │                                     - Tạo Token
     │                                     - Trả về LoginResponse
     │                                        │
     │ 5. Frontend nhận token                │
     │ <────────────────────────────────────│
     │    Body: {                            │
     │      "token": "abc123xyz",            │
     │      "username": "john",              │
     │      "role": "admin",                 │
     │      "status": "SUCCESS"              │
     │    }                                  │
     │                                        │
     │ 6. localStorage.setItem('token', ...)│
     │ 7. window.location = '/dashboard'    │
     │                                        │
```

---

### 2.2 Flow Kiểm Tra Quyền (Truy cập API)

```
┌─────────┐                              ┌─────────┐
│Frontend │                              │ Backend │
│         │                              │         │
└────┬────┘                              └────┬────┘
     │                                        │
     │ 1. User bấm "View Properties"        │
     │                                        │
     │ 2. Frontend GET /api/properties       │
     │────────────────────────────────────>  │
     │    Header: {                          │
     │      "Authorization": "Bearer abc123" │
     │    }                                   │
     │                                        │
     │                                  3. Server kiểm tra:
     │                                     - Token có trong header?
     │                                     - Token có hợp lệ?
     │                                     - Token chưa hết hạn?
     │                                        │
     │                                  4. Nếu OK:
     │                                     - SELECT * FROM Property
     │                                     - Trả về dữ liệu
     │                                        │
     │ 5. Frontend nhận dữ liệu               │
     │ <────────────────────────────────────│
     │    Body: [                            │
     │      {"id": 1, "name": "Nhà A", ...} │
     │    ]                                  │
     │                                        │
     │ 6. Hiển thị lên bảng                 │
     │                                        │
```

---

## 🚀 PHẦN 3: HỌC TỪng BƯỚC

### **BƯỚC 1: SỬA FRONTEND LOGIN (2 giờ)**

Hiện tại form login đang submit như form HTML thường:
```html
<form action="/login" method="post">  <!-- ❌ Sai, không gọi API -->
```

**Cần thay thành:**
```html
<form onsubmit="handleLogin(event)">  <!-- ✅ Đúng, gọi JS -->
```

**File cần sửa:** `src/main/resources/templates/index.html`

#### Bước 1.1: Sửa Form (Thêm IDs cho input)

```html
<!-- Login Modal -->
<div id="overlay" class="overlay"></div>
<div id="loginModal" class="modal">
    <div class="modal-content">
        <span class="close" id="closeLogin">&times;</span>
        <h2>Welcome Back</h2>
        <p class="modal-subtitle">Sign in to your account</p>
        
        <!-- SỬA: onsubmit + thay email thành username -->
        <form onsubmit="handleLogin(event)">
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" placeholder="Enter your username" required>
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" placeholder="Enter your password" required>
            </div>
            <div class="form-remember">
                <input type="checkbox" id="remember" name="remember">
                <label for="remember" class="remember-label">Remember me</label>
            </div>
            <button type="submit" class="btn-submit">Sign In</button>
            
            <!-- Thêm div để hiển thị lỗi -->
            <div id="loginError" style="color: red; margin-top: 10px; display: none;"></div>
        </form>
    </div>
</div>
```

---

#### Bước 1.2: Thêm Hàm handleLogin() (Ghi vào cuối file index.html, trước thẻp đóng body)

```html
<script>
    // ========== LOGIN HANDLER ==========
    
    /**
     * Hàm handleLogin: Xử lý đăng nhập
     * 
     * Quá trình:
     * 1. Ngăn form submit mặc định
     * 2. Lấy username + password từ input
     * 3. Gửi POST đến /api/auth/login
     * 4. Nếu OK → lưu token + redirect dashboard
     * 5. Nếu lỗi → hiển thị thông báo lỗi
     */
    function handleLogin(event) {
        event.preventDefault();  // Ngăn form refresh page
        
        // 1. Lấy giá trị từ form
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        
        // 2. Gửi request đến backend
        fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        })
        .then(response => {
            // Kiểm tra HTTP status (200, 401, 500...)
            if (!response.ok) {
                throw new Error(`HTTP Error: ${response.status}`);
            }
            return response.json();  // Parse JSON
        })
        .then(data => {
            // 3. Xử lý response từ backend
            
            console.log('Login response:', data);  // Debug
            
            if (data.status === 'FIRST_LOGIN') {
                // Lần đầu đăng nhập → cần đổi mật khẩu
                alert('Vui lòng đổi mật khẩu lần đầu đăng nhập');
                window.location.href = `/change-password.html?accountId=${data.accountId}`;
            } 
            else if (data.status === 'SUCCESS') {
                // Đăng nhập thành công
                
                // 4. Lưu token + thông tin user vào localStorage
                localStorage.setItem('token', data.accountId);  // Lưu account ID làm token
                localStorage.setItem('username', data.username);
                localStorage.setItem('userRole', data.role);
                
                // 5. Đóng modal
                document.getElementById('loginModal').style.display = 'none';
                document.getElementById('overlay').style.display = 'none';
                
                // Xóa form
                document.querySelector('#loginModal form').reset();
                
                // 6. Redirect đến dashboard
                setTimeout(() => {
                    window.location.href = '/dashboard';
                }, 500);
            }
        })
        .catch(error => {
            // 7. Xử lý lỗi
            console.error('Login error:', error);
            
            const errorDiv = document.getElementById('loginError');
            errorDiv.style.display = 'block';
            errorDiv.textContent = 'Đăng nhập thất bại. Vui lòng kiểm tra username/password';
        });
    }
    
    // ========== LOGOUT HANDLER ==========
    
    /**
     * Hàm logout: Xóa token + redirect về login
     * Gọi hàm này khi user bấm nút "Đăng xuất"
     */
    function handleLogout() {
        // 1. Xóa token từ localStorage
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('userRole');
        
        // 2. Redirect về trang login
        window.location.href = '/';
    }
    
    // ========== CHECK LOGIN STATUS ==========
    
    /**
     * Hàm checkAuthStatus: Kiểm tra xem user đã đăng nhập chưa
     * 
     * Dùng trên trang dashboard:
     * - Nếu chưa đăng nhập → redirect về login
     * - Nếu đã đăng nhập → load dữ liệu
     * 
     * Gọi hàm này ở đầu trang dashboard.html
     */
    function checkAuthStatus() {
        const token = localStorage.getItem('token');
        
        if (!token) {
            console.log('Không có token, redirect về login');
            window.location.href = '/';
        } else {
            console.log('Token có, user đã đăng nhập');
        }
    }
    
    // ========== GET REQUEST VỚI TOKEN ==========
    
    /**
     * Hàm fetchWithAuth: Gọi API với token trong header
     * 
     * Cách dùng:
     * fetchWithAuth('/api/properties')
     *     .then(data => console.log(data))
     * 
     * Hoặc với body:
     * fetchWithAuth('/api/properties', {
     *     method: 'POST',
     *     body: JSON.stringify({name: 'New Property'})
     * })
     */
    function fetchWithAuth(url, options = {}) {
        const token = localStorage.getItem('token');
        
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };
        
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
        
        return fetch(url, {
            ...options,
            headers: headers
        })
        .then(response => {
            if (response.status === 401) {
                // Token hết hạn hoặc không hợp lệ
                alert('Phiên đăng nhập hết hạn, vui lòng đăng nhập lại');
                handleLogout();
                throw new Error('Unauthorized');
            }
            return response.json();
        });
    }
    
</script>
```

---

### **BƯỚC 2: KIỂM TRA TRÊN DASHBOARD (1 giờ)**

**File:** `src/main/resources/templates/dashboard.html`

Thêm vào đầu file `<body>` để kiểm tra login:

```html
<body>
    <script>
        // Kiểm tra user đã đăng nhập chưa
        checkAuthStatus();
        
        // Lấy thông tin user
        const username = localStorage.getItem('username');
        const role = localStorage.getItem('userRole');
        
        console.log('Logged in as:', username, role);
    </script>
    
    <!-- Sidebar với nút Logout -->
    <div class="sidebar">
        <!-- ... existing code ... -->
        <div class="logout-section">
            <button onclick="handleLogout()" class="btn-logout">Đăng Xuất</button>
        </div>
    </div>
    
    <!-- ... rest of page ... -->
</body>
```

---

### **BƯỚC 3: TỪ TOKEN ĐƠN GIẢN → JWT (2 giờ - BỎ QUA LẦN NÀY)**

**Ngày sau mới làm**, bước này là nâng cấp `handleLogin()` để dùng JWT thay vì simple token.

---

## 📝 PHẦN 4: HƯỚNG DẪN TỪNG BƯỚC CHI TIẾT

### Bước 4.1: Xem lại Backend Code

**File:** `src/main/java/com/example/ltw_quanlybds/api/AccountController.java`

Hiện tại backend `/api/auth/login` đã OK:

```java
@PostMapping("/login")
public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
    if (accountService.validatePassword(loginRequest.getUsername(), loginRequest.getPassword())) {
        Account account = accountService.findByUsername(loginRequest.getUsername());
        
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccountId(account.getId());
        loginResponse.setUsername(account.getUsername());
        loginResponse.setRole(account.getRole());
        loginResponse.setStatus("SUCCESS");
        loginResponse.setMessage("Đăng nhập thành công");
        
        return ResponseEntity.ok(loginResponse);
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
}
```

**Giải thích:**
1. Nhận `LoginRequest` (username + password)
2. Kiểm tra `validatePassword()` → so sánh với DB
3. Nếu OK → tạo `LoginResponse` + trả về
4. Frontend nhận → lưu token

---

### Bước 4.2: Code Frontend

**File:** `src/main/resources/templates/index.html`

Sửa form login và thêm `handleLogin()` (xem BƯỚC 1 ở trên)

---

### Bước 4.3: Test

```bash
# 1. Chạy server
gradlew bootRun

# 2. Vào http://localhost:8080

# 3. Bấm "Sign in" → nhập:
#    Username: admin
#    Password: admin123

# 4. Kiểm tra:
#    ✓ Có redirect sang dashboard?
#    ✓ localStorage có token?
#    ✓ Refresh page vẫn ở dashboard?
#    ✓ Bấm Logout → back về login?
```

---

## 🔐 PHẦN 5: JWT SẼ HỌC TRONG PHASE TIẾP THEO

**Hiện tại:** Token = ID (ví dụ: "123")
**Tương lai:** Token = JWT (ví dụ: "eyJhbGc...")

**JWT có thêm:**
- ✅ Chữ ký (signature) → khó giả mạo
- ✅ Hạn sử dụng (expiration) → token tự hết hạn
- ✅ Thông tin (claims) → lưu username, role trong token

---

## 📚 TÓM TẮT

| Khía cạnh | Login Đơn Giản | JWT |
|-----------|--|--|
| **Tạo token** | `UUID.randomUUID()` | `Jwts.builder().signWith(...)` |
| **Kiểm tra** | Query DB | Kiểm tra chữ ký |
| **Hết hạn** | Thủ công xóa | Tự động (expiration) |
| **An toàn** | Trung bình | Cao |
| **Tốc độ** | Chậm | Nhanh |
| **Khi nào học** | Ngay bây giờ | Phase 2 |

---

## ✅ CHECKLIST BƯỚC 1

- [ ] Sửa form index.html (thêm onsubmit)
- [ ] Thêm hàm handleLogin() 
- [ ] Thêm hàm handleLogout()
- [ ] Thêm hàm checkAuthStatus()
- [ ] Thêm hàm fetchWithAuth()
- [ ] Build & chạy server
- [ ] Test login flow
- [ ] Test logout
- [ ] Test refresh page

---

**Làm được chưa? Hãy báo tôi kết quả! 🚀**


