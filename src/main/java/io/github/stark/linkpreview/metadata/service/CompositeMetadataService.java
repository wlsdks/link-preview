package io.github.stark.linkpreview.metadata.service;

import io.github.stark.linkpreview.extractor.jsoup.JsoupMetadataExtractor;
import io.github.stark.linkpreview.extractor.core.MetadataExtractor;
import io.github.stark.linkpreview.metadata.domain.Metadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CompositeMetadataService {

    // 여러 추출기들을 주입받습니다
    private final JsoupMetadataExtractor jsoupExtractor;
    private final MetadataCache metadataCache;

    // 추출기들의 우선순위를 설정합니다
    private List<MetadataExtractor> getExtractors() {
        return Arrays.asList(
                jsoupExtractor  // 실패하면 일반 HTML 파싱
        );
    }

    public Metadata extractMetadata(String url) {
        return metadataCache.getOrCompute(url, this::executeExtraction);
    }

    private Metadata executeExtraction(String url) {
        for (MetadataExtractor extractor : getExtractors()) {
            try {
                Metadata metadata = extractor.extract(url);
                if (metadata.isValid()) {
                    return metadata;
                }
                log.debug("Extractor {} produced invalid metadata for URL {}",
                        extractor.getClass().getSimpleName(), url);
            } catch (Exception e) {
                log.warn("Extractor {} failed for URL {}: {}",
                        extractor.getClass().getSimpleName(), url, e.getMessage());
            }
        }
        // 모든 추출 시도가 실패하면 빈 메타데이터 반환
        return Metadata.empty(url);
    }

}