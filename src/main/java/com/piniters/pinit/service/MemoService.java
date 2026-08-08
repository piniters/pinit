package com.piniters.pinit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piniters.pinit.dto.CommentRequestDto;
import com.piniters.pinit.dto.CommentResponseDto;
import com.piniters.pinit.dto.MemoRequestDto;
import com.piniters.pinit.dto.MemoResponseDto;
import com.piniters.pinit.entity.Comment;
import com.piniters.pinit.entity.Likes;
import com.piniters.pinit.entity.Memo;
import com.piniters.pinit.entity.User;
import com.piniters.pinit.repository.LikesRepository;
import com.piniters.pinit.repository.MemoRepository;
import com.piniters.pinit.repository.UserRepository;
import com.piniters.pinit.repository.CommentRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;
    private final UserRepository userRepository;
    private final LikesRepository likesRepository;
    private final CommentRepository commentRepository;

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

    //내 메모 보기
    @Transactional(readOnly = true)
    public List<MemoResponseDto> getMyMemos(Long userId) {
        return memoRepository.findByUser_UserIdOrderByCreatedAtDesc(userId).stream()
                .map(MemoResponseDto::new)
                .collect(Collectors.toList());
    }

    // 특정 메모에 좋아요 추가/취소
    @Transactional
    public String toggleLike(Long userId, Long memoId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메모입니다."));

        // 유저와 메모 정보를 바탕으로 기존 좋아요 기록이 있는지 조회
        Optional<Likes> existingLike = likesRepository.findByUserAndMemo(user, memo);

        // 과거 데이터의 likeCount가 null일 경우 0으로 초기화
        int currentCount = (memo.getLikeCount() != null) ? memo.getLikeCount() : 0;

        if (existingLike.isPresent()) {
            // 이미 기록이 있다면 -> 좋아요 삭제
            likesRepository.delete(existingLike.get());

            // 메모의 좋아요 수 감소
            memo.setLikeCount(Math.max(0, currentCount - 1));

            return "좋아요가 취소되었습니다.";
        } else {
            // 기록이 없다면 -> 좋아요 생성
            Likes newLike = new Likes();
            newLike.setUser(user);
            newLike.setMemo(memo);
            newLike.setCreatedAt(LocalDateTime.now());

            likesRepository.save(newLike);

            // 메모의 좋아요 수 증가
            memo.setLikeCount(currentCount + 1);

            return "좋아요가 추가되었습니다.";
        }
    }

    //댓글 달기
    @Transactional
    public Long createComment(Long userId, Long memoId, CommentRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메모입니다."));

        if (requestDto.getParentId() != null) {
            // 1. 부모 댓글이 실제로 DB에 존재하는지, 삭제되지는 않았는지 확인
            Comment parentComment = commentRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 삭제된 부모 댓글입니다."));

            if (parentComment.getDeletedAt() != null) {
                throw new IllegalArgumentException("삭제된 댓글에는 대댓글을 달 수 없습니다.");
            }

            // 2. 부모 댓글이 지금 작성하려는 '같은 메모'에 속해 있는지 무결성 검증
            if (!parentComment.getMemo().getMemoId().equals(memoId)) {
                throw new IllegalArgumentException("부모 댓글의 메모 주소가 일치하지 않습니다.");
            }

            // 3. 대댓글에 또 대댓글을 다는 것 방지 (1 Depth 제한)
            if (parentComment.getParentId() != null) {
                throw new IllegalArgumentException("대댓글의 대댓글은 작성할 수 없습니다.");
            }
        }


        // 1. 댓글 엔티티 생성 및 값 세팅
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setMemo(memo);
        comment.setContent(requestDto.getContent());
        comment.setParentId(requestDto.getParentId()); // 대댓글 지원
        comment.setCreatedAt(LocalDateTime.now());
        // deletedAt은 처음 생성 시 null 상태

        commentRepository.save(comment);

        // 2. 메모의 댓글 수(comment_count) +1 증가
        int currentCount = (memo.getCommentCount() != null) ? memo.getCommentCount() : 0;
        memo.setCommentCount(currentCount + 1);

        return comment.getCommentId();
    }

    //댓글 가져오기
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByMemo(Long memoId) {
        // 삭제되지 않은 댓글만 가져오게 설정
        return commentRepository.findByMemo_MemoIdAndDeletedAtIsNullOrderByCreatedAtAsc(memoId).stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long userId, Long commentId) {

        // 1. 대상 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        // 2. 작성자 본인인지 검증
        if (comment.getUser() == null || !comment.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("작성자 본인만 댓글을 삭제할 수 있습니다.");
        }

        // 3. 물리적 삭제(commentRepository.delete) 대신 논리적 삭제(UPDATE) 실행
        // JPA의 Dirty Checking 기능으로 인해 이 한 줄만으로 UPDATE 쿼리가 데이터베이스로 날아갑니다.
        comment.setDeletedAt(LocalDateTime.now());

        // 4. 데이터 무결성 방어: 댓글이 삭제되었으니 메모의 총 댓글 수(comment_count) 1 감소
        Memo memo = comment.getMemo();
        int currentCount = (memo.getCommentCount() != null) ? memo.getCommentCount() : 0;
        memo.setCommentCount(Math.max(0, currentCount - 1)); // 음수 방어
    }


}
