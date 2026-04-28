package ir.ac.ut.ece.ie.web.html;

import ir.ac.ut.ece.ie.app.Article;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class Pages {
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    private Pages() {
    }

    public static String layout(String title, String bodyHtml) {
        return "<html><head><meta charset=\"utf-8\">" +
                "<title>" + Html.escape(title) + "</title>" +
                "</head><body>" +
                bodyHtml +
                "</body></html>";
    }

    public static String homePage(String query, List<Article> titleMatches, List<Article> abstractOnlyMatches) {
        String q = query == null ? "" : query;

        StringBuilder sb = new StringBuilder();
        sb.append("<h1>Articles</h1>");
        sb.append("<p><a href=\"/add\">Add article</a></p>");

        sb.append("<form method=\"get\" action=\"/\">");
        sb.append("<label>Search: <input name=\"q\" value=\"").append(Html.escape(q)).append("\"></label>");
        sb.append("<button type=\"submit\">Search</button>");
        sb.append("</form>");

        if (q.trim().isEmpty()) {
            sb.append("<h2>Newest</h2>");
            sb.append(renderArticleList(titleMatches));
        } else {
            sb.append("<h2>Results for: ").append(Html.escape(q)).append("</h2>");

            sb.append("<h3>Matches in title</h3>");
            sb.append(renderArticleList(titleMatches));

            sb.append("<h3>Matches in abstract</h3>");
            sb.append(renderArticleList(abstractOnlyMatches));
        }

        return layout("Home", sb.toString());
    }

    public static String articlePage(Article article) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p><a href=\"/\">Back</a></p>");
        sb.append("<h1>").append(Html.escape(article.getTitle())).append("</h1>");
        sb.append("<p><em>Published at: ").append(Html.escape(TIME_FMT.format(article.getCreatedAt()))).append("</em></p>");

        sb.append("<h2>Abstract</h2>");
        sb.append("<p>").append(Html.escape(article.getAbstractText())).append("</p>");

        sb.append("<h2>Body</h2>");
        sb.append("<pre>").append(Html.escape(article.getBody())).append("</pre>");

        return layout(article.getTitle(), sb.toString());
    }

    public static String addFormPage(String titleValue, String abstractValue, String bodyValue, String errorMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p><a href=\"/\">Back</a></p>");
        sb.append("<h1>Add Article</h1>");

        if (errorMessage != null && !errorMessage.trim().isEmpty()) {
            sb.append("<p>").append(Html.escape(errorMessage)).append("</p>");
        }

        sb.append("<form method=\"post\" action=\"/add\">");

        sb.append("<p><label>Title<br><input name=\"title\" value=\"")
                .append(Html.escape(titleValue))
            .append("\"></label></p>");

        sb.append("<p><label>Abstract<br><textarea name=\"abstract\" rows=\"4\" cols=\"60\">")
                .append(Html.escape(abstractValue))
                .append("</textarea></label></p>");

        sb.append("<p><label>Body<br><textarea name=\"body\" rows=\"10\" cols=\"60\">")
                .append(Html.escape(bodyValue))
                .append("</textarea></label></p>");

        sb.append("<p><button type=\"submit\">Add</button></p>");
        sb.append("</form>");

        return layout("Add Article", sb.toString());
    }

    public static String notFoundPage(String message) {
        return layout("Not Found", "<h1>404 Not Found</h1><p>" + Html.escape(message) + "</p><p><a href=\"/\">Home</a></p>");
    }

    private static String renderArticleList(List<Article> articles) {
        if (articles == null || articles.isEmpty()) {
            return "<p><em>No articles.</em></p>";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<ul>");
        for (Article a : articles) {
            String title = a.getTitle();
            sb.append("<li>");
            sb.append("<a href=\"/article?title=").append(Html.urlEncode(title)).append("\">")
                    .append(Html.escape(title))
                    .append("</a>");
            sb.append(" <small>(").append(Html.escape(TIME_FMT.format(a.getCreatedAt()))).append(")</small>");
            sb.append("</li>");
        }
        sb.append("</ul>");
        return sb.toString();
    }
}
