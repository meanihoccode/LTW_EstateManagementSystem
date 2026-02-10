package com.example.ltw_quanlybds.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "thanhtoan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "thanh_toan_id")
    private Integer id;

    // ✅ @ManyToOne relationship: Nhiều thanh toán liên quan đến 1 hợp đồng
    @ManyToOne
    @JoinColumn(name = "hop_dong_id")
    private Contract contract;

    @NotNull(message = "Ngày thanh toán không được null")
    @Column(name = "ngay_thanh_toan")
    private LocalDate paymentDate;

    @NotNull(message = "Số tiền không được null")
    @DecimalMin(value = "0.01", message = "Số tiền phải lớn hơn 0")
    @Column(name = "so_tien")
    private BigDecimal amount;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    @Column(name = "phuong_thuc")
    private String method;

    @Column(name = "trang_thai")
    private String status;
}

