package bel.dev.sa_backend.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "JWT")
public class Jwt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String valeur;
    private Boolean desactive;
    private Boolean expired;

    @OneToOne(cascade ={CascadeType.PERSIST, CascadeType.REMOVE} )
    private RefreshToken refreshToken;


    @ManyToOne(cascade ={CascadeType.DETACH, CascadeType.MERGE} )
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

}
