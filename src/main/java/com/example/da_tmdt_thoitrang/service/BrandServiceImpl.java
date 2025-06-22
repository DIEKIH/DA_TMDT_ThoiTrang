//package com.example.da_tmdt_thoitrang.service;
//
//import com.example.da_tmdt_thoitrang.entity.BrandEntity;
//import com.example.da_tmdt_thoitrang.repository.BrandRepository;
//import com.example.da_tmdt_thoitrang.service.BrandService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class BrandServiceImpl implements BrandService {
//
//    @Autowired
//    private BrandRepository brandRepository;
//
//    @Override
//    public List<BrandEntity> getAllBrands() {
//        return brandRepository.findAll();
//    }
//
//    @Override
//    public BrandEntity getBrandById(Long id) {
//        return brandRepository.findById(id).orElse(null);
//    }
//
//    @Override
//    public BrandEntity saveBrand(BrandEntity brand) {
//        return brandRepository.save(brand);
//    }
//
//    @Override
//    public void deleteBrand(Long id) {
//        brandRepository.deleteById(id);
//    }
//}