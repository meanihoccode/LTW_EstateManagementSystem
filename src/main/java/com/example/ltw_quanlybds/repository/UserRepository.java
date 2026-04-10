package com.example.ltw_quanlybds.repository;

import com.example.ltw_quanlybds.entity.Account;
import com.example.ltw_quanlybds.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    User findByFullName(String username);
    User findByAccount(Account account);
    @Query("SELECT s FROM User s WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "s.fullName LIKE CONCAT('%', :keyword, '%') OR " +
            "s.phone LIKE CONCAT('%', :keyword, '%') OR " +
            "s.role LIKE CONCAT('%', :keyword, '%'))")
    Page<User> searchStaffs(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.account IS NOT NULL AND " +
            "(:role IS NULL OR :role = '' OR :role = 'all' OR u.account.role = :role) AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "u.fullName LIKE CONCAT('%', :keyword, '%') OR " +
            "u.phone LIKE CONCAT('%', :keyword, '%') OR " +
            "u.account.username LIKE CONCAT('%', :keyword, '%'))")
    Page<User> searchUsersWithAccounts(
            @Param("keyword") String keyword,
            @Param("role") String role,
            Pageable pageable);
}
