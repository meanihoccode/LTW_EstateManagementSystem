let tenantsData = [];
let userRole = '';
let currentPage = 0;
const pageSize = 8;

document.addEventListener('DOMContentLoaded', function() {
    fetchUserRole().then(() => {
        loadTenants(0);
        setupEventListeners();
    });
});

// ==========================================
// 1. DATA FETCHING & PHÂN TRANG BACKEND
// ==========================================
async function fetchUserRole() {
    try {
        const response = await fetch('/api/me/role');
        if (!response.ok) return;
        const data = await response.json();
        userRole = data.role || '';
    } catch (error) {
        console.error('Error fetching user role:', error);
    }
}

async function loadTenants(page = 0) {
    currentPage = page;
    const keyword = document.getElementById('searchInput').value.trim();

    try {
        // Sử dụng API phân trang mới
        const url = `/api/tenants/paged?page=${page}&size=${pageSize}&keyword=${encodeURIComponent(keyword)}`;

        const response = await fetch(url);
        if (!response.ok) throw new Error('Failed to load tenants');

        const pageData = await response.json();

        // Kiểm tra an toàn
        if (Array.isArray(pageData)) {
            tenantsData = pageData;
            renderRealPagination(1, 0);
        } else if (pageData && pageData.content) {
            tenantsData = pageData.content;
            renderRealPagination(pageData.totalPages, pageData.number);
        } else {
            tenantsData = [];
        }

        renderTable(tenantsData);
    } catch (error) {
        console.error('Error: ', error);
        renderTable([]);
    }
}

// ==========================================
// 2. RENDERING GIAO DIỆN
// ==========================================
function renderTable(dataToRender) {
    const tbody = document.getElementById('tenantsTable');

    if (!dataToRender || dataToRender.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: #999;">Không có dữ liệu</td></tr>';
        return;
    }

    tbody.innerHTML = dataToRender.map(t => {
        let actions = `<button class="btn-small" onclick="editTenant(${t.id})">Sửa</button>`;
        if (userRole !== 'Nhân viên') {
            actions += `<button class="btn-small btn-danger" onclick="deleteTenant(${t.id})">Xóa</button>`;
        }

        return `
            <tr>
                <td>#${t.id}</td>
                <td>${t.fullName || ''}</td>
                <td>${t.idNumber || ''}</td>
                <td>${t.phone || ''}</td>
                <td>${t.email || ''}</td>
                <td>${actions}</td>
            </tr>
        `;
    }).join('');
}

function renderRealPagination(totalPages, current) {
    const container = document.getElementById('paginationButtons');
    if (!container) return;
    container.innerHTML = '';

    if (totalPages <= 1) return;

    let prevHtml = `<button onclick="loadTenants(${current - 1})" ${current === 0 ? 'disabled' : ''}>&laquo;</button>`;
    container.insertAdjacentHTML('beforeend', prevHtml);

    for (let i = 0; i < totalPages; i++) {
        let activeClass = i === current ? 'active' : '';
        let pageHtml = `<button onclick="loadTenants(${i})" class="${activeClass}">${i + 1}</button>`;
        container.insertAdjacentHTML('beforeend', pageHtml);
    }

    let nextHtml = `<button onclick="loadTenants(${current + 1})" ${current === totalPages - 1 ? 'disabled' : ''}>&raquo;</button>`;
    container.insertAdjacentHTML('beforeend', nextHtml);
}

// ==========================================
// 3. NGHIỆP VỤ CRUD & SỰ KIỆN
// ==========================================
function setupEventListeners() {
    document.getElementById('addTenantBtn')?.addEventListener('click', openModal);
    document.getElementById('closeModalBtn')?.addEventListener('click', closeModal);
    document.getElementById('cancelBtn')?.addEventListener('click', closeModal);
    document.getElementById('tenantForm')?.addEventListener('submit', saveTenant);

    // Gắn sự kiện nhấn Enter để tìm kiếm
    document.getElementById('searchInput')?.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') filterTable();
    });

    window.addEventListener('click', function(event) {
        const modal = document.getElementById('tenantModal');
        if (event.target === modal) closeModal();
    });
}

function filterTable() {
    loadTenants(0); // Lọc sẽ tự động gọi API và quay về trang đầu
}

function openModal() {
    document.getElementById('modalTitle').textContent = 'Thêm Khách Thuê';
    document.getElementById('tenantForm').reset();
    delete document.getElementById('tenantForm').dataset.editingId;
    document.getElementById('tenantModal').style.display = 'block';
}

function closeModal() {
    document.getElementById('tenantModal').style.display = 'none';
}

async function saveTenant(e) {
    e.preventDefault();
    const editingId = document.getElementById('tenantForm').dataset.editingId;

    const tenant = {
        fullName: document.getElementById('hoTen').value,
        idNumber: document.getElementById('soCCCD').value,
        phone: document.getElementById('soDienThoai').value,
        email: document.getElementById('email').value
    };

    if (!tenant.fullName || !tenant.idNumber || !tenant.phone || !tenant.email) {
        alert('Vui lòng điền đầy đủ thông tin');
        return;
    }

    try {
        const url = editingId ? `/api/tenants/${editingId}` : '/api/tenants';
        const method = editingId ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(tenant)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Lỗi khi lưu khách thuê');
        }

        alert((editingId ? 'Cập nhật' : 'Thêm') + ' khách thuê thành công');
        closeModal();
        loadTenants(currentPage); // Tải lại trang hiện tại sau khi save
    } catch (error) {
        console.error('Error: ', error);
        alert('Lỗi: ' + error.message);
    }
}

async function editTenant(id) {
    try {
        const response = await fetch(`/api/tenants/${id}`);
        if (!response.ok) throw new Error('Failed to load tenant details');

        const tenant = await response.json();

        document.getElementById('hoTen').value = tenant.fullName || '';
        document.getElementById('soCCCD').value = tenant.idNumber || '';
        document.getElementById('soDienThoai').value = tenant.phone || '';
        document.getElementById('email').value = tenant.email || '';

        document.getElementById('tenantForm').dataset.editingId = id;
        document.getElementById('modalTitle').textContent = 'Sửa Khách Thuê';
        document.getElementById('tenantModal').style.display = 'block';
    } catch (error) {
        console.error('Error: ', error);
        alert('Lỗi khi tải thông tin khách thuê.');
    }
}

async function deleteTenant(id) {
    if (!confirm('Bạn có chắc muốn xóa khách thuê này?')) return;

    try {
        const response = await fetch(`/api/tenants/${id}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Failed to delete tenant');

        alert('Xóa khách thuê thành công!');
        loadTenants(currentPage);
    } catch (error) {
        console.error('Error:', error);
        alert('Lỗi khi xóa khách thuê.');
    }
}