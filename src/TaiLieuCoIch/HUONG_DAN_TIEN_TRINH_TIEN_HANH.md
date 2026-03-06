# 🎯 HƯỚNG DẪN CÁC BƯỚC TIẾP THEO - Quản Lý Bất Động Sản

## 📊 TÌNH HÌNH HIỆN TẠI

Dự án của bạn hiện đã có:
- ✅ Database thiết kế hoàn chỉnh (MySQL)
- ✅ Backend API đầy đủ (CRUD cho tất cả entity)
- ✅ Trang đăng nhập với xác thực
- ✅ Dashboard hiển thị thống kê
- ✅ Quản lý tài khoản (admin chỉnh sửa)
- ✅ Frontend cơ bản cho các trang quản lý

---

## 🚀 CÁC BƯỚC TIẾP THEO (Theo thứ tự ưu tiên)

### **BƯỚC 1: Hoàn thiện tính năng CHANGE PASSWORD (Đã có file sẵn)**

**Tệp cần làm:** `change-password.html`

**Yêu cầu:**
- Nhân viên có thể đổi mật khẩu của chính mình
- Yêu cầu nhập mật khẩu cũ → mật khẩu mới → xác nhận mật khẩu mới

**Các bước cần làm:**

1. **Backend - Tạo endpoint trong AccountController:**
   ```
   PUT /api/auth/change-password
   Request body: { oldPassword, newPassword, confirmPassword }
   Response: { success, message }
   ```

2. **Backend - Tạo method trong AccountService:**
   - Lấy ID user hiện tại (từ session/JWT)
   - Kiểm tra mật khẩu cũ có đúng không
   - Validate mật khẩu mới (3-15 ký tự)
   - Hash và lưu mật khẩu mới

3. **Frontend - Code form change-password.html:**
   - Tạo form với 3 input: mật khẩu cũ, mật khẩu mới, xác nhận
   - Thêm validation JavaScript
   - Call API `/api/auth/change-password` khi submit
   - Hiển thị thông báo thành công/lỗi

---

### **BƯỚC 2: Implement JWT Authentication (Bảo mật)**

**Tại sao cần:** Hiện tại bạn dùng Session, JWT sẽ an toàn hơn

**Các bước:**

1. **Thêm dependency trong build.gradle:**
   ```gradle
   implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
   implementation 'io.jsonwebtoken:jjwt-impl:0.12.3'
   implementation 'io.jsonwebtoken:jjwt-jackson:0.12.3'
   ```

2. **Tạo class JwtUtil trong thư mục security:**
   - Hàm tạo token: `generateToken(username, role)`
   - Hàm xác thực token: `validateToken(token)`
   - Hàm lấy username từ token: `getUsername(token)`
   - Hàm lấy role từ token: `getRole(token)`

3. **Tạo JwtFilter để chặn request:**
   - Kiểm tra token trong header Authorization
   - Nếu không hợp lệ → trả 401

4. **Sửa lại login flow:**
   - Thay vì lưu Session → trả token cho frontend
   - Frontend lưu token vào localStorage

---

### **BƯỚC 3: Hoàn thiện Frontend các trang quản lý**

**Thứ tự ưu tiên:**

#### **3a. Trang STAFF (Nhân Viên)**
**Tệp:** `staff.html`
- Danh sách tất cả nhân viên
- Thêm nhân viên mới (form modal)
- Sửa thông tin nhân viên
- Xóa nhân viên

#### **3b. Trang PROPERTIES (Bất Động Sản)**
**Tệp:** `properties.html`
- Danh sách tất cả BĐS
- Thêm BĐS mới
- Sửa thông tin BĐS
- Xóa BĐS
- Filter theo loại/trạng thái

#### **3c. Trang CONTRACTS (Hợp Đồng)**
**Tệp:** `contracts.html`
- Danh sách hợp đồng
- Tạo hợp đồng mới
- Xem chi tiết hợp đồng
- Update trạng thái hợp đồng

#### **3d. Trang PAYMENTS (Thanh Toán)**
**Tệp:** `payments.html`
- Danh sách thanh toán
- Ghi nhận thanh toán mới
- Filter theo trạng thái/kỳ hạn

