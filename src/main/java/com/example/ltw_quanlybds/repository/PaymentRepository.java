package com.example.ltw_quanlybds.repository;

import com.example.ltw_quanlybds.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Lấy 10 thanh toán gần đây nhất
    @Query(value = "SELECT p.thanh_toan_id, p.hop_dong_id, p.ngay_thanh_toan, p.so_tien, p.trang_thai " +
            "FROM thanhtoan p " +
            "ORDER BY p.ngay_thanh_toan DESC " +
            "LIMIT 10", nativeQuery = true)
    List<Object[]> findRecentPayments();

    @Query("SELECT p FROM Payment p WHERE " +
            "(:status IS NULL OR :status = '' OR p.status = :status) AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "CAST(p.id AS string) LIKE CONCAT('%', :keyword, '%') OR " +
            "CAST(p.contract.id AS string) LIKE CONCAT('%', :keyword, '%'))")
    Page<Payment> searchPayments(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable);
}