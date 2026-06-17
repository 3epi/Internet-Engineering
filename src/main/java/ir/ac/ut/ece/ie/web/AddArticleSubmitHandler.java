package ir.ac.ut.ece.ie.web;

import ir.ac.ut.ece.ie.app.ArticleService;
import ir.ac.ut.ece.ie.app.DuplicateTitleException;
import ir.ac.ut.ece.ie.http.HttpRequest;
import ir.ac.ut.ece.ie.http.HttpRequestParser;
import ir.ac.ut.ece.ie.http.HttpResponse;
import ir.ac.ut.ece.ie.http.routing.Handler;
import ir.ac.ut.ece.ie.web.html.Html;
import ir.ac.ut.ece.ie.web.html.Pages;

import java.util.Map;

public final class AddArticleSubmitHandler implements Handler {
    private final ArticleService service;

    public AddArticleSubmitHandler(ArticleService service) {
        this.service = service;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        String contentType = request.getHeader("content-type");
        if (contentType == null || !contentType.toLowerCase().startsWith("application/x-www-form-urlencoded")) {
            return HttpResponse.badRequestHtml(Pages.layout("Bad Request", "<h1>400 Bad Request</h1><p>Expected form submission.</p>"));
        }

        Map<String, String> form = HttpRequestParser.parseUrlEncodedParams(request.getBodyAsUtf8());
        String title = form.get("title");
        String abstractText = form.get("abstract");
        String body = form.get("body");

        try {
            service.addArticle(title, abstractText, body, "Unknown Author", "General");
        } catch (IllegalArgumentException ex) {
            return HttpResponse.html(400, "Bad Request", Pages.addFormPage(nvl(title), nvl(abstractText), nvl(body), ex.getMessage()));
        } catch (DuplicateTitleException ex) {
            return HttpResponse.html(400, "Bad Request", Pages.addFormPage(nvl(title), nvl(abstractText), nvl(body), ex.getMessage()));
        }

        String location = "/article?title=" + Html.urlEncode(title == null ? "" : title.trim());
        return HttpResponse.redirect303(location);
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }
}
