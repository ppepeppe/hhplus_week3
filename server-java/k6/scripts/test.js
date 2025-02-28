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