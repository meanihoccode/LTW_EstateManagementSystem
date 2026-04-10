/**
 * Tệp JavaScript quản lý trang Bất Động Sản (Real Estate Management)
 * Tích hợp Server-side Pagination, Tìm kiếm và Phân quyền
 */

// ==========================================
// 1. BIẾN TOÀN CỤC (GLOBAL VARIABLES)
// ==========================================
let propertiesData = []; // Lưu data của trang hiện tại để dùng khi bấm "Sửa"
let currentPage = 0;
const pageSize = 8;      // Số lượng BĐS trên 1 trang
let userRole = '';
let myStaffId = null;

// ==========================================
// 2. KHỞI TẠO (INITIALIZATION)
// ==========================================
document.addEventListener('DOMContentLoaded', async function() {
    await fetchUserRole(); // Phải lấy role trước để biết quyền hạn
    loadProperties(0);     // Tải trang đầu tiên
    loadOwners();          // Tải dropdown chủ sở hữu
    loadStaffs();          // Tải dropdown nhân viên
    setupEventListeners(); // Gắn các sự kiện (click, enter...)
});

// ==========================================
// 3. LOGIC PHÂN QUYỀN & DATA FETCHING
// ==========================================
async function fetchUserRole() {
    try {
        const response = await fetch('/api/me/role'); // Đảm bảo bạn có API này hoặc thay bằng cách lấy từ token/session
        if (response.ok) {
            const data = await response.json();
            userRole = data.role || '';
            myStaffId = data.staffId || null;
        }
    } catch (error) {
        console.error('Lỗi khi lấy thông tin quyền người dùng:', error);
    }
}

async function loadProperties(page = 0) {
    currentPage = page;
    const keyword = document.getElementById('searchInput').value.trim();
    const status = document.getElementById('filterStatus').value;

    try {
        // Gắn Query Params để Spring Boot xử lý phân trang & tìm kiếm
        const url = `/api/properties?page=${page}&size=${pageSize}&keyword=${encodeURIComponent(keyword)}&status=${encodeURIComponent(status)}`;

        const response = await fetch(url);
        if (!response.ok) throw new Error('Lỗi tải danh sách bất động sản');

        const pageData = await response.json();

        propertiesData = pageData.content; // Dữ liệu BĐS nằm trong field 'content' của Spring Data JPA

        renderTable(propertiesData);
        renderRealPagination(pageData.totalPages, pageData.number);
    } catch (error) {
        console.error('Error:', error);
        renderTable([]); // Render bảng rỗng nếu lỗi
    }
}

async function loadOwners() {
    const ownerSelect = document.getElementById('chuSoHuu');
    if (!ownerSelect) return;

    try {
        const response = await fetch('/api/owners');
        if (!response.ok) throw new Error('Lỗi tải chủ sở hữu');

        const ownersData = await response.json();

        // Giữ lại option đầu tiên (placeholder)
        while (ownerSelect.options.length > 1) ownerSelect.remove(1);

        ownersData.forEach(owner => {
            const option = document.createElement('option');
            option.value = owner.id;
            option.textContent = owner.fullName;
            ownerSelect.appendChild(option);
        });
    } catch (error) {
        console.error('Error:', error);
    }
}

async function loadStaffs() {
    const staffSelect = document.getElementById('nhanVienQuanLy');
    if (!staffSelect) return;

    try {
        const response = await fetch('/api/staffs');
        if (!response.ok) throw new Error('Lỗi tải nhân viên');

        const staffsData = await response.json();

        while (staffSelect.options.length > 1) staffSelect.remove(1);

        staffsData.forEach(staff => {
            const option = document.createElement('option');
            option.value = staff.id;
            option.textContent = staff.fullName;
            staffSelect.appendChild(option);
        });
    } catch (error) {
        console.error('Error:', error);
    }
}

