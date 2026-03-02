package bel.dev.sa_backend.controller.requestDTO;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SentimentRequest {

    private String texte;

    private String produitId;

    private String utilisateurEmail;
}