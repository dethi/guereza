package com.epita.guereza;

public interface ICrawler {
    void crawl(final String url);

    String[] extractLinkUrl();

    String requestTextHtml();
}
