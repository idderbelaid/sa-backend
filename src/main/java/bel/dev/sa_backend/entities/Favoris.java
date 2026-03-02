package bel.dev.sa_backend.entities;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "FAVORIS")
public class Favoris {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String favoris_id;

    // Un utilisateur possède UNE liste de favoris
    @OneToOne
    @JoinColumn(name = "utilisateur_id", nullable = false, unique = true)
    private Utilisateur utilisateur;

    // La liste des produits favoris
    @ManyToMany
    @JoinTable(
        name = "favoris_produits",
        joinColumns = @JoinColumn(name = "favoris_id"),
        inverseJoinColumns = @JoinColumn(name = "produit_id")
    )
    private Set<Produit> produits = new HashSet<>();
}