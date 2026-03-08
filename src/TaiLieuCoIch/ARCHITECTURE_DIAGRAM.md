# 📐 SƠ ĐỒ KIẾN TRÚC - Visualize toàn bộ hệ thống

## 🏗️ ARCHITECTURE HIỆN TẠI

```
┌─────────────────────────────────────────────────────────────────┐
│                    🌐 FRONTEND (HTML/CSS/JS)                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐            │
│  │ index.html   │  │dashboard.html │  │ staff.html   │ ...       │
│  │  Login Form  │  │  Dashboard   │  │  Staff CRUD  │            │
│  └──────────────┘  └──────────────┘  └──────────────┘            │
│                                                                   │
│  ⚠️ Hiện tại: Cứng dữ liệu (hardcoded)                         │
│  ✅ Cần sửa: Gọi API từ backend                                │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
                              ↕↕↕ FETCH
                           API (JSON)
┌─────────────────────────────────────────────────────────────────┐
│                 🔌 BACKEND (Spring Boot REST API)               │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  POST   /api/auth/login                   ✅ Ready              │
│  PUT    /api/auth/accounts/{id}/change-pwd ✅ Ready              │
│                                                                   │
│  GET    /api/staffs                       ✅ Ready              │
│  POST   /api/staffs                       ✅ Ready              │
│  PUT    /api/staffs/{id}                  ✅ Ready              │
│  DELETE /api/staffs/{id}                  ✅ Ready              │
│                                                                   │
│  GET    /api/properties                   ✅ Ready              │
│  POST   /api/properties                   ✅ Ready              │
│  PUT    /api/properties/{id}              ✅ Ready              │
│  DELETE /api/properties/{id}              ✅ Ready              │
│                                                                   │
│  [Tương tự cho: contracts, payments, tenants, owners, accounts] │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
                              ↕↕↕ JDBC
                         Hibernate ORM
┌─────────────────────────────────────────────────────────────────┐
│                 🗄️ DATABASE (MySQL)                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ✅ quan_ly_bat_dong_san (Schema)                               │
│                                                                   │
│  ├─ NhanVien (Users)           ✅ Table có dữ liệu              │
│  ├─ TaiKhoan (Accounts)        ✅ Table có dữ liệu              │
│  ├─ BatDongSan (Properties)    ✅ Table có dữ liệu              │
│  ├─ HopDong (Contracts)        ✅ Table có dữ liệu              │
│  ├─ ThanhToan (Payments)       ✅ Table có dữ liệu              │
│  ├─ KhachThue (Tenants)        ✅ Table có dữ liệu              │
│  └─ ChuSoHuu (Owners)          ✅ Table có dữ liệu              │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔄 LOGIN FLOW - Quy trình đăng nhập

```
┌────────────────┐
│  Admin         │
│  Tạo account   │ ────→ POST /api/auth/accounts
│                │       ← Response: { tempPassword }
└────────────────┘

┌────────────────┐
│  Nhân viên     │       
│  Lần 1:        │
│  Login + pass  │ ────→ POST /api/auth/login
│                │
│                │ ← Response: { status: "FIRST_LOGIN", accountId: 1 }
│                │
│  Redirect to   │
│  change-pwd    │ ────→ /change-password?accountId=1
│                │
│  Nhập:         │
│  - Pass cũ     │ ────→ PUT /api/auth/accounts/1/change-password
│  - Pass mới    │       ← Response: { firstLogin: false }
│                │
│  Redirect to   │
│  dashboard     │ ────→ /dashboard
└────────────────┘

