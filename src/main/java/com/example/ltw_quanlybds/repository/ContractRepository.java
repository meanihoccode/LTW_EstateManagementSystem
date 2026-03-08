package com.example.ltw_quanlybds.repository;

import com.example.ltw_quanlybds.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract,Integer> {
    public List<Contract> findByStatus(String status);
    public long countByStatus(String status);

    // Lấy hợp đồng sắp kết thúc trong 30 ngày tới
    @Query(value = "SELECT c.* FROM hopdong c WHERE c.ngay_ket_thuc >= CURDATE() AND c.ngay_ket_thuc <= DATE_ADD(CURDATE(), INTERVAL 30 DAY) ORDER BY c.ngay_ket_thuc ASC", nativeQuery = true)
    List<Contract> findExpiringContracts();
}