package ir.ac.ut.ece.ie.app;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ArticleService {
    private final ArticleRepository repository;

    public ArticleService(ArticleRepository repository) {
        this.repository = repository;
    }

    public void addArticle(String title, String abstractText, String body) {
        String normalizedTitle = title == null ? "" : title.trim();
        String normalizedAbstract = abstractText == null ? "" : abstractText.trim();
        String normalizedBody = body == null ? "" : body.trim();

        if (normalizedTitle.isEmpty()) {
            throw new IllegalArgumentException("Title is required.");
        }

        Article article = new Article(normalizedTitle, normalizedAbstract, normalizedBody, Instant.now());
        repository.add(article);
    }

    public Article getByTitle(String title) {
        if (title == null) return null;
        return repository.findByTitle(title);
    }

    public List<Article> listNewestFirst() {
        List<Article> all = repository.listAll();
        all.sort(Comparator.comparing(Article::getCreatedAt).reversed());
        return all;
    }

    public SearchResult search(String phrase) {
        String q = phrase == null ? "" : phrase.trim();
        if (q.isEmpty()) {
            return new SearchResult(listNewestFirst(), new ArrayList<Article>());
        }

        String qLower = q.toLowerCase();

        List<Article> titleMatches = new ArrayList<>();
        List<Article> abstractOnlyMatches = new ArrayList<>();

        for (Article a : listNewestFirst()) {
            String titleLower = a.getTitle() == null ? "" : a.getTitle().toLowerCase();
            String abstractLower = a.getAbstractText() == null ? "" : a.getAbstractText().toLowerCase();

            boolean inTitle = titleLower.contains(qLower);
            boolean inAbstract = abstractLower.contains(qLower);

            if (inTitle) {
                titleMatches.add(a);
            } else if (inAbstract) {
                abstractOnlyMatches.add(a);
            }
        }

        return new SearchResult(titleMatches, abstractOnlyMatches);
    }
}
