package com.example.ltw_quanlybds.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nhanvien")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nhan_vien_id")
    private Integer id;

    @NotBlank(message = "Tên nhân viên không được để trống")
    @Size(min = 3, max = 100, message = "Tên phải từ 3-100 ký tự")
    @Column(name = "ho_ten")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải là 10-11 chữ số")
    @Column(name = "so_dien_thoai")
    private String phone;

    @NotBlank(message = "Vai trò không được để trống")
    @Column(name = "vai_tro")
    private String role;

    // ✅ @OneToOne relationship: 1 nhân viên có 1 tài khoản duy nhất
    @OneToOne
    @JoinColumn(name = "tai_khoan_id")
    private Account account;

    // Không lưu vào DB, chỉ nhận từ frontend khi tạo mới để set quyền hạn cho account
    @Transient
    private String quyenHan;
}

