package io.github.stark.linkpreview.metadata.controller;

import io.github.stark.linkpreview.metadata.domain.Metadata;
import io.github.stark.linkpreview.metadata.service.MetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/v1/metadata")
@RequiredArgsConstructor
@RestController
public class MetadataController {

    private final MetadataService metadataService;

    @GetMapping("/preview")
    public ResponseEntity<Metadata> getPreview(@RequestParam String url) {
        try {
            Metadata metadata = metadataService.extractMetadata(url);
            return ResponseEntity.ok(metadata);
        } catch (Exception e) {
            log.error("Failed to extract metadata", e);
            return ResponseEntity.badRequest().build();
        }
    }

}