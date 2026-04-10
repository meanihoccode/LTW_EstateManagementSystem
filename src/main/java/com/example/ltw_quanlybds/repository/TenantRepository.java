package com.example.ltw_quanlybds.repository;

import com.example.ltw_quanlybds.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
@Repository
public interface TenantRepository  extends JpaRepository<Tenant,Integer> {
    List<Tenant> findByPhone(Integer contractId);

    @Query("SELECT t FROM Tenant t WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "t.fullName LIKE CONCAT('%', :keyword, '%') OR " +
            "t.phone LIKE CONCAT('%', :keyword, '%') OR " +
            "t.email LIKE CONCAT('%', :keyword, '%') OR " +
            "t.idNumber LIKE CONCAT('%', :keyword, '%'))")
    Page<Tenant> searchTenants(@Param("keyword") String keyword, Pageable pageable);
}