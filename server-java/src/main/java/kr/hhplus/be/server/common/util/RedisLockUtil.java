package kr.hhplus.be.server.common.util;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisLockUtil {

    private final RedissonClient redissonClient;

    public RedisLockUtil(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 락 획득 (Redisson tryLock 사용)
     *
     * @param key      락을 걸 key
     * @param userId   요청한 사용자 ID
     * @param waitTime 최대 대기 시간 (초)
     * @param leaseTime 락 유지 시간 (초)
     * @return 락 획득 성공 여부
     */
    public boolean acquireLock(String key, Long userId, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(key);

        try {
            boolean success = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (success) {
                System.out.println("✅ 락 획득 성공: " + key + " userId = " + userId);
            } else {
                System.out.println("❌ 락 획득 실패: " + key + " userId = " + userId);
            }
            return success;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("⛔ 락 획득 중 인터럽트 발생: " + key + " userId = " + userId);
            return false;
        }
    }

    /**
     * 락 해제
     *
     * @param key    락을 걸었던 key
     */
    public void releaseLock(String key, Long userId) {
        RLock lock = redissonClient.getLock(key);

        if (lock.isHeldByCurrentThread()) { // 현재 스레드가 락을 가지고 있는지 확인
            lock.unlock();
            System.out.println("🔓 락 해제 완료: " + key + " userId = " + userId);
        } else {
            System.out.println("⚠️ 락 해제 실패 (다른 스레드가 보유 중): " + key);
        }
    }
}
