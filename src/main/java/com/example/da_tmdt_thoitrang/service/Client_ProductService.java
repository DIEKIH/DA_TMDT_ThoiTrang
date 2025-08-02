package com.example.da_tmdt_thoitrang.service;

import com.example.da_tmdt_thoitrang.entity.ProductEntity;
import com.example.da_tmdt_thoitrang.entity.ProductImageEntity;
import com.example.da_tmdt_thoitrang.repository.ProductImageRepository;
import com.example.da_tmdt_thoitrang.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Client_ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    /**
     * Lấy tất cả sản phẩm đang hoạt động
     */
    public List<ProductEntity> getAllActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }

    /**
     * Lấy sản phẩm theo ID
     */
    public ProductEntity getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
    }

    /**
     * Lấy sản phẩm kèm theo danh sách hình ảnh
     */
    public ProductEntity getProductWithImages(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
    }

    /**
     * Lấy hình ảnh sản phẩm theo ID hình ảnh
     */
    public ProductImageEntity getProductImageById(Long imageId) {
        return productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hình ảnh với ID: " + imageId));
    }

    /**
     * Tăng số lượt xem sản phẩm
     */
    public void incrementViewCount(Long productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));

        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);
    }

    /**
     * Kiểm tra sản phẩm có tồn tại và đang hoạt động không
     */
    public boolean isProductActiveAndExists(Long id) {
        return productRepository.existsByIdAndIsActiveTrue(id);
    }

    /**
     * Lấy sản phẩm theo danh mục
     */
    public List<ProductEntity> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryIdAndIsActiveTrueOrderByCreatedAtDesc(categoryId);
    }

    /**
     * Lấy sản phẩm theo thương hiệu
     */
    public List<ProductEntity> getProductsByBrand(Long brandId) {
        return productRepository.findByBrandIdAndIsActiveTrueOrderByCreatedAtDesc(brandId);
    }

    /**
     * Tìm kiếm sản phẩm theo tên
     */
    public List<ProductEntity> searchProductsByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCaseAndIsActiveTrueOrderByCreatedAtDesc(keyword);
    }

    /**
     * Lấy sản phẩm được xem nhiều nhất
     */
    public List<ProductEntity> getMostViewedProducts(int limit) {
        return productRepository.findByIsActiveTrueOrderByViewCountDescCreatedAtDesc()
                .stream()
                .limit(limit)
                .toList();
    }

    /**
     * Lấy sản phẩm mới nhất
     */
    public List<ProductEntity> getLatestProducts(int limit) {
        return productRepository.findByIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .limit(limit)
                .toList();
    }

    /**
     * Lấy sản phẩm theo khoảng giá
     */
    public List<ProductEntity> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByIsActiveTrueAndPriceBetweenOrderByPriceAsc(minPrice, maxPrice);
    }

    /**
     * Lấy sản phẩm liên quan (cùng danh mục, trừ sản phẩm hiện tại)
     */
    public List<ProductEntity> getRelatedProducts(Long productId, Long categoryId, int limit) {
        return productRepository.findByCategoryIdAndIsActiveTrueAndIdNotOrderByViewCountDesc(categoryId, productId)
                .stream()
                .limit(limit)
                .toList();
    }
}