# [신민준] 뉴스 수집 및 관리 시스템

## 1. 프로젝트 목적
이 프로젝트는 RSS 방식을 통해 뉴스 데이터를 수집하고 관리하는 시스템을 구현합니다.
카프카를 활용한 메시지 스트리밍과 Materialized View를 통한 성능 최적화를 제공하며, 실시간 뉴스 데이터 처리 및 조회 기능을 지원합니다.

주요 기능:
1. RSS 기반 뉴스 데이터 수집 및 카프카 프로듀싱 (5분마다 자동 수집)
2. 카프카 메시지 컨슈밍을 통한 데이터베이스 적재 및 중복 저장 방지
3. Materialized View를 활용한 고성능 조회 및 캐싱 처리
4. 스케줄러를 통한 Materialized View 자동 갱신
5. JWT 기반 인증 시스템 (Access Token, Refresh Token)
6. RESTful API를 통한 뉴스 데이터 관리
7. 웹 인터페이스를 통한 사용자 친화적 뉴스 조회

## 2. 기술 스펙

### Backend
- 언어: Kotlin 1.9.25
- 프레임워크: Spring Boot 3.3.5
- 빌드 도구: Gradle 8.10.2
- 데이터베이스: PostgreSQL
- 캐시: Redis
- ORM: Spring Data JPA, QueryDSL
- 메시지 큐: Apache Kafka
- 스케줄러: Spring Scheduler
- DB 마이그레이션: Flyway
- API 문서화: Swagger
- 테스트: JUnit 5, Mockito
- 컨테이너: Docker, Docker Compose

### Frontend
- 언어: TypeScript
- 프레임워크: Next.js
- 빌드 도구: npm
- 런타임: Node.js 21
- 스타일링: shadcn


## 3. 프로젝트 구조

