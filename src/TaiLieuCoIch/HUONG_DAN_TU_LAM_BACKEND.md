# 📖 HƯỚNG DẪN TỰ LÀM BACKEND STEP BY STEP

## 🎯 MỤC TIÊU
Bạn sẽ hiểu và tự tạo:
1. **Repository** - kết nối database
2. **Service** - xử lý logic
3. **API Controller** - tạo endpoints

---

## 📚 PHẦN 1: HIỂU REPOSITORY

### Khái Niệm:
```
Repository = Lớp truy cập dữ liệu từ database
```

### Repository extends JpaRepository:
```java
public interface PropertyRepository extends JpaRepository<Property, Integer> {
    // JpaRepository có sẵn: findAll(), findById(), save(), delete()...
    
    // Bạn chỉ cần định nghĩa các method tìm kiếm custom
    List<Property> findByStatus(String status);
}
```

### Naming Convention (Quy tắc đặt tên):
```java
findByStatus() → WHERE status = value
findByNameContainingIgnoreCase() → WHERE name LIKE '%value%'
findByStatusAndOwnerId() → WHERE status = value AND owner_id = value
```

---

## 🛠️ THỰC HÀNH: TẠO PropertyRepository

### Bước 1: Tạo file
```
src/main/java/com/example/ltw_quanlybds/repository/PropertyRepository.java
```

### Bước 2: Viết code
```java
package com.example.ltw_quanlybds.repository;

import com.example.ltw_quanlybds.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Integer> {
    List<Property> findByStatus(String status);
    List<Property> findByOwnerId(Integer ownerId);
    List<Property> findByNameContainingIgnoreCase(String name);
}
```

---

## 📚 PHẦN 2: HIỂU SERVICE

### Khái Niệm:
```
Service = Tầng xử lý logic nghiệp vụ
```

### Service Pattern:
```
Controller → Service → Repository → Database
```

---

## 🛠️ THỰC HÀNH: TẠO PropertyService

### Bước 1: Tạo file
```
src/main/java/com/example/ltw_quanlybds/service/PropertyService.java
```

### Bước 2: Viết code
```java
package com.example.ltw_quanlybds.service;

import com.example.ltw_quanlybds.entity.Property;
import com.example.ltw_quanlybds.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class PropertyService {
    
    @Autowired
    private PropertyRepository propertyRepository;
    
    // Lấy tất cả
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }
    
    // Lấy theo ID
    public Property getPropertyById(Integer id) {
        return propertyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Property not found"));
    }
    
    // Tìm kiếm
    public List<Property> searchProperties(String keyword) {
        return propertyRepository.findByNameContainingIgnoreCase(keyword);
    }
    
    // Thêm mới
    public Property createProperty(Property property) {
        return propertyRepository.save(property);
    }
    
    // Cập nhật
    public Property updateProperty(Integer id, Property propertyDetails) {
        Property property = getPropertyById(id);
        if (propertyDetails.getName() != null) property.setName(propertyDetails.getName());
        if (propertyDetails.getStatus() != null) property.setStatus(propertyDetails.getStatus());
        return propertyRepository.save(property);
    }
    
    // Xóa
    public void deleteProperty(Integer id) {
        if (!propertyRepository.existsById(id)) {
            throw new RuntimeException("Property not found");
        }
        propertyRepository.deleteById(id);
    }
}
```

---

## 📚 PHẦN 3: HIỂU CONTROLLER (REST API)

### Khái Niệm:
```
Controller = Lớp tiếp nhận HTTP request từ client
```

### REST API Endpoints:
```
GET    /api/properties          → Lấy tất cả
GET    /api/properties/{id}     → Lấy chi tiết
POST   /api/properties          → Tạo mới
PUT    /api/properties/{id}     → Cập nhật
DELETE /api/properties/{id}     → Xóa
```

---

## 🛠️ THỰC HÀNH: TẠO PropertyController

