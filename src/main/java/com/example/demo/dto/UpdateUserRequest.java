package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    private String nickname;
    private String email;
    private String phone;
    private String gender;
    private Integer age;
    private LocalDate birthday;
    private String qq;
    private String wechat;
    private String bio;
    private String avatar;
}
