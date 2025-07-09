# Holiday Keeper

최근 5 년(2020 ~ 2025) 의 전 세계 공휴일 데이터를 저장·조회·관리하는 Mini Service

# Api 명세서

## 1. 휴일 목록 조회

- 엔드포인트: `/api/v1/holidays`
- 메소드: `GET`
- 설명: 연도와 국가 코드를 기준으로 휴일 목록을 조회
- 페이징 파라미터:
    - `size` (기본값: 10, 최대: 100): 페이지 크기
    - `page` (기본값: 0): 페이지 번호
    - `sort` (기본값: DATE): 정렬 기준 (DATE, NAME, COUNTRY)
    - `direction` (기본값: ASC): 정렬 방향 (ASC, DESC)
- 검색 파라미터
    - `year`: 검색 연도
    - `code`: 국가 코드
    - `from`: 시작 날짜
    - `to`: 끝 날짜
    - `type`: 공휴일 타입
- 응답 예시:
  ```json
  {
    "status": 200,
    "message": "Success",
    "data": {
        "content": [
            {
                "id": 8188,
                "date": "2025-01-01",
                "localName": "새해",
                "name": "New Year's Day",
                "code": "KR",
                "fixed": false,
                "global": true,
                "launchYear": null,
                "types": [
                    "Public"
                ],
                "counties": []
            },
            {
                "id": 8189,
                "date": "2025-01-28",
                "localName": "설날",
                "name": "Lunar New Year",
                "code": "KR",
                "fixed": false,
                "global": true,
                "launchYear": null,
                "types": [
                    "Public"
                ],
                "counties": []
            }
        ],
        "page": 0,
        "size": 2,
        "totalElements": 15,
        "totalPages": 8
    }
  }

  ```

## 2. 휴일 목록 갱신

- 엔드포인트: `/api/v1/holidays/refresh`
- 메소드: `POST`
- 설명: 연도와 국가 코드를 기준으로 휴일 목록을 갱신
- 요청 본문 예시

```json
{
  "year": 2023,
  "code": "KR"
}
```

- 응답 예시

```json
{
  "status": 200,
  "message": "Success",
  "data": {
    "content": [
      {
        "id": 8188,
        "date": "2025-01-01",
        "localName": "새해",
        "name": "New Year's Day",
        "code": "KR",
        "fixed": false,
        "global": true,
        "launchYear": null,
        "types": [
          "Public"
        ],
        "counties": []
      },
      {
        "id": 8189,
        "date": "2025-01-28",
        "localName": "설날",
        "name": "Lunar New Year",
        "code": "KR",
        "fixed": false,
        "global": true,
        "launchYear": null,
        "types": [
          "Public"
        ],
        "counties": []
      }
    ],
    "page": 0,
    "size": 2,
    "totalElements": 15,
    "totalPages": 8
  }
}
```

## 3. 휴일 목록 삭제

- 엔드포인트: `/api/v1/holidays`
- 메소드: `DELETE`
- 설명: 연도와 국가 코드를 기준으로 휴일 목록을 삭제
- 요청 본문 예시

```json
{
  "year": 2025,
  "code": "KR"
}
```

응답 예시

```json
{
  "code": "SUCCESS",
  "message": "성공",
  "data": null
}
```

# 테스트 성공 스크린 샷
<img width="1423" alt="image" src="https://github.com/user-attachments/assets/cbb4fa6b-eb8a-4203-9ced-29b61b91c386" />


# Swagger 링크

http://localhost:8080/api-docs
