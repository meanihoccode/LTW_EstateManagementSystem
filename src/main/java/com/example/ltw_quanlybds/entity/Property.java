package com.example.ltw_quanlybds.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "Tên bất động sản không được để trống")
    @Size(min = 3, max = 100, message = "Tên phải từ 3-100 ký tự")
    @Column(name = "ten_bds")
    private String name;

    @NotBlank(message = "Loại bất động sản không được để trống")
    @Column(name = "loai_bds")
    private String type;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Column(name = "dia_chi")
    private String address;

    @NotNull(message = "Diện tích không được null")
    @DecimalMin(value = "0.1", message = "Diện tích phải lớn hơn 0")
    @Column(name = "dien_tich")
    private BigDecimal area;

    @NotNull(message = "Giá thuê không được null")
    @DecimalMin(value = "0", message = "Giá thuê phải lớn hơn hoặc bằng 0")
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

