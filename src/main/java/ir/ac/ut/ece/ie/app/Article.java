package ir.ac.ut.ece.ie.app;

import java.time.Instant;

public final class Article {
    private final String title;
    private final String abstractText;
    private final String body;
    private final Instant createdAt;

    public Article(String title, String abstractText, String body, Instant createdAt) {
        this.title = title;
        this.abstractText = abstractText;
        this.body = body;
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public String getBody() {
        return body;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
