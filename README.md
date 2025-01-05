# Link Preview Service

이 프로젝트는 URL에서 메타데이터를 추출하여 링크 프리뷰를 생성하는 Spring Boot 기반의 프로젝트입니다. 웹페이지의 제목, 설명, 이미지 URL 등의 메타데이터를 추출하여 채팅이나 소셜 미디어에서 흔히 볼 수 있는 링크 프리뷰 기능을 제공합니다.


## 주요 기능

- URL에서 메타데이터(제목, 설명, 이미지 등) 추출
- Redis를 활용한 메타데이터 캐싱
- 실패 시 자동 재시도 메커니즘
- 비동기 처리 지원

## 기술 스택

- Java 21
- Spring Boot 3.4.1
- Redis (캐싱)
- Jsoup (HTML 파싱)
- Lombok
- Jackson (JSON 처리)

## 시작하기

### 필수 요구사항

- Java 21 이상
- Redis 서버
- Gradle

### 프로젝트 설정

1. Redis 서버 실행:
```bash
# Redis가 설치되어 있지 않다면 먼저 설치해주세요
redis-server
```

2. 애플리케이션 설정(application.yml):
```yaml
spring:
  application:
    name: link-preview-service
  # 레디스 설정
  data:
    redis:
      host: localhost
      port: 6379
      password: redis
      # Lettuce 설정
      lettuce:
        pool:
          max-active: 32  # 동시 연결 수 증가
          max-idle: 32
          min-idle: 8

metadata:
  extractor:
    timeout: 5000  # 요청 타임아웃 (ms)
    user-agent: "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"  # 크롬 User-Agent
    max-retries: 3  # 최대 재시도 횟수
    retry-delay: 1000  # 재시도 간격 (ms)
  cache:
    duration: 24h  # 캐시 유효 기간
```

3. 프로젝트 빌드:
```bash
./gradlew build
```

## API 사용법

### 링크 프리뷰 조회

```http
GET /api/v1/metadata/preview?url={url}
```

#### 요청 예시:
```bash
curl "http://localhost:8080/api/v1/metadata/preview?url=https://example.com"
```

#### 응답 예시:
```json
{
    "url": "https://example.com",
    "title": "Example Domain",
    "description": "This domain is for use in illustrative examples in documents.",
    "image": "https://example.com/image.jpg",
    "siteName": "Example",
    "favicon": "https://example.com/favicon.ico",
    "extractedAt": "2025-01-05T12:34:56"
}
```

## 프로젝트 구조 설명

```
src/main/java/io/github/stark/linkpreview/
├── common
│   ├── config
│   │   ├── AsyncConfig.java        # 비동기 처리 설정
│   │   └── RedisConfig.java        # Redis 캐시 설정
│   └── exception
│       └── MetadataExtractionException.java  # 예외 처리
├── extractor
│   ├── RetryableMetadataExtractor.java      # 재시도 로직
│   ├── core
│   │   └── MetadataExtractor.java           # 추출기 인터페이스
│   └── jsoup
│       └── JsoupMetadataExtractor.java      # Jsoup 구현체
└── metadata
    ├── controller
    │   └── MetadataController.java          # API 엔드포인트
    ├── domain
    │   └── Metadata.java                    # 도메인 모델
    └── service
        ├── AsyncMetadataService.java        # 비동기 처리
        ├── MetadataService.java             # 메타데이터 서비스
        └── MetadataCache.java               # 캐싱 로직
```

## 주요 컴포넌트 설명

### JsoupMetadataExtractor

- 이 컴포넌트는 Jsoup 라이브러리를 사용하여 웹페이지에서 메타데이터를 추출합니다.
### RetryableMetadataExtractor

- 네트워크 오류나 일시적인 문제로 인한 실패를 처리하기 위한 재시도 메커니즘을 제공합니다. 설정된 횟수만큼 자동으로 재시도합니다.

### MetadataCache

- Redis를 사용하여 추출된 메타데이터를 캐싱합니다. 동일한 URL에 대한 반복적인 요청을 효율적으로 처리할 수 있습니다.

## 확장 및 커스터마이징

1. 새로운 메타데이터 추출기 추가:
    - `MetadataExtractor` 인터페이스를 구현하여 새로운 추출 방식을 추가할 수 있습니다.

2. 캐싱 전략 변경:
    - `MetadataCache` 클래스를 수정하여 다른 캐싱 방식을 적용할 수 있습니다.

3. 재시도 정책 조정:
    - application.yml에서 retry 관련 설정을 조정할 수 있습니다.

## 주의사항

- 일부 웹사이트는 빠른 연속 요청을 차단할 수 있으므로, 적절한 간격을 두고 요청하는 것이 좋습니다.
- 메모리 사용량 관리를 위해 Redis의 캐시 정책을 적절히 설정하세요.
- 프로덕션 환경에서는 적절한 모니터링과 로깅 전략이 필요합니다.

## 라이센스

이 프로젝트는 제가 링크 프리뷰 기능 구현에 대해 학습하고 이해하기 위해 만든 개인 학습용 프로젝트입니다. 다른 개발자분들도 링크 프리뷰 기능 구현 방법을 이해하고 학습하는 데 참고자료로 자유롭게 사용하실 수 있습니다.

이 프로젝트에서 사용된 모든 라이브러리는 검증된 오픈소스 라이브러리들입니다:

- Spring Boot (Apache License 2.0)
- Jackson (Apache License 2.0)
- Jsoup (MIT License)
- Lombok (MIT License)
- JUnit (Eclipse Public License v2.0)

각 라이브러리는 자체 라이선스 정책을 가지고 있으므로, 이 프로젝트를 참고하여 실제 서비스를 구현하실 때는 사용하는 라이브러리들의 라이선스 정책을 확인하고 준수하시기 바랍니다.

이 프로젝트가 링크 프리뷰 기능을 구현하고자 하는 다른 개발자분들의 학습에 도움이 되길 바랍니다.