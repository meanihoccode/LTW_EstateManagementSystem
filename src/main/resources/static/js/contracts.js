let contractsData = [];
let currentPage = 0;
const pageSize = 8; // Cố định 8 đơn 1 trang

document.addEventListener('DOMContentLoaded', function() {
    loadContracts(0);
    loadProperties();
    loadTenants();
    setupEventListeners();
});

// ==========================================
// 1. DATA FETCHING & PHÂN TRANG BACKEND
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

        // Kiểm tra an toàn xem Backend trả về chuẩn Page chưa
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

// Load dropdown BĐS
async function loadProperties() {
    const select = document.getElementById('batDongSan');
    try {
        // Bây giờ API /api/properties đã trả về List đầy đủ như cũ
        const response = await fetch('/api/properties');
        if (!response.ok) throw new Error("Failed to load properties");

        const properties = await response.json();

        while (select.options.length > 1) select.remove(1);

        properties.forEach(prop => {
            // 👉 Nếu bạn MÚỐN HIỆN TẤT CẢ BĐS kể cả đang cho thuê, hãy bỏ dòng if(...) đi.
            // Nghiệp vụ chuẩn: Chỉ hiện BĐS "Trống"
            if (prop.status === 'Trống') {
                const option = document.createElement('option');
                option.value = prop.id;
                // Hiển thị ID để dễ phân biệt
                option.textContent = `[ID: ${prop.id}] ${prop.name} - ${prop.address}`;
                select.appendChild(option);
            }
        });
    } catch (error) {
        console.error("Error loading properties:", error);
    }
}

// Load dropdown Khách thuê
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

    tbody.innerHTML = dataToRender.map(c => `
        <tr>
            <td>#${c.id}</td>
            <td>${c.property ? c.property.name : 'N/A'}</td>
            <td>${c.tenant ? c.tenant.fullName : 'N/A'}</td>
            <td>${c.startDate}</td>
            <td>${c.endDate}</td>
            <td>${c.deposit ? c.deposit.toLocaleString('vi-VN') : 0}Đ</td>
            <td><span class="badge ${getStatusClass(c.status)}">${c.status}</span></td>
            <td>
                <button class="btn-small" onclick="editContract(${c.id})">Sửa</button>
                <button class="btn-small btn-danger" onclick="deleteContract(${c.id})">Xóa</button>
            </td>
        </tr>
    `).join('');
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
        case 'Kết thúc': return 'inactive';
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

    // Dùng keypress thay vì keyup để tránh gọi API liên tục
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
    loadContracts(0); // Lọc xong luôn quay về trang 1
}

function openModal() {
    document.getElementById('modalTitle').textContent = 'Thêm Hợp Đồng';
    document.getElementById('contractForm').reset();
    delete document.getElementById('contractForm').dataset.editingId;
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
    const status = document.getElementById('trangThai').value;

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
        const url = editingId ? '/api/contracts/' + editingId : '/api/contracts';
        const method = editingId ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(contract)
        });

        if (!response.ok) throw new Error(await response.text());

        alert((editingId ? 'Cập nhật' : 'Thêm') + ' hợp đồng thành công!');
        closeModal();
        loadContracts(currentPage); // Refresh lại trang hiện tại
    } catch (error) {
        console.error("Error saving contract:", error);
        alert('Lỗi: ' + error.message);
    }
}

async function editContract(id) {
    try {
        const response = await fetch('/api/contracts/' + id);
        if (!response.ok) throw new Error("Failed to load contract details");

        const contract = await response.json();

        // Kiểm tra xem backend trả về contract.property.id hay contract.propertyId
        document.getElementById('batDongSan').value = contract.property ? contract.property.id : contract.propertyId;
        document.getElementById('khachThue').value = contract.tenant ? contract.tenant.id : contract.tenantId;
        document.getElementById('ngayBatDau').value = contract.startDate;
        document.getElementById('ngayKetThuc').value = contract.endDate;
        document.getElementById('tienCoc').value = contract.deposit;
        document.getElementById('trangThai').value = contract.status;

        document.getElementById('contractForm').dataset.editingId = id;
        document.getElementById('modalTitle').textContent = 'Sửa Hợp Đồng';
        document.getElementById('contractModal').style.display = 'block';

    } catch (error) {
        console.error('Error:', error);
        alert('Lỗi khi tải chi tiết hợp đồng.');
    }
}

async function deleteContract(id) {
    if (!confirm('Bạn có chắc muốn xóa hợp đồng này?')) return;

    try {
        const response = await fetch('/api/contracts/' + id, { method: 'DELETE' });
        if (!response.ok) throw new Error("Failed to delete contract");

        alert('Xóa thành công!');
        loadContracts(currentPage);
    } catch (error) {
        console.error("Error: ", error);
        alert('Lỗi khi xóa hợp đồng.');
    }
}