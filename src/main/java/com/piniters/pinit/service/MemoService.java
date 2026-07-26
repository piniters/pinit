package com.piniters.pinit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piniters.pinit.dto.MemoRequestDto;
import com.piniters.pinit.dto.MemoResponseDto;
import com.piniters.pinit.entity.Memo;
import com.piniters.pinit.entity.User;
import com.piniters.pinit.repository.MemoRepository;
import com.piniters.pinit.repository.UserRepository;
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
    private final UserRepository userRepository;

    // application.yml에 적어둔 카카오 키
    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public static class AddressInfo {
        public String roadAddress;
        public String jibunAddress;
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

    // 특정 ID의 메모 상세 조회
    @Transactional(readOnly = true)
    public MemoResponseDto getMemoById(Long memoId) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메모가 존재하지 않습니다. memoId = " + memoId));

        return new MemoResponseDto(memo);
    }

    // 특정 ID의 메모 삭제
    @Transactional
    public void deleteMemo(Long userId, Long memoId) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 메모가 존재하지 않습니다. memoId = " + memoId));

        if (memo.getUser() == null || !memo.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("작성자 본인만 삭제할 수 있습니다.");
        }
        memoRepository.delete(memo);
    }

    // 특정 ID의 메모 수정
    @Transactional
    public Long updateMemo(Long userId, Long memoId, MemoRequestDto requestDto) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 메모가 존재하지 않습니다. memoId = " + memoId));

        if (memo.getUser() == null || !memo.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("작성자 본인만 수정할 수 있습니다.");
        }

        memo.setContent(requestDto.getContent());
        memo.setVisibility(requestDto.getVisibility());
        return memo.getMemoId();
    }


    // 메모 생성 메서드 (유저 ID를 함께 받아서 저장)
    @Transactional
    public Long createMemo(Long userId, MemoRequestDto requestDto) {

        // 1. 유저 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 2. 카카오 API 연동 (위/경도로 주소 추출)
        AddressInfo addressInfo = getAddressFromKakao(requestDto.getLatitude(), requestDto.getLongitude());

        // 3. 엔티티 생성 및 값 세팅 (유저 정보 + 카카오 주소 통합)
        Memo memo = new Memo();
        memo.setUser(user);
        memo.setContent(requestDto.getContent());
        memo.setLatitude(requestDto.getLatitude());
        memo.setLongitude(requestDto.getLongitude());
        memo.setRoadAddress(addressInfo.roadAddress);
        memo.setJibunAddress(addressInfo.jibunAddress);
        memo.setPlaceName(requestDto.getPlaceName());
        memo.setVisibility(requestDto.getVisibility());
        memo.setQuestionId(requestDto.getQuestionId());
        memo.setCreatedAt(LocalDateTime.now());

        Memo savedMemo = memoRepository.save(memo);
        return savedMemo.getMemoId();
    }

    @Transactional(readOnly = true)
    public List<MemoResponseDto> getMyMemos(Long userId) {
        return memoRepository.findByUser_UserIdOrderByCreatedAtDesc(userId).stream()
                .map(MemoResponseDto::new)
                .collect(Collectors.toList());
    }
}
