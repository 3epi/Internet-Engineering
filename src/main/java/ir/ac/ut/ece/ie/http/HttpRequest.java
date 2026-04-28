package ir.ac.ut.ece.ie.http;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class HttpRequest {
    private final HttpMethod method;
    private final String target;
    private final String path;
    private final Map<String, String> query;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpRequest(
            HttpMethod method,
            String target,
            String path,
            Map<String, String> query,
            Map<String, String> headers,
            byte[] body
    ) {
        this.method = method;
        this.target = target;
        this.path = path;
        this.query = Collections.unmodifiableMap(new LinkedHashMap<>(query));
        this.headers = Collections.unmodifiableMap(new LinkedHashMap<>(headers));
        this.body = body == null ? new byte[0] : body.clone();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getTarget() {
        return target;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQuery() {
        return query;
    }

    public String getQueryParam(String name) {
        return query.get(name);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String name) {
        if (name == null) return null;
        return headers.get(name.toLowerCase());
    }

    public byte[] getBody() {
        return body.clone();
    }

    public String getBodyAsUtf8() {
        return new String(body, StandardCharsets.UTF_8);
    }
}
