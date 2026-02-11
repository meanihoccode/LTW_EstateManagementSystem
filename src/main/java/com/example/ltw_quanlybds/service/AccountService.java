package com.example.ltw_quanlybds.service;

import com.example.ltw_quanlybds.dto.PasswordResetResponse;
import com.example.ltw_quanlybds.entity.Account;
import com.example.ltw_quanlybds.entity.User;
import com.example.ltw_quanlybds.repository.AccountRepository;
import com.example.ltw_quanlybds.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public boolean validatePassword(String username, String password) {
        Account account = findByUsername(username);
        if (account == null) {
            return false;
        }
        return passwordEncoder.matches(password, account.getPassword());
    }

    public Account createAccount(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
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
    public PasswordResetResponse resetPassword(Integer accountId) {
        Account account = getAccountById(accountId);

        // Tạo mật khẩu ngẫu nhiên mới
        String tempPassword = generateSecurePassword();
        account.setPassword(passwordEncoder.encode(tempPassword));
        account.setFirstLogin(true);  // Bắt buộc đổi mật khẩu lần đầu đăng nhập
        accountRepository.save(account);

        PasswordResetResponse response = new PasswordResetResponse();
        response.setAccountId(accountId);
        response.setTemporaryPassword(tempPassword);
        response.setUsername(account.getUsername());
        response.setMessage("Mật khẩu tạm thời, vui lòng đổi sau khi đăng nhập.");
        return response;
    }

    private String generateSecurePassword() {
        // Tạo password mạnh hơn UUID
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "@#$%";

        String all = uppercase + lowercase + digits + special;
        StringBuilder password = new StringBuilder();

        // Đảm bảo có ít nhất 1 ký tự từ mỗi loại
        password.append(uppercase.charAt((int) (Math.random() * uppercase.length())));
        password.append(lowercase.charAt((int) (Math.random() * lowercase.length())));
        password.append(digits.charAt((int) (Math.random() * digits.length())));
        password.append(special.charAt((int) (Math.random() * special.length())));

        // Thêm 4 ký tự ngẫu nhiên nữa
        for (int i = 0; i < 4; i++) {
            password.append(all.charAt((int) (Math.random() * all.length())));
        }

        return password.toString();
    }

    // Nhân viên đổi mật khẩu
    public Account changePassword(Integer accountId, String oldPassword, String newPassword) {
        Account account = getAccountById(accountId);

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(oldPassword, account.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }

        // Cập nhật mật khẩu mới
        account.setPassword(passwordEncoder.encode(newPassword));
        account.setFirstLogin(false);  // Đánh dấu đã đổi mật khẩu lần đầu
        return accountRepository.save(account);
    }
}

