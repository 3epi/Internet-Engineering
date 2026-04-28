package ir.ac.ut.ece.ie.http;

import ir.ac.ut.ece.ie.http.routing.Router;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public final class HttpServer {
    private final int port;
    private final Router router;

    public HttpServer(int port, Router router) {
        this.port = port;
        this.router = router;
    }

    public void start() throws IOException {
        HttpRequestParser parser = new HttpRequestParser();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                handleOne(socket, parser);
            }
        }
    }

    private void handleOne(Socket socket, HttpRequestParser parser) {
        try (Socket s = socket) {
            HttpRequest request;
            try {
                request = parser.parse(s.getInputStream());
                if (request == null) {
                    return;
                }
            } catch (Exception ex) {
                writeResponse(s.getOutputStream(), HttpResponse.badRequestHtml(simpleHtml("Bad Request", "Malformed HTTP request.")));
                return;
            }

            if (request.getMethod() == null) {
                writeResponse(s.getOutputStream(), HttpResponse.methodNotAllowedHtml(simpleHtml("Method Not Allowed", "Only GET and POST are supported.")));
                return;
            }

            HttpResponse response;
            try {
                response = router.route(request);
            } catch (Exception ex) {
                response = HttpResponse.internalServerErrorHtml(simpleHtml("Internal Server Error", "An unexpected error happened."));
            }

            writeResponse(s.getOutputStream(), response);
        } catch (IOException ignore) {
            // Client disconnect or socket error.
        }
    }

    private void writeResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ").append(response.getStatusCode()).append(' ').append(response.getReasonPhrase()).append("\r\n");
        for (String name : response.getHeaders().keySet()) {
            sb.append(name).append(": ").append(response.getHeaders().get(name)).append("\r\n");
        }
        sb.append("\r\n");

        outputStream.write(sb.toString().getBytes(StandardCharsets.US_ASCII));
        outputStream.write(response.getBody());
        outputStream.flush();
    }

    private static String simpleHtml(String title, String message) {
        return "<html><head><meta charset=\"utf-8\"><title>" + title + "</title></head>" +
                "<body><h1>" + title + "</h1><p>" + message + "</p></body></html>";
    }
}
