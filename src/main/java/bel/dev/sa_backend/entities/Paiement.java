package bel.dev.sa_backend.entities;

import java.time.Instant;
import java.util.UUID;

import bel.dev.sa_backend.Enums.PaiementStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Id;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "ix_payment_order", columnList = "orderId")
})
public class Paiement {

    @Id @GeneratedValue 
    private UUID id;

   
 // Côté propriétaire : FK vers commande, unique = one-to-one
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Commande order;


    @Column(length = 32) 
    private String provider; // ex: Stripe

    @Column(length = 16) 
    private String method;   // ex: CARD

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private PaiementStatus status = PaiementStatus.PENDING;

    @Column(nullable = false, length = 3) private String currency = "EUR";
    @Column(nullable = false) private Long amount;

    private String providerIntentId;
    private String providerChargeId;

    private Instant createdAt;
    private Instant authorizedAt;
    private Instant capturedAt;
    private Instant updatedAt;

}
