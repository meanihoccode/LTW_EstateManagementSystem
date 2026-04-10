package com.example.ltw_quanlybds.repository;

import com.example.ltw_quanlybds.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
@Repository
public interface OwnerRepository extends JpaRepository<Owner,Integer> {
    Owner findByFullName(String name);

    @Query("SELECT o FROM Owner o WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "o.fullName LIKE CONCAT('%', :keyword, '%') OR " +
            "o.phone LIKE CONCAT('%', :keyword, '%') OR " +
            "o.email LIKE CONCAT('%', :keyword, '%'))")
    Page<Owner> searchOwners(@Param("keyword") String keyword, Pageable pageable);
}