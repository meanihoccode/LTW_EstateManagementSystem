# 🔧 TEMPLATE CODE - Liên kết API cho Frontend

## 📌 PATTERN CHUẨN (Copy-Paste cho tất cả trang)

Tất cả các trang CRUD (staff, properties, contracts, payments, tenants, owners) đều theo pattern này:

---

## 1️⃣ STAFF.HTML - Ví dụ hoàn chỉnh

### **A. Load Data khi trang tải**

```javascript
// Thay đoạn này trong staff.html:
let staffData = [];

document.addEventListener('DOMContentLoaded', function() {
    loadStaff();              // ← SỬA: Load từ API
    setupEventListeners();
});

// HÀM LOAD TỪ API
async function loadStaff() {
    try {
        const response = await fetch('/api/staffs');
        
        if (!response.ok) {
            throw new Error('Failed to load staff');
        }
        
        staffData = await response.json();
        renderTable();
    } catch (error) {
        console.error('Error:', error);
        alert('❌ Lỗi tải dữ liệu: ' + error.message);
        staffData = [];  // Set empty array
        renderTable();
    }
}

// REST CỦA CODE KHÔNG THAY ĐỔI:
function renderTable() {
    const tbody = document.getElementById('staffTable');
    if (staffData.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: #999;">Không có dữ liệu</td></tr>';
        return;
    }
    // ... rest of rendering code
}
```

---

### **B. Thêm mới (POST)**

```javascript
// Thay hàm saveStaff cũ:
async function saveStaff(e) {
    e.preventDefault();
    
    const editingId = document.getElementById('staffForm').dataset.editingId;
    
    // Lấy dữ liệu từ form
    const staffData_form = {
        fullName: document.getElementById('hoTen').value,
        phone: document.getElementById('soDienThoai').value,
        role: document.getElementById('vaiTro').value
    };
    
    // Validate
    if (!staffData_form.fullName || !staffData_form.phone) {
        alert('❌ Vui lòng điền đầy đủ thông tin');
        return;
    }
    
    try {
        const url = editingId 
            ? `/api/staffs/${editingId}`  // UPDATE
            : `/api/staffs`;              // CREATE
            
        const method = editingId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(staffData_form)
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Failed to save');
        }
        
        // Thành công
        alert('✅ ' + (editingId ? 'Cập nhật' : 'Thêm') + ' thành công!');
        closeModal();
        loadStaff();  // ← Reload lại list
        
    } catch (error) {
        console.error('Error:', error);
        alert('❌ Lỗi: ' + error.message);
    }
}
```

---

### **C. Sửa (PUT)**

```javascript
// Hàm editStaff giữ nguyên, chỉ thêm try-catch:
async function editStaff(id) {
    try {
        const response = await fetch(`/api/staffs/${id}`);
        
        if (!response.ok) {
            throw new Error('Failed to load staff details');
        }
        
        const staff = await response.json();
        
        // Fill form
        document.getElementById('modalTitle').textContent = 'Sửa Nhân Viên';
        document.getElementById('hoTen').value = staff.fullName;
        document.getElementById('soDienThoai').value = staff.phone;
        document.getElementById('vaiTro').value = staff.role;
        document.getElementById('staffForm').dataset.editingId = id;
        
        document.getElementById('staffModal').style.display = 'block';
        
    } catch (error) {
        console.error('Error:', error);
        alert('❌ Lỗi: ' + error.message);
    }
}
```

---

### **D. Xóa (DELETE)**

