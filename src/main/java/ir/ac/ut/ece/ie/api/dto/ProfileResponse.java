package ir.ac.ut.ece.ie.api.dto;

import java.util.List;

public record ProfileResponse(
        String username,
        String name,
        String email,
        String phone,
        List<ArticleDto> articles
) {
}
