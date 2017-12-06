package com.epita.guereza;

import org.jsoup.nodes.Document;

public interface ICrawler {
    Document crawl(final String url);

    String[] extractUrl(Document doc);

    String extractText(Document doc);
}
