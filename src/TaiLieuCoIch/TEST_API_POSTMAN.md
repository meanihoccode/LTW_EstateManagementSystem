# 🧪 TEST API BẰNG POSTMAN - Quản Lý Bất Động Sản

> **Base URL:** `http://localhost:8080`  
> **Content-Type:** `application/json`  
> **Ghi chú:** Phải đăng nhập trước để có session cookie. Dùng Postman với setting **"Automatically follow redirects"** = ON và **"Send cookies"** = ON.

---

## ⚠️ QUAN TRỌNG - Cách test đúng thứ tự

```
1. Login trước (POST /login bằng form-data để lấy session)
2. Test Owners (chủ sở hữu) - không phụ thuộc gì
3. Test Staffs (nhân viên) - không phụ thuộc gì
4. Test Tenants (khách thuê) - không phụ thuộc gì
5. Test Properties (BĐS) - cần ownerId và staffId
6. Test Contracts (hợp đồng) - cần propertyId và tenantId
7. Test Payments (thanh toán) - cần contractId
8. Test Accounts (tài khoản) - cần staffId
```

> ⚠️ **Tại sao /api/staffs trả về HTML?**  
> Vì chưa đăng nhập → Spring Security redirect về trang login (`/`).  
> Từ phiên bản mới, khi gọi `/api/**` mà chưa đăng nhập sẽ nhận **401 JSON** thay vì HTML.  
> **Phải đăng nhập trước** bằng bước dưới đây, Postman sẽ tự giữ session cookie.

---

## 🔑 BƯỚC 0 - Đăng nhập để lấy Session (PHẢI LÀM TRƯỚC)

> Project dùng **Session-based login**, không phải JWT.

```
URL: http://localhost:8080/login
Method: POST
Body: x-www-form-urlencoded
```

### ⚙️ Cài đặt Postman TRƯỚC KHI LOGIN (quan trọng!)

> Vào **Postman Settings (⚙️ góc trên phải) > General:**  
> ❌ **"Automatically follow redirects"** = **OFF** (tắt đi!)  
> Lý do: Nếu bật ON, Postman follow redirect → mất cookie. Phải tắt để nhận cookie từ response 302.

### Các bước đăng nhập:

1. Tạo request mới → Method `POST` → URL `http://localhost:8080/login`
2. Tab **Body** → chọn **`x-www-form-urlencoded`** (option thứ 3, KHÔNG phải `form-data`)
3. Thêm 2 key-value:

| Key | Value |
|-----|-------|
| `username` | `nv03` |
| `password` | `Nghia@1212` |

4. Bấm **Send**
5. Response trả về **`302 Found`** + header `Set-Cookie: JSESSIONID=...` → **thành công!**
6. Postman tự lưu cookie `JSESSIONID` vào Cookie Manager
7. Kiểm tra: click **"Cookies"** (góc trên phải cạnh Send) → thấy `JSESSIONID` cho `localhost`

### Sau khi login → test API:

- Tất cả request tiếp theo **tự động gửi kèm cookie** → nhận JSON bình thường
- Nếu vẫn nhận HTML → click **"Cookies"** kiểm tra xem có `JSESSIONID` chưa, nếu chưa có thì login lại

### Tại sao `form-data` bị 500 còn `x-www-form-urlencoded` thì được?

| Lựa chọn Body | Content-Type thực tế | Kết quả |
|--------------|---------------------|---------|
| `form-data` | `multipart/form-data` | ❌ Spring Security không đọc được → 500 |
| `x-www-form-urlencoded` | `application/x-www-form-urlencoded` | ✅ 302 success → có cookie |
| `raw > JSON` | `application/json` | ✅ Chỉ dùng cho `/api/auth/login` — KHÔNG tạo session |

### POST /api/auth/login

```
URL: http://localhost:8080/api/auth/login
Method: POST
Headers: Content-Type: application/json
```

**Body (đăng nhập Admin):**
```json
{
    "username": "admin",
    "password": "123456"
}
```

**Body (đăng nhập lần đầu - sẽ trả về FIRST_LOGIN):**
```json
{
    "username": "nhanvien1",
    "password": "123456"
}
```

**Response thành công (LOGIN bình thường):**
```json
{
    "accountId": 1,
    "username": "admin",
    "role": "Admin",
    "status": "SUCCESS",
    "message": "Đăng nhập thành công"
}
```

