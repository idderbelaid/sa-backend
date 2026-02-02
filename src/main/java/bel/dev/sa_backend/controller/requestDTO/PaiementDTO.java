package bel.dev.sa_backend.controller.requestDTO;

public record PaiementDTO(
    String method,
    String status,
    String provider,
    String paymentToken
) {

}
