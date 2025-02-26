import http from 'k6/http';
import { sleep, check } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';
import { SharedArray } from 'k6/data';
import { randomItem, randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

// 사용자 데이터
const users = new SharedArray('users', function () {
    return JSON.parse(open('/test-data/users.json')).users;
});

// 상품 데이터
const products = new SharedArray('products', function () {
    return JSON.parse(open('/test-data/products.json')).products;
});

// 쿠폰 데이터
const coupons = new SharedArray('coupons', function () {
    return JSON.parse(open('/test-data/coupons.json')).coupons;
});

const errorRate = new Rate('errors');
const successRate = new Rate('success_rate');
const couponRegTrend = new Trend('coupon_reg_duration');

// 쿠폰 등록 API 테스트 예시
export default function() {
    // 테스트용 데이터
    const userToken = "your-auth-token";
    const BASE_URL = 'http://host.docker.internal:8080'; // 실제 API 서버 URL로 변경 필요

    // 현재 날짜 + 30일 계산
    const today = new Date();
    const validDate = new Date(today);
    validDate.setDate(today.getDate() + 30);
    const formattedValidDate = validDate.toISOString().split('T')[0]; // YYYY-MM-DD 형식

    // 헤더 함수
    function getHeaders(token) {
        return {
            'Content-Type': 'application/json'
            // 'Authorization': `Bearer ${token}`
        };
    }

    // 쿠폰 등록 API 호출
    const url = `${BASE_URL}/coupon/reg`;
    const payload = JSON.stringify({
        couponId: null,  // 새 쿠폰 등록이므로 null
        code: "WELCOME2024",
        discountPercent: 0.25,  // 25% 할인
        validDate: formattedValidDate,
        maxCount: 30,
        currentCount: 0
    });

    const params = {
        headers: getHeaders(userToken),
        tags: { name: 'couponRegApi' }
    };

    const response = check(http.post(url, payload, params), {
        'coupon-reg-status-200': (r) => r.status === 200,
        'coupon-reg-valid-json': (r) => r.json() !== null,
    });

    errorRate.add(!response);
    successRate.add(response);
    couponRegTrend.add(response.timings.duration);

    // 다음 요청까지 1-3초 대기
    sleep(randomIntBetween(1, 3));
}
// import http from 'k6/http';
// import { sleep, check } from 'k6';
// import { Counter, Rate, Trend } from 'k6/metrics';
// import { SharedArray } from 'k6/data';
// import { randomItem, randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
// import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
// //
// // // 사용자 데이터
// const users = new SharedArray('users', function () {
//   return JSON.parse(open('/test-data/users.json')).users;
// });
//
// // 상품 데이터
// const products = new SharedArray('products', function () {
//   return JSON.parse(open('/test-data/products.json')).products;
// });
//
// // 쿠폰 데이터
// const coupons = new SharedArray('coupons', function () {
//   return JSON.parse(open('/test-data/coupons.json')).coupons;
// });
// const errorRate = new Rate('errors');
// const successRate = new Rate('success_rate');
// const couponRegTrend = new Trend('coupon_reg_duration');
//
// // 쿠폰 등록 API 테스트 예시
// export default function() {
//   // 테스트용 데이터
//   const userId = "user123";
//   const couponCode = "WELCOME2024";
//   const userToken = "your-auth-token";
//   const BASE_URL = 'http://host.docker.internal:8080'; // 실제 API 서버 URL로 변경 필요
//
//   // 헤더 함수 (getHeaders 함수가 정의되어 있지 않아서 추가)
//   function getHeaders(token) {
//     return {
//       'Content-Type': 'application/json'
//       // 'Authorization': `Bearer ${token}`
//     };
//   }
//
//   // 쿠폰 등록 API 호출
//   const url = `${BASE_URL}/coupon/reg`;
//   const payload = JSON.stringify({
//     userId: userId,
//     couponCode: couponCode
//   });
//
//   const params = {
//     headers: getHeaders(userToken),
//     tags: { name: 'couponRegApi' }
//   };
//
//   const response = check(http.post(url, payload, params), {
//     'coupon-reg-status-200': (r) => r.status === 200,
//     'coupon-reg-valid-json': (r) => r.json() !== null,
//   });
//
//   errorRate.add(!response);
//   successRate.add(response);
//   // 다음 요청까지 1-3초 대기
//   sleep(randomIntBetween(1, 3));
// }
//
// // 사용자 정의 메트릭

// const couponRegTrend = new Trend('coupon_reg_duration');
// const couponListTrend = new Trend('coupon_list_duration');
// const couponIssueTrend = new Trend('coupon_issue_duration');
// const orderPaymentTrend = new Trend('order_payment_duration');
// const productDetailTrend = new Trend('product_detail_duration');
// const productListTrend = new Trend('product_list_duration');
// const topProductsTrend = new Trend('top_products_duration');
// const userPointTrend = new Trend('user_point_duration');
// const userChargeTrend = new Trend('user_charge_duration');
//
// // 옵션 설정
// export const options = {
//   // 기본 부하 테스트 설정
//   stages: [
//     { duration: '2m', target: 100 }, // 2분 동안 100명의 가상 사용자까지 증가
//     { duration: '5m', target: 100 }, // 5분 동안 100명 유지
//     { duration: '2m', target: 200 }, // 2분 동안 200명으로 증가
//     { duration: '5m', target: 200 }, // 5분 동안 200명 유지
//     { duration: '2m', target: 0 },   // 2분 동안 점차 감소
//   ],
//   thresholds: {
//     http_req_duration: ['p(95)<2000'], // 95% 요청이 2초 이내 완료되어야 함
//     'http_req_duration{name:couponRegApi}': ['p(95)<1500'],
//     'http_req_duration{name:couponListApi}': ['p(95)<1000'],
//     'http_req_duration{name:couponIssueApi}': ['p(95)<1500'],
//     'http_req_duration{name:orderPaymentApi}': ['p(95)<2000'],
//     'http_req_duration{name:productDetailApi}': ['p(95)<1000'],
//     'http_req_duration{name:productListApi}': ['p(95)<1200'],
//     'http_req_duration{name:topProductsApi}': ['p(95)<1000'],
//     'http_req_duration{name:userPointApi}': ['p(95)<800'],
//     'http_req_duration{name:userChargeApi}': ['p(95)<1500'],
//     'errors': ['rate<0.01'], // 오류율 1% 미만
//   },
// };
//
// // 설정
// const BASE_URL = 'http://localhost:8080'; // 실제 API 서버 URL로 변경 필요
// const SLEEP_DURATION = { min: 1, max: 5 }; // 요청 사이의 대기 시간(초)
//
// // 쿠폰 등록 API
// function couponRegApi(userToken, userId, couponCode) {
//   const url = `${BASE_URL}/coupon/reg`;
//   const payload = JSON.stringify({
//     userId: userId,
//     couponCode: couponCode
//   });
// }
// //
// //   const params = {
// //     headers: getHeaders(userToken),
// //     tags: { name: 'couponRegApi' }
// //   };
// //
// //   const response = check(http.post(url, payload, params), {
// //     'coupon-reg-status-200': (r) => r.status === 200,
// //     'coupon-reg-valid-json': (r) => r.json() !== null,
// //   });
// //
// //   errorRate.add(!response);
// //   successRate.add(response);
// //   couponRegTrend.add(response.timings.duration);
// // }
// //
// // // 유저 쿠폰 리스트 API
// // function couponListApi(userToken, userId) {
// //   const url = `${BASE_URL}/coupon/${userId}/couponlist`;
// //
// //   const params = {
// //     headers: getHeaders(userToken),
// //     tags: { name: 'couponListApi' }
// //   };
// //
// //   const response = check(http.get(url, params), {
// //     'coupon-list-status-200': (r) => r.status === 200,
// //     'coupon-list-valid-json': (r) => r.json() !== null,
// //   });
// //
// //   errorRate.add(!response);
// //   successRate.add(response);
// //   couponListTrend.add(response.timings.duration);
// // }
// //
// // // 유저 쿠폰 발행 API
// // function couponIssueApi(userToken, userId, couponId) {
// //   const url = `${BASE_URL}/coupon/users/${userId}/coupon/${couponId}`;
// //   const payload = JSON.stringify({});
// //
// //   const params = {
// //     headers: getHeaders(userToken),
// //     tags: { name: 'couponIssueApi' }
// //   };
// //
// //   const response = check(http.post(url, payload, params), {
// //     'coupon-issue-status-200': (r) => r.status === 200,
// //     'coupon-issue-valid-json': (r) => r.json() !== null,
// //   });
// //
// //   errorRate.add(!response);
// //   successRate.add(response);
// //   couponIssueTrend.add(response.timings.duration);
// // }
// //
// // // 상품 주문 결제 API
// // function orderPaymentApi(userToken, userId, productId, couponId) {
// //   const url = `${BASE_URL}/orders/payment`;
// //   const payload = JSON.stringify({
// //     userId: userId,
// //     productId: productId,
// //     quantity: 1,
// //     paymentMethod: "POINT",
// //     couponId: couponId
// //   });
// //
// //   const params = {
// //     headers: getHeaders(userToken),
// //     tags: { name: 'orderPaymentApi' }
// //   };
// //
// //   const response = check(http.post(url, payload, params), {
// //     'order-payment-status-200': (r) => r.status === 200,
// //     'order-payment-valid-json': (r) => r.json() !== null,
// //   });
// //
// //   errorRate.add(!response);
// //   successRate.add(response);
// //   orderPaymentTrend.add(response.timings.duration);
// // }
// //
// // // 상품 정보 조회 API
// // function productDetailApi(userToken, productId) {
// //   const url = `${BASE_URL}/products/${productId}`;
// //   const payload = JSON.stringify({});
// //
// //   const params = {
// //     headers: getHeaders(userToken),
// //     tags: { name: 'productDetailApi' }
// //   };
// //
// //   const response = check(http.post(url, payload, params), {
// //     'product-detail-status-200': (r) => r.status === 200,
// //     'product-detail-valid-json': (r) => r.json() !== null,
// //   });
// //
// //   errorRate.add(!response);
// //   successRate.add(response);
// //   productDetailTrend.add(response.timings.duration);
// // }
// //
// // // 상품 리스트 조회 API
// // function productListApi(userToken) {
// //   const url = `${BASE_URL}/products/productlist`;
// //   const payload = JSON.stringify({
// //     page: 1,
// //     size: 20,
// //     sortBy: "popularity"
// //   });
// //
// //   const params = {
// //     headers: getHeaders(userToken),
// //     tags: { name: 'productListApi' }
// //   };
// //
// //   const response = check(http.post(url, payload, params), {
// //     'product-list-status-200': (r) => r.status === 200,
// //     'product-list-valid-json': (r) => r.json() !== null,
// //   });
// //
// //   errorRate.add(!response);
// //   successRate.add(response);
// //   productListTrend.add(response.timings.duration);
// // }
// //
// // // 인기 판매 조회 API
// // function topProductsApi(userToken) {
// //   const url = `${BASE_URL}/stats/products/top`;
// //   const payload = JSON.stringify({
// //     limit: 10,
// //     period: "DAILY"
// //   });
// //
// //   const params = {
// //     headers: getHeaders(userToken),
// //     tags: { name: 'topProductsApi' }
// //   };
// //
// //   const response = check(http.post(url, payload, params), {
// //     'top-products-status-200': (r) => r.status === 200,
// //     'top-products-valid-json': (r) => r.json() !== null,
// //   });
// //
// //   errorRate.add(!response);
// //   successRate.add(response);
// //   topProductsTrend.add(response.timings.duration);
// // }
// //
// // // 유저 잔액 조회 API
// // function userPointApi(userToken, userId) {
// //   const url = `${BASE_URL}/users/point`;
// //   const payload = JSON.stringify({
// //     userId: userId
// //   });
// //
// //   const params = {
// //     headers: getHeaders(userToken),
// //     tags: { name: 'userPointApi' }
// //   };
// //
// //   const response = check(http.post(url, payload, params), {
// //     'user-point-status-200': (r) => r.status === 200,
// //     'user-point-valid-json': (r) => r.json() !== null,
// //   });
// //
// //   errorRate.add(!response);
// //   successRate.add(response);
// //   userPointTrend.add(response.timings.duration);
// // }
// //
// // // 유저 잔액 충전 API
// // function userChargeApi(userToken, userId, amount) {
// //   const url = `${BASE_URL}/users/charge`;
// //   const payload = JSON.stringify({
// //     userId: userId,
// //     amount: amount,
// //     paymentMethod: "CARD"
// //   });
// //
// //   const params = {
// //     headers: getHeaders(userToken),
// //     tags: { name: 'userChargeApi' }
// //   };
// //
// //   const response = check(http.post(url, payload, params), {
// //     'user-charge-status-200': (r) => r.status === 200,
// //     'user-charge-valid-json': (r) => r.json() !== null,
// //   });
// //
// //   errorRate.add(!response);
// //   successRate.add(response);
// //   userChargeTrend.add(response.timings.duration);
// // }
// //
// // // 비즈니스 시나리오 1: 상품 구매 프로세스
// // function productPurchaseScenario() {
// //   const user = randomItem(users);
// //   const product = randomItem(products);
// //   const coupon = randomItem(coupons);
// //
// //   // 상품 리스트 조회
// //   productListApi(user.token);
// //   sleep(randomIntBetween(SLEEP_DURATION.min, SLEEP_DURATION.max));
// //
// //   // 상품 상세 조회
// //   productDetailApi(user.token, product.id);
// //   sleep(randomIntBetween(SLEEP_DURATION.min, SLEEP_DURATION.max));
// //
// //   // 유저 쿠폰 리스트 조회
// //   couponListApi(user.token, user.id);
// //   sleep(randomIntBetween(SLEEP_DURATION.min, SLEEP_DURATION.max));
// //
// //   // 상품 주문 및 결제
// //   orderPaymentApi(user.token, user.id, product.id, coupon.id);
// // }
// //
// // // 비즈니스 시나리오 2: 쿠폰 등록 및 사용
// // function couponUseScenario() {
// //   const user = randomItem(users);
// //   const product = randomItem(products);
// //   const coupon = randomItem(coupons);
// //
// //   // 쿠폰 등록
// //   couponRegApi(user.token, user.id, coupon.code);
// //   sleep(randomIntBetween(SLEEP_DURATION.min, SLEEP_DURATION.max));
// //
// //   // 유저 쿠폰 리스트 조회
// //   couponListApi(user.token, user.id);
// //   sleep(randomIntBetween(SLEEP_DURATION.min, SLEEP_DURATION.max));
// //
// //   // 상품 주문 및 결제
// //   orderPaymentApi(user.token, user.id, product.id, coupon.id);
// // }
// //
// // // 비즈니스 시나리오 3: 포인트 충전 및 사용
// // function pointChargeScenario() {
// //   const user = randomItem(users);
// //   const product = randomItem(products);
// //
// //   // 유저 잔액 조회
// //   userPointApi(user.token, user.id);
// //   sleep(randomIntBetween(SLEEP_DURATION.min, SLEEP_DURATION.max));
// //
// //   // 유저 잔액 충전
// //   userChargeApi(user.token, user.id, 50000);
// //   sleep(randomIntBetween(SLEEP_DURATION.min, SLEEP_DURATION.max));
// //
// //   // 상품 구매
// //   orderPaymentApi(user.token, user.id, product.id, null);
// // }
//
// // k6 기본 실행 함수
// // export default function() {
//   // 시나리오 랜덤 실행
// //   const scenarioSelector = Math.random();
// //
// //   if (scenarioSelector < 0.4) {
// //     // 40% 확률로 상품 구매 시나리오 실행
// //     productPurchaseScenario();
// //   } else if (scenarioSelector < 0.7) {
// //     // 30% 확률로 쿠폰 사용 시나리오 실행
// //     couponUseScenario();
// //   } else {
// //     // 30% 확률로 포인트 충전 시나리오 실행
// //     pointChargeScenario();
// //   }
// //
// //   // 다음 반복까지 대기
// //   sleep(randomIntBetween(1, 3));
// // }
//
// // 결과 리포트 생성
// export function handleSummary(data) {
//   return {
//     "summary.html": htmlReport(data),
//     "summary.json": JSON.stringify(data),
//   };
// }
