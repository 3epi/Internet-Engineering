package ir.ac.ut.ece.ie.api.dto;

import ir.ac.ut.ece.ie.app.Article;

import java.time.Instant;

public record ArticleDto(
        String title,
        String abstractText,
        String body,
        Instant createdAt
) {
    public static ArticleDto from(Article article) {
        if (article == null) return null;
        return new ArticleDto(
                article.getTitle(),
                article.getAbstractText(),
                article.getBody(),
                article.getCreatedAt()
        );
    }
}
