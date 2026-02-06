package com.example.ltw_quanlybds.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "hopdong")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hop_dong_id")
    private Integer id;

    @Column(name = "bat_dong_san_id")
    private Integer propertyId;

    @Column(name = "khach_thue_id")
    private Integer tenantId;

    @Column(name = "ngay_bat_dau")
    private LocalDate startDate;

    @Column(name = "ngay_ket_thuc")
    private LocalDate endDate;

    @Column(name = "tien_coc")
    private BigDecimal deposit;

    @Column(name = "trang_thai")
    private String status;
}

