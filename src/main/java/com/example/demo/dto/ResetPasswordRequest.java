package com.example.demo.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String username;
    private String email;
    private String newPassword;
}
