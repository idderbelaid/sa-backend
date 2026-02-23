package bel.dev.sa_backend.dto;

import bel.dev.sa_backend.controller.requestDTO.InfoUserInviteDTO;
import bel.dev.sa_backend.controller.requestDTO.ItemCommandeDTO;
import bel.dev.sa_backend.controller.requestDTO.PaiementDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommandeRequestDTO {
    private InfoUserInviteDTO infoUserInvite;
    private ItemCommandeDTO[] itemsCommande;
    private AddressDTO AdresseShipping;
    private PaiementDTO paiement;
    private long deliveryPrice;
    private long sousTotalPrice;
    private long totalPrice;
}
