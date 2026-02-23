package bel.dev.sa_backend.dto;

public record CommandeLivraisonResponseDTO(
    CommandeResponseDTO commande,
    AddressResponseDTO livraison
) {

}
