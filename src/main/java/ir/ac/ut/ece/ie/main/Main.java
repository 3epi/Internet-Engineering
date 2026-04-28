package ir.ac.ut.ece.ie.main;

import ir.ac.ut.ece.ie.app.ArticleRepository;
import ir.ac.ut.ece.ie.app.ArticleService;
import ir.ac.ut.ece.ie.app.InMemoryArticleRepository;
import ir.ac.ut.ece.ie.http.HttpMethod;
import ir.ac.ut.ece.ie.http.HttpServer;
import ir.ac.ut.ece.ie.http.routing.Router;
import ir.ac.ut.ece.ie.web.AddArticleFormHandler;
import ir.ac.ut.ece.ie.web.AddArticleSubmitHandler;
import ir.ac.ut.ece.ie.web.ArticleHandler;
import ir.ac.ut.ece.ie.web.HomeHandler;

public final class Main {
    public static void main(String[] args) throws Exception {
        int port = 9090;

        ArticleRepository repository = new InMemoryArticleRepository();
        ArticleService service = new ArticleService(repository);

        Router router = new Router()
                .add(HttpMethod.GET, "/", new HomeHandler(service))
                .add(HttpMethod.GET, "/article", new ArticleHandler(service))
                .add(HttpMethod.GET, "/add", new AddArticleFormHandler())
                .add(HttpMethod.POST, "/add", new AddArticleSubmitHandler(service));

        System.out.println("Server running on http://localhost:" + port);
        new HttpServer(port, router).start();
    }
}
