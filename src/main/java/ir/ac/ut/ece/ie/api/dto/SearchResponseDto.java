package ir.ac.ut.ece.ie.api.dto;

import ir.ac.ut.ece.ie.app.SearchResult;

import java.util.List;

public record SearchResponseDto(
        List<ArticleDto> titleMatches,
        List<ArticleDto> abstractOnlyMatches
) {
    public static SearchResponseDto from(SearchResult result) {
        if (result == null) {
            return new SearchResponseDto(List.of(), List.of());
        }

        List<ArticleDto> titleMatches = result.getTitleMatches().stream()
                .map(ArticleDto::from)
                .toList();

        List<ArticleDto> abstractOnlyMatches = result.getAbstractOnlyMatches().stream()
                .map(ArticleDto::from)
                .toList();

        return new SearchResponseDto(titleMatches, abstractOnlyMatches);
    }
}
