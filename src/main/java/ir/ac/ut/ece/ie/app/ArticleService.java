package ir.ac.ut.ece.ie.app;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    public ArticleService(ArticleRepository articleRepository,
                          AuthorRepository authorRepository,
                          CategoryRepository categoryRepository) {
        this.articleRepository = articleRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public void addArticle(String title, String abstractText, String body, String authorName, String categoryName) {
        String normalizedTitle = title == null ? "" : title.trim();
        String normalizedAbstract = abstractText == null ? "" : abstractText.trim();
        String normalizedBody = body == null ? "" : body.trim();
        String normAuthor = authorName == null || authorName.trim().isEmpty() ? "Unknown Author" : authorName.trim();
        String normCategory = categoryName == null || categoryName.trim().isEmpty() ? "General" : categoryName.trim();

        if (normalizedTitle.isEmpty()) {
            throw new IllegalArgumentException("Title is required.");
        }

        if (articleRepository.findByTitle(normalizedTitle).isPresent()) {
            throw new DuplicateTitleException("Article with this title already exists.");
        }

        Author author = authorRepository.findByName(normAuthor)
                .orElseGet(() -> authorRepository.save(new Author(normAuthor)));

        Category category = categoryRepository.findByName(normCategory)
                .orElseGet(() -> categoryRepository.save(new Category(normCategory)));

        Article article = new Article(normalizedTitle, normalizedAbstract, normalizedBody, Instant.now(), author, category);
        articleRepository.save(article);
    }

    @Transactional(readOnly = true)
    public Article getByTitle(String title) {
        if (title == null) return null;
        return articleRepository.findByTitle(title).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Article> listNewestFirst() {
        return articleRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public SearchResult search(String phrase) {
        String q = phrase == null ? "" : phrase.trim();
        if (q.isEmpty()) {
            return new SearchResult(listNewestFirst(), new ArrayList<>());
        }

        List<Article> titleMatches = articleRepository.findByTitleContainingIgnoreCase(q);
        List<Article> abstractMatches = articleRepository.findByAbstractTextContainingIgnoreCase(q);

        // Remove duplicates if an article matches both title and abstract
        List<Article> abstractOnlyMatches = new ArrayList<>();
        for (Article a : abstractMatches) {
            boolean inTitleMatches = titleMatches.stream().anyMatch(t -> t.getId().equals(a.getId()));
            if (!inTitleMatches) {
                abstractOnlyMatches.add(a);
            }
        }

        return new SearchResult(titleMatches, abstractOnlyMatches);
    }
}