package ir.ac.ut.ece.ie.http;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class HttpResponse {
    private final int statusCode;
    private final String reasonPhrase;
    private final Map<String, String> headers;
    private final byte[] body;

    private HttpResponse(int statusCode, String reasonPhrase, Map<String, String> headers, byte[] body) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.headers = headers;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public static HttpResponse html(int statusCode, String reasonPhrase, String htmlUtf8) {
        byte[] bytes = htmlUtf8.getBytes(StandardCharsets.UTF_8);
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "text/html; charset=utf-8");
        headers.put("Content-Length", String.valueOf(bytes.length));
        headers.put("Connection", "close");
        return new HttpResponse(statusCode, reasonPhrase, headers, bytes);
    }

    public static HttpResponse redirect303(String location) {
        byte[] bytes = new byte[0];
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Location", location);
        headers.put("Content-Length", "0");
        headers.put("Connection", "close");
        return new HttpResponse(303, "See Other", headers, bytes);
    }

    public static HttpResponse notFoundHtml(String htmlUtf8) {
        return html(404, "Not Found", htmlUtf8);
    }

    public static HttpResponse badRequestHtml(String htmlUtf8) {
        return html(400, "Bad Request", htmlUtf8);
    }

    public static HttpResponse methodNotAllowedHtml(String htmlUtf8) {
        return html(405, "Method Not Allowed", htmlUtf8);
    }

    public static HttpResponse internalServerErrorHtml(String htmlUtf8) {
        return html(500, "Internal Server Error", htmlUtf8);
    }
}
