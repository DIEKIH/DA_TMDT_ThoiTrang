package com.example.da_tmdt_thoitrang.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String fullName;
    private String phoneNumber;
    private String gender;
}
