package bel.dev.sa_backend.entities;


import java.math.BigDecimal;
import java.time.Instant;

import java.util.UUID;

import jakarta.persistence.*;

import bel.dev.sa_backend.Enums.PanierStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    name = "PANIER",
    indexes = {
        @Index(name = "idx_carts_user_id", columnList = "user_id"),
        @Index(name = "idx_carts_status", columnList = "status"),
        @Index(name = "idx_carts_expires_at", columnList = "expires_at")
    }

)
public class Panier {
    
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    
    @Column(name = "user_id")
    private UUID userId; // NULL pour les invités

    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PanierStatus status = PanierStatus.ACTIVE;


    private BigDecimal prix_total;

    private Instant createdAt;
    private Instant expiresAt;
    private Instant updateAt;

    
 
   
}
