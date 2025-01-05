package io.github.stark.linkpreview.metadata.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor      // Jackson 역직렬화를 위한 기본 생성자
@AllArgsConstructor     // Builder 패턴을 위한 모든 필드 생성자
@Builder
@Getter
@RedisHash("metadata")  // Redis Hash로 저장됨을 명시
public class Metadata implements Serializable {

    private static final long serialVersionUID = 1L;  // 직렬화를 위한 버전 ID

    private String url;
    private String title;
    private String description;
    private String image;
    private String siteName;
    private String favicon;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime extractedAt;

    // 메타데이터가 유효한지 검증하는 메서드
    @JsonIgnore  // JSON 직렬화에서 제외
    public boolean isValid() {
        return title != null && !title.trim().isEmpty()
                && (description != null || image != null);
    }

    // 빈 메타데이터 객체 생성
    public static Metadata empty(String url) {
        return Metadata.builder()
                .url(url)
                .extractedAt(LocalDateTime.now())
                .build();
    }

}