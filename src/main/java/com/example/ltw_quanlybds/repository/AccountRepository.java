package com.example.ltw_quanlybds.repository;

import com.example.ltw_quanlybds.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Account findByUsername(String username);
}

