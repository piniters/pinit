package com.piniters.pinit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piniters.pinit.dto.MemoRequestDto;
import com.piniters.pinit.dto.MemoResponseDto;
import com.piniters.pinit.entity.Memo;
import com.piniters.pinit.repository.MemoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;

    // application.yml에 적어둔 카카오 키
    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public static class AddressInfo {
        public String roadAddress;
        public String jibunAddress;
    }

    @Transactional // DB에 데이터를 저장/수정할 때 안전하게 처리해주는 어노테이션
    public Long createMemo(MemoRequestDto requestDto) {

        Memo memo = new Memo();
        memo.setContent(requestDto.getContent());
        memo.setLatitude(requestDto.getLatitude());
        memo.setLongitude(requestDto.getLongitude());
        memo.setVisibility(requestDto.getVisibility());
        memo.setCreatedAt(LocalDateTime.now());

        AddressInfo addressInfo = getAddressFromKakao(requestDto.getLatitude(), requestDto.getLongitude());

        memo.setRoadAddress(addressInfo.roadAddress);
        memo.setJibunAddress(addressInfo.jibunAddress);

        //user 연관관계 매핑이나 questionId 설정 등은 지금은 임시로 생략
        // 하드코딩해둬도 됨 (나중에 로그인 기능 붙일 때 처리)

        // DB에 저장
        Memo savedMemo = memoRepository.save(memo);

        return savedMemo.getMemoId(); // 저장된 메모의 ID 반환
    }


    // 카카오 서버와 통신하는 메서드
    private AddressInfo getAddressFromKakao(Double latitude, Double longitude) {
        AddressInfo result = new AddressInfo();
        // 카카오 API 문서에 나와있는 요청 주소 (경도가 x, 위도가 y)
        String url = "https://dapi.kakao.com/v2/local/geo/coord2address.json?x=" + longitude + "&y=" + latitude;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // 카카오 서버로 GET 요청
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode documents = root.path("documents");

            if (documents.isArray() && !documents.isEmpty()) {
                JsonNode document = documents.get(0);
                // 1. 도로명 주소가 있으면 바구니에 담기
                JsonNode roadNode = document.path("road_address");
                if (!roadNode.isMissingNode() && !roadNode.isNull()) {
                    result.roadAddress = roadNode.path("address_name").asText();
                }

                // 2. 지번 주소가 있으면 바구니에 담기
                JsonNode jibunNode = document.path("address");
                if (!jibunNode.isMissingNode() && !jibunNode.isNull()) {
                    result.jibunAddress = jibunNode.path("address_name").asText();
                }
            }
        } catch (Exception e) {
            System.out.println("카카오 API 호출 중 에러 발생: " + e.getMessage());

        }

        return result;
    }

    // 저장된 모든 메모를 최신순으로 가져와서 DTO로 변환
    @Transactional(readOnly = true) // 읽기 전용
    public List<MemoResponseDto> getAllMemos() {
        return memoRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(MemoResponseDto::new)
                .collect(Collectors.toList());
    }

    // 사용자의 현재 위도, 경도와 반경을 받아 주변 메모만 DTO로 변환
    @Transactional(readOnly = true)
    public List<MemoResponseDto> getNarrowMemos(Double latitude, Double longitude, Double distance) {
        return memoRepository.findMemosWithinDistance(latitude, longitude, distance).stream()
                .map(MemoResponseDto::new)
                .collect(Collectors.toList());
    }
}
