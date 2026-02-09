package bel.dev.sa_backend.controller.requestDTO;

public record ItemCommandeDTO(
    String productId,
    String nomProduit,
    int quantity,
    Long unitPrice
) {
}