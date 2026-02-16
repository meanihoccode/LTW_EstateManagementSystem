# 🔐 JWT (JSON WEB TOKEN) - TÀI LIỆU HỌC TẬP

## 📚 PHẦN 1: JWT LÀ GÌ?

### 1.1 Định Nghĩa

**JWT = JSON Web Token**

Là 1 chuỗi text được **mã hóa + ký số**, dùng để:
- Chứng minh user đã đăng nhập
- Truyền thông tin giữa client & server

**Ví dụ JWT:**
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

### 1.2 Cấu Trúc JWT

JWT chia thành **3 phần**, ngăn cách bởi dấu chấm `.`:

```
header.payload.signature
```

**Phần 1: Header (Tiêu đề)**
```json
{
  "alg": "HS256",      // Thuật toán ký: HS256, RS256...
  "typ": "JWT"         // Loại token: JWT
}

// Mã hóa Base64 → eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
```

**Phần 2: Payload (Tải dữ liệu)**
```json
{
  "sub": "1234567890",          // Subject (user ID)
  "name": "John Doe",           // Tên user
  "iat": 1516239022,            // Issued at (lúc tạo)
  "exp": 1516242622,            // Expiration (hết hạn)
  "role": "admin"               // Custom claim (role)
}

// Mã hóa Base64 → eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ
```

**Phần 3: Signature (Chữ ký)**
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret_key
)

// → SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

---

### 1.3 Cách Hoạt Động của JWT

```
┌──────────────────────────────────────────────────────────┐
│                   SERVER TẠO JWT                        │
├──────────────────────────────────────────────────────────┤
│                                                          │
│  Bước 1: Tạo header                                     │
│  {"alg": "HS256", "typ": "JWT"}                         │
│          ↓                                              │
│  Bước 2: Tạo payload                                   │
│  {"sub": "123", "name": "John", "exp": 3600}          │
│          ↓                                              │
│  Bước 3: Ký chữ (mã hóa)                               │
│  HMACSHA256(header.payload, SECRET_KEY)               │
│          ↓                                              │
│  JWT = header.payload.signature                        │
│  "eyJ...abc...xyz"                                    │
│          ↓                                              │
│  Trả về JWT cho client                                │
└──────────────────────────────────────────────────────────┘
           ↓
┌──────────────────────────────────────────────────────────┐
│              CLIENT LƯU & GỬI JWT                      │
├──────────────────────────────────────────────────────────┤
│                                                          │
│  1. localStorage.setItem('token', jwt)                 │
│                                                          │
│  2. Lần tới gọi API:                                  │
│     GET /api/properties                               │
│     Header: Authorization: Bearer eyJ...xyz           │
└──────────────────────────────────────────────────────────┘
           ↓
┌──────────────────────────────────────────────────────────┐
│           SERVER KIỂM TRA JWT                          │
├──────────────────────────────────────────────────────────┤
│                                                          │
│  Bước 1: Lấy JWT từ header                            │
│  "Authorization: Bearer eyJ...xyz"                    │
│          ↓                                              │
│  Bước 2: Tách 3 phần                                 │
│  header = eyJ...                                      │
│  payload = abc...                                     │
│  signature = xyz...                                   │
│          ↓                                              │
│  Bước 3: Ký lại với SECRET_KEY                        │
│  newSignature = HMACSHA256(header.payload, SECRET)   │
│          ↓                                              │
│  Bước 4: So sánh chữ ký                              │
│  if (newSignature == signature) {                     │
│      // JWT hợp lệ ✅                                 │
│      // Kiểm tra hạn sử dụng                         │
│      if (now < expiration) {                         │
│          // Chưa hết hạn ✅                          │
│          // Cho phép truy cập 🟢                     │
│      } else {                                        │
│          // Hết hạn ❌                               │
│          // Từ chối truy cập 🔴                     │
│      }                                                │
│  } else {                                             │
│      // JWT không hợp lệ (giả mạo) ❌               │
│      // Từ chối truy cập 🔴                        │
│  }                                                    │
└──────────────────────────────────────────────────────────┘
```

---

## 🏗️ PHẦN 2: JWT vs TOKEN THƯỜNG

### So Sánh Chi Tiết

