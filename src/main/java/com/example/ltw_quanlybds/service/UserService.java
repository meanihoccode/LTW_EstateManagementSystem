package com.example.ltw_quanlybds.service;

import com.example.ltw_quanlybds.dto.UserResponse;
import com.example.ltw_quanlybds.entity.Account;
import com.example.ltw_quanlybds.entity.User;
import com.example.ltw_quanlybds.exception.ResourceNotFoundException;
import com.example.ltw_quanlybds.repository.AccountRepository;
import com.example.ltw_quanlybds.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User getUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User not found with id: " + id));
    }
    public User createUser(User user) {
        // Tự động tạo tài khoản trước
        String username = "nv" + System.currentTimeMillis();
        String password = UUID.randomUUID().toString().substring(0, 8);

        // Dùng quyenHan (Admin/Quản lý/Nhân viên) thay vì role (chức danh)
        String quyenHan = (user.getQuyenHan() != null && !user.getQuyenHan().isBlank())
                ? user.getQuyenHan()
                : "Nhân viên";  // mặc định là Nhân viên

        Account account = Account.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(quyenHan)
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

    // ✅ Convert User entity thành UserResponse (không có password)
    public UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        if (user.getAccount() != null) {
            response.setUsername(user.getAccount().getUsername());
        }
        return response;
    }
}