package com.example.ltw_quanlybds.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "BatDongSan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bat_dong_san_id")
    private Integer id;

    @Column(name = "ten_bds")
    private String name;

    @Column(name = "loai_bds")
    private String type;

    @Column(name = "dia_chi")
    private String address;

    @Column(name = "dien_tich")
    private Float area;

    @Column(name = "gia_thue")
    private BigDecimal rentalPrice;

    @Column(name = "trang_thai")
    private String status;

    @Column(name = "chu_so_huu_id")
    private Integer ownerId;

    @Column(name = "nhan_vien_id")
    private Integer staffId;
}

