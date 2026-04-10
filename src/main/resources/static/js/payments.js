let paymentsData = [];
let currentPage = 0;
const pageSize = 8;

document.addEventListener('DOMContentLoaded', function() {
    loadPayments(0);
    loadContracts();
    setupEventListeners();
});

// ==========================================
// 1. DATA FETCHING & PHÂN TRANG BACKEND
// ==========================================
async function loadPayments(page = 0) {
    currentPage = page;
    const keyword = document.getElementById('searchInput').value.trim();
    const status = document.getElementById('filterStatus').value;

    try {
        const url = `/api/payments/paged?page=${page}&size=${pageSize}&keyword=${encodeURIComponent(keyword)}&status=${encodeURIComponent(status)}`;

        const response = await fetch(url);
        if (!response.ok) throw new Error('Lỗi tải danh sách thanh toán');

        const pageData = await response.json();

        // Kiểm tra an toàn dữ liệu trả về
        if (Array.isArray(pageData)) {
            paymentsData = pageData;
            renderRealPagination(1, 0);
        } else if (pageData && pageData.content) {
            paymentsData = pageData.content;
            renderRealPagination(pageData.totalPages, pageData.number);
        } else {
            paymentsData = [];
        }

        renderTable(paymentsData);
    } catch (error) {
        console.error('Error: ', error);
        renderTable([]);
    }
}

async function loadContracts() {
    const select = document.getElementById('hopDong');
    try {
        // Dùng API gốc không phân trang để lấy List cho dropdown
        const response = await fetch('/api/contracts');
        if (!response.ok) throw new Error('Lỗi tải hợp đồng');

        const contractsData = await response.json();
        const contracts = Array.isArray(contractsData) ? contractsData : (contractsData.content || []);

        while (select.options.length > 1) select.remove(1);

        contracts.forEach(contract => {
            const option = document.createElement('option');
            option.value = contract.id;
            option.textContent = `Hợp đồng #${contract.id} - BĐS: ${contract.property ? contract.property.name : ''}`;
            select.appendChild(option);
        });
    } catch (error) {
        console.error('Error: ', error);
    }
}

