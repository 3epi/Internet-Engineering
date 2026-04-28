package ir.ac.ut.ece.ie.web;

import ir.ac.ut.ece.ie.app.Article;
import ir.ac.ut.ece.ie.app.ArticleService;
import ir.ac.ut.ece.ie.http.HttpRequest;
import ir.ac.ut.ece.ie.http.HttpResponse;
import ir.ac.ut.ece.ie.http.routing.Handler;
import ir.ac.ut.ece.ie.web.html.Pages;

public final class ArticleHandler implements Handler {
    private final ArticleService service;

    public ArticleHandler(ArticleService service) {
        this.service = service;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        String title = request.getQueryParam("title");
        if (title == null || title.trim().isEmpty()) {
            return HttpResponse.badRequestHtml(Pages.layout("Bad Request", "<h1>400 Bad Request</h1><p>Missing title.</p>"));
        }

        Article article = service.getByTitle(title);
        if (article == null) {
            return HttpResponse.notFoundHtml(Pages.notFoundPage("Article not found."));
        }

        return HttpResponse.html(200, "OK", Pages.articlePage(article));
    }
}