```
homework/
├── core/                    # 공통 모듈
│   ├── src/main/kotlin/
│   │   └── com.ddi.core/
│   │       ├── common/           # 공통 설정 및 유틸리티
│   │       │   ├── config/       # Jackson, Password 등 설정
│   │       │   ├── domain/       # 공통 도메인 객체 및 Base Entity
│   │       │   ├── dto/          # 공통 응답 DTO (BaseResponse, Meta, AuthDetails)
│   │       │   └── exception/    # 예외 처리
│   │       ├── member/           # 회원 도메인
│   │       │   └── property/     # Username, Password 등 속성 객체
│   │       ├── newsitem/         # 뉴스 아이템 도메인
│   │       │   └── property/     # NewsTitle, NewsUrl 등 속성 객체
│   │       ├── newssource/       # 뉴스 소스 도메인  
│   │       │   └── property/     # NewsSourceCode, Type, Name, Url 등
│   │       ├── keyword/          # 키워드 도메인
│   │       └── dlqmessage/       # DLQ 메시지 도메인
│   │           └── property/     # DlqStatus 등 속성 객체
│   ├── src/main/avro/
│   │   └── NewsEvent.avsc       # Kafka 메시지용 Avro 스키마
│   └── build.gradle.kts
│
├── flyway/                  # 데이터베이스 마이그레이션
│   ├── src/main/resources/
│   │   └── db/migration/    # 마이그레이션 스크립트
│   └── build.gradle.kts
│
├── scheduler/               # Materialized View 갱신 스케줄러
│   ├── src/main/kotlin/
│   │   └── com.ddi.scheduler/
│   │       └── newsitem/    # 뉴스 아이템 MV 스케줄러
│   └── build.gradle.kts
│
├── scrape-rss/              # RSS 뉴스 수집 및 카프카 프로듀싱
│   ├── src/main/kotlin/
│   │   └── com.ddi.scraperss/
│   │       ├── config/      # HTTP Client, Redis, Async, Job 설정
│   │       ├── repository/  # Keyword, NewsSource 리포지토리
│   │       ├── mapper/      # RSS Feed 매퍼 (Google, SBS, MK, YNA 등)
│   │       ├── model/       # RSS 도메인 모델
│   │       ├── job/         # RSS 수집 작업 (Static/Keyword 처리)
│   │       ├── service/     # RSS 파싱 및 중복 제거 서비스
│   │       └── runner/      # CommandLine Runner
│   └── build.gradle.kts
││
├── scrape-web/              # Web 뉴스 수집 및 카프카 프로듀싱
│   ├── src/main/kotlin/
│   │   └── com.ddi.scraperweb/
│   │       ├── config/      # HTTP Client, Redis, Async, Job 설정
│   │       ├── repository/  # Keyword, NewsSource 리포지토리
│   │       ├── mapper/      # Web Feed 매퍼 (Naver 등)
│   │       ├── model/       # Web 도메인 모델
│   │       ├── job/         # Web 수집 작업
│   │       ├── service/     # Web 중복 제거 서비스
│   │       └── runner/      # CommandLine Runner
│   └── build.gradle.kts
│
├── stream-news/             # 뉴스 메시지 컨슈밍 및 DB 적재
│   ├── src/main/kotlin/
│   │   └── com.ddi.streamnews/
│   │       ├── config/      # Kafka 설정
│   │       ├── component/   # Kafka 컨슈머 (NewsEvent, DLQ)
│   │       └── repository/  # NewsItem, DlqMessage 리포지토리
│   └── build.gradle.kts
│
├── api/                     # API 서버
│   ├── src/main/kotlin/
│   │   └── com.ddi.api/
│   │       ├── common/      # 공통 설정 및 핸들러
│   │       │   ├── config/
│   │       │   ├── handler/
│   │       │   ├── component/
│   │       │   ├── application/
│   │       │   └── authentication/
│   │       ├── member/      # 회원 관리
│   │       │   ├── ui/      # 컨트롤러
│   │       │   ├── application/  # 서비스 계층
│   │       │   ├── repository/   # 데이터 접근
│   │       │   ├── dto/     # DTO
│   │       │   └── impl/    # 구현체
│   │       ├── newsitem/    # 뉴스 아이템 관리
│   │       │   ├── ui/
│   │       │   ├── application/
│   │       │   ├── repository/
│   │       │   └── dto/
│   │       ├── newssource/  # 뉴스 소스 관리
│   │       │   ├── ui/
│   │       │   ├── application/
│   │       │   └── repository/
│   │       ├── keyword/     # 키워드 관리
│   │       │   ├── ui/
│   │       │   ├── application/
│   │       │   ├── repository/
│   │       │   ├── dto/
│   │       │   └── impl/
│   │       └── dlqmessage/  # DLQ 메시지 관리
│   │           ├── ui/
│   │           ├── application/
│   │           └── repository/
│   └── build.gradle.kts
│
├── web/                     # Frontend 애플리케이션 (Next.js)
│   ├── src/
│   │   ├── app/             # App Router 페이지
│   │   │   ├── dashboard/   # 대시보드 페이지들
│   │   │   │   ├── keywords/    # 키워드 관리
│   │   │   │   ├── sources/     # 뉴스 소스 관리
│   │   │   │   ├── results/     # 수집 결과
│   │   │   │   └── dlq-messages/ # DLQ 메시지 관리
│   │   │   ├── login/       # 로그인 페이지
│   │   │   └── register/    # 회원가입 페이지
│   │   ├── components/      # 컴포넌트
│   │   │   ├── ui/          # shadcn/ui 기본 컴포넌트
│   │   │   ├── auth/        # 인증 관련 컴포넌트
│   │   │   └── dashboard/   # 대시보드 컴포넌트
│   │   └── lib/             # 라이브러리 및 유틸리티
│   │       ├── api/         # API 호출 함수들
│   │       ├── store/       # Zustand 상태 관리
│   │       └── type/        # TypeScript 타입 정의
│   ├── package.json
│   └── next.config.ts
│
├── request/                 # HTTP 요청 테스트 파일들
│
├── docker-compose.yml       # Docker Compose 설정
├── build.gradle.kts         # 루트 빌드 설정
└── settings.gradle.kts      # 프로젝트 설정
```

