package ir.ac.ut.ece.ie.api.dto;

public record LoginRequest(
        String username,
        String password
) {
}