#### **3e. Trang TENANTS (Khách Thuê)**
**Tệp:** `tenants.html`
- Danh sách khách thuê
- Thêm khách thuê
- Sửa thông tin

#### **3f. Trang OWNERS (Chủ Sở Hữu)**
**Tệp:** `owners.html`
- Danh sách chủ sở hữu
- Quản lý thông tin

---

### **BƯỚC 4: Thêm tính năng tìm kiếm & lọc nâng cao**

- Tìm kiếm theo tên, số điện thoại
- Filter theo ngày (hợp đồng)
- Filter theo trạng thái

---

### **BƯỚC 5: Reports & Statistics**

- Dashboard hiển thị biểu đồ doanh thu
- Báo cáo hợp đồng hết hạn sắp tới
- Báo cáo thanh toán chậm

---

## 📋 HƯỚNG DẪN CHI TIẾT BƯỚC 1: Change Password

### Frontend HTML Template

```html
<!-- Form cơ bản -->
<form id="changePasswordForm">
    <input type="password" id="oldPassword" placeholder="Mật khẩu cũ" required>
    <input type="password" id="newPassword" placeholder="Mật khẩu mới (3-15 ký tự)" required>
    <input type="password" id="confirmPassword" placeholder="Xác nhận mật khẩu" required>
    <button type="submit">Đổi mật khẩu</button>
</form>
```

### Frontend JavaScript

```javascript
// 1. Gắn sự kiện submit
document.getElementById('changePasswordForm').addEventListener('submit', handleChangePassword);

// 2. Hàm xử lý
async function handleChangePassword(e) {
    e.preventDefault();
    
    const oldPassword = document.getElementById('oldPassword').value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    // Validate frontend
    if (newPassword !== confirmPassword) {
        alert('Mật khẩu không khớp!');
        return;
    }
    
    if (newPassword.length < 3 || newPassword.length > 15) {
        alert('Mật khẩu phải từ 3-15 ký tự!');
        return;
    }
    
    // Call API
    try {
        const response = await fetch('/api/auth/change-password', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                oldPassword,
                newPassword,
                confirmPassword
            })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            alert('Đổi mật khẩu thành công!');
            // Redirect hoặc reset form
        } else {
            alert(data.message || 'Lỗi: ' + data.error);
        }
    } catch (error) {
        alert('Lỗi kết nối: ' + error);
    }
}
```

### Backend Controller

```java
@PostMapping("/change-password")
public ResponseEntity<?> changePassword(
    @Valid @RequestBody ChangePasswordRequest request,
    HttpSession session) {
    
    // Lấy user ID từ session
    Integer userId = (Integer) session.getAttribute("userId");
    
    if (userId == null) {
        return ResponseEntity.status(401).body(
            new ErrorResponse("Chưa đăng nhập!")
        );
    }
    
    try {
        boolean success = accountService.changePassword(
            userId,
            request.getOldPassword(),
            request.getNewPassword()
        );
        
        if (success) {
            return ResponseEntity.ok(
                new SuccessResponse("Đổi mật khẩu thành công!")
            );
        } else {
            return ResponseEntity.badRequest().body(
                new ErrorResponse("Mật khẩu cũ không đúng!")
            );
        }
    } catch (Exception e) {
        return ResponseEntity.status(500).body(
            new ErrorResponse(e.getMessage())
        );
    }
}
```

### Backend Service

```java
public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
    Account account = accountRepository.findByUserId(userId)
        .orElseThrow(() -> new RuntimeException("Tài khoản không tìm thấy"));
    
    // Kiểm tra mật khẩu cũ
    if (!passwordEncoder.matches(oldPassword, account.getPassword())) {
        return false;
    }
    
    // Validate mật khẩu mới
    if (newPassword.length() < 3 || newPassword.length() > 15) {
        throw new RuntimeException("Mật khẩu phải từ 3-15 ký tự");
    }
    
    // Hash và lưu mật khẩu mới
    account.setPassword(passwordEncoder.encode(newPassword));
    accountRepository.save(account);
    
    return true;
}
```

---

## 💡 HƯỚNG DẪN CHI TIẾT BƯỚC 2: Các trang CRUD (Ví dụ STAFF)

### 1. Tạo Form Modal để Thêm/Sửa

