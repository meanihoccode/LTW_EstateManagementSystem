package com.example.ltw_quanlybds;

import com.example.ltw_quanlybds.entity.Account;
import com.example.ltw_quanlybds.entity.User;
import com.example.ltw_quanlybds.repository.AccountRepository;
import com.example.ltw_quanlybds.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * WebController: Chỉ handle điều hướng trang
 * Việc login/logout/phân quyền do Spring Security xử lý tự động
 */
@Controller
public class WebController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/properties")
    public String properties() {
        return "properties";
    }

    @GetMapping("/contracts")
    public String contracts() {
        return "contracts";
    }

    @GetMapping("/payments")
    public String payments() {
        return "payments";
    }

    @GetMapping("/tenants")
    public String tenants() {
        return "tenants";
    }

    @GetMapping("/owners")
    public String owners() {
        return "owners";
    }

    @GetMapping("/staff")
    public String staff() {
        return "staff";
    }

    @GetMapping("/accounts")
    public String accounts() {
        return "accounts";
    }

    @GetMapping("/change-password")
    public String changePassword() {
        return "change-password";
    }

    // ✅ Endpoint để frontend lấy role và staffId của user đang đăng nhập
    @GetMapping("/api/me/role")
    @ResponseBody
    public Map<String, Object> getMyRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Account account = accountRepository.findByUsername(username);

        Map<String, Object> result = new HashMap<>();
        if (account == null) {
            result.put("role", "");
            result.put("staffId", null);
            return result;
        }

        result.put("role", account.getRole());
        result.put("username", username);

        // Tìm nhân viên tương ứng với tài khoản này
        User staff = userRepository.findByAccount(account);
        result.put("staffId", staff != null ? staff.getId() : null);

        return result;
    }
}
