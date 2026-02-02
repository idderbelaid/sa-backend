package bel.dev.sa_backend.mapper;

import bel.dev.sa_backend.dto.CommandeResponseDTO;
import bel.dev.sa_backend.entities.Commande;

public class CommandeMapper {


    public static CommandeResponseDTO toCommandeResponseDTO(Commande commande) {
        return new CommandeResponseDTO(
            commande.getCommande_id().toString(),
            commande.getNumeroCommande(),
            commande.getPaiement().getAmount(),
            commande.getStatus(),
            commande.getItems().stream()
                .map(item -> new bel.dev.sa_backend.controller.requestDTO.ItemCommandeDTO(
                    item.getId().toString(),
                    item.getProductName(),
                    item.getQuantity(),
                    item.getUnitPriceExclTax()
                ))
                .toArray(bel.dev.sa_backend.controller.requestDTO.ItemCommandeDTO[]::new),
            commande.getCreatedAt()
        );
    }
}
