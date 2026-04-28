package ir.ac.ut.ece.ie.app;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class InMemoryArticleRepository implements ArticleRepository {
    private final Map<String, Article> articlesByTitle = new LinkedHashMap<>();

    @Override
    public synchronized void add(Article article) throws DuplicateTitleException {
        if (articlesByTitle.containsKey(article.getTitle())) {
            throw new DuplicateTitleException("Article title must be unique.");
        }
        articlesByTitle.put(article.getTitle(), article);
    }

    @Override
    public synchronized Article findByTitle(String title) {
        return articlesByTitle.get(title);
    }

    @Override
    public synchronized List<Article> listAll() {
        return new ArrayList<>(articlesByTitle.values());
    }
}
