package bel.dev.sa_backend.controller.requestDTO;



import bel.dev.sa_backend.dto.AddressDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommandeInviteRequest {
      
    private InfoUserInviteDTO infoUserInvite;
    private ItemCommandeDTO[] itemsCommande;
    private AddressDTO AdresseShipping;
    private PaiementDTO paiement;
}
