let ownersData = [];
let userRole = '';
let currentPage = 0;
const pageSize = 8;

document.addEventListener('DOMContentLoaded', function() {
    fetchUserRole().then(() => {
        loadOwners(0);
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

async function loadOwners(page = 0) {
    currentPage = page;
    const keyword = document.getElementById('searchInput').value.trim();

    try {
        const url = `/api/owners/paged?page=${page}&size=${pageSize}&keyword=${encodeURIComponent(keyword)}`;

        const response = await fetch(url);
        if (!response.ok) throw new Error('Lỗi tải danh sách chủ sở hữu');

        const pageData = await response.json();

        // Kiểm tra an toàn xem Backend trả về chuẩn Page chưa
        if (Array.isArray(pageData)) {
            ownersData = pageData;
            renderRealPagination(1, 0);
        } else if (pageData && pageData.content) {
            ownersData = pageData.content;
            renderRealPagination(pageData.totalPages, pageData.number);
        } else {
            ownersData = [];
        }

        renderTable(ownersData);
    } catch (error) {
        console.error('Error: ', error);
        renderTable([]);
    }
}

// ==========================================
// 2. RENDERING GIAO DIỆN
// ==========================================
function renderTable(dataToRender) {
    const tbody = document.getElementById('ownersTable');

    if (!dataToRender || dataToRender.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: #999;">Không có dữ liệu</td></tr>';
        return;
    }

    tbody.innerHTML = dataToRender.map(o => {
        let actions = '';
        if (userRole !== 'Nhân viên') {
            actions = `<button class="btn-small" onclick="editOwner(${o.id})">Sửa</button>
                       <button class="btn-small btn-danger" onclick="deleteOwner(${o.id})">Xóa</button>`;
        }

        return `
            <tr>
                <td>#${o.id}</td>
                <td>${o.fullName || ''}</td>
                <td>${o.phone || ''}</td>
                <td>${o.email || ''}</td>
                <td>${o.address || ''}</td>
                <td>${actions || 'Không có'}</td>
            </tr>
        `;
    }).join('');
}

function renderRealPagination(totalPages, current) {
    const container = document.getElementById('paginationButtons');
    if (!container) return;
    container.innerHTML = '';

    if (totalPages <= 1) return;

    let prevHtml = `<button onclick="loadOwners(${current - 1})" ${current === 0 ? 'disabled' : ''}>&laquo;</button>`;
    container.insertAdjacentHTML('beforeend', prevHtml);

    for (let i = 0; i < totalPages; i++) {
        let activeClass = i === current ? 'active' : '';
        let pageHtml = `<button onclick="loadOwners(${i})" class="${activeClass}">${i + 1}</button>`;
        container.insertAdjacentHTML('beforeend', pageHtml);
    }

    let nextHtml = `<button onclick="loadOwners(${current + 1})" ${current === totalPages - 1 ? 'disabled' : ''}>&raquo;</button>`;
    container.insertAdjacentHTML('beforeend', nextHtml);
}

// ==========================================
// 3. NGHIỆP VỤ CRUD & SỰ KIỆN
// ==========================================
function setupEventListeners() {
    document.getElementById('addOwnerBtn')?.addEventListener('click', openModal);
    document.getElementById('closeModalBtn')?.addEventListener('click', closeModal);
    document.getElementById('cancelBtn')?.addEventListener('click', closeModal);
    document.getElementById('ownerForm')?.addEventListener('submit', saveOwner);

    // Gắn sự kiện nhấn Enter để tìm kiếm thay vì tìm lúc đang gõ (tiết kiệm API calls)
    document.getElementById('searchInput')?.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') filterTable();
    });

    window.addEventListener('click', function(event) {
        const modal = document.getElementById('ownerModal');
        if (event.target === modal) closeModal();
    });
}

function filterTable() {
    loadOwners(0); // Gọi API lọc từ Server và reset về trang 1
}

function openModal() {
    document.getElementById('modalTitle').textContent = 'Thêm Chủ Sở Hữu';
    document.getElementById('ownerForm').reset();
    delete document.getElementById('ownerForm').dataset.editingId;
    document.getElementById('ownerModal').style.display = 'block';
}

function closeModal() {
    document.getElementById('ownerModal').style.display = 'none';
}

async function saveOwner(e) {
    e.preventDefault();
    const editingId = document.getElementById('ownerForm').dataset.editingId;
    const owner = {
        fullName: document.getElementById('hoTen').value,
        phone: document.getElementById('soDienThoai').value,
        email: document.getElementById('email').value,
        address: document.getElementById('diaChi').value
    };

    if (editingId) {
        owner.id = parseInt(editingId);
    }

    if (!owner.fullName || !owner.phone || !owner.email || !owner.address) {
        alert('Vui lòng điền đầy đủ thông tin');
        return;
    }

    try {
        const url = editingId ? `/api/owners/${editingId}` : '/api/owners';
        const method = editingId ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(owner)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Lỗi khi lưu chủ sở hữu');
        }

        alert((editingId ? 'Cập nhật' : 'Thêm') + ' chủ sở hữu thành công');
        closeModal();
        loadOwners(currentPage); // Refresh đúng trang hiện tại
    } catch (error) {
        console.error('Error: ', error);
        alert('Lỗi: ' + error.message);
    }
}

async function editOwner(id) {
    if (userRole === 'Nhân viên') {
        alert('Bạn không có quyền sửa chủ sở hữu');
        return;
    }

    try {
        const response = await fetch(`/api/owners/${id}`);
        if (!response.ok) throw new Error('Failed to load owner details');

        const owner = await response.json();

        document.getElementById('modalTitle').textContent = 'Sửa Chủ Sở Hữu';
        document.getElementById('hoTen').value = owner.fullName || '';
        document.getElementById('soDienThoai').value = owner.phone || '';
        document.getElementById('email').value = owner.email || '';
        document.getElementById('diaChi').value = owner.address || '';

        document.getElementById('ownerForm').dataset.editingId = id;
        document.getElementById('ownerModal').style.display = 'block';
    } catch (error) {
        console.error('Error: ', error);
        alert('Lỗi khi tải thông tin chủ sở hữu.');
    }
}

async function deleteOwner(id) {
    if (!confirm('Bạn có chắc muốn xóa chủ sở hữu này?')) return;

    try {
        const response = await fetch(`/api/owners/${id}`, { method: 'DELETE' });
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Lỗi khi xóa chủ sở hữu');
        }

        alert('Xoá chủ sở hữu thành công');
        loadOwners(currentPage);
    } catch (error) {
        console.error('Error: ', error);
        alert('Lỗi: ' + error.message);
    }
}