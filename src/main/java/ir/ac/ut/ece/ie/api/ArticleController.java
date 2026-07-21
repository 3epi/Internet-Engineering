package ir.ac.ut.ece.ie.api;

import ir.ac.ut.ece.ie.api.dto.ArticleDto;
import ir.ac.ut.ece.ie.api.dto.CreateArticleRequest;
import ir.ac.ut.ece.ie.api.dto.SearchResponseDto;
import ir.ac.ut.ece.ie.api.exception.NotFoundException;
import ir.ac.ut.ece.ie.app.Article;
import ir.ac.ut.ece.ie.app.ArticleService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping(path = "/api/articles", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArticleController {
    private final ArticleService service;

    public ArticleController(ArticleService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArticleDto> create(@RequestBody CreateArticleRequest request,
                                             jakarta.servlet.http.HttpServletRequest servletRequest) {
        String title = request == null ? null : request.title();
        String abstractText = request == null ? null : request.abstractText();
        String body = request == null ? null : request.body();
        String categoryName = request == null ? null : request.categoryName();

        ir.ac.ut.ece.ie.app.Author authUser = (ir.ac.ut.ece.ie.app.Author) servletRequest.getAttribute("authUser");
        if (authUser != null) {
            service.addArticleForAuthor(title, abstractText, body, authUser, categoryName);
        } else {
            String authorName = request == null ? null : request.authorName();
            service.addArticle(title, abstractText, body, authorName, categoryName);
        }

        String normalizedTitle = title == null ? null : title.trim();
        Article created = service.getByTitle(normalizedTitle);
        if (created == null) {
            throw new IllegalStateException("Article was created but could not be retrieved.");
        }
        ArticleDto response = ArticleDto.from(created);

        URI location = URI.create("/api/articles?title=" + urlEncode(created.getTitle()));
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public List<ArticleDto> list() {
        return service.listNewestFirst().stream().map(ArticleDto::from).toList();
    }

    @GetMapping(params = "title")
    public ArticleDto getByTitle(@RequestParam(name = "title") String title) {
        String normalized = title == null ? "" : title.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Query parameter 'title' must not be empty.");
        }

        Article article = service.getByTitle(normalized);
        if (article == null) {
            throw new NotFoundException("Article not found.");
        }

        return ArticleDto.from(article);
    }

    @GetMapping(path = "/search")
    public SearchResponseDto search(@RequestParam(name = "q", required = false) String q) {
        return SearchResponseDto.from(service.search(q));
    }

    private static String urlEncode(String value) {
        if (value == null) return "";
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
