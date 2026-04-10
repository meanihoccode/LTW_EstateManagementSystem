# 🔍 Tìm Kiếm Không Dấu - Hướng Dẫn Chi Tiết

## ✨ Tính Năng

Tất cả các trang quản lý đều hỗ trợ **tìm kiếm không dấu tiếng Việt**:
- ✅ Tìm kiếm không cần gõ đúng dấu
- ✅ Hỗ trợ toàn bộ tiếng Việt
- ✅ Phối hợp với phân trang
- ✅ Tìm kiếm theo nhiều field

---

## 📋 Các trang được hỗ trợ

### 1. **Khách Thuê** (tenants.html)
- Tìm kiếm theo: Tên, Email, Điện thoại
- **Ví dụ:**
  - Tìm "Nguyen Van A" → tìm "Nguyễn Văn A" ✓
  - Tìm "hang" → tìm "Hàng" ✓
  - Tìm "091" → tìm "0912345678" ✓

### 2. **Bất Động Sản** (properties.html)
- Tìm kiếm theo: Tên BĐS, Địa chỉ
- Kết hợp với lọc trạng thái
- **Ví dụ:**
  - Tìm "pho" → tìm "Phố" ✓
  - Tìm "ha noi" → tìm "Hà Nội" ✓

### 3. **Chủ Sở Hữu** (owners.html)
- Tìm kiếm theo: Tên, Email, Điện thoại
- **Ví dụ:**
  - Tìm "tran" → tìm "Trần" ✓

### 4. **Hợp Đồng** (contracts.html)
- Tìm kiếm theo: Tên BĐS, Tên khách thuê
- Kết hợp với lọc trạng thái
- **Ví dụ:**
  - Tìm "hop dong" → tìm "Hợp Đồng" ✓

### 5. **Thanh Toán** (payments.html)
- Tìm kiếm theo: ID hợp đồng, ID thanh toán
- Kết hợp với lọc trạng thái
- **Ví dụ:**
  - Tìm "1" → tìm ID "1" ✓

### 6. **Nhân Viên** (staff.html)
- Tìm kiếm theo: Tên, Chức vụ, Điện thoại
- **Ví dụ:**
  - Tìm "nhan vien" → tìm "Nhân Viên" ✓
  - Tìm "quan ly" → tìm "Quản Lý" ✓

### 7. **Tài Khoản** (accounts.html)
- Tìm kiếm theo: Username, Tên nhân viên
- Kết hợp với lọc quyền hạn
- **Ví dụ:**
  - Tìm "admin" → tìm "admin" ✓

---

## 🛠️ Cách Hoạt Động

### Hàm `removeDiacritics(text)`
Loại bỏ tất cả dấu tiếng Việt:
```javascript
'Nguyễn Văn Á' → 'Nguyen Van A'
'Hàng, Hãng, Hạng' → 'hang, hang, hang'
'Chủ Sở Hữu' → 'Chu So Huu'
'Đặc Biệt' → 'Dac Biet'
```

### Hàm `searchWithoutDiacritics(searchText, targetText)`
So sánh hai string không dấu:
```javascript
// Trả về true
searchWithoutDiacritics('Nguyen', 'Nguyễn')
searchWithoutDiacritics('ha noi', 'Hà Nội')
searchWithoutDiacritics('0912', '0912345678')

// Trả về false
searchWithoutDiacritics('Tran', 'Nguyen')
```

### Hàm `filterBySearch(items, searchText, fields)`
Lọc array theo từ khóa:
```javascript
const result = filterBySearch(staffList, 'nhan vien', ['fullName', 'role']);
// Tìm 'nhan vien' trong field fullName và role
```

---

## 💡 Ví Dụ Sử Dụng

### Trong filterTable()
```javascript
function filterTable() {
    const searchText = document.getElementById('searchInput').value;
    
    pagination.filterData(item => {
        return searchWithoutDiacritics(searchText, item.name) ||
               searchWithoutDiacritics(searchText, item.email);
    });
    
    renderTable();
    renderPagination();
}
```

### So sánh cũ vs mới
**Cũ (không hỗ trợ dấu):**
```javascript
// ❌ Phải gõ đúng tên: "Nguyễn"
searchText.toLowerCase().includes('Nguyễn')
```

**Mới (hỗ trợ không dấu):**
```javascript
// ✅ Có thể gõ bất kỳ cách nào
searchWithoutDiacritics('Nguyen', 'Nguyễn')  // true
searchWithoutDiacritics('nguyen', 'Nguyễn')  // true
searchWithoutDiacritics('nguyễn', 'Nguyễn')  // true
```

---

## 📊 Bảng Tương Ứng Dấu

| Dấu | Ký Tự | Kết Quả |
|-----|-------|--------|
| Dấu Sắc | á, é, í, ó, ú | a, e, i, o, u |
| Dấu Huyền | à, è, ì, ò, ù | a, e, i, o, u |
| Dấu Hỏi | ả, ẻ, ỉ, ỏ, ủ | a, e, i, o, u |
| Dấu Ngã | ã, ẽ, ĩ, õ, ũ | a, e, i, o, u |
| Dấu Nặng | ạ, ẹ, ị, ọ, ụ | a, e, i, o, u |
| Cộng | ă, â, ê, ô, ơ, ư | a, e, o, u |
| Đ | đ | d |

---

## 🚀 Cài Đặt Trên Trang Mới

### Bước 1: Thêm script
```html
<script src="/js/search-utils.js"></script>
```

### Bước 2: Sử dụng trong filterTable()
```javascript
function filterTable() {
    const searchText = document.getElementById('searchInput').value;
    
    pagination.filterData(item => 
        searchWithoutDiacritics(searchText, item.name)
    );
    
    renderTable();
    renderPagination();
}
```

### Bước 3: Thế là xong! ✨

---

## 🔧 Mở Rộng (Tùy Chỉnh)

### Thêm hỗ trợ ký tự khác
Sửa hàm `removeDiacritics()`:
```javascript
const diacriticsMap = {
    // ... existing mappings
    'ç': 'c',      // Tiếng Pháp
    'ñ': 'n',      // Tiếng Tây Ban Nha
    'ß': 'ss'      // Tiếng Đức
};
```

### Tìm kiếm nhiều từ khóa
```javascript
function multiFieldSearch(searchText, ...values) {
    const words = searchText.split(' ');
    return values.some(value => 
        words.some(word => 
            searchWithoutDiacritics(word, value)
        )
    );
}
```

---

## ⚡ Performance

- **Tốc độ:** Rất nhanh, xử lý trong milliseconds
- **Bộ nhớ:** Tối thiểu, không lưu trữ thêm dữ liệu
- **Kết hợp:** Hoạt động tốt với phân trang
- **Dark mode:** Hỗ trợ đầy đủ

---

## 📝 Tài Liệu

File: `/js/search-utils.js`
- `removeDiacritics(text)` - Loại bỏ dấu
- `searchWithoutDiacritics(searchText, targetText)` - So sánh không dấu
- `filterBySearch(items, searchText, fields)` - Lọc array

---

## ✅ Checklist

- ✅ 7 trang đã cập nhật
- ✅ Tìm kiếm không dấu
- ✅ Hoạt động với phân trang
- ✅ Dark mode support
- ✅ Tài liệu đầy đủ

**Tìm kiếm không dấu đã sẵn sàng! 🎉**

