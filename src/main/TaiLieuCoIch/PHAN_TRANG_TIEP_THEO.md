# Script hướng dẫn thêm Phân trang cho các trang còn lại

## ✅ Đã hoàn thành:
- tenants.html - Phân trang ✓
- properties.html - Phân trang ✓  
- owners.html - Phân trang ✓

## 🔄 Còn cần làm:

### 1. **contracts.html**
Thực hiện các bước sau:
1. Thêm `<script src="/js/pagination.js"></script>` trong `<head>`
2. Thêm HTML pagination sau bảng
3. Sửa script:
   - Khai báo: `let pagination = new PaginationManager({ pageSize: 10 });`
   - loadContracts: `pagination.setData(data); renderTable(); renderPagination();`
   - renderTable: `const pagedData = pagination.getPagedData();`
   - Thêm `renderPagination(), goToContractPage(), setupPaginationListener()`
   - filterTable: dùng `pagination.filterData()`

### 2. **payments.html**
Thực hiện các bước như contracts.html

### 3. **staff.html**  
Thực hiện các bước như contracts.html

### 4. **accounts.html**
Thực hiện các bước như contracts.html

## 📋 Template HTML pagination (copy-paste):
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

## 📝 Template JavaScript cần thêm:

```javascript
// 1. Khai báo biến
let pagination = new PaginationManager({ pageSize: 10 });

// 2. Trong DOMContentLoaded thêm
setupPaginationListener();

// 3. Thêm hàm renderPagination
function renderPagination() {
    pagination.renderPaginationButtons('paginationButtons', 'goToPageName');
}

// 4. Thêm hàm goToPage
function goToPageName(page) {
    if (pagination.goToPage(page)) {
        renderTable();
        renderPagination();
        document.querySelector('.table-card').scrollIntoView({ behavior: 'smooth' });
    }
}

// 5. Thêm setupPaginationListener
function setupPaginationListener() {
    document.getElementById('pageSize')?.addEventListener('change', function(e) {
        pagination.setPageSize(e.target.value);
        renderTable();
        renderPagination();
    });
}
```

## 🎯 Thứ tự ưu tiên:
1. contracts.html (hay dùng)
2. payments.html (hay dùng)
3. staff.html (hay dùng)
4. accounts.html (ít dùng hơn)

