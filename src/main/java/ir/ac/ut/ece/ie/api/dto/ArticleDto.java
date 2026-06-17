package ir.ac.ut.ece.ie.api.dto;

import ir.ac.ut.ece.ie.app.Article;

import java.time.Instant;

public record ArticleDto(
        String title,
        String abstractText,
        String body,
        Instant createdAt,
        String authorName,
        String categoryName
) {
    public static ArticleDto from(Article article) {
        if (article == null) return null;
        String authorName = article.getAuthor() != null ? article.getAuthor().getName() : null;
        String categoryName = article.getCategory() != null ? article.getCategory().getName() : null;
        return new ArticleDto(
                article.getTitle(),
                article.getAbstractText(),
                article.getBody(),
                article.getCreatedAt(),
                authorName,
                categoryName
        );
    }
}
