package ir.ac.ut.ece.ie.api;

import ir.ac.ut.ece.ie.api.dto.ArticleDto;
import ir.ac.ut.ece.ie.api.dto.ChangePasswordRequest;
import ir.ac.ut.ece.ie.api.dto.ProfileResponse;
import ir.ac.ut.ece.ie.api.dto.UpdateProfileRequest;
import ir.ac.ut.ece.ie.api.util.ValidationUtils;
import ir.ac.ut.ece.ie.app.ArticleRepository;
import ir.ac.ut.ece.ie.app.Author;
import ir.ac.ut.ece.ie.app.AuthorRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/profile", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfileController {

    private final AuthorRepository authorRepository;
    private final ArticleRepository articleRepository;

    public ProfileController(AuthorRepository authorRepository, ArticleRepository articleRepository) {
        this.authorRepository = authorRepository;
        this.articleRepository = articleRepository;
    }

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(HttpServletRequest request) {
        Author author = getAuthenticatedAuthor(request);
        return ResponseEntity.ok(buildProfileResponse(author));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileResponse> updateProfile(HttpServletRequest request,
                                                         @RequestBody UpdateProfileRequest updateRequest) {
        Author currentAuthor = getAuthenticatedAuthor(request);

        if (updateRequest == null) {
            throw new IllegalArgumentException("Request body cannot be null.");
        }

        String email = ValidationUtils.normalize(updateRequest.email());
        String phone = ValidationUtils.normalize(updateRequest.phone());

        if (email == null && phone == null) {
            throw new IllegalArgumentException("At least one of email or phone number is required.");
        }

        if (email != null) {
            if (!ValidationUtils.isValidEmail(email)) {
                throw new IllegalArgumentException("Invalid email format.");
            }
            if (authorRepository.existsByEmailAndIdNot(email, currentAuthor.getId())) {
                throw new IllegalArgumentException("Email is already in use by another account.");
            }
        }

        if (phone != null) {
            if (!ValidationUtils.isValidIranianPhone(phone)) {
                throw new IllegalArgumentException("Invalid Iranian mobile phone number format.");
            }
            if (authorRepository.existsByPhoneAndIdNot(phone, currentAuthor.getId())) {
                throw new IllegalArgumentException("Phone number is already in use by another account.");
            }
        }

        currentAuthor.setEmail(email);
        currentAuthor.setPhone(phone);
        Author saved = authorRepository.save(currentAuthor);

        return ResponseEntity.ok(buildProfileResponse(saved));
    }

    @PutMapping(path = "/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changePassword(HttpServletRequest request,
                                               @RequestBody ChangePasswordRequest passwordRequest) {
        Author currentAuthor = getAuthenticatedAuthor(request);

        if (passwordRequest == null) {
            throw new IllegalArgumentException("Request body cannot be null.");
        }

        String oldPassword = passwordRequest.oldPassword();
        String newPassword = passwordRequest.newPassword();

        if (oldPassword == null || newPassword == null) {
            throw new IllegalArgumentException("Old and new passwords are required.");
        }

        if (!BCrypt.checkpw(oldPassword, currentAuthor.getPasswordHash())) {
            throw new IllegalArgumentException("Incorrect old password.");
        }

        if (!ValidationUtils.isStrongPassword(newPassword)) {
            throw new IllegalArgumentException("New password must be at least 6 characters long and contain at least one uppercase letter, one lowercase letter, and one digit.");
        }

        currentAuthor.setPasswordHash(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        authorRepository.save(currentAuthor);

        return ResponseEntity.ok().build();
    }

    private Author getAuthenticatedAuthor(HttpServletRequest request) {
        Author author = (Author) request.getAttribute("authUser");
        if (author == null) {
            throw new IllegalStateException("User is not authenticated.");
        }
        // Re-fetch from DB to get the latest articles and attributes
        return authorRepository.findById(author.getId())
                .orElseThrow(() -> new IllegalStateException("Author not found."));
    }

    private ProfileResponse buildProfileResponse(Author author) {
        List<ArticleDto> articleDtos = articleRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(article -> article.getAuthor() != null && article.getAuthor().getId().equals(author.getId()))
                .map(ArticleDto::from)
                .toList();

        return new ProfileResponse(
                author.getUsername(),
                author.getName(),
                author.getEmail(),
                author.getPhone(),
                articleDtos
        );
    }
}
