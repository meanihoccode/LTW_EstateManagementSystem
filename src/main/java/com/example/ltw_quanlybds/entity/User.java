package com.example.ltw_quanlybds.entity;

import jakarta.persistence.*;
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

    @Column(name = "ho_ten")
    private String fullName;

    @Column(name = "so_dien_thoai")
    private String phone;

    @Column(name = "vai_tro")
    private String role;

    // ✅ @OneToOne relationship: 1 nhân viên có 1 tài khoản duy nhất
    @OneToOne
    @JoinColumn(name = "tai_khoan_id")
    private Account account;
}

