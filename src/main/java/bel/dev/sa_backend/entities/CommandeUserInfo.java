package bel.dev.sa_backend.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Entity;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "commande_user_info")
public class CommandeUserInfo {

    @Id @GeneratedValue 
    private UUID id; // identique à l'id de commande (1-1 partagé) ou @GeneratedValue + FK unique


    
    @OneToOne
    @MapsId
    @JoinColumn(name = "commande_id")
    private Commande commande;


    
    // Lien optionnel vers le compte
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = true)
    private Utilisateur utilisateur;


    //Info de user qui a passé la commande

    private String nom;
    private String prenom;
    private String email;
    private String telephone;



    //les champs adresse de shipping

    @Column(length = 128)
    private String numeroEtvoie; // numero + voie

    @Column(length = 128)
    private String complementAdresse; // complément (optionnel)

    @Column(length = 32)
    private String codePostal;

    @Column(length = 64)
    private String ville;

    @Column(length = 64)
    private String pays;




    // Helpers
    public boolean isGuest() { return utilisateur == null; }


    //info de facturation peut être différent de shipping
    //info de paiement peut être ajouté ici aussi
    //info de statut de commande
    // gérer le cas d'un cadeau.

}