// ==========================================
// 2. RENDERING GIAO DIỆN
// ==========================================
function renderTable(dataToRender) {
    const tbody = document.getElementById('paymentsTable');

    if (!dataToRender || dataToRender.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: #999;">Không có dữ liệu</td></tr>';
        return;
    }

    tbody.innerHTML = dataToRender.map(p => {
        const contractId = p.contract ? p.contract.id : '';
        return `
        <tr>
            <td>#${p.id}</td>
            <td>${contractId ? `Hợp đồng #${contractId}` : '-'}</td>
            <td>${p.paymentDate || ''}</td>
            <td>${p.amount ? p.amount.toLocaleString('vi-VN') : 0}Đ</td>
            <td>${p.method || ''}</td>
            <td><span class="badge ${getStatusClass(p.status)}">${p.status || ''}</span></td>
            <td>
                <button class="btn-small" onclick="editPayment(${p.id})">Sửa</button>
                <button class="btn-small btn-danger" onclick="deletePayment(${p.id})">Xóa</button>
            </td>
        </tr>
    `;
    }).join('');
}

function renderRealPagination(totalPages, current) {
    const container = document.getElementById('paginationButtons');
    if (!container) return;
    container.innerHTML = '';

    if (totalPages <= 1) return;

    let prevHtml = `<button onclick="loadPayments(${current - 1})" ${current === 0 ? 'disabled' : ''}>&laquo;</button>`;
    container.insertAdjacentHTML('beforeend', prevHtml);

    for (let i = 0; i < totalPages; i++) {
        let activeClass = i === current ? 'active' : '';
        let pageHtml = `<button onclick="loadPayments(${i})" class="${activeClass}">${i + 1}</button>`;
        container.insertAdjacentHTML('beforeend', pageHtml);
    }

    let nextHtml = `<button onclick="loadPayments(${current + 1})" ${current === totalPages - 1 ? 'disabled' : ''}>&raquo;</button>`;
    container.insertAdjacentHTML('beforeend', nextHtml);
}

function getStatusClass(status) {
    switch(status) {
        case 'Đã duyệt': return 'active';
        case 'Chờ duyệt': return 'pending';
        case 'Từ chối': return 'inactive';
        default: return '';
    }
}

// ==========================================
// 3. NGHIỆP VỤ CRUD & SỰ KIỆN
// ==========================================
function setupEventListeners() {
    document.getElementById('addPaymentBtn')?.addEventListener('click', openModal);
    document.getElementById('closeModalBtn')?.addEventListener('click', closeModal);
    document.getElementById('cancelBtn')?.addEventListener('click', closeModal);
    document.getElementById('paymentForm')?.addEventListener('submit', savePayment);

    // Gắn event keypress (Enter) thay vì input để giảm tải server
    document.getElementById('searchInput')?.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') filterTable();
    });
    document.getElementById('filterStatus')?.addEventListener('change', filterTable);

    window.addEventListener('click', function(event) {
        const modal = document.getElementById('paymentModal');
        if (event.target === modal) closeModal();
    });
}

function filterTable() {
    loadPayments(0);
}

function openModal() {
    document.getElementById('modalTitle').textContent = 'Thêm Thanh Toán';
    document.getElementById('paymentForm').reset();
    delete document.getElementById('paymentForm').dataset.editingId;
    document.getElementById('paymentModal').style.display = 'block';
}

function closeModal() {
    document.getElementById('paymentModal').style.display = 'none';
}

async function savePayment(e) {
    e.preventDefault();

    const editingId = document.getElementById('paymentForm').dataset.editingId;
    const contractId = document.getElementById('hopDong').value;
    const paymentDate = document.getElementById('ngayThanhToan').value;
    const amount = document.getElementById('soTien').value;
    const method = document.getElementById('phuongThuc').value;
    const status = document.getElementById('trangThai').value;

    if (!contractId || !paymentDate || !amount || !method || !status) {
        alert('Vui lòng điền đầy đủ thông tin');
        return;
    }

    const payment = {
        id: editingId,
        contractId: parseInt(contractId),
        paymentDate: paymentDate,
        amount: parseFloat(amount),
        method: method,
        status: status
    };

    try {
        const url = editingId ? '/api/payments/' + editingId : '/api/payments';
        const httpMethod = editingId ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: httpMethod,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payment)
        });

        if (!response.ok) throw new Error(await response.text());

        alert((editingId ? 'Cập nhật' : 'Thêm') + ' thanh toán thành công!');
        closeModal();
        loadPayments(currentPage);
    } catch (error) {
        console.error('Error saving payment:', error);
        alert('Lỗi: ' + error.message);
    }
}

async function editPayment(id) {
    try {
        const response = await fetch('/api/payments/' + id);
        if (!response.ok) throw new Error('Failed to load payment details');

        const payment = await response.json();

        document.getElementById('hopDong').value = payment.contract ? payment.contract.id : payment.contractId;
        document.getElementById('ngayThanhToan').value = payment.paymentDate || '';
        document.getElementById('soTien').value = payment.amount || '';
        document.getElementById('phuongThuc').value = payment.method || '';
        document.getElementById('trangThai').value = payment.status || '';

        document.getElementById('paymentForm').dataset.editingId = id;
        document.getElementById('modalTitle').textContent = 'Sửa Thanh Toán';
        document.getElementById('paymentModal').style.display = 'block';
    } catch (error) {
        console.error('Error:', error);
        alert('Lỗi khi tải chi tiết thanh toán.');
    }
}

async function deletePayment(id) {
    if (!confirm('Bạn có chắc muốn xóa giao dịch thanh toán này?')) return;

    try {
        const response = await fetch('/api/payments/' + id, { method: 'DELETE' });
        if (!response.ok) throw new Error('Failed to delete payment');

        alert('Xóa thành công!');
        loadPayments(currentPage);
    } catch (error) {
        console.error('Error:', error);
        alert('Lỗi khi xóa thanh toán.');
    }
}