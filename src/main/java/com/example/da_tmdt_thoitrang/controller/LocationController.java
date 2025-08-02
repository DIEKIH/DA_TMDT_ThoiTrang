package com.example.da_tmdt_thoitrang.controller;

import com.example.da_tmdt_thoitrang.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping("/provinces")
    public List<Map<String, Object>> getProvinces() {
        return locationService.getProvinces();
    }

    @GetMapping("/districts/{provinceCode}")
    public List<Map<String, Object>> getDistricts(@PathVariable String provinceCode) {
        return locationService.getDistrictsByProvince(provinceCode);
    }

    @GetMapping("/wards/{districtCode}")
    public List<Map<String, Object>> getWards(@PathVariable String districtCode) {
        return locationService.getWardsByDistrict(districtCode);
    }
}

