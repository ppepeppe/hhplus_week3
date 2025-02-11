package kr.hhplus.be.server.common.util;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedissonLockService {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 주어진 키로 RLock 객체를 반환합니다.
     *
     * @param key 락 키
     * @return RLock 객체
     */
    public RLock getLock(String key) {
        return redissonClient.getLock(key);
    }

    /**
     * 주어진 키에 대해 락을 획득합니다.
     *
     * @param key       락 키 (예: "coupon:lock:1")
     * @param waitTime  락 획득을 위해 대기할 최대 시간
     * @param leaseTime 락 보유 시간 (획득 후 자동 해제 시간)
     * @param unit      시간 단위
     * @return 락 획득 성공 시 true, 실패 시 false
     * @throws InterruptedException 인터럽트 발생 시 예외 발생
     */
    public boolean acquireLock(String key, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        RLock lock = getLock(key);
        return lock.tryLock(waitTime, leaseTime, unit);
    }

    /**
     * 주어진 키의 락을 해제합니다.
     *
     * @param key 락 키
     */
    public void releaseLock(String key) {
        RLock lock = getLock(key);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
