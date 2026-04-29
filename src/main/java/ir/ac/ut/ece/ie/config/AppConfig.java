package ir.ac.ut.ece.ie.config;

import ir.ac.ut.ece.ie.app.ArticleRepository;
import ir.ac.ut.ece.ie.app.ArticleService;
import ir.ac.ut.ece.ie.app.InMemoryArticleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ArticleRepository articleRepository() {
        return new InMemoryArticleRepository();
    }

    @Bean
    public ArticleService articleService(ArticleRepository repository) {
        return new ArticleService(repository);
    }
}
