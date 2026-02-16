# 🔐 HƯỚNG DẪN JWT CHO NGƯỜI MỚI BẮT ĐẦU

## 📚 JWT LÀ GÌ?

### **Khái Niệm Đơn Giản**

Hãy tưởng tượng: Bạn đi vào một quán cà phê:

```
1️⃣ BAN ĐẦU (Không có JWT):
   - Bạn: "Tôi là Nguyễn Văn A"
   - Quán: "OK, bạn là ai, tôi không biết bạn"
   - Bạn phải nhận diện lại mỗi lần đặt hàng 😅

2️⃣ SỬ DỤNG JWT (Có JWT):
   - Bạn: "Tôi là Nguyễn Văn A" + đưa vé VIP
   - Quán: "OK, vé này của bạn, tôi tin đó. Đặt hàng gì?"
   - Bạn chỉ cần đưa vé, không cần nói lại tên 👍
```

**JWT = "Vé VIP" này!**

---

## 🧩 CÁCH HOẠT ĐỘNG

### **3 Bước JWT**

```
┌─────────────────────────────────────────────────────────────────┐
│                      FLOW ĐĂNG NHẬP VỚI JWT                      │
└─────────────────────────────────────────────────────────────────┘

📱 FRONTEND (Trình duyệt)          🖥️ BACKEND (Server)
        │                                    │
        │  1️⃣ POST /api/auth/login          │
        │    {username, password}            │
        │───────────────────────────────────>│
        │                                    │
        │                          ✓ Kiểm tra username/password
        │                          ✓ Nếu đúng → Tạo JWT token
        │                          ✓ Trả về token
        │                                    │
        │  2️⃣ 200 OK                         │
        │    {token: "eyJhbGc..."}           │
        │<───────────────────────────────────│
        │                                    │
        │  💾 Lưu token vào localStorage      │
        │                                    │
        │  3️⃣ GET /api/properties            │
        │    Header: Authorization: Bearer {token}
        │───────────────────────────────────>│
        │                                    │
        │                      ✓ Xác minh token (có hợp lệ không?)
        │                      ✓ Nếu OK → trả về dữ liệu
        │                                    │
        │  4️⃣ 200 OK                         │
        │    {data: [...]}                   │
        │<───────────────────────────────────│
```

---

## 🔒 JWT CÓ CẤU TRÚC NTH?

### **JWT Token Bao Gồm 3 Phần**

```
Token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
                           ↓                      ↓                      ↓
                      HEADER                  PAYLOAD                 SIGNATURE
                    (Phần 1)                 (Phần 2)                (Phần 3)
```

### **1️⃣ HEADER (Phần 1) - Loại Token**
```json
{
  "alg": "HS256",        // Thuật toán mã hóa
  "typ": "JWT"           // Kiểu token
}
```

### **2️⃣ PAYLOAD (Phần 2) - Dữ Liệu**
```json
{
  "sub": "1234567890",   // Subject (User ID)
  "name": "John Doe",    // Tên user
  "iat": 1516239022,     // Khi phát hành (timestamp)
  "exp": 1516242622,     // Khi hết hạn (timestamp)
  "role": "Admin"        // Vai trò
}
```

### **3️⃣ SIGNATURE (Phần 3) - Chữ Ký**
```
Tạo bằng cách:
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  SECRET_KEY
)
```

**Tại sao cần chữ ký?** → Để bảo vệ, chỉ server mới có thể tạo/thay đổi token

---

## 🛠️ BƯỚC 1: THÊM JWT LIBRARY VÀO BACKEND

### **1. Mở file `build.gradle`**

Tìm section `dependencies`:

```gradle
dependencies {
    // Các dependency cũ...
    
    // ✅ Thêm JWT dependency này
    implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'
}
```

### **2. Build lại project**

```bash
gradlew clean build
```

---

## 🛠️ BƯỚC 2: TẠOCLASS JWT UTILITY

### **Tạo file: `JwtTokenProvider.java`**

**Path:** `src/main/java/com/example/ltw_quanlybds/security/JwtTokenProvider.java`

```java
package com.example.ltw_quanlybds.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {

    // ✅ Secret key để ký token (lưu ý: phải >= 32 ký tự)
    @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationAndValidation12345}")
    private String jwtSecret;

    // ✅ Thời hạn token (24 giờ = 86400000 milliseconds)
    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationMs;

    /**
     * 1️⃣ TẠO TOKEN
     * Nhập vào: username, role
     * Trả ra: JWT token string
     */
    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(username)                    // Ai sở hữu token (username)
                .claim("role", role)                     // Thông tin thêm (role)
                .setIssuedAt(now)                        // Khi phát hành
                .setExpiration(expiryDate)               // Khi hết hạn
                .signWith(key, SignatureAlgorithm.HS256) // Ký token
                .compact();                              // Chuyển thành string
    }

    /**
     * 2️⃣ LẤY USERNAME TỪ TOKEN
     * Nhập vào: token
     * Trả ra: username
     */
    public String getUsernameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 3️⃣ LẤY ROLE TỪ TOKEN
     * Nhập vào: token
     * Trả ra: role
     */
    public String getRoleFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    /**
     * 4️⃣ KIỂM TRA TOKEN CÓ HỢP LỆ KHÔNG
     * Nhập vào: token
     * Trả ra: true/false
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);  // Nếu lỗi → throw exception
            
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("JWT validation failed: " + e.getMessage());
            return false;
        }
    }
}
```