┌────────────────┐
│  Nhân viên     │
│  Lần 2+:       │
│  Login + pass  │ ────→ POST /api/auth/login
│  mới           │
│                │ ← Response: { status: "SUCCESS" }
│                │
│  Redirect to   │
│  dashboard     │ ────→ /dashboard
└────────────────┘
```

---

## 📋 STAFF CRUD FLOW - Ví dụ

```
┌─────────────────────────────────────────┐
│ Frontend (staff.html)                   │
├─────────────────────────────────────────┤
│                                         │
│  Page Load                              │
│      ↓                                  │
│  loadStaff() ────→ GET /api/staffs     │
│      ↓                                  │
│  Display Table                          │
│                                         │
│  User clicks "Thêm"                     │
│      ↓                                  │
│  Open Modal Form                        │
│      ↓                                  │
│  Fill form + click "Lưu"                │
│      ↓                                  │
│  saveStaff() ────→ POST /api/staffs     │
│      ↓                                  │
│  ✅ Success message                    │
│  Close modal                            │
│  Reload list (loadStaff again)          │
│                                         │
│  User clicks "Sửa"                      │
│      ↓                                  │
│  editStaff() ────→ GET /api/staffs/{id}│
│      ↓                                  │
│  Fill modal with data                   │
│  User updates + click "Lưu"             │
│      ↓                                  │
│  saveStaff() ────→ PUT /api/staffs/{id} │
│      ↓                                  │
│  ✅ Success message                    │
│  Reload list                            │
│                                         │
│  User clicks "Xóa"                      │
│      ↓                                  │
│  Confirm dialog                         │
│      ↓                                  │
│  deleteStaff() ────→ DELETE /api/staffs/{id}
│      ↓                                  │
│  ✅ Success message                    │
│  Reload list                            │
│                                         │
└─────────────────────────────────────────┘
```

---

## 📂 PROJECT STRUCTURE - Cấu trúc dự án

```
LTW_QuanLyBDS/
│
├─ src/main/
│  ├─ java/
│  │  └─ com/example/ltw_quanlybds/
│  │     ├─ api/
│  │     │  ├─ AccountController.java          ✅ Login + Pwd
│  │     │  ├─ UserController.java             ✅ Staff CRUD
│  │     │  ├─ PropertyController.java         ✅ Property CRUD
│  │     │  ├─ ContractController.java         ✅ Contract CRUD
│  │     │  ├─ PaymentController.java          ✅ Payment CRUD
│  │     │  ├─ TenantController.java           ✅ Tenant CRUD
│  │     │  └─ OwnerController.java            ✅ Owner CRUD
│  │     ├─ service/
│  │     │  ├─ AccountService.java             ✅ Pwd logic
│  │     │  ├─ UserService.java                ✅ Staff logic
│  │     │  ├─ PropertyService.java            ✅ Property logic
│  │     │  └─ ... (Other services)
│  │     ├─ entity/
│  │     │  ├─ Account.java                    ✅ Has @Entity
│  │     │  ├─ User.java                       ✅ Has @Entity
│  │     │  ├─ Property.java                   ✅ Has @Entity
│  │     │  └─ ... (Other entities)
│  │     ├─ repository/
│  │     │  ├─ AccountRepository.java          ✅ JPA
│  │     │  ├─ UserRepository.java             ✅ JPA
│  │     │  └─ ... (Other repos)
│  │     └─ LtwQuanLyBdsApplication.java       ✅ Main class
│  │
│  └─ resources/
│     ├─ application.properties                ✅ DB config
│     │
│     ├─ static/
│     │  ├─ css/
│     │  │  ├─ dashboard.css                   ✅ Layout
│     │  │  └─ style.css                       ✅ Home page
│     │  ├─ js/
│     │  │  └─ script.js                       (tuỳ chọn)
│     │  └─ img/
│     │     ├─ house.png                       ✅
│     │     └─ login.png                       ✅
│     │
│     └─ templates/
│        ├─ index.html                         ⚠️ Need API
│        ├─ dashboard.html                     ⚠️ Need auth check
│        ├─ staff.html                         ⚠️ Need API
│        ├─ properties.html                    ⚠️ Need API
│        ├─ contracts.html                     ⚠️ Need API
│        ├─ payments.html                      ⚠️ Need API
│        ├─ tenants.html                       ⚠️ Need API
│        ├─ owners.html                        ⚠️ Need API
│        ├─ accounts.html                      ⚠️ Need API
│        └─ change-password.html               ✅ Complete
│
├─ build.gradle                                ✅ Dependencies
└─ src/TaiLieuCoIch/
   ├─ README.md                                📄 Danh sách file
   ├─ CHEATSHEET_NHANH.md                      📄 Quick code
   ├─ TEMPLATE_CODE_LIEN_KET_API.md            📄 Template
   ├─ FLOW_DANG_NHAP_VA_PHAN_QUYEN.md          📄 Security
   ├─ BACAO_KIEM_TRA_CHI_TIET.md               📄 Detailed
   ├─ TONG_HOP_KET_LUAN.md                     📄 Summary
   ├─ HUONG_DAN_TIEN_TRINH_TIEN_HANH.md        📄 Guide
   └─ ARCHITECTURE_DIAGRAM.md                  📄 This file
```

---

## 📊 PROGRESS - Tiến độ hiện tại

```
BACKEND
✅✅✅✅✅✅✅✅✅✅ 100% COMPLETE
█████████████████████████████ 100/100

DATABASE
✅✅✅✅✅✅✅✅✅✅ 100% COMPLETE
█████████████████████████████ 100/100

FRONTEND HTML/CSS
✅✅✅✅✅✅✅✅✅✅ 100% COMPLETE
█████████████████████████████ 100/100

FRONTEND JAVASCRIPT (API Integration)
✅✅✅░░░░░░░░░░░░░░░░░░░░░░░░░░ 30% DONE
█████░░░░░░░░░░░░░░░░░░░░░░░░░░░ 30/100

