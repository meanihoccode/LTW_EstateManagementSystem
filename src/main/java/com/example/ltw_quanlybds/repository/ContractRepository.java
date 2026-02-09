package com.example.ltw_quanlybds.repository;

import com.example.ltw_quanlybds.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract,Integer> {
    public List<Contract> findByStatus(String status);
}