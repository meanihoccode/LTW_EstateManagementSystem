package com.example.ltw_quanlybds.security;

import com.example.ltw_quanlybds.entity.Account;
import com.example.ltw_quanlybds.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Spring Security dùng class này để load user từ database khi login.
 * Nó lấy username → tìm trong bảng taikhoan → trả về UserDetails (gồm username, password, role)
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm account trong database theo username
        Account account = accountRepository.findByUsername(username);

        if (account == null) {
            throw new UsernameNotFoundException("Không tìm thấy tài khoản: " + username);
        }

        // Trả về UserDetails với role từ cột quyen_han
        // Spring Security yêu cầu role phải có prefix "ROLE_"
        return new User(
                account.getUsername(),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole()))
        );
    }
}