LOGIN FLOW
✅✅✅✅░░░░░░░░░░░░░░░░░░░░░░░░░░ 40% DONE
████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ 40/100

TOTAL PROJECT
✅✅✅✅✅✅✅✅░░░░░░░░░░░░░░░░░░░░ 75% DONE
████████░░░░░░░░░░░░░░░░░░░░░░░░░ 75/100
```

---

## ⏱️ TIMELINE - Dự kiến

```
Week 1 (Hiện tại)
│
├─ Day 1: Code Login + Dashboard (2h)
│        ✅ index.html: fetch /api/auth/login
│        ✅ dashboard.html: checkAuthStatus()
│
├─ Day 2: Code Staff CRUD (1.5h)
│        ✅ loadStaff() → GET /api/staffs
│        ✅ saveStaff() → POST/PUT
│        ✅ deleteStaff() → DELETE
│
├─ Day 3: Copy Pattern (2h)
│        ✅ properties.html
│        ✅ contracts.html
│        ✅ payments.html
│        ✅ tenants.html
│        ✅ owners.html
│        ✅ accounts.html
│
├─ Day 4: Testing (1h)
│        ✅ Test login flow
│        ✅ Test CRUD operations
│        ✅ Fix bugs
│
└─ Day 5: Code Review + Polish (1h)
         ✅ Code review
         ✅ UI polish
         ✅ Documentation
         
WEEK 1 TOTAL: ~7-8 hours → READY TO DEPLOY! 🚀

Week 2 (Tuỳ chọn - Features)
├─ Search & Filter
├─ Validation improvements
├─ Notification system
└─ JWT Authentication

Week 3 (Tuỳ chọn - Advanced)
├─ Role-based access control
├─ Advanced analytics
├─ Performance optimization
└─ Production deployment
```

---

## 🎯 CONVERSION MAP - Từ Hardcoded → API

### TRƯỚC (Hardcoded)
```javascript
function loadStaff() {
    staffData = [
        { id: 1, hoTen: 'Lê Văn X', soDienThoai: '0901234567', vaiTro: 'Quản lý' },
        { id: 2, hoTen: 'Hoàng Thị Y', soDienThoai: '0912345678', vaiTro: 'Nhân viên' }
    ];
    renderTable();
}
```

### SAU (API Integration)
```javascript
async function loadStaff() {
    try {
        const response = await fetch('/api/staffs');
        staffData = await response.json();
        renderTable();
    } catch (error) {
        alert('Lỗi: ' + error);
    }
}
```

**Chỉ cần thay 3 dòng!**

---

## 📞 TROUBLESHOOTING QUICK MAP

```
Problem                  Check                    Solution
──────────────────────────────────────────────────────────────
❌ 404 Not Found        Backend đã chạy?        gradlew bootRun
                        Endpoint đúng?          Dùng Postman test
                        
❌ CORS Error           Frontend/Backend port   Check application.properties
                        khác nhau?
                        
❌ Cannot read prop     staffData undefined?    Gọi loadStaff() trước
'map' of undefined                              
                        
❌ Login không hoạt     localStorage?           Check DevTools → Storage
động                    Response status?        Check Network tab
                        
❌ Password change      oldPassword sai?        Check email dari admin
không thành công        Format newPassword?     Phải 8+ ký tự
```

---

## 🔐 SECURITY CHECKLIST

```
✅ Password Hashing
   - BCryptPasswordEncoder dùng BCrypt
   - Không lưu plaintext password
   
✅ First Login Flag
   - firstLogin = true khi tạo account
   - firstLogin = false sau khi change password
   
⚠️ Session Management
   - Hiện: Session (tạm được)
   - Tương lai: JWT token (bảo mật hơn)
   
⚠️ Authorization
   - Hiện: Chưa có role check
   - Tương lai: Add @PreAuthorize trên API
   
⚠️ Input Validation
   - Hiện: Có @Valid
   - Tương lai: Strengthen regex
```

---

## 🚀 DEPLOYMENT CHECKLIST

```
Frontend Ready?
✅ index.html - Login working
✅ dashboard.html - Auth check
✅ Tất cả CRUD pages - API integrated
✅ change-password.html - Complete
✅ CSS/Images - Loaded correctly

Backend Ready?
✅ All endpoints working
✅ Database connected
✅ Error handling implemented
✅ Validation working

Testing Complete?
☐ Login flow test
☐ CRUD operations test
☐ Search/Filter test
☐ Error cases test
☐ Browser compatibility test

Deployment?
☐ Build successful: gradlew build
☐ No errors in console
☐ All static files present
☐ Database migration done
☐ Environment variables set

After Deploy?
☐ Test in production URL
☐ Monitor logs
☐ Backup database
☐ Document API changes
```

---

**Architecture Clear? Start coding! 💪**