```javascript
// Thay hàm deleteStaff cũ:
async function deleteStaff(id) {
    if (!confirm('Bạn có chắc muốn xóa?')) return;
    
    try {
        const response = await fetch(`/api/staffs/${id}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            throw new Error('Failed to delete');
        }
        
        alert('✅ Xóa thành công!');
        loadStaff();  // Reload list
        
    } catch (error) {
        console.error('Error:', error);
        alert('❌ Lỗi: ' + error.message);
    }
}
```

---

### **E. Search/Filter**

```javascript
// Thay hàm filterTable cũ:
function filterTable() {
    const keyword = document.getElementById('searchInput').value.toLowerCase();
    
    const filtered = staffData.filter(staff => {
        return staff.fullName.toLowerCase().includes(keyword) ||
               staff.phone.includes(keyword) ||
               staff.role.toLowerCase().includes(keyword);
    });
    
    // Render filtered data
    const tbody = document.getElementById('staffTable');
    if (filtered.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">Không tìm thấy</td></tr>';
        return;
    }
    
    tbody.innerHTML = filtered.map(s => `
        <tr>
            <td>#${s.id}</td>
            <td>${s.fullName}</td>
            <td>${s.phone}</td>
            <td>${s.role || 'N/A'}</td>
            <td>
                <button class="btn-small" onclick="editStaff(${s.id})">Sửa</button>
                <button class="btn-small btn-danger" onclick="deleteStaff(${s.id})">Xóa</button>
            </td>
        </tr>
    `).join('');
}

// Gắn event listener:
document.getElementById('searchInput').addEventListener('keyup', filterTable);
```

---

## 2️⃣ PROPERTIES.HTML - Áp dụng tương tự

**Thay đổi chính:**
- API: `/api/properties` (thay vì `/api/staffs`)
- Fields: `tenBDS`, `loaiBDS`, `diaChi`, `dienTich`, `giaThue`, `trangThai`, `chuSoHuu`

**Code template:**
```javascript
// Copy từ staff.html nhưng đổi:
async function loadProperties() {
    try {
        const response = await fetch('/api/properties');
        if (!response.ok) throw new Error('Failed to load');
        propertiesData = await response.json();
        renderTable();
    } catch (error) {
        alert('❌ ' + error.message);
        propertiesData = [];
        renderTable();
    }
}

async function saveProperty(e) {
    e.preventDefault();
    const propertyData = {
        tenBDS: document.getElementById('tenBDS').value,
        loaiBDS: document.getElementById('loaiBDS').value,
        diaChi: document.getElementById('diaChi').value,
        dienTich: parseFloat(document.getElementById('dienTich').value),
        giaThue: parseFloat(document.getElementById('giaThue').value),
        trangThai: document.getElementById('trangThai').value,
        chuSoHuuId: parseInt(document.getElementById('chuSoHuu').value)
    };
    
    // Validate
    if (!propertyData.tenBDS || !propertyData.diaChi) {
        alert('❌ Vui lòng điền đầy đủ thông tin');
        return;
    }
    
    try {
        const editingId = document.getElementById('propertyForm').dataset.editingId;
        const url = editingId ? `/api/properties/${editingId}` : `/api/properties`;
        const method = editingId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(propertyData)
        });
        
        if (!response.ok) throw new Error('Failed to save');
        
        alert('✅ Lưu thành công!');
        closeModal();
        loadProperties();
    } catch (error) {
        alert('❌ ' + error.message);
    }
}
```

---

## 3️⃣ CONTRACTS.HTML - Pattern tương tự

```javascript
// API: /api/contracts
// Fields: batDongSanId, khachThueId, ngayBatDau, ngayKetThuc, tienCoc, trangThai

async function loadContracts() {
    try {
        const response = await fetch('/api/contracts');
        contractsData = await response.json();
        renderTable();
    } catch (error) {
        alert('❌ ' + error.message);
        contractsData = [];
        renderTable();
    }
}
```

---

## 4️⃣ PAYMENTS.HTML - Pattern tương tự

```javascript
// API: /api/payments
// Fields: hopDongId, ngayThanhToan, soTien, phuongThuc, trangThai

