-- INSERT Sample Data vào quan_ly_bat_dong_san database
USE quan_ly_bat_dong_san;

-- Thêm dữ liệu Chủ Sở Hữu
INSERT INTO ChuSoHuu (ho_ten, so_dien_thoai, email, dia_chi) VALUES
('Nguyễn Văn A', '0901234567', 'nguyena@email.com', '123 Nguyễn Huệ, Q1, TP.HCM'),
('Trần Thị B', '0912345678', 'tranb@email.com', '456 Lê Lợi, Q5, TP.HCM'),
('Phạm Minh C', '0923456789', 'phamc@email.com', '789 Trần Hưng Đạo, Q7, TP.HCM');

-- Thêm dữ liệu Nhân Viên
INSERT INTO NhanVien (ho_ten, so_dien_thoai, vai_tro) VALUES
('Lê Văn X', '0901111111', 'Quản lý'),
('Hoàng Thị Y', '0912222222', 'Nhân viên kinh doanh'),
('Đặng Minh Z', '0913333333', 'Nhân viên kinh doanh');

-- Thêm dữ liệu Bất Động Sản
INSERT INTO BatDongSan (ten_bds, loai_bds, dia_chi, dien_tich, gia_thue, trang_thai, chu_so_huu_id, nhan_vien_id) VALUES
('Căn hộ Quận 1', 'Căn hộ', '123 Nguyễn Huệ, Q1', 85.0, 1200.00, 'Cho thuê', 1, 1),
('Nhà Quận 5', 'Nhà riêng', '456 Lê Lợi, Q5', 120.0, 950.00, 'Cho thuê', 2, 2),
('Biệt thự Quận 7', 'Biệt thự', '789 Trần Hưng Đạo, Q7', 200.0, 1500.00, 'Trống', 3, 1),
('Cửa hàng Quận 1', 'Cửa hàng', '321 Đinh Tiên Hoàng, Q1', 50.0, 600.00, 'Cho thuê', 1, 3);

-- Thêm dữ liệu Khách Thuê
INSERT INTO KhachThue (ho_ten, so_cccd, so_dien_thoai, email) VALUES
('Lý Công Uẩn', '123456789012345', '0934567890', 'lyconguon@email.com'),
('Trương Thị Mỹ Hoa', '987654321098765', '0945678901', 'truongmyhoa@email.com'),
('Phan Văn Đức', '456789012345678', '0956789012', 'phanvanduc@email.com');

-- Thêm dữ liệu Hợp Đồng
INSERT INTO HopDong (bat_dong_san_id, khach_thue_id, ngay_bat_dau, ngay_ket_thuc, tien_coc, trang_thai) VALUES
(1, 1, '2025-01-15', '2026-01-15', 1200.00, 'Hiệu lực'),
(2, 2, '2025-02-01', '2026-02-01', 950.00, 'Hiệu lực'),
(4, 3, '2025-03-10', '2026-03-10', 600.00, 'Chờ duyệt');

-- Thêm dữ liệu Thanh Toán
INSERT INTO ThanhToan (hop_dong_id, ngay_thanh_toan, so_tien, phuong_thuc, trang_thai) VALUES
(1, '2025-02-15', 1200.00, 'Chuyển khoản', 'Đã duyệt'),
(2, '2025-03-01', 950.00, 'Chuyển khoản', 'Đã duyệt'),
(1, '2025-03-15', 1200.00, 'Tiền mặt', 'Chờ duyệt');

