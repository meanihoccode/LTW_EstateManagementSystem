# 🔐 FLOW ĐĂNG NHẬP & PHÂN QUYỀN CHI TIẾT

## 📊 Quy trình hiện tại

```
1. ADMIN TẠO TÀI KHOẢN
   ↓
   Admin POST → /api/auth/accounts
   ↓
   Account được tạo với:
   - username
   - password (tạo tạm thời ngẫu nhiên)
   - role
   - firstLogin = true ← FLAG ĐẶC BIỆT
   
2. NHÂN VIÊN ĐĂNG NHẬP LẦN ĐẦU
   ↓
   Nhân viên POST → /api/auth/login
   Request: { username, password }
   ↓
   Backend kiểm tra:
   - Tài khoản có tồn tại? ✓
   - Mật khẩu đúng? ✓
   - firstLogin = true? → REDIRECT CHANGE PASSWORD
   ↓
   Response: {
     accountId: 1,
     username: "nhanvien01",
     role: "Quản lý",
     status: "FIRST_LOGIN" ← CÁI NÀY QUAN TRỌNG,
     message: "Vui lòng đổi mật khẩu"
   }
   ↓
   Frontend nhận status = "FIRST_LOGIN"
   → Redirect tới /change-password?accountId=1

3. NHÂN VIÊN ĐỔI MẬT KHẨU
   ↓
   Nhân viên nhập:
   - Mật khẩu cũ (lấy từ email admin gửi)
   - Mật khẩu mới (8+ ký tự)
   - Xác nhận mật khẩu
   ↓
   POST → /api/auth/accounts/{accountId}/change-password
   Request: {
     oldPassword: "...",
     newPassword: "..."
   }
   ↓
   Backend xử lý:
   - Kiểm tra oldPassword đúng? ✓
   - Hash newPassword
   - Lưu lại
   - Set firstLogin = false ← QUAN TRỌNG
   ↓
   Response: {
     id: 1,
     username: "nhanvien01",
     firstLogin: false ← ĐÃ ĐỔI RỒI
   }
   ↓
   Frontend hiển thị success
   → Redirect tới /dashboard sau 2 giây

4. NHÂN VIÊN ĐĂNG NHẬP LẦN THỨ 2 TRỞ ĐI
   ↓
   POST → /api/auth/login
   Request: { username, newPassword }
   ↓
   Backend kiểm tra:
   - firstLogin = false ✓
   ↓
   Response: {
     accountId: 1,
     username: "nhanvien01",
     role: "Quản lý",
     status: "SUCCESS",
     message: "Đăng nhập thành công"
   }
   ↓
   Frontend:
   - Lưu vào localStorage
   - Redirect tới /dashboard
```

---

## 💻 CODE BACKEND ĐỦ - Account Login Flow

### AccountController.java (Hiện tại)
```java
@PostMapping("/login")
public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
    if (accountService.validatePassword(loginRequest.getUsername(), loginRequest.getPassword())) {
        Account account = accountService.findByUsername(loginRequest.getUsername());

        // Kiểm tra nếu lần đầu đăng nhập
        if (account.getFirstLogin() != null && account.getFirstLogin()) {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccountId(account.getId());
            loginResponse.setUsername(account.getUsername());
            loginResponse.setRole(account.getRole());
            loginResponse.setStatus("FIRST_LOGIN");  // ← QUAN TRỌNG
            loginResponse.setMessage("Vui lòng đổi mật khẩu lần đầu đăng nhập");
            return ResponseEntity.ok(loginResponse);
        }

        // Login bình thường
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

@PutMapping("/accounts/{id}/change-password")
public ResponseEntity<?> changePassword(@PathVariable Integer id, @RequestBody ChangePasswordRequest request) {
    Account response = accountService.changePassword(id, request.getOldPassword(), request.getNewPassword());
    return ResponseEntity.ok(response);
}
```

✅ **Backend đã có, không cần sửa!**

---

## 💻 CODE FRONTEND CẦN SỬA

### index.html - Form đăng nhập

**Hiện tại:** form có sẽ gửi POST /login (form submission bình thường)

**Cần sửa:** Gửi JavaScript fetch

