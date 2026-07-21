package ir.ac.ut.ece.ie.api.dto;

public record AuthResponse(
        String token,
        String username,
        String name,
        String email,
        String phone
) {
}
