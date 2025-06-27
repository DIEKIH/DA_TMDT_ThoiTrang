package com.example.da_tmdt_thoitrang.service;


import com.example.da_tmdt_thoitrang.entity.ProductEntity;
import com.example.da_tmdt_thoitrang.entity.ProductImageEntity;
import com.example.da_tmdt_thoitrang.repository.ProductImageRepository;
import com.example.da_tmdt_thoitrang.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    public ProductEntity getProductById(Long id) {
        Optional<ProductEntity> product = productRepository.findById(id);
        return product.orElse(null);
    }

    public ProductEntity saveProduct(ProductEntity product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        // Xóa tất cả ảnh phụ trước
        productImageRepository.deleteByProductId(id);
        // Xóa sản phẩm
        productRepository.deleteById(id);
    }

    public List<ProductImageEntity> saveProductImages(List<ProductImageEntity> productImages) {
        return productImageRepository.saveAll(productImages);
    }

    public void deleteProductImage(Long imageId) {
        productImageRepository.deleteById(imageId);
    }

    public List<ProductImageEntity> getProductImages(Long productId) {
        return productImageRepository.findByProductIdOrderBySortOrder(productId);
    }

    public int getMaxSortOrderForProduct(Long productId) {
        Integer maxOrder = productImageRepository.findMaxSortOrderByProductId(productId);
        return maxOrder != null ? maxOrder : 0;
    }

    public List<ProductEntity> getActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }

    public List<ProductEntity> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId);
    }

    public List<ProductEntity> getProductsByBrand(Long brandId) {
        return productRepository.findByBrandIdAndIsActiveTrue(brandId);
    }

    public List<ProductEntity> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(keyword);
    }

    // Trong ProductService, bổ sung các phương thức sau:

    /**
     * Lấy danh sách ảnh phụ của sản phẩm
     */
    public List<ProductImageEntity> getProductImagesByProductId(Long productId) {
        return productImageRepository.findByProductIdOrderBySortOrder(productId);
    }

    /**
     * Lấy ảnh phụ theo ID
     */
    public ProductImageEntity getProductImageById(Long imageId) {
        return productImageRepository.findById(imageId).orElse(null);
    }

    /**
     * Xóa ảnh phụ
     */
//    public void deleteProductImage(Long imageId) {
//        productImageRepository.deleteById(imageId);
//    }
//
//    /**
//     * Lấy sortOrder lớn nhất của ảnh phụ cho sản phẩm
//     */
//    public int getMaxSortOrderForProduct(Long productId) {
//        Integer maxSortOrder = productImageRepository.findMaxSortOrderByProductId(productId);
//        return maxSortOrder != null ? maxSortOrder : 0;
//    }
//
//    /**
//     * Lưu danh sách ảnh phụ
//     */
//    public void saveProductImages(List<ProductImageEntity> productImages) {
//        productImageRepository.saveAll(productImages);
//    }
//
//    /**
//     * Lưu sản phẩm
//     */
//    public ProductEntity saveProduct(ProductEntity product) {
//        return productRepository.save(product);
//    }
//
//    /**
//     * Lấy sản phẩm theo ID
//     */
//    public ProductEntity getProductById(Long id) {
//        return productRepository.findById(id).orElse(null);
//    }
}