### 모듈별 상세 설명

#### 1. core (공통 모듈)
- 전체 시스템에서 공통으로 사용되는 설정, 도메인 객체, 예외 처리, 유틸리티 클래스를 포함
- 코드 재사용성을 높이고 일관성을 보장하여 마이크로서비스 간 중복 코드 방지

#### 2. flyway (데이터베이스 마이그레이션)
- Flyway를 활용한 데이터베이스 스키마 버전 관리
- 충돌 없이 DB 변경사항을 공유하고 배포 환경별 일관성 보장

#### 3. scheduler (Materialized View 갱신 스케줄러)
- Spring Scheduler를 활용한 주기적 작업 수행
- Materialized View의 데이터 갱신을 자동화하여 조회 성능 최적화

#### 4. scrape-rss, scrape-web (뉴스 수집)
- **Strategy 패턴 적용**: 다양한 RSS 포맷을 지원하기 위해 mapper를 인터페이스와 구현체로 분리
- **병렬 처리**: KeywordRssProcessor/KeywordProcessor에서 CompletableFuture를 활용한 비동기 병렬 처리로 성능 최적화
- **멱등성 보장**: NewsEventWriter에서 Redis를 활용한 메시지 중복 발송 방지 로직 구현
  - Redis Pipeline을 활용한 배치 중복 체크로 네트워크 호출 최소화
  - 100개 단위 청크 처리로 메모리 효율성과 성능 최적화 동시 달성
  - Fallback 메커니즘으로 Pipeline 실패 시에도 안정적인 중복 체크 보장
- **고빈도 수집**: 제시된 문제와 달리 빠른 결과 확인을 위해 5분마다 뉴스 수집 실행
- **카프카 최적화 설정**:
  - 멱등성 설정으로 중복 메시지 발송 방지
  - Avro 스키마 활용으로 스키마 진화 지원 및 직렬화 성능 최적화
  - 고가용성을 위한 복제 설정
  - 라운드로빈 파티션 할당 전략으로 균등한 부하 분산
- 뉴스 수집 방식 확장: RSS(최신, 키워드) 조회 수집 방식 외에 크롤링 방식이 추가된다면 모듈 추가 가능

#### 5. stream-news (뉴스 스트림 처리)
- 카프카 토픽에서 뉴스 메시지를 컨슈밍하여 실시간 데이터베이스 적재
- **중복 저장 방지**: 컨슈밍된 데이터의 중복 저장을 방지하는 로직 구현
- **DLQ(Dead Letter Queue) 처리**: 메시지 처리 실패 시 DLQ로 라우팅하여 데이터 유실 방지
- **복원력 제공**: 메시지 처리 실패에 대한 재시도 및 복구 메커니즘

#### 6. api (API 서버)
- **RESTful API**: 뉴스 데이터 조회 및 관리를 위한 REST API 제공
- **JWT 인증 시스템**:
  - 회원가입/로그인 시 JWT Access Token과 Refresh Token 발급
  - Access Token은 클라이언트 쿠키에 저장하여 상태 관리
  - Refresh Token은 Redis에 안전하게 보관
  - AuthenticationFilter에서 토큰 유효성 검사 및 자동 재발급 처리
  - Redis의 Refresh Token과 요청 토큰 비교를 통한 보안성 강화
- **성능 최적화**:
  - 빈번하게 호출되는 뉴스 목록(NewsItem)은 Materialized View로 조회
    * Materialized View 장점: 복잡한 조인 쿼리를 미리 계산하여 저장함으로써 조회 성능 대폭 향상
  - 뉴스 상세 정보는 @Cacheable 어노테이션을 활용한 캐싱 처리로 응답 속도 개선
- **API 문서화**: Swagger를 활용한 자동 API 문서 생성

#### 7. web (Frontend)
- **쿠키 기반 인증**: middleware에서 쿠키에 저장된 Access Token과 Refresh Token을 활용한 로그인 상태 유지

## 4. 아키텍처 특징