**Giải thích:**
- `generateToken()` → Tạo token khi user đăng nhập
- `getUsernameFromToken()` → Lấy username từ token
- `getRoleFromToken()` → Lấy role từ token
- `validateToken()` → Kiểm tra token còn hợp lệ không

---

## 🛠️ BƯỚC 3: CẬP NHẬT ACCOUNTCONTROLLER

### **Sửa file: `AccountController.java`**

Thay đổi login method để trả về JWT token:

```java
package com.example.ltw_quanlybds.api;

import com.example.ltw_quanlybds.dto.LoginRequest;
import com.example.ltw_quanlybds.dto.LoginResponse;
import com.example.ltw_quanlybds.entity.Account;
import com.example.ltw_quanlybds.service.AccountService;
import com.example.ltw_quanlybds.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;  // ✅ Thêm dòng này

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        // ✅ Kiểm tra username/password
        if (accountService.validatePassword(loginRequest.getUsername(), loginRequest.getPassword())) {
            Account account = accountService.findByUsername(loginRequest.getUsername());

            // ✅ Kiểm tra lần đầu đăng nhập
            if (account.getFirstLogin() != null && account.getFirstLogin()) {
                LoginResponse loginResponse = new LoginResponse();
                loginResponse.setAccountId(account.getId());
                loginResponse.setUsername(account.getUsername());
                loginResponse.setRole(account.getRole());
                loginResponse.setStatus("FIRST_LOGIN");
                loginResponse.setMessage("Vui lòng đổi mật khẩu lần đầu đăng nhập");
                return ResponseEntity.ok(loginResponse);
            }

            // ✅ TẠO JWT TOKEN (SỬA PHẦN NÀY)
            String token = jwtTokenProvider.generateToken(account.getUsername(), account.getRole());
            
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccountId(account.getId());
            loginResponse.setUsername(account.getUsername());
            loginResponse.setRole(account.getRole());
            loginResponse.setToken(token);           // ✅ Thêm token
            loginResponse.setStatus("SUCCESS");
            loginResponse.setMessage("Đăng nhập thành công");
            
            return ResponseEntity.ok(loginResponse);
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new LoginResponse(null, null, null, null, "FAILED", "Invalid credentials"));
    }

    // ... các method khác giữ nguyên ...
}
```

---

## 🛠️ BƯỚC 4: CẬP NHẬT LOGINRESPONSE DTO

### **Sửa file: `LoginResponse.java`**

```java
package com.example.ltw_quanlybds.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Integer accountId;
    private String username;
    private String role;
    private String token;           // ✅ Thêm field này
    private String status;
    private String message;
}
```

---

## 🛠️ BƯỚC 5: TẠO JWT FILTER (ĐỂ KIỂM TRA TOKEN)

### **Tạo file: `JwtAuthenticationFilter.java`**

**Path:** `src/main/java/com/example/ltw_quanlybds/security/JwtAuthenticationFilter.java`

```java
package com.example.ltw_quanlybds.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // 1️⃣ Lấy token từ header
            String token = getTokenFromRequest(request);

            // 2️⃣ Nếu có token và hợp lệ
            if (token != null && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsernameFromToken(token);
                String role = jwtTokenProvider.getRoleFromToken(token);
                
                // ✅ Lưu vào request attributes để controller dùng
                request.setAttribute("username", username);
                request.setAttribute("role", role);
            }
        } catch (Exception e) {
            System.err.println("JWT filter error: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 🔍 LẤY TOKEN TỪ HEADER
     * Format: Authorization: Bearer eyJhbGc...
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);  // Bỏ "Bearer " → lấy token
        }
        
        return null;
    }
}
```

---

## 📝 BƯỚC 6: THÊM VÀO APPLICATION.PROPERTIES

### **Sửa file: `application.properties`**

```properties
# ... Các config cũ ...

# ✅ JWT Configuration
jwt.secret=mySecretKeyForJWTTokenGenerationAndValidation12345
jwt.expiration=86400000  # 24 giờ (milliseconds)
```

---

## 🌐 BƯỚC 7: JAVASCRIPT FRONTEND - LƯỚI HỌC CODE

### **Tệp: `src/main/resources/static/js/script.js`**

