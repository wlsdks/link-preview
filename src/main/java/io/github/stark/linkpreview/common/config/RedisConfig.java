package io.github.stark.linkpreview.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.stark.linkpreview.metadata.domain.Metadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;

    /**
     * @return RedisConnectionFactory
     * @apiNote Redis 연결 Factory 설정
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(host);
        redisConfiguration.setPort(port);
        redisConfiguration.setPassword(password);

        return new LettuceConnectionFactory(redisConfiguration);
    }

    /**
     * @param connectionFactory - Redis 연결 Factory
     * @return RedisTemplate
     * @apiNote RedisTemplate 설정
     */
    @Bean
    public RedisTemplate<String, Metadata> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Metadata> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key Serializer
        template.setKeySerializer(new StringRedisSerializer());

        // Value Serializer를 위한 ObjectMapper 설정
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Java 8 date/time 지원
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // ISO-8601 형식 사용

        // Value Serializer using GenericJackson2JsonRedisSerializer
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(serializer);

        // Template 초기화
        template.afterPropertiesSet();
        return template;
    }

}
