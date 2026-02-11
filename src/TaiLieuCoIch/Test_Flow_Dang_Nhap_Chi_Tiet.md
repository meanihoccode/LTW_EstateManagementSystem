# 🧪 HƯỚNG DẪN TEST FLOW ĐĂNG NHẬP CHI TIẾT

## 📌 FLOW CÓ 2 TRƯỜNG HỢP

---

## **FLOW 1: Lần Đầu Đăng Nhập (Sau Reset Password)**

### **Bước 1: Admin Reset Password cho Nhân Viên**

Dùng **Postman** hoặc **curl**, gửi:

```
PUT http://localhost:8080/api/auth/accounts/1/reset-password
```

**Response:**
```json
{
    "accountId": 1,
    "temporaryPassword": "Ab@12456",
    "username": "admin",
    "message": "Mật khẩu tạm thời, vui lòng đổi sau khi đăng nhập."
}
```

✅ **Lưu** mật khẩu tạm: `Ab@12456`

---

### **Bước 2: Nhân Viên Login Bằng Mật Khẩu Tạm**

Gửi request:

```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
    "username": "admin",
    "password": "Ab@12456"
}
```

**Response (LƯU Ý: status = FIRST_LOGIN):**
```json
{
    "accountId": 1,
    "username": "admin",
    "role": "Admin",
    "status": "FIRST_LOGIN",
    "message": "Vui lòng đổi mật khẩu lần đầu đăng nhập"
}
```

⚠️ **Status = "FIRST_LOGIN"** = Bắt buộc phải đổi mật khẩu!

---

### **Bước 3: Nhân Viên Đổi Mật Khẩu**

Gửi request:

```
PUT http://localhost:8080/api/auth/accounts/1/change-password
Content-Type: application/json

{
    "oldPassword": "Ab@12456",
    "newPassword": "MyPassword123"
}
```

**Response:**
```json
{
    "id": 1,
    "username": "admin",
    "password": "$2a$10$N9qo8uLOickgx2ZMRZoMyText...",
    "role": "Admin",
    "firstLogin": false
}
```

✅ **firstLogin = false** = Đã đổi xong!

---

### **Bước 4: Nhân Viên Login Lần 2 (Bình Thường)**

Gửi request:

```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
    "username": "admin",
    "password": "MyPassword123"
}
```

**Response (Status = SUCCESS):**
```json
{
    "accountId": 1,
    "username": "admin",
    "role": "Admin",
    "status": "SUCCESS",
    "message": "Đăng nhập thành công"
}
```

✅ **Status = "SUCCESS"** = Có thể vào dashboard!

---

## **FLOW 2: Login Bình Thường (firstLogin = false)**

Nếu `firstLogin` đã = false, chỉ cần:

```
POST http://localhost:8080/api/auth/login

{
    "username": "admin",
    "password": "MyPassword123"
}
```

→ Trả về `"status": "SUCCESS"` ngay

---

## 🔧 TEST BẰNG CURL (DỄ NHẤT)

### **Step 1: Reset Password**
```bash
curl -X PUT http://localhost:8080/api/auth/accounts/1/reset-password
```
Copy mật khẩu tạm từ kết quả (vd: `Ab@12456`)

### **Step 2: Login Lần Đầu**
```bash
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"Ab@12456\"}"
```
Kiểm tra có `"status":"FIRST_LOGIN"` không

### **Step 3: Đổi Mật Khẩu**
```bash
curl -X PUT http://localhost:8080/api/auth/accounts/1/change-password ^
  -H "Content-Type: application/json" ^
  -d "{\"oldPassword\":\"Ab@12456\",\"newPassword\":\"MyPassword123\"}"
```
Kiểm tra có `"firstLogin":false` không

### **Step 4: Login Bình Thường**
```bash
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"MyPassword123\"}"
```
Kiểm tra có `"status":"SUCCESS"` không

---

## 📱 TEST BẰNG POSTMAN

1. **Tạo 4 request** như trên
2. **Run từng request lần lượt**
3. **Copy response** để verify

Hoặc dùng **Postman Collection** để tự động run theo thứ tự

---

## ⚠️ NHỮNG LỖI CÓ THỂ GẶP

| Lỗi | Nguyên nhân | Cách sửa |
|-----|-----------|---------|
| **500 Internal Error** | oldPassword sai | Dùng đúng mật khẩu tạm |
| **404 Not Found** | Endpoint sai | Check URL chính xác |
| **401 Unauthorized** | Username/password sai | Kiểm tra tài khoản tồn tại |
| **Column không tồn tại** | Chưa add column `lan_dau_dang_nhap` | Chạy SQL: `ALTER TABLE taikhoan ADD lan_dau_dang_nhap BIT DEFAULT 1;` |

---

## ✅ CHECKLIST TEST THÀNH CÔNG

- [ ] Bước 1: Reset password → nhận mật khẩu tạm
- [ ] Bước 2: Login lần đầu → status = "FIRST_LOGIN"
- [ ] Bước 3: Đổi mật khẩu → firstLogin = false
- [ ] Bước 4: Login bình thường → status = "SUCCESS"

**Nếu pass hết 4 bước = THÀNH CÔNG! ✅**

---

## 💡 LƯU Ý QUAN TRỌNG

1. **accountId** ở endpoint phải đúng với tài khoản bạn muốn test
2. **Mật khẩu tạm** chỉ dùng 1 lần, sau reset lại sẽ khác
3. **firstLogin = true** bắt buộc phải đổi mật khẩu
4. **Password luôn được hash** trong DB, không thể xem trực tiếp
5. **Mỗi account chỉ reset 1 lần** (hoặc reset lại sẽ tạo mật khẩu tạm mới)

