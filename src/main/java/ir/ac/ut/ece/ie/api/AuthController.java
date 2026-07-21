package ir.ac.ut.ece.ie.api;

import ir.ac.ut.ece.ie.api.dto.AuthResponse;
import ir.ac.ut.ece.ie.api.dto.LoginRequest;
import ir.ac.ut.ece.ie.api.dto.RegisterRequest;
import ir.ac.ut.ece.ie.api.security.JwtProvider;
import ir.ac.ut.ece.ie.api.util.ValidationUtils;
import ir.ac.ut.ece.ie.app.Author;
import ir.ac.ut.ece.ie.app.AuthorRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final AuthorRepository authorRepository;
    private final JwtProvider jwtProvider;

    public AuthController(AuthorRepository authorRepository, JwtProvider jwtProvider) {
        this.authorRepository = authorRepository;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be null.");
        }

        String username = ValidationUtils.normalize(request.username());
        String name = ValidationUtils.normalize(request.name());
        String password = request.password();
        String email = ValidationUtils.normalize(request.email());
        String phone = ValidationUtils.normalize(request.phone());

        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username is required.");
        }

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }

        if (!ValidationUtils.isStrongPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 6 characters long and contain at least one uppercase letter, one lowercase letter, and one digit.");
        }

        if (email == null && phone == null) {
            throw new IllegalArgumentException("At least one of email or phone number is required.");
        }

        if (email != null) {
            if (!ValidationUtils.isValidEmail(email)) {
                throw new IllegalArgumentException("Invalid email format.");
            }
            if (authorRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email is already registered.");
            }
        }

        if (phone != null) {
            if (!ValidationUtils.isValidIranianPhone(phone)) {
                throw new IllegalArgumentException("Invalid Iranian mobile phone number format.");
            }
            if (authorRepository.existsByPhone(phone)) {
                throw new IllegalArgumentException("Phone number is already registered.");
            }
        }

        if (authorRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        Author newAuthor = new Author(username, name, passwordHash, email, phone);
        Author saved = authorRepository.save(newAuthor);

        String token = jwtProvider.generateToken(saved);
        AuthResponse response = new AuthResponse(token, saved.getUsername(), saved.getName(), saved.getEmail(), saved.getPhone());

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be null.");
        }

        String username = ValidationUtils.normalize(request.username());
        String password = request.password();

        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password are required.");
        }

        Author author = authorRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        if (!BCrypt.checkpw(password, author.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        String token = jwtProvider.generateToken(author);
        AuthResponse response = new AuthResponse(token, author.getUsername(), author.getName(), author.getEmail(), author.getPhone());

        return ResponseEntity.ok(response);
    }
}
