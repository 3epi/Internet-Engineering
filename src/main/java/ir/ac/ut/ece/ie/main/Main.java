package ir.ac.ut.ece.ie.main;

import ir.ac.ut.ece.ie.ApiApplication;
import ir.ac.ut.ece.ie.app.ArticleRepository;
import ir.ac.ut.ece.ie.app.ArticleService;
import ir.ac.ut.ece.ie.http.HttpMethod;
import ir.ac.ut.ece.ie.http.HttpServer;
import ir.ac.ut.ece.ie.http.routing.Router;
import ir.ac.ut.ece.ie.web.AddArticleFormHandler;
import ir.ac.ut.ece.ie.web.AddArticleSubmitHandler;
import ir.ac.ut.ece.ie.web.ArticleHandler;
import ir.ac.ut.ece.ie.web.HomeHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

public final class Main {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = SpringApplication.run(ApiApplication.class, args);
        ArticleService service = context.getBean(ArticleService.class);

        int port = 9090;

        Router router = new Router()
                .add(HttpMethod.GET, "/", new HomeHandler(service))
                .add(HttpMethod.GET, "/article", new ArticleHandler(service))
                .add(HttpMethod.GET, "/add", new AddArticleFormHandler())
                .add(HttpMethod.POST, "/add", new AddArticleSubmitHandler(service));

        System.out.println("System Server running on http://localhost:" + port);
        new HttpServer(port, router).start();
    }
}
