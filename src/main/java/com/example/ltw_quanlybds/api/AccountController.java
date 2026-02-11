package com.example.ltw_quanlybds.api;

import com.example.ltw_quanlybds.dto.LoginRequest;
import com.example.ltw_quanlybds.dto.LoginResponse;
import com.example.ltw_quanlybds.dto.ChangePasswordRequest;
import com.example.ltw_quanlybds.entity.Account;
import com.example.ltw_quanlybds.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        if (accountService.validatePassword(loginRequest.getUsername(), loginRequest.getPassword())) {
            Account account = accountService.findByUsername(loginRequest.getUsername());

            // Kiểm tra nếu lần đầu đăng nhập
            if (account.getFirstLogin() != null && account.getFirstLogin()) {
                LoginResponse loginResponse = new LoginResponse();
                loginResponse.setAccountId(account.getId());
                loginResponse.setUsername(account.getUsername());
                loginResponse.setRole(account.getRole());
                loginResponse.setStatus("FIRST_LOGIN");
                loginResponse.setMessage("Vui lòng đổi mật khẩu lần đầu đăng nhập");
                return ResponseEntity.ok(loginResponse);
            }

            // Login bình thường
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccountId(account.getId());
            loginResponse.setUsername(account.getUsername());
            loginResponse.setRole(account.getRole());
            loginResponse.setStatus("SUCCESS");
            loginResponse.setMessage("Đăng nhập thành công");
            return ResponseEntity.ok(loginResponse);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    // Admin xem danh sách tất cả tài khoản
    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    // Admin xem chi tiết một tài khoản theo ID
    @GetMapping("/accounts/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Integer id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    // Admin xem tài khoản của một nhân viên theo staffId
    @GetMapping("/staff/{staffId}")
    public ResponseEntity<Account> getAccountByStaffId(@PathVariable Integer staffId) {
        return ResponseEntity.ok(accountService.getAccountByStaffId(staffId));
    }

    // Admin đặt lại mật khẩu
    @PutMapping("/accounts/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Integer id) {
        var response = accountService.resetPassword(id);
        return ResponseEntity.ok(response);
    }

    // Nhân viên đổi mật khẩu lần đầu đăng nhập
    @PutMapping("/accounts/{id}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Integer id, @RequestBody ChangePasswordRequest request) {
        Account response = accountService.changePassword(id, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(response);
    }
}




