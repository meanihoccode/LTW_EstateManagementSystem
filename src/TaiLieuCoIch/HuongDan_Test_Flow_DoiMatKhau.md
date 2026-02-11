# 🧪 HƯỚNG DẪN TEST FLOW ĐỀI MẬT KHẨU

## ✅ NHỮNG GÌ ĐÃ SỬA

1. ✅ **Entity Account** - Thêm field `firstLogin` để track lần đầu đăng nhập
2. ✅ **AccountService** - Thêm method `changePassword()` để đổi mật khẩu
3. ✅ **AccountController** - Cập nhật login() để check `firstLogin` flag
4. ✅ **DTO** - Tạo `LoginResponse` và `ChangePasswordRequest`
5. ✅ **Endpoint** - Thêm `/api/auth/accounts/{id}/change-password`

---

## 🔍 CÁC API ENDPOINT

### **1. Login API**
```
POST http://localhost:8080/api/auth/login

Body:
{
    "username": "admin",
    "password": "12345678"
}

Response (Lần đầu):
{
    "accountId": 1,
    "username": "admin",
    "role": "Admin",
    "status": "FIRST_LOGIN",
    "message": "Vui lòng đổi mật khẩu lần đầu đăng nhập"
}

Response (Bình thường):
{
    "accountId": 1,
    "username": "admin",
    "role": "Admin",
    "status": "SUCCESS",
    "message": "Đăng nhập thành công"
}
```

### **2. Reset Password (Admin)**
```
PUT http://localhost:8080/api/auth/accounts/1/reset-password

Response:
{
    "accountId": 1,
    "temporaryPassword": "Ab@12456",
    "username": "admin",
    "message": "Mật khẩu tạm thời, vui lòng đổi sau khi đăng nhập."
}
```

### **3. Change Password (Nhân viên)**
```
PUT http://localhost:8080/api/auth/accounts/1/change-password

Body:
{
    "oldPassword": "Ab@12456",
    "newPassword": "MyNewPassword123"
}

Response:
{
    "id": 1,
    "username": "admin",
    "password": "$2a$10$...",  // Hash của mk mới
    "role": "Admin",
    "firstLogin": false
}
```

---

## 📋 FLOW TEST

### **Scenario 1: Reset mật khẩu từ Admin**

1. Admin vào trang quản lí tài khoản
2. Bấm nút "Reset Password" cho nhân viên X
3. Nhân viên nhận được mật khẩu tạm: `Ab@12456`

### **Scenario 2: Nhân viên login lần đầu**

1. Nhân viên vào login page
2. Nhập: username=`abc`, password=`Ab@12456`
3. Server trả về status=`FIRST_LOGIN` + message="Vui lòng đổi mật khẩu lần đầu"
4. Frontend show form đổi mật khẩu (bắt buộc)
5. Nhân viên nhập mật khẩu mới (vd: `MyPassword123`)
6. Call API `/api/auth/accounts/1/change-password` với:
   - oldPassword: `Ab@12456`
   - newPassword: `MyPassword123`

### **Scenario 3: Nhân viên login lần kế tiếp**

1. Nhân viên vào login page
2. Nhập: username=`abc`, password=`MyPassword123`
3. Server trả về status=`SUCCESS` + cho vào dashboard

---

## 🧪 TEST BẰNG CURL/POSTMAN

### **Test 1: Reset Password**
```bash
curl -X PUT http://localhost:8080/api/auth/accounts/1/reset-password
```

### **Test 2: Login Lần Đầu**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin", "password":"Ab@12456"}'
```

Response sẽ có `"status": "FIRST_LOGIN"`

### **Test 3: Đổi Mật Khẩu**
```bash
curl -X PUT http://localhost:8080/api/auth/accounts/1/change-password \
  -H "Content-Type: application/json" \
  -d '{"oldPassword":"Ab@12456", "newPassword":"MyPassword123"}'
```

### **Test 4: Login Bình Thường**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin", "password":"MyPassword123"}'
```

Response sẽ có `"status": "SUCCESS"`

---

## ⚠️ CÁC LỖI CÓ THỂ GẶP

| Lỗi | Nguyên nhân | Cách sửa |
|-----|-----------|---------|
| 404 Not Found | Endpoint sai | Check URL |
| 500 Internal Error | Mật khẩu cũ sai | Kiểm tra oldPassword đúng không |
| Validation failed | Password tạm bị mất | Reset lại password |

---

## ✅ CHECKLIST HOÀN THÀNH

- [ ] Build thành công (BUILD SUCCESSFUL)
- [ ] App chạy trên port 8080
- [ ] Test reset password → nhận mật khẩu tạm
- [ ] Test login lần đầu → nhận status FIRST_LOGIN
- [ ] Test đổi mật khẩu → firstLogin = false
- [ ] Test login lần 2 → nhận status SUCCESS

---

## 🎯 NEXT STEP

Khi toàn bộ test pass, cần:
1. Tạo migration SQL để add column `lan_dau_dang_nhap` vào DB
2. Tạo frontend form đổi mật khẩu khi lần đầu login
3. Tạo trang quản lí tài khoản cho admin (reset password UI)

