package bel.dev.sa_backend.dto;

import bel.dev.sa_backend.controller.requestDTO.InfoUserInviteDTO;
import bel.dev.sa_backend.controller.requestDTO.ItemCommandeDTO;
import bel.dev.sa_backend.controller.requestDTO.PaiementDTO;

public class CommandeRequestDTO {
    private InfoUserInviteDTO infoUserInvite;
    private ItemCommandeDTO[] itemsCommande;
    private AddressDTO AdresseShipping;
    private PaiementDTO paiement;
}
