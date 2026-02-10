package com.example.ltw_quanlybds.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "khachthue")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "khach_thue_id")
    private Integer id;

    @NotBlank(message = "Tên khách thuê không được để trống")
    @Size(min = 3, max = 100, message = "Tên phải từ 3-100 ký tự")
    @Column(name = "ho_ten")
    private String fullName;

    @NotBlank(message = "Số CCCD không được để trống")
    @Size(min = 9, max = 12, message = "Số CCCD phải từ 9-12 ký tự")
    @Column(name = "so_cccd")
    private String idNumber;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải là 10-11 chữ số")
    @Column(name = "so_dien_thoai")
    private String phone;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email phải hợp lệ")
    @Column(name = "email")
    private String email;
}

