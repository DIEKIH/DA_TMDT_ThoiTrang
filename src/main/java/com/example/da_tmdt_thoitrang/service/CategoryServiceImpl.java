//package com.example.da_tmdt_thoitrang.service;
//
//import com.example.da_tmdt_thoitrang.entity.CategoryEntity;
//import com.example.da_tmdt_thoitrang.repository.CategoryRepository;
//import com.example.da_tmdt_thoitrang.service.CategoryService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class CategoryServiceImpl implements CategoryRepository {
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @Override
//    public List<CategoryEntity> getAllCategories() {
//        return categoryRepository.findAll();
//    }
//
//    @Override
//    public CategoryEntity getCategoryById(Long id) {
//        return categoryRepository.findById(id).orElse(null);
//    }
//
//    @Override
//    public CategoryEntity saveCategory(CategoryEntity category) {
//        return categoryRepository.save(category);
//    }
//
//    @Override
//    public void deleteCategory(Long id) {
//        categoryRepository.deleteById(id);
//    }
//}
