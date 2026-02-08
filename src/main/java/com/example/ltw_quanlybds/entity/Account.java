package com.example.ltw_quanlybds.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

    @Column(name = "ten_dang_nhap", unique = true)
    private String username;

    @Column(name = "mat_khau")
    private String password;

    @Column(name = "quyen_han")
    private String role;
}

