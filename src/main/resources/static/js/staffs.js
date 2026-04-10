let staffData = [];
let currentPage = 0;
const pageSize = 8;

document.addEventListener('DOMContentLoaded', function() {
    loadStaff(0);
    setupEventListeners();
});

// ==========================================
// 1. DATA FETCHING & PHÂN TRANG BACKEND
// ==========================================
async function loadStaff(page = 0) {
    currentPage = page;
    const keyword = document.getElementById('searchInput').value.trim();

    try {
        const url = `/api/staffs/paged?page=${page}&size=${pageSize}&keyword=${encodeURIComponent(keyword)}`;

        const response = await fetch(url);
        if (!response.ok) throw new Error('Lỗi tải danh sách nhân viên');

        const pageData = await response.json();

        // Kiểm tra an toàn dữ liệu
        if (Array.isArray(pageData)) {
            staffData = pageData;
            renderRealPagination(1, 0);
        } else if (pageData && pageData.content) {
            staffData = pageData.content;
            renderRealPagination(pageData.totalPages, pageData.number);
        } else {
            staffData = [];
        }

        renderTable(staffData);
    } catch (error) {
        console.error('Error:', error);
        renderTable([]);
    }
}

// ==========================================
// 2. RENDERING GIAO DIỆN
// ==========================================
function renderTable(dataToRender) {
    const tbody = document.getElementById('staffTable');

    if (!dataToRender || dataToRender.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: #999;">Không có dữ liệu</td></tr>';
        return;
    }

    tbody.innerHTML = dataToRender.map(s => `
        <tr>
            <td>#${s.id}</td>
            <td>${s.fullName || ''}</td>
            <td>${s.phone || ''}</td>
            <td>${s.role || ''}</td>
            <td>
                <button class="btn-small" onclick="editStaff(${s.id})">Sửa</button>
                <button class="btn-small btn-danger" onclick="deleteStaff(${s.id})">Xóa</button>
            </td>
        </tr>
    `).join('');
}

function renderRealPagination(totalPages, current) {
    const container = document.getElementById('paginationButtons');
    if (!container) return;
    container.innerHTML = '';

    if (totalPages <= 1) return;

    let prevHtml = `<button onclick="loadStaff(${current - 1})" ${current === 0 ? 'disabled' : ''}>&laquo;</button>`;
    container.insertAdjacentHTML('beforeend', prevHtml);

    for (let i = 0; i < totalPages; i++) {
        let activeClass = i === current ? 'active' : '';
        let pageHtml = `<button onclick="loadStaff(${i})" class="${activeClass}">${i + 1}</button>`;
        container.insertAdjacentHTML('beforeend', pageHtml);
    }

    let nextHtml = `<button onclick="loadStaff(${current + 1})" ${current === totalPages - 1 ? 'disabled' : ''}>&raquo;</button>`;
    container.insertAdjacentHTML('beforeend', nextHtml);
}

// ==========================================
// 3. NGHIỆP VỤ CRUD & SỰ KIỆN
// ==========================================
function setupEventListeners() {
    document.getElementById('addStaffBtn')?.addEventListener('click', openModal);
    document.getElementById('closeModalBtn')?.addEventListener('click', closeModal);
    document.getElementById('cancelBtn')?.addEventListener('click', closeModal);
    document.getElementById('staffForm')?.addEventListener('submit', saveStaff);

    // Sử dụng Enter để tìm kiếm (tiết kiệm gọi API)
    document.getElementById('searchInput')?.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') filterTable();
    });

    window.addEventListener('click', function(event) {
        const modal = document.getElementById('staffModal');
        if (event.target === modal) closeModal();
    });
}

function filterTable() {
    loadStaff(0); // Lọc xong reset về trang đầu tiên
}

function autoSetQuyenHan(vaiTro) {
    const quyen = vaiTro.startsWith('Nhân viên') ? 'Nhân viên' : vaiTro;
    document.getElementById('quyenHan').value = quyen;
}

function openModal() {
    document.getElementById('modalTitle').textContent = 'Thêm Nhân Viên';
    document.getElementById('staffForm').reset();
    delete document.getElementById('staffForm').dataset.editingId;
    autoSetQuyenHan(document.getElementById('vaiTro').value);
    document.getElementById('staffModal').style.display = 'block';
}

function closeModal() {
    document.getElementById('staffModal').style.display = 'none';
}

async function saveStaff(e) {
    e.preventDefault();
    const editingId = document.getElementById('staffForm').dataset.editingId;
    const staffData_form = {
        fullName: document.getElementById('hoTen').value,
        phone: document.getElementById('soDienThoai').value,
        role: document.getElementById('vaiTro').value,
        quyenHan: document.getElementById('quyenHan').value
    };

    if (!staffData_form.fullName || !staffData_form.phone) {
        alert('Vui lòng điền đầy đủ thông tin');
        return;
    }

    try {
        const url = editingId ? `/api/staffs/${editingId}` : '/api/staffs';
        const method = editingId ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(staffData_form)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Lỗi khi lưu nhân viên');
        }

        alert((editingId ? 'Cập nhật' : 'Thêm') + ' nhân viên thành công');
        closeModal();
        loadStaff(currentPage);
    } catch (error) {
        console.error('Error:', error);
        alert('Lỗi: ' + error.message);
    }
}

async function editStaff(id) {
    try {
        const response = await fetch(`/api/staffs/${id}`);
        if (!response.ok) throw new Error('Failed to load staff details');

        const staff = await response.json();

        document.getElementById('modalTitle').textContent = 'Sửa Nhân Viên';
        document.getElementById('hoTen').value = staff.fullName || '';
        document.getElementById('soDienThoai').value = staff.phone || '';
        document.getElementById('vaiTro').value = staff.role || '';
        document.getElementById('quyenHan').value = staff.account ? staff.account.role : 'Nhân viên';

        document.getElementById('staffForm').dataset.editingId = id;
        document.getElementById('staffModal').style.display = 'block';
    } catch (error) {
        console.error('Error:', error);
        alert('Lỗi tải thông tin nhân viên.');
    }
}

async function deleteStaff(id) {
    if (!confirm('Bạn có chắc muốn xóa nhân viên này?')) return;

    try {
        const response = await fetch(`/api/staffs/${id}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Failed to delete staff');

        alert('Xóa nhân viên thành công');
        loadStaff(currentPage);
    } catch (error) {
        console.error('Error:', error);
        alert('Lỗi khi xóa nhân viên.');
    }
}