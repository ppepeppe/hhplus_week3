# Project Structure
---
```plaintext
kr.hhplus.be.server
│
├── api
│   ├── coupon
│   │   ├── controller  (Controller Layer)
│   │   ├── dto         (Data Transfer Objects)
│   │   ├── usecase     (Application Use Cases)
│   ├── order
│   │   ├── controller
│   │   ├── dto
│   │   ├── usecase
│   ├── product
│   │   ├── controller
│   │   ├── dto
│   │   ├── usecase
│   ├── stats
│   │   ├── controller
│   │   ├── dto
│   │   ├── usecase
│   ├── user
│       ├── controller
│       ├── dto
│       ├── usecase
│
├── common             (Shared Utilities or Constants)
│
├── config.jpa         (Configuration for JPA)
│
├── domain
│   ├── coupon
│   │   ├── infrastructure (External systems integration)
│   │   ├── models         (Domain Models / Entities)
│   │   ├── repository     (Data Access Layer)
│   │   ├── service        (Domain Services or Core Logic)
│   ├── order
│   │   ├── infrastructure
│   │   ├── models
│   │   ├── repository
│   │   ├── service
│   ├── product
│   │   ├── infrastructure
│   │   ├── models
│   │   ├── repository
│   │   ├── service
│   ├── stats
│   │   ├── infrastructure
│   │   ├── models
│   │   ├── repository
│   │   ├── service
│   ├── user
│       ├── infrastructure
│       ├── models
│       ├── repository
│       ├── service
│
└── ServerApplication (Main Application Entry Point)

**1. API Layer (api)**

- 역할: 클라이언트와의 인터페이스를 처리합니다.
- 구성
    
    - controller: 클라이언트 요청을 처리하며, HTTP 요청을 받아 Use Case를 호출합니다.
    - dto: 클라이언트와 데이터를 주고받기 위한 객체로, 요청 및 응답 데이터를 구조화합니다.
    - usecase: 특정 비즈니스 로직을 처리하는 애플리케이션 계층. 도메인 서비스를 호출하고 작업을 조율합니다.

**2. Domain Layer (domain)**

- 역할: 비즈니스 규칙 및 핵심 로직을 처리합니다.
- 구성

    - models: 핵심 비즈니스 객체 또는 엔터티로, 데이터와 비즈니스 규칙을 포함합니다.
    - repository: 데이터베이스와의 상호작용하는 repositroy 를 추상화 합니다.(DIP)
    - service: 도메인 비즈니스 로직을 처리합니다.
    - infrastructure: 외부 시스템과의 상호작용하는 JPA Repository 등과 그 구현체를 처리합니다.
**3. Common Layer (common)**

역할: 여러모듈에서 재상용 가능한 메소드를 제공합니다(추후 Exception 메소드 분리할 예정)