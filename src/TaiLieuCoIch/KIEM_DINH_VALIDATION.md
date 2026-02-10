# ✅ KIỂM ĐỊNH: Bean Validation - HOÀN THÀNH

## 📋 **DANH SÁCH KIỂM TRA**

### ✅ **1. Dependencies (build.gradle)**
```gradle
implementation 'jakarta.validation:jakarta.validation-api:3.0.2'
implementation 'org.hibernate.validator:hibernate-validator:8.0.1.Final'
```
**Status:** ✅ ĐÃ THÊM

---

### ✅ **2. Entity Validation - DONE**

#### Property.java ✅
```java
@NotBlank(message = "Tên bất động sản không được để trống")
@Size(min = 3, max = 100, message = "Tên phải từ 3-100 ký tự")
private String name;

@NotNull(message = "Diện tích không được null")
@DecimalMin(value = "0.1", message = "Diện tích phải lớn hơn 0")
private BigDecimal area;

@NotNull(message = "Giá thuê không được null")
@DecimalMin(value = "0", message = "Giá thuê phải lớn hơn hoặc bằng 0")
private BigDecimal rentalPrice;
```

#### Tenant.java ✅
```java
@NotBlank(message = "Tên khách thuê không được để trống")
@Size(min = 3, max = 100, message = "Tên phải từ 3-100 ký tự")
private String fullName;

@Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải là 10-11 chữ số")
private String phone;

@Email(message = "Email phải hợp lệ")
private String email;
```

#### Owner.java ✅
```java
@NotBlank(message = "Tên chủ sở hữu không được để trống")
@Size(min = 3, max = 100, message = "Tên phải từ 3-100 ký tự")
private String fullName;

@Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải là 10-11 chữ số")
private String phone;

@Email(message = "Email phải hợp lệ")
private String email;
```

#### User.java ✅
```java
@NotBlank(message = "Tên nhân viên không được để trống")
@Size(min = 3, max = 100, message = "Tên phải từ 3-100 ký tự")
private String fullName;

@Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải là 10-11 chữ số")
private String phone;

@NotBlank(message = "Vai trò không được để trống")
private String role;
```

#### Contract.java ✅
```java
@NotNull(message = "Ngày bắt đầu không được null")
private LocalDate startDate;

@NotNull(message = "Ngày kết thúc không được null")
private LocalDate endDate;

@NotNull(message = "Tiền cọc không được null")
@DecimalMin(value = "0", message = "Tiền cọc phải lớn hơn hoặc bằng 0")
private BigDecimal deposit;
```

#### Payment.java ✅
```java
@NotNull(message = "Ngày thanh toán không được null")
private LocalDate paymentDate;

@NotNull(message = "Số tiền không được null")
@DecimalMin(value = "0.01", message = "Số tiền phải lớn hơn 0")
private BigDecimal amount;

@NotBlank(message = "Phương thức thanh toán không được để trống")
private String method;
```

---

### ✅ **3. Controller - @Valid Annotations - DONE**

#### PropertyController.java ✅
```java
@PostMapping
public ResponseEntity<Property> createProperty(@Valid @RequestBody Property property)

@PutMapping("/{id}")
public ResponseEntity<Property> updateProperty(@PathVariable Integer id,
                                               @Valid @RequestBody Property propertyDetails)
```

#### TenantController.java ✅
```java
@PostMapping
public ResponseEntity<Tenant> createTenant(@Valid @RequestBody Tenant tenant)

@PutMapping("/{id}")
public ResponseEntity<Tenant> updateTenant(@PathVariable Integer id, @Valid @RequestBody Tenant tenant)
```

#### OwnerController.java ✅
```java
@PostMapping
public ResponseEntity<Owner> createOwner(@Valid @RequestBody Owner owner)

@PutMapping("/{id}")
public ResponseEntity<Owner> updateOwner(@PathVariable Integer id, @Valid @RequestBody Owner ownerDetails)
```

#### ContractController.java ✅
```java
@PostMapping
public ResponseEntity<Contract> createContract(@Valid @RequestBody Contract contract)

@PutMapping("/{id}")
public ResponseEntity<Contract> updateContract(@PathVariable Integer id, @Valid @RequestBody Contract contract)
```

