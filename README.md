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



docker compose 를 이용해 테스트 진행했습니다
docker compose up -d 후

CouponUseCaseTest, SalesStatsIntegrationTest 순서로 테스트 진행 후

docker compose down -v 후

docker compose up -d

OrderFacadeIntegrationTest, OrderFacadeConcurrencyIntegrationTest 순으로 테스트 해주시면 감사하겠습니다.