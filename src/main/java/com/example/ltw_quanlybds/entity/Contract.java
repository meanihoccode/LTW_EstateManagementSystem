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

    // ✅ @ManyToOne relationship: Nhiều hợp đồng liên quan đến 1 bất động sản
    @ManyToOne
    @JoinColumn(name = "bat_dong_san_id")
    private Property property;

    // ✅ @ManyToOne relationship: Nhiều hợp đồng liên quan đến 1 khách thuê
    @ManyToOne
    @JoinColumn(name = "khach_thue_id")
    private Tenant tenant;

    @Column(name = "ngay_bat_dau")
    private LocalDate startDate;

    @Column(name = "ngay_ket_thuc")
    private LocalDate endDate;

    @Column(name = "tien_coc")
    private BigDecimal deposit;

    @Column(name = "trang_thai")
    private String status;
}

