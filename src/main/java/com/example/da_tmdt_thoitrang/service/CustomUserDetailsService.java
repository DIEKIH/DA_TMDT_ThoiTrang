//package com.example.da_tmdt_thoitrang.service;
//
//import com.example.da_tmdt_thoitrang.entity.UserEntity;
//import com.example.da_tmdt_thoitrang.repository.UserRepository;
//import org.springframework.stereotype.Service;
//
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    public CustomUserDetailsService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        UserEntity user = userRepository.findByUsername(username);
//        if (user == null) {
//            throw new UsernameNotFoundException("Không tìm thấy người dùng: " + username);
//        }
//
//        return new CustomUserDetails(user);
//    }
//}
