package com.epita.guereza;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class Indexer {
    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    public Document index(String url) {
        logger.info("logging {}", url);
        Crawler c = new Crawler();
        RawDocument d = c.crawl(url);
        String text = c.extractText(d);

        String[] sentences = getSentences(text);
        Map<String, Long> tokens = Arrays.stream(sentences)
                .map(this::getWords)
                .flatMap(Function.identity())
                .collect(groupingBy(Function.identity(), counting()));

        Long totalTokens = tokens.values().stream().mapToLong(i -> i).sum();

        HashMap<String, Term> terms = new HashMap<>();
        for (Map.Entry<String, Long> entry: tokens.entrySet()) {
            terms.put(entry.getKey(), new Term(entry.getKey(), null, entry.getValue() / totalTokens));
        }
        return new Document(url, terms);
    }

    public HashMap<Document, Double> search(List<Document> docs, String query) {
        HashMap<Document, Double> hits = new HashMap<>();
        for (String q : query.split("\\s+")) {
            double idf = idf(docs, q);
            for (Document doc : docs) {
                HashMap<String, Term> terms = doc.getTerms();
                if (terms.containsKey(q)) {
                    double tfIdf = idf * terms.get(q).getFrequency();
                    double currentRanking = hits.getOrDefault(doc, 0.0);
                    hits.put(doc, currentRanking + tfIdf);
                }
            }
        }

        return hits;
    }

    private double idf(List<Document> docs, String term) {
        double n = 0;
        for (Document doc : docs) {
            if (doc.getTerms().containsKey(term)) {
                n++;
                break;
            }
        }
        return Math.log(docs.size() / (1 + n));
    }

    String[] getSentences(String text) {
        return Arrays.stream(text.split("[.!?]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }

    Stream<String> getWords(String sentence) {
        return Arrays.stream(sentence.split("\\s+"))
                .map(String::toLowerCase)
                .map(s -> Normalizer.normalize(s, Normalizer.Form.NFD))
                .map(s -> s.replaceAll("[^-\\dA-Za-z ]", ""))
                .map(this::stemmed)
                .filter(s -> !s.isEmpty())
                .filter(w -> !StopWords.match(w));
    }

    String stemmed(String word) {
        return word.replaceAll("(ing|ed|ly|ment|ency|ation|s|ent|e|ous|ator)$", "")
                .replaceAll("y$", "i")
                .replaceAll("(b|d|f|g|m|n|p|r|t){2}$", "$1")
                .replaceAll("ies$", "i");
    }

    public void publish(Document d) {

    }
}