| Tiêu chí | Token Thường | JWT |
|---------|-------------|-----|
| **Tạo token** | `UUID.randomUUID()` → "abc123xyz" | `Jwts.builder()...` → "header.payload.signature" |
| **Lưu trữ** | Phải lưu vào DB | Không cần lưu (stateless) |
| **Kiểm tra** | Query DB mỗi lần | Kiểm tra chữ ký (không cần DB) |
| **An toàn** | Có thể bị lộ nếu DB bị hack | Khó giả mạo (có chữ ký) |
| **Hết hạn** | Thủ công DELETE từ DB | Tự động (expiration claim) |
| **Tốc độ** | Chậm (mỗi lần vào DB) | Nhanh (chỉ kiểm tra chữ ký) |
| **Scalability** | Khó (mỗi server phải share DB) | Dễ (mỗi server độc lập) |

### Ví Dụ So Sánh

**Token Thường:**
```
Flow:
1. User login → Server tạo token = "user_123_token"
2. Server: INSERT INTO tokens VALUES ("user_123_token", "123", NOW())
3. Client: localStorage.setItem('token', "user_123_token")
4. Client gọi API: Header: Authorization: Bearer user_123_token
5. Server: SELECT * FROM tokens WHERE token="user_123_token"
   → Có trong DB? → OK
   → Hết hạn? → Xóa đi, từ chối
```

**JWT:**
```
Flow:
1. User login → Server tạo JWT = "eyJ...xyz" (không lưu)
2. Client: localStorage.setItem('token', "eyJ...xyz")
3. Client gọi API: Header: Authorization: Bearer eyJ...xyz
4. Server: 
   - Tách JWT = [header, payload, signature]
   - Ký lại: newSig = HMACSHA256(header.payload, SECRET)
   - So sánh: newSig == signature? → OK
   - Kiểm tra: now < expiration? → Chưa hết hạn → OK
```

---

## 🛠️ PHẦN 3: IMPLEMENT JWT TRONG JAVA SPRING

### 3.1 Thêm Dependency

**File:** `build.gradle`

```gradle
dependencies {
    // ... existing dependencies ...
    
    // JWT Library
    implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'
}
```

---

### 3.2 Tạo JWT Utility Class

**File:** `src/main/java/com/example/ltw_quanlybds/util/JwtUtil.java`

```java
package com.example.ltw_quanlybds.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JwtUtil: Lớp tiện ích để tạo & kiểm tra JWT
 * 
 * Cách dùng:
 * - Tạo JWT: jwtUtil.generateToken("john", "admin", 3600)
 * - Kiểm tra: jwtUtil.validateToken(token)
 * - Lấy thông tin: jwtUtil.getUsernameFromToken(token)
 */
@Component
public class JwtUtil {
    
    // Secret key dùng để ký JWT (phải giữ bí mật!)
    @Value("${jwt.secret:mySecretKeyForJwtTokenGenerationPurposeOnly}")
    private String jwtSecret;
    
    // Thời hạn sử dụng JWT (milliseconds)
    @Value("${jwt.expiration:3600000}")  // Mặc định 1 giờ
    private long jwtExpirationMs;
    
    /**
     * Tạo JWT Token
     * 
     * @param username Tên đăng nhập
     * @param role Vai trò (admin, staff...)
     * @return JWT Token
     */
    public String generateToken(String username, String role) {
        // Chuẩn bị thông tin cần lưu trong token
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        // Tạo JWT
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        String token = Jwts.builder()
            .setSubject(username)           // Tên đăng nhập
            .claim("role", role)            // Vai trò
            .setIssuedAt(now)               // Lúc tạo
            .setExpiration(expiryDate)      // Hết hạn
            .signWith(key, SignatureAlgorithm.HS512)  // Ký chữ
            .compact();
        
        return token;
    }
    
    /**
     * Lấy username từ JWT
     * 
     * @param token JWT Token
     * @return Username
     */
    public String getUsernameFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            
            return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Lấy role từ JWT
     * 
     * @param token JWT Token
     * @return Role
     */
    public String getRoleFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            
            return (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Kiểm tra JWT có hợp lệ không
     * 
     * @param token JWT Token
     * @return true nếu hợp lệ, false nếu lỗi
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);  // Nếu lỗi → throw exception
            
            return true;
        } catch (Exception e) {
            System.err.println("JWT validation failed: " + e.getMessage());
            return false;
        }
    }
}
```

---

### 3.3 Sửa AccountController để Dùng JWT

**File:** `src/main/java/com/example/ltw_quanlybds/api/AccountController.java`

Sửa hàm `login()`:

```java
@Autowired
private JwtUtil jwtUtil;

@PostMapping("/login")
public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
    if (accountService.validatePassword(loginRequest.getUsername(), loginRequest.getPassword())) {
        Account account = accountService.findByUsername(loginRequest.getUsername());
        
        // 🔑 Tạo JWT Token
        String jwtToken = jwtUtil.generateToken(account.getUsername(), account.getRole());
        
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);              // ← Thay đổi: trả JWT thay vì ID
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

---

### 3.4 Tạo Interceptor để Kiểm Tra JWT

**File:** `src/main/java/com/example/ltw_quanlybds/config/JwtInterceptor.java`

```java
package com.example.ltw_quanlybds.config;

