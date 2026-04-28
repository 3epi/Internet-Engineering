package ir.ac.ut.ece.ie.app;

import java.util.List;

public interface ArticleRepository {
    void add(Article article) throws DuplicateTitleException;

    Article findByTitle(String title);

    List<Article> listAll();
}