### Bước 1: Tạo file
```
src/main/java/com/example/ltw_quanlybds/api/PropertyController.java
```

### Bước 2: Viết code
```java
package com.example.ltw_quanlybds.api;

import com.example.ltw_quanlybds.entity.Property;
import com.example.ltw_quanlybds.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
public class PropertyController {
    
    @Autowired
    private PropertyService propertyService;
    
    @GetMapping
    public ResponseEntity<List<Property>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Property> getProperty(@PathVariable Integer id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Property>> searchProperties(@RequestParam String keyword) {
        return ResponseEntity.ok(propertyService.searchProperties(keyword));
    }
    
    @PostMapping
    public ResponseEntity<Property> createProperty(@RequestBody Property property) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(propertyService.createProperty(property));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Property> updateProperty(
            @PathVariable Integer id,
            @RequestBody Property property) {
        return ResponseEntity.ok(propertyService.updateProperty(id, property));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Integer id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## ✅ CHECKLIST - BƯỚC TIẾP THEO

- [ ] Tạo PropertyRepository
- [ ] Tạo PropertyService
- [ ] Tạo PropertyController
- [ ] Build & test: `gradlew clean build`
- [ ] Run: `gradlew bootRun`
- [ ] Test API bằng Postman

---

## 📝 TEST BẰNG POSTMAN

**GET All:**
```
URL: http://localhost:8080/api/properties
Method: GET
```

**POST Create:**
```
URL: http://localhost:8080/api/properties
Method: POST
Body (JSON):
{
  "name": "Căn hộ Q1",
  "type": "Căn hộ",
  "address": "123 Nguyễn Huệ",
  "area": 85.0,
  "rentalPrice": 1200.00,
  "status": "Cho thuê"
}
```

---

**Ready? Hãy bắt đầu code! 💪**
# 🚀 ROADMAP HOÀN THIỆN SẢN PHẨM - MEANI REAL ESTATE

## 📊 TÌNH TRẠNG HIỆN TẠI

### ✅ Đã Có:
- Frontend hoàn chỉnh (8 trang HTML với UI đẹp)
- Database schema trong MySQL
- Controller routes cơ bản
- Entity models (User, Property, Tenant, Contract, Owner, Payment)
- Application.properties kết nối MySQL

### ❌ Chưa Có:
- Repositories (JPA)
- Services (Business logic)
- REST API endpoints
- Authentication/Login logic
- Data validation
- Exception handling
- Frontend ↔ Backend integration

---

## 📋 CÁC BƯỚC TIẾP THEO (Ưu tiên)

### **PHASE 1: Backend Infrastructure (2-3 ngày)**

#### **Bước 1: Tạo Repository & Service**
**Thời gian:** ~4-6 giờ

#### **Bước 2: Tạo REST API Endpoints**
**Thời gian:** ~6-8 giờ

#### **Bước 3: Xử lý Authentication (Đăng nhập)**
**Thời gian:** ~3-4 giờ

### **PHASE 2: Frontend ↔ Backend Integration (2-3 ngày)**

#### **Bước 4: Connect API từ JavaScript**
**Thời gian:** ~4-6 giờ

#### **Bước 5: Form Validation & Error Handling**
**Thời gian:** ~2-3 giờ

### **PHASE 3: Polish & Testing (1-2 ngày)**

#### **Bước 6: CSS Responsive & Mobile Optimization**
**Thời gian:** ~2-3 giờ

#### **Bước 7: Testing & Bug Fixes**
**Thời gian:** ~2-3 giờ

#### **Bước 8: Documentation & Deployment**
**Thời gian:** ~1-2 giờ

---

## ⚡ QUICK START

**Hôm nay (Ngay bây giờ):**
1. Tạo Repository & Service cho Properties
2. Tạo API Controller cơ bản
3. Test API bằng Postman

**Ngày mai:**
1. Integrate Frontend với API
2. Fix bugs
3. Test toàn bộ flow

