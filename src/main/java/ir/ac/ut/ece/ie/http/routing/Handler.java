package ir.ac.ut.ece.ie.http.routing;

import ir.ac.ut.ece.ie.http.HttpRequest;
import ir.ac.ut.ece.ie.http.HttpResponse;

public interface Handler {
    HttpResponse handle(HttpRequest request);
}
