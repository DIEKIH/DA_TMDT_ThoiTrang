package com.example.da_tmdt_thoitrang.service;

import com.example.da_tmdt_thoitrang.entity.CategoryEntity;
import com.example.da_tmdt_thoitrang.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final String uploadDir = "uploads/categories/";

    public List<CategoryEntity> getAllCategories() {
        return categoryRepository.findAll(Sort.by("createdAt").descending());
    }
    public List<CategoryEntity> findAll() {
        return categoryRepository.findAll(); // phải trả về danh sách có dữ liệu
    }


    public List<CategoryEntity> findCategories(String search, String status) {
        if (search.isEmpty() && status.equals("all")) {
            return categoryRepository.findAll(Sort.by("createdAt").descending());
        } else if (search.isEmpty()) {
            boolean isActive = status.equals("active");
            return categoryRepository.findByIsActiveOrderByCreatedAtDesc(isActive);
        } else if (status.equals("all")) {
            return categoryRepository.findByNameContainingIgnoreCaseOrderByCreatedAtDesc(search);
        } else {
            boolean isActive = status.equals("active");
            return categoryRepository.findByNameContainingIgnoreCaseAndIsActiveOrderByCreatedAtDesc(search, isActive);
        }
    }

    public CategoryEntity findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    public CategoryEntity saveCategory(CategoryEntity category, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = saveImage(imageFile);
            category.setImageUrl(imageUrl);
        }
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        CategoryEntity category = findById(id);
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new RuntimeException("Không thể xóa danh mục có sản phẩm");
        }
        categoryRepository.deleteById(id);
    }

    public void toggleStatus(Long id) {
        CategoryEntity category = findById(id);
        category.setIsActive(!category.getIsActive());
        categoryRepository.save(category);
    }

    private String saveImage(MultipartFile file) throws IOException {
        // Tạo thư mục nếu chưa tồn tại
        Path uploadPath = Paths.get(uploadDir);
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

        return "/uploads/categories/" + filename;
    }
}