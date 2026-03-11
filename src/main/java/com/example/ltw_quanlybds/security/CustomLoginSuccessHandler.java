package com.example.ltw_quanlybds.security;

import com.example.ltw_quanlybds.entity.Account;
import com.example.ltw_quanlybds.repository.AccountRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String username = authentication.getName();
        Account account = accountRepository.findByUsername(username);

        boolean isAdmin = account != null && "Admin".equals(account.getRole());

        if (!isAdmin && account != null && Boolean.TRUE.equals(account.getFirstLogin())) {
            response.sendRedirect("/change-password");
        } else {
            response.sendRedirect("/dashboard");
        }
    }
}

