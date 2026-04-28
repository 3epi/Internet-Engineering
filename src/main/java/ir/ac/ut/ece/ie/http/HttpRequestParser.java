package ir.ac.ut.ece.ie.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class HttpRequestParser {

    public HttpRequest parse(InputStream inputStream) throws IOException {
        String requestLine = readLineAscii(inputStream);
        if (requestLine == null || requestLine.isEmpty()) {
            return null;
        }

        String[] parts = requestLine.split(" ");
        if (parts.length < 3) {
            throw new IOException("Invalid request line: " + requestLine);
        }

        HttpMethod method;
        try {
            method = HttpMethod.valueOf(parts[0]);
        } catch (IllegalArgumentException ex) {
            method = null;
        }

        String target = parts[1];

        Map<String, String> headers = readHeaders(inputStream);
        byte[] body = readBodyIfAny(inputStream, headers);

        String path = target;
        String queryString = "";
        int qIndex = target.indexOf('?');
        if (qIndex >= 0) {
            path = target.substring(0, qIndex);
            queryString = target.substring(qIndex + 1);
        }

        Map<String, String> query = parseUrlEncodedParams(queryString);
        return new HttpRequest(method, target, path, query, headers, body);
    }

    public static Map<String, String> parseUrlEncodedParams(String s) {
        Map<String, String> result = new LinkedHashMap<>();
        if (s == null || s.isEmpty()) {
            return result;
        }

        String[] pairs = s.split("&");
        for (String pair : pairs) {
            if (pair.isEmpty()) continue;

            int eq = pair.indexOf('=');
            String rawKey = eq >= 0 ? pair.substring(0, eq) : pair;
            String rawValue = eq >= 0 ? pair.substring(eq + 1) : "";

            String key = urlDecode(rawKey);
            String value = urlDecode(rawValue);

            if (!key.isEmpty()) {
                result.put(key, value);
            }
        }
        return result;
    }

    private Map<String, String> readHeaders(InputStream inputStream) throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();
        while (true) {
            String line = readLineAscii(inputStream);
            if (line == null) {
                break;
            }
            if (line.isEmpty()) {
                break;
            }

            int colon = line.indexOf(':');
            if (colon <= 0) {
                continue;
            }

            String name = line.substring(0, colon).trim().toLowerCase(Locale.ROOT);
            String value = line.substring(colon + 1).trim();
            headers.put(name, value);
        }
        return headers;
    }

    private byte[] readBodyIfAny(InputStream inputStream, Map<String, String> headers) throws IOException {
        String contentLengthValue = headers.get("content-length");
        if (contentLengthValue == null) {
            return new byte[0];
        }

        int length;
        try {
            length = Integer.parseInt(contentLengthValue);
        } catch (NumberFormatException e) {
            throw new IOException("Invalid Content-Length: " + contentLengthValue);
        }

        if (length <= 0) {
            return new byte[0];
        }

        byte[] body = new byte[length];
        int offset = 0;
        while (offset < length) {
            int read = inputStream.read(body, offset, length - offset);
            if (read == -1) {
                throw new IOException("Unexpected end of stream while reading request body");
            }
            offset += read;
        }
        return body;
    }

    private static String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, StandardCharsets.UTF_8.name());
        } catch (Exception ex) {
            return s;
        }
    }

    private static String readLineAscii(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int b;
        boolean sawCR = false;
        while ((b = inputStream.read()) != -1) {
            if (b == '\r') {
                sawCR = true;
                continue;
            }
            if (b == '\n') {
                break;
            }
            if (sawCR) {
                break;
            }
            buffer.write(b);
            if (buffer.size() > 8192) {
                throw new IOException("HTTP line too long");
            }
        }

        if (b == -1 && buffer.size() == 0) {
            return null;
        }
        return buffer.toString(StandardCharsets.US_ASCII.name());
    }
}
