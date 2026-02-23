package bel.dev.sa_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponseDTO {
    private String numeroEtvoie;
    private String complementAddress;
    private String ville;
    private String codePostal;
    private String pays;
}
