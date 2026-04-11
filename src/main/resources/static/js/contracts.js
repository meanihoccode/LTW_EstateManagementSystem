let contractsData = [];
let currentPage = 0;
const pageSize = 8;
let userRole = ''; // Lưu quyền của user

document.addEventListener('DOMContentLoaded', function() {
    // Phải lấy role xong mới load dữ liệu và gán sự kiện
    fetchUserRole().then(() => {
        loadContracts(0);
        loadProperties();
        loadTenants();
        setupEventListeners();
    });
});

async function fetchUserRole() {
    try {
        const response = await fetch('/api/me/role');
        if (response.ok) {
            const data = await response.json();
            userRole = data.role || '';
        }
    } catch (error) {
        console.error('Lỗi lấy role:', error);
    }
}

// ==========================================
// 1. DATA FETCHING (PHÂN TRANG BACKEND)
// ==========================================
async function loadContracts(page = 0) {
    currentPage = page;
    const keyword = document.getElementById('searchInput').value.trim();
    const status = document.getElementById('filterStatus').value;

    try {
        const url = `/api/contracts/paged?page=${page}&size=${pageSize}&keyword=${encodeURIComponent(keyword)}&status=${encodeURIComponent(status)}`;

        const response = await fetch(url);
        if (!response.ok) throw new Error("Lỗi tải danh sách hợp đồng");

        const pageData = await response.json();

        if (Array.isArray(pageData)) {
            contractsData = pageData;
            renderRealPagination(1, 0);
        } else if (pageData && pageData.content) {
            contractsData = pageData.content;
            renderRealPagination(pageData.totalPages, pageData.number);
        } else {
            contractsData = [];
        }

        renderTable(contractsData);
    } catch (error) {
        console.error("Error loading contracts:", error);
        renderTable([]);
    }
}

async function loadProperties() {
    const select = document.getElementById('batDongSan');
    try {
        const response = await fetch('/api/properties');
        if (!response.ok) throw new Error("Failed to load properties");
        const properties = await response.json();

        while (select.options.length > 1) select.remove(1);
        properties.forEach(prop => {
            const option = document.createElement('option');
            option.value = prop.id;
            option.textContent = `[ID: ${prop.id}] ${prop.name} - ${prop.address}`;
            select.appendChild(option);
        });
    } catch (error) {
        console.error("Error loading properties:", error);
    }
}

async function loadTenants() {
    const select = document.getElementById('khachThue');
    try {
        const response = await fetch('/api/tenants');
        if (!response.ok) throw new Error("Failed to load tenants");

        const tenantsData = await response.json();
        const tenants = Array.isArray(tenantsData) ? tenantsData : (tenantsData.content || []);

        while (select.options.length > 1) select.remove(1);
        tenants.forEach(tenant => {
            const option = document.createElement('option');
            option.value = tenant.id;
            option.textContent = tenant.fullName;
            select.appendChild(option);
        });
    } catch (error) {
        console.error("Error loading tenants:", error);
    }
}

