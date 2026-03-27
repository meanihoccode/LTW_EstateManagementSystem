# Hướng dẫn thêm Phân trang cho các trang

## Đã hoàn thành:
✅ **tenants.html** - Phân trang đã được thêm
✅ **pagination.js** - File utility cho phân trang
✅ **dashboard.css** - CSS cho pagination đã được thêm

## Còn lại cần làm:

### 1. **properties.html** - Cần thêm phân trang
- Thêm `<script src="/js/pagination.js"></script>` trong `<head>`
- Thêm phần HTML pagination sau bảng
- Sửa JavaScript: 
  - Thêm `let pagination = new PaginationManager({ pageSize: 10 });`
  - Sửa `renderTable()` để dùng `pagination.getPagedData()`
  - Thêm `renderPagination()` và `goToPropertyPage()`
  - Sửa `filterTable()` để dùng `pagination.filterData()`

### 2. **owners.html** - Cần thêm phân trang
- Bước như properties.html

### 3. **contracts.html** - Cần thêm phân trang
- Bước như properties.html

### 4. **payments.html** - Cần thêm phân trang
- Bước như properties.html

### 5. **staff.html** - Cần thêm phân trang
- Bước như properties.html

### 6. **accounts.html** - Cần thêm phân trang
- Bước như properties.html

## HTML cần thêm sau bảng:
```html
<!-- Pagination -->
<div class="pagination-container">
    <div class="pagination-info">
        <span>Hiển thị <strong id="startRecord">0</strong> - <strong id="endRecord">0</strong> trong <strong id="totalRecords">0</strong> bản ghi</span>
        <select id="pageSize" class="page-size-select">
            <option value="5">5 bản ghi/trang</option>
            <option value="10" selected>10 bản ghi/trang</option>
            <option value="20">20 bản ghi/trang</option>
            <option value="50">50 bản ghi/trang</option>
        </select>
    </div>
    <div class="pagination-buttons" id="paginationButtons">
        <!-- Được tạo bởi JavaScript -->
    </div>
</div>
```

## JavaScript cần thêm:
- Thêm biến: `let pagination = new PaginationManager({ pageSize: 10 });`
- Sửa `loadData()`: `pagination.setData(data); renderTable(); renderPagination();`
- Tạo `renderPagination()` và `goToPage()` functions
- Sửa `renderTable()` để dùng `pagination.getPagedData()`
- Sửa `filterTable()` để dùng `pagination.filterData()`
- Thêm `setupPaginationListener()` cho select pageSize

