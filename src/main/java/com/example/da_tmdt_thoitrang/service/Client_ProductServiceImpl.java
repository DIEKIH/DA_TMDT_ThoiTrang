//package com.example.da_tmdt_thoitrang.service;
//
//import com.example.da_tmdt_thoitrang.entity.ProductEntity;
//import com.example.da_tmdt_thoitrang.entity.ProductImageEntity;
//import com.example.da_tmdt_thoitrang.repository.ProductImageRepository;
//import com.example.da_tmdt_thoitrang.repository.ProductRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class Client_ProductServiceImpl implements Client_ProductService {
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private ProductImageRepository productImageRepository;
//
////    @Override
////    public List<ProductEntity> getAllActiveProducts() {
////        return productRepository.findByIsActiveTrue();
////    }
//
//    @Override
//    public List<ProductEntity> getAllActiveProducts() {
//        return productRepository.findByIsActiveTrueAndImageUrlIsNotNull();
//    }
//
//
//    @Override
//    public ProductEntity getProductById(Long id) {
//        return productRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
//    }
//
//    public ProductEntity getProductWithImages(Long id) {
//        return productRepository.findById(id).orElse(null); // Assumes images fetched with @OneToMany
//    }
//
//    public ProductImageEntity getProductImageById(Long imageId) {
//        return productImageRepository.findById(imageId).orElse(null);
//    }
//
//    public void incrementViewCount(Long productId) {
//        ProductEntity product = productRepository.findById(productId)
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//        product.setViewCount(product.getViewCount() + 1);
//        productRepository.save(product);
//    }
//}
