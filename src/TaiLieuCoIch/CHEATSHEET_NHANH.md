# ⚡ CHEATSHEET NHANH - Copy-Paste ngay

## 🎯 5 Phút - Quick Start

### 1️⃣ **Sửa index.html - Login Form**

**Tìm:** trong `<script>` của index.html

**Thay hàm handleLogin cũ bằng:**

```javascript
function handleLogin(event) {
    event.preventDefault();
    
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    
    if (!username || !password) {
        alert('❌ Vui lòng nhập username và password');
        return;
    }
    
    fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    })
    .then(res => {
        if (!res.ok) throw new Error('Login failed');
        return res.json();
    })
    .then(data => {
        // Lưu vào localStorage
        localStorage.setItem('accountId', data.accountId);
        localStorage.setItem('username', data.username);
        localStorage.setItem('userRole', data.role);
        
        // Kiểm tra lần đầu đăng nhập
        if (data.status === 'FIRST_LOGIN') {
            alert('⚠️ Lần đầu đăng nhập, vui lòng đổi mật khẩu');
            window.location.href = `/change-password?accountId=${data.accountId}`;
        } else {
            alert('✅ Đăng nhập thành công');
            window.location.href = '/dashboard';
        }
    })
    .catch(err => {
        alert('❌ Lỗi: ' + err.message);
    });
}
```

---

### 2️⃣ **Sửa dashboard.html - Auth Check**

**Thêm vào phần `<script>` đầu tiên (trước DOMContentLoaded):**

```javascript
function checkAuthStatus() {
    const username = localStorage.getItem('username');
    if (!username) {
        alert('⚠️ Vui lòng đăng nhập');
        window.location.href = '/';
    }
}

function handleLogout() {
    localStorage.removeItem('accountId');
    localStorage.removeItem('username');
    localStorage.removeItem('userRole');
    window.location.href = '/';
}

// Gọi ngay khi load
checkAuthStatus();
```

**Thay link logout bằng:**
```html
<a href="javascript:handleLogout()" class="btn-logout">Đăng xuất</a>
```

---

### 3️⃣ **Sửa staff.html - Liên kết API**

**Thay hàm loadStaff cũ:**

```javascript
async function loadStaff() {
    try {
        const response = await fetch('/api/staffs');
        if (!response.ok) throw new Error('Load failed');
        staffData = await response.json();
        renderTable();
    } catch (error) {
        alert('❌ ' + error.message);
        staffData = [];
        renderTable();
    }
}
```

**Thay hàm saveStaff cũ:**

```javascript
async function saveStaff(e) {
    e.preventDefault();
    
    const editingId = document.getElementById('staffForm').dataset.editingId;
    const formData = {
        fullName: document.getElementById('hoTen').value,
        phone: document.getElementById('soDienThoai').value,
        role: document.getElementById('vaiTro').value
    };
    
    if (!formData.fullName || !formData.phone) {
        alert('❌ Điền đầy đủ thông tin');
        return;
    }
    
    try {
        const url = editingId ? `/api/staffs/${editingId}` : `/api/staffs`;
        const method = editingId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData)
        });
        
        if (!response.ok) throw new Error('Save failed');
        
        alert('✅ ' + (editingId ? 'Cập nhật' : 'Thêm') + ' thành công');
        closeModal();
        loadStaff();
    } catch (error) {
        alert('❌ ' + error.message);
    }
}
```

**Thay hàm deleteStaff cũ:**