// ==========================================
// 4. HIỂN THỊ GIAO DIỆN (RENDERING)
// ==========================================
function renderTable(dataToRender) {
    const tbody = document.getElementById('propertiesTable');
    if (!tbody) return;

    if (!dataToRender || dataToRender.length === 0) {
        const colspan = userRole === 'Nhân viên' ? 9 : 10;
        tbody.innerHTML = `<tr><td colspan="${colspan}" style="text-align: center; color: #999;">Không tìm thấy dữ liệu</td></tr>`;
        return;
    }

    tbody.innerHTML = dataToRender.map(p => {
        const staffColumn = userRole === 'Nhân viên' ? '' : `<td>${p.staff ? p.staff.fullName : 'N/A'}</td>`;
        return `
            <tr>
                <td>#${p.id}</td>
                <td>${p.name}</td>
                <td>${p.type}</td>
                <td>${p.address}</td>
                <td>${p.area} m²</td>
                <td>${p.rentalPrice.toLocaleString('vi-VN')} Đ</td>
                <td><span class="badge ${getStatusClass(p.status)}">${p.status}</span></td>
                <td>${p.owner ? p.owner.fullName : 'N/A'}</td>
                ${staffColumn}
                <td>${buildActions(p)}</td>
            </tr>
        `;
    }).join('');
}

function getStatusClass(status) {
    switch(status) {
        case 'Cho thuê': return 'rented';
        case 'Trống': return 'empty';
        case 'Bảo trì': return 'maintenance';
        default: return '';
    }
}

function buildActions(p) {
    if (userRole !== 'Nhân viên') {
        // Admin/Quản lý thấy đủ nút
        return `<button class="btn-small" onclick="editProperty(${p.id})">Sửa</button>
                <button class="btn-small btn-danger" onclick="deleteProperty(${p.id})">Xóa</button>`;
    }
    // Nhân viên: chỉ thấy nút Sửa nếu BĐS do mình quản lý
    if (p.staff && p.staff.id === myStaffId) {
        return `<button class="btn-small" onclick="editProperty(${p.id})">Sửa</button>`;
    }
    return '';
}

function renderRealPagination(totalPages, current) {
    const container = document.getElementById('paginationButtons');
    if (!container) return;
    container.innerHTML = '';

    if (totalPages <= 1) return;

    // Nút Previous
    let prevHtml = `<button onclick="loadProperties(${current - 1})" ${current === 0 ? 'disabled' : ''}>&laquo;</button>`;
    container.insertAdjacentHTML('beforeend', prevHtml);

    // Các trang số
    for (let i = 0; i < totalPages; i++) {
        let activeClass = i === current ? 'active' : '';
        let pageHtml = `<button onclick="loadProperties(${i})" class="${activeClass}">${i + 1}</button>`;
        container.insertAdjacentHTML('beforeend', pageHtml);
    }

    // Nút Next
    let nextHtml = `<button onclick="loadProperties(${current + 1})" ${current === totalPages - 1 ? 'disabled' : ''}>&raquo;</button>`;
    container.insertAdjacentHTML('beforeend', nextHtml);
}

// ==========================================
// 5. NGHIỆP VỤ CRUD (CREATE, UPDATE, DELETE)
// ==========================================
async function saveProperty(e) {
    e.preventDefault();
    const form = document.getElementById('propertyForm');
    const editingId = form.dataset.editingId;

    // Nhân viên tự dùng staffId của mình, không cho chọn
    const staffIdValue = userRole === 'Nhân viên'
        ? myStaffId
        : parseInt(document.getElementById('nhanVienQuanLy').value);

    const formData = {
        name: document.getElementById('tenBDS').value,
        type: document.getElementById('loaiBDS').value,
        address: document.getElementById('diaChi').value,
        area: parseFloat(document.getElementById('dienTich').value),
        rentalPrice: parseFloat(document.getElementById('giaThue').value),
        status: document.getElementById('trangThai').value,
        ownerId: parseInt(document.getElementById('chuSoHuu').value),
        staffId: staffIdValue
    };

    if (!formData.name || !formData.address || !formData.ownerId || !formData.type ||
        !formData.area || !formData.rentalPrice || !formData.status || !formData.staffId) {
        alert('Vui lòng điền đầy đủ thông tin');
        return;
    }

    try {
        const url = editingId ? `/api/properties/${editingId}` : '/api/properties';
        const method = editingId ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(formData)
        });

        if (!response.ok) throw new Error(await response.text());

        alert((editingId ? 'Cập nhật' : 'Thêm') + ' bất động sản thành công');
        closeModal();
        loadProperties(currentPage); // Tải lại trang hiện tại sau khi save
    } catch (error) {
        console.error('Error:', error);
        alert('Lỗi: ' + error.message);
    }
}

