package com.example.ltw_quanlybds.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chusohuu")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chu_so_huu_id")
    private Integer id;

    @NotBlank(message = "Tên chủ sở hữu không được để trống")
    @Size(min = 3, max = 100, message = "Tên phải từ 3-100 ký tự")
    @Column(name = "ho_ten")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải là 10-11 chữ số")
    @Column(name = "so_dien_thoai")
    private String phone;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email phải hợp lệ")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Column(name = "dia_chi")
    private String address;
}

