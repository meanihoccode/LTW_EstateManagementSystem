package com.example.ltw_quanlybds.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Integer accountId;
    private String username;
    private String role;
    private String status;  // "SUCCESS" hoặc "FIRST_LOGIN"
    private String message;
}

