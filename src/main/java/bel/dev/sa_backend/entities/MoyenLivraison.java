package bel.dev.sa_backend.entities;


import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "moyen_livraison")
public class MoyenLivraison {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(nullable = false)
    private BigDecimal prix;

    @Column(nullable = false)
    private Integer delaiEnJours;  // ex : 2 = 2 jours

    private String description;

    public MoyenLivraison() {}

    public MoyenLivraison(String nom, BigDecimal prix, Integer delaiEnJours, String description) {
        this.nom = nom;
        this.prix = prix;
        this.delaiEnJours = delaiEnJours;
        this.description = description;
    }

    // Getters / Setters
}