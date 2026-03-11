const tongBDS = document.querySelector('#totalProperties')

document.addEventListener('DOMContentLoaded', function() {
    fetchRole();
    fetchTotalProperties();
    fetchActiveContracts();
    fetchEmptyProperties();
    fetchRevenueThisMonth();
    loadRevenueChart();
    fetchExpiringContracts();
    fetchRecentPayments();
});

const roleElement = document.querySelector('.user');
async function fetchRole() {
    try {
        const response = await fetch('/api/me/role');
        if (!response.ok) throw new Error('Failed to load role');

        const data = await response.json();

        if (roleElement) {
            roleElement.textContent = `👤 ${data.username} (${data.role})`;
        }
    } catch (error) {
        console.error('Error loading role:', error);
    }
}
async function fetchTotalProperties() {
    try {
        const response = await fetch('api/properties/total');
        if (!response.ok) {
            throw new Error('Failed to load total properties');
        }
        tongBDS.innerHTML = await response.json();
    } catch (error) {
        console.error('Data Error: ',response);
        alert('Lỗi tải số lượng BĐS');
    }
}

const tongHopDongHoatDong = document.querySelector('#activeContracts');

async function fetchActiveContracts() {
    try {
        const response = await fetch('api/contracts/totalActiveContracts');
        if (!response.ok) {
            throw new Error('Failed to load active contracts');
        }
        tongHopDongHoatDong.innerHTML = await response.json();
    } catch (error) {
        console.log('Data Error: ', response.json());
        alert('Lỗi tải số hợp đồng hoạt động');
    }
}

const BDSTrong = document.querySelector('#emptyProperties');

async function fetchEmptyProperties() {
    try {
        const response = await fetch('api/properties/emptyProperties');
        if (!response.ok) {
            throw new Error('Failed to load empty properties');
        }
        BDSTrong.innerHTML = await response.json();
    } catch (error) {
        console.error('Data Error: ',response);
        alert('Lỗi tải số lượng BĐS trống');
    }
}

const revenueThisMonth = document.querySelector('#monthRevenue');
async function fetchRevenueThisMonth() {
    try {
        const response = await fetch('/api/contracts/revenueThisMonth');
        if (!response.ok) {
            throw new Error('Failed to load revenue this month');
        }
        revenueThisMonth.innerHTML = await response.json() + ' VND';
    } catch (error) {
        console.error('Data Error: ',response);
        alert('Lỗi tải doanh thu tháng này');
    }
}


// ===== BIỂU ĐỒ DOANH THU =====
async function loadRevenueChart() {
    try {
        const response = await fetch('/api/contracts/revenueByMonth');
        if (!response.ok) {
            throw new Error('Failed to load revenue chart');
        }

        const data = await response.json();

        // Tạo mảng nhãn và dữ liệu cho 12 tháng
        const labels = [];
        const chartData = [];

        // Tính ngày 12 tháng trước
        const today = new Date();
        const startDate = new Date(today.getFullYear(), today.getMonth() - 11, 1);

        // Tạo nhãn cho 12 tháng
        for (let i = 0; i < 12; i++) {
            const date = new Date(startDate.getFullYear(), startDate.getMonth() + i, 1);
            const monthYear = `T${date.getMonth() + 1}/${date.getFullYear()}`;
            labels.push(monthYear);
            chartData.push(0);
        }

        // Điền dữ liệu từ API
        data.forEach(item => {
            const month = item[0]; // 1-12
            const year = item[1];
            const revenue = item[2];

            // Tìm vị trí trong mảng
            const date = new Date(year, month - 1, 1);
            const index = Math.round((date - startDate) / (1000 * 60 * 60 * 24 * 30));

            if (index >= 0 && index < 12) {
                chartData[index] = revenue;
            }
        });

        // Định dạng số tiền (đơn vị triệu)
        const formattedData = chartData.map(val => Math.round(val / 1000000 * 100) / 100);

        // Vẽ biểu đồ
        const ctx = document.getElementById('revenueChart');
        if (!ctx) {
            console.warn('Canvas element #revenueChart not found');
            return;
        }

        const Chart_instance = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Doanh thu (triệu VND)',
                    data: formattedData,
                    borderColor: '#4CAF50',
                    backgroundColor: 'rgba(76, 175, 80, 0.1)',
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4,
                    pointRadius: 5,
                    pointHoverRadius: 7,
                    pointBackgroundColor: '#4CAF50',
                    pointBorderColor: '#fff',
                    pointBorderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: true,
                        position: 'top',
                        labels: {
                            font: {
                                size: 14,
                                weight: 'bold'
                            },
                            color: document.body.classList.contains('dark-mode') ? '#e8e8e8' : '#333'
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            font: {
                                size: 12
                            },
                            color: document.body.classList.contains('dark-mode') ? '#b8b8b8' : '#666',
                            callback: function(value) {
                                return value.toLocaleString() + 'M';
                            }
                        },
                        grid: {
                            color: document.body.classList.contains('dark-mode') ? 'rgba(255, 255, 255, 0.1)' : 'rgba(200, 200, 200, 0.2)'
                        }
                    },
                    x: {
                        ticks: {
                            font: {
                                size: 12
                            },
                            color: document.body.classList.contains('dark-mode') ? '#b8b8b8' : '#666'
                        },
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    } catch (error) {
        console.error('Error loading revenue chart:', error);
    }
}

// ===== HỢP ĐỒNG SẮP KẾT THÚC =====
async function fetchExpiringContracts() {
    try {
        const response = await fetch('/api/contracts/expiringContracts');
        if (!response.ok) {
            throw new Error('Failed to load expiring contracts');
        }

        const contracts = await response.json();
        const tbody = document.getElementById('expiringContractsTable');

        if (!tbody) return;

        if (contracts.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: #999;">Không có hợp đồng sắp kết thúc</td></tr>';
            return;
        }

        tbody.innerHTML = contracts.map(contract => {
            const endDate = new Date(contract.endDate).toLocaleDateString('vi-VN');
            const propertyName = contract.property ? contract.property.name : 'N/A';
            const tenantName = contract.tenant ? contract.tenant.fullName : 'N/A';

            return `
                <tr>
                    <td>${contract.id}</td>
                    <td>${propertyName}</td>
                    <td>${tenantName}</td>
                    <td>${endDate}</td>
                    <td>${contract.status || 'N/A'}</td>
                </tr>
            `;
        }).join('');
    } catch (error) {
        console.error('Error loading expiring contracts:', error);
    }
}

// ===== THANH TOÁN GẦN ĐÂY =====
async function fetchRecentPayments() {
    try {
        const response = await fetch('/api/payments/recent');
        if (!response.ok) {
            throw new Error('Failed to load recent payments');
        }

        const payments = await response.json();
        const tbody = document.getElementById('recentPaymentsTable');

        if (!tbody) return;

        if (payments.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: #999;">Không có thanh toán nào</td></tr>';
            return;
        }

        tbody.innerHTML = payments.map(payment => {
            const paymentDate = new Date(payment[2]).toLocaleDateString('vi-VN');
            const amount = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(payment[3]);

            return `
                <tr>
                    <td>${payment[0]}</td>
                    <td>HD #${payment[1]}</td>
                    <td>${amount}</td>
                    <td>${paymentDate}</td>
                    <td>${payment[4] || 'N/A'}</td>
                </tr>
            `;
        }).join('');
    } catch (error) {
        console.error('Error loading recent payments:', error);
    }
}