### 마이크로서비스 아키텍처
- 각 모듈이 독립적으로 배포 및 확장 가능
- 카프카를 통한 비동기 메시지 처리로 시스템 간 결합도 최소화

### 성능 최적화
- **Materialized View**: 복잡한 조인 쿼리를 미리 계산하여 조회 성능을 10배 이상 향상
- **다층 캐싱 전략**: Redis 캐시와 Spring @Cacheable을 활용한 다단계 캐싱
- **스케줄러**: 자동 데이터 갱신으로 최신 정보 유지
- **카프카 스트림**: 실시간 데이터 처리로 지연시간 최소화
- **Redis Pipeline 최적화**: 중복 체크 시 단건 조회 대신 Pipeline을 활용한 배치 처리로 Redis 네트워크 호출 횟수를 대폭 감소시켜 성능 향상

### 고가용성 및 안정성
- **멱등성 보장**: Redis를 활용한 중복 메시지 발송 방지
- **DLQ 처리**: 메시지 처리 실패 시 Dead Letter Queue로 안전한 에러 처리
- **JWT 보안**: Access Token과 Refresh Token을 활용한 안전한 인증 시스템
- **데이터 무결성**: 중복 저장 방지 로직으로 데이터 일관성 보장

### 확장성 및 유연성
- **Strategy 패턴**: 다양한 RSS 포맷을 쉽게 추가할 수 있는 확장 가능한 구조
- **병렬 처리**: CompletableFuture를 활용한 비동기 병렬 처리로 처리량 증대
- **수평적 확장**: 무상태 설계로 서버 인스턴스 추가를 통한 확장 용이
- **컨테이너 기반**: Docker를 활용한 클라우드 네이티브 배포 지원


## 5. 실행 방법

### 1. Docker Compose를 사용한 실행

전체 시스템을 한 번에 실행하는 권장 방법입니다.

```bash
docker-compose up -d
```

**실행 순서:**
1. PostgreSQL 데이터베이스 시작
2. Redis 캐시 서버 시작
3. Kafka 및 Zookeeper 시작
4. Flyway를 통한 데이터베이스 마이그레이션 실행
5. scrape-rss 서비스 시작 (뉴스 수집 및 프로듀싱)
6. scrape-web 서비스 시작 (뉴스 수집 및 프로듀싱)
7. stream-news 서비스 시작 (메시지 컨슈밍 및 DB 적재)
8. scheduler 서비스 시작 (Materialized View 갱신)
9. api 서비스 시작 (API 서버)
10. web 서비스 시작 (Frontend)

**접속 정보:**
- Web 애플리케이션: http://localhost:3000
- API 서버: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Kafka UI: http://localhost:9091

### 2. 개별 애플리케이션 실행

Docker 없이 개별적으로 실행하는 방법입니다.

#### Backend 서비스 실행
```bash
# 1. 데이터베이스 마이그레이션
cd flyway
./gradlew flywayMigrate

# 2. RSS 수집 서비스
cd ../scrape-rss
./gradlew bootRun

# 3. Web 수집 서비스
cd ../scrape-rss
./gradlew bootRun

# 4. 스트림 처리 서비스
cd ../stream-news
./gradlew bootRun

# 5. 스케줄러 서비스
cd ../scheduler
./gradlew bootRun

# 6. API 서버
cd ../api
./gradlew bootRun
```

#### Frontend 실행
```bash
cd web
npm install
npm run dev
```

### 3. 테스트 실행

#### 전체 테스트
```bash
# 루트 디렉토리에서 전체 모듈 테스트
./gradlew test
```

#### 모듈별 테스트
```bash
# 특정 모듈 테스트
./gradlew :core:test
./gradlew :api:test
```

#### 통합 테스트
```bash
# Testcontainers를 활용한 통합 테스트
./gradlew integrationTest
```

### 4. API 문서 확인

API 서버 실행 후 다음 URL에서 Swagger UI를 통해 API 문서를 확인할 수 있습니다:

- **Swagger UI**: http://localhost:8080/swagger-ui.html

