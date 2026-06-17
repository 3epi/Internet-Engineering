package ir.ac.ut.ece.ie.app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findByTitle(String title);

    List<Article> findAllByOrderByCreatedAtDesc();

    List<Article> findByTitleContainingIgnoreCase(String title);
    List<Article> findByAbstractTextContainingIgnoreCase(String abstractText);
}