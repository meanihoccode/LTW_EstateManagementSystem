package com.example.ltw_quanlybds.repository;

import com.example.ltw_quanlybds.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Integer> {
    List<Payment> findAllByStatus(String status);

    List<Payment> findByContractId(Integer contractId);

    // Custom query để lấy tổng tiền thanh toán trong tháng hiện tại
    // Chỉ cần lấy tất cả thanh toán có ngay_thanh_toan trong tháng hiện tại
    @Query(value = "SELECT COALESCE(SUM(so_tien), 0) FROM thanhtoan WHERE MONTH(ngay_thanh_toan) = MONTH(NOW()) AND YEAR(ngay_thanh_toan) = YEAR(NOW())", nativeQuery = true)
    Double calculateTotalPaymentsThisMonth();

    // Lấy doanh thu theo từng tháng trong 12 tháng gần nhất
    @Query(value = "SELECT MONTH(ngay_thanh_toan) as month, YEAR(ngay_thanh_toan) as year, COALESCE(SUM(so_tien), 0) as revenue " +
            "FROM thanhtoan " +
            "WHERE ngay_thanh_toan >= DATE_SUB(NOW(), INTERVAL 12 MONTH) " +
            "GROUP BY YEAR(ngay_thanh_toan), MONTH(ngay_thanh_toan) " +
            "ORDER BY YEAR(ngay_thanh_toan) ASC, MONTH(ngay_thanh_toan) ASC", nativeQuery = true)
    List<Object[]> getRevenueByMonth();
}