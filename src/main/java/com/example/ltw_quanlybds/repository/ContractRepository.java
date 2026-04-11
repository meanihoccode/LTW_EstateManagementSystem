package com.example.ltw_quanlybds.repository;

import com.example.ltw_quanlybds.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
@Repository
public interface ContractRepository extends JpaRepository<Contract,Integer> {
    public List<Contract> findByStatus(String status);
    public long countByStatus(String status);

    // Lấy hợp đồng sắp kết thúc trong 30 ngày tới
    @Query(value = "SELECT c.* FROM hopdong c WHERE c.ngay_ket_thuc >= CURDATE() AND c.ngay_ket_thuc <= DATE_ADD(CURDATE(), INTERVAL 30 DAY) ORDER BY c.ngay_ket_thuc ASC", nativeQuery = true)
    List<Contract> findExpiringContracts();

    @Query("SELECT c FROM Contract c WHERE " +
            "(:status IS NULL OR :status = '' OR c.status = :status) AND " +
            "(:keyword IS NULL OR :keyword = '' OR c.tenant.fullName LIKE CONCAT('%', :keyword, '%') OR c.property.name LIKE CONCAT('%', :keyword, '%'))")
    Page<Contract> searchContracts(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable);

        // Đếm số lượng hợp đồng của 1 BĐS bị trùng lặp thời gian
        @Query("SELECT COUNT(c) FROM Contract c WHERE c.property.id = :propertyId " +
                "AND c.status IN ('Hiệu lực', 'Chờ duyệt') " +
                "AND c.id != :excludeContractId " + // Bỏ qua chính nó khi đang Edit
                "AND ((:startDate BETWEEN c.startDate AND c.endDate) " +
                "OR (:endDate BETWEEN c.startDate AND c.endDate) " +
                "OR (c.startDate BETWEEN :startDate AND :endDate))")
        long countOverlappingContracts(
                @Param("propertyId") Integer propertyId,
                @Param("startDate") LocalDate startDate,
                @Param("endDate") LocalDate endDate,
                @Param("excludeContractId") Integer excludeContractId);

    // Tìm các hợp đồng có ngày kết thúc nhỏ hơn ngày hiện tại và đang có Hiệu lực
        @Query("SELECT c FROM Contract c WHERE c.endDate < CURRENT_DATE AND c.status = 'Hiệu lực'")
        List<Contract> findExpiredContracts();

        @Query("SELECT c FROM Contract c WHERE c.startDate = CURRENT_DATE AND c.status = 'Hiệu lực'")
        List<Contract> findContractsStartingToday();
    }

