package ir.ac.ut.ece.ie.app;

import java.util.List;

public final class SearchResult {
    private final List<Article> titleMatches;
    private final List<Article> abstractOnlyMatches;

    public SearchResult(List<Article> titleMatches, List<Article> abstractOnlyMatches) {
        this.titleMatches = titleMatches;
        this.abstractOnlyMatches = abstractOnlyMatches;
    }

    public List<Article> getTitleMatches() {
        return titleMatches;
    }

    public List<Article> getAbstractOnlyMatches() {
        return abstractOnlyMatches;
    }
}
