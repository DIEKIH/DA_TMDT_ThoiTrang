package com.example.da_tmdt_thoitrang.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Id;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String fullName;
    private String phoneNumber;
    private String gender;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    private Role role = Role.CUSTOMER;

    public enum Role {
        ADMIN,
        CUSTOMER
    }

    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AddressEntity> addresses = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CartEntity cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderEntity> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewEntity> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WishlistEntity> wishlists = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SearchHistoryEntity> searchHistories = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductViewEntity> productViews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoucherUsageEntity> voucherUsages = new ArrayList<>();

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                // KHÔNG đưa danh sách Address vào
                '}';
    }


    // Getters and Setters
//    public Long getId() { return id; }
//    public String getUsername() { return username; }
//    public String getEmail() { return email; }
//    public void setPassword(String password) { this.password = password; }
//    public void updateProfile(String fullName, String phone, String gender) {
//        this.fullName = fullName;
//        this.phoneNumber = phone;
//        this.gender = gender;
//    }
    public Boolean isAdmin() { return role == Role.ADMIN; }
    public Boolean isActive() { return isActive; }

    public Role getRole() {
        return role;
    }

    // Other getters and setters...
}

