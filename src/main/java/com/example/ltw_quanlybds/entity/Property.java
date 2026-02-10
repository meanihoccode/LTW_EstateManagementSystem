package com.example.ltw_quanlybds.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "batdongsan")
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
    private BigDecimal area;

    @Column(name = "gia_thue")
    private BigDecimal rentalPrice;

    @Column(name = "trang_thai")
    private String status;

    // ✅ @ManyToOne relationship: Nhiều bất động sản thuộc 1 chủ sở hữu
    @ManyToOne
    @JoinColumn(name = "chu_so_huu_id")
    private Owner owner;

    // ✅ @ManyToOne relationship: Nhiều bất động sản được quản lý bởi 1 nhân viên
    @ManyToOne
    @JoinColumn(name = "nhan_vien_id")
    private User staff;
}

