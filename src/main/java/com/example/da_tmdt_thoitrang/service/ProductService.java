package com.example.da_tmdt_thoitrang.service;

import com.example.da_tmdt_thoitrang.dto.ProductDTO;
import com.example.da_tmdt_thoitrang.entity.ProductEntity;
import com.example.da_tmdt_thoitrang.entity.ProductImageEntity;
import com.example.da_tmdt_thoitrang.repository.ProductRepository;
import com.example.da_tmdt_thoitrang.repository.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    private static final String UPLOAD_DIR = "uploads/products/";

    public Page<ProductEntity> getFilteredProducts(String search, Long categoryId,
                                                   Boolean isActive, Pageable pageable) {
        Specification<ProductEntity> spec = Specification.where(null);

        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"));
        }

        if (categoryId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("categoryId"), categoryId));
        }

        if (isActive != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("isActive"), isActive));
        }

        return productRepository.findAll(spec, pageable);
    }

    public ProductEntity getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public ProductEntity saveProduct(ProductDTO productDTO, MultipartFile mainImage,
                                     MultipartFile[] additionalImages) throws IOException {
        ProductEntity product = ProductEntity.builder()
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .basePrice(productDTO.getBasePrice())
                .categoryId(productDTO.getCategoryId())
                .brandId(productDTO.getBrandId())
                .isActive(productDTO.getIsActive())
                .viewCount(0)
                .averageRating(BigDecimal.ZERO)
                .build();

        // Lưu ảnh chính
        if (mainImage != null && !mainImage.isEmpty()) {
            String imageUrl = saveImage(mainImage);
            product.setImageUrl(imageUrl);
        }

        ProductEntity savedProduct = productRepository.save(product);

        // Lưu ảnh phụ
        if (additionalImages != null && additionalImages.length > 0) {
            saveAdditionalImages(savedProduct, additionalImages);
        }

        return savedProduct;
    }

    public ProductEntity updateProduct(ProductDTO productDTO, MultipartFile mainImage,
                                       MultipartFile[] additionalImages) throws IOException {
        ProductEntity existingProduct = getProductById(productDTO.getId());
        if (existingProduct == null) {
            throw new RuntimeException("Sản phẩm không tồn tại");
        }

        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setBasePrice(productDTO.getBasePrice());
        existingProduct.setCategoryId(productDTO.getCategoryId());
        existingProduct.setBrandId(productDTO.getBrandId());
        existingProduct.setIsActive(productDTO.getIsActive());

        // Cập nhật ảnh chính nếu có
        if (mainImage != null && !mainImage.isEmpty()) {
            String imageUrl = saveImage(mainImage);
            existingProduct.setImageUrl(imageUrl);
        }

        ProductEntity updatedProduct = productRepository.save(existingProduct);

        // Cập nhật ảnh phụ nếu có
        if (additionalImages != null && additionalImages.length > 0) {
            saveAdditionalImages(updatedProduct, additionalImages);
        }

        return updatedProduct;
    }

    public void softDeleteProduct(Long id) {
        ProductEntity product = getProductById(id);
        if (product != null) {
            product.setIsActive(false);
            productRepository.save(product);
        }
    }

    public ProductEntity toggleProductStatus(Long id) {
        ProductEntity product = getProductById(id);
        if (product != null) {
            product.setIsActive(!product.getIsActive());
            return productRepository.save(product);
        }
        throw new RuntimeException("Sản phẩm không tồn tại");
    }

    public Map<String, Object> getProductStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", productRepository.count());
        stats.put("activeProducts", productRepository.countByIsActive(true));
        stats.put("inactiveProducts", productRepository.countByIsActive(false));

        return stats;
    }

    private String saveImage(MultipartFile file) throws IOException {
        // Tạo thư mục nếu chưa tồn tại
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Tạo tên file unique
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        // Lưu file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        return "/uploads/products/" + filename;
    }

    private void saveAdditionalImages(ProductEntity product, MultipartFile[] images) throws IOException {
        for (int i = 0; i < images.length; i++) {
            if (images[i] != null && !images[i].isEmpty()) {
                String imageUrl = saveImage(images[i]);

                ProductImageEntity productImage = ProductImageEntity.builder()
                        .productId(product.getId())
                        .imageUrl(imageUrl)
                        .isPrimary(false)
                        .sortOrder(i + 1)
                        .build();

                productImageRepository.save(productImage);
            }
        }
    }
}