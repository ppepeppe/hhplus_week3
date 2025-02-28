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
8. [DB인덱스](#8-db인덱스)
9. [MSA로 전환기](#9-msa로-전환기)


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

### 동시성 제어
**선착순 쿠폰 발급**
선착순 쿠폰 발급 API는 동시성 처리를 반드시 고려해야 하는 기능입니다.

이 API는 여러 사용자의 요청을 선착순으로 처리하며, 
정해진 쿠폰 수량만 정확하게 발급해야 합니다. 다수의 사용자가 동시에 요청을 보낼 경우, 요청이 겹치면서 데이터 충돌이나 초과 발급 문제가 발생할 수 있으므로,
이를 방지하기 위한 철저한 동시성 제어가 필요합니다

**낙관적락**

쿠폰 발급의 경우 정확한 개수와 선착순 처리가 필수적입니다.

낙관적 락의 경우 정확히 30개의 개수를 보장하기는 어렵습니다.

낙관적 락은 version 필드를 사용해 업데이트를 관리합니다. version 값이 동일한 경우에만 업데이트가 가능하며, 그렇지 않으면 업데이트가 실패합니다. 
따라서 초기에 SELECT 시점에서 가져온 version 정보가 매우 중요합니다.

만약 동시에 20개의 쓰레드가 쿠폰 발급 요청을 보내면, 모두 동일한 version 값을 (0) 갖게 됩니다. 이
때, 하나의 쓰레드가 성공적으로 업데이트를 수행하면 version 값이 증가하며, 나머지 19개의 쓰레드는 version 값 불일치로 인해 업데이트가 실패하는 구조입니다.

결과적으로, 40개의 요청 중 30개의 성공을 보장할 수는 있지만, 정확히 30개의 성공을 보장하려면 재시도 로직을 구현해야 합니다.
![alt](./docs/img/낙관적락1.png)

![alt](./docs/img/낙관적락2.png)

요청이 오는 순서대로 완료가 되는것이 아니라
완료 -> 실패 -> 실패 -> 완료 이런 형식으로 version 값에 따라 성공 실패가 달라지는것을 확인 할 수 있습니다.
따라서 결과가 30을 기대했지만 15의 성공이 일어났습니다.

**비관적락**

비관적 락을 사용하는 경우, 정확히 30개의 쿠폰 발급을 효과적으로 보장할 수 있습니다.

비관적 락은 데이터베이스에서 행 단위로 락을 걸어 다른 트랜잭션이 동시에 해당 데이터를 수정하거나 읽는 것을 차단합니다. 
따라서 요청이 동시에 들어와도 락이 설정된 순서대로 요청이 처리되며, 선착순 처리가 가능합니다.

만약 동시에 20개의 쓰레드가 쿠폰 발급 요청을 보낸 경우, 비관적 락이 설정된 상태에서 하나의 쓰레드만 데이터를 수정할 수 있습니다. 
다른 쓰레드들은 락이 해제될 때까지 대기하며, 락이 해제된 후 순차적으로 요청을 처리합니다. 
이 과정에서 데이터베이스의 락 대기 큐가 순서를 관리하므로, 정확히 30개의 쿠폰이 발급되도록 제어할 수 있습니다.

결과적으로, 비관적 락을 사용하면 정확히 30개의 쿠폰 발급을 안정적으로 관리할 수 있습니다. 
추가적인 재시도 로직은 필요하지 않으며, 락 대기와 트랜잭션 관리를 통해 동시성 문제를 해결합니다.

![alt](./docs/img/비관적락1.png)

![alt](./docs/img/비관적락2.png)

요청을 차례대로 처리하며 정확히 30개의 완료 후 나머지 요청에는 실패를 하는것을 알 수 있습니다.

하지만 정확히 1~30까지의 선착순처리는 안된것을 확인할 수 있습니다.

선착순을 위해 요청을 queue 에 쌓고 하나씩 꺼내 처리하도록 테스트 코드를 수정하였습니다
![alt](./docs/img/선착순처리_쿠폰1.png)

![alt](./docs/img/선착순처리_쿠폰2.png)

queue에 쌓인 요청 순서 대로 userId를 처리하는 것을 볼 수 있습니다.

### 주문 요청 ###
**낙관적락**

주문요청의 경우 정확한 재고관리와 선착순 처리가 필수적입니다.
따라서 저는 11명의 주문 요청이 재고가 10개인 상품을 주문하는 것을 테스트 해봤습니다.

만약 동시에 11개의 쓰레드가 주문 요청을 보내면, 모두 동일한 version 값을 (0) 갖게 됩니다. 이
때, 하나의 쓰레드가 성공적으로 업데이트를 수행하면 version 값이 증가하며, 나머지 19개의 쓰레드는 version 값 불일치로 인해 업데이트가 실패하는 구조입니다.

결과적으로, 11개의 요청 중 10개의 이하의 성공 보장할 수는 있지만, 정확히 30개의 성공을 보장하려면 재시도 로직을 구현해야 합니다.

![alt](./docs/img/낙관적락_주문.png)

**비관적락**

비관적 락을 사용하는 경우, 정확히 10개의 주문처리를 보장할 수 있습니다.

만약 동시에 11개의 쓰레드가 주문 요청을 보낸 경우, 비관적 락이 설정된 상태에서 하나의 쓰레드만 데이터를 수정할 수 있습니다. 
다른 쓰레드들은 락이 해제될 때까지 대기하며, 락이 해제된 후 순차적으로 요청을 처리합니다. 
이 과정에서 데이터베이스의 락 대기 큐가 순서를 관리하므로, 정확히 10개의 주문이 처리되도록 제어할 수 있습니다.

결과적으로, 비관적 락을 사용하면 정확히 10개의 주문 요청을 안정적으로 관리할 수 있습니다. 
추가적인 재시도 로직은 필요하지 않으며, 락 대기와 트랜잭션 관리를 통해 동시성 문제를 해결합니다.
하지만 이 역시 선착순 처리는 하지 못한 것을 확인 할 수 있습니다,

![alt](./docs/img/비관적락_주문.png)

선착순을 위해 요청을 queue 에 쌓고 하나씩 꺼내 처리하도록 테스트 코드를 수정하였습니다

![alt](./docs/img/선착순처리_주문.png)

queue에 쌓인 요청 순서 대로 userId를 처리하는 것을 볼 수 있습니다.

### 추가 개발사항

- 현재 테스트 코드내에서 요청을 큐에 쌓고 진행했습니다.
- 이걸 실제 controller 단위에서 어떻게 큐에 쌓을건지 고민을 해볼것입니다.
- 비관적락은 db락으로 성능에 영향이 있을 수 있기때문에 다른 lock도 고민해볼것입니다.

### 캐시 

- 캐시 데이터나 값을 미리 복사해놓는 임시 저장소

**캐시 전략 패턴**
 

Application - cache - database

Application - cache (데이터가 있다면 해당 데이터 바로 반환 이를 cache hit)
			(원하는 데이터가 없다 -> db를 직접 찾아가서 반환 이를 cache miss)


**캐시 전략 패턴 - 읽기 전략**

Look aside (보다 옆(곁)을)
어플리케이션 - 캐시에 데이터가 있다면 캐시에서 데이터를 가져옴
 		- 캐시에 데이터가 없다면 곁을 봐서 db에서 데이터를 가져옴 - > 케시에 올려놓고 사용함

장점 - 캐시에 문제가 생기는 경우 db로 요청을 위임해서 데이터를 가져올 수 있음
단점 - 캐시, db를 잇는 연결점이 없어서 정합성 유지가 어려움, 또한 첫 번째 조회를 할 때는 항상 db로부터 데이터를 가져와서 캐시에 올려놓고 사용하므로 db에 부하 발생

Read through (읽다 통하여)

어플리케이션 - 캐시에 데이터가 있다면 캐시에서 데이터를 가져옴 
		- 캐시에 데이터가 없다면 캐시가 db에서 데이터를 직접 가져옴 그 데이터를 어플리케이션이 읽음

장점 - 캐시와 db 간의 연결점이 있어 데이터의 정합성이 보장됨
단점 - 캐시가 죽어버리면 어플리케이션도 문제가 생김

**쓰기 전략**
Write Around( 쓰다 우회)

캐시를 우회해서 직접 쓴다
어플리케이션 - db에 직접 쓴다
읽기 전략과 혼합해서 사용하면 캐시 미스가 일어났을 떄 직접 캐시 스토어에 쓰기도 함
장점 - 성능이 좋음 직접 db에 쓰기 때문, 불필요한 데이터를 캐시에 올리지 않음 리소스를 아낌, 캐시를 거치지 않아 성능이 좋음
단점 - 캐시와 db에 연결점이 없기 때문에 데이터 정합성 유지가 어렵다

Write back(쓰다 나중에)
캐시에 데이터를 한꺼번에 써놓고 나중에 db에 쓰기 작업

어플케이션 - 캐시에 많은 양의 데이터를 쓰기 작업-> 그 후 스케쥴링을 통해 db에 쓰기 작업 진행 
장점 - 쓰기 횟수 비용 줄일 수 있음 (스케쥴링을 통해 하나의 insert문으로 데이터를 묶어 쓰기 작업)
단점 - 캐시의 데이터 유실 문제(캐시 스토어에만 데이터를 쌓아놓은 상태에서 캐시가 죽어버리면 데이터가 db까지 직접 써지지 않음)

Write through(쓰다 통해서)
캐시를 통해서 쓰기를 진행
어플리케이션이 캐시에 먼저쓰고 db에 씀
장점 - 데이터 정합성이 보장됨 (캐시를 거치고 데이터베이스로 가기때문에)
단점 - 두번의 쓰기가 일어나기 때문에 성능 문제

**캐시 스탬피드 현상**
대량의 캐시 만료로 인해 모든 요청이 동시에 db로 몰리는 현상

발생 원인
 - 캐시가 만료되면 같은 데이터를 조회하려는 다수의 요청이 동시에 db에 몰려 성능 저하가 발생
- 트래픽이 많은 서비스에서 발생할 가능성이 높음 (예: 인기 상품, 이벤트 페이지)

해결 방법

	1	캐시 만료 시간(Random Expiration Time) 적용
	◦	모든 캐시가 같은 시간에 만료되지 않도록, 만료 시간을 랜덤하게 설정
	2	Lazy Loading + Mutex Lock 사용
	◦	특정 요청이 DB에서 데이터를 가져올 때, 다른 요청이 같은 작업을 수행하지 못하도록 락(lock) 적용
	◦	예: SETNX(Redis), Redisson Lock 활용
	3	Double Caching (이중 캐싱)
	◦	기존 캐시가 만료되더라도, 백업 캐시를 사용하여 갑작스러운 부하를 방지
	◦	예: 메모리 캐시(Redis) + 디스크 캐시 활용

1. 인기 판매 리스트 API → DB 조회에서 Redis 캐싱으로 변경

문제점 
  •	인기 상품 리스트는 동일한 데이터를 반복적으로 조회하는 요청이 많음


	•	매번 DB에서 조회하면 트래픽 증가 및 부하 발생
        인기 상품 리스트는 동일한 데이터를 반복적으로 조회하는 요청이 많음 
	•	높은 트래픽 시 DB 부하로 인해 응답 속도 저하 가능

레디스 활용

	•	읽기 성능 개선 → 동일한 요청을 캐시에서 바로 반환 (DB 부하 감소)
	•	TTL(Time To Live) 설정 가능 → 일정 시간마다 최신 데이터로 갱신 가능
	•	빠른 응답 시간 제공 → DB 접근 없이 Redis에서 바로 응답 가능

2. 쿠폰 발급 → 비관적 락(Pessimistic Lock)에서 Redis로 변경

문제점(비관적 락)

	•	트랜잭션 충돌 빈번: 쿠폰 발급 요청이 많아지면 락 경합이 발생하여 성능 저하
	•	DB 부하 증가: 모든 쿠폰 발급 요청이 DB에서 트랜잭션을 발생시킴
	•	확장성 문제: 동시 요청이 많을 경우 데드락(Deadlock) 위험 존재

레디스 활용

	•	Redis의 원자적 연산(DECR)을 활용하면 락 없이 동시성 보장
	•	쿠폰 재고를 Redis에서 관리하면 빠르게 감소 처리 가능
	•	DB 트랜잭션이 발생하지 않으므로 성능 향상
	•	TTL 설정을 통해 특정 시간 이후 자동 만료 가능

 

# 8. DB인덱스

### 1. 인덱스란

**1.1 인덱스란**
- 인덱스는 데이터베이스 테이블의 검색 속도를 향상시키기 위한 자료구조로 백과사전의 색인과 비슷
- 저장되는 컬럼의 값을 사용하여 항상 정렬된 상태를 유지하는 것이 특징, 이러한 특징으로 인해 인덱스는 INSERT, UPDATE, DELETE의 성능이 희생된다는 단점이 잇음.

**1.2 인덱스 자료구조**

MySQL 기준으로 B+Tree와 같은 변형 B-Tree 자료구조를 이용해서 인덱스를 구현
기본 토대인 B-Tree 인덱스는 컬럼의 값을 변형하지 않고 인덱스 구조체 내에서 항상 정렬된 상태로 유지합니다.

B-Tree(Balanced-Tree)에서는 크게 3가지 노드 존재. 
최상위에 하나의 루트 노드가 존재하며, 가장 하위 노드인 리프 노드가 존재. 이 두 노드의 중간에 존재하는 브랜치 노드가 존재. 
최하위 노드인 리프 노드에는 실제 데이터 레코드를 찾아가기 위한 주소값을 가지고 있음


**1.3 MySQL 스캔 방식**
MySQL에는 크게 인덱스 레인지 스캔, 인덱스 풀 스캔, 루스 인덱스 스캔 방식이 있음

인덱스 레인지 스캔
- 검색할 인덱스 범위가 결정되었을 경우 사용하며 가장 빠른특징.
 
- 인덱스에서 조건을 만족하는 값이 저장된 시작 리프 노드를 찾음(index seek)
- 시작 리프 노드부터 필요한 만큼 인덱스를 차례대로 읽음 (index scan)
- 인덱스 키와 레코드 주소를 이용해 저장된 페이지를 가져오고 레코드를 읽어옴.
- 레코드를 읽어오는 과정에서 랜덤 IO가 발생할 수 있음.
- 읽어야할 데이터 레코드가 전체 20-25%의 경우에는 풀 테이블 스캔(순차 IO를 이용)이 더욱 효과적일 수 있다.

인덱스 풀 스캔
- 인덱스를 사용하지만 인덱스를 처음부터 끝까지 모두 읽는 방식입니다.

- 인덱스를 ABC 순서로 만들었는데 조건절에 B 혹은 C로 검색하는 경우 사용
- 인덱스를 생성하는 목적은 아니지만, 그래도 풀 테이블 스캔보다는 낫다.

루스 인덱스 스캔
- 듬성듬성하게 인덱스를 읽는 것을 의미
- 중간에 필요하지 않은 인덱스 키 값은 무시하고 다음으로 넘어가는 형태로 처리
- group by, max(), min() 함수에 대해 최적화하는 경우에 사용


### 2. 쿼리 분석
**2.1 주요 쿼리 분석**

분석 대상 Repository 및 주요 쿼리:
- **CouponJpaRepository**  
  - `findCouponByCouponIdWithLock`
  - `findCouponByCouponId`
- **UserCouponJpaRepository**  
  - `findByUserIdAndCouponId`
  - `findAllByUserId`
- **ProductJpaRepository**  
  - `findProductByProductId`
  - `findAll(Pageable pageable)`
  - `findByIdWithLock`
- **SalesStatsJpaRepository**  
  - `findTopSellingProductIds` (조건: sold_date ≥ :startDate, 또는 BETWEEN :startDate AND :endDate)
  - `updateSalesStats` (ON DUPLICATE KEY UPDATE 사용)
- **UserPointJpaRepository**  
  - `findUserPointByUserIdWithLock`
  - `findUserPointByUserId`

**2.2 쿠폰 관련 쿼리**

**쿼리:** `SELECT c FROM Coupon c WHERE c.couponId = :couponId`  

**용도:** 쿠폰 조회 (락 버전/락 미사용 버전)  

**인덱스 고려 :**  
  - `couponId`는 PK 으로 기본 인덱스 설정이되므로 별도 인덱스 추가가 필요 없음


**2.3 UserCoupon 관련 쿼리**

- **쿼리:** `findByUserIdAndCouponId(Long userId, Long couponId)` 

  **용도:** 특정 사용자와 쿠폰 조합으로 조회  

- **쿼리:** `findAllByUserId(Long userId)`  

  **용도:** 특정 사용자의 모든 쿠폰 조회 

**인덱스 고려 :**  
  - user_id 가 pk는 아니지만 단순 조회에 쓰이기 때문에 인덱스 불필요할 것으로 예상
  - user_id, coupon_id 가 pk는 아니지만 단순 조회에 쓰이기 때문에 인덱스 불필요할 것으로 예상

**2.4 Product 관련 쿼리**

- **쿼리:** `findProductByProductId(Long productId)` 

  **용도:** 특정 상품을 상품 ID로 조회

- **쿼리:** `findByIdWithLock(Long productId)`  

  **용도:** 특정 상품을 상품 ID로 조회(비관적락)

- **쿼리:** `findAll(Pageable pageable)`  

  **용도:** 상품 조회 페이지 네이션 이용


**인덱스 고려 :**  
  - `productId`는 PK 으로 기본 인덱스 설정이되므로 별도 인덱스 추가가 필요 없음
  - `pagenation` 자체에 limit가 걸려있어 추가 적인 설정 필요 없음



**2.5 Sales 관련 쿼리**

- **쿼리:** `findTopSellingProductIds` 

  **용도:** 날짜 범위 별 인기 상품을 조회한다

- **쿼리:** `updateSalesStats`  

  **용도:** 상품 판매가 일어나면 통계테이블의 상품 수량을 업데이트한다

**인덱스 고려 :**  
  - sold_date 필드가 날짜 범위로 필터링에 사용되므로 인덱스가 필요
  - product_id는 그룹화에 사용되므로 인덱스가 필요
  - SUM(s.sold_quantity) DESC에서 정렬이 필요하므로 해당 필드에 대한 인덱스가 유리할 수 있음

**2.6 UserPoint 관련 쿼리** 

- **쿼리:** `findUserPointByUserIdWithLock` 

  **용도:** 유저 포인트를 유저ID로 조회한다 (비관적락)
- **쿼리:** `findUserPointByUserId`  

  **용도:** 유저포인트를 유저ID로 조회한다

**인덱스 고려 :**  
  - user_id 가 pk는 아니지만 단순 조회에 쓰이기 때문에 인덱스 불필요할 것으로 예상

### 3. 문제 쿼리 분석

```sql
SELECT s.product_id 
FROM sales_stats s 
WHERE s.sold_date BETWEEN :startDate AND :endDate 
GROUP BY s.product_id 
ORDER BY SUM(s.sold_quantity) DESC 
LIMIT :topN
```
**3.1 쿼리분석**

날짜 범위 조회 (BETWEEN)
GROUP BY를 통한 집계
ORDER BY와 집계 함수(SUM) 사용
LIMIT을 통한 결과 제한

**3.2 인덱스 적용**

2.1 인덱스 적용전 실행 계획 및 시간
- 기본 데이터 준비 (200만건)

![alt](./docs/img/데이터수.png)

 - 출력 시간

![alt](./docs/img/인덱스전.png)
 
 - explain

![alt](./docs/img/인덱스전_explain.png)

- db에서 직접 조회하는 경우 0.58 초 정도가 소요되며
실제 spring 을 활용해 조회할 경우 connection, 
다른 로직 처리등을 진행하면 이 쿼리에 해당하는 api는 1초이상의 시간이 소요될것으로 예상돼 불편을 줄수 있음


 -> 인덱스를 이용해 개선 필요
- 복합 인덱스 설정

![alt](./docs/img/인덱스설정.png)

- 쿼리 조회 결과

![alt](./docs/img/인덱스후.png)

(0.58 -> 0.26 ) 줄어든 것 확인

![alt](./docs/img/인덱스후_explain.png)

하지만 3개의 복합인덱스를 걸어뒀기 때문에 Using temporary; Using filesort는 유지되는 것을 알 수 있음

ORDER BY 절에 사용된 SUM(sold_quantity)는 집계 함수이기 때문에, 인덱스만으로 미리 정렬된 값을 얻을 수 없음

즉, WHERE와 GROUP BY는 커버링 인덱스로 최적화할 수 있지만,
ORDER BY SUM(sold_quantity) DESC는 각 그룹의 합을 계산한 후 정렬해야 하므로,
임시 테이블과 파일 소트가 발생하는 것을 완전히 없애기는 어려움.


인덱스를 다르게 걸었을 떄의 차이를 알 수 있게 인덱스 설정을 수정 해봄

product_id, sold_date 순서로 인덱스 설정

- 복합 인덱스 설정

![alt](./docs/img/인덱스설정_id_date.png)

- 쿼리 조회 결과

![alt](./docs/img/인덱스후_id_date.png)

오히려 2초로 늘어났음을 확인

**결론**
#### 1. 단일 인덱스 (sold_date)
- **구성**:  
  인덱스에 `sold_date`만 포함

- **WHERE 절**:  
  - `sold_date` 범위 조건에 대해 인덱스 스캔으로 빠르게 필터링 가능

- **GROUP BY 및 집계**:  
  - 인덱스에 `product_id`와 `sold_quantity`가 없으므로,  
    필터링 후 각 행의 해당 컬럼을 가져오기 위해 추가적인 테이블 데이터 페이지 조회가 필요

- **결과**:  
  - WHERE 조건은 최적화되지만, GROUP BY와 집계 시 추가 조회로 인한 오버헤드가 발생하여 전체 성능이 떨어질 가능성이 있음

---

#### 2. 복합 인덱스 (sold_date, product_id, sold_quantity)
- **구성**:  
  인덱스에 `sold_date`, `product_id`, `sold_quantity`가 순서대로 포함

- **WHERE 절**:  
  - 인덱스의 첫 번째 컬럼인 `sold_date`로 날짜 범위 필터링이 효율적으로 수행됨

- **GROUP BY 및 집계**:  
  - 두 번째 컬럼인 `product_id`가 바로 그룹핑에 사용됨  
  - 세 번째 컬럼인 `sold_quantity`가 포함되어 있어,  
    `SUM(sold_quantity)` 계산도 인덱스 내에서 처리 가능 (커버링 인덱스 효과)

- **전체 과정**:  
  - 인덱스만으로 WHERE, GROUP BY, 집계까지 모두 처리할 수 있으므로,  
    테이블 데이터 페이지 접근 횟수가 크게 줄어듦

- **결과**:  
  - 가장 효율적인 인덱스 옵션으로, 전반적인 쿼리 성능을 향상시킴

---

#### 3. 복합 인덱스 (product_id, sold_date, sold_quantity)
- **구성**:  
  인덱스에 `product_id`, `sold_date`, `sold_quantity` 순서로 포함

- **WHERE 절**:  
  - 첫 번째 컬럼이 `product_id`이나, 쿼리에서는 `product_id`에 대한 필터링 조건이 없음  
  - 이로 인해 인덱스의 첫 번째 키를 활용하지 못하여,  
    `sold_date`에 대한 효과적인 범위 검색이 어려움

- **GROUP BY 및 집계**:  
  - `product_id`는 GROUP BY에 사용되지만,  
    WHERE 조건 최적화가 제대로 이루어지지 않아 전체 인덱스 스캔 또는 불필요한 데이터 접근이 발생

- **결과**:  
  - 날짜 조건에 맞는 데이터를 찾기 위해 인덱스 전체를 스캔하거나 비효율적인 경로를 선택하게 되어,  
    쿼리 처리 시간이 길어짐 (성능 저하)


추후 상품명으로 like 조회 api를 추가하여 그 부분에도 개선사항이 있을 지 생각해볼 예정임

# 9. MSA 전환기

## 트랜잭션 처리 한계 및 Saga 패턴 설계 분석

서비스의 규모가 확장되어 MSA 형태로 각 도메인별로 배포 단위를 분리할 경우,  
실시간 주문 및 쿠폰 발급과 같이 여러 도메인에 걸친 업무 프로세스에서 발생하는  
트랜잭션 처리 한계를 어떻게 해결할 것인지에 대해 Saga 패턴과 이벤트 기반 아키텍처를 적용하는 방안을 설계

---

## 9.1 쿠폰 발급 서비스

### 9.1.1 기존 쿠폰 발급 로직 분석

- **구현 내용:**  
  쿠폰 발급 로직은 Redisson을 사용하여 분산 락을 적용하고, 로컬 트랜잭션 내에서 쿠폰 조회,  
  사용량 증가, Redis 재고 감소, 사용자 쿠폰 발급을 처리
  
- **비고:**  
  멘토링 시간에 데이터베이스를 독립적으로 분리하는 경우는 드물지만,  
  여기서는 각 서비스가 독립 데이터베이스를 사용하는 상황으로 가정

### 9.1.2 문제 상황: 동시 쿠폰 발급

- **시나리오:**  
  여러 사용자가 동시에 쿠폰 발급을 요청할 때,  
  Redisson 분산 락과 로컬 트랜잭션을 사용하여 쿠폰 사용량 증가 및 재고 감소를 처리
- **문제:**  
  발급 가능한 쿠폰 수(예: 최대 30개)를 초과하면 일부 요청은 실패

### 9.1.3 Saga 패턴의 역할 (쿠폰 발급)

- **각 서비스는 로컬 트랜잭션만 관리:**  
  - 쿠폰 서비스는 쿠폰 사용량 증가, 재고 감소 등을 자신의 데이터베이스에서 처리
  - 다른 서비스(예: 주문, 결제 등)도 각자 독립적인 로컬 트랜잭션을 관리
  
- **전체 업무 흐름 오케스트레이션:**  
  - Saga 패턴은 전체 프로세스를 여러 단계로 나누고,  
    각 단계의 성공/실패에 따라 보상(Compensation) 트랜잭션을 실행
  
- **실패 처리 예시:**  
  - 예를 들어, `couponService.incrementCouponUsage(coupon)` 호출 시 이미 최대 수량에 도달하면 예외가 발생하고,  
    Saga 오케스트레이터는 이전 단계에서 완료된 작업(예: 주문 생성, 결제 예약 등)에 대해 보상 작업을 실행

### 9.1.4 Saga 트랜잭션 제어 구현 방식 설계 (쿠폰 발급)

- **쿠폰 서비스 내 보상 트랜잭션:**  
  - 쿠폰 사용량 증가 단계에서 최대 발급 수를 초과하면 예외가 발생
  - Saga 오케스트레이터는 이를 감지하여,  
    이전 단계(예: 재고 감소, 사용자 쿠폰 발급 등)에 대해 보상 트랜잭션을 실행
  - 예를 들어, 이미 증가된 쿠폰 사용량을 복구하기 위해 `couponService.decrementCouponUsage(coupon)` 메서드를 호출할 수 있음
- **이벤트 기반 보상:**  
  - 각 서비스는 작업 완료 후 이벤트를 발행
  - Saga 오케스트레이터는 이 이벤트들을 수신하여 전체 프로세스의 성공 여부를 판단
  - 실패 발생 시, 관련 서비스(쿠폰 서비스, 주문 서비스 등)에 보상 이벤트를 발행하여 각 서비스가 보상 작업을 실행

### 9.1.5 해결방안

#### 트랜잭션 흐름
1. 쿠폰 발급 요청
  - 쿠폰 유효성 검증
  - 재고 확인
  - `CouponStockCheckEvent` 발행

2. 쿠폰 재고 감소
  - 재고 차감
  - `CouponStockDecreasedEvent` 발행

3. 사용자 쿠폰 발급
  - 쿠폰 발급 처리
  - `UserCouponIssuedEvent` 발행

**[실패 케이스 1: 재고 부족]**

쿠폰 재고 확인 실패
CouponStockInsufficientEvent 발행
이전 단계 작업 없음 - 보상 트랜잭션 불필요

**[실패 케이스 2: 쿠폰 발급 실패]**

재고 차감 완료
쿠폰 발급 실패
CouponIssueFailedEvent 발행
보상 트랜잭션: 재고 복구

### 9.1.6 쿠폰 발급 시 이벤트 발행 및 구독 도메인 분석 (쿠폰 도메인)

- **이벤트 발행 도메인:**  
  - 쿠폰 서비스는 쿠폰 발급이 성공적으로 완료되면 "CouponIssuedEvent"를 발행
  - 이 이벤트는 발급된 쿠폰 정보(사용자 ID, 쿠폰 ID, 발급 시각 등)를 포함
  - 이벤트는 `CouponFacade.issueCoupon()` 내에서 데이터 플랫폼 전송 컴포넌트(예: `DataPlatformPublisher`)를 통해 발행

- **이벤트 구독 도메인:**  
  - 별도의 데이터 플랫폼 연동 서비스(또는 이벤트 처리 서비스)는 메시지 브로커(Kafka, RabbitMQ 등)를 구독하여,  
    쿠폰 발급 이벤트를 포함한 각 도메인에서 발생한 이벤트를 수집하고,  
    이를 분석, 모니터링, 리워드 처리 등에 활용

---

## 9.2 상품 주문 서비스

### 9.2.1 기존 상품 주문 로직 분석

- **구현 내용:**  
  기존 주문 처리 로직은 `@Transactional`을 사용하여  
  사용자 검증, 상품 및 재고 검증, 쿠폰 적용, 잔액 차감, 주문 생성, 재고 감소, 판매 통계 업데이트 등을  
  하나의 로컬 트랜잭션 내에서 처리
  
- **문제점:**  
  단일 데이터베이스 내에서는 일관성을 보장할 수 있으나,  
  서비스가 분리된 MSA 환경에서는 데이터베이스가 독립적이므로 문제가 발생

### 9.2.2 문제 상황: 동시 상품 주문

- **시나리오:**  
  여러 사용자가 동시에 주문을 요청할 때,  
  DB 비관적 락과 로컬 트랜잭션을 사용하여 상품 재고 감소, 주문 생성, 잔액 차감, 판매 통계 업데이트를 처리
  
- **문제:**  
  주문 가능한 상품 수를 초과하는 경우,  
  일부 요청은 재고 부족으로 실패하며,  
  재고 감소 전에 진행된 주문 생성, 잔액 차감 등의 작업은 롤백

### 9.2.3 Saga 패턴의 역할 (상품 주문)

- **각 서비스는 로컬 트랜잭션만 관리:**  
  - 주문 서비스는 사용자 검증, 상품 및 재고 검증, 쿠폰 적용, 잔액 차감, 주문 생성, 재고 감소, 판매 통계 업데이트를  
    자신의 데이터베이스에서 로컬 트랜잭션으로 처리
  - 다른 도메인(예: 사용자 포인트, 배송 등)도 각자 로컬 트랜잭션을 관리

- **실패 처리:**  
  - 예를 들어, `productService.decreaseStock(orderPrepareResult.getOrderItems())` 호출 시 상품 수량이 부족하여 예외가 발생하면,  
    Saga 패턴은 이전 단계(예: 주문 생성, 잔액 차감 등)에 대해 보상 작업을 호출하여 전체 프로세스의 일관성을 유지

### 9.2.4 Saga 트랜잭션 제어 구현 방식 설계 (상품 주문)

- **보상 트랜잭션:**  
  - 주문 서비스에서 상품의 재고 감소 단계에서 예외 발생 시,  
    이미 생성된 주문 및 차감된 잔액에 대해 보상 트랜잭션(예: 주문 취소, 포인트 환불 등)을 실행할 수 있도록 Saga 오케스트레이터가  
    각 서비스에 보상 이벤트를 발행

- **이벤트 기반 보상:**  
  - 각 서비스는 작업 완료 후 성공 이벤트를 발행하고,  
    실패 시 보상 이벤트를 발행하여 Saga 오케스트레이터가 전체 프로세스의 성공 여부를 판단
  - 관련 서비스(주문, 사용자 포인트, 상품 재고 등)는 보상 이벤트를 수신하여 보상 작업을 실행함

### 9.2.5 해결 방안

#### 트랜잭션 흐름

  1. 주문 시작
   - 주문 정보 검증
   - `OrderCreatedEvent` 발행

2. 재고 확인 및 차감
   - 재고 유효성 검증
   - 재고 차감
   - `StockDecreasedEvent` 발행

3. 결제 처리
   - 결제 진행
   - `PaymentCompletedEvent` 발행

4. 주문 완료
   - 주문 상태 업데이트
   - `OrderCompletedEvent` 발행

**[실패 케이스 1: 재고 부족]**

재고 확인 실패
StockInsufficientEvent 발행
보상 트랜잭션: 주문 상태 취소

**[실패 케이스 2: 결제 실패]**

재고 차감 완료
결제 실패
PaymentFailedEvent 발행
보상 트랜잭션:

재고 복구
주문 상태 취소

### 9.2.6 상품 주문 시 이벤트 발행 및 구독 도메인 분석

- **이벤트 발행 도메인:**  
  - 주문 서비스는 상품 주문이 성공적으로 완료되면 "PlaceOrderEvent"와 같은 이벤트를 발행
  - 이 이벤트는 주문 ID, 사용자 ID, 상품 ID, 주문 수량, 주문 금액, 주문 시각 등 주문 관련 정보를 포함
  - 이벤트 발행은 주문 프로세스의 마지막 단계(외부 데이터 플랫폼 전송)에서 비동기적으로 처리

- **이벤트 구독 도메인:**  
  - 데이터 플랫폼 연동 서비스 또는 관련 후속 서비스(배송, 통계, 외부 플랫폼)는 메시지 브로커(Kafka, RabbitMQ 등)를 구독하여  
    "PlaceOrderEvent"를 수신합니다.
  - 이를 통해, 실시간 데이터 분석, 모니터링, 리포팅 등의 후속 처리가 진행되며,  
    핵심 주문 처리 로직과 별도로 데이터 전달이 안정적으로 이루어짐

---

## 9.3 결론

- **MSA 환경에서의 트랜잭션 처리:**  
  각 도메인은 자체 로컬 트랜잭션만을 관리하며,  
  서비스 간 일관성은 Saga 패턴과 보상 트랜잭션을 통해 일관성 보장
  보상 트랜잭션은 한 서비스에서 실패가 발생하면 이전 단계의 작업을 취소하거나 복구하는 방식으로 실행


# Apache Kafka 개요

Apache Kafka는 **실시간 데이터 파이프라인**과 **스트리밍 애플리케이션**을 구축하기 위해 널리 사용되는 **분산형 스트리밍 플랫폼** 입니다.
 높은 처리량, 낮은 지연 시간, 확장성을 제공하여 대규모 데이터 흐름을 처리하는 데 적합합니다. 이 문서는 Kafka의 주요 개념인 **Producer, Consumer, Broker, Topic, Partition, Consumer Group, Rebalancing**에 대해 설명합니다.

---

## 1. **Producer & Consumer**

### Producer (프로듀서)
- **정의:** 
  - **메시지를 Kafka 브로커에 발행**하는 서비스
- **기능:**
  - 특정 **토픽(Topic)** 에 메시지를 발행
  - 메시지를 어떤 **파티션(Partition)** 에 저장할지 결정
    - `Key`가 있는 경우: **Key 해시값**을 이용해 특정 파티션에 저장
    - `Key`가 없는 경우: **라운드 로빈 (Round-Robin)** 방식으로 균등하게 분배

---

### Consumer (컨슈머)
- **정의:** 
  - **Kafka 브로커에 적재된 메시지를 읽어오는** 서비스
- **기능:**
  - 각 **파티션 별 Offset** 을 유지하여 어디까지 처리했는지 추적
  - 메시지를 읽을 때마다 **Current-Offset** 을 업데이트하고 **Commit** 해야 함
    - `Current-Offset`: 
      - 컨슈머가 마지막으로 처리한 메시지의 위치를 나타냄
      - 동일한 메시지를 재처리하지 않고, 처리하지 않은 메시지를 건너뛰지 않도록 마지막으로 처리한 offset을 저장
  - **Offset Reset:**
    - 오류 발생 시 또는 특정 시점으로 돌아가야 할 때 `--reset-offsets` 옵션 사용
    - Consumer Group 단위로 **Offset 되돌리기** 가능

---

## 2. **Broker (브로커)**
- **정의:**
  - **Kafka 서버 유닛**으로 Producer와 Consumer 사이에서 메시지를 관리
- **기능:**
  - Producer로부터 메시지를 받아 **디스크에 저장**하고 **Offset 지정**
  - Consumer의 요청에 응답하여 저장된 메시지 **전송**
- **Cluster 내의 역할:**
  - **Controller**:
    - 다른 브로커 상태 모니터링
    - 장애 발생 시 **Leader 파티션** 을 다른 브로커로 **재분배**
  - **Coordinator**:
    - **Consumer Group 모니터링**
    - Consumer 장애 시 **Partition Rebalance (재배정)** 수행

---

## 3. **Message (메시지)**
- **정의:**
  - Kafka에서 **데이터의 최소 단위**
- **구조:**
  - `<Key, Message>` 형태로 구성
    - `Key`: 메시지의 분류 또는 순서를 보장하기 위한 식별자
    - `Message`: 실제 데이터 내용

---

## 4. **Topic & Partition (토픽 & 파티션)**

### Topic
- **정의:** 
  - **메시지를 분류**하기 위한 논리적인 단위
  - N개의 **Partition** 으로 구성
- **특징:**
  - 같은 토픽의 메시지는 여러 파티션에 나누어 저장
  - 대용량 트래픽을 파티션 개수만큼 **병렬 처리** 가능

---

### Partition
- **정의:** 
  - **메시지를 물리적으로 저장**하는 단위
- **특징:**
  - **순차 처리 보장:** 한 파티션 안에서는 메시지 순서가 보장됨
  - **병렬 처리 가능:** 여러 파티션으로 나누어 동시다발적인 처리가 가능
  - **Partitioner 사용:** 
    - Producer가 메시지 발행 시, **Key 해시** 또는 **Round-Robin** 방식으로 저장할 파티션 결정
    - **Key**가 있는 경우: 같은 Key는 항상 **같은 파티션**에 저장되어 순서 보장
    - **Key**가 없는 경우: Round-Robin 방식으로 **균등 분배**
- **제약사항:**
  - **한 파티션은 하나의 Consumer 에서만 소비** 가능
    - 여러 Consumer가 같은 파티션의 메시지를 읽으면 **순서 보장 불가**

---

## 5. **Consumer Group (컨슈머 그룹)**
- **정의:** 
  - **여러 개의 Consumer가 하나의 토픽을 동시에 소비**하기 위해 묶는 그룹
- **특징:**
  - 하나의 Consumer Group 내에서 **같은 파티션은 하나의 Consumer** 만 소비
  - **Consumer 확장성 보장:** 
    - Consumer Group에 Consumer 추가 시, 파티션을 **Rebalance (재배정)** 함
  - **여러 서비스에서 동일한 토픽을 소비**할 때 사용
    - 예: 주문 완료 메시지를 **결제 서비스**와 **상품 서비스**에서 각각 소비
- **설정 방법:**
  - 일반적으로 **애플리케이션 단위**로 Consumer Group 생성
  - 동일한 Topic에 대해 **여러 그룹**을 만들어 구독 가능

---

## 6. **Rebalancing (리밸런싱)**
- **정의:** 
  - **Consumer Group의 가용성과 확장성**을 위해 파티션 소유권을 재배정
- **발생 시점:**
  1. Consumer Group 내 **새로운 Consumer 추가** 시
  2. Consumer 장애 발생 시 **다른 Consumer에 파티션 재배정**
  3. **Topic에 새로운 Partition 추가** 시
- **주의사항:**
  - 리밸런싱 중에는 **메시지를 읽을 수 없음**
  - **장애 대응 및 확장성**을 위한 필수 개념

---

## 7. **참고 이미지**
- **Topic & Partition 구조:**
  ![Topic & Partition](https://prod-files-secure.s3.us-west-2.amazonaws.com/83c75a39-3aba-4ba4-a792-7aefe4b07895/dbf132c7-fea8-4dc1-9479-adb08f670f8b/Untitled.png)
- **Consumer Group & Partition 매칭:**
  ![Consumer Group](https://prod-files-secure.s3.us-west-2.amazonaws.com/83c75a39-3aba-4ba4-a792-7aefe4b07895/19e413f1-bb1b-4f32-92f6-1b10ac90ab4e/Untitled.png)

---

## 8. **정리 및 활용 팁**
- **Producer** 에서는 **Partitioner** 설정을 통해 메시지 순서 보장
- **Consumer Group** 은 애플리케이션 단위로 관리하여 **확장성과 가용성** 유지
- **Rebalancing** 발생 시 **일시적 정지**에 유의
- **Offset 관리** 를 통해 **중복 처리 방지** 및 **정확한 순서 유지**

---

## 9. kafka 결과

![alt](./docs/img/kafka결과.png)


## 부하 테스트 보고서 

## 부하테스트 시나리오 선정 및 선정된 시나리오의 API 개별 테스트
(리스트)
1. 쿠폰 등록 API (POST) /coupon/reg
2. 유저 쿠폰 리스트 API (GET) /coupon/{userId}/couponlist
3. 유저 쿠폰 발행 API(POST) /coupon/users/{userId}/coupon/{couponId}
4. 상품 주문 API(POST) /orders/payment
5. 상품 주문 API(POST) /products/{productId}
6. 상품 주문 API(POST) /products/productl ist
7. 인기 판매 조회 API(POST) /stats/products/top
8. 유저 잔액 조회 API(POST) /users/point
9. 유저 잔액 충전 조회 API(POST) /users/charge

이 중 유저 쿠폰 발행은

1. 높은 동시성 요청 : 쿠폰 발행은 이벤트나 프로모션 기간에 다수의 사용자가 동시에 요청할 가능성이 높아, 동시 접속 처리 및 트랜잭션 관리 성능 검증이 필수
2. 비즈니스 핵심 기능: 쿠폰 발행은 사용자 만족도와 매출에 직결되는 핵심 기능이므로, 부하가 걸렸을 때 안정적으로 동작하는지 확인하는 것이 중요
3. 데이터 무결성 및 일관성 검증: 쿠폰 발행 과정에서 중복 발행이나 오류가 발생할 경우, 사용자 신뢰도 하락 및 재발행 이슈가 발생할 수 있으므로, 부하 상황에서도 데이터의 정확성을 유지

따라 쿠폰 발행에 대한 부하테스트를 하기로 결정
```js
import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';

// 성공 및 실패 카운터
const successCounter = new Counter('successful_requests');
const failureCounter = new Counter('failed_requests');
const limitReachedCounter = new Counter('coupon_limit_reached');

// 테스트 설정
export const options = {
    stages: [
        { duration: '10s', target: 100 },
        { duration: '20s', target: 300 },
        { duration: '30s', target: 500 },
        { duration: '20s', target: 500 },
        { duration: '10s', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<3000'],
        'failed_requests': ['count<100'],
    },
};

// 테스트할 쿠폰 정보
const coupons = [
    { id: 1, maxCount: 30 },
    { id: 2, maxCount: 30 },
    { id: 3, maxCount: 30 },
    { id: 4, maxCount: 30 },
    { id: 5, maxCount: 30 }
];

export default function() {
    // 랜덤 사용자 ID (1-500)
    const userId = Math.floor(Math.random() * 500) + 1;
    // 랜덤 쿠폰 선택
    const couponIndex = Math.floor(Math.random() * coupons.length);
    const coupon = coupons[couponIndex];

    // 클라이언트 측 쿠폰 발급 제한 검사 제거
    // 대신 서버가 쿠폰 한도를 관리하므로 요청을 보냄

    // API 엔드포인트 URL
    const url = `http://host.docker.internal:8080/coupon/users/${userId}/coupon/${coupon.id}`;

    // HTTP 요청 헤더
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    // POST 요청 전송
    const response = http.post(url, null, params);

    // 응답 처리
    if (response.status === 200) {
        try {
            const responseBody = JSON.parse(response.body);
            if (responseBody.result_code === 200) {
                successCounter.add(1);
                // 디버그 로그는 주석 처리하거나 필요 시 조건부로 출력
                // console.log(`쿠폰 ${coupon.id} 발급 성공 (사용자: ${userId})`);
            } else {
                failureCounter.add(1);
                // console.log(`API 오류: ${responseBody.message}`);
            }
        } catch (e) {
            failureCounter.add(1);
            // console.log(`응답 파싱 오류: ${e}`);
        }
    } else if (response.status === 400) {
        try {
            const responseBody = JSON.parse(response.body);
            if (responseBody.message && responseBody.message.includes("limit reached")) {
                // 서버에서 한도 도달로 판단
                limitReachedCounter.add(1);
                // console.log(`서버 응답: 쿠폰 ${coupon.id} 한도 도달`);
            } else {
                failureCounter.add(1);
                // console.log(`요청 오류: ${responseBody.message}`);
            }
        } catch (e) {
            failureCounter.add(1);
            // console.log(`응답 파싱 오류: ${e}`);
        }
    } else {
        failureCounter.add(1);
        // console.log(`HTTP 오류: ${response.status}`);
    }

    // 요청 간 대기 시간
    sleep(Math.random() * 1 + 0.5); // 0.5 ~ 1.5초 대기
}

// 테스트 완료 후 실행
export function teardown() {
    console.log("테스트 완료.");
}
```
테스트 코는 다음과 같다

![alt](./docs/img/부하테스트_결과.png)

HTTP 요청 자체는 정상적으로 응답되고 있으며
평균 응답시간(15.18ms)과 p95(36.26ms) 상태. 또한 http_req_failed 지표는 0%로, 네트워크나 연결 측면의 오류는 없음
다만 커스텀 실패 카운트가 28000건 이상 나온 상황
쿠폰 발급 한도가 넘은 경우인데 이 경우 어떻게 처리할 지 좀더 고민이 필요

 ![alt](./docs/img/디비결과.png)

db엔 발급이 잘 된것을 확인 할 수 있음

## 장애 대응
5.1 장애 감지 및 모니터링
모니터링 도구 적용:

Prometheus + Grafana: 실시간 메트릭 시각화

APM (New Relic, Datadog) 도입: 트랜잭션 추적 및 성능 분석

알림 시스템 구성: Slack, 이메일 연동

5.2 장애 대응 절차
장애 감지 후 대응 시나리오:
  Step 1. 장애 감지 및 알림 수신: 
    - 모니터링 도구 알림 확인
  
  Step 2. 장애 원인 분석:
    - Redis 분산락 확인

  Step 3. 장애 복구 조치:
    - Redis 분산락 해제
    - 백엔드 서비스 스케일 아웃
    - 빠른 롤백 준비

  Step 4. 후속 조치 및 로그 분석:
    - 발생 원인 분석 및 재발 방지책 수립
    - 장애 대응 과정 기록 및 리뷰
    - 모니터링 철저히 확인

결론 및 개선 계획
 문제점 요약:
  높은 동시성 요청에 대한 실패 
  실제 요청 처리는 됐으나 쿠폰 발급 한도 제한으로 인해 에러가나는 상황
  - > 수정 필요
```
 // 쿠폰 발급 가능 여부 확인 및 발급 수 증가
    public void incrementUsage() {
        if (currentCount >= maxCount) {
            throw new IllegalArgumentException("Coupon usage limit reached");
        }
        this.currentCount += 1;
    }
```
