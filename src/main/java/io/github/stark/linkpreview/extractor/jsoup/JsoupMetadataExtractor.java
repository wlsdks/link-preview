package io.github.stark.linkpreview.extractor.jsoup;

import io.github.stark.linkpreview.common.exception.MetadataExtractionException;
import io.github.stark.linkpreview.extractor.core.MetadataExtractor;
import io.github.stark.linkpreview.metadata.domain.Metadata;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;

@Slf4j
@Component
public class JsoupMetadataExtractor implements MetadataExtractor {

    @Override
    public Metadata extract(String url) throws MetadataExtractionException {
        try {
            // Jsoup으로 문서 가져오기
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            // OpenGraph 태그에서 메타데이터 추출 시도
            String title = getMetaTagContent(doc, "og:title");
            if (title.isEmpty()) {
                title = doc.title(); // 폴백: 일반 title 태그 사용
            }

            String description = getMetaTagContent(doc, "og:description");
            if (description.isEmpty()) {
                description = getMetaTagContent(doc, "description"); // 폴백: 일반 description 메타 태그
            }

            String image = getMetaTagContent(doc, "og:image");
            String siteName = getMetaTagContent(doc, "og:site_name");
            String favicon = getFavicon(doc, url);

            return Metadata.builder()
                    .url(url)
                    .title(title)
                    .description(description)
                    .image(image)
                    .siteName(siteName)
                    .favicon(favicon)
                    .extractedAt(LocalDateTime.now())
                    .build();

        } catch (IOException e) {
            log.error("Failed to extract metadata from URL: {}", url, e);
            throw new MetadataExtractionException("Failed to extract metadata", e);
        }
    }

    private String getMetaTagContent(Document doc, String property) {
        Element element = doc.select(String.format("meta[property=%s], meta[name=%s]", property, property))
                .first();
        return element != null ? element.attr("content").trim() : "";
    }

    private String getFavicon(Document doc, String url) {
        // 파비콘 찾기 시도
        Element favicon = doc.select("link[rel~=icon]").first();
        if (favicon != null) {
            String faviconUrl = favicon.attr("href");
            return resolveUrl(url, faviconUrl);
        }
        // 기본 파비콘 위치 시도
        return resolveUrl(url, "/favicon.ico");
    }

    private String resolveUrl(String base, String path) {
        try {
            URL baseUrl = new URL(base);
            URL resolvedUrl = new URL(baseUrl, path);
            return resolvedUrl.toString();
        } catch (MalformedURLException e) {
            log.warn("Failed to resolve URL: {} with path: {}", base, path);
            return path;
        }
    }

}