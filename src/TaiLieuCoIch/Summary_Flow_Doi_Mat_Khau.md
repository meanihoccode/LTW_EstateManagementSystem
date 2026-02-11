# ✅ SUMMARY - NHỮNG GÌ ĐÃ SỬA CHO FLOW ĐỐI MẬT KHẨU

## 🎯 MỤC TIÊU ĐẠT ĐƯỢC

✅ **Admin có thể reset password cho nhân viên**
✅ **Nhân viên bắt buộc phải đổi mật khẩu lần đầu**
✅ **Mật khẩu được hash an toàn (BCrypt)**
✅ **Flow đơn giản, tuân best practice**

---

## 🔧 CÁC THAY ĐỔI ĐÃ LÀM

### **1. Entity Account**
```java
// Thêm field firstLogin
@Column(name = "lan_dau_dang_nhap")
private Boolean firstLogin = true;
```

**File:** `src/main/java/com/example/ltw_quanlybds/entity/Account.java`

---

### **2. DTO Classes**

#### **LoginResponse.java** - Trả về status login
```java
{
    "accountId": 1,
    "username": "admin",
    "role": "Admin",
    "status": "SUCCESS" hoặc "FIRST_LOGIN",
    "message": "..."
}
```

#### **ChangePasswordRequest.java** - Request đổi mật khẩu
```java
{
    "oldPassword": "...",
    "newPassword": "..."
}
```

#### **PasswordResetResponse.java** - Trả về mật khẩu tạm
```java
{
    "accountId": 1,
    "temporaryPassword": "Ab@12456",
    "username": "admin",
    "message": "..."
}
```

**Files:** `src/main/java/com/example/ltw_quanlybds/dto/`

---

### **3. AccountService**

#### **resetPassword()** - Admin reset password
```java
public PasswordResetResponse resetPassword(Integer accountId) {
    Account account = getAccountById(accountId);
    String tempPassword = generateSecurePassword();
    account.setPassword(passwordEncoder.encode(tempPassword));
    account.setFirstLogin(true);  // ← Bắt buộc đổi mk lần đầu
    accountRepository.save(account);
    
    PasswordResetResponse response = new PasswordResetResponse();
    response.setAccountId(accountId);
    response.setTemporaryPassword(tempPassword);  // ← Trả về mk chưa hash
    response.setUsername(account.getUsername());
    response.setMessage("Mật khẩu tạm thời...");
    return response;
}
```

#### **changePassword()** - Nhân viên đổi mật khẩu
```java
public Account changePassword(Integer accountId, String oldPassword, String newPassword) {
    Account account = getAccountById(accountId);
    
    // Kiểm tra mật khẩu cũ
    if (!passwordEncoder.matches(oldPassword, account.getPassword())) {
        throw new RuntimeException("Mật khẩu cũ không đúng");
    }
    
    // Cập nhật mật khẩu mới
    account.setPassword(passwordEncoder.encode(newPassword));
    account.setFirstLogin(false);  // ← Đã đổi xong, cho phép login bình thường
    return accountRepository.save(account);
}
```

#### **generateSecurePassword()** - Tạo password mạnh
```java
// Tạo: "Aa@12345" (8 ký tự, có uppercase, lowercase, digit, special)
```

**File:** `src/main/java/com/example/ltw_quanlybds/service/AccountService.java`

---

### **4. AccountController**

#### **login()** - Check firstLogin flag
```java
@PostMapping("/login")
public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
    if (accountService.validatePassword(loginRequest.getUsername(), loginRequest.getPassword())) {
        Account account = accountService.findByUsername(loginRequest.getUsername());
        
        // Kiểm tra nếu lần đầu đăng nhập
        if (account.getFirstLogin() != null && account.getFirstLogin()) {
            // Trả về FIRST_LOGIN → FE sẽ show form đổi mk
            return ResponseEntity.ok(new LoginResponse(
                account.getId(),
                account.getUsername(),
                account.getRole(),
                "FIRST_LOGIN",
                "Vui lòng đổi mật khẩu lần đầu đăng nhập"
            ));
        }
        
        // Login bình thường
        return ResponseEntity.ok(new LoginResponse(
            account.getId(),
            account.getUsername(),
            account.getRole(),
            "SUCCESS",
            "Đăng nhập thành công"
        ));
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
}
```

