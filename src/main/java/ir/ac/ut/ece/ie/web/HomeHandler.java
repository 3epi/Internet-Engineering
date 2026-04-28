package ir.ac.ut.ece.ie.web;

import ir.ac.ut.ece.ie.app.ArticleService;
import ir.ac.ut.ece.ie.app.SearchResult;
import ir.ac.ut.ece.ie.http.HttpRequest;
import ir.ac.ut.ece.ie.http.HttpResponse;
import ir.ac.ut.ece.ie.http.routing.Handler;
import ir.ac.ut.ece.ie.web.html.Pages;

public final class HomeHandler implements Handler {
    private final ArticleService service;

    public HomeHandler(ArticleService service) {
        this.service = service;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        String q = request.getQueryParam("q");
        SearchResult result = service.search(q);
        String html = Pages.homePage(q, result.getTitleMatches(), result.getAbstractOnlyMatches());
        return HttpResponse.html(200, "OK", html);
    }
}