```javascript
// ============================================
// 1️⃣ ĐĂNG NHẬP & LẤY TOKEN
// ============================================

function handleLogin(event) {
    event.preventDefault();
    
    // 📝 Lấy dữ liệu từ form
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    
    // 🌐 Gọi API login
    fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username, password })
    })
    .then(res => res.json())
    .then(data => {
        console.log('Login response:', data);
        
        // ✅ Nếu đăng nhập thành công
        if (data.token) {
            // 💾 Lưu token vào localStorage
            localStorage.setItem('token', data.token);
            localStorage.setItem('username', data.username);
            localStorage.setItem('role', data.role);
            
            // ➡️ Chuyển hướng sang dashboard
            window.location.href = '/dashboard.html';
        } else {
            // ❌ Nếu thất bại
            alert('Đăng nhập thất bại: ' + data.message);
        }
    })
    .catch(err => alert('Lỗi: ' + err.message));
}

// ============================================
// 2️⃣ GỬI REQUEST VỚI TOKEN
// ============================================

function getTokenFromLocalStorage() {
    return localStorage.getItem('token');
}

function fetchWithAuth(url, options = {}) {
    const token = getTokenFromLocalStorage();
    
    // ✅ Thêm token vào header
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };
    
    // ✅ Nếu có token thì thêm Authorization
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    
    return fetch(url, {
        ...options,
        headers
    });
}

// ============================================
// 3️⃣ KIỂM TRA ĐĂNG NHẬP
// ============================================

function checkAuth() {
    const token = localStorage.getItem('token');
    
    if (!token) {
        // ❌ Không có token → Về trang login
        window.location.href = '/';
    }
}

// ============================================
// 4️⃣ SỬ DỤNG TRONG CÁC TRANG
// ============================================

// VÍ DỤ: Lấy danh sách properties
function loadProperties() {
    checkAuth(); // ✅ Kiểm tra login trước
    
    fetchWithAuth('/api/properties')  // ✅ Dùng fetchWithAuth
        .then(res => res.json())
        .then(data => {
            console.log('Properties:', data);
            // ... Hiển thị dữ liệu ...
        })
        .catch(err => {
            if (err.status === 401) {
                // ❌ Token hết hạn → Logout
                alert('Phiên đăng nhập hết hạn, vui lòng đăng nhập lại');
                logout();
            } else {
                alert('Lỗi: ' + err.message);
            }
        });
}

// ============================================
// 5️⃣ ĐĂNG XUẤT
// ============================================

function logout() {
    // 🗑️ Xóa token khỏi localStorage
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
    
    // ➡️ Về trang login
    window.location.href = '/';
}
```

---

## 📋 TÓMLƯỢC 4 BƯỚC CHÍNH

### **Backend:**
1. ✅ Thêm JWT library (`build.gradle`)
2. ✅ Tạo `JwtTokenProvider.java` (tạo/kiểm tra token)
3. ✅ Cập nhật `AccountController.login()` (trả về token)
4. ✅ Cập nhật `LoginResponse` (thêm field token)

### **Frontend (JavaScript):**
1. ✅ `handleLogin()` - Lưu token vào localStorage
2. ✅ `fetchWithAuth()` - Gửi token kèm mỗi request
3. ✅ `checkAuth()` - Kiểm tra token trước load trang
4. ✅ `logout()` - Xóa token

---

## ✅ TEST FLOW

### **1. Test API Login bằng Postman**

```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
    "username": "admin",
    "password": "admin123"
}

✅ Response:
{
    "accountId": 1,
    "username": "admin",
    "role": "Admin",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "status": "SUCCESS",
    "message": "Đăng nhập thành công"
}
```

### **2. Test Lấy Properties Với Token**

```bash
GET http://localhost:8080/api/properties
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

✅ Response: [...]
```

### **3. Test Frontend**

1. Chạy server: `gradlew bootRun`
2. Truy cập: http://localhost:8080
3. Đăng nhập
4. Kiểm tra DevTools (F12):
   - Console: `localStorage.getItem('token')`
   - Phải thấy token

---

## 🎯 CHECKLIST

- [ ] Thêm JWT dependency vào `build.gradle`
- [ ] Tạo `JwtTokenProvider.java`
- [ ] Tạo `JwtAuthenticationFilter.java`
- [ ] Cập nhật `AccountController.java`
- [ ] Cập nhật `LoginResponse.java`
- [ ] Thêm JWT config vào `application.properties`
- [ ] Cập nhật `script.js` (handleLogin, fetchWithAuth, checkAuth, logout)
- [ ] Build lại: `gradlew clean build`
- [ ] Test login API bằng Postman
- [ ] Test frontend login flow

---

## ❓ CÂU HỎI THƯỜNG GẶP

### **Q1: Token lưu ở đâu?**
A: localStorage của trình duyệt (giống cookies)

### **Q2: Token có bao lâu?**
A: 24 giờ (config trong `jwt.expiration`)

### **Q3: Nếu token hết hạn sao?**
A: Phải đăng nhập lại để lấy token mới

### **Q4: Có cần giấu secret key không?**
A: **CÓ!** Đặt trong `application-prod.properties` cho production

### **Q5: Bearer token là gì?**
A: Format chuẩn: `Authorization: Bearer {token}`

---

## 📝 LƯUÝ

- ⚠️ **Không hardcode** secret key, dùng environment variables
- ⚠️ Token lưu localStorage dễ bị XSS attack, nên sử dụng HttpOnly cookies ở production
- ⚠️ Backend phải validate token trước khi xử lý request
- ⚠️ Khi user logout, xóa token khỏi localStorage

---

**Sẵn sàng bắt đầu? Hãy follow từng bước! 🚀**