```javascript
async function deleteStaff(id) {
    if (!confirm('Xóa không thể hoàn tác?')) return;
    
    try {
        const response = await fetch(`/api/staffs/${id}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Delete failed');
        
        alert('✅ Xóa thành công');
        loadStaff();
    } catch (error) {
        alert('❌ ' + error.message);
    }
}
```

**Thay hàm editStaff cũ:**

```javascript
async function editStaff(id) {
    try {
        const response = await fetch(`/api/staffs/${id}`);
        if (!response.ok) throw new Error('Load failed');
        const staff = await response.json();
        
        document.getElementById('modalTitle').textContent = 'Sửa Nhân Viên';
        document.getElementById('hoTen').value = staff.fullName;
        document.getElementById('soDienThoai').value = staff.phone;
        document.getElementById('vaiTro').value = staff.role;
        document.getElementById('staffForm').dataset.editingId = id;
        document.getElementById('staffModal').style.display = 'block';
    } catch (error) {
        alert('❌ ' + error.message);
    }
}
```

---

### 4️⃣ **Áp dụng Pattern này cho:**

- **properties.html** → API `/api/properties`, fields `tenBDS, loaiBDS, diaChi, dienTich, giaThue, trangThai, chuSoHuuId`
- **contracts.html** → API `/api/contracts`, fields `batDongSanId, khachThueId, ngayBatDau, ngayKetThuc, tienCoc, trangThai`
- **payments.html** → API `/api/payments`, fields `hopDongId, ngayThanhToan, soTien, phuongThuc, trangThai`
- **tenants.html** → API `/api/tenants`, fields `fullName, phone, email, cccd`
- **owners.html** → API `/api/owners`, fields `fullName, phone, email, diaChi`
- **accounts.html** → API `/api/auth/accounts`, chỉ GET + Reset

---

## 📋 Mapping API Endpoint

| Trang | Load | Create | Update | Delete |
|-------|------|--------|--------|--------|
| **staff** | GET /api/staffs | POST /api/staffs | PUT /api/staffs/{id} | DELETE /api/staffs/{id} |
| **properties** | GET /api/properties | POST /api/properties | PUT /api/properties/{id} | DELETE /api/properties/{id} |
| **contracts** | GET /api/contracts | POST /api/contracts | PUT /api/contracts/{id} | DELETE /api/contracts/{id} |
| **payments** | GET /api/payments | POST /api/payments | PUT /api/payments/{id} | DELETE /api/payments/{id} |
| **tenants** | GET /api/tenants | POST /api/tenants | PUT /api/tenants/{id} | DELETE /api/tenants/{id} |
| **owners** | GET /api/owners | POST /api/owners | PUT /api/owners/{id} | DELETE /api/owners/{id} |
| **accounts** | GET /api/auth/accounts | - | PUT /api/auth/accounts/{id}/reset-password | - |
| **login** | - | POST /api/auth/login | - | - |

---

## 🔧 2 Dòng Code thay đổi số lớn

### Trong setupEventListeners():
```javascript
document.getElementById('searchInput').addEventListener('keyup', filterTable);
```

### Ở cuối staff.html thêm hàm filter:
```javascript
function filterTable() {
    const keyword = document.getElementById('searchInput').value.toLowerCase();
    const filtered = staffData.filter(s => 
        s.fullName.toLowerCase().includes(keyword) ||
        s.phone.includes(keyword)
    );
    
    const tbody = document.getElementById('staffTable');
    tbody.innerHTML = filtered.map(s => `
        <tr>
            <td>#${s.id}</td>
            <td>${s.fullName}</td>
            <td>${s.phone}</td>
            <td>${s.role}</td>
            <td>
                <button class="btn-small" onclick="editStaff(${s.id})">Sửa</button>
                <button class="btn-small btn-danger" onclick="deleteStaff(${s.id})">Xóa</button>
            </td>
        </tr>
    `).join('');
}
```

---

## ✅ CHECKLIST 30 phút

- [ ] Sửa index.html - Login
- [ ] Thêm checkAuthStatus() vào dashboard.html
- [ ] Sửa staff.html - Load/Save/Delete
- [ ] Thêm filter vào staff.html
- [ ] Copy pattern sang properties.html
- [ ] Copy pattern sang contracts.html
- [ ] Copy pattern sang payments.html
- [ ] Copy pattern sang tenants.html
- [ ] Copy pattern sang owners.html
- [ ] Sửa accounts.html - Load accounts

---

## 🧪 Test nhanh (Postman)

```bash
# Test login
POST http://localhost:8080/api/auth/login
{
    "username": "root",
    "password": "1234"
}

# Test load staff
GET http://localhost:8080/api/staffs

# Test tạo staff
POST http://localhost:8080/api/staffs
{
    "fullName": "Test Name",
    "phone": "0901234567",
    "role": "Quản lý"
}

# Test sửa staff
PUT http://localhost:8080/api/staffs/1
{
    "fullName": "Updated Name",
    "phone": "0909090909",
    "role": "Nhân viên"
}

# Test xóa staff
DELETE http://localhost:8080/api/staffs/1
```

---

## 💻 Quick Reference - Fetch Template

```javascript
// READ
const response = await fetch('/api/staffs');
const data = await response.json();

// CREATE
await fetch('/api/staffs', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
});

// UPDATE
await fetch(`/api/staffs/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
});

// DELETE
await fetch(`/api/staffs/${id}`, { method: 'DELETE' });
```

---

## 🎯 Order làm việc (Nhanh nhất)

1. **Login** (index.html) - 5 min
2. **Dashboard** (dashboard.html) - 3 min
3. **Staff** (staff.html) - 15 min
4. **Properties** (copy pattern) - 5 min
5. **Contracts** (copy pattern) - 5 min
6. **Payments** (copy pattern) - 5 min
7. **Tenants** (copy pattern) - 5 min
8. **Owners** (copy pattern) - 5 min
9. **Accounts** (accounts.html) - 10 min
10. **Test** - 10 min

**TOTAL: ~1 giờ** ⚡

---

## ⚠️ Lỗi thường gặp & Fix

### Lỗi: Cannot read property 'map' of undefined
```
❌ Nguyên nhân: staffData chưa load từ API

✅ Fix: Đảm bảo gọi loadStaff() khi page load
document.addEventListener('DOMContentLoaded', loadStaff);
```

### Lỗi: 404 Not Found - /api/staffs
```
❌ Nguyên nhân: Backend chưa chạy hoặc endpoint sai

✅ Fix:
1. Chạy gradlew bootRun
2. Check URL có dấu / không
3. Test bằng Postman trước
```

### Lỗi: TypeError: response.json is not a function
```
❌ Nguyên nhân: Response không phải JSON

✅ Fix:
if (!response.ok) {
    console.log('Status:', response.status);
    throw new Error('HTTP ' + response.status);
}
```

### Lỗi: Không logout được
```
❌ Nguyên nhân: handleLogout() chưa gắn

✅ Fix:
<a href="javascript:handleLogout()">Logout</a>

hoặc

<button onclick="handleLogout()">Logout</button>
```

---

## 🚀 Deploy khi nào?

✅ Deploy khi:
- [x] Login hoạt động
- [x] Dashboard auth check OK
- [x] Staff CRUD OK (test Postman)
- [x] Properties CRUD OK
- [x] Contracts CRUD OK
- [x] Payments CRUD OK
- [x] Tenants CRUD OK
- [x] Owners CRUD OK
- [x] Accounts OK
- [x] Change password OK

**= Ready to Production!** 🎉

---

**Ngắn gọn, dễ nhớ, copy-paste ngay!** ⚡

