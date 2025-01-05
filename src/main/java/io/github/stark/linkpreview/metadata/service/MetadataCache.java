package io.github.stark.linkpreview.metadata.service;

import io.github.stark.linkpreview.metadata.domain.Metadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Function;

@RequiredArgsConstructor
@Component
public class MetadataCache {

    private final RedisTemplate<String, Metadata> redisTemplate;

    @Value("${metadata.cache.duration}")
    private Duration cacheDuration;

    public Metadata getOrCompute(String url, Function<String, Metadata> supplier) {
        String cacheKey = "metadata:" + url;
        Metadata cached = redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            return cached;
        }

        Metadata metadata = supplier.apply(url);
        redisTemplate.opsForValue().set(cacheKey, metadata, cacheDuration);
        return metadata;
    }

}