#### **changePassword()** - Endpoint đổi mật khẩu
```java
@PutMapping("/accounts/{id}/change-password")
public ResponseEntity<?> changePassword(@PathVariable Integer id, @RequestBody ChangePasswordRequest request) {
    Account response = accountService.changePassword(id, request.getOldPassword(), request.getNewPassword());
    return ResponseEntity.ok(response);
}
```

#### **resetPassword()** - Endpoint admin reset
```java
@PutMapping("/accounts/{id}/reset-password")
public ResponseEntity<?> resetPassword(@PathVariable Integer id) {
    var response = accountService.resetPassword(id);
    return ResponseEntity.ok(response);
}
```

**File:** `src/main/java/com/example/ltw_quanlybds/api/AccountController.java`

---

## 📊 FLOW DIAGRAM

```
Admin Reset Password
        ↓
   Tạo mk tạm: "Ab@12456"
   Set firstLogin = true
   Hash & save
        ↓
   Trả về LoginResponse (mật khẩu chưa hash)
        ↓
Nhân viên Login
        ↓
Check password ✓
Check firstLogin = true?
        ↓ YES
   Trả về: status="FIRST_LOGIN"
   Frontend show form đổi mk
        ↓
Nhân viên Đổi Mật Khẩu
        ↓
Check oldPassword ✓
Hash newPassword & save
Set firstLogin = false
        ↓
Nhân viên Login Lần 2
        ↓
Check password ✓
Check firstLogin = false
        ↓
   Trả về: status="SUCCESS"
   Cho vào dashboard ✓
```

---

## 🗄️ DATABASE MIGRATION

Cần chạy SQL này:

```sql
-- MySQL
ALTER TABLE taikhoan ADD COLUMN lan_dau_dang_nhap BIT DEFAULT 1;

-- SQL Server
ALTER TABLE taikhoan ADD lan_dau_dang_nhap BIT DEFAULT 1;
```

---

## 📋 ENDPOINTS CÓ

| Method | Endpoint | Mô tả |
|--------|----------|--------|
| POST | `/api/auth/login` | Login (check firstLogin) |
| PUT | `/api/auth/accounts/{id}/reset-password` | Admin reset password |
| PUT | `/api/auth/accounts/{id}/change-password` | Nhân viên đổi password |
| GET | `/api/auth/accounts` | Admin xem danh sách tài khoản |
| GET | `/api/auth/accounts/{id}` | Admin xem chi tiết tài khoản |
| GET | `/api/auth/staff/{staffId}` | Admin xem tài khoản của nhân viên |

---

## ✅ KIỂM TRA BUILD

- ✅ Code compile thành công
- ✅ Không có error, chỉ có warning (non-critical)
- ✅ Sẵn sàng test

---

## 🧪 BƯỚC TIẾP THEO

1. **Chạy SQL migration** thêm column `lan_dau_dang_nhap`
2. **Chạy web**: `gradlew bootRun`
3. **Test 4 bước** theo hướng dẫn trong file `Test_Flow_Đăng_Nhập.md`
4. **Tạo frontend form đổi mật khẩu** (khi lần đầu login)
5. **Integrate** với trang dashboard

---

## 🎯 HIỆU QUẢ ĐẠT ĐƯỢC

✅ **An toàn:** Password được hash BCrypt
✅ **Tuân best practice:** Bắt buộc đổi mk lần đầu
✅ **Dễ quản lý:** Admin có thể reset mk bất kỳ lúc nào
✅ **User-friendly:** Nhân viên tự quản lý mk của mình
✅ **Scalable:** Dễ thêm feature như 2FA, password reset via email