**Response lần đầu đăng nhập:**
```json
{
    "accountId": 2,
    "username": "nhanvien1",
    "role": "Nhân viên",
    "status": "FIRST_LOGIN",
    "message": "Vui lòng đổi mật khẩu lần đầu đăng nhập"
}
```

---

### PUT /api/auth/accounts/{id}/change-password - Đổi mật khẩu

```
URL: http://localhost:8080/api/auth/accounts/2/change-password
Method: PUT
Headers: Content-Type: application/json
```

**Body:**
```json
{
    "oldPassword": "123456",
    "newPassword": "newpass123"
}
```

---

### PUT /api/auth/accounts/{id}/reset-password - Admin reset mật khẩu

```
URL: http://localhost:8080/api/auth/accounts/2/reset-password
Method: PUT
```

*(Không cần body - sẽ reset về mật khẩu mặc định)*

---

### GET /api/auth/accounts - Lấy tất cả tài khoản (Admin)

```
URL: http://localhost:8080/api/auth/accounts
Method: GET
```

---

### GET /api/auth/accounts/{id} - Lấy tài khoản theo ID

```
URL: http://localhost:8080/api/auth/accounts/1
Method: GET
```

---

### GET /api/auth/staff/{staffId} - Lấy tài khoản theo staffId

```
URL: http://localhost:8080/api/auth/staff/1
Method: GET
```

---

### DELETE /api/auth/accounts/{id} - Xóa tài khoản (Admin)

```
URL: http://localhost:8080/api/auth/accounts/2
Method: DELETE
```

---

## 2. 👤 OWNERS - Chủ sở hữu

### GET /api/owners - Lấy tất cả chủ sở hữu

```
URL: http://localhost:8080/api/owners
Method: GET
```

**Response:**
```json
[
    {
        "id": 1,
        "fullName": "Nguyễn Văn A",
        "phone": "0901234567",
        "email": "nguyenvana@email.com",
        "address": "123 Nguyễn Huệ, Q1, TP.HCM"
    }
]
```

---

### GET /api/owners/{id} - Lấy chủ sở hữu theo ID

```
URL: http://localhost:8080/api/owners/1
Method: GET
```

---

### POST /api/owners - Thêm chủ sở hữu mới

```
URL: http://localhost:8080/api/owners
Method: POST
Headers: Content-Type: application/json
```

**Body:**
```json
{
    "fullName": "Trần Thị Bích",
    "phone": "0912345678",
    "email": "tranthib@email.com",
    "address": "456 Lê Lợi, Q3, TP.HCM"
}
```

---

### PUT /api/owners/{id} - Cập nhật chủ sở hữu

```
URL: http://localhost:8080/api/owners/1
Method: PUT
Headers: Content-Type: application/json
```

**Body:**
```json
{
    "fullName": "Trần Thị Bích Ngọc",
    "phone": "0912345679",
    "email": "bichngoc@email.com",
    "address": "789 Đinh Tiên Hoàng, Q1, TP.HCM"
}
```

---

### DELETE /api/owners/{id} - Xóa chủ sở hữu

```
URL: http://localhost:8080/api/owners/1
Method: DELETE
```

*(Response: 204 No Content)*

---

## 3. 👨‍💼 STAFFS - Nhân viên

### GET /api/staffs - Lấy tất cả nhân viên

```
URL: http://localhost:8080/api/staffs
Method: GET
```

**Response:**
```json
[
    {
        "id": 1,
        "fullName": "Nguyễn Văn Hùng",
        "phone": "0909000001",
        "role": "Quản lý",
        "account": {
            "id": 1,
            "username": "hungnv",
            "role": "Quản lý",
            "firstLogin": false
        }
    }
]
```

---

### GET /api/staffs/{id} - Lấy nhân viên theo ID

```
URL: http://localhost:8080/api/staffs/1
Method: GET
```

---

### POST /api/staffs - Thêm nhân viên mới (tự động tạo tài khoản)

```
URL: http://localhost:8080/api/staffs
Method: POST
Headers: Content-Type: application/json
```

**Body:**
```json
{
    "fullName": "Lê Minh Tuấn",
    "phone": "0933456789",
    "role": "Nhân viên kinh doanh"
}
```

> 💡 **role có thể là:** `"Quản lý"`, `"Nhân viên kinh doanh"`, `"Nhân viên kỹ thuật"`, `"Admin"`  
> 💡 **Khi tạo nhân viên, hệ thống tự tạo account** với username từ tên + ID, mật khẩu mặc định

