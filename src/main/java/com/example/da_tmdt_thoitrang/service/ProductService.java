package com.example.da_tmdt_thoitrang.service;

import com.example.da_tmdt_thoitrang.dto.CreateProductRequest;
import com.example.da_tmdt_thoitrang.dto.ProductDTO;
import com.example.da_tmdt_thoitrang.dto.ProductImageDTO;
import com.example.da_tmdt_thoitrang.dto.UpdateProductRequest;
import com.example.da_tmdt_thoitrang.entity.ProductEntity;
import com.example.da_tmdt_thoitrang.entity.ProductImageEntity;
import com.example.da_tmdt_thoitrang.repository.BrandRepository;
import com.example.da_tmdt_thoitrang.repository.CategoryRepository;
import com.example.da_tmdt_thoitrang.repository.ProductImageRepository;
import com.example.da_tmdt_thoitrang.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::convertToDTO);
    }

    public Page<ProductDTO> getActiveProducts(Pageable pageable) {
        return productRepository.findAllActive(pageable).map(this::convertToDTO);
    }

    public Page<ProductDTO> getInactiveProducts(Pageable pageable) {
        return productRepository.findAllInactive(pageable).map(this::convertToDTO);
    }

    public Page<ProductDTO> getOutOfStockProducts(Pageable pageable) {
        return productRepository.findOutOfStockProducts(pageable).map(this::convertToDTO);
    }

    public Page<ProductDTO> getLowStockProducts(Pageable pageable) {
        return productRepository.findLowStockProducts(pageable).map(this::convertToDTO);
    }

    public ProductDTO getProductById(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

        // Tăng view count
        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);

        return convertToDTO(product);
    }

    public Page<ProductDTO> searchProducts(String name, Long categoryId, Long brandId,
                                           BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.searchProducts(name, categoryId, brandId, minPrice, maxPrice, pageable)
                .map(this::convertToDTO);
    }

    public ProductDTO createProduct(CreateProductRequest request) {
        // Validate category and brand exist
        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new RuntimeException("Không tìm thấy danh mục với ID: " + request.getCategoryId());
        }

        if (!brandRepository.existsById(request.getBrandId())) {
            throw new RuntimeException("Không tìm thấy thương hiệu với ID: " + request.getBrandId());
        }

        // Check SKU uniqueness
        if (request.getSku() != null && productRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("SKU đã tồn tại: " + request.getSku());
        }

        ProductEntity product = new ProductEntity();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSize(request.getSize());
        product.setColor(request.getColor());
        product.setQuantity(request.getQuantity());
        product.setPrice(request.getPrice());
        product.setSku(request.getSku());
        product.setCategoryId(request.getCategoryId());
        product.setBrandId(request.getBrandId());
        product.setIsActive(true);
        product.setViewCount(0);
        product.setAverageRating(BigDecimal.ZERO);

        ProductEntity savedProduct = productRepository.save(product);

        // Save images
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            saveProductImages(savedProduct.getId(), request.getImageUrls());
        }

        return convertToDTO(savedProduct);
    }

    public ProductDTO updateProduct(Long id, UpdateProductRequest request) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

        // Check SKU uniqueness if changed
        if (request.getSku() != null && !request.getSku().equals(product.getSku())) {
            if (productRepository.existsBySkuAndIdNot(request.getSku(), id)) {
                throw new RuntimeException("SKU đã tồn tại: " + request.getSku());
            }
        }

        // Update fields if provided
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getSize() != null) product.setSize(request.getSize());
        if (request.getColor() != null) product.setColor(request.getColor());
        if (request.getQuantity() != null) product.setQuantity(request.getQuantity());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getSku() != null) product.setSku(request.getSku());
        if (request.getCategoryId() != null) {
            if (!categoryRepository.existsById(request.getCategoryId())) {
                throw new RuntimeException("Không tìm thấy danh mục với ID: " + request.getCategoryId());
            }
            product.setCategoryId(request.getCategoryId());
        }
        if (request.getBrandId() != null) {
            if (!brandRepository.existsById(request.getBrandId())) {
                throw new RuntimeException("Không tìm thấy thương hiệu với ID: " + request.getBrandId());
            }
            product.setBrandId(request.getBrandId());
        }

        ProductEntity savedProduct = productRepository.save(product);

        // Update images if provided
        if (request.getImageUrls() != null) {
            productImageRepository.deleteByProductId(id);
            if (!request.getImageUrls().isEmpty()) {
                saveProductImages(id, request.getImageUrls());
            }
        }

        return convertToDTO(savedProduct);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + id);
        }
        productRepository.deleteById(id);
    }

    public ProductDTO toggleProductStatus(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

        product.setIsActive(!product.getIsActive());
        ProductEntity savedProduct = productRepository.save(product);

        return convertToDTO(savedProduct);
    }

    public ProductDTO updateStock(Long id, Integer quantity) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

        product.setQuantity(quantity);
        ProductEntity savedProduct = productRepository.save(product);

        return convertToDTO(savedProduct);
    }

    public List<ProductImageDTO> uploadProductImages(Long productId, List<MultipartFile> files) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId);
        }

        List<String> imageUrls = files.stream()
                .map(file -> {
                    try {
                        return fileStorageService.storeFile(file, "products");
                    } catch (Exception e) {
                        throw new RuntimeException("Lỗi upload file: " + e.getMessage());
                    }
                })
                .collect(Collectors.toList());

        return saveProductImages(productId, imageUrls);
    }

    private List<ProductImageDTO> saveProductImages(Long productId, List<String> imageUrls) {
        AtomicInteger sortOrder = new AtomicInteger(0);

        List<ProductImageEntity> imageEntities = imageUrls.stream()
                .map(url -> {
                    ProductImageEntity image = new ProductImageEntity();
                    image.setProductId(productId);
                    image.setImageUrl(url);
                    image.setIsPrimary(sortOrder.get() == 0); // First image is primary
                    image.setSortOrder(sortOrder.getAndIncrement());
                    return image;
                })
                .collect(Collectors.toList());

        List<ProductImageEntity> savedImages = productImageRepository.saveAll(imageEntities);

        return savedImages.stream()
                .map(this::convertImageToDTO)
                .collect(Collectors.toList());
    }

