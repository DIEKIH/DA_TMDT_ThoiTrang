//package com.example.da_tmdt_thoitrang.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import jakarta.persistence.Id; // ✅ Dùng đúng `@Id` của JPA
//
//
//@Entity
//@Table(name = "addresses")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class AddressEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private Long userId;
//
//    private String fullName;
//    private String phoneNumber;
//    private String street;
//    private String ward;
//    private String district;
//    private String city;
//
//    @Column(nullable = false)
//    private Boolean isDefault = false;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "userId", insertable = false, updatable = false)
//    private UserEntity user;
//
//    public Long getId() { return id; }
//    public String getFullAddress() {
//        return String.format("%s, %s, %s, %s", street, ward, district, city);
//    }
//    public void setAsDefault() { this.isDefault = true; }
//    public Boolean isDefault() { return isDefault; }
//
//    // Other getters and setters...
//}
package com.example.da_tmdt_thoitrang.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private String fullName;
    private String phoneNumber;
    private String street;
    private String ward;
    private String district;
    private String city;

    @Column(nullable = false)
    private Boolean isDefault = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private UserEntity user;

    @Override
    public String toString() {
        return "AddressEntity{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                '}';
    }



//    public Long getId() { return id; }
//
//    public String getFullAddress() {
//        return String.format("%s, %s, %s, %s", street, ward, district, city);
//    }
//
//    public void setAsDefault() {
//        this.isDefault = true;
//    }
//
//    public Boolean isDefault() {
//        return isDefault;
//    }
}
