package com.example.da_tmdt_thoitrang.service;

import com.example.da_tmdt_thoitrang.dto.UserRegistrationDto;
import com.example.da_tmdt_thoitrang.entity.UserEntity;
import com.example.da_tmdt_thoitrang.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserEntity authenticateAdmin(String username, String password) {
        UserEntity user = userRepository.findByUsername(username);
        if (user != null && user.isAdmin() && user.isActive() &&
                passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public UserEntity authenticateClient(String username, String password) {
        UserEntity user = userRepository.findByUsername(username);
        if (user != null && user.getRole() == UserEntity.Role.CUSTOMER &&
                user.isActive() && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public void registerAdmin(UserRegistrationDto dto) throws Exception {
        validateRegistration(dto);

        UserEntity user = UserEntity.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(UserEntity.Role.ADMIN)
                .isActive(true)
                .build();

        userRepository.save(user);
    }

    public void registerClient(UserRegistrationDto dto) throws Exception {
        validateRegistration(dto);

        UserEntity user = UserEntity.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .fullName(dto.getFullName())
                .phoneNumber(dto.getPhoneNumber())
                .gender(dto.getGender())
                .role(UserEntity.Role.CUSTOMER)
                .isActive(true)
                .build();

        userRepository.save(user);
    }

    private void validateRegistration(UserRegistrationDto dto) throws Exception {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new Exception("Tên đăng nhập đã tồn tại!");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new Exception("Email đã được sử dụng!");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new Exception("Mật khẩu xác nhận không khớp!");
        }

        if (dto.getPassword().length() < 6) {
            throw new Exception("Mật khẩu phải có ít nhất 6 ký tự!");
        }
    }

    public UserEntity getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}