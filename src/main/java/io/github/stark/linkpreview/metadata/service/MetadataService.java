package io.github.stark.linkpreview.metadata.service;

import io.github.stark.linkpreview.extractor.jsoup.JsoupMetadataExtractor;
import io.github.stark.linkpreview.metadata.domain.Metadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MetadataService {

    private final JsoupMetadataExtractor jsoupExtractor;
    private final MetadataCache metadataCache;

    public Metadata extractMetadata(String url) {
        return metadataCache.getOrCompute(url, this::executeExtraction);
    }

    private Metadata executeExtraction(String url) {
        try {
            Metadata metadata = jsoupExtractor.extract(url);
            if (metadata.isValid()) {
                return metadata;
            }
            log.debug("Failed to extract valid metadata for URL: {}", url);
            return Metadata.empty(url);
        } catch (Exception e) {
            log.warn("Failed to extract metadata for URL: {}", url, e);
            return Metadata.empty(url);
        }
    }

}