import http from 'k6/http';
import { sleep, check } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';
import { SharedArray } from 'k6/data';
import { randomItem, randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

export const options = {
  vus: 10,
  iterations: 100,
  thresholds: {
    'http_req_duration': ['p(95)<500'],  // 95%의 요청이 500ms 이내여야 함
    'http_req_failed': ['rate<0.1'],     // "failed_requests" 대신 "http_req_failed" 사용
    'success_rate': ['rate>0.9']         // 90% 이상의 성공률 기대
  }
};

const errorRate = new Rate('errors');
const successRate = new Rate('success_rate');
const couponRegTrend = new Trend('coupon_reg_duration');

// 쿠폰 등록 API 테스트
export default function() {
  // 테스트용 데이터
  const userToken = "your-auth-token";
  const BASE_URL = 'http://host.docker.internal:8080'; // 실제 API 서버 URL로 변경 필요

  // 현재 날짜 + 30일 계산
  const today = new Date();
  const validDate = new Date(today);
  validDate.setDate(today.getDate() + 30);
  const formattedValidDate = validDate.toISOString().split('T')[0]; // YYYY-MM-DD 형식

  // 고유한 쿠폰 코드 생성
  const couponNumber = (__VU * 1000) + __ITER;  // VU와 반복 횟수를 조합하여 고유 번호 생성
  const couponCode = `WELCOME2025_${couponNumber}`;

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
    code: couponCode,
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

  // 측정 지표 기록
  errorRate.add(!response);
  successRate.add(response);
  couponRegTrend.add(response.timings.duration);

  // 로깅
  console.log(`쿠폰 등록: ${couponCode}, 상태: ${response ? '성공' : '실패'}`);

  // 다음 요청까지 100-500ms 대기
  sleep(randomIntBetween(0.1, 0.5));
}

export function handleSummary(data) {
  return {
    "coupon_reg_test_summary.html": htmlReport(data),
    "stdout": textSummary(data, { indent: " ", enableColors: true }),
  };
}