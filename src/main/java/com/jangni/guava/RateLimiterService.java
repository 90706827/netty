package com.jangni.guava;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Component;

/**
 * Author ZhangGuoQiang
 * Date: 2018/6/28/028
 * Time: 16:20
 * Description: 令牌模式 限流
 */
@Component
public class RateLimiterService {

    /**
     * 每秒产生100个令牌，相当于每秒TPS
     */
    RateLimiter rateLimiter = RateLimiter.create(100.0);

    /**
     * 获取令牌 根据是否获取到令牌来限制请求是否被允许
     * @return 获取到令牌 返回true 否则返回false
     */
    public boolean tryAcquire() {
        return rateLimiter.tryAcquire();
    }
}
