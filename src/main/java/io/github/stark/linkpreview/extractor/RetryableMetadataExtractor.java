package io.github.stark.linkpreview.extractor;

import io.github.stark.linkpreview.common.exception.MetadataExtractionException;
import io.github.stark.linkpreview.extractor.core.MetadataExtractor;
import io.github.stark.linkpreview.metadata.domain.Metadata;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RetryableMetadataExtractor implements MetadataExtractor {

    private final MetadataExtractor delegate;
    private final int maxRetries;
    private final long retryDelay;

    public RetryableMetadataExtractor(
            @Qualifier("jsoupMetadataExtractor") MetadataExtractor delegate,
            @Value("${metadata.extractor.max-retries:3}") int maxRetries,
            @Value("${metadata.extractor.retry-delay:1000}") long retryDelay) {
        this.delegate = delegate;
        this.maxRetries = maxRetries;
        this.retryDelay = retryDelay;
    }

    @Override
    public Metadata extract(String url) {
        int attempts = 0;
        while (attempts < maxRetries) {
            try {
                return delegate.extract(url);
            } catch (Exception e) {
                attempts++;
                if (attempts == maxRetries) {
                    throw e;
                }
                try {
                    Thread.sleep(retryDelay * attempts);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new MetadataExtractionException("Retry interrupted", ie);
                }
            }
        }
        throw new MetadataExtractionException("Failed after " + maxRetries + " attempts");
    }

}