package com.vampon.manager;

import com.vampon.exception.BusinessException;
import com.vampon.vamapicommon.common.ErrorCode;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 专门提供 RedisLimiter 限流基础服务的（提供了通用的能力）
 */
@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流操作
     *
     * @param key 区分不同的限流器，比如不同的用户 id 应该分别统计
     */
    public void doRateLimit(String key) {
        // 创建一个名称为user_limiter的限流器，每秒最多访问 2 次
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        /**
         * trySetRate() 方法用于设置速率限制器的速率限制参数。
         * RateType.OVERALL 表示设置速率限制器的整体速率限制，即整个速率限制器受到限制。
         * 1 是限制的速率值，表示每秒最多允许通过 1 个请求。
         * 1 是令牌发放的初始数量，表示在开始时速率限制器中有 1 个令牌可用。
         * RateIntervalUnit.SECONDS 表示速率的时间单位，这里是以秒为单位。
         */
        rateLimiter.trySetRate(RateType.OVERALL, 1, 1, RateIntervalUnit.SECONDS);
        // 每当一个操作来了后，请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(1);
        if (!canOp) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }
}
