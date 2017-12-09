package com.epita.domain;


public interface Crawler {
    /**
     * Crawl an url
     *
     * @param url The url to crawl
     * @return The body of the crawled url
     */
    RawDocument crawl(final String url);

    /**
     * Extract all urls in a document
     *
     * @param doc The document to parse
     * @return All urls found
     */
    String[] extractUrl(RawDocument doc);

    /**
     * Extract the body content as test from an url
     *
     * @param doc The body to parse
     * @return The body content
     */
    String extractText(RawDocument doc);
}
