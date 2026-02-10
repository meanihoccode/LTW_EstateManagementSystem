
package com.example.ltw_quanlybds.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Integer id;
    private String fullName;
    private String phone;
    private String role;
    private String username;  // Username từ Account

    // Không có password!
}

