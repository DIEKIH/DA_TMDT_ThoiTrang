package com.example.da_tmdt_thoitrang.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class LocationService {

    private final String BASE_URL = "https://provinces.open-api.vn/api/";

    public List<Map<String, Object>> getProvinces() {
        RestTemplate restTemplate = new RestTemplate();
        return Arrays.asList(restTemplate.getForObject(BASE_URL + "?depth=1", Map[].class));
    }

    public List<Map<String, Object>> getDistrictsByProvince(String provinceCode) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(BASE_URL + "p/" + provinceCode + "?depth=2", Map.class);
        return (List<Map<String, Object>>) response.get("districts");
    }

    public List<Map<String, Object>> getWardsByDistrict(String districtCode) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(BASE_URL + "d/" + districtCode + "?depth=2", Map.class);
        return (List<Map<String, Object>>) response.get("wards");
    }
}
