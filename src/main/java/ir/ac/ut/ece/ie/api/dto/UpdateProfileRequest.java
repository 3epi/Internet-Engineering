package ir.ac.ut.ece.ie.api.dto;

public record UpdateProfileRequest(
        String email,
        String phone
) {
}
