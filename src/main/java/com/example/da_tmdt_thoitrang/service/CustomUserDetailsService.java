//package com.example.da_tmdt_thoitrang.service;
//
//import com.example.da_tmdt_thoitrang.entity.UserEntity;
//import com.example.da_tmdt_thoitrang.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.*;
//import org.springframework.stereotype.Service;
//import java.util.Collections;
//
//@Service
//@RequiredArgsConstructor
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        UserEntity user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User không tồn tại"));
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getUsername(),
//                user.getPassword(), // mật khẩu đã mã hóa
//                Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")) // hoặc ROLE_USER
//        );
//    }
//}