// ==========================================
// 2. RENDERING GIAO DIỆN
// ==========================================
function renderTable(dataToRender) {
    const tbody = document.getElementById('contractsTable');

    if (!dataToRender || dataToRender.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" style="text-align: center; color: #999;">Không có dữ liệu</td></tr>';
        return;
    }

    tbody.innerHTML = dataToRender.map(c => {
        let actions = `<button class="btn-small" onclick="editContract(${c.id})">Sửa</button>`;

        // Hiện nút Duyệt / Từ chối cho Admin/Quản lý đối với đơn Chờ duyệt
        if (userRole !== 'Nhân viên' && c.status === 'Chờ duyệt') {
            actions += `
                <button class="btn-small" style="background-color:#4CAF50;color:white;margin-left:5px;" onclick="quickApprove(${c.id}, 'Hiệu lực')">Duyệt</button>
                <button class="btn-small" style="background-color:#f44336;color:white;margin-left:5px;" onclick="quickApprove(${c.id}, 'Từ chối')">Từ chối</button>
            `;
        }

        return `
            <tr>
                <td>#${c.id}</td>
                <td>${c.property ? c.property.name : 'N/A'}</td>
                <td>${c.tenant ? c.tenant.fullName : 'N/A'}</td>
                <td>${c.startDate}</td>
                <td>${c.endDate}</td>
                <td>${c.deposit ? c.deposit.toLocaleString('vi-VN') : 0}Đ</td>
                <td><span class="badge ${getStatusClass(c.status)}">${c.status}</span></td>
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

    let prevHtml = `<button onclick="loadContracts(${current - 1})" ${current === 0 ? 'disabled' : ''}>&laquo;</button>`;
    container.insertAdjacentHTML('beforeend', prevHtml);

    for (let i = 0; i < totalPages; i++) {
        let activeClass = i === current ? 'active' : '';
        let pageHtml = `<button onclick="loadContracts(${i})" class="${activeClass}">${i + 1}</button>`;
        container.insertAdjacentHTML('beforeend', pageHtml);
    }

    let nextHtml = `<button onclick="loadContracts(${current + 1})" ${current === totalPages - 1 ? 'disabled' : ''}>&raquo;</button>`;
    container.insertAdjacentHTML('beforeend', nextHtml);
}

function getStatusClass(status) {
    switch(status) {
        case 'Hiệu lực': return 'active';
        case 'Kết thúc': case 'Từ chối': case 'Đã hủy': return 'inactive';
        case 'Chờ duyệt': return 'pending';
        default: return '';
    }
}

// ==========================================
// 3. NGHIỆP VỤ CRUD & SỰ KIỆN
// ==========================================
function setupEventListeners() {
    document.getElementById('addContractBtn')?.addEventListener('click', openModal);
    document.getElementById('closeModalBtn')?.addEventListener('click', closeModal);
    document.getElementById('cancelBtn')?.addEventListener('click', closeModal);
    document.getElementById('contractForm')?.addEventListener('submit', saveContract);

    document.getElementById('searchInput')?.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') filterTable();
    });
    document.getElementById('filterStatus')?.addEventListener('change', filterTable);

    window.addEventListener('click', function(event) {
        const modal = document.getElementById('contractModal');
        if (event.target === modal) closeModal();
    });
}

function filterTable() {
    loadContracts(0);
}

function openModal() {
    document.getElementById('modalTitle').textContent = 'Thêm Hợp Đồng';
    document.getElementById('contractForm').reset();
    delete document.getElementById('contractForm').dataset.editingId;

    // Khóa trạng thái nếu là Nhân viên
    const statusDropdown = document.getElementById('trangThai');
    if (userRole === 'Nhân viên') {
        statusDropdown.value = 'Chờ duyệt';
        statusDropdown.disabled = true;
    } else {
        statusDropdown.disabled = false;
    }

    document.getElementById('contractModal').style.display = 'block';
}

function closeModal() {
    document.getElementById('contractModal').style.display = 'none';
}

async function saveContract(e) {
    e.preventDefault();

    const editingId = document.getElementById('contractForm').dataset.editingId;
    const propertyId = document.getElementById('batDongSan').value;
    const tenantId = document.getElementById('khachThue').value;
    const startDate = document.getElementById('ngayBatDau').value;
    const endDate = document.getElementById('ngayKetThuc').value;
    const deposit = document.getElementById('tienCoc').value;

    // Nếu dropdown bị disabled, JS sẽ không lấy được value, nên phải fallback về "Chờ duyệt"
    const statusDropdown = document.getElementById('trangThai');
    const status = statusDropdown.disabled ? 'Chờ duyệt' : statusDropdown.value;

    if (!propertyId || !tenantId || !startDate || !endDate || !deposit || !status) {
        alert('Vui lòng điền đầy đủ thông tin');
        return;
    }

    if (new Date(endDate) <= new Date(startDate)) {
        alert('Ngày kết thúc phải sau ngày bắt đầu');
        return;
    }

    const contract = {
        id: editingId,
        propertyId: parseInt(propertyId),
        tenantId: parseInt(tenantId),
        startDate: startDate,
        endDate: endDate,
        deposit: parseFloat(deposit),
        status: status
    };

    try {
        const url = editingId ? `/api/contracts/${editingId}` : '/api/contracts';
        const method = editingId ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(contract)
        });

        // Xử lý lỗi trả về chuyên nghiệp
        if (!response.ok) {
            const errorText = await response.text();
            let errorMessage = "Đã xảy ra lỗi từ hệ thống!";
            try {
                errorMessage = JSON.parse(errorText).message || errorText;
            } catch (parseError) {
                errorMessage = errorText;
            }
            throw new Error(errorMessage);
        }

        alert((editingId ? 'Cập nhật' : 'Thêm') + ' hợp đồng thành công!');
        closeModal();
        loadContracts(currentPage);
    } catch (error) {
        console.error("Lỗi:", error);
        alert('Lỗi: ' + error.message);
    }
}

async function editContract(id) {
    try {
        const response = await fetch('/api/contracts/' + id);
        if (!response.ok) throw new Error("Lỗi tải chi tiết hợp đồng");

        const contract = await response.json();

        document.getElementById('batDongSan').value = contract.property ? contract.property.id : contract.propertyId;
        document.getElementById('khachThue').value = contract.tenant ? contract.tenant.id : contract.tenantId;
        document.getElementById('ngayBatDau').value = contract.startDate;
        document.getElementById('ngayKetThuc').value = contract.endDate;
        document.getElementById('tienCoc').value = contract.deposit;

        const statusDropdown = document.getElementById('trangThai');
        statusDropdown.value = contract.status;
        if (userRole === 'Nhân viên') {
            statusDropdown.disabled = true;
        } else {
            statusDropdown.disabled = false;
        }

        document.getElementById('contractForm').dataset.editingId = id;
        document.getElementById('modalTitle').textContent = 'Sửa Hợp Đồng';
        document.getElementById('contractModal').style.display = 'block';

    } catch (error) {
        console.error('Lỗi:', error);
        alert('Lỗi khi tải chi tiết hợp đồng.');
    }
}

// Hàm duyệt/từ chối nhanh ngay trên bảng
// Hàm duyệt/từ chối nhanh ngay trên bảng
async function quickApprove(id, newStatus) {
    if (!confirm(`Bạn có chắc chắn muốn ${newStatus === 'Hiệu lực' ? 'DUYỆT' : 'TỪ CHỐI'} hợp đồng này?`)) return;

    try {
        // 👉 SỬA URL Ở ĐÂY: Thêm /status vào cuối
        const response = await fetch(`/api/contracts/${id}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: newStatus })
        });

        if (!response.ok) {
            const errorText = await response.text();
            let errorMessage = "Đã xảy ra lỗi!";
            try { errorMessage = JSON.parse(errorText).message || errorText; } catch (e) { errorMessage = errorText; }
            throw new Error(errorMessage);
        }

        alert('Thao tác thành công!');
        loadContracts(currentPage);
    } catch (error) {
        alert('Lỗi: ' + error.message);
    }
}