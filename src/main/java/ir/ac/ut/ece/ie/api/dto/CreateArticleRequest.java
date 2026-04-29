package ir.ac.ut.ece.ie.api.dto;

public record CreateArticleRequest(
        String title,
        String abstractText,
        String body
) {
}