**Response:**
```json
{
    "id": 5,
    "fullName": "Lê Minh Tuấn",
    "phone": "0933456789",
    "role": "Nhân viên kinh doanh",
    "username": "leminhtu5",
    "plainPassword": "abc12345"
}
```

---

### PUT /api/staffs/{id} - Cập nhật nhân viên

```
URL: http://localhost:8080/api/staffs/1
Method: PUT
Headers: Content-Type: application/json
```

**Body:**
```json
{
    "fullName": "Nguyễn Văn Hùng",
    "phone": "0909000099",
    "role": "Quản lý"
}
```

---

### DELETE /api/staffs/{id} - Xóa nhân viên

```
URL: http://localhost:8080/api/staffs/1
Method: DELETE
```

---

## 4. 🏠 TENANTS - Khách thuê

### GET /api/tenants - Lấy tất cả khách thuê

```
URL: http://localhost:8080/api/tenants
Method: GET
```

**Response:**
```json
[
    {
        "id": 1,
        "fullName": "Phạm Văn Long",
        "idNumber": "079200001234",
        "phone": "0977123456",
        "email": "phamlong@email.com"
    }
]
```

---

### GET /api/tenants/{id} - Lấy khách thuê theo ID

```
URL: http://localhost:8080/api/tenants/1
Method: GET
```

---

### POST /api/tenants - Thêm khách thuê mới

```
URL: http://localhost:8080/api/tenants
Method: POST
Headers: Content-Type: application/json
```

**Body:**
```json
{
    "fullName": "Vũ Thị Lan",
    "idNumber": "036199056789",
    "phone": "0966789012",
    "email": "vuthilan@email.com"
}
```

> ⚠️ **Validation:**
> - `fullName`: bắt buộc, 3-100 ký tự
> - `idNumber`: bắt buộc, 9-12 ký tự (CCCD 12 số hoặc CMND 9 số)
> - `phone`: bắt buộc, 10-11 chữ số
> - `email`: bắt buộc, đúng định dạng email

---

### PUT /api/tenants/{id} - Cập nhật khách thuê

```
URL: http://localhost:8080/api/tenants/1
Method: PUT
Headers: Content-Type: application/json
```

**Body:**
```json
{
    "fullName": "Vũ Thị Lan Anh",
    "idNumber": "036199056789",
    "phone": "0966789013",
    "email": "vuthilananh@email.com"
}
```

---

### DELETE /api/tenants/{id} - Xóa khách thuê

```
URL: http://localhost:8080/api/tenants/1
Method: DELETE
```

---

## 5. 🏢 PROPERTIES - Bất động sản

### GET /api/properties - Lấy tất cả BĐS

```
URL: http://localhost:8080/api/properties
Method: GET
```

**Response:**
```json
[
    {
        "id": 1,
        "name": "Căn hộ Vinhomes Central Park",
        "type": "Căn hộ",
        "address": "720A Điện Biên Phủ, Bình Thạnh",
        "area": 65.5,
        "rentalPrice": 12000000.00,
        "status": "Đang thuê",
        "owner": { "id": 1, "fullName": "Nguyễn Văn A" },
        "staff": { "id": 1, "fullName": "Nguyễn Văn Hùng" }
    }
]
```

---

### GET /api/properties/{id} - Lấy BĐS theo ID

```
URL: http://localhost:8080/api/properties/1
Method: GET
```

---

### GET /api/properties/total - Tổng số BĐS

```
URL: http://localhost:8080/api/properties/total
Method: GET
```

---

### GET /api/properties/emptyProperties - Số BĐS đang trống

```
URL: http://localhost:8080/api/properties/emptyProperties
Method: GET
```

---

### GET /api/properties/status/{status} - Lọc BĐS theo trạng thái

```
URL: http://localhost:8080/api/properties/status/Đang%20thuê
Method: GET
```

> **status có thể là:** `Đang thuê`, `Còn trống`, `Bảo trì`

---

### POST /api/properties - Thêm BĐS mới

```
URL: http://localhost:8080/api/properties
Method: POST
Headers: Content-Type: application/json
```

**Body:**
```json
{
    "name": "Nhà phố Thảo Điền",
    "type": "Nhà phố",
    "address": "15 Thảo Điền, Q2, TP.HCM",
    "area": 120.0,
    "rentalPrice": 25000000,
    "status": "Còn trống",
    "ownerId": 1,
    "staffId": 1
}
```

