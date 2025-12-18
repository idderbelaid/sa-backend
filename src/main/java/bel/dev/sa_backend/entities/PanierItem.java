package bel.dev.sa_backend.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    name = "PANIER_ITEM", 
    
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_panier_item_unique", columnNames = {"panier_id", "produit_id"})
    },
    indexes = {
        @Index(name = "idx_panier_item_panier_id", columnList = "panier_id"),
        @Index(name = "idx_panier_item_produit_id", columnList = "produit_id")
    }


)

public class PanierItem {
    
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "panier_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_panier_item_panier")
    )
    private Panier panier;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
        name = "produit_id",
        referencedColumnName = "id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_panier_item_produit")
    )
    private Produit produit;

    /** Quantité > 0 */
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    /** Prix unitaire saisi au moment de l’ajout (TTC si c’est ton modèle) */
    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    /** Prix unitaire effectif après promotions/remises */
    @Column(name = "effective_unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal effectiveUnitPrice;

    /** Total de la ligne = effective_unit_price * quantity */
    @Column(name = "line_total", precision = 12, scale = 2, nullable = false)
    private BigDecimal lineTotal;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();


    
    // ——— Lifecycle ———
    
    @PrePersist
    @PreUpdate
    public void prePersistOrUpdate() {
        if (quantity == null || quantity <= 0) {
            quantity = 1;
        }
        if (effectiveUnitPrice == null) {
            effectiveUnitPrice = (unitPrice != null) ? unitPrice : BigDecimal.ZERO;
        }
        this.lineTotal = effectiveUnitPrice.multiply(BigDecimal.valueOf(quantity));
        this.updatedAt = Instant.now();
    }


}