async function loadPayments() {
    try {
        const response = await fetch('/api/payments');
        paymentsData = await response.json();
        renderTable();
    } catch (error) {
        alert('❌ ' + error.message);
        paymentsData = [];
        renderTable();
    }
}
```

---

## 5️⃣ TENANTS.HTML & OWNERS.HTML - Pattern tương tự

```javascript
// Tenants API: /api/tenants
// Owners API: /api/owners
// Same pattern as above
```

---

## 6️⃣ ACCOUNTS.HTML - Reset Password

```javascript
// Lấy danh sách tài khoản
async function loadAccounts() {
    try {
        const response = await fetch('/api/auth/accounts');
        accountsData = await response.json();
        renderTable();
    } catch (error) {
        alert('❌ ' + error.message);
    }
}

// Reset password - Tạo mật khẩu mới
async function resetPassword(accountId) {
    if (!confirm('Bạn có chắc muốn reset mật khẩu?')) return;
    
    try {
        const response = await fetch(`/api/auth/accounts/${accountId}/reset-password`, {
            method: 'PUT'
        });
        
        if (!response.ok) throw new Error('Failed');
        
        const data = await response.json();
        
        // Hiển thị mật khẩu tạm thời
        alert(`✅ Reset thành công!\n\nMật khẩu tạm thời: ${data.temporaryPassword}`);
        
        // Sao chép vào clipboard
        navigator.clipboard.writeText(data.temporaryPassword);
        alert('✅ Mật khẩu đã copy vào clipboard!');
        
    } catch (error) {
        alert('❌ ' + error.message);
    }
}
```

---

## 🎯 CHECKLIST SỬA TỪ FILE HIỆN CÓ

### **staff.html - 5 phút**
1. Tìm: `function loadStaff()`
2. Thay: Dùng `fetch('/api/staffs')`
3. Tìm: `function saveStaff()`
4. Thay: Thêm try-catch + fetch POST/PUT
5. Tìm: `function deleteStaff()`
6. Thay: Thêm fetch DELETE

### **properties.html - 5 phút**
Làm tương tự staff.html nhưng đổi:
- API endpoint
- Field names

### **contracts.html - 5 phút**
Tương tự

### **payments.html - 5 phút**
Tương tự

### **tenants.html - 5 phút**
Tương tự

### **owners.html - 5 phút**
Tương tự

### **accounts.html - 3 phút**
- Load: `fetch('/api/auth/accounts')`
- Reset: `fetch('/api/auth/accounts/{id}/reset-password', {method: 'PUT'})`

---

## 💡 LƯU Ý QUAN TRỌNG

### ✅ Luôn có try-catch
```javascript
try {
    const response = await fetch(...);
    if (!response.ok) throw new Error('API Error');
    return await response.json();
} catch (error) {
    alert('❌ Lỗi: ' + error.message);
}
```

### ✅ Validate form trước submit
```javascript
if (!data.fullName) {
    alert('❌ Vui lòng nhập họ tên');
    return;
}
```

### ✅ Reload list sau thay đổi
```javascript
// Sau POST/PUT/DELETE
loadStaff();  // Làm mới danh sách
```

### ✅ Clear form sau save
```javascript
function closeModal() {
    document.getElementById('staffForm').reset();
    document.getElementById('staffModal').style.display = 'none';
}
```

### ✅ Giữ ID khi sửa
```javascript
document.getElementById('staffForm').dataset.editingId = id;
```

---

## 🚀 CÓ THỂ COPY-PASTE TRỰC TIẾP

Pattern cơ bản không thay đổi, chỉ cần đổi:

| Thay đổi | staff.html | properties.html | contracts.html |
|---------|-----------|-----------------|----------------|
| API endpoint | `/api/staffs` | `/api/properties` | `/api/contracts` |
| Form ID | `#staffForm` | `#propertyForm` | `#contractForm` |
| Data var | `staffData` | `propertiesData` | `contractsData` |
| Field names | `fullName, phone, role` | `tenBDS, diaChi, ...` | `hopDongId, ...` |

---

**Ước tính:** ~30 phút sửa tất cả 7 trang! 🚀

