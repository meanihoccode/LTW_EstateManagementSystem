package com.example.ltw_quanlybds.repository;

import com.example.ltw_quanlybds.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerRepository extends JpaRepository<Owner,Integer> {
    Owner findByFullName(String name);
}