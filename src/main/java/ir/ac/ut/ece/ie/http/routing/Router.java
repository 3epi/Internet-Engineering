package ir.ac.ut.ece.ie.http.routing;

import ir.ac.ut.ece.ie.http.HttpMethod;
import ir.ac.ut.ece.ie.http.HttpRequest;
import ir.ac.ut.ece.ie.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public final class Router {
    private final Map<String, Handler> routes = new HashMap<>();

    public Router add(HttpMethod method, String path, Handler handler) {
        routes.put(key(method, path), handler);
        return this;
    }

    public HttpResponse route(HttpRequest request) {
        Handler handler = routes.get(key(request.getMethod(), request.getPath()));
        if (handler == null) {
            return HttpResponse.notFoundHtml("<html><head><meta charset=\"utf-8\"><title>Not Found</title></head>" +
                    "<body><h1>404 Not Found</h1></body></html>");
        }
        return handler.handle(request);
    }

    private static String key(HttpMethod method, String path) {
        return method.name() + " " + path;
    }
}
