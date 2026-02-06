package com.example.ltw_quanlybds.entity;

import jakarta.persistence.*;
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

    @Column(name = "ho_ten")
    private String fullName;

    @Column(name = "so_cccd")
    private String idNumber;

    @Column(name = "so_dien_thoai")
    private String phone;

    @Column(name = "email")
    private String email;
}

