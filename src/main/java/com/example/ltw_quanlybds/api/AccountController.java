package com.example.ltw_quanlybds.api;

import com.example.ltw_quanlybds.dto.LoginRequest;
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
            return ResponseEntity.ok(account);
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
    public ResponseEntity<Account> resetPassword(@PathVariable Integer id) {
        return ResponseEntity.ok(accountService.resetPassword(id));
    }
}

