package com.example.da_tmdt_thoitrang.repository;

import com.example.da_tmdt_thoitrang.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {

    List<AddressEntity> findByUserId(Long userId);


    void deleteByIdAndUserId(Long id, Long userId);

    Optional<AddressEntity> findByIdAndUserId(Long id, Long userId);

}