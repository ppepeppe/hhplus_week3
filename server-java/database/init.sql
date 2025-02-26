CREATE TABLE coupon (
    coupon_id BIGINT AUTO_INCREMENT PRIMARY KEY,      -- 쿠폰 ID (PK)
    code VARCHAR(255) NOT NULL,                       -- 쿠폰 코드
    discount_percent DOUBLE NOT NULL,                 -- 할인율
    valid_date DATE NOT NULL,                         -- 유효 기간
    max_count INT NOT NULL,                           -- 최대 발급 수
    current_count INT DEFAULT 0                       -- 현재 발급 수 (기본값: 0)
);
CREATE TABLE user_coupon (
    user_coupon_id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 사용자 쿠폰 ID (PK)
    user_id BIGINT NOT NULL,                          -- 사용자 ID
    coupon_id BIGINT NOT NULL,                        -- 쿠폰 ID
    is_used BOOLEAN DEFAULT FALSE                     -- 사용 여부 (기본값: false)
);
CREATE TABLE `order` (
     order_id BIGINT AUTO_INCREMENT PRIMARY KEY,       -- 주문 ID (PK)
     user_id BIGINT NOT NULL,                          -- 사용자 ID
     total_payment_amount INT NOT NULL,                -- 총 주문 가격
     total_quantity INT NOT NULL                       -- 총 주문 수량
);
CREATE TABLE `order_item` (
     order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,       -- 주문 아이템 ID (PK)
     order_id BIGINT NOT NULL,                          -- 주문 ID
     product_id BIGINT NOT NULL,                          -- 상품 ID
     payment_amount INT NOT NULL,                -- 주문 가격
     quantity INT NOT NULL                       -- 주문 수량
);
CREATE TABLE `payment` (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,     -- 결제 ID (PK)
    user_id BIGINT NOT NULL,                          -- 사용자 ID
    point INT NOT NULL,                               -- 가격
    type VARCHAR(50) NOT NULL                         -- 타입
);
CREATE TABLE `product` (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,     -- 상품 ID (PK)
    name VARCHAR(255) NOT NULL,                       -- 상품 이름
    price INT NOT NULL,                               -- 상품 가격
    quantity INT NOT NULL,                            -- 상품 수량
    sales INT NOT NULL DEFAULT 0                      -- 판매량 (기본값: 0)
);
CREATE TABLE `sales_stats` (
    stats_id BIGINT AUTO_INCREMENT PRIMARY KEY,       -- 통계 ID (PK)
    product_id BIGINT NOT NULL,                       -- 상품 ID
    sold_quantity INT NOT NULL,                       -- 판매된 수량
    sold_date DATE NOT NULL                           -- 판매 날짜
);
CREATE TABLE `user` (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,        -- 회원 ID (PK)
    name VARCHAR(255) NOT NULL                        -- 회원 이름
);
CREATE TABLE `user_point` (
    user_point_id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 회원 포인트 ID (PK)
    user_id BIGINT NOT NULL,                          -- 회원 ID
    point INT NOT NULL                                -- 회원 포인트
);

CREATE TABLE `order_outbox` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 회원 포인트 ID (PK)
    event_type VARCHAR(255) NOT NULL,                          -- 회원 ID
    payload VARCHAR(255) NOT NULL,                                -- 회원 포인트
    status VARCHAR(255) NOT NULL,
    fail_count INT NOT NULL ,
    error_message VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    sent_at DATETIME NOT NULL
);



INSERT INTO coupon (code, discount_percent, valid_date, max_count, current_count)
VALUES ("TESTCODE", 0.25, "2025-1-11", 30, 0);

INSERT INTO user_coupon (user_id, coupon_id, is_used)
VALUES (1, 1, false);

-- INSERT INTO `order` (user_id, total_payment_amount, total_quantity)
-- VALUES (1, 100000, 1);

INSERT INTO `payment` (user_id, point, type)
VALUES (1, 100000, "CHARGE");

-- INSERT INTO `product` (name, price, quantity, sales)
-- VALUES ("패딩", 100000, 10, 0);
--
-- INSERT INTO `sales_stats` (product_id, sold_quantity, sold_date)
-- VALUES (1, 1, "2025-1-11");

INSERT INTO `user` (name)
VALUES ("seongdo");

INSERT INTO `user_point` (user_id, point)
VALUES (1, 200000);
INSERT INTO product (name, price, quantity, sales)
VALUES
    ('Product A', 1000, 10, 0),
    ('Product B', 1500, 40, 25),
    ('Product C', 2000, 30, 20),
    ('Product D', 3000, 20, 15),
    ('Product E', 2500, 10, 5),
    ('Product G', 2500, 10, 5);


-- Sales Stats 테이블 초기 데이터 삽입
INSERT INTO sales_stats (product_id, sold_quantity, sold_date)
VALUES
    (1, 10, CURDATE() - INTERVAL 2 DAY), -- 2일 전 판매 데이터
    (1, 5, CURDATE() - INTERVAL 1 DAY), -- 1일 전 판매 데이터
    (2, 8, CURDATE() - INTERVAL 3 DAY), -- 3일 전 판매 데이터
    (2, 12, CURDATE() - INTERVAL 1 DAY), -- 1일 전 판매 데이터
    (3, 15, CURDATE() - INTERVAL 2 DAY), -- 2일 전 판매 데이터
    (4, 6, CURDATE() - INTERVAL 3 DAY), -- 3일 전 판매 데이터
    (6, 30, CURDATE() - INTERVAL 5 DAY), -- 3일 전 판매 데이터
    (5, 4, CURDATE() - INTERVAL 1 DAY); -- 1일 전 판매 데이터
