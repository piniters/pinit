package com.piniters.pinit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;

@Slf4j
@Service
public class TourApiService {

    @Value("${tour.api.key}")
    private String serviceKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getNearbyTourists(double mapX, double mapY, int radius) {
        String url = String.format(
                "https://apis.data.go.kr/B551011/KorService2/locationBasedList2?serviceKey=%s&numOfRows=50&pageNo=1&MobileOS=ETC&MobileApp=Pinit&_type=json&mapX=%f&mapY=%f&radius=%d",
                serviceKey, mapX, mapY, radius
        );

        log.info("[TourAPI 호출] 최종 URL: {}", url);

        try {
            URI uri = URI.create(url);
            String rawJsonResponse = restTemplate.getForObject(uri, String.class);

            // 실제 item 리스트만 파싱해서 추출하기
            JsonNode root = objectMapper.readTree(rawJsonResponse);
            JsonNode itemsNode = root.path("response").path("body").path("items").path("item");

            // 만약 item이 존재한다면 그 배열 페이지만 리턴, 없으면 빈 배열 리턴
            if (!itemsNode.isMissingNode() && !itemsNode.isNull()) {
                return objectMapper.writeValueAsString(itemsNode);
            } else {
                return "[]"; // 데이터가 없을 경우 빈 리스트 반환
            }

        } catch (Exception e) {
            log.error("TourAPI 파싱 중 에러 발생: {}", e.getMessage());
            return "[]";
        }
    }
}