### 주요 API 엔드포인트

#### 계정/인증 관리
[http파일 보기](./request/member.http)
- `POST /member`: 회원가입
- `POST /member/login`: 로그인 (JWT 토큰 발급)
- `POST /member/logout`: 로그아웃 (토큰 무효화)
- `POST /member/refresh`: Access Token 갱신

#### 뉴스 관리
[http파일 보기](./request/news-item.http)
- `GET /news-items`: 뉴스 목록 조회 (Materialized View 활용)
- `GET /news-items/{id}`: 특정 뉴스 상세 조회 (캐싱 적용)

#### 뉴스 제공처 관리
[http파일 보기](./request/news-source.http)
- `GET /news-source`: 뉴스 제공처 목록 조회
- `POST /news-source`: 뉴스 제공처 추가 (관리자 권한 필요)
- `POST /news-source/{id}/activate`: 뉴스 제공처 활성화
- `POST /news-source/{id}/deactivate`: 뉴스 제공처 비활성화

#### 키워드 관리
[http파일 보기](./request/keyword.http)
- `GET /keyword`: 키워드 목록 조회
- `POST /keyword`: 키워드 추가 (관리자 권한 필요)
- `PUT /keyword/{id}`: 키워드 수정 (관리자 권한 필요)
- `DELETE /keyword/{id}`: 키워드 삭제 (관리자 권한 필요)
- `POST /keyword/{id}/activate`: 키워드 활성화
- `POST /keyword/{id}/deactivate`: 키워드 비활성화


## 6. 인프라 & CICD

### **1. 메인 리소스 스펙**

| 서비스              | Replicas | CPU Req | CPU Limit | Memory Req | Memory Limit | Storage | 용도           |
|------------------|----------|---------|-----------|------------|--------------|---------|--------------|
| **PostgreSQL**   | 1        | 500m | 1000m | 1Gi | 2Gi | 20Gi    | 메인 데이터베이스    |
| **Redis Master** | 1        | 250m | 500m | 512Mi | 1Gi | 2Gi     | 캐시 & 세션 (쓰기) |
| **Redis Slave**  | 1        | 250m | 500m | 512Mi | 1Gi | 2Gi     | 캐시 & 세션 (읽기) |
| **Kafka**        | 3        | 500m | 1000m | 1Gi | 2Gi | 5Gi     | 메시지 스트리밍     |
| **Zookeeper**    | 1        | 250m | 500m | 512Mi | 1Gi | 2Gi     | Kafka        |
| **API Server**   | 3        | 250m | 500m | 512Mi | 1Gi | -       | REST API 서버  |
| **RSS Scraper**  | 1        | 250m | 500m | 512Mi | 1Gi | -       | RSS 뉴스 수집    |
| **WEB Scraper**  | 1        | 250m | 500m | 512Mi | 1Gi | -       | WEB 뉴스 수집    |
| **Stream News**  | 2        | 250m | 500m | 512Mi | 1Gi | -       | Kafka 컨슈머    |
| **Scheduler**    | 1        | 100m | 250m | 256Mi | 512Mi | -       | MV 스케줄러      |
| **Frontend**     | 2        | 100m | 250m | 256Mi | 512Mi | -       | next 웹       |
| **Flyway**       | 1        | 100m | 250m | 256Mi | 512Mi | -       | DB 마이그레이션    |

### **2. Auto Scaling 설정**

| 서비스 | Min Replicas | Max Replicas | CPU Target | Memory Target | Scale Up | Scale Down |
|--------|--------------|--------------|------------|---------------|----------|------------|
| **API Server** | 3            | 10 | 70% | 80% | 2분 | 5분 |
| **Stream News** | 2            | 8 | 70% | 80% | 2분 | 5분 |
| **Frontend** | 2            | 5 | 60% | 70% | 3분 | 5분 |

### **3. CI/CD**

- GitHub Push → Actions (CI) → ECR → ArgoCD (CD) → EKS
- 배포전략: Blue/Green (무중단)배포