```javascript
// Thêm vào index.html:

document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    
    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        
        if (!response.ok) {
            throw new Error('Username hoặc password không đúng');
        }
        
        const data = await response.json();
        
        // QUAN TRỌNG: Lưu thông tin vào localStorage
        localStorage.setItem('accountId', data.accountId);
        localStorage.setItem('username', data.username);
        localStorage.setItem('userRole', data.role);
        
        // Kiểm tra status
        if (data.status === 'FIRST_LOGIN') {
            // Lần đầu → Redirect tới change-password
            alert('🔐 Lần đầu đăng nhập, vui lòng đổi mật khẩu');
            window.location.href = `/change-password?accountId=${data.accountId}`;
        } else if (data.status === 'SUCCESS') {
            // Login thường → Dashboard
            alert('✅ ' + data.message);
            window.location.href = '/dashboard';
        }
        
    } catch (error) {
        alert('❌ Lỗi: ' + error.message);
    }
});
```

---

### change-password.html - Đã OK

**Tình trạng:** ✅ Hoàn thiện 100%

Chỉ cần kiểm tra:
```javascript
// Dòng trong change-password.html
function submitChangePassword(oldPassword, newPassword) {
    const urlParams = new URLSearchParams(window.location.search);
    const accountId = urlParams.get('accountId');  // ← Lấy từ URL

    if (!accountId) {
        showError('oldPasswordError', 'Account ID not found');
        return;
    }

    // Call API
    fetch(`/api/auth/accounts/${accountId}/change-password`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            oldPassword: oldPassword,
            newPassword: newPassword
        })
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(data => {
                throw new Error(data.message || 'Failed to change password');
            });
        }
        return response.json();
    })
    .then(data => {
        showLoading(false);
        showSuccess();
        
        // Cập nhật localStorage (tuỳ chọn)
        localStorage.setItem('username', data.username);
        
        // Redirect to login sau 2 giây
        setTimeout(() => {
            window.location.href = '/';
        }, 2000);
    })
    .catch(error => {
        showLoading(false);
        showError('oldPasswordError', error.message);
    });
}
```

✅ **change-password.html đã OK**

---

### dashboard.html - Check auth

**Cần thêm:** Kiểm tra user đã đăng nhập

```javascript
// Thêm vào dashboard.html (trong <script> phần đầu):

function checkAuthStatus() {
    const username = localStorage.getItem('username');
    const accountId = localStorage.getItem('accountId');
    
    if (!username || !accountId) {
        // Chưa đăng nhập → Redirect về login
        alert('⚠️ Bạn chưa đăng nhập');
        window.location.href = '/';
    }
}

function handleLogout() {
    // Xóa localStorage
    localStorage.removeItem('username');
    localStorage.removeItem('accountId');
    localStorage.removeItem('userRole');
    
    // Redirect
    window.location.href = '/';
}

// Gọi khi trang load
document.addEventListener('DOMContentLoaded', function() {
    checkAuthStatus();  // ← Bắt buộc gọi
});
```

---

## 📋 FLOW ĐẦY ĐỦ - STEP BY STEP

### **BƯỚC 1: Admin tạo tài khoản cho nhân viên**

Admin sử dụng trang `/accounts` (cần thêm tính năng):

```javascript
// Trong accounts.html - Thêm nút "Tạo tài khoản"
async function createNewAccount() {
    const fullName = prompt('Nhập họ tên nhân viên:');
    const username = prompt('Nhập username:');
    
    if (!fullName || !username) return;
    
    try {
        // Admin POST → tạo account
        const response = await fetch('/api/auth/accounts', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                username: username,
                role: 'Quản lý',
                firstLogin: true
            })
        });
        
        if (!response.ok) throw new Error('Failed');
        
        const data = await response.json();
        
        // Hiển thị mật khẩu tạm thời để admin copy
        alert(`✅ Tài khoản tạo thành công!
        
Username: ${data.username}
Mật khẩu tạm: ${data.temporaryPassword}

⚠️ Gửi cho nhân viên`);
        
        loadAccounts();  // Reload list
        
    } catch (error) {
        alert('❌ ' + error.message);
    }
}
```

---

### **BƯỚC 2: Nhân viên đăng nhập lần đầu**

