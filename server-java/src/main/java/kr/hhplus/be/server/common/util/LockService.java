package kr.hhplus.be.server.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class LockService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 지정한 키에 대해 락을 획득합니다.
     *
     * @param key     락 키
     * @param timeout 락 유효 시간
     * @param unit    시간 단위
     * @return 락 획득 성공 시 true, 실패 시 false
     */
    public boolean acquireLock(String key, long timeout, TimeUnit unit) {
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, "lock", timeout, unit);
        return acquired != null && acquired;
    }

    /**
     * 지정한 키의 락을 해제합니다.
     */
    public void releaseLock(String key) {
        redisTemplate.delete(key);
    }
}