import com.example.ltw_quanlybds.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JwtInterceptor: Interceptor kiểm tra JWT trước khi vào controller
 * 
 * Flow:
 * 1. Request tới → Interceptor kiểm tra
 * 2. Nếu không có Authorization header → 401 Unauthorized
 * 3. Nếu JWT không hợp lệ → 401 Unauthorized
 * 4. Nếu OK → cho phép vào controller
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // Các endpoint không cần kiểm tra token
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/login") || 
            path.equals("/") || 
            path.startsWith("/css") || 
            path.startsWith("/js") || 
            path.startsWith("/img")) {
            return true;  // Bỏ qua kiểm tra
        }
        
        // Lấy Authorization header
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || authHeader.isEmpty()) {
            // Không có token → 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Missing Authorization header\"}");
            return false;
        }
        
        // Tách "Bearer <token>"
        if (!authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Invalid Authorization format\"}");
            return false;
        }
        
        String token = authHeader.substring(7);  // Bỏ "Bearer "
        
        // Kiểm tra JWT
        if (!jwtUtil.validateToken(token)) {
            // Token không hợp lệ → 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
            return false;
        }
        
        // Token hợp lệ → cho phép vào controller
        request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
        request.setAttribute("role", jwtUtil.getRoleFromToken(token));
        
        return true;
    }
}
```

---

### 3.5 Đăng Ký Interceptor trong Config

**File:** `src/main/java/com/example/ltw_quanlybds/config/WebConfig.java`

```java
package com.example.ltw_quanlybds.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private JwtInterceptor jwtInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor);
    }
}
```

---

## 🧪 PHẦN 4: TEST JWT

### 4.1 Test Tạo JWT (Postman)

```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

Body:
{
  "username": "admin",
  "password": "admin123"
}

Response:
{
  "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJhZG1pbiIsImlhdCI6MTcwMjM0NTYwMCwiZXhwIjoxNzAyMzQ5MjAwfQ.abc...",
  "status": "SUCCESS",
  "message": "Đăng nhập thành công"
}
```

### 4.2 Test Sử Dụng JWT (Postman)

```
GET http://localhost:8080/api/properties
Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...

Response:
[
  {"id": 1, "name": "Nhà A", ...},
  {"id": 2, "name": "Nhà B", ...}
]
```

---

## 📝 PHẦN 5: CÁCH TẠI application.properties

**File:** `src/main/resources/application.properties`

Thêm config JWT:

```properties
# JWT Configuration
jwt.secret=myVerySecretKeyFor_JWT_TokenGeneration_Purpose_Only_Change_This_In_Production
jwt.expiration=3600000
```

---

## ✅ TIMELINE HỌC JWT

| Thứ tự | Nội dung | Thời gian | Khi nào |
|--------|---------|----------|--------|
| 1 | **Login đơn giản** (không JWT) | 2-3 giờ | Ngay hôm nay |
| 2 | **Hiểu JWT là gì** (lý thuyết) | 1 giờ | Hôm nay chiều |
| 3 | **Implement JWT** (code backend) | 2-3 giờ | Ngày mai |
| 4 | **Test JWT** (Postman) | 1 giờ | Ngày mai |
| 5 | **Fix Frontend** (gửi JWT) | 1 giờ | Ngày mai |

---

## 🎯 TÓM LẠI

**Lộ trình bạn nên làm:**

```
NGAY HÔM NAY:
┌─────────────────────────────────────────┐
│ 1. Sửa Frontend Login (1 giờ)          │
│ 2. Test login flow (30 phút)           │
│ 3. Đọc file này (30 phút)              │
│ 4. Hiểu JWT là gì (lý thuyết)          │
└─────────────────────────────────────────┘
        ↓
NGÀY MAI:
┌─────────────────────────────────────────┐
│ 1. Thêm JWT dependency (5 phút)        │
│ 2. Tạo JwtUtil class (30 phút)        │
│ 3. Sửa AccountController (30 phút)     │
│ 4. Tạo JwtInterceptor (30 phút)       │
│ 5. Test JWT (1 giờ)                   │
│ 6. Sửa Frontend gửi JWT (30 phút)     │
└─────────────────────────────────────────┘
```

---

**Bất kỳ thắc mắc nào, hỏi tôi! 🚀**


