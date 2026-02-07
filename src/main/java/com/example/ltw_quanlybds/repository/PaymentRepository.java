package com.example.ltw_quanlybds.repository;

import com.example.ltw_quanlybds.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Integer> {
    List<Payment> findAllByStatus(String status);
    void deleteById(Integer id);
    List<Payment> findByContractId(Integer contractId);
}