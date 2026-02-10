package com.example.ltw_quanlybds.service;

import com.example.ltw_quanlybds.entity.Account;
import com.example.ltw_quanlybds.entity.User;
import com.example.ltw_quanlybds.repository.AccountRepository;
import com.example.ltw_quanlybds.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;

@Service
@Transactional

public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User getUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(() ->
                new RuntimeException("User not found"));
    }
    public User createUser(User user) {
        // Tự động tạo tài khoản trước
        String username = "nv" + System.currentTimeMillis(); // Tạo username tạm
        String password = UUID.randomUUID().toString().substring(0, 8);
        String role = user.getRole() != null ? user.getRole() : "NhanVien";

        Account account = Account.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();

        Account savedAccount = accountRepository.save(account);

        // Gán account object cho user
        user.setAccount(savedAccount);

        // Tạo nhân viên
        User savedUser = userRepository.save(user);

        // Cập nhật username để chứa ID nhân viên
        savedAccount.setUsername("nv" + savedUser.getId());
        accountRepository.save(savedAccount);

        return savedUser;
    }
    public User updateUser(User user) {
        User existingUser = getUserById(user.getId());

        if (user.getFullName() != null) {
            existingUser.setFullName(user.getFullName());
        }
        if (user.getPhone() != null) {
            existingUser.setPhone(user.getPhone());
        }
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        return userRepository.save(existingUser);
    }
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}