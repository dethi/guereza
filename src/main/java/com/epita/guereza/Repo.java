package com.epita.guereza;

public interface Repo {
    /**
     * Add urls to the repo
     *
     * @param urls The urls to add to the repo
     */
    void store(String[] urls);

    /**
     * Return the next url to crawl
     *
     * @return The Url
     */
    String nextUrl();
}