> ⚠️ **Validation:**
> - `name`: bắt buộc, 3-100 ký tự
> - `type`: bắt buộc
> - `address`: bắt buộc
> - `area`: bắt buộc, > 0
> - `rentalPrice`: bắt buộc, >= 0
> - `ownerId`: ID của chủ sở hữu (phải tồn tại trong DB)
> - `staffId`: ID của nhân viên quản lý (phải tồn tại trong DB)

---

### PUT /api/properties/{id} - Cập nhật BĐS

```
URL: http://localhost:8080/api/properties/1
Method: PUT
Headers: Content-Type: application/json
```

**Body:**
```json
{
    "name": "Nhà phố Thảo Điền - Updated",
    "type": "Nhà phố",
    "address": "15 Thảo Điền, Q2, TP.HCM",
    "area": 125.0,
    "rentalPrice": 27000000,
    "status": "Đang thuê",
    "ownerId": 1,
    "staffId": 2
}
```

---

### DELETE /api/properties/{id} - Xóa BĐS

```
URL: http://localhost:8080/api/properties/1
Method: DELETE
```

---

## 6. 📄 CONTRACTS - Hợp đồng

### GET /api/contracts - Lấy tất cả hợp đồng

```
URL: http://localhost:8080/api/contracts
Method: GET
```

**Response:**
```json
[
    {
        "id": 1,
        "property": { "id": 1, "name": "Căn hộ Vinhomes Central Park" },
        "tenant": { "id": 1, "fullName": "Phạm Văn Long" },
        "startDate": "2025-01-01",
        "endDate": "2026-01-01",
        "deposit": 24000000.00,
        "status": "Đang hiệu lực"
    }
]
```

---

### GET /api/contracts/{id} - Lấy hợp đồng theo ID

```
URL: http://localhost:8080/api/contracts/1
Method: GET
```

---

### GET /api/contracts/totalActiveContracts - Tổng hợp đồng đang hiệu lực

```
URL: http://localhost:8080/api/contracts/totalActiveContracts
Method: GET
```

---

### GET /api/contracts/revenueThisMonth - Doanh thu tháng này

```
URL: http://localhost:8080/api/contracts/revenueThisMonth
Method: GET
```

---

### GET /api/contracts/revenueByMonth - Doanh thu theo tháng (biểu đồ)

```
URL: http://localhost:8080/api/contracts/revenueByMonth
Method: GET
```

---

### GET /api/contracts/expiringContracts - Hợp đồng sắp hết hạn

```
URL: http://localhost:8080/api/contracts/expiringContracts
Method: GET
```

---

### POST /api/contracts - Thêm hợp đồng mới

```
URL: http://localhost:8080/api/contracts
Method: POST
Headers: Content-Type: application/json
```

**Body:**
```json
{
    "propertyId": 1,
    "tenantId": 1,
    "startDate": "2026-03-01",
    "endDate": "2027-03-01",
    "deposit": 20000000,
    "status": "Đang hiệu lực"
}
```

> ⚠️ **Validation:**
> - `propertyId`: ID BĐS (phải tồn tại)
> - `tenantId`: ID khách thuê (phải tồn tại)
> - `startDate`, `endDate`: bắt buộc, định dạng `"YYYY-MM-DD"`
> - `deposit`: bắt buộc, >= 0
> - `status`: `"Đang hiệu lực"`, `"Hết hạn"`, `"Đã hủy"`

---

### PUT /api/contracts/{id} - Cập nhật hợp đồng

```
URL: http://localhost:8080/api/contracts/1
Method: PUT
Headers: Content-Type: application/json
```

**Body:**
```json
{
    "propertyId": 1,
    "tenantId": 1,
    "startDate": "2026-03-01",
    "endDate": "2027-06-01",
    "deposit": 22000000,
    "status": "Đang hiệu lực"
}
```

---

### DELETE /api/contracts/{id} - Xóa hợp đồng

```
URL: http://localhost:8080/api/contracts/1
Method: DELETE
```

---

## 7. 💰 PAYMENTS - Thanh toán

### GET /api/payments - Lấy tất cả thanh toán

```
URL: http://localhost:8080/api/payments
Method: GET
```

**Response:**
```json
[
    {
        "id": 1,
        "contract": { "id": 1 },
        "paymentDate": "2026-03-05",
        "amount": 12000000.00,
        "method": "Chuyển khoản",
        "status": "Đã thanh toán"
    }
]
```

