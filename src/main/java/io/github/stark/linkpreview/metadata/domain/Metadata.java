package io.github.stark.linkpreview.metadata.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Metadata {

    private String url;
    private String title;
    private String description;
    private String image;
    private String siteName;
    private String favicon;
    private LocalDateTime extractedAt;

    // 메타데이터가 유효한지 검증하는 메서드
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