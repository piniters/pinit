package com.piniters.pinit.controller;

import com.piniters.pinit.service.TourApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourController {

    private final TourApiService tourApiService;

    // GET 요청 : 내 주변 관광지 리스트 조회
    @GetMapping("/nearby")
    public ResponseEntity<String> getNearbyTours(
            @RequestParam("latitude") double latitude,  // 위도 (mapY)
            @RequestParam("longitude") double longitude, // 경도 (mapX)
            @RequestParam(value = "radius", defaultValue = "1000") int radius) { // 기본 반경 1km

        String responseJson = tourApiService.getNearbyTourists(longitude, latitude, radius);

        return ResponseEntity.ok(responseJson);
    }
}