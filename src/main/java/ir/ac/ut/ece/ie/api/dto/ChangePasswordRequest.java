package ir.ac.ut.ece.ie.api.dto;

public record ChangePasswordRequest(
        String oldPassword,
        String newPassword
) {
}
