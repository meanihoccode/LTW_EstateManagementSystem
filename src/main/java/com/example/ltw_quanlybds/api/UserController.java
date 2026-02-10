package com.example.ltw_quanlybds.api;

import com.example.ltw_quanlybds.dto.UserCreateRequest;
import com.example.ltw_quanlybds.dto.UserResponse;
import com.example.ltw_quanlybds.entity.User;
import com.example.ltw_quanlybds.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staffs")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        // Tạo User entity từ request
        User user = new User();
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());

        // Tạo user (tự động tạo account)
        User createdUser = userService.createUser(user);

        // Convert thành response (không có password)
        UserResponse response = userService.convertToResponse(createdUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id,
                                             @Valid @RequestBody User userDetails) {
        userDetails.setId(id);
        return ResponseEntity.ok(userService.updateUser(userDetails));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}