```html
<!-- Modal Form -->
<div id="staffModal" class="modal">
    <div class="modal-content">
        <span class="close">&times;</span>
        <h2 id="modalTitle">Thêm Nhân Viên</h2>
        
        <form id="staffForm">
            <input type="hidden" id="staffId">
            
            <input type="text" id="fullName" placeholder="Họ tên" required>
            <input type="tel" id="phone" placeholder="Số điện thoại" required>
            <select id="role" required>
                <option value="">Chọn vị trí</option>
                <option value="Quản lý">Quản lý</option>
                <option value="Nhân viên">Nhân viên</option>
            </select>
            
            <button type="submit">Lưu</button>
            <button type="button" class="btn-cancel">Hủy</button>
        </form>
    </div>
</div>
```

### 2. Load và Hiển Thị Dữ Liệu

```javascript
async function loadStaffs() {
    const response = await fetch('/api/staffs');
    const staffs = await response.json();
    
    let html = '';
    staffs.forEach(staff => {
        html += `
            <tr>
                <td>${staff.fullName}</td>
                <td>${staff.phone}</td>
                <td>${staff.role || 'N/A'}</td>
                <td>
                    <button onclick="editStaff(${staff.id})">Sửa</button>
                    <button onclick="deleteStaff(${staff.id})">Xóa</button>
                </td>
            </tr>
        `;
    });
    
    document.getElementById('staffTable').innerHTML = html;
}
```

### 3. Thêm Mới

```javascript
async function handleAddStaff() {
    const data = {
        fullName: document.getElementById('fullName').value,
        phone: document.getElementById('phone').value,
        role: document.getElementById('role').value
    };
    
    const response = await fetch('/api/staffs', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    
    if (response.ok) {
        alert('Thêm thành công!');
        closeModal();
        loadStaffs();
    }
}
```

### 4. Sửa

```javascript
async function editStaff(id) {
    // Load dữ liệu
    const response = await fetch(`/api/staffs/${id}`);
    const staff = await response.json();
    
    // Fill form
    document.getElementById('staffId').value = staff.id;
    document.getElementById('fullName').value = staff.fullName;
    document.getElementById('phone').value = staff.phone;
    document.getElementById('role').value = staff.role;
    
    // Mở modal
    document.getElementById('modalTitle').textContent = 'Sửa Nhân Viên';
    document.getElementById('staffModal').style.display = 'block';
}

async function handleUpdateStaff() {
    const id = document.getElementById('staffId').value;
    const data = {
        fullName: document.getElementById('fullName').value,
        phone: document.getElementById('phone').value,
        role: document.getElementById('role').value
    };
    
    const response = await fetch(`/api/staffs/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    
    if (response.ok) {
        alert('Cập nhật thành công!');
        closeModal();
        loadStaffs();
    }
}
```

### 5. Xóa

```javascript
async function deleteStaff(id) {
    if (!confirm('Bạn chắc chắn muốn xóa?')) return;
    
    const response = await fetch(`/api/staffs/${id}`, {
        method: 'DELETE'
    });
    
    if (response.ok) {
        alert('Xóa thành công!');
        loadStaffs();
    }
}
```

---

## ✅ CHECKLIST LÀM VIỆC

- [ ] Bước 1: Change Password (Backend + Frontend)
- [ ] Bước 2: Staff Management (Thêm/Sửa/Xóa)
- [ ] Bước 2: Properties Management
- [ ] Bước 2: Contracts Management
- [ ] Bước 2: Payments Management
- [ ] Bước 2: Tenants Management
- [ ] Bước 2: Owners Management
- [ ] Bước 3: Tìm kiếm & lọc
- [ ] Bước 4: JWT Authentication
- [ ] Bước 5: Reports & Statistics

---

## 📝 GHI CHÚ QUAN TRỌNG

1. **Mỗi lần code xong một tính năng:**
   - Test bằng Postman (Backend)
   - Test trên Browser (Frontend)
   - Kiểm tra lỗi trong Console

2. **Sử dụng try-catch để xử lý lỗi**

3. **Validate dữ liệu ở Frontend trước khi gửi API**

4. **Hiển thị thông báo lỗi rõ ràng cho user**

5. **Lưu code thường xuyên**

---

Hãy bắt đầu với **Bước 1: Change Password** trước! 🎯

