package io.github.stark.linkpreview.common.exception;

public class MetadataExtractionException extends RuntimeException {

    public MetadataExtractionException(String message) {
        super(message);
    }

    public MetadataExtractionException(String message, Throwable cause) {
        super(message, cause);
    }

}