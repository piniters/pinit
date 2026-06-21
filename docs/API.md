# Pinit API 명세서

## 1. 공통 사항
- **Base URL:** 
- **인증 방식:** JWT

## 2. User API
| 기능 | Method | URL | 설명 |
| :--- | :--- | :--- | :--- |
| 회원가입 | POST | /api/users/signup | 사용자 계정 생성 |
| 로그인 | POST | /api/users/login | JWT 토큰 발급 |

## 3. Memo API
| 기능 | Method | URL | 설명 |
| :--- | :--- | :--- | :--- |
| 메모 저장 | POST | /api/memos | 위치 정보와 내용 저장 |
| 메모 목록 조회 | GET | /api/memos | 전체 메모 리스트 호출 |
| 상세 조회 | GET | /api/memos/{id} | 특정 메모 상세 확인 |
| 메모 삭제 | DELETE | /api/memos/{id} | 특정 메모 삭제 |