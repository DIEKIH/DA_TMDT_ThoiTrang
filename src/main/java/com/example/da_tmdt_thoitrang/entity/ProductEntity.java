//package com.example.da_tmdt_thoitrang.entity;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//import jakarta.persistence.Id;
//
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Entity
//@Table(name = "products")
//public class ProductEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String name;
//
//    @Column(columnDefinition = "TEXT")
//    private String description;
//
//    private String imageUrl;
//
//    @Column(columnDefinition = "DECIMAL(10,2)")
//    private BigDecimal basePrice;
//
//    @Column(nullable = false)
//    private Long categoryId;
//
//    @Column(nullable = false)
//    private Long brandId;
//
//    @Column(nullable = false)
//    private Boolean isActive = true;
//
//    @CreationTimestamp
//    private LocalDateTime createdAt;
//
//    @UpdateTimestamp
//    private LocalDateTime updatedAt;
//
//    @Column(nullable = false)
//    private Integer viewCount = 0;
//
//    @Column(precision = 3, scale = 2)
//    private BigDecimal averageRating = BigDecimal.ZERO;
//
//    // Relationships
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "categoryId", insertable = false, updatable = false)
//    private CategoryEntity category;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "brandId", insertable = false, updatable = false)
//    private BrandEntity brand;
//
//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<ProductDetailEntity> productDetails = new ArrayList<>();
//
//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<ProductImageEntity> productImages = new ArrayList<>();
//
//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<ReviewEntity> reviews = new ArrayList<>();
//
//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<WishlistEntity> wishlists = new ArrayList<>();
//
//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<ProductViewEntity> productViews = new ArrayList<>();
//
//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<ProductDiscountEntity> productDiscounts = new ArrayList<>();
//
////    public Long getId() { return id; }
////    public String getName() { return name; }
////    public BigDecimal getBasePrice() { return basePrice; }
////    public void updateRating(Double rating) {
////        if (rating >= 0.0 && rating <= 5.0) {
////            this.averageRating = rating;
////        }
////    }
////    public synchronized void incrementViewCount() {
////        this.viewCount++;
////    }
////    public Boolean isActive() { return isActive; }
////    public void activate() { this.isActive = true; }
////    public void deactivate() { this.isActive = false; }
//
//    // Other getters and setters...
//}

package com.example.da_tmdt_thoitrang.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    // ⚠️ Gộp các trường từ ProductDetailEntity vào đây
    private String size;
    private String color;

    @Column(nullable = false)
    private Integer quantity = 0;


    @Column(columnDefinition = "DECIMAL(10,2)")
    private BigDecimal price;

    @Column(unique = true)
    private String sku;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private Long brandId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @Column(precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    // Các quan hệ giữ nguyên
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", insertable = false, updatable = false)
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brandId", insertable = false, updatable = false)
    private BrandEntity brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @OrderBy("sortOrder ASC")
    private List<ProductImageEntity> productImages;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewEntity> reviews;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WishlistEntity> wishlists;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductViewEntity> productViews;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductDiscountEntity> productDiscounts;


    @Column(name = "available_sizes") // VD: "S,M,L"
    private String availableSizes;

    @Column(name = "available_colors") // VD: "Đỏ,Đen"
    private String availableColors;

    // Trả về List<String> để view hiển thị
    @Transient
    public List<String> getAvailableSizes() {
        if (availableSizes != null && !availableSizes.isEmpty()) {
            return Arrays.asList(availableSizes.split(","));
        }
        return Collections.emptyList();
    }

    @Transient
    public List<String> getAvailableColors() {
        if (availableColors != null && !availableColors.isEmpty()) {
            return Arrays.asList(availableColors.split(","));
        }
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "ProductEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categoryId=" + (category != null ? category.getId() : null) +
                '}';
    }



    // ⚠️ Nếu các entity như CartItemEntity hay InventoryEntity đang dùng ProductDetailEntity
    // → bạn cần sửa lại để dùng trực tiếp ProductEntity
}
