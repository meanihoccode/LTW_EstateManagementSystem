package com.example.ltw_quanlybds.entity;

import jakarta.persistence.*;
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

    @Column(name = "ho_ten")
    private String fullName;

    @Column(name = "so_dien_thoai")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "dia_chi")
    private String address;
}