//    private ProductDTO convertToDTO(ProductEntity entity) {
//        ProductDTO dto = new ProductDTO();
//        dto.setId(entity.getId());
//        dto.setName(entity.getName());
//        dto.setDescription(entity.getDescription());
//        dto.setImageUrl(entity.getImageUrl());
//        dto.setSize(entity.getSize());
//        dto.setColor(entity.getColor());
//        dto.setQuantity(entity.getQuantity());
//        dto.setPrice(entity.getPrice());
//        dto.setSku(entity.getSku());
//        dto.setIsActive(entity.getIsActive());
//        dto.setCategoryId(entity.getCategoryId());
//        dto.setBrandId(entity.getBrandId());
//        dto.setCreatedAt(entity.getCreatedAt());
//        dto.setUpdatedAt(entity.getUpdatedAt());
//        dto.setViewCount(entity.getViewCount());
//        dto.setAverageRating(entity.getAverageRating());
//
//        // Set category and brand names
//        if (entity.getCategory() != null) {
//            dto.setCategoryName(entity.getCategory().getName());
//        }
//        if (entity.getBrand() != null) {
//            dto.setBrandName(entity.getBrand().getName());
//        }
//
//        // Set product images
//        List<ProductImageEntity> images = productImageRepository
//                .findByProductIdOrderByPrimaryAndSort(entity.getId());
//        dto.setProductImages(images.stream()
//                .map(this::convertImageToDTO)
//                .collect(Collectors.toList()));
//
//        return dto;
//    }
private ProductDTO convertToDTO(ProductEntity entity) {
    ProductDTO dto = new ProductDTO();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    dto.setDescription(entity.getDescription());
    dto.setImageUrl(entity.getImageUrl());
    dto.setSize(entity.getSize());
    dto.setColor(entity.getColor());
    dto.setQuantity(entity.getQuantity());
    dto.setPrice(entity.getPrice());
    dto.setSku(entity.getSku());
    dto.setIsActive(entity.getIsActive());
    dto.setCategoryId(entity.getCategoryId());
    dto.setBrandId(entity.getBrandId());
    dto.setCreatedAt(entity.getCreatedAt());
    dto.setUpdatedAt(entity.getUpdatedAt());
    dto.setViewCount(entity.getViewCount());
    dto.setAverageRating(entity.getAverageRating());

    // An toàn khi truy cập Category và Brand
    try {
        if (entity.getCategory() != null) {
            dto.setCategoryName(entity.getCategory().getName());
        } else {
            dto.setCategoryName("Không xác định");
        }
    } catch (jakarta.persistence.EntityNotFoundException ex) {
        dto.setCategoryName("Không tồn tại");
    }

    try {
        if (entity.getBrand() != null) {
            dto.setBrandName(entity.getBrand().getName());
        } else {
            dto.setBrandName("Không xác định");
        }
    } catch (jakarta.persistence.EntityNotFoundException ex) {
        dto.setBrandName("Không tồn tại");
    }

    // Ảnh sản phẩm
    List<ProductImageEntity> images = productImageRepository
            .findByProductIdOrderByPrimaryAndSort(entity.getId());
    dto.setProductImages(images.stream()
            .map(this::convertImageToDTO)
            .collect(Collectors.toList()));

    return dto;
}


    private ProductImageDTO convertImageToDTO(ProductImageEntity entity) {
        ProductImageDTO dto = new ProductImageDTO();
        dto.setId(entity.getId());
        dto.setProductId(entity.getProductId());
        dto.setImageUrl(entity.getImageUrl());
        dto.setIsPrimary(entity.getIsPrimary());
        dto.setSortOrder(entity.getSortOrder());
        return dto;
    }

    public void bulkActivate(List<Long> ids) {
        productRepository.updateIsActiveByIds(true, ids);
    }

    public void bulkDeactivate(List<Long> ids) {
        productRepository.updateIsActiveByIds(false, ids);
    }

    public void bulkDelete(List<Long> ids) {
        productRepository.deleteAllByIdInBatch(ids);
    }

}