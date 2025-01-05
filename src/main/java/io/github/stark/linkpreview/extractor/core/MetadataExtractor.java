package io.github.stark.linkpreview.extractor.core;

import io.github.stark.linkpreview.common.exception.MetadataExtractionException;
import io.github.stark.linkpreview.metadata.domain.Metadata;

public interface MetadataExtractor {

    Metadata extract(String url) throws MetadataExtractionException;

}