package bel.dev.sa_backend.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavorisDTO {
    private String favoris_id;
    private String username;
    private Set<ProduitDTO> produits;

}
