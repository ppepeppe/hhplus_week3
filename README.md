# 이커머스 서비스

## 문서 목차

---
1. [Milestone](./docs/milestone.md)
2. [Sequence Diagram](./docs/sequence_diagram.md)
3. [flow chart](./docs/flow_chart.md)
4. [erd](./docs/erd.md)
5. [API specification](./docs/api_specification.md)
6. [project structure](./docs/project_structure.md)
7. [swagger](./docs/swagger.md)

**test 조건**
docker desktop 실행 , local mysql 중지(포트겹침)


### 2주차 회고
- 2주차는 직접 ecommerce 서비스를 개발해보는것 입니다
- 최대한 욕심 부리지 않고 할거만 하고자 했습니다.
- 문서작업, 설계 , 계획수립, 개발의 과정을 경험해볼 수 있었습니다.
- 개발하며 설계의 중요성 또 api문서화의 중요성도 깨달을 수 있었습니다.
- 로직을 꾸준히 수정해야 했기 때문에 유지 보수의 중요성도 알 수 있었습니다.
- 현재 락은 db락을 통한 비관적락으로 동시성을 제어중입니다.

이 부분을 더 develop 시키고자 합니다 또한 도메인 책임 분리도 더 명확히 수정할 계획입니다.

현재 개발 상황은 다음과 같습니다.
- 유저
  - 잔액조회, 충전
- 상품
  - 목록 조회, 개별조회
- 주문
  - 주문
- 쿠폰
  - 발급, 조회
- 통계
  - 판매량 top 5
- filter , exception
이렇게 기본적인 api 를 구현한 상황입니다
추가적으로 부족한 api들 추가시켜나갈 예정입니다.

1주차 했던 TDD 기반으로 test 코드와 로직 구현을 동시에 진행하였고
2주차 했던 락을 이용해서 주문, 쿠폰발급의 동시성을 제어했습니다.


낙관적 락은 version으로 업데이트를 관리합니다 .
version 이 같을 시 업데이트 아닐 시 update 를 하지 않습니다
따라서 처음에 select 시 갖고 있는 version 정보가 주요합니다

동시에 select 를 업데이트 하기 전에 20개의 쓰레드가 했다면
20 개 모두 version 은 0으로 하나의 업데이트가 되면 19개는 업데이트가 실패하는 구조입니다.

낙관적 락은 select 시점의 version 정보에 따라 정확한 락 구현이 힘들다 판단되어 
저는 비관적 락으로 구성하도록 했습니다

그리고 전체적인 아키텍처는 clean 아키텍처로 구성하고자 했으며
최대한 도메인간의 연관을 줄이고자 했습니다

하나의 도메인 서비스에서 다른 도메인의 레포지토리나 서비스를 불르지 않게끔 구현 하였고
다른 도메인과의 협력은 usecase에서 풀었습니다.

현재 리팩토링 후
도메인관한 로직은 비즈니스 로직에서 푸는것이 아닌 도메인객체에서 푸는게 도메인 책임 분리에 더 맞다고 판단되어
도메인 객체에서 풀도록 수정 예정입니다.

