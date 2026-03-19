package com.example.ltw_quanlybds.api;

import com.example.ltw_quanlybds.dto.LoginRequest;
import com.example.ltw_quanlybds.dto.LoginResponse;
import com.example.ltw_quanlybds.dto.ChangePasswordRequest;
import com.example.ltw_quanlybds.entity.Account;
import com.example.ltw_quanlybds.entity.User;
import com.example.ltw_quanlybds.service.AccountService;
import com.example.ltw_quanlybds.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest,
                                   HttpServletRequest request) {
        // Xác thực username/password qua Spring Security
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        // Lưu authentication vào SecurityContext + tạo session
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        // Lấy thông tin account
        Account account = accountService.findByUsername(loginRequest.getUsername());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccountId(account.getId());
        loginResponse.setUsername(account.getUsername());
        loginResponse.setRole(account.getRole());

        // Kiểm tra lần đầu đăng nhập
        if (account.getFirstLogin() != null && account.getFirstLogin()) {
            loginResponse.setStatus("FIRST_LOGIN");
            loginResponse.setMessage("Vui lòng đổi mật khẩu lần đầu đăng nhập");
        } else {
            loginResponse.setStatus("SUCCESS");
            loginResponse.setMessage("Đăng nhập thành công");
        }

        return ResponseEntity.ok(loginResponse);
    }

    // Admin xem danh sách tất cả tài khoản
    @GetMapping("/accounts")
    public ResponseEntity<?> getAllAccounts() {
        // Lấy tất cả User
        List<User> staffs = userService.getAllUsers();
        // Khi trả về, User sẽ kèm Account
        return ResponseEntity.ok(staffs);
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

    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}