#### PaymentController.java ✅
```java
@PostMapping
public ResponseEntity<Payment> createPayment(@Valid @RequestBody Payment payment)

@PutMapping("/{id}")
public ResponseEntity<Payment> updatePayment(@Valid @RequestBody Payment payment, @PathVariable Integer id)
```

#### UserController.java ✅
```java
@PostMapping
public ResponseEntity<User> createUser(@Valid @RequestBody User user)

@PutMapping("/{id}")
public ResponseEntity<User> updateUser(@PathVariable Integer id, @Valid @RequestBody User userDetails)
```

---

## 🏗️ **BUILD STATUS**

```
BUILD SUCCESSFUL in 5s
✅ Không có compile errors
✅ Tất cả validation annotations OK
```

---

## 📊 **VALIDATION RULES**

### **Property**
- Tên: NotBlank, Size 3-100
- Loại: NotBlank
- Địa chỉ: NotBlank
- Diện tích: NotNull, min 0.1
- Giá thuê: NotNull, min 0

### **Tenant/Owner**
- Tên: NotBlank, Size 3-100
- Điện thoại: Pattern (10-11 chữ số)
- Email: Email format
- CCCD: Size 9-12 (Tenant)

### **User**
- Tên: NotBlank, Size 3-100
- Điện thoại: Pattern (10-11 chữ số)
- Vai trò: NotBlank

### **Contract**
- Ngày bắt đầu: NotNull
- Ngày kết thúc: NotNull
- Tiền cọc: NotNull, min 0

### **Payment**
- Ngày thanh toán: NotNull
- Số tiền: NotNull, min 0.01
- Phương thức: NotBlank

---

## 🧪 **TEST VALIDATION**

### **Test 1: Invalid Property (tên trống)**
```bash
POST http://localhost:8080/api/properties
Content-Type: application/json

{
  "name": "",          # ❌ Sẽ báo lỗi: "Tên bất động sản không được để trống"
  "type": "Nhà phố",
  "address": "123 Đường ABC",
  "area": 100.5,
  "rentalPrice": 5000000
}
```

**Kết quả mong đợi:**
```json
{
  "message": "Validation failed",
  "errors": {
    "name": "Tên bất động sản không được để trống"
  },
  "status": "VALIDATION_ERROR",
  "code": 400
}
```

### **Test 2: Invalid Email**
```bash
POST http://localhost:8080/api/owners
{
  "fullName": "Nguyễn Văn A",
  "phone": "0123456789",
  "email": "invalid-email",  # ❌ Lỗi: không đúng format email
  "address": "123 Đường XYZ"
}
```

### **Test 3: Invalid Phone**
```bash
POST http://localhost:8080/api/tenants
{
  "fullName": "Trần Thị B",
  "idNumber": "123456789",
  "phone": "123",           # ❌ Lỗi: phải 10-11 chữ số
  "email": "b@example.com"
}
```

### **Test 4: Invalid Amount**
```bash
POST http://localhost:8080/api/payments
{
  "paymentDate": "2026-02-11",
  "amount": -100,           # ❌ Lỗi: phải lớn hơn 0
  "method": "Chuyển khoản"
}
```

---

## 💡 **VALIDATION ANNOTATIONS GIẢI THÍCH**

| Annotation | Ý Nghĩa | Ví Dụ |
|-----------|---------|-------|
| **@NotNull** | Giá trị không được null | Số tiền không null |
| **@NotBlank** | String không được trống | Tên không trống |
| **@Size** | Độ dài từ min-max | Tên 3-100 ký tự |
| **@Email** | Email hợp lệ | user@example.com |
| **@Pattern** | Regex pattern | `^[0-9]{10,11}$` |
| **@DecimalMin** | Giá trị tối thiểu | min 0 hoặc 0.1 |
| **@DecimalMax** | Giá trị tối đa | max 100000000 |
| **@Min** | Số nguyên tối thiểu | min 1 |
| **@Max** | Số nguyên tối đa | max 100 |

---

## ✅ **NEXT STEP**

### ✅ **Phần 1: BCrypt** - HOÀN THÀNH ✓
### ✅ **Phần 2: Bean Validation** - HOÀN THÀNH ✓

### 🔜 **Phần 3: Global Exception Handler** - CẦN LÀM

**Status:** 🟢 **Validation DONE - Tiếp Exception Handler**

