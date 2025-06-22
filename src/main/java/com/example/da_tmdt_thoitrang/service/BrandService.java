package com.example.da_tmdt_thoitrang.service;

//import com.example.da_tmdt_thoitrang.entity.BrandEntity;
//
//import java.util.List;
//
//public interface BrandService {
//    List<BrandEntity> getAllBrands();
//    BrandEntity getBrandById(Long id);
//    BrandEntity saveBrand(BrandEntity brand);
//    void deleteBrand(Long id);
//}

import com.example.da_tmdt_thoitrang.entity.BrandEntity;
import com.example.da_tmdt_thoitrang.entity.CategoryEntity;
import com.example.da_tmdt_thoitrang.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    public List<BrandEntity> getAllBrands() {
        List<BrandEntity> brands = brandRepository.findAll();
        System.out.println("Number of brands retrieved: " + brands.size()); // Thêm dòng này
        return brands;
    }

    public List<BrandEntity> getActiveBrands() {
        return brandRepository.findByIsActiveTrue();
    }

    public Optional<BrandEntity> getBrandById(Long id) {
        return brandRepository.findById(id);
    }

    public BrandEntity saveBrand(BrandEntity brand) {
        // Kiểm tra tên thương hiệu đã tồn tại chưa
        Optional<BrandEntity> existingBrand = brandRepository.findByNameIgnoreCase(brand.getName());
        if (existingBrand.isPresent() && !existingBrand.get().getId().equals(brand.getId())) {
            throw new RuntimeException("Tên thương hiệu đã tồn tại!");
        }
        return brandRepository.save(brand);
    }

    public void deleteBrand(Long id) {
        Optional<BrandEntity> brand = brandRepository.findById(id);
        if (brand.isPresent()) {
            // Soft delete - chỉ đánh dấu là không hoạt động
            BrandEntity brandEntity = brand.get();
            brandEntity.setIsActive(false);
            brandRepository.save(brandEntity);
        } else {
            throw new RuntimeException("Không tìm thấy thương hiệu!");
        }
    }

    public List<BrandEntity> findAll() {
        return brandRepository.findAll(); // phải trả về danh sách có dữ liệu
    }


    public void activateBrand(Long id) {
        Optional<BrandEntity> brand = brandRepository.findById(id);
        if (brand.isPresent()) {
            brand.get().activate();
            brandRepository.save(brand.get());
        }
    }

    public void deactivateBrand(Long id) {
        Optional<BrandEntity> brand = brandRepository.findById(id);
        if (brand.isPresent()) {
            brand.get().deactivate();
            brandRepository.save(brand.get());
        }
    }

    public List<BrandEntity> searchBrands(String keyword) {
        return brandRepository.findByNameContainingIgnoreCase(keyword);
    }
}