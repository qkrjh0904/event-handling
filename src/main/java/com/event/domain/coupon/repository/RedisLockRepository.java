package com.event.domain.coupon.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisLockRepository {

    private final RedisTemplate<String, String> redisLockTemplate;

    public Boolean lock(Long key) {
        return redisLockTemplate
                .opsForValue()
                .setIfAbsent(key.toString(), "lock");
    }

    public Boolean unlock(Long key) {
        return redisLockTemplate.delete(key.toString());
    }

}
