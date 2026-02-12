# 📚 HƯỚNG DẪN LẤY NHÂN VIÊN CÓ ACCOUNT ĐỂ HIỂN THỊ

## 🎯 CÂU HỎI

Khi lấy nhân viên sẽ có account one-to-one đúng không? Vậy muốn lấy nhân viên để hiển thị lên trang account thì sao?

## ✅ TRẢ LỜI

**Đúng!** Hiện tại bạn đã có:
- **User entity (nhanvien)** - Thông tin nhân viên
- **Account entity (taikhoan)** - Thông tin tài khoản đăng nhập
- **Mối quan hệ:** 1 User có 1 Account (@OneToOne)

```java
@Entity
@Table(name = "nhanvien")
public class User {
    @OneToOne
    @JoinColumn(name = "tai_khoan_id")
    private Account account;
}
```

---

## 🔍 CÓ 2 CÁCH LẤY DỮ LIỆU

### **CÁCH 1: Lấy User (Nhân viên) → Truy cập Account**

Nếu bạn **bắt đầu từ nhân viên**, sau đó lấy tài khoản của họ:

```java
// ✅ UserService.java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    // Lấy 1 user + account
    public User getUserWithAccount(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        // Khi lấy User, Account sẽ được tự động load (vì @OneToOne)
        return user;
    }
    
    // Trong Controller
    @GetMapping("/api/staffs/{id}")
    public ResponseEntity<?> getStaff(@PathVariable Integer id) {
        User user = userService.getUserWithAccount(id);
        // Dùng user.getAccount() để lấy tài khoản
        return ResponseEntity.ok(user);
    }
}
```

**Kết quả JSON:**
```json
{
    "id": 1,
    "fullName": "Nguyễn Văn A",
    "phone": "0909000001",
    "role": "Quản lý",
    "account": {
        "id": 1,
        "username": "admin",
        "role": "Admin",
        "firstLogin": false
    }
}
```

---

### **CÁCH 2: Lấy Account → Truy cập User**

Nếu bạn **bắt đầu từ tài khoản**, sau đó lấy nhân viên của họ:

**⚠️ PROBLEM:** Account không có reference đến User, vì @OneToOne chỉ ở phía User

```java
// ❌ KHÔNG THỂ LÀM VẬY (chưa set up)
account.getUser()  // ← NullPointerException
```

**💡 GIẢI PHÁP:** Cập nhật Account entity để có 2 chiều

```java
@Entity
@Table(name = "taikhoan")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // ...các field khác...
    
    // ✅ Thêm dòng này để có quan hệ 2 chiều
    @OneToOne(mappedBy = "account")
    private User user;
}
```

Sau đó mới có thể:

```java
@GetMapping("/api/accounts/{id}")
public ResponseEntity<?> getAccountWithStaff(@PathVariable Integer id) {
    Account account = accountService.getById(id);
    // Giờ mới lấy được nhân viên
    User staff = account.getUser();
    return ResponseEntity.ok(account);
}
```

---

## 📊 SO SÁNH 2 CÁCH

| Cách | Khi nào dùng | JSON trả về | Cộng/Trừ |
|------|----------|-----------|---------|
| **Cách 1: User → Account** | Hiển thị danh sách nhân viên + account | `{"id", "fullName", "account": {...}}` | ✅ Dễ, không cần sửa |
| **Cách 2: Account → User** | Admin xem tài khoản + nhân viên | `{"id", "username", "user": {...}}` | ❌ Cần sửa Account entity |

---

## 🛠️ CÓ NÊN THÊM CHIỀU NGƯỢC VÀO ACCOUNT KHÔNG?

**ĐÁP ÁN:** Tùy vào needs của bạn!

### **Nếu CÓ (2 chiều):**
```java
// Account.java
@OneToOne(mappedBy = "account")
private User user;
```

**Ưu:** Có thể từ Account lấy User dễ dàng
**Nhược:** Nhiều truy vấn DB, phức tạp hơn

---

### **Nếu KHÔNG (1 chiều - hiện tại):**
Giữ nguyên như hiện tại, chỉ User có Account

**Ưu:** Đơn giản, nhanh, hiệu năng tốt
**Nhược:** Chỉ lấy được Account từ User, không ngược lại

---

## 🎯 KHUYẾN CÁO

Cho dự án solo của bạn, tôi **khuyến cáo: GIỮ NGUYÊN (1 CHIỀU)** vì:

✅ Đơn giản, dễ quản lý
✅ Hiệu năng tốt (1 truy vấn thay vì 2)
✅ Logic clear: Nhân viên có tài khoản, không phải ngược lại
✅ Dễ code backend

**Cách lấy:**
```java
// Lấy nhân viên + account
User staff = userService.getUserWithAccount(id);
Account account = staff.getAccount();
```

---

## 📋 ĐỀ XUẤT THIẾT KẾ TRANG ACCOUNT

### **1. Hiển thị danh sách nhân viên + tài khoản**

**Endpoint:**
```java
@GetMapping("/api/accounts")
public ResponseEntity<?> getAllAccounts() {
    // Lấy tất cả User
    List<User> staffs = userService.getAllUsers();
    // Khi trả về, User sẽ kèm Account
    return ResponseEntity.ok(staffs);
}
```

**JSON Response:**
```json
[
    {
        "id": 1,
        "fullName": "Nguyễn Văn A",
        "phone": "0909000001",
        "role": "Quản lý",
        "account": {
            "id": 1,
            "username": "admin",
            "role": "Admin",
            "firstLogin": false
        }
    },
    {
        "id": 2,
        "fullName": "Trần Thị B",
        "phone": "0909000002",
        "role": "Nhân viên",
        "account": {
            "id": 2,
            "username": "staff1",
            "role": "Staff",
            "firstLogin": false
        }
    }
]
```

### **2. Hiển thị chi tiết 1 tài khoản**

**Endpoint:**
```java
@GetMapping("/api/accounts/{id}")
public ResponseEntity<?> getAccountDetail(@PathVariable Integer id) {
    User staff = userService.getUserWithAccount(id);
    return ResponseEntity.ok(staff);
}
```

### **3. Reset password (Admin)**

**Endpoint:**
```java
@PutMapping("/api/accounts/{id}/reset-password")
public ResponseEntity<?> resetPassword(@PathVariable Integer id) {
    PasswordResetResponse response = accountService.resetPassword(id);
    return ResponseEntity.ok(response);
}
```

**Response:**
```json
{
    "accountId": 1,
    "username": "admin",
    "temporaryPassword": "Aa@12345",
    "message": "Reset mật khẩu thành công"
}
```

---

## 🧪 TEST

**Bước 1:** Chạy server
```bash
gradlew bootRun
```

**Bước 2:** Test lấy danh sách
```bash
GET http://localhost:8080/api/accounts
```

**Bước 3:** Test lấy chi tiết
```bash
GET http://localhost:8080/api/accounts/1
```

---

## ✅ KẾT LUẬN

Với thiết kế hiện tại:
- ✅ User có thể truy cập Account
- ✅ Lấy nhân viên + account dễ dàng
- ✅ Hiển thị trên trang admin hoàn toàn có thể
- ✅ Không cần thay đổi gì thêm

**Cách dùng:**
```java
User staff = userService.getUserWithAccount(staffId);
Account account = staff.getAccount();  // ✅ Dùng được ngay
```

