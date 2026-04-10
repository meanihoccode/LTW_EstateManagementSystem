let accountsData = [];
let currentFilter = 'all';
let currentPage = 0;
const pageSize = 4; // Cố định 4 account 1 trang để giao diện không bị quá dài

document.addEventListener('DOMContentLoaded', function() {
    loadAccounts(0);
    setupEventListeners();
});

// ==========================================
// 1. DATA FETCHING & PHÂN TRANG BACKEND
// ==========================================
async function loadAccounts(page = 0) {
    currentPage = page;
    const keyword = document.getElementById('searchInput').value.trim();

    try {
        const url = `/api/auth/accounts/paged?page=${page}&size=${pageSize}&keyword=${encodeURIComponent(keyword)}&role=${encodeURIComponent(currentFilter)}`;

        const response = await fetch(url);
        if (!response.ok) throw new Error('Lỗi tải dữ liệu tài khoản');

        const pageData = await response.json();

        if (Array.isArray(pageData)) {
            accountsData = pageData;
            renderRealPagination(1, 0);
        } else if (pageData && pageData.content) {
            accountsData = pageData.content;
            renderRealPagination(pageData.totalPages, pageData.number);
        } else {
            accountsData = [];
        }

        displayAccounts(accountsData);
    } catch (error) {
        console.error('Lỗi:', error);
        document.getElementById('accountsContainer').innerHTML =
            '<div style="color:red;padding:20px;text-align:center;">Lỗi khi tải dữ liệu!</div>';
    }
}

// ==========================================
// 2. RENDERING GIAO DIỆN
// ==========================================
function displayAccounts(dataToRender) {
    const container = document.getElementById('accountsContainer');

    if (!dataToRender || dataToRender.length === 0) {
        container.innerHTML = '<div style="text-align:center;color:#999;padding:40px;">Không tìm thấy tài khoản nào</div>';
        return;
    }

    let html = '';
    dataToRender.forEach(staff => {
        const acc = staff.account;
        if(!acc) return; // Đề phòng trường hợp lỗi data

        html += `
            <div class="account-card">
                <div class="staff-info">
                    <strong>👤 ${staff.fullName || 'N/A'}</strong> | 📞 ${staff.phone || 'N/A'}
                </div>

                <div class="account-info">
                    <div class="info-item">
                        <span class="info-label">Tên Tài Khoản</span>
                        <span class="info-value">${acc.username}</span>
                    </div>

                    <div class="info-item">
                        <span class="info-label">Quyền Hạn</span>
                        <span class="info-value">${acc.role}</span>
                    </div>

                    <div class="info-item">
                        <span class="info-label">ID</span>
                        <span class="info-value">#${acc.id}</span>
                    </div>
                </div>

                <div class="account-actions">
                    <button class="btn-copy" onclick="copyToClipboard('${acc.username}')">
                        📋 Copy Username
                    </button>

                    <button class="btn-reset-pwd" onclick="resetPassword(${acc.id})">
                        🔄 Đặt Lại MK
                    </button>
                </div>
            </div>
        `;
    });

    container.innerHTML = html;
}

function renderRealPagination(totalPages, current) {
    const container = document.getElementById('paginationButtons');
    if (!container) return;
    container.innerHTML = '';

    if (totalPages <= 1) return;

    let prevHtml = `<button onclick="loadAccounts(${current - 1})" ${current === 0 ? 'disabled' : ''}>&laquo;</button>`;
    container.insertAdjacentHTML('beforeend', prevHtml);

    for (let i = 0; i < totalPages; i++) {
        let activeClass = i === current ? 'active' : '';
        let pageHtml = `<button onclick="loadAccounts(${i})" class="${activeClass}">${i + 1}</button>`;
        container.insertAdjacentHTML('beforeend', pageHtml);
    }

    let nextHtml = `<button onclick="loadAccounts(${current + 1})" ${current === totalPages - 1 ? 'disabled' : ''}>&raquo;</button>`;
    container.insertAdjacentHTML('beforeend', nextHtml);
}

// ==========================================
// 3. NGHIỆP VỤ LỌC & SỰ KIỆN
// ==========================================
function setupEventListeners() {
    // Tìm kiếm bằng phím Enter
    document.getElementById('searchInput')?.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') loadAccounts(0);
    });
}

function filterByRole(role, btn) {
    currentFilter = role;

    // Đổi màu nút active
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');

    // Lọc xong load lại từ trang đầu
    loadAccounts(0);
}

// ==========================================
// 4. CHỨC NĂNG PHỤ TRỢ (COPY, RESET PWD)
// ==========================================
function copyToClipboard(text) {
    navigator.clipboard.writeText(text)
        .then(() => alert('Đã copy username!'))
        .catch(err => console.error(err));
}

async function resetPassword(accountId) {
    if (!confirm('Bạn có chắc muốn đặt lại mật khẩu?')) return;

    try {
        // Đã thêm thẻ btn vào trạng thái loading nếu bạn muốn có thể tự xử lý UX ở đây
        const response = await fetch(`/api/auth/accounts/${accountId}/reset-password`, {
            method: 'PUT'
        });

        if (response.ok) {
            const data = await response.json();

            // Đổ dữ liệu vào Modal
            document.getElementById('resetUname').textContent = data.username;
            document.getElementById('resetTempPwd').textContent = data.temporaryPassword;

            // Hiện Modal
            document.getElementById('successResetModal').style.display = 'block';

            // Gắn sự kiện cho nút Copy
            document.getElementById('btnCopyResetInfo').onclick = function() {
                const copyText = `Username: ${data.username}\nMật khẩu tạm: ${data.temporaryPassword}`;

                navigator.clipboard.writeText(copyText).then(() => {
                    alert('Đã copy vào khay nhớ tạm! Bạn có thể Paste (Ctrl+V) cho nhân viên.');
                    document.getElementById('successResetModal').style.display = 'none'; // Đóng modal
                });
            };

            loadAccounts(currentPage);
        } else {
            alert('Lỗi khi reset mật khẩu!');
        }
    } catch (error) {
        console.error(error);
        alert('Lỗi khi reset mật khẩu!');
    }
}