1. Nhân viên truy cập http://localhost:8080/
2. Nhập username + mật khẩu tạm thời
3. Frontend gọi `POST /api/auth/login`
4. Backend trả `status: "FIRST_LOGIN"`
5. Frontend redirect `/change-password?accountId=X`

---

### **BƯỚC 3: Nhân viên đổi mật khẩu**

1. Nhân viên ở trang change-password
2. Nhập: mật khẩu cũ (tạm thời từ admin) + mật khẩu mới
3. Frontend gọi `PUT /api/auth/accounts/{id}/change-password`
4. Backend kiểm tra & cập nhật, set `firstLogin = false`
5. Frontend redirect `/dashboard`

---

### **BƯỚC 4: Nhân viên đăng nhập lần 2**

1. Truy cập http://localhost:8080/
2. Nhập username + mật khẩu mới
3. Frontend gọi `POST /api/auth/login`
4. Backend kiểm tra `firstLogin = false` → trả `status: "SUCCESS"`
5. Frontend lưu localStorage → redirect `/dashboard`

---

## ⚙️ BACKEND CẦN THÊM

### Endpoint tạo tài khoản (Chưa có)

Thêm vào AccountController:

```java
@PostMapping("/accounts")  // Chỉ admin dùng
public ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountRequest request) {
    // Validate
    if (request.getUsername() == null || request.getUsername().isEmpty()) {
        return ResponseEntity.badRequest().body("Username không được để trống");
    }
    
    // Kiểm tra username đã tồn tại?
    if (accountService.findByUsername(request.getUsername()) != null) {
        return ResponseEntity.badRequest().body("Username đã tồn tại");
    }
    
    try {
        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setRole(request.getRole());
        account.setFirstLogin(true);
        
        // Tạo mật khẩu tạm thời
        String tempPassword = accountService.generateSecurePassword();
        account.setPassword(tempPassword);  // Sẽ được hash trong service
        
        Account saved = accountService.createAccount(account);
        
        CreateAccountResponse response = new CreateAccountResponse();
        response.setId(saved.getId());
        response.setUsername(saved.getUsername());
        response.setTemporaryPassword(tempPassword);
        response.setMessage("Tài khoản tạo thành công");
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        return ResponseEntity.status(500).body(e.getMessage());
    }
}
```

---

## 📝 DTO cần tạo

### CreateAccountRequest.java
```java
public class CreateAccountRequest {
    private String username;
    private String role;
    // getters/setters
}
```

### CreateAccountResponse.java
```java
public class CreateAccountResponse {
    private Integer id;
    private String username;
    private String temporaryPassword;
    private String message;
    // getters/setters
}
```

---

## 🎯 CHECKLIST HOÀN THIỆN

### **Frontend**
- [ ] index.html - Sửa form login gọi API
- [ ] dashboard.html - Thêm checkAuthStatus()
- [ ] Tất cả trang - Thêm handleLogout()
- [ ] change-password.html - ✅ Đã OK

### **Backend**
- [ ] Thêm endpoint POST /api/auth/accounts
- [ ] Tạo DTO: CreateAccountRequest, CreateAccountResponse
- [ ] ✅ changePassword endpoint - Đã có
- [ ] ✅ login endpoint - Đã có

---

## 🚀 TEST FLOW ĐẦY ĐỦ

### Kịch bản test:

1. **Admin tạo account**
   - POST /api/auth/accounts
   - Request: { username: "nv01", role: "Quản lý" }
   - Response: { id: 1, temporaryPassword: "A@1b2c3d..." }

2. **Nhân viên login lần 1**
   - POST /api/auth/login
   - Request: { username: "nv01", password: "A@1b2c3d..." }
   - Response: { status: "FIRST_LOGIN", accountId: 1 }
   - Frontend: Redirect /change-password?accountId=1

3. **Nhân viên change password**
   - PUT /api/auth/accounts/1/change-password
   - Request: { oldPassword: "A@1b2c3d...", newPassword: "MyNew@123" }
   - Response: { firstLogin: false }
   - Frontend: Redirect /dashboard

4. **Nhân viên login lần 2**
   - POST /api/auth/login
   - Request: { username: "nv01", password: "MyNew@123" }
   - Response: { status: "SUCCESS" }
   - Frontend: Lưu localStorage + Redirect /dashboard

---

✅ **Done! Flow đầy đủ & rõ ràng** 🎯

