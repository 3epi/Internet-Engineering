package ir.ac.ut.ece.ie.api.dto;

public record RegisterRequest(
        String username,
        String name,
        String password,
        String email,
        String phone
) {
}