---

### GET /api/payments/{id} - Lấy thanh toán theo ID

```
URL: http://localhost:8080/api/payments/1
Method: GET
```

---

### GET /api/payments/revenueThisMonth - Doanh thu tháng này

```
URL: http://localhost:8080/api/payments/revenueThisMonth
Method: GET
```

---

### GET /api/payments/recent - Thanh toán gần đây

```
URL: http://localhost:8080/api/payments/recent
Method: GET
```

---

### POST /api/payments - Thêm thanh toán mới

```
URL: http://localhost:8080/api/payments
Method: POST
Headers: Content-Type: application/json
```

**Body:**
```json
{
    "contractId": 1,
    "paymentDate": "2026-03-12",
    "amount": 12000000,
    "method": "Chuyển khoản",
    "status": "Đã thanh toán"
}
```

> ⚠️ **Validation:**
> - `contractId`: ID hợp đồng (phải tồn tại)
> - `paymentDate`: bắt buộc, định dạng `"YYYY-MM-DD"`
> - `amount`: bắt buộc, > 0
> - `method`: `"Chuyển khoản"`, `"Tiền mặt"`, `"Thẻ ngân hàng"`
> - `status`: `"Đã thanh toán"`, `"Chờ thanh toán"`, `"Quá hạn"`

---

### PUT /api/payments/{id} - Cập nhật thanh toán

```
URL: http://localhost:8080/api/payments/1
Method: PUT
Headers: Content-Type: application/json
```

**Body:**
```json
{
    "contractId": 1,
    "paymentDate": "2026-03-15",
    "amount": 12500000,
    "method": "Tiền mặt",
    "status": "Đã thanh toán"
}
```

---

### DELETE /api/payments/{id} - Xóa thanh toán

```
URL: http://localhost:8080/api/payments/1
Method: DELETE
```

---

## 📋 Bảng tóm tắt nhanh

| Module | GET All | GET by ID | POST | PUT | DELETE |
|--------|---------|-----------|------|-----|--------|
| Auth | `/api/auth/accounts` | `/api/auth/accounts/{id}` | `/api/auth/login` | `/api/auth/accounts/{id}/reset-password` | `/api/auth/accounts/{id}` |
| Owners | `/api/owners` | `/api/owners/{id}` | `/api/owners` | `/api/owners/{id}` | `/api/owners/{id}` |
| Staffs | `/api/staffs` | `/api/staffs/{id}` | `/api/staffs` | `/api/staffs/{id}` | `/api/staffs/{id}` |
| Tenants | `/api/tenants` | `/api/tenants/{id}` | `/api/tenants` | `/api/tenants/{id}` | `/api/tenants/{id}` |
| Properties | `/api/properties` | `/api/properties/{id}` | `/api/properties` | `/api/properties/{id}` | `/api/properties/{id}` |
| Contracts | `/api/contracts` | `/api/contracts/{id}` | `/api/contracts` | `/api/contracts/{id}` | `/api/contracts/{id}` |
| Payments | `/api/payments` | `/api/payments/{id}` | `/api/payments` | `/api/payments/{id}` | `/api/payments/{id}` |

---

## 🔥 HTTP Status Code hay gặp

| Code | Ý nghĩa |
|------|---------|
| `200 OK` | Thành công (GET, PUT) |
| `201 Created` | Tạo mới thành công (POST) |
| `204 No Content` | Xóa thành công (DELETE) |
| `400 Bad Request` | Dữ liệu không hợp lệ (validation fail) |
| `401 Unauthorized` | Chưa đăng nhập hoặc sai mật khẩu |
| `403 Forbidden` | Không có quyền truy cập |
| `404 Not Found` | Không tìm thấy resource |
| `500 Internal Server Error` | Lỗi server (kiểm tra console log) |

---

## 💡 Tips khi test Postman

1. **Tạo Collection** mới tên "BDS API" để quản lý dễ hơn
2. **Tạo Variable** `baseUrl = http://localhost:8080` rồi dùng `{{baseUrl}}/api/...`
3. **Đăng nhập trước** rồi Postman sẽ tự giữ session cookie
4. Khi test **POST/PUT**, luôn chọn `Body > raw > JSON`
5. Khi gặp **403**, kiểm tra lại xem đã đăng nhập đúng role chưa
6. Khi gặp **500**, mở console Spring Boot để xem lỗi chi tiết

