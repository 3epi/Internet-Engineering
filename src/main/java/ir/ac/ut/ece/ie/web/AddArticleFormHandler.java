package ir.ac.ut.ece.ie.web;

import ir.ac.ut.ece.ie.http.HttpRequest;
import ir.ac.ut.ece.ie.http.HttpResponse;
import ir.ac.ut.ece.ie.http.routing.Handler;
import ir.ac.ut.ece.ie.web.html.Pages;

public final class AddArticleFormHandler implements Handler {
    @Override
    public HttpResponse handle(HttpRequest request) {
        return HttpResponse.html(200, "OK", Pages.addFormPage("", "", "", null));
    }
}
