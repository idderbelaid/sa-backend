package bel.dev.sa_backend.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SentimentDTO {

    private int id;
    private String texte;
    private String utilisateurEmail;
    private Date creation;

   
}
