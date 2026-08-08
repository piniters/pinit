package com.piniters.pinit.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
public class TourApiService {

    @Value("${tour.api.key}")
    private String serviceKey; // yml에서 가져옴

    private final RestTemplate restTemplate = new RestTemplate();

    public String getNearbyTourists(double mapX, double mapY, int radius) {

        // String.format을 이용해 직접 URL 문자열을 조립
        String url = String.format(
                "https://apis.data.go.kr/B551011/KorService2/locationBasedList2?serviceKey=%s&numOfRows=50&pageNo=1&MobileOS=ETC&MobileApp=Pinit&_type=json&mapX=%f&mapY=%f&radius=%d",
                serviceKey, mapX, mapY, radius
        );

        log.info("[TourAPI 호출] 최종 URL: {}", url);

        URI uri = URI.create(url);
        return restTemplate.getForObject(uri, String.class);
    }
}