async function editProperty(id) {
    if (userRole === 'Nhân viên') {
        const p = propertiesData.find(x => x.id === id);
        if (!p || !p.staff || p.staff.id !== myStaffId) {
            alert('Bạn chỉ có thể sửa BĐS do mình quản lý');
            return;
        }
    }

    try {
        const response = await fetch(`/api/properties/${id}`);
        if (!response.ok) throw new Error('Không thể tải chi tiết BĐS');
        const property = await response.json();

        document.getElementById('modalTitle').textContent = 'Sửa Bất Động Sản';
        document.getElementById('tenBDS').value = property.name;
        document.getElementById('loaiBDS').value = property.type;
        document.getElementById('diaChi').value = property.address;
        document.getElementById('dienTich').value = property.area;
        document.getElementById('giaThue').value = property.rentalPrice;
        document.getElementById('trangThai').value = property.status;
        document.getElementById('chuSoHuu').value = property.owner ? property.owner.id : '';

        if (userRole === 'Nhân viên') {
            document.getElementById('staffRow').style.display = 'none';
            document.getElementById('nhanVienQuanLy').value = myStaffId;
        } else {
            document.getElementById('staffRow').style.display = '';
            document.getElementById('nhanVienQuanLy').value = property.staff ? property.staff.id : '';
        }

        document.getElementById('propertyForm').dataset.editingId = id;
        document.getElementById('propertyModal').style.display = 'block';
    } catch (error) {
        console.error('Error:', error);
        alert('Lỗi: ' + error.message);
    }
}

async function deleteProperty(id) {
    if (!confirm('Bạn có chắc muốn xóa bất động sản này?')) return;

    try {
        const response = await fetch(`/api/properties/${id}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Không thể xóa BĐS');

        alert('Xóa thành công!');
        loadProperties(currentPage); // Tải lại trang hiện tại
    } catch (error) {
        console.error('Error:', error);
        alert('Lỗi: ' + error.message);
    }
}

// ==========================================
// 6. QUẢN LÝ SỰ KIỆN & MODAL (EVENTS & UI)
// ==========================================
function setupEventListeners() {
    // Nút thêm mới
    document.getElementById('addPropertyBtn')?.addEventListener('click', openModal);

    // Đóng Modal
    document.getElementById('closeModalBtn')?.addEventListener('click', closeModal);
    document.getElementById('cancelBtn')?.addEventListener('click', closeModal);

    // Submit form
    document.getElementById('propertyForm')?.addEventListener('submit', saveProperty);

    // Tìm kiếm (nhấn Enter) và Lọc
    document.getElementById('searchInput')?.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') filterTable();
    });
    document.getElementById('filterStatus')?.addEventListener('change', filterTable);

    // Đóng modal khi click ra ngoài
    window.addEventListener('click', function(event) {
        const modal = document.getElementById('propertyModal');
        if (event.target === modal) closeModal();
    });
}

function filterTable() {
    // Khi lọc/tìm kiếm, luôn đưa về trang đầu tiên (trang 0)
    loadProperties(0);
}

function openModal() {
    if (userRole === 'Nhân viên') {
        alert('Bạn không có quyền thêm mới bất động sản');
        return;
    }
    document.getElementById('modalTitle').textContent = 'Thêm Bất Động Sản';
    document.getElementById('propertyForm').reset();
    delete document.getElementById('propertyForm').dataset.editingId;

    // Admin/Quản lý mới thấy phần chọn nhân viên
    const staffRow = document.getElementById('staffRow');
    if(staffRow) staffRow.style.display = '';

    document.getElementById('propertyModal').style.display = 'block';
}

function closeModal() {
    const modal = document.getElementById('propertyModal');
    if (modal) modal.style.display = 'none';
}