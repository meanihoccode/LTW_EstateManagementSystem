package com.example.ltw_quanlybds.repository;

import com.example.ltw_quanlybds.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantRepository  extends JpaRepository<Tenant,Integer> {
    List<Tenant> findByPhone(Integer contractId);

}