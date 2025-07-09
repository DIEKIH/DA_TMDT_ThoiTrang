package com.example.da_tmdt_thoitrang.service;

import com.example.da_tmdt_thoitrang.entity.AddressEntity;
import com.example.da_tmdt_thoitrang.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public List<AddressEntity> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    public AddressEntity saveAddress(AddressEntity address) {
        return addressRepository.save(address);
    }

    public void deleteAddress(Long addressId, Long userId) {
        if (addressRepository.findByIdAndUserId(addressId, userId).isEmpty()) {
            throw new RuntimeException("Không tìm thấy địa chỉ hoặc không có quyền xóa");
        }
        addressRepository.deleteByIdAndUserId(addressId, userId);
    }

    public Optional<AddressEntity> getAddressById(Long id, Long userId) {
        return addressRepository.findByIdAndUserId(id, userId);
    }
}