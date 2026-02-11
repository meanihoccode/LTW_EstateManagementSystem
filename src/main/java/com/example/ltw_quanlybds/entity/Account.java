package com.example.ltw_quanlybds.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "taikhoan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tai_khoan_id")
    private Integer id;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 20, message = "Tên đăng nhập phải từ 8-20 ký tự")
    @Column(name = "ten_dang_nhap", unique = true)
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Column(name = "mat_khau")
    private String password;

    @NotBlank(message = "Quyền hạn không được để trống")
    @Column(name = "quyen_han")
    private String role;

    @Column(name = "lan_dau_dang_nhap")
    private Boolean firstLogin = true;
}



