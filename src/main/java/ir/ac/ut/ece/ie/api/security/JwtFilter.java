package ir.ac.ut.ece.ie.api.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import ir.ac.ut.ece.ie.app.Author;
import ir.ac.ut.ece.ie.app.AuthorRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final AuthorRepository authorRepository;

    public JwtFilter(JwtProvider jwtProvider, AuthorRepository authorRepository) {
        this.jwtProvider = jwtProvider;
        this.authorRepository = authorRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        boolean isProtected = path.startsWith("/api/profile") ||
                (path.equals("/api/articles") && "POST".equalsIgnoreCase(method));

        if (!isProtected) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header.");
            return;
        }

        String token = authHeader.substring(7).trim();

        try {
            Long authorId = jwtProvider.getAuthorId(token);
            Optional<Author> authorOpt = authorRepository.findById(authorId);

            if (authorOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found.");
                return;
            }

            request.setAttribute("authUser", authorOpt.get());
            filterChain.doFilter(request, response);

        } catch (JWTVerificationException | NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token.");
        }
    }
}
