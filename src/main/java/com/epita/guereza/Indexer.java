package com.epita.guereza;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class Indexer {
    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    public Document index(String url) {
        logger.info("logging {}", url);
        Crawler c = new Crawler();
        RawDocument d = c.crawl(url);
        String text = c.extractText(d);


        String[] sentences = getSentences(text);

        String[] tokens = Arrays.stream(sentences)
                .map(this::getWords)
                .flatMap(Function.identity())
                .toArray(String[]::new);

        String[][] tokensPerSentences = Arrays.stream(sentences)
                .map(this::getWords)
                .map(s -> s.toArray(String[]::new))
                .toArray(String[][]::new);

        HashSet<String> terms = new HashSet<>(Arrays.asList(tokens));

        List<Term> res = new ArrayList<>();
        for (String term : terms) {
            double val = tfIdf(tokens, tokensPerSentences, term);
            res.add(new Term(term, null, val));
        }

        res.sort((a, b) -> Double.compare(b.getFrequency(), a.getFrequency()));
        for (Term t : res) {
            System.out.printf("%s: %f\n", t.getToken(), t.getFrequency());
        }

        return null;
    }

    private double tf(String[] tokens, String term) {
        double result = 0;
        for (String word : tokens) {
            if (term.equalsIgnoreCase(word))
                result++;
        }
        return result / tokens.length;
    }

    private double idf(String[][] sentences, String term) {
        double n = 0;
        for (String[] sentence : sentences) {
            for (String word : sentence) {
                if (term.equalsIgnoreCase(word)) {
                    n++;
                    break;
                }
            }
        }
        return Math.log(sentences.length / n);
    }

    private double tfIdf(String[] tokens, String[][] tokensPerSentences, String term) {
        return tf(tokens, term) * idf(tokensPerSentences, term);
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
