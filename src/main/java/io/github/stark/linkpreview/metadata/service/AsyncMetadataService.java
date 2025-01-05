package io.github.stark.linkpreview.metadata.service;

import io.github.stark.linkpreview.metadata.domain.Metadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class AsyncMetadataService {

    private final CompositeMetadataService metadataService;

    @Async
    public CompletableFuture<Metadata> extractMetadataAsync(String url) {
        return CompletableFuture.completedFuture(
                metadataService.extractMetadata(url));
    }

}