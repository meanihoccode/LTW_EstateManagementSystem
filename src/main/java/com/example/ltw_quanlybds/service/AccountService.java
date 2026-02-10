package com.example.ltw_quanlybds.service;

import com.example.ltw_quanlybds.entity.Account;
import com.example.ltw_quanlybds.entity.User;
import com.example.ltw_quanlybds.repository.AccountRepository;
import com.example.ltw_quanlybds.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public boolean validatePassword(String username, String password) {
        Account account = findByUsername(username);
        if (account == null) {
            return false;
        }
        // TODO: Trong production, cần dùng BCrypt để so sánh password
        return account.getPassword().equals(password);
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }

    public void deleteAccount(Integer id) {
        accountRepository.deleteById(id);
    }

    // Admin xem danh sách tất cả tài khoản
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // Admin xem chi tiết một tài khoản
    public Account getAccountById(Integer id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    // Admin xem tài khoản của một nhân viên
    public Account getAccountByStaffId(Integer staffId) {
        User user = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        if (user.getAccount() == null) {
            throw new RuntimeException("Staff has no account");
        }

        return user.getAccount();
    }

    // Admin đặt lại mật khẩu
    public Account resetPassword(Integer accountId) {
        Account account = getAccountById(accountId);

        // Tạo mật khẩu ngẫu nhiên mới
        String newPassword = java.util.UUID.randomUUID().toString().substring(0, 8);
        account.setPassword(newPassword);

        return accountRepository.save(